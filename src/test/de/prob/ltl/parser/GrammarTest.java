package de.prob.ltl.parser;

import junit.framework.Assert;

import org.junit.Test;

import de.prob.ltl.parser.pattern.PatternManager;


public class GrammarTest extends AbstractOldParserTest {

	@Test
	public void testEmptyInput() throws Exception {
		PatternManager patternManager = new PatternManager();
		Assert.assertEquals(0, parse(""));
		Assert.assertEquals(null, parseToString(""));
		Assert.assertEquals(0, parse("", patternManager));
		Assert.assertEquals(null, parseToString("", patternManager));
		Assert.assertEquals(0, parse(" "));
		Assert.assertEquals(null, parseToString(" "));
		Assert.assertEquals(0, parse(" ", patternManager));
		Assert.assertEquals(null, parseToString(" ", patternManager));
		Assert.assertEquals(0, parse("//"));
		Assert.assertEquals(null, parseToString("//"));
		Assert.assertEquals(0, parse("//", patternManager));
		Assert.assertEquals(null, parseToString("//", patternManager));
		Assert.assertEquals(0, parse("/* */"));
		Assert.assertEquals(null, parseToString("/* */"));
		Assert.assertEquals(0, parse("/* */", patternManager));
		Assert.assertEquals(null, parseToString("/* */", patternManager));
	}

	@Test
	public void testParenthesisExpression() throws Exception {
		assertEquals("true", "(true)");
		assertEquals("true", "((true))");
		assertEquals("true", " ( ( ( true ) ) ) ");
		assertEquals("and(true,false)", "((true& false))");
	}

	@Test
	public void testNot() throws Exception {
		String expected = "not(true)";
		assertEquals(expected, "not(true)");
		assertEquals(expected, "not (true)");
		throwsException(expected, "nottrue", ExceptionCause.Deprecated);
		assertEquals(expected, "not true");
		throwsException(expected, "!(true)", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "! (true)", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "!true", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "! true", ExceptionCause.DownwardIncompatible);

		expected = "not(not(true))";
		assertEquals(expected, "not not true");
		assertEquals(expected, "not (not true)");
		throwsException(expected, "not ! true", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "not!true", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "!not true", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "!!true", ExceptionCause.DownwardIncompatible);

		expected = "next(not(true))";
		assertEquals(expected, "X not true");
		assertEquals(expected, "X (not true)");
		throwsException(expected, "X!true", ExceptionCause.DownwardIncompatible);

		expected = "not(next(true))";
		assertEquals(expected, "not X true");
		assertEquals(expected, "not(X true)");
		throwsException(expected, "!X true", ExceptionCause.DownwardIncompatible);

		expected = "not(next(finally(true)))";
		assertEquals(expected, "not XF true");
		assertEquals(expected, "not(XF true)");
		throwsException(expected, "!XF true", ExceptionCause.DownwardIncompatible);

		expected = "not(ap(predicate('...')))";
		assertEquals(expected, "not {...}");
		assertEquals(expected, "not{...}");
		assertEquals(expected, "not({...})");

		expected = "not(action(transition_predicate('...')))";
		assertEquals(expected, "not [...]");
		assertEquals(expected, "not[...]");
		assertEquals(expected, "not([...])");

		expected = "not(ap(enabled(transition_predicate('...'))))";
		assertEquals(expected, "not e(...)");
		assertEquals(expected, "not(e(...))");
		throwsException(expected, "note(...)", ExceptionCause.Deprecated);
	}

	@Test
	public void testAnd() throws Exception {
		String expected = "and(true,false)";
		assertEquals(expected, "true & false");
		assertEquals(expected, "true&false");
		assertEquals(expected, "(true)& (false)");
		assertEquals(expected, "(true & false)");
		throwsException(expected, "true and false", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "(true) and(false)", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "(true and false)", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "true andfalse", ExceptionCause.Unsupported);

		expected = "and(ap(predicate(abc)),ap(enabled(transition_predicate(def))))";
		assertEquals(expected, "{abc} & e(def)");
		throwsException(expected, "{abc}and e(def)", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "{abc}ande(def)", ExceptionCause.Unsupported);

		expected = "and(ap(predicate(abc)),action(transition_predicate(def)))";
		assertEquals(expected, "{abc}&[def]");
		throwsException(expected, "{abc}and[def]", ExceptionCause.DownwardIncompatible);
	}

