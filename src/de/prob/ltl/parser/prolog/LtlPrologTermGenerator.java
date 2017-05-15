package de.prob.ltl.parser.prolog;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.prolog.scope.ScopeReplacer;
import de.prob.ltl.parser.semantic.AbstractSemanticObject;
import de.prob.ltl.parser.semantic.Argument;
import de.prob.ltl.parser.semantic.Body;
import de.prob.ltl.parser.semantic.Expr;
import de.prob.ltl.parser.semantic.Loop;
import de.prob.ltl.parser.semantic.PatternCall;
import de.prob.ltl.parser.semantic.PatternDefinition;
import de.prob.ltl.parser.semantic.ScopeCall;
import de.prob.ltl.parser.semantic.SeqCall;
import de.prob.ltl.parser.semantic.SeqDefinition;
import de.prob.ltl.parser.semantic.VariableAssignment;
import de.prob.ltl.parser.semantic.VariableDefinition;
import de.prob.ltl.parser.symboltable.SymbolTableManager;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.VariableTypes;
import de.prob.parserbase.ProBParserBase;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class LtlPrologTermGenerator {

	private final LtlParser parser;
	private final String currentState;
	private final ProBParserBase parserBase;

	private final SymbolTableManager symbolTableManager;

	public LtlPrologTermGenerator(LtlParser parser, String currentState, ProBParserBase parserBase) {
		this.parser = parser;
		this.currentState = currentState;
		this.parserBase = parserBase;

		symbolTableManager = parser.getSymbolTableManager();
	}

	public void generatePrologTerm(Body body, IPrologTermOutput pto) {
		if (body != null) {
			for (AbstractSemanticObject object : body.getChildren()) {
				if (object instanceof VariableDefinition) {
					generateVariableDefinition((VariableDefinition) object);
				} else if (object instanceof VariableAssignment) {
					generateVariableAssignment((VariableAssignment) object);
				} else if (object instanceof Loop) {
					generateLoop((Loop) object);
				} else if (object instanceof Expr) {
					generateExpr((Expr) object, pto);
				}
			}
		}
	}

	private void generateVariableDefinition(VariableDefinition definition) {
		generateVariableArgument(definition.getVariable(), definition.getValue());
	}

	private void generateVariableAssignment(VariableAssignment assignment) {
		generateVariableArgument(assignment.getVariable(), assignment.getValue());
	}

	private void generateVariableArgument(Variable variable, Argument argument) {
		if (variable.getType().equals(VariableTypes.seq)) {
			variable.setSeqValue(argument.getSeq());
		}
		variable.setValue(generateArgument(argument));
		if (argument.getVariable() != null) {
			variable.setSeqValue(argument.getVariable().getSeqValue());
		}
		if (variable.getSeqValue() != null) {
			variable.getSeqValue().createCopyOfArguments();
		}
	}

	private void generateLoop(Loop loop) {
		symbolTableManager.pushScope(loop.getSymbolTable());

		Variable variable = loop.getCounterVariable();
		PrologTerm startTerm = generateArgument(loop.getArguments().get(0));
		PrologTerm endTerm = generateArgument(loop.getArguments().get(1));
		if (!startTerm.isNumber() || !endTerm.isNumber()) {
			if (!startTerm.isNumber()) {
				parser.notifyErrorListeners(loop.getToken(), "Start value of the loop is not a number.", null);
			}
			if (!endTerm.isNumber()) {
				parser.notifyErrorListeners(loop.getToken(), "End value of the loop is not a number.", null);
			}
		} else {
			int compare = (loop.isUp() ? -1 : 1);

			BigInteger inc = BigInteger.valueOf(-compare);
			BigInteger count = ((IntegerPrologTerm) startTerm).getValue();
			BigInteger end = ((IntegerPrologTerm) endTerm).getValue();
			while (count.compareTo(end) == compare) {
				if (variable != null) {
					variable.setValue(new IntegerPrologTerm(count));
				}

				for (AbstractSemanticObject object : loop.getChildren()) {
					if (object instanceof VariableDefinition) {
						generateVariableDefinition((VariableDefinition) object);
					} else if (object instanceof VariableAssignment) {
						generateVariableAssignment((VariableAssignment) object);
					}
				}

				count = count.add(inc);
			}
		}
		symbolTableManager.popScope();
	}

	private void generateExpr(Expr expr, IPrologTermOutput pto) {
		ParseTreeWalker.DEFAULT.walk(new ExprPrologTermGenerator(parser, this, pto, currentState, parserBase), expr.getContext());
	}

	private PrologTerm generateArgument(Argument argument) {
		PrologTerm value = null;
		if (argument.getVariable() != null) {
			value = argument.getVariable().getValue();
		} else if (argument.getNum() != null) {
			value = new IntegerPrologTerm(argument.getNum());
		} else if (argument.getSeq() != null) {
			StructuredPrologOutput epto = new StructuredPrologOutput();
			generateSeqDefinition(argument.getSeq(), epto);
			epto.fullstop();
			value = epto.getSentences().get(0);
		} else if (argument.getExpr() != null) {
			StructuredPrologOutput epto = new StructuredPrologOutput();
			generateExpr(argument.getExpr(), epto);
			epto.fullstop();
			value = epto.getSentences().get(0);
		}
		return value;
	}

	public void generatePatternCall(PatternCall call, IPrologTermOutput pto) {
		PatternDefinition definition = call.getDefinition();
		symbolTableManager.pushScope(definition.getSymbolTable());
		// set arguments
		List<Variable> parameters = definition.getParameters();
		List<Argument> arguments = call.getArguments();
		for (int i = 0; i < parameters.size(); i++) {
			Variable parameter = parameters.get(i);
			Argument argument = arguments.get(i);

			parameter.setValue(generateArgument(argument));
			if (parameter.getType().equals(VariableTypes.seq)) {
				parameter.setSeqValue(argument.getSeq());
			}
			if (argument.getVariable() != null) {
				parameter.setSeqValue(argument.getVariable().getSeqValue());
			}
		}

		generatePrologTerm(definition.getBody(), pto);
		symbolTableManager.popScope();
	}

	public void generateScopeCall(ScopeCall call, IPrologTermOutput pto) {
		List<PrologTerm> arguments = new LinkedList<PrologTerm>();
		for (Argument arg : call.getArguments()) {
			arguments.add(generateArgument(arg));
		}

		ScopeReplacer replacer = ScopeReplacer.createReplacer(call.getType(), arguments);
		replacer.generatePrologTerm(pto);
	}

	public void generateVariableCall(String name, IPrologTermOutput pto) {
		Variable variable = symbolTableManager.resolveVariable(name);
		pto.printTerm(variable.getValue());
	}

	public void generateSeqCall(SeqCall call, IPrologTermOutput pto) {
		pto.printTerm(generateArgument(call.getArgument()));
	}

	public void generateSeqDefinition(SeqDefinition definition, IPrologTermOutput pto)  {
		List<PrologTerm> arguments = new LinkedList<PrologTerm>();
		List<PrologTerm> withoutArguments = new LinkedList<PrologTerm>();

		// Generate arguments
		if (definition.getVariable() != null) {
			Variable variable = definition.getVariable();
			SeqDefinition seq = variable.getSeqValue();

			for (Argument arg : seq.getArguments()) {
				arguments.add(generateArgument(arg));
			}

			if (seq.getWithoutArgument() != null) {
				withoutArguments.add(generateArgument(seq.getWithoutArgument()));
			}
		} else {
			for (Argument arg : definition.getArguments()) {
				arguments.add(generateArgument(arg));
			}
		}
		if (definition.getWithoutArgument() != null) {
			withoutArguments.add(generateArgument(definition.getWithoutArgument()));
		}

		// Generate sequence term
		PrologTerm withoutTerm = null;
		if (withoutArguments.size() > 0) {
			withoutTerm = new CompoundPrologTerm("not", withoutArguments.get(0));
			for (int i = 1; i < withoutArguments.size(); i++) {
				withoutTerm = new CompoundPrologTerm("and", withoutTerm, new CompoundPrologTerm("not", withoutArguments.get(i)));
			}
		}

		int size = arguments.size();
		PrologTerm term = singleSeq(arguments.get(size - 2), arguments.get(size - 1), withoutTerm);
		for (int i = size - 3; i >= 0; i--) {
			term = singleSeq(arguments.get(i), term, withoutTerm);
		}

		pto.printTerm(term);
	}

	private PrologTerm singleSeq(PrologTerm a, PrologTerm b, PrologTerm w) {
		if (w == null) {
			return new CompoundPrologTerm("and", a, new CompoundPrologTerm("next", new CompoundPrologTerm("finally", b)));
		}
		PrologTerm next = new CompoundPrologTerm("next", new CompoundPrologTerm("until", w, b));
		return new CompoundPrologTerm("and", new CompoundPrologTerm("and", a, w), next);
	}

}
