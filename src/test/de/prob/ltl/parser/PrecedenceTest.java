package de.prob.ltl.parser;

import org.junit.Assert;
import org.junit.Test;


public class PrecedenceTest extends AbstractOldParserTest {

	// Helper
	public void incompatiblePrecedence(String expected, String incompatibleInput, String equivInput) throws Exception {
		Assert.assertEquals(expected, parseToString(incompatibleInput));
		String oldOutput = parseOld(incompatibleInput);
		if (expected.equals(oldOutput)) {
			Assert.fail("The old parser version output should differ from the expected output. (Input: "+incompatibleInput+")");
		} else if (printDeprecatedInputs) {
			System.out.println("Incompatible input: " + incompatibleInput + " ## Expected precedence: " + equivInput);
		}
		assertEquals(expected, equivInput);
	}

	// Tests
	@Test
	public void testAnd() throws Exception {
		// (), constant
		assertEquals("and(true,false)",
				"true & false",
				"(true & false)",
				"(true) & (false)");

		// not
		assertEquals("and(not(true),not(false))",
				"not true & not false",
				"(not true) & (not false)");
		assertEquals("and(not(not(true)),not(false))",
				"not not true & not false",
				"(not (not true)) & (not false)");
		assertEquals("not(and(not(true),not(false)))",
				"not (not true & not false)",
				"not ((not true) & (not false))");

		// and
		assertEquals("and(and(true,false),ap(sink))",
				"true & false & sink",
				"((true & false) & sink)");
		assertEquals("and(and(and(true,false),ap(sink)),ap(deadlock))",
				"true & false & sink & deadlock",
				"(((true & false) & sink) & deadlock)");

		// or
		assertEquals("or(and(true,false),ap(sink))",
				"true & false or sink",
				"(true & false) or sink");
		incompatiblePrecedence("or(ap(sink),and(true,false))",
				"sink or true & false",
				"sink or (true & false)");
		incompatiblePrecedence("or(and(ap(sink),ap(deadlock)),and(true,false))",
				"sink & deadlock or true & false",
				"(sink & deadlock) or (true & false)");
		incompatiblePrecedence("or(or(ap(sink),and(ap(deadlock),true)),false)",
				"sink or deadlock & true or false",
				"(sink or (deadlock & true)) or false");

		// implies
		assertEquals("implies(and(true,false),ap(sink))",
				"true & false => sink",
				"(true & false) => sink");
		assertEquals("implies(ap(sink),and(true,false))",
				"sink => true & false",
				"sink => (true & false)");
		assertEquals("implies(and(ap(sink),ap(deadlock)),and(true,false))",
				"sink & deadlock => true & false",
				"(sink & deadlock) => (true & false)");

		// Binary Ltl
		assertEquals("and(true,until(false,ap(sink)))",
				"true & false U sink",
				"true & (false U sink)");
		assertEquals("and(release(ap(sink),true),false)",
				"sink R true & false",
				"(sink R true) & false");
		assertEquals("and(and(ap(sink),trigger(ap(deadlock),true)),false)",
				"sink & deadlock T true & false",
				"(sink & (deadlock T true)) & false");

		// Unary Ltl
		assertEquals("and(globally(true),finally(false))",
				"G true & F false",
				"(G true) & (F false)");
		assertEquals("and(globally(true),false)",
				"G true & false",
				"(G true) & false");
		assertEquals("and(globally(finally(true)),false)",
				"GF true & false",
				"(GF true) & false");
	}

	@Test
	public void testOr() throws Exception {
		// (), constant
		assertEquals("or(true,false)",
				"true or false",
				"(true or false)",
				"(true) or (false)");

		// not
		assertEquals("or(not(true),not(false))",
				"not true or not false",
				"(not true) or (not false)");
		assertEquals("or(not(not(true)),not(false))",
				"not not true or not false",
				"(not (not true)) or (not false)");
		assertEquals("not(or(not(true),not(false)))",
				"not (not true or not false)",
				"not ((not true) or (not false))");

		// or
		assertEquals("or(or(true,false),ap(sink))",
				"true or false or sink",
				"((true or false) or sink)");
		assertEquals("or(or(or(true,false),ap(sink)),ap(deadlock))",
				"true or false or sink or deadlock",
				"(((true or false) or sink) or deadlock)");

		// implies
		assertEquals("implies(or(true,false),ap(sink))",
				"true or false => sink",
				"(true or false) => sink");
		assertEquals("implies(ap(sink),or(true,false))",
				"sink => true or false",
				"sink => (true or false)");
		assertEquals("implies(or(ap(sink),ap(deadlock)),or(true,false))",
				"sink or deadlock => true or false",
				"(sink or deadlock) => (true or false)");

		// Binary Ltl
		assertEquals("or(true,until(false,ap(sink)))",
				"true or false U sink",
				"true or (false U sink)");
		assertEquals("or(release(ap(sink),true),false)",
				"sink R true or false",
				"(sink R true) or false");
		assertEquals("or(or(ap(sink),trigger(ap(deadlock),true)),false)",
				"sink or deadlock T true or false",
				"(sink or (deadlock T true)) or false");

		// Unary Ltl
		assertEquals("or(globally(true),finally(false))",
				"G true or F false",
				"(G true) or (F false)");
		assertEquals("or(globally(true),false)",
				"G true or false",
				"(G true) or false");
		assertEquals("or(globally(finally(true)),false)",
				"GF true or false",
				"(GF true) or false");
	}

