package de.prob.ltl.parser.semantic;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.Var_defContext;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;

public class VariableDefinition extends AbstractSemanticObject {

	private Var_defContext context;

	private Variable variable;
	private Argument value;

	public VariableDefinition(LtlParser parser, Var_defContext context) {
		super(parser);

		this.context = context;
		if (this.context != null) {
			determineVariableInfo();

			checkInitialValue();
			// Define variable
			defineVariable(variable);
		}
	}

	private void determineVariableInfo() {
		VariableTypes type = VariableTypes.num;
		if (context.VAR() != null) {
			type = VariableTypes.var;
		} else if (context.SEQ_VAR() != null) {
			type = VariableTypes.seq;
		}

		variable = createVariable(context.ID(), type);
		token = variable.getToken();
	}

	private void checkInitialValue() {
		value = new Argument(parser, context.argument());

		VariableTypes type = variable.getType();
		VariableTypes types[] = new VariableTypes[] { type };

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
