/*
 * Copyright (C) 2009-2010 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled.errors;

import org.parboiled.common.Formatter;
import org.parboiled.common.StringUtils;
import org.parboiled.matchers.CharSetMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.MatcherPath;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Formatter} for {@link InvalidInputError}s that automatically creates the correct "expected" text
 * for the error.
 */
public class DefaultInvalidInputErrorFormatter implements Formatter<InvalidInputError> {

    public String format(InvalidInputError error) {
        if (error == null) return "";

        String errorMessage = String.format("Invalid input '%s%s'",
                StringUtils.escape(String.valueOf(error.getInputBuffer().charAt(error.getStartIndex()))),
                error.getEndIndex() - error.getStartIndex() > 1 ? "..." : ""
        );
        String expectedString = getExpectedString(error);
        return StringUtils.isEmpty(expectedString) ? errorMessage : errorMessage + ", expected " + expectedString;
    }

    public String getExpectedString(InvalidInputError error) {
        List<String> labelList = new ArrayList<String>();
        for (MatcherPath path : error.getFailedMatchers()) {
            Matcher labelMatcher = ErrorUtils.findProperLabelMatcher(path, error.getLastMatch());
            if (labelMatcher == null) continue;
            String[] labels = getLabels(labelMatcher);
            for (String label : labels) {
                if (label != null && !labelList.contains(label)) {
                    labelList.add(label);
                }
            }
        }
        return join(labelList);
    }

    /**
     * Gets the labels corresponding to the given matcher, CharSetMatchers are treated specially in that their
     * label is constructed as a list of their contents
     *
     * @param matcher the matcher
     * @return the labels
     */
    public String[] getLabels(Matcher matcher) {
        if (matcher instanceof CharSetMatcher) {
            CharSetMatcher cMatcher = (CharSetMatcher) matcher;
            if (!cMatcher.characters.isSubtractive()) {
                String[] labels = new String[cMatcher.characters.getChars().length];
                for (int i = 0; i < labels.length; i++) {
                    labels[i] = '\'' + String.valueOf(cMatcher.characters.getChars()[i]) + '\'';
                }
                return labels;
            }
        }
        return new String[] {matcher.getLabel()};
    }

    public String join(List<String> labelList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < labelList.size(); i++) {
            if (i > 0) sb.append(i < labelList.size() - 1 ? ", " : " or ");
            sb.append(labelList.get(i));
        }
        return StringUtils.escape(sb.toString());
    }

}
