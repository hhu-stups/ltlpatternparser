package de.prob.ltl.parser;

import org.junit.Test;

public class ScopeTest extends AbstractParserTest {

	// Tests
	@Test
	public void testSimpleCall() throws Exception {
		parse("before(true, false)");
		parse("after(true, false)");
		parse("between(true, false, sink)");
		parse("after_until(true, false, sink)");

		parse("before(after(true, false), between(true, false, sink))");
	}

}
