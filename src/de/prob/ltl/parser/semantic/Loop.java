package de.prob.ltl.parser.semantic;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.ArgumentContext;
import de.prob.ltl.parser.LtlParser.LoopContext;
import de.prob.ltl.parser.LtlParser.Var_assignContext;
import de.prob.ltl.parser.LtlParser.Var_defContext;
import de.prob.ltl.parser.symboltable.SymbolTable;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;

public class Loop extends AbstractSemanticObject {

	private final SymbolTable symbolTable;

	private LoopContext context;

	private boolean isUp;
	private Variable counterVariable;
	private List<Argument> arguments = new LinkedList<Argument>();

	public Loop(LtlParser parser, LoopContext context) {
		super(parser);
		symbolTable = new SymbolTable(symbolTableManager.getCurrentScope(), true);

		this.context = context;
		if (this.context != null) {
			symbolTableManager.pushScope(symbolTable);
			checkArguments();	/* 	Check arguments before defining the counting variable
									Else it would be possible to use the counting variable as argument
									e.g:
									count i: 1 up to i:
									...
									end */
			determineLoopInfo();

			checkLoopBody();
			checkUnusedVariables();
			symbolTableManager.popScope();
		}
	}

	private void determineLoopInfo() {
		token = context.LOOP_BEGIN().getSymbol();
		isUp = (context.UP() != null);
		if (context.ID() != null) {
			counterVariable = createVariable(context.ID(), VariableTypes.num);
			defineVariable(counterVariable);
		}
	}

	private void checkArguments() {
		for (ArgumentContext arg : context.argument()) {
			Argument value = new Argument(parser, arg);
			arguments.add(value);

			VariableTypes types[] = new VariableTypes[] { VariableTypes.num };
			value.checkArgument(types);
		}
	}

	private void checkLoopBody() {
		for (ParseTree child : context.loop_body().children) {
			if (child instanceof Var_defContext) {
				addChild(new VariableDefinition(parser, (Var_defContext) child));
			} else if (child instanceof Var_assignContext) {
				addChild(new VariableAssignment(parser, (Var_assignContext) child));
			}
		}
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public boolean isUp() {
		return isUp;
	}

	public Variable getCounterVariable() {
		return counterVariable;
	}

	public List<Argument> getArguments() {
		return arguments;
	}

}
