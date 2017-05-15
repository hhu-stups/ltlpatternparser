package de.prob.ltl.parser.semantic;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.prob.ltl.parser.LtlBlockingListener;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;
import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.ExprContext;
import de.prob.ltl.parser.LtlParser.Pattern_callContext;
import de.prob.ltl.parser.LtlParser.Scope_callContext;
import de.prob.ltl.parser.LtlParser.SeqCallAtomContext;
import de.prob.ltl.parser.LtlParser.VariableCallAtomContext;

public class Expr extends AbstractSemanticObject {

	private ExprContext context;

	public Expr(LtlParser parser, ExprContext context) {
		super(parser);

		this.context = context;
		if (this.context != null) {
			checkContext();
		}
	}

	private void checkContext() {
		token = createToken(context.start, context.stop);
		ParseTreeWalker.DEFAULT.walk(new LtlBlockingListener() {

			@Override
			public void enterVariableCallAtom(VariableCallAtomContext ctx) {
				if (enterContext(ctx)) {
					Variable variable = resolveVariable(ctx.ID());
					if (variable != null) {
						variable.setWasCalled(true);
						// Only type 'var' is allowed here , because it is the variable call for exprs
						if (!variable.getType().equals(VariableTypes.var)) {
							notifyErrorListeners(variable.getToken(), "The type of the variable '%s' is not allowed. Expected type: %s", variable, VariableTypes.var);
						}
					}
				}
			}

			@Override
			public void enterPattern_call(Pattern_callContext ctx) {
				if (enterContext(ctx)) {
					new PatternCall(parser, ctx);
				}
			}

			@Override
			public void enterScope_call(Scope_callContext ctx) {
				if (enterContext(ctx)) {
					new ScopeCall(parser, ctx);
				}
			}

			@Override
			public void enterSeqCallAtom(SeqCallAtomContext ctx) {
				if (enterContext(ctx)) {
					new SeqCall(parser, ctx.seq_call());
				}
			}

		}, context);
	}

	public ExprContext getContext() {
		return context;
	}

}
