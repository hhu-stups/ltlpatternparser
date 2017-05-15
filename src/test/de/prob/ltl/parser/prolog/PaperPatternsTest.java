package de.prob.ltl.parser.prolog;

import org.junit.Assert;
import org.junit.Test;

import de.prob.ltl.parser.AbstractParserTest;

public class PaperPatternsTest extends AbstractParserTest {

	// Helper
	public void assertEquals(String expected, String actual) {
		String a = parseToString(expected);
		String b = parseToString(actual);
		Assert.assertEquals(a, b);
	}

	public void testBefore(String expected, String pattern) {
		assertEquals(String.format("F({r}) => (%s)", expected), String.format("before({r}, %s)", pattern));
	}

	public void testAfter(String expected, String pattern) {
		assertEquals(String.format("G(!{q}) | !{q} U ({q} & (%s))", expected), String.format("after({q}, %s)", pattern));
	}

	public void testBetween(String expected, String pattern) {
		assertEquals(String.format("G(({q} & !{r} & F({r}) & Y(H(!{q}) | (!{q} S {r}))) => (%s))", expected), String.format("between({q}, {r}, %s)", pattern));
	}

	public void testAfterUntil(String expected, String pattern) {
		assertEquals(String.format("G(({q} & !{r} & Y(H(!{q}) | (!{q} S {r}))) => (%s))", expected), String.format("after_until({q}, {r}, %s)", pattern));
	}

	// Tests
	@Test
	public void testAbsence() {
		String pattern = "G!{p}";

		testBefore("!{p} U {r}", pattern);		// !p!p!p!pr
		testAfter("G!{p}", pattern);			// q!p!p!p...
		testBetween("!{p} U {r}", pattern);		//    q!p!pr   q!p!pr
		testAfterUntil("!{p} W {r}", pattern);	//    q!p!pr   q!p!p!p...
	}

	@Test
	public void testExistence() {
		String pattern = "F{p}";

		testBefore("!{r} U ({p} & !{r})", pattern);		//    p  r
		testAfter("F{p}", pattern);						// q     p
		testBetween("!{r} U ({p} & !{r})", pattern);	//    q  p r   q p  r
		testAfterUntil("!{r} U ({p} & !{r})", pattern);	//    q p   r   q      p...
	}

	// TODO @Test
	public void testBoundedExistence() {
		String pattern = "def ";

		testBefore("", pattern);
		testAfter("", pattern);
		testBetween("", pattern);
		testAfterUntil("", pattern);
	}

	@Test
	public void testUniversality() {
		String pattern = "G{p}";

		testBefore("{p} U {r}", pattern);		// ppppppr
		testAfter("G{p}", pattern);				// qpppppppp...
		testBetween("{p} U {r}", pattern);		//    qpppr   qppppr
		testAfterUntil("{p} W {r}", pattern);	//    qppppr   qppppp...
	}

	@Test
	public void testPrecedence() {
		String pattern = "!{p} W {s}";

		testBefore("!{p} U ({r} | {s})", pattern);		//     s p r
		testAfter("!{p} W {s}", pattern);				// q    sp
		testBetween("!{p} U ({r} | {s})", pattern);		//    q  s r   q   r  q  s pr
		testAfterUntil("!{p} W ({r} | {s})", pattern);	//    q s p r   q  sp  ...
	}

	@Test
	public void testResponse() {
		String pattern = "G({p} => F({s}))";

		testBefore("({p} => (!{r} U ({s} & !{r}))) U {r}", pattern);		//     p  s r
		testAfter("G({p} => F({s}))", pattern);								// q  p s
		testBetween("({p} => (!{r} U ({s} & !{r}))) U {r}", pattern);		//    q  s r   q   r  q  psr
		testAfterUntil("({p} => (!{r} U ({s} & !{r}))) W {r}", pattern);	//    q s p s r   q  s ps  ...
	}

	@Test
	public void testPrecedenceChain() {
		String pattern2_1 = "!{p} W seq(({s}, {t} without {p}))";
		// !{p} W ({s} & !{p} & X(!{p} U {t}))

		testBefore("!{p} U ({r} | ({s} & !{p} & (!{r} & X((!{p} & !{r}) U ({t} & !{r}) & !{r}))))", pattern2_1);		//    s  t p r
		testAfter("!{p} W ({s} & !{p} & X(!{p} U {t}))", pattern2_1);		// q  s     t p
		testBetween("!{p} U ({r} | ({s} & !{p} & (!{r} & X(((!{p} & !{r}) U ({t} & !{r})) & !{r}))))", pattern2_1);	//    q  s r   q t  r  q  st r   q st p r
		testAfterUntil("!{p} W ({r} | ({s} & !{p} & (!{r} & X(((!{p} & !{r}) U ({t} & !{r})) & !{r}))))", pattern2_1);	//    q s t p r   q  s t p p p  ...
	}

	// TODO @Test
	public void testResponseChain() {

	}

	// TODO @Test
	public void testConstrainedChain() {

	}

}
