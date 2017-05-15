package de.prob.ltl.parser;

import org.junit.Test;

public class PatternDefinitionTest extends AbstractParserTest {

	// Helper
	protected void parseDef(String input) {
		LtlParser parser = createParser(input);

		parser.pattern_def();

		if (hasErrors(parser)) {
			throw new RuntimeException();
		}
	}

	protected void parseSimple(String input) {
		LtlParser parser = createParser(input);

		parser.start();

		if (hasErrors(parser)) {
			throw new RuntimeException();
		}
	}

	// Tests
	@Test
	public void testDefinitionSimple() throws Exception {
		parseDef("def pattern(): true or false");
		parseDef("def f(x): x");
		parseDef("def A(x): x");
		parseDef("def fAa_0b(x): x");
	}

	@Test
	public void testDefinitionParamsSimple() throws Exception {
		parseDef("def pattern(x): x or false");
		parseDef("def pattern(x, y): x or y");
		parseDef("def pattern(x, y, z): x or y or z");
	}

	@Test
	public void testDefinitionNumParamSimple() throws Exception {
		parseDef("def pattern(n:num): false");
		parseDef("def pattern(x, n:num): false");
		parseDef("def pattern(n:num, x): false");
	}

	@Test
	public void testCallSimple() throws Exception {
		parseSimple("pattern()");
	}

	@Test
	public void testCallInDefSimple() throws Exception {
		parseDef("def pattern(): other()");
	}

	@Test
	public void testCallArgsSimple() throws Exception {
		parseSimple("pattern(true, false)");
		parseSimple("pattern(true, false, true or false)");
	}

	@Test
	public void testCallNumArgsSimple() throws Exception {
		parseSimple("pattern(true, 1)");
		parseSimple("pattern(false, 1234567890, true)");

		throwsException("1 or false");
		throwsException("pattern(true, -1)");
	}

	@Test
	public void testForbiddenDefintion() throws Exception {
		throwsException("def not(a): a not(true)");
		throwsException("def f(not): not f(true)");
		throwsException("def and(a): a and(true)");
		throwsException("def f(and): and f(true)");
		throwsException("def or(a): a or(true)");
		throwsException("def f(or): or f(true)");
		throwsException("def U(a): a U(true)");
		throwsException("def f(U): U f(true)");
		throwsException("def G(a): a G(true)");
		throwsException("def f(G): G f(true)");
		throwsException("def GF(a): a GF(true)");
		throwsException("def f(GF): GF f(false)");
		throwsException("def true(a): a true(true)");
		throwsException("def f(true): true f(false)");
		throwsException("def 1fAa_0b(x): x 1fAa_0b(true)");
		throwsException("def a b(x): x a b(true)");
		throwsException("def X(x): x X(true)");
		throwsException("def F(x): x F(true)");
		throwsException("def U(x): x U(true)");
		throwsException("def W(x): x W(true)");
		throwsException("def sink(x): x sink(true)");
		throwsException("def deadlock(x): x deadlock(true)");
		throwsException("def and(x): x and(true)");
		throwsException("def before(x): x before(true)");

		throwsException("def f(a,a): a f(false,true)");

		throwsException("def");
		throwsException("def ");
		throwsException("def abc");
		throwsException("def abc()");
		throwsException("def abc():");
		throwsException("def abc(a):");
	}

}
