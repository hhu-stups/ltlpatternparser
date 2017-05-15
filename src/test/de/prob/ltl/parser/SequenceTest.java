package de.prob.ltl.parser;

import junit.framework.Assert;

import org.junit.Test;

public class SequenceTest extends AbstractParserTest {

	// Helper
	protected void parseSeq(String input) {
		LtlParser parser = createParser(input);

		parser.seq_def();

		if (hasErrors(parser)) {
			throw new RuntimeException();
		}
	}

	protected void throwsExceptionSeq(String input) {
		try {
			parseSeq(input);
			Assert.fail("Exception should have been thrown.");
		} catch (RuntimeException e) {
		}
	}

	protected void parseCall(String input) {
		LtlParser parser = createParser(input);

		parser.seq_call();

		if (hasErrors(parser)) {
			throw new RuntimeException();
		}
	}

	protected void throwsExceptionCall(String input) {
		try {
			parseCall(input);
			Assert.fail("Exception should have been thrown.");
		} catch (RuntimeException e) {
		}
	}

	// Tests
	@Test
	public void testDefinition() throws Exception {
		parseSeq("(true, false)");
		parseSeq("(true, false, sink)");
		parseSeq("(true, false without sink)");
		parseSeq("s without x");

		throwsExceptionSeq("(true)");
		throwsExceptionSeq("(true, )");
		throwsExceptionSeq("(true without x)");
		throwsExceptionSeq("(true, without x)");
	}

	@Test
	public void testCall() throws Exception {
		parseCall("seq(s)");
		parseCall("seq(s without x)");
		parseCall("seq((a, b))");
		parseCall("seq((a, b without x))");

		throwsExceptionCall("seq(a, b)");
		throwsExceptionCall("seq(a, b without x)");
		throwsExceptionCall("seq((a, b) without x)");
		throwsExceptionCall("seq s without x");
	}

}
