package de.prob.ltl.parser.semantic;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.ArgumentContext;
import de.prob.ltl.parser.LtlParser.Scope_callContext;
import de.prob.ltl.parser.symboltable.ScopeTypes;
import de.prob.ltl.parser.symboltable.VariableTypes;

public class ScopeCall extends AbstractSemanticObject {

	private Scope_callContext context;

	private ScopeTypes type;
	private List<Argument> arguments = new LinkedList<Argument>();

	public ScopeCall(LtlParser parser, Scope_callContext context) {
		super(parser);

		this.context = context;
		if (this.context != null) {
			determineTokenAndType();
			checkArguments();
		}
	}

	private void determineTokenAndType() {
		TerminalNode node = null;
		if (context.BEFORE_SCOPE() != null) {
			node = context.BEFORE_SCOPE();
			type = ScopeTypes.BEFORE;
		} else if (context.AFTER_SCOPE() != null) {
			node = context.AFTER_SCOPE();
			type = ScopeTypes.AFTER;
		} else if (context.BETWEEN_SCOPE() != null) {
			node = context.BETWEEN_SCOPE();
			type = ScopeTypes.BETWEEN;
		} else {
			node = context.UNTIL_SCOPE();
			type = ScopeTypes.AFTER_UNTIL;
		}

		token = node.getSymbol();
	}

	private void checkArguments() {
		for (ArgumentContext arg : context.argument()) {
			Argument argument = new Argument(parser, arg);
			argument.checkArgument(new VariableTypes[] { VariableTypes.var });
			arguments.add(argument);
		}
		if (type.equals(ScopeTypes.BEFORE) || type.equals(ScopeTypes.AFTER) ) {
			if (arguments.size() != 2) {
				notifyErrorListeners("Wrong argument count. Expected 2 arguments.");
			}
		} else {
			if (arguments.size() != 3) {
				notifyErrorListeners("Wrong argument count. Expected 3 arguments.");
			}
		}
	}

	public ScopeTypes getType() {
		return type;
	}

	public List<Argument> getArguments() {
		return arguments;
	}

}
