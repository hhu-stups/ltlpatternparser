package de.prob.ltl.parser.semantic;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.Var_assignContext;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;

public class VariableAssignment extends AbstractSemanticObject {

	private Var_assignContext context;

	private Variable variable;
	private Argument value;

	public VariableAssignment(LtlParser parser, Var_assignContext context) {
		super(parser);

		this.context = context;
		if (this.context != null) {
			determineVariableInfo();
			if (variable != null) {
				checkAssignedValue();
			}
		}
	}

	private void determineVariableInfo() {
		TerminalNode node = context.ID();
		variable = resolveVariable(node);
		token = node.getSymbol();
	}

	private void checkAssignedValue() {
		value = new Argument(parser, context.argument());

		VariableTypes type = variable.getType();
		VariableTypes types[] = new VariableTypes[] { type };
		boolean numAllowed = type.equals(VariableTypes.num);
		boolean seqDefinitionAllowed = type.equals(VariableTypes.seq);
		boolean exprAllowed = type.equals(VariableTypes.var);

		boolean temp = variable.wasCalled();
		value.checkArgument(types);
		variable.setWasCalled(temp);
	}

	public Variable getVariable() {
		return variable;
	}

	public Argument getValue() {
		return value;
	}

}
