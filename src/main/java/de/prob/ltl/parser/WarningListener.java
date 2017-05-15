package de.prob.ltl.parser;

import org.antlr.v4.runtime.Token;

public interface WarningListener {

	public void warning(Token token, String message);

}
