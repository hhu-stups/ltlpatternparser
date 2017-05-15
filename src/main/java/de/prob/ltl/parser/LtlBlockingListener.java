package de.prob.ltl.parser;

import org.antlr.v4.runtime.ParserRuleContext;

public class LtlBlockingListener extends LtlBaseListener {

	protected ParserRuleContext blockingContext = null;

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		if (blockingContext != null && ctx.equals(blockingContext)) {
			blockingContext = null;
		}
	}

	protected boolean enterContext(ParserRuleContext ctx) {
		if (blockingContext == null) {
			blockingContext = ctx;
			return true;
		}
		return false;
	}

}
