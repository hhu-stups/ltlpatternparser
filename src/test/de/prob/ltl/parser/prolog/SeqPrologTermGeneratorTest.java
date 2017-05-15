package de.prob.ltl.parser.prolog;

import org.junit.Assert;
import org.junit.Test;

import de.prob.ltl.parser.AbstractParserTest;

public class SeqPrologTermGeneratorTest extends AbstractParserTest {

	// Helper
	public void assertEquals(String expected, String actual) {
		//System.out.println(expected);
		String a = parseToString(expected);
		String b = parseToString(actual);
		Assert.assertEquals(a, b);
	}

	// Tests
	@Test
	public void testSeqCall() {
		// Simple
		String expected = "true & XF(false)";
		assertEquals(expected, "seq s: (true, false) seq(s)");
		assertEquals(expected, "seq s: (true, false) seq t: s seq(t)");
		assertEquals(expected, "seq s: (true, false) seq t: s s: (sink, deadlock) seq(t)");
		assertEquals(expected, "var a: true var b: false seq s: (a, b) seq(s)");
		assertEquals(expected, "var a: true var b: false seq s: (a, b) a: sink seq(s)");
		assertEquals(expected, "var a: sink var b: false a: true seq s: (a, b) seq(s)");

		assertEquals(expected, "seq((true, false))");
		assertEquals(expected, "var a: true var b: false seq((a, b))");

		// Simple without
		expected = "true & !sink & X(!sink U false)";
		assertEquals(expected, "seq s: (true, false without sink) seq(s)");
		assertEquals(expected, "seq s: (true, false without sink) seq t: s seq(t)");
		assertEquals(expected, "seq s: (true, false without sink) seq t: s s: (sink, deadlock) seq(t)");
		assertEquals(expected, "var a: true var b: false seq s: (a, b without sink) seq(s)");
		assertEquals(expected, "var a: true var b: false seq s: (a, b without sink) a: sink seq(s)");
		assertEquals(expected, "var a: sink var b: false a: true seq s: (a, b without sink) seq(s)");

		assertEquals(expected, "seq((true, false without sink))");
		assertEquals(expected, "var a: true var b: false seq((a, b without sink))");

		assertEquals(expected, "seq s: (true, false) seq t: s without sink seq(t)");
		assertEquals(expected, "seq s: (true, false) seq t: s without sink s: (sink, deadlock) seq(t)");
		assertEquals(expected, "seq s: (true, false) seq(s without sink)");
		assertEquals(expected, "seq s: (true, false) seq t: s seq(t without sink)");
		assertEquals(expected, "seq s: (true, false) seq t: s s: (sink, deadlock) seq(t without sink)");
		assertEquals(expected, "var a: true var b: false seq s: (a, b) seq(s without sink)");
		assertEquals(expected, "var a: true var b: false seq s: (a, b) a: sink seq(s without sink)");
		assertEquals(expected, "var a: sink var b: false a: true seq s: (a, b) seq(s without sink)");

		// var w: sink
		assertEquals(expected, "var w: sink seq s: (true, false without w) seq(s)");
		assertEquals(expected, "var w: sink seq s: (true, false without w) w:deadlock seq(s)");
		assertEquals(expected, "var w: sink seq s: (true, false without w) seq t: s seq(t)");
		assertEquals(expected, "var w: sink seq s: (true, false without w) seq t: s s: (sink, deadlock) seq(t)");
		assertEquals(expected, "var w: sink var a: true var b: false seq s: (a, b without w) seq(s)");
		assertEquals(expected, "var w: sink var a: true var b: false seq s: (a, b without w) a: sink seq(s)");
		assertEquals(expected, "var w: sink var a: sink var b: false a: true seq s: (a, b without w) seq(s)");

		assertEquals(expected, "var w: sink seq((true, false without w))");
		assertEquals(expected, "var w: sink var a: true var b: false seq((a, b without w))");

		assertEquals(expected, "var w: sink seq s: (true, false) seq t: s without w seq(t)");
		assertEquals(expected, "var w: sink seq s: (true, false) seq t: s without w s: (sink, deadlock) seq(t)");

		assertEquals(expected, "var w: sink seq s: (true, false) seq(s without w)");
		assertEquals(expected, "var w: sink seq s: (true, false) seq t: s seq(t without w)");
		assertEquals(expected, "var w: sink seq s: (true, false) seq t: s s: (sink, deadlock) seq(t without w)");
		assertEquals(expected, "var w: sink var a: true var b: false seq s: (a, b) seq(s without w)");
		assertEquals(expected, "var w: sink var a: true var b: false seq s: (a, b) a: sink seq(s without w)");
		assertEquals(expected, "var w: sink var a: sink var b: false a: true seq s: (a, b) seq(s without w)");

		// seq w: (sink, deadlock)
		expected = "true & !(sink & XF(deadlock)) & X(!(sink & XF(deadlock)) U false)";
		assertEquals(expected, "seq w: (sink, deadlock) seq s: (true, false without w) seq(s)");
		assertEquals(expected, "seq w: (sink, deadlock) seq s: (true, false without w) seq t: s seq(t)");
		assertEquals(expected, "seq w: (sink, deadlock) seq s: (true, false without w) seq t: s s: (sink, deadlock) seq(t)");
		assertEquals(expected, "seq w: (sink, deadlock) var a: true var b: false seq s: (a, b without w) seq(s)");
		assertEquals(expected, "seq w: (sink, deadlock) var a: true var b: false seq s: (a, b without w) a: sink seq(s)");
		assertEquals(expected, "seq w: (sink, deadlock) var a: sink var b: false a: true seq s: (a, b without w) seq(s)");

		assertEquals(expected, "seq w: (sink, deadlock) seq((true, false without w))");
		assertEquals(expected, "seq w: (sink, deadlock) var a: true var b: false seq((a, b without w))");

		assertEquals(expected, "seq w: (sink, deadlock) seq s: (true, false) seq t: s without w seq(t)");
		assertEquals(expected, "seq w: (sink, deadlock) seq s: (true, false) seq t: s without w s: (sink, deadlock) seq(t)");

		assertEquals(expected, "seq w: (sink, deadlock) seq s: (true, false) seq(s without w)");
		assertEquals(expected, "seq w: (sink, deadlock) seq s: (true, false) seq t: s seq(t without w)");
		assertEquals(expected, "seq w: (sink, deadlock) seq s: (true, false) seq t: s s: (sink, deadlock) seq(t without w)");
		assertEquals(expected, "seq w: (sink, deadlock) var a: true var b: false seq s: (a, b) seq(s without w)");
		assertEquals(expected, "seq w: (sink, deadlock) var a: true var b: false seq s: (a, b) a: sink seq(s without w)");
		assertEquals(expected, "seq w: (sink, deadlock) var a: sink var b: false a: true seq s: (a, b) seq(s without w)");

		// p(s) p(s without w)
		expected = "true & XF(false)";
		assertEquals(expected, "def p(s:seq): seq(s) seq s: (true, false) p(s)");
		assertEquals(expected, "def p(s:seq): seq(s) p((true, false))");

		expected = "true & !sink & X(!sink U false)";
		assertEquals(expected, "def p(s:seq): seq(s) seq s: (true, false) p(s without sink)");
		assertEquals(expected, "def p(s:seq): seq(s) p((true, false without sink))");
		assertEquals(expected, "def p(s:seq): seq(s without sink) seq s: (true, false) p(s)");
		assertEquals(expected, "def p(s:seq): seq(s without sink) p((true, false))");

		// arg count > 2
		assertEquals("true & XF(false & XF(deadlock))", "seq s: (true, false, deadlock) seq(s)");
		assertEquals("true & !sink & X(!sink U (false & !sink & X(!sink U deadlock)))", "seq s: (true, false, deadlock without sink) seq(s)");

		// 2 withouts
		assertEquals("true & (!sink & !deadlock) & X((!sink & !deadlock) U false)", "seq s: (true, false without sink) seq(s without deadlock)");
	}

}
