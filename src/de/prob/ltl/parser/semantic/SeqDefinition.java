package de.prob.ltl.parser.semantic;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.SeqDefinitionContext;
import de.prob.ltl.parser.LtlParser.SeqVarExtensionContext;
import de.prob.ltl.parser.LtlParser.Seq_defContext;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;

public class SeqDefinition extends AbstractSemanticObject {

	private Seq_defContext context;

	private Variable variable;
	private List<Argument> arguments = new LinkedList<Argument>();
	private Argument withoutArgument;

	public SeqDefinition(LtlParser parser, Seq_defContext context) {
		super(parser);

		this.context = context;
		if (this.context != null) {
			checkArguments();
		}
	}

	private void checkArguments() {
		if(context instanceof SeqVarExtensionContext) {
			TerminalNode node = ((SeqVarExtensionContext) context).ID();
			token = createToken(node.getSymbol(), context.stop);

			// check ID
			variable = resolveVariable(node);
			if (variable != null) {
				variable.setWasCalled(true);
				if (!variable.getType().equals(VariableTypes.seq)) {
					notifyErrorListeners(node.getSymbol(), "The type of the variable '%s' is not allowed. Expected type: %s", variable, VariableTypes.seq);
				}
			}

			// Check without argument
			withoutArgument = new Argument(parser, ((SeqVarExtensionContext) context).argument());

			VariableTypes types[] = new VariableTypes[] { VariableTypes.var, VariableTypes.seq };
			withoutArgument.checkArgument(types);
		} else if (context instanceof SeqDefinitionContext) {
			SeqDefinitionContext ctx = (SeqDefinitionContext) context;
			token = createToken(ctx.LEFT_PAREN().getSymbol(), ctx.RIGHT_PAREN().getSymbol());

			int size = ctx.argument().size();
			if (ctx.SEQ_WITHOUT() != null) {
				size -= 1;
				// Check without argument
				withoutArgument = new Argument(parser, ctx.argument(size));

				VariableTypes types[] = new VariableTypes[] { VariableTypes.var, VariableTypes.seq };
				withoutArgument.checkArgument(types);
			}
			for (int i = 0; i < size; i++) {
				Argument argument = new Argument(parser, ctx.argument(i));
				arguments.add(argument);

				VariableTypes types[] = new VariableTypes[] { VariableTypes.var };
				argument.checkArgument(types);
			}
		} else {
			// TODO error ?
		}
	}

	public void createCopyOfArguments() {
		for (Argument argument : arguments) {
			createCopy(argument);
		}
		if (withoutArgument != null) {
			createCopy(withoutArgument);
		}
	}

	private void createCopy(Argument argument) {
		Variable var = argument.getVariable();
		if (var != null) {
			argument.setVariable(var.copy());
		}
	}

	public Variable getVariable() {
		return variable;
	}

	public List<Argument> getArguments() {
		return arguments;
	}

	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	public Argument getWithoutArgument() {
		return withoutArgument;
	}

	public void setWithoutArgument(Argument withoutArgument) {
		this.withoutArgument = withoutArgument;
	}

}
