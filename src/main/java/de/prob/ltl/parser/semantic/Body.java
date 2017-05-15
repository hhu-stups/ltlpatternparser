package de.prob.ltl.parser.semantic;

import org.antlr.v4.runtime.tree.ParseTree;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.BodyContext;
import de.prob.ltl.parser.LtlParser.LoopContext;
import de.prob.ltl.parser.LtlParser.Pattern_defContext;
import de.prob.ltl.parser.LtlParser.Var_assignContext;
import de.prob.ltl.parser.LtlParser.Var_defContext;

public class Body extends AbstractSemanticObject {

	private BodyContext context;

	public Body(LtlParser parser, BodyContext context) {
		super(parser);

		this.context = context;
		if (this.context != null) {
			checkContext();
		}
	}

	private void checkContext() {
		for (ParseTree child : context.children) {
			if (child instanceof Pattern_defContext) {
				if (symbolTableManager.getCurrentScope() != symbolTableManager.getGlobalScope()) {
					// Pattern definitions in other other scope than the global scope are not allowed
					notifyErrorListeners(((Pattern_defContext) child).ID().getSymbol(), "Pattern definition in wrong scope. Definitions are only allowed in global scope.");
				}
			} else if (child instanceof Var_defContext) {
				addChild(new VariableDefinition(parser, (Var_defContext) child));
			} else if (child instanceof Var_assignContext) {
				addChild(new VariableAssignment(parser, (Var_assignContext) child));
			} else if (child instanceof LoopContext) {
				addChild(new Loop(parser, (LoopContext) child));
			}
		}
		// Check final expr
		addChild(new Expr(parser, context.expr()));
	}

}
