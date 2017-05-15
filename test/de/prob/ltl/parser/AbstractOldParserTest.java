package de.prob.ltl.parser;

import org.junit.Assert;
import org.junit.BeforeClass;

import de.prob.prolog.term.PrologTerm;

public abstract class AbstractOldParserTest extends AbstractParserTest {

	enum ExceptionCause {
		DownwardIncompatible,
		Deprecated,
		Unsupported
	};

	protected static de.be4.ltl.core.parser.LtlParser oldParser;
	protected static boolean printDeprecatedInputs = false;

	@BeforeClass
	public static void setUpBeforeClass() {
		oldParser = new de.be4.ltl.core.parser.LtlParser(parserBase);
	}

	protected String parseOld(String input) throws Exception {
		PrologTerm term = oldParser.generatePrologTerm(input, "current");
		return term.toString();
	}

	protected void assertEquals(String expected, String ... inputs) throws Exception {
		for (String input : inputs) {
			Assert.assertEquals(expected, parseToString(input));
			Assert.assertEquals(expected, parseOld(input));
		}
	}

	protected void throwsException(String expected, String input, ExceptionCause cause) {
		if (cause == ExceptionCause.Deprecated || cause == ExceptionCause.Unsupported) {
			throwsException(null, input, "Exception for new parser version should have been thrown. (Input: "+input+")");
		} else {
			try {
				parse(input);
			} catch(Exception e) {
				Assert.fail("Exception for new parser version should not have been thrown. (Input: "+input+")");
			}
		}

		try {
			Assert.assertEquals(expected, parseOld(input));
			if (!cause.equals(ExceptionCause.Deprecated)) {
				Assert.fail("Exception for old parser version should have been thrown. (Input: "+input+")");
			}
		} catch(Exception ex) {
			if (cause.equals(ExceptionCause.Deprecated)) {
				Assert.fail("Exception for old parser version should not have been thrown. (Input: "+input+")");
			}
		}
		if (cause.equals(ExceptionCause.Deprecated) && printDeprecatedInputs) {
			System.out.println("Deprecated input: " + input);
		}
	}

}
