package de.prob.ltl.parser.prolog;

import org.junit.Assert;
import org.junit.Test;

import de.prob.ltl.parser.AbstractParserTest;

public class PrologTermGeneratorTest extends AbstractParserTest {

	@Test
	public void testPatternParameterAndArguments() {
		Assert.assertEquals("true", parseToString("def pattern(): true pattern()"));
		Assert.assertEquals("or(false,true)", parseToString("def pattern(): true false or pattern()"));

		Assert.assertEquals("true", parseToString("def pattern(x): x pattern(true)"));
		Assert.assertEquals("or(false,true)", parseToString("def pattern(x): x false or pattern(true)"));

		Assert.assertEquals("true", parseToString("def pattern(x): x pattern(pattern(true))"));
		Assert.assertEquals("or(false,true)", parseToString("def pattern(x): x pattern(false or pattern(true))"));

		Assert.assertEquals("or(false,true)", parseToString("def pattern(x, y): true false or pattern(true,false)"));
		Assert.assertEquals("or(false,or(true,false))", parseToString("def pattern(x, y): x or y false or pattern(true,false)"));
		Assert.assertEquals("or(or(true,false),false)", parseToString("def pattern(x, y): x or y pattern(true,false) or false"));
		Assert.assertEquals("or(or(ap(sink),or(true,false)),false)", parseToString("def pattern(x, y): x or y sink or pattern(true,false) or false"));

		Assert.assertEquals("or(false,true)", parseToString("def pattern(x): x var y: true false or pattern(y)"));

		Assert.assertEquals("false", parseToString("def pattern(): true false"));
	}

	@Test
	public void testPatternBody() {
		Assert.assertEquals("or(false,true)", parseToString("def a(): b() def b(): true false or a()"));
		Assert.assertEquals("or(false,true)", parseToString("def b(): true def a(): b() false or a()"));

		Assert.assertEquals("or(false,true)", parseToString("def a(x): x def b(x): a(x) false or b(true)"));
		Assert.assertEquals("or(false,true)", parseToString("def a(x): x def b(x): a(x) false or b(b(true))"));
		Assert.assertEquals("or(false,true)", parseToString("def a(x): x def b(x): a(x) false or b(a(true))"));
		Assert.assertEquals("or(false,true)", parseToString("def a(x): x def b(x): a(x) false or a(b(true))"));

		Assert.assertEquals("or(false,true)", parseToString("def a(): var x: true false or x a()"));
		Assert.assertEquals("or(true,false)", parseToString("def a(): var x: true x: false true or x a()"));
		Assert.assertEquals("or(true,false)", parseToString("def a(x): x: false true or x a(true)"));
	}

	@Test
	public void testVariableGlobal() {
		Assert.assertEquals("or(true,false)", parseToString("var x: true x or false"));
		Assert.assertEquals("or(false,true)", parseToString("var x: true false or x"));

		Assert.assertEquals("or(false,true)", parseToString("var x: true x: false x or true"));
		Assert.assertEquals("or(true,false)", parseToString("var x: true x: false true or x"));

		Assert.assertEquals("and(true,false)", parseToString("var x: true x: x and false x"));
		Assert.assertEquals("true", parseToString("var x: true var y: x y"));
		Assert.assertEquals("or(true,not(true))", parseToString("var x: true var y: x y: y or !x y"));
	}

	@Test
	public void testGlobalVarsAndPatterns() {
		Assert.assertEquals("true", parseToString("var x: true def pattern(y): y pattern(x)"));
		Assert.assertEquals("true", parseToString("def pattern(y): y var x: true pattern(x)"));
	}

	@Test
	public void testLoop() {
		Assert.assertEquals("false", parseToString("def pattern(): var x: false count 1 up to 1: x: x or true end x pattern()"));
		Assert.assertEquals("false", parseToString("def pattern(): var x: false count 1 down to 1: x: x or true end x pattern()"));

		Assert.assertEquals("or(false,true)", parseToString("def pattern(): var x: false count 1 up to 2: x: x or true end x pattern()"));
		Assert.assertEquals("or(false,true)", parseToString("def pattern(): var x: false count 2 down to 1: x: x or true end x pattern()"));

		Assert.assertEquals("or(or(false,true),true)", parseToString("def pattern(): var x: false count 1 up to 3: x: x or true end x pattern()"));
		Assert.assertEquals("or(or(false,true),true)", parseToString("def pattern(): var x: false count 3 down to 1: x: x or true end x pattern()"));

		Assert.assertEquals("or(or(false,true),true)", parseToString("def pattern(n:num): var x: false count n down to 1: x: x or true end x pattern(3)"));

		Assert.assertEquals("or(or(false,true),true)", parseToString("def pattern(): var x: false count 1 up to 3: var y: true x: x or y end x pattern()"));

		Assert.assertEquals("or(true,ap(sink))", parseToString("def a(): sink def b(): var x: false count 1 up to 2: x: true or a() end x b()"));

		String pattern = "def pattern(n:num): var r: false count 1 up to n: r: true and r end r ";
		Assert.assertEquals("true", parseToString(pattern + "var x: true count i: 1 up to 1: x: x or pattern(i) end x"));
		Assert.assertEquals("or(true,false)", parseToString(pattern + "var x: true count i: 1 up to 2: x: x or pattern(i) end x"));
		Assert.assertEquals("or(or(true,false),and(true,false))", parseToString(pattern + "var x: true count i: 1 up to 3: x: x or pattern(i) end x"));
	}

}
