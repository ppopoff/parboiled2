/*
 * Copyright (C) 2009-2013 Mathias Doenitz, Alexander Myltsev
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

package org.parboiled2

class CombinatorModifierSpec extends TestParserSpec {

  "The Parser should correctly recognize/reject input for the" >> {

    "`~` combinator" in new TestParser0 {
      def targetRule = rule { 'a' ~ 'b' }
      "" must beMismatched
      "ab" must beMatched
      "ac" must beMismatched
      "a" must beMismatched
      "b" must beMismatched
    }

    "`|` combinator" in new TestParser0 {
      def targetRule = rule { ch('a') | 'b' }
      "" must beMismatched
      "a" must beMatched
      "b" must beMatched
      "c" must beMismatched
    }

    "`zeroOrMore(Rule0)` modifier" in new TestParser0 {
      def targetRule = rule { zeroOrMore("a") ~ EOI }
      "" must beMatched
      "a" must beMatched
      "aa" must beMatched
      "b" must beMismatched
    }

    "`zeroOrMore(Rule0).separatedBy('|')` modifier" in new TestParser0 {
      def targetRule = rule { zeroOrMore("a").separatedBy('|') ~ EOI }
      "" must beMatched
      "a" must beMatched
      "a|a" must beMatched
      "a|a|" must beMismatched
      "aa" must beMismatched
      "b" must beMismatched
    }

    "`zeroOrMore(Rule1[T])` modifier" in new TestParser1[Seq[String]] {
      def targetRule = rule { zeroOrMore(capture("a")) ~ EOI }
      "a" must beMatchedBy(Seq("a"))
      "aa" must beMatchedBy(Seq("a", "a"))
      "b" must beMismatched
      "" must beMatchedBy(Seq.empty)
    }

    "`zeroOrMore(Rule[I, O <: I])` modifier" in new TestParser1[String] {
      def targetRule = rule { capture("a") ~ zeroOrMore(ch('x') ~> ((_: String) + 'x')) ~ EOI }
      "a" must beMatchedBy("a")
      "ax" must beMatchedBy("ax")
      "axx" must beMatchedBy("axx")
    }

    "`oneOrMore(Rule0)` modifier" in new TestParser0 {
      def targetRule = rule { oneOrMore("a") ~ EOI }
      "a" must beMatched
      "aa" must beMatched
      "b" must beMismatched
      "" must beMismatched
    }

    "`oneOrMore(Rule0).separatedBy('|')` modifier" in new TestParser0 {
      def targetRule = rule { oneOrMore("a").separatedBy('|') ~ EOI }
      "" must beMismatched
      "a" must beMatched
      "a|a" must beMatched
      "a|a|" must beMismatched
      "aa" must beMismatched
      "b" must beMismatched
    }

    "`oneOrMore(Rule1[T])` modifier" in new TestParser1[Seq[String]] {
      def targetRule = rule { oneOrMore(capture("a")) ~ EOI }
      "a" must beMatchedBy(Seq("a"))
      "aa" must beMatchedBy(Seq("a", "a"))
      "b" must beMismatched
      "" must beMismatched
    }

    "`oneOrMore(Rule[I, O <: I])` modifier" in new TestParser1[String] {
      def targetRule = rule { capture("a") ~ oneOrMore(ch('x') ~> ((_: String) + 'x')) ~ EOI }
      "a" must beMismatched
      "ax" must beMatchedBy("ax")
      "axx" must beMatchedBy("axx")
    }

    "`optional(Rule0)` modifier" in new TestParser0 {
      def targetRule = rule { optional("a") ~ EOI }
      "a" must beMatched
      "b" must beMismatched
      "" must beMatched
    }

    "`optional(Rule1[T])` modifier" in new TestParser1[Option[String]] {
      def targetRule = rule { optional(capture("a")) ~ EOI }
      "a" must beMatchedBy(Some("a"))
      "" must beMatchedBy(None)
      "b" must beMismatched
      "ab" must beMismatched
    }

    "`optional(Rule[I, O <: I])` modifier" in new TestParser1[String] {
      def targetRule = rule { capture("a") ~ optional(ch('x') ~> ((_: String) + 'x')) ~ EOI }
      "a" must beMatchedBy("a")
      "ax" must beMatchedBy("ax")
      "axx" must beMismatched
    }

    "`!(Rule0)` modifier" in new TestParser0 {
      def targetRule = rule { !"a" }
      "a" must beMismatched
      "b" must beMatched
      "" must beMatched
    }

    "`&` modifier" in new TestParser0 {
      def targetRule = rule { &("a") }
      "a" must beMatched
      "b" must beMismatched
      "" must beMismatched
    }

    "`0.times(Rule0)` modifier" in new TestParser0 {
      def targetRule = rule { 0.times("a") }
      "" must beMatched
      "x" must beMatched
    }

    "`2.times(Rule0)` modifier (example 1)" in new TestParser0 {
      def targetRule = rule { 2.times("x") }
      "" must beMismatched
      "x" must beMismatched
      "xx" must beMatched
      "xxx" must beMatched
    }

    "`2.times(Rule0)` modifier (example 2)" in new TestParser0 {
      def targetRule = rule { 2.times("x") ~ EOI }
      "x" must beMismatched
      "xx" must beMatched
      "xxx" must beMismatched
    }

    "`2.times(Rule0).separatedBy('|')` modifier" in new TestParser0 {
      def targetRule = rule { 2.times("x").separatedBy('|') ~ EOI }
      "xx" must beMismatched
      "x|x" must beMatched
      "x|x|" must beMismatched
    }

    "`(2 to 4).times(Rule0)` modifier (example 1)" in new TestParser0 {
      def targetRule = rule { (2 to 4).times("x") }
      "" must beMismatched
      "x" must beMismatched
      "xx" must beMatched
      "xxx" must beMatched
      "xxxx" must beMatched
      "xxxxx" must beMatched
    }

    "`(2 to 4).times(Rule0)` modifier (example 2)" in new TestParser0 {
      def targetRule = rule { (2 to 4).times("x") ~ EOI }
      "xx" must beMatched
      "xxx" must beMatched
      "xxxx" must beMatched
      "xxxxx" must beMismatched
    }

    "`(2 to 4).times(Rule0).separatedBy('|')` modifier" in new TestParser0 {
      def targetRule = rule { (2 to 4).times("x").separatedBy('|') ~ EOI }
      "xx" must beMismatched
      "x|" must beMismatched
      "x|x" must beMatched
      "x|x|" must beMismatched
      "x|x|x" must beMatched
      "x|x|x|" must beMismatched
      "x|x|x|x" must beMatched
      "x|x|x|x|" must beMismatched
      "x|x|x|x|x" must beMismatched
    }

    "`(1 to 3).times(Rule1[T])` modifier" in new TestParser1[Seq[String]] {
      def targetRule = rule { (1 to 3).times(capture("x")) ~ EOI }
      "" must beMismatched
      "x" must beMatchedBy(Seq("x"))
      "xx" must beMatchedBy(Seq("x", "x"))
      "xxx" must beMatchedBy(Seq("x", "x", "x"))
      "xxxx" must beMismatched
    }

    "`2.times(Rule[I, O <: I])` modifier" in new TestParser1[String] {
      def targetRule = rule { capture("a") ~ 2.times(ch('x') ~> ((_: String) + 'x')) ~ EOI }
      "a" must beMismatched
      "ax" must beMismatched
      "axx" must beMatchedBy("axx")
      "axxx" must beMismatched
    }
  }
}