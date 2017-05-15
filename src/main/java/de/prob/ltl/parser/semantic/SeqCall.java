package de.prob.ltl.parser.semantic;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.Seq_callContext;
import de.prob.ltl.parser.symboltable.VariableTypes;

public class SeqCall extends AbstractSemanticObject {

	private Seq_callContext context;

	private Argument argument;

	public SeqCall(LtlParser parser, Seq_callContext context) {
		super(parser);

		this.context = context;
		if (this.context != null) {
			checkArguments();
		}
	}

	private void checkArguments() {
		token = context.SEQ_VAR().getSymbol();
		argument = new Argument(parser, context.argument());
		argument.checkArgument(new VariableTypes[] { VariableTypes.seq });
	}

	public Argument getArgument() {
		return argument;
	}

}
