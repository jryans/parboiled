package org.parboiled.scala

import org.parboiled.support.ParsingResult

/**
 * <p>A simple wrapper around the {@link org.parboiled.RecoveringParseRunner} which returns a properly parameterized
 * {@link ParsingResult} for the given rule.</p>
 * <p>Note that the ParseRunner only accepts rules with zero or one value type parameter, as parsers leaving more
 * than one value on the value stack are considered to be somewhat bad style.</p>
 */
object RecoveringParseRunner {

  def run(rule: Rule0, input: String) =
    org.parboiled.RecoveringParseRunner.run(rule, input)

  def run[V](rule: Rule1[V], input: String) =
    org.parboiled.RecoveringParseRunner.run(rule, input).asInstanceOf[ParsingResult[V]]

}