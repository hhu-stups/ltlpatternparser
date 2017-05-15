package de.prob.ltl.parser.prolog;

import org.junit.Assert;
import org.junit.Test;

import de.prob.ltl.parser.AbstractParserTest;

public class ScopePrologTermGeneratorTest extends AbstractParserTest {

	// Helper
	public void assertEquals(String expected, String actual) {
		String a = parseToString(expected);
		String b = parseToString(actual);
		Assert.assertEquals(a, b);
	}

	// Tests
	@Test
	public void testBasicTerms() {
		assertEquals("F({r}) => {term}", "before({r}, {term})");
		assertEquals("G(!{q}) | !{q} U ({q} & {term})", "after({q}, {term})");
		assertEquals("G(({q} & !{r} & F({r}) & Y(H(!{q}) | (!{q} S {r}))) => {term})", "between({q}, {r}, {term})");
		assertEquals("G(({q} & !{r} & Y(H(!{q}) | (!{q} S {r}))) => {term})", "after_until({q}, {r}, {term})");
	}

	@Test
	public void testOtherTerms() {
		assertEquals("F({r}) => !{term}", "before({r}, !{term})");
		assertEquals("G(!{q}) | !{q} U ({q} & ({a} and {b}))", "after({q}, {a} and {b})");
		assertEquals("G(({q} & !{r} & F({r}) & Y(H(!{q}) | (!{q} S {r}))) => ({a} or {b}))", "between({q}, {r}, {a} or {b})");
		assertEquals("G(({q} & !{r} & Y(H(!{q}) | (!{q} S {r}))) => ({a} => {b}))", "after_until({q}, {r}, {a} => {b})");
	}

	@Test
	public void testBeforeScopeOperators() {
		String basic = "F({r}) => (%s)";
		assertEquals(String.format(basic, "false U {r}"), "before({r}, G(false))");
		assertEquals(String.format(basic, "!{r} U (false & !{r})"), "before({r}, F(false))");
		assertEquals(String.format(basic, "!{r} & X(false & !{r})"), "before({r}, X(false))");
		assertEquals(String.format(basic, "H(false)"), "before({r}, H(false))");
		assertEquals(String.format(basic, "O(false)"), "before({r}, O(false))");
		assertEquals(String.format(basic, "Y(false)"), "before({r}, Y(false))");

		assertEquals(String.format(basic, "(false & !{r}) U (sink & !{r})"), "before({r}, false U sink)");
		assertEquals(String.format(basic, "false U ({r} | sink)"), "before({r}, false W sink)");
		assertEquals(String.format(basic, "sink U ({r} | (false & sink))"), "before({r}, false R sink)");
		assertEquals(String.format(basic, "false S sink"), "before({r}, false S sink)");
		assertEquals(String.format(basic, "false T sink"), "before({r}, false T sink)");

		assertEquals(String.format(basic, "(!{r} U (false & !{r})) U {r}"), "before({r}, GF(false))");
		assertEquals(String.format(basic, "((!{r} & X(false & !{r})) & !{r}) U ((true U {r}) & !{r})"), "before({r}, X(false) U G(true))");
		assertEquals(String.format(basic, "HO(false)"), "before({r}, HO(false))");
		assertEquals(String.format(basic, "Y(false) S H(true)"), "before({r}, Y(false) S H(true))");
	}

	@Test
	public void testAfterScopeOperators() {
		String basic = "G(!{q}) | !{q} U ({q} & (%s))";
		assertEquals(String.format(basic, "G(false)"), "after({q}, G(false))");
		assertEquals(String.format(basic, "F(false)"), "after({q}, F(false))");
		assertEquals(String.format(basic, "X(false)"), "after({q}, X(false))");
		assertEquals(String.format(basic, "false S ({q} & false)"), "after({q}, H(false))");
		assertEquals(String.format(basic, "!{q} S false"), "after({q}, O(false))");
		assertEquals(String.format(basic, "!{q} & Y(false)"), "after({q}, Y(false))");

		assertEquals(String.format(basic, "false U sink"), "after({q}, false U sink)");
		assertEquals(String.format(basic, "false W sink"), "after({q}, false W sink)");
		assertEquals(String.format(basic, "false R sink"), "after({q}, false R sink)");
		assertEquals(String.format(basic, "(false & !{q}) S sink"), "after({q}, false S sink)");
		assertEquals(String.format(basic, "sink S ({q} & sink | false & sink)"), "after({q}, false T sink)");

		assertEquals(String.format(basic, "GF(false)"), "after({q}, GF(false))");
		assertEquals(String.format(basic, "X(false) U G(true)"), "after({q}, X(false) U G(true))");
		assertEquals(String.format(basic, "(!{q} S false) S ({q} & (!{q} S false))"), "after({q}, HO(false))");
		assertEquals(String.format(basic, "((!{q} & Y(false)) & !{q}) S (true S ({q} & true))"), "after({q}, Y(false) S H(true))");
	}

