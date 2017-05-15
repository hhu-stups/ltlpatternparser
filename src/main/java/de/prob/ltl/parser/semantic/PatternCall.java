package de.prob.ltl.parser.semantic;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.ArgumentContext;
import de.prob.ltl.parser.LtlParser.Pattern_callContext;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;

public class PatternCall extends AbstractSemanticObject {

	private Pattern_callContext context;

	private PatternDefinition definition;
	private String name;
	private String identifier;
	private List<Argument> arguments = new LinkedList<Argument>();

	public PatternCall(LtlParser parser, Pattern_callContext context) {
		super(parser);

		this.context = context;
		if (this.context != null) {
			determineTokenAndName();
			determineArguments();
			determineIdentifier();

			definition = symbolTableManager.resolvePattern(identifier);
			if (definition == null) {
				notifyErrorListeners("Pattern '%s' cannot be resolved.", identifier);
			} else {
				checkArguments();
				if (symbolTableManager.onCallStack(identifier)) {
					notifyErrorListeners(definition.getToken(), "Cycle detected in pattern '%s'.", identifier);
				} else {
					definition.checkBody();
				}
			}
		}
	}

	private void determineTokenAndName() {
		TerminalNode node = context.ID();
		token = node.getSymbol();
		name = node.getText();
	}

	private void determineArguments() {
		for (ArgumentContext arg : context.argument()) {
			Argument argument = new Argument(parser, arg);
			arguments.add(argument);
		}
	}

	private void determineIdentifier() {
		List<Variable> vars = new LinkedList<Variable>();

		for (Argument arg : arguments) {
			vars.add(new Variable(null, arg.determineType()));
		}

		identifier = PatternDefinition.createPatternIdentifier(name, vars);
	}

	private void checkArguments() {
		List<Variable> parameters = definition.getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			Variable parameter = parameters.get(i);
			Argument argument = arguments.get(i);

			VariableTypes type = parameter.getType();
			VariableTypes types[] = new VariableTypes[] { type };
			argument.checkArgument(types);
		}
	}

	public PatternDefinition getDefinition() {
		return definition;
	}

	public List<Argument> getArguments() {
		return arguments;
	}

}