	@Test
	public void testImplies() throws Exception {
		// (), constant
		assertEquals("implies(true,false)",
				"true => false",
				"(true => false)",
				"(true) => (false)");

		// not
		assertEquals("implies(not(true),not(false))",
				"not true => not false",
				"(not true) => (not false)");
		assertEquals("implies(not(not(true)),not(false))",
				"not not true => not false",
				"(not (not true)) => (not false)");
		assertEquals("not(implies(not(true),not(false)))",
				"not (not true => not false)",
				"not ((not true) => (not false))");

		// implies
		assertEquals("implies(implies(true,false),ap(sink))",
				"true => false => sink",
				"((true => false) => sink)");
		assertEquals("implies(implies(implies(true,false),ap(sink)),ap(deadlock))",
				"true => false => sink => deadlock",
				"(((true => false) => sink) => deadlock)");

		// or
		assertEquals("implies(true,or(false,ap(sink)))",
				"true => false or sink",
				"true => (false or sink)");
		assertEquals("implies(or(ap(sink),true),false)",
				"sink or true => false",
				"(sink or true) => false");
		assertEquals("implies(implies(ap(sink),or(ap(deadlock),true)),false)",
				"sink => deadlock or true => false",
				"(sink => (deadlock or true)) => false");
		assertEquals("implies(or(ap(sink),ap(deadlock)),or(true,false))",
				"sink or deadlock => true or false",
				"(sink or deadlock) => (true or false)");

		// Binary Ltl
		assertEquals("implies(true,until(false,ap(sink)))",
				"true => false U sink",
				"true => (false U sink)");
		assertEquals("implies(release(ap(sink),true),false)",
				"sink R true => false",
				"(sink R true) => false");
		assertEquals("implies(implies(ap(sink),trigger(ap(deadlock),true)),false)",
				"sink => deadlock T true => false",
				"(sink => (deadlock T true)) => false");

		// Unary Ltl
		assertEquals("implies(globally(true),finally(false))",
				"G true => F false",
				"(G true) => (F false)");
		assertEquals("implies(globally(true),false)",
				"G true => false",
				"(G true) => false");
		assertEquals("implies(globally(finally(true)),false)",
				"GF true => false",
				"(GF true) => false");
	}

	@Test
	public void testBinaryLtl() throws Exception {
		// (), constant
		assertEquals("until(true,false)",
				"true U false",
				"(true U false)",
				"(true) U (false)");

		// not
		assertEquals("release(not(true),not(false))",
				"not true R not false",
				"(not true) R (not false)");
		assertEquals("weakuntil(not(not(true)),not(false))",
				"not not true W not false",
				"(not (not true)) W (not false)");
		assertEquals("not(since(not(true),not(false)))",
				"not (not true S not false)",
				"not ((not true) S (not false))");

		// Binary Ltl
		assertEquals("until(until(true,false),ap(sink))",
				"true U false U sink",
				"((true U false) U sink)");
		assertEquals("until(until(until(true,false),ap(sink)),ap(deadlock))",
				"true U false U sink U deadlock",
				"(((true U false) U sink) U deadlock)");

		assertEquals("until(release(true,false),ap(sink))",
				"true R false U sink",
				"(true R false) U sink");
		assertEquals("release(until(true,false),ap(sink))",
				"true U false R sink",
				"((true U false) R sink)");
		assertEquals("until(release(since(false,true),false),ap(sink))",
				"false S true R false U sink",
				"((false S true) R false) U sink");

		// or
		assertEquals("or(trigger(true,false),ap(sink))",
				"true T false or sink",
				"(true T false) or sink");
		assertEquals("or(ap(sink),trigger(true,false))",
				"sink or true T false",
				"sink or (true T false)");
		assertEquals("or(until(ap(sink),ap(deadlock)),until(true,false))",
				"sink U deadlock or true U false",
				"(sink U deadlock) or (true U false)");
		assertEquals("or(or(ap(sink),until(ap(deadlock),true)),false)",
				"sink or deadlock U true or false",
				"(sink or (deadlock U true)) or false");

		// implies
		assertEquals("implies(until(true,false),ap(sink))",
				"true U false => sink",
				"(true U false) => sink");
		assertEquals("implies(ap(sink),until(true,false))",
				"sink => true U false",
				"sink => (true U false)");
		assertEquals("implies(until(ap(sink),ap(deadlock)),until(true,false))",
				"sink U deadlock => true U false",
				"(sink U deadlock) => (true U false)");

		// Unary Ltl
		assertEquals("until(globally(true),finally(false))",
				"G true U F false",
				"(G true) U (F false)");
		assertEquals("until(globally(true),false)",
				"G true U false",
				"(G true) U false");
		assertEquals("until(globally(finally(true)),false)",
				"GF true U false",
				"(GF true) U false");
	}

}
