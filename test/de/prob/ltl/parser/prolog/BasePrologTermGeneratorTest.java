package de.prob.ltl.parser.prolog;

import org.junit.Assert;
import org.junit.Test;

import de.prob.ltl.parser.AbstractParserTest;

public class BasePrologTermGeneratorTest extends AbstractParserTest {

	@Test
	public void testAtoms() {
		Assert.assertEquals("true", parseToString("true"));
		Assert.assertEquals("false", parseToString("false"));
		Assert.assertEquals("ap(sink)", parseToString("sink"));
		Assert.assertEquals("ap(deadlock)", parseToString("deadlock"));
		Assert.assertEquals("ap(stateid(current))", parseToString("current"));

		Assert.assertEquals("true", parseToString("(true)"));
		Assert.assertEquals("true", parseToString(" ( (true ) ) "));

		Assert.assertEquals("ap(predicate('...'))", parseToString("{...}"));
		Assert.assertEquals("ap(predicate(abc))", parseToString("{abc}"));
		Assert.assertEquals("ap(predicate(' a b c'))", parseToString("{ a b c}"));
		Assert.assertEquals("ap(predicate(' {}'))", parseToString("{ {}}"));

		Assert.assertEquals("action(transition_predicate('...'))", parseToString("[...]"));
		Assert.assertEquals("action(transition_predicate(abc))", parseToString("[abc]"));
		Assert.assertEquals("action(transition_predicate(' a b c'))", parseToString("[ a b c]"));
		Assert.assertEquals("action(transition_predicate(' []'))", parseToString("[ []]"));

		Assert.assertEquals("ap(enabled(transition_predicate('...')))", parseToString("e(...)"));
		Assert.assertEquals("ap(enabled(transition_predicate(abc)))", parseToString("e(abc)"));
		Assert.assertEquals("ap(enabled(transition_predicate(' a b c')))", parseToString("e( a b c)"));
		Assert.assertEquals("ap(enabled(transition_predicate(' ()')))", parseToString("e( ())"));
	}

	@Test
	public void testExpr() {
		Assert.assertEquals("not(true)", parseToString("not true"));
		Assert.assertEquals("not(true)", parseToString("! true"));
		Assert.assertEquals("not(true)", parseToString("!true"));
		Assert.assertEquals("not(true)", parseToString("not(true)"));

		Assert.assertEquals("globally(true)", parseToString("G true"));
		Assert.assertEquals("finally(true)", parseToString("F true"));
		Assert.assertEquals("next(true)", parseToString("X true"));
		Assert.assertEquals("historically(true)", parseToString("H true"));
		Assert.assertEquals("once(true)", parseToString("O true"));
		Assert.assertEquals("yesterday(true)", parseToString("Y true"));
		Assert.assertEquals("globally(finally(true))", parseToString("GF true"));
		Assert.assertEquals("historically(once(true))", parseToString("HO true"));

		Assert.assertEquals("until(true,false)", parseToString("true U false"));
		Assert.assertEquals("weakuntil(true,false)", parseToString("true W false"));
		Assert.assertEquals("release(true,false)", parseToString("true R false"));
		Assert.assertEquals("since(true,false)", parseToString("true S false"));
		Assert.assertEquals("trigger(true,false)", parseToString("true T false"));

		Assert.assertEquals("and(true,false)", parseToString("true and false"));
		Assert.assertEquals("and(true,false)", parseToString("true & false"));
		Assert.assertEquals("or(true,false)", parseToString("true or false"));
		Assert.assertEquals("or(true,false)", parseToString("true | false"));
		Assert.assertEquals("implies(true,false)", parseToString("true => false"));
	}

}
