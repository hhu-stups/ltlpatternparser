package de.prob.ltl.parser.semantic;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.NumVarParamContext;
import de.prob.ltl.parser.LtlParser.Pattern_defContext;
import de.prob.ltl.parser.LtlParser.Pattern_def_paramContext;
import de.prob.ltl.parser.LtlParser.SeqVarParamContext;
import de.prob.ltl.parser.LtlParser.VarParamContext;
import de.prob.ltl.parser.symboltable.SymbolTable;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;

public class PatternDefinition extends AbstractSemanticObject {

	private final SymbolTable symbolTable;

	private Pattern_defContext context;

	private String name;
	private boolean newDefinition = true;
	private List<Variable> parameters = new LinkedList<Variable>();
	private Body body;
	private boolean checked = false;

	public PatternDefinition(LtlParser parser, Pattern_defContext context) {
		super(parser);
		symbolTable = new SymbolTable(symbolTableManager.getCurrentScope());

		this.context = context;
		if (this.context != null) {
			symbolTableManager.pushScope(symbolTable);
			determineTokenAndName();
			determineParameters();
			symbolTableManager.popScope();

			// Define pattern
			if (!symbolTableManager.define(this)) {
				notifyErrorListeners("The pattern '%s' is already defined.", getName());
			}
		}
	}

	private void determineTokenAndName() {
		TerminalNode node = context.ID();
		token = node.getSymbol();
		name = node.getText();
	}

	private void determineParameters() {
		for (Pattern_def_paramContext ctx : context.pattern_def_param()) {
			Variable parameter = null;
			if (ctx instanceof NumVarParamContext) {
				parameter = createVariable(((NumVarParamContext) ctx).ID(), VariableTypes.num);
			} else if (ctx instanceof SeqVarParamContext) {
				parameter = createVariable(((SeqVarParamContext) ctx).ID(), VariableTypes.seq);
			} else if (ctx instanceof VarParamContext) {
				parameter = createVariable(((VarParamContext) ctx).ID(), VariableTypes.var);
			} else {
				// TODO error ?
			}

			defineVariable(parameter);
			parameters.add(parameter);
		}
	}

	public void checkBody() {
		if (!checked) {
			checked = true;
			symbolTableManager.pushScope(symbolTable);
			symbolTableManager.pushCall(getName());
			body = new Body(parser, context.body());
			checkUnusedVariables();
			symbolTableManager.popCall();
			symbolTableManager.popScope();
		}
	}

	public String getName() {
		return createPatternIdentifier(name, parameters);
	}

	public String getSimpleName() {
		return name;
	}

	public boolean isNewDefinition() {
		return newDefinition;
	}

	public void setNewDefinition(boolean newDefinition) {
		this.newDefinition = newDefinition;
	}

	public List<Variable> getParameters() {
		return parameters;
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public Body getBody() {
		return body;
	}

	public Pattern_defContext getContext() {
		return context;
	}

	private static void printParam(StringBuilder sb, VariableTypes type, int count) {
		sb.append(type);
		if (count > 1) {
			sb.append(count);
		}
	}

	public static String createPatternIdentifier(String name, List<Variable> parameters) {
		StringBuilder sb = new StringBuilder(name);
		sb.append('/');
		if (parameters.size() == 0) {
			sb.append(0);
		} else {
			VariableTypes lastType = parameters.get(0).getType();
			int count = 1;
			for (int i = 1; i < parameters.size(); i++) {
				VariableTypes current = parameters.get(i).getType();
				if (lastType.equals(current)) {
					count++;
				} else {
					printParam(sb, lastType, count);
					lastType = current;
					count = 1;
				}
			}
			printParam(sb, lastType, count);
		}
		return sb.toString();
	}

}