	@Test
	public void testOr() throws Exception {
		String expected = "or(true,false)";
		assertEquals(expected, "true or false");
		throwsException(expected, "true orfalse", ExceptionCause.Deprecated);
		assertEquals(expected, "(true)or (false)");
		assertEquals(expected, "(true or false)");
		throwsException(expected, "true | false", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "(true) |(false)", ExceptionCause.DownwardIncompatible);
		throwsException(expected, "(true | false)", ExceptionCause.DownwardIncompatible);

		expected = "or(ap(predicate(abc)),ap(enabled(transition_predicate(def))))";
		assertEquals(expected, "{abc}or e(def)");
		throwsException(expected, "{abc}ore(def)", ExceptionCause.Deprecated);
		throwsException(expected, "{abc} |e(def)", ExceptionCause.DownwardIncompatible);

		expected = "or(ap(predicate(abc)),action(transition_predicate(def)))";
		assertEquals(expected, "{abc}or[def]");
		throwsException(expected, "{abc} | [def]", ExceptionCause.DownwardIncompatible);
	}

	@Test
	public void testImplies() throws Exception {
		String expected = "implies(true,false)";
		assertEquals(expected, "true => false");
		assertEquals(expected, "true=>false");
		assertEquals(expected, "(true)=> (false)");
		assertEquals(expected, "(true => false)");

		expected = "implies(ap(predicate(abc)),ap(enabled(transition_predicate(def))))";
		assertEquals(expected, "{abc}=>e(def)");

		expected = "implies(ap(predicate(abc)),action(transition_predicate(def)))";
		assertEquals(expected, "{abc}=>[def]");
	}

	@Test
	public void testBinaryOp() throws Exception {
		String expected = "until(true,false)";
		assertEquals(expected, "true U false");
		throwsException(expected, "true Ufalse", ExceptionCause.Deprecated);
		assertEquals(expected, "(true)U (false)");
		assertEquals(expected, "(true U false)");

		expected = "release(ap(predicate(abc)),ap(enabled(transition_predicate(def))))";
		assertEquals(expected, "{abc}R e(def)");
		throwsException(expected, "{abc}Re(def)", ExceptionCause.Deprecated);

		expected = "weakuntil(ap(predicate(abc)),action(transition_predicate(def)))";
		assertEquals(expected, "{abc}W[def]");

		assertEquals("since(true,false)", "true S false");
		assertEquals("trigger(true,false)", "true T false");
	}

	@Test
	public void testUnaryOp() throws Exception {
		String expected = "globally(true)";
		assertEquals(expected, "G true");
		throwsException(expected, "Gtrue", ExceptionCause.Deprecated);
		assertEquals(expected, "G(true)");
		assertEquals(expected, "(G true)");

		assertEquals("finally(ap(predicate(abc)))", "F{abc}");
		assertEquals("next(action(transition_predicate(abc)))", "X[abc]");

		expected = "historically(ap(enabled(transition_predicate(abc))))";
		assertEquals(expected, "H e(abc)");
		throwsException(expected, "He(abc)", ExceptionCause.Deprecated);

		assertEquals("once(yesterday(true))", "OY true");
		assertEquals("next(next(true))", "XX true");
		throwsException("next(next(true))", "XXtrue", ExceptionCause.Deprecated);
	}

	@Test
	public void testPredicate() throws Exception {
		assertEquals("ap(predicate('...'))", "{...}");
		assertEquals("ap(predicate(abc))", "{abc}");
		assertEquals("ap(predicate('ab cd'))", "{ab cd}");
		assertEquals("ap(predicate(' ab cd '))", "{ ab cd }");

		assertEquals("ap(predicate('{ab}'))", "{{ab}}");
		assertEquals("ap(predicate('(ab)'))", "{(ab)}");
		assertEquals("ap(predicate('[ab]'))", "{[ab]}");
		assertEquals("ap(predicate('e(ab)'))", "{e(ab)}");

		assertEquals("ap(predicate(' {..} '))", "{ {..} }");
		assertEquals("ap(predicate(' {{..} }'))", "{ {{..} }}");

		assertEquals("ap(predicate(' ( '))", "{ ( }");
		throwsException(null, "{ { }", ExceptionCause.Unsupported);
		assertEquals("ap(predicate(' [ '))", "{ [ }");
		assertEquals("ap(predicate(' e( '))", "{ e( }");

		throwsException(null, "{ } }", ExceptionCause.Unsupported);
		throwsException(null, "{ (} }", ExceptionCause.Unsupported);
		throwsException(null, "{ [} }", ExceptionCause.Unsupported);
	}

