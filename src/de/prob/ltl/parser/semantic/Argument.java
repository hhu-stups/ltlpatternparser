package de.prob.ltl.parser.semantic;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.ArgumentContext;
import de.prob.ltl.parser.LtlParser.ExprArgumentContext;
import de.prob.ltl.parser.LtlParser.NumArgumentContext;
import de.prob.ltl.parser.LtlParser.ParArgumentContext;
import de.prob.ltl.parser.LtlParser.SeqArgumentContext;
import de.prob.ltl.parser.LtlParser.VarArgumentContext;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;

public class Argument extends AbstractSemanticObject {

	private ArgumentContext context;

	private Variable variable;
	private BigInteger num;
	private SeqDefinition seq;
	private Expr expr;

	public Argument(LtlParser parser, ArgumentContext context) {
		super(parser);

		this.context = context;
	}

	public void checkArgument(VariableTypes[] allowedVariableTypes) {
		List<VariableTypes> types = Arrays.asList(allowedVariableTypes);
		boolean numAllowed = types.contains(VariableTypes.num);
		boolean seqDefinitionAllowed = types.contains(VariableTypes.seq);
		boolean exprAllowed = types.contains(VariableTypes.var);

		if (context instanceof VarArgumentContext) {
			token = ((VarArgumentContext) context).ID().getSymbol();
			if (allowedVariableTypes == null) {
				// Variable arguments are not allowed
				notifyErrorListeners("A variable argument is not allowed.");
			} else {
				variable = resolveVariable(((VarArgumentContext) context).ID());
				if (variable != null) {
					variable.setWasCalled(true);

					boolean typeFound = false;
					// Check if type is allowed
					for (VariableTypes type : allowedVariableTypes) {
						if (type.equals(variable.getType())) {
							typeFound = true;
							break;
						}
					}
					if (!typeFound) {
						notifyErrorListeners("The type of the variable argument '%s' is not allowed. Expected type(s): %s", variable, Arrays.toString(allowedVariableTypes));
					}
				}
			}
		} else if (context instanceof NumArgumentContext) {
			TerminalNode node = ((NumArgumentContext) context).NUM();
			token = node.getSymbol();
			if (!numAllowed) {
				// Num arguments are not allowed
				notifyErrorListeners("A num argument is not allowed.");
			} else {
				// Set value
				num = new BigInteger(node.getText());
			}
		} else if (context instanceof SeqArgumentContext) {
			// Set value
			seq = new SeqDefinition(parser, ((SeqArgumentContext) context).seq_def());
			token = seq.getToken();
			if (!seqDefinitionAllowed) {
				// Seq definition arguments are not allowed
				notifyErrorListeners("A sequence definition argument is not allowed.");
			}
		} else if (context instanceof ParArgumentContext) {
			// Check sub argument
			Argument argument = new Argument(parser, ((ParArgumentContext) context).argument());
			argument.checkArgument(allowedVariableTypes);

			token = argument.getToken();
			variable = argument.getVariable();
			num = argument.getNum();
			seq = argument.getSeq();
			expr = argument.getExpr();
		} else if (context instanceof ExprArgumentContext) {
			// Set value
			expr = new Expr(parser, ((ExprArgumentContext) context).expr());
			token = expr.getToken();
			if (!exprAllowed) {
				// Expr arguments are not allowed
				notifyErrorListeners("An expression argument is not allowed.");
			}
		} else {
			// TODO error?
		}
	}

	public VariableTypes determineType() {
		VariableTypes type = VariableTypes.var;
		if (context instanceof VarArgumentContext) {
			Variable variable = resolveVariable(((VarArgumentContext) context).ID());
			if (variable != null) {
				type = variable.getType();
			}
		} else if (context instanceof NumArgumentContext) {
			type = VariableTypes.num;
		} else if (context instanceof SeqArgumentContext) {
			type = VariableTypes.seq;
		} else if (context instanceof ParArgumentContext) {
			Argument argument = new Argument(parser, ((ParArgumentContext) context).argument());
			type = argument.determineType();
		}
		return type;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public Variable getVariable() {
		return variable;
	}

	public BigInteger getNum() {
		return num;
	}

	public SeqDefinition getSeq() {
		return seq;
	}

	public Expr getExpr() {
		return expr;
	}

}
