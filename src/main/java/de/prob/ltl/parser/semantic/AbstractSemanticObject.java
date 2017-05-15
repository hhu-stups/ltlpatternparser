package de.prob.ltl.parser.semantic;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.symboltable.SymbolTableManager;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;

public abstract class AbstractSemanticObject {

	protected LtlParser parser;
	protected SymbolTableManager symbolTableManager;

	protected Token token;
	private List<AbstractSemanticObject> children = new LinkedList<AbstractSemanticObject>();

	public AbstractSemanticObject(LtlParser parser) {
		this.parser = parser;
		symbolTableManager = this.parser.getSymbolTableManager();
	}

	public Variable createVariable(TerminalNode node, VariableTypes type) {
		String varName = node.getText();
		Variable var = new Variable(varName, type);
		var.setToken(node.getSymbol());
		return var;
	}

	public boolean defineVariable(Variable var) {
		if (!symbolTableManager.define(var)) {
			notifyErrorListeners(var.getToken(), "The variable '%s' is already defined in this scope.", var);
			return false;
		}
		return true;
	}

	public Variable resolveVariable(TerminalNode node) {
		Variable variable = symbolTableManager.resolveVariable(node.getText());
		if (variable == null) {
			notifyErrorListeners(node.getSymbol(), "Variable '%s' cannot be resolved.", node.getText());
		}
		return variable;
	}

	public void notifyErrorListeners(String format, Object ... args) {
		notifyErrorListeners(token, format, args);
	}

	public void notifyErrorListeners(Token t, String format, Object ... args) {
		if (t != null) {
			parser.notifyErrorListeners(t, String.format(format, args), null);
		} else {
			parser.notifyErrorListeners(String.format(format, args));
		}
	}

	public void notifyWarningListeners(String format, Object ... args) {
		notifyWarningListeners(token, format, args);
	}

	public void notifyWarningListeners(Token t, String format, Object ... args) {
		parser.notifyWarningListeners(t, String.format(format, args));
	}

	public void checkUnusedVariables() {
		for (Variable var : symbolTableManager.getUnusedVariables()) {
			notifyWarningListeners(var.getToken(), "Unused variable '%s'.", var);
		}
	}

	public static Token createToken(Token start, Token stop) {
		CommonToken newToken = new CommonToken(start);
		newToken.setStopIndex(stop.getStopIndex());

		return newToken;
	}

	public void addChild(AbstractSemanticObject child) {
		children.add(child);
	}

	public LtlParser getParser() {
		return parser;
	}

	public SymbolTableManager getSymbolTableManager() {
		return symbolTableManager;
	}

	public Token getToken() {
		return token;
	}

	public List<AbstractSemanticObject> getChildren() {
		return children;
	}

}
