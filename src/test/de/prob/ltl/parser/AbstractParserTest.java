package de.prob.ltl.parser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.junit.Assert;
import org.junit.BeforeClass;

import de.prob.ltl.parser.pattern.PatternManager;
import de.prob.parserbase.UnparsedParserBase;
import de.prob.prolog.term.PrologTerm;

public abstract class AbstractParserTest {

	protected static UnparsedParserBase parserBase;

	@BeforeClass
	public static void setUpBeforeClass() {
		parserBase = new UnparsedParserBase("expression", "predicate", "transition_predicate");
	}

	protected LtlParser createParser(String input) {
		return createParser(input, new TestErrorListener(), new TestWarningListener());
	}

	protected LtlParser createParser(String input, TestErrorListener errorListener, TestWarningListener warningListener) {
		LtlParser parser = new LtlParser(input);

		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		parser.addWarningListener(warningListener);

		return parser;
	}

	protected int getWarningCount(LtlParser parser) {
		if (parser.getWarningListeners().size() > 0 && parser.getWarningListeners().get(0) instanceof TestWarningListener) {
			TestWarningListener listener = (TestWarningListener) parser.getWarningListeners().get(0);
			return listener.getCount();
		}
		return -1;
	}

	protected boolean hasErrors(LtlParser parser) {
		if (parser.getErrorListeners().size() > 0 && parser.getErrorListeners().get(0) instanceof TestErrorListener) {
			TestErrorListener listener = (TestErrorListener) parser.getErrorListeners().get(0);

			if (listener.getErrors() > 0) {
				return true;
			}
		}
		return false;
	}

	protected List<RuntimeException> getExceptions(LtlParser parser) {
		if (parser.getErrorListeners().size() > 0 && parser.getErrorListeners().get(0) instanceof TestErrorListener) {
			TestErrorListener listener = (TestErrorListener) parser.getErrorListeners().get(0);

			return listener.getExceptions();
		}
		return Collections.emptyList();
	}

	protected int parse(String input) {
		return parse(input, null);
	}

	protected List<RuntimeException> parseAndGetErrors(String input) {
		return parseAndGetErrors(input, null);
	}

	protected String parseToString(String input) {
		return parseToString(input, null);
	}

	protected int parse(String input, PatternManager patternManager) {
		LtlParser parser = createParser(input);
		parser.setPatternManager(patternManager);
		parser.parse();
		if (hasErrors(parser)) {
			throw getExceptions(parser).get(0);
		}

		return getWarningCount(parser);
	}

	protected List<RuntimeException> parseAndGetErrors(String input, PatternManager patternManager) {
		LtlParser parser = createParser(input);
		parser.setPatternManager(patternManager);
		parser.parse();

		return getExceptions(parser);
	}



	protected String parseToString(String input, PatternManager patternManager) {
		LtlParser parser = createParser(input);
		parser.setPatternManager(patternManager);
		parser.parse();
		if (hasErrors(parser)) {
			throw getExceptions(parser).get(0);
		}

		PrologTerm term = parser.generatePrologTerm("current", parserBase);
		if (term != null) {
			return term.toString();
		} else {
			return null;
		}
	}

	protected void throwsException(PatternManager patternManager, String input, String msg) {
		try {
			parse(input, patternManager);
			Assert.fail(msg);
		} catch(RuntimeException e) {
		}
	}

	protected void throwsException(String input, PatternManager patternManager) {
		throwsException(patternManager, input, "Exception should have been thrown. (Input: \""+ input +"\")");
	}

	protected void throwsException(String input) {
		throwsException(input, null);
	}

	// Test helper classes
	public class TestErrorListener extends BaseErrorListener {

		private List<RuntimeException> exceptions = new LinkedList<RuntimeException>();

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer,
				Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e) {
			if (e == null) {
				exceptions.add(new RuntimeException(msg));
			} else {
				exceptions.add(e);
			}
		}

		public int getErrors() {
			return exceptions.size();
		}

		public List<RuntimeException> getExceptions() {
			return exceptions;
		}

	}

	public class TestWarningListener implements WarningListener {

		private int count;

		@Override
		public void warning(Token token, String message) {
			count++;
		}

		public int getCount() {
			return count;
		}

	}

}