	@Test
	public void testBetweenScopeOperators() {
		String basic = "G(({q} & !{r} & F({r}) & Y(H(!{q}) | (!{q} S {r}))) => (%s))";
		assertEquals(String.format(basic, "false U {r}"), "between({q}, {r}, G(false))");
		assertEquals(String.format(basic, "!{r} U (false & !{r})"), "between({q}, {r}, F(false))");
		assertEquals(String.format(basic, "!{r} & X(false & !{r})"), "between({q}, {r}, X(false))");
		assertEquals(String.format(basic, "false S ({q} & false)"), "between({q}, {r}, H(false))");
		assertEquals(String.format(basic, "!{q} S false"), "between({q}, {r}, O(false))");
		assertEquals(String.format(basic, "!{q} & Y(false)"), "between({q}, {r}, Y(false))");

		assertEquals(String.format(basic, "(false & !{r}) U (sink & !{r})"), "between({q}, {r}, false U sink)");
		assertEquals(String.format(basic, "false U ({r} | sink)"), "between({q}, {r}, false W sink)");
		assertEquals(String.format(basic, "sink U ({r} | (false & sink))"), "between({q}, {r}, false R sink)");
		assertEquals(String.format(basic, "(false & !{q}) S sink"), "between({q}, {r}, false S sink)");
		assertEquals(String.format(basic, "sink S ({q} & sink | false & sink)"), "between({q}, {r}, false T sink)");

		assertEquals(String.format(basic, "(!{r} U (false & !{r})) U {r}"), "between({q}, {r}, GF(false))");
		assertEquals(String.format(basic, "((!{r} & X(false & !{r})) & !{r}) U (true U {r} & !{r})"), "between({q}, {r}, X(false) U G(true))");
		assertEquals(String.format(basic, "(!{q} S false) S ({q} & (!{q} S false))"), "between({q}, {r}, HO(false))");
		assertEquals(String.format(basic, "((!{q} & Y(false)) & !{q}) S (true S ({q} & true))"), "between({q}, {r}, Y(false) S H(true))");
	}

	@Test
	public void testAfterUntilScopeOperators() {
		String basic = "G(({q} & !{r} & Y(H(!{q}) | (!{q} S {r}))) => (%s))";
		assertEquals(String.format(basic, "false W {r}"), "after_until({q}, {r}, G(false))");
		assertEquals(String.format(basic, "!{r} U (false & !{r})"), "after_until({q}, {r}, F(false))");
		assertEquals(String.format(basic, "!{r} & X(false & !{r})"), "after_until({q}, {r}, X(false))");
		assertEquals(String.format(basic, "false S ({q} & false)"), "after_until({q}, {r}, H(false))");
		assertEquals(String.format(basic, "!{q} S false"), "after_until({q}, {r}, O(false))");
		assertEquals(String.format(basic, "!{q} & Y(false)"), "after_until({q}, {r}, Y(false))");

		assertEquals(String.format(basic, "(false & !{r}) U (sink & !{r})"), "after_until({q}, {r}, false U sink)");
		assertEquals(String.format(basic, "false W ({r} | sink)"), "after_until({q}, {r}, false W sink)");
		assertEquals(String.format(basic, "sink W ({r} | (false & sink))"), "after_until({q}, {r}, false R sink)");
		assertEquals(String.format(basic, "(false & !{q}) S sink"), "after_until({q}, {r}, false S sink)");
		assertEquals(String.format(basic, "sink S ({q} & sink | false & sink)"), "after_until({q}, {r}, false T sink)");

		assertEquals(String.format(basic, "(!{r} U (false & !{r})) W {r}"), "after_until({q}, {r}, GF(false))");
		assertEquals(String.format(basic, "((!{r} & X(false & !{r})) & !{r}) U (true W {r} & !{r})"), "after_until({q}, {r}, X(false) U G(true))");
		assertEquals(String.format(basic, "(!{q} S false) S ({q} & (!{q} S false))"), "after_until({q}, {r}, HO(false))");
		assertEquals(String.format(basic, "((!{q} & Y(false)) & !{q}) S (true S ({q} & true))"), "after_until({q}, {r}, Y(false) S H(true))");
	}

	@Test
	public void testMixedScopes() {
		String before = "F({r}) => (%s)";
		String after = "G(!{q}) | !{q} U ({q} & (%s))";

		assertEquals(String.format("before({r}, %s)", String.format(before, "H({p})")), "before({r}, before({r}, H({p})))");
		assertEquals(String.format("before({r}, %s)", String.format(after, "{p} S ({q} & {p})")), "before({r}, after({q}, H({p})))");

		assertEquals(String.format("after({r}, %s)", String.format(before, "H({p})")), "after({r}, before({r}, H({p})))");
		assertEquals(String.format("after({r}, %s)", String.format(after, "{p} S ({q} & {p})")), "after({r}, after({q}, H({p})))");

		assertEquals(String.format("between({q}, {r}, %s)", String.format(before, "H({p})")), "between({q}, {r}, before({r}, H({p})))");
		assertEquals(String.format("between({q}, {r}, %s)", String.format(after, "{p} S ({q} & {p})")), "between({q}, {r}, after({q}, H({p})))");

		assertEquals(String.format("after_until({q}, {r}, %s)", String.format(before, "H({p})")), "after_until({q}, {r}, before({r}, H({p})))");
		assertEquals(String.format("after_until({q}, {r}, %s)", String.format(after, "{p} S ({q} & {p})")), "after_until({q}, {r}, after({q}, H({p})))");
	}

}