	@Test
	public void testAction() throws Exception {
		assertEquals("action(transition_predicate('...'))", "[...]");
		assertEquals("action(transition_predicate(abc))", "[abc]");
		assertEquals("action(transition_predicate('ab cd'))", "[ab cd]");
		assertEquals("action(transition_predicate(' ab cd '))", "[ ab cd ]");

		assertEquals("action(transition_predicate('{ab}'))", "[{ab}]");
		assertEquals("action(transition_predicate('(ab)'))", "[(ab)]");
		assertEquals("action(transition_predicate('[ab]'))", "[[ab]]");
		assertEquals("action(transition_predicate('e(ab)'))", "[e(ab)]");

		assertEquals("action(transition_predicate(' [..] '))", "[ [..] ]");
		assertEquals("action(transition_predicate(' [[..] ]'))", "[ [[..] ]]");

		// TODO: assertEquals("action(transition_predicate(' ( '))", "[ ( ]");
		assertEquals("action(transition_predicate(' { '))", "[ { ]");
		throwsException(null, "[ [ ]", ExceptionCause.Unsupported);
		// TODO: assertEquals("action(transition_predicate(' e( '))", "[ e( ]");

		throwsException(null, "[ ] ]", ExceptionCause.Unsupported);
		throwsException(null, "[ {] ]", ExceptionCause.Unsupported);
		assertEquals("action(transition_predicate(' () '))", "[ () ]");
		assertEquals("action(transition_predicate(' e() '))", "[ e() ]");
	}

	@Test
	public void testEnabled() throws Exception {
		assertEquals("ap(enabled(transition_predicate('...')))", "e(...)");
		assertEquals("ap(enabled(transition_predicate(abc)))", "e(abc)");
		assertEquals("ap(enabled(transition_predicate(' ab cd ')))", "e( ab cd )");

		assertEquals("ap(enabled(transition_predicate('(abc)')))", "e((abc))");
		assertEquals("ap(enabled(transition_predicate('{abc}')))", "e({abc})");
		assertEquals("ap(enabled(transition_predicate('[abc]')))", "e([abc])");
		assertEquals("ap(enabled(transition_predicate('e(abc)')))", "e(e(abc))");

		assertEquals("ap(enabled(transition_predicate(' e( e (...) )')))", "e( e( e (...) ))");

		throwsException(null, "e( ( )", ExceptionCause.Unsupported);
		assertEquals("ap(enabled(transition_predicate(' { ')))", "e( { )");
		// TODO: throwsException(null, "e( [ )", ExceptionCause.Unsupported);
		throwsException(null, "e( e( )", ExceptionCause.Unsupported);

		// TODO: throwsException(null, "e( ] )", ExceptionCause.Unsupported);
		throwsException(null, "e( ) )", ExceptionCause.Unsupported);
		assertEquals("ap(enabled(transition_predicate(' [] ')))", "e( [] )");
		assertEquals("ap(enabled(transition_predicate(' () ')))", "e( () )");
	}

	@Test
	public void testConstants() throws Exception {
		assertEquals("true", "true");
		assertEquals("false", "false");
		assertEquals("ap(sink)", "sink");
		assertEquals("ap(deadlock)", "deadlock");
		assertEquals("ap(stateid(current))", "current");
		throwsException(null, "true false", ExceptionCause.Unsupported);
		throwsException(null, "truefalse", ExceptionCause.Unsupported);
	}

	@Test
	public void testLinebreaks() throws Exception {
		assertEquals("or(true,false)", "true\nor\nfalse");
	}

	@Test
	public void testOneLineComment() throws Exception {
		throwsException("or(true,false)", "true or false// comment", ExceptionCause.DownwardIncompatible);
		throwsException("or(true,false)", "true// a comment\nor false", ExceptionCause.DownwardIncompatible);
		throwsException("or(true,false)", "true//\nor false", ExceptionCause.DownwardIncompatible);
	}

	@Test
	public void testMultiLineComment() throws Exception {
		throwsException("or(true,false)", "true or false/* a comment */", ExceptionCause.DownwardIncompatible);
		throwsException("or(true,false)", "/* a comment */true or false", ExceptionCause.DownwardIncompatible);
		throwsException("or(true,false)", "true or/* a comment */ false", ExceptionCause.DownwardIncompatible);
		throwsException("or(true,false)", "true/* a comment\n*/or false", ExceptionCause.DownwardIncompatible);
		throwsException("or(true,false)", "true/* a\ncomment*/or false", ExceptionCause.DownwardIncompatible);
		throwsException("or(true,false)", "true/**/or false", ExceptionCause.DownwardIncompatible);

		throwsException(null, "true or false/*", ExceptionCause.Unsupported);
		throwsException(null, "/*true or false", ExceptionCause.Unsupported);
		throwsException(null, "true */ or false", ExceptionCause.Unsupported);
	}

}
