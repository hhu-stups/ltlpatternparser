package de.prob.ltl.parser.semantic;

import junit.framework.Assert;

import org.junit.Test;

import de.prob.ltl.parser.AbstractParserTest;

public class ExtendedSemanticTest extends AbstractParserTest {

	@Test
	public void testDefineAndCallVariable() throws Exception {
		parse("var x: true x");
		throwsException("var x: (1) x");
		parse("var s: true var x: s x");
		throwsException("var x: true s");
		throwsException("var x: x x");
		throwsException("var x: s var s: true x");

		parse("num x: 1 true");
		parse("num x: (1) true");
		parse("num s: 1 num x: s true");
		parse("num s: 1 num x: (s) true");
		throwsException("var s: true num x: (s) true");
		throwsException("num x: 1 s");
		throwsException("num x: 1 x");
		throwsException("num x: x true");
		throwsException("num x: s num s: 1 true");

		throwsException("num x: 1 var s: x s");
		throwsException("var s: true num x: s s");
		throwsException("var s: true num x: s x");

		throwsException("var s: 1 s");
		throwsException("num s: true true");

		throwsException("var x: true var x: false x");
		throwsException("num x: 1 num x: 2 true");
		throwsException("var x: true num x: 1 x");
		throwsException("num x: 1 var x: true x");
	}

	@Test
	public void testAssignVariable() throws Exception {
		parse("var x: true x: false sink");
		throwsException("x: true sink");
		throwsException("var x: true s: false sink");

		parse("var x: true x: x sink");
		parse("var x: true x: x or false sink");

		parse("var x: true var y: false x: y sink");
		parse("var x: true var y: false x: x or y sink");
		throwsException("var x: true x: y var y: false sink");

		parse("num x: 1 x: 2 sink");
		throwsException("x: 1 sink");
		throwsException("num x: 1 s: 2 sink");

		parse("num x: 1 x: x sink");

		parse("num x: 1 num y: 2 x: y sink");
		throwsException("num x: 1 x: y num y: 2 sink");

		throwsException("var x: true x: 1 sink");
		throwsException("num x: 1 x: true sink");
		throwsException("var x: true num n: 1 x: n sink");
		throwsException("var x: true num n: 1 n: x sink");

		parse("var x: true var y: false x: y sink");
		parse("var x: true var y: false x: (y) sink");
		parse("num x: 1 num y: 2 x: y sink");
		parse("num x: 1 num y: 2 x: (y) sink");

		throwsException("var x: true num y: 2 x: y sink");
		throwsException("var x: true num y: 2 x: (y) sink");
		throwsException("num x: 1 var y: true x: y sink");
		throwsException("num x: 1 var y: true x: (y) sink");
	}

	@Test
	public void testDefineAndCallPattern() throws Exception {
		parse("def pattern(): true false");
		parse("def pattern(): true pattern()");

		throwsException("def pattern(): true a()");

		parse("def a(): true def b(): true true");
		parse("def a(): true def b(): true a() or b()");
		parse("def a(): true def b(): true b() or a()");
		throwsException("def a(): true def a(): true true");
	}

	@Test
	public void testLoops() throws Exception {
		parse("count 1 up to 2: var x: true x: false end false");
		parse("count 2 down to 1: var x: true x: false end false");
		parse("count (1) up to (2): var x: true x: false end false");
		parse("count (2) down to (1): var x: true x: false end false");

		throwsException("count x up to 2: num x: 1 end false");
		throwsException("count 1 up to x: num x: 1 end false");
		throwsException("count i: 1 up to i: num x: 1 end false");
		throwsException("count i: i up to 2: num x: 1 end false");

		throwsException("count 1 up to 2: x: false end false");
		throwsException("count x up to 2: var x: false end false");
		throwsException("count 1 up to y: var x: false end false");

		parse("num x: 1 num y: 2 count x up to y: var s: false end false");
		parse("num x: 1 num y: 2 count (x) up to (y): var s: false end false");
		parse("num x: 1 num y: 2 count (x) up to (y): x: 3 end false");
		throwsException("num x: 1 num y: 2 count x up to y: var x: false end false");
		throwsException("num x: 1 var y: 2 count x up to y: var s: false end false");

		parse("count i: 1 up to 2: var x: true x: false end false");
		parse("count i: 2 down to 1: var x: true x: false end false");
		parse("count i: 1 up to 2: i: 0 end false");
		parse("count i: 2 down to 1: i: 0 end false");
		throwsException("count i: 1 up to 2: i: true end false");
		throwsException("count i: 1 up to 2: num i: 0 end false");
		throwsException("count i: 2 down to 1: var i: true end false");
		throwsException("var i: true count i: 2 down to 1: i: 0 end false");
		throwsException("num i: 1 count i: 2 down to 1: i: 0 end false");
		throwsException("count i: 1 up to 2: var x: true end i: 0 false");
	}

	@Test
	public void testScopes() throws Exception {
		parse("def a(): true def b(x): x before(a(), b(true))");
		parse("var x: true before(x, x)");

		throwsException("num x: 1 before(x, true)");
		throwsException("num x: 1 before(true, x)");
		throwsException("before(1, true)");
	}

	@Test
	public void testPreventDoubleCheckOfExpr() throws Exception {
		Assert.assertEquals(1, parseAndGetErrors("var a: true before({r}, x S a)").size());
		Assert.assertEquals(1, parseAndGetErrors("def pattern(x): x pattern(a or true)").size());
		Assert.assertEquals(1, parseAndGetErrors("var a: true seq((a, a or b))").size());
	}

}
