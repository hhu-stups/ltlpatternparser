package de.prob.ltl.parser.prolog.scope;

import java.util.List;

import de.prob.ltl.parser.symboltable.ScopeTypes;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public abstract class ScopeReplacer {

	private PrologTerm q;
	private PrologTerm r;
	private PrologTerm p;

	public static ScopeReplacer createReplacer(ScopeTypes type, List<PrologTerm> arguments) {
		ScopeReplacer replacer = null;

		// Determine call and starting (q) and ending (r) state/event
		if (type.equals(ScopeTypes.BEFORE)) {
			replacer = new BeforeScopeReplacer();
			replacer.r = arguments.get(0);
			replacer.p = arguments.get(1);
		} else if (type.equals(ScopeTypes.AFTER)) {
			replacer = new AfterScopeReplacer();
			replacer.q = arguments.get(0);
			replacer.p = arguments.get(1);
		} else {
			if (type.equals(ScopeTypes.BETWEEN)) {
				replacer = new BetweenScopeReplacer();
			} else {
				replacer = new AfterUntilScopeReplacer();
			}
			replacer.q = arguments.get(0);
			replacer.r = arguments.get(1);
			replacer.p = arguments.get(2);
		}

		return replacer;
	}

	public void generatePrologTerm(IPrologTermOutput pto) {
		pto.printTerm(scopeFormula(replace(p)));
	}

	private PrologTerm replace(PrologTerm term) {
		String functor = term.getFunctor();
		if (term.getArity() == 0 || functor.equals("ap") || functor.equals("action")) {
			// Nothing to replace
			return term;
		}

		PrologTerm result = null;
		PrologTerm arg1 = replace(term.getArgument(1));
		if (term.getArity() == 1) {
			if (functor.equals("globally")) {
				result = globally(arg1);
			} else if (functor.equals("finally")) {
				result = finallyOp(arg1);
			} else if (functor.equals("next")) {
				result = next(arg1);
			} else if (functor.equals("historically")) {
				result = historically(arg1);
			} else if (functor.equals("once")) {
				result = once(arg1);
			} else if (functor.equals("yesterday")) {
				result = yesterday(arg1);
			} else {
				// DEFAULT: Just replace the argument
				result = new CompoundPrologTerm(functor, arg1);
			}
		} else if (term.getArity() == 2) {
			PrologTerm arg2 = replace(term.getArgument(2));

			if (functor.equals("until")) {
				result = until(arg1, arg2);
			} else if (functor.equals("weakuntil")) {
				result = weakuntil(arg1, arg2);
			} else if (functor.equals("release")) {
				result = release(arg1, arg2);
			} else if (functor.equals("since")) {
				result = since(arg1, arg2);
			} else if (functor.equals("trigger")) {
				result = trigger(arg1, arg2);
			} else {
				// DEFAULT: Just replace the arguments
				result = new CompoundPrologTerm(functor, arg1, arg2);
			}
		}

		return result;
	}

	public PrologTerm getQ() {
		return q;
	}

	public PrologTerm getR() {
		return r;
	}

	public abstract PrologTerm scopeFormula(PrologTerm term);

	public abstract PrologTerm globally(PrologTerm a);
	public abstract PrologTerm finallyOp(PrologTerm a);
	public abstract PrologTerm next(PrologTerm a);
	public abstract PrologTerm historically(PrologTerm a);
	public abstract PrologTerm once(PrologTerm a);
	public abstract PrologTerm yesterday(PrologTerm a);

	public abstract PrologTerm until(PrologTerm a, PrologTerm b);
	public abstract PrologTerm weakuntil(PrologTerm a, PrologTerm b);
	public abstract PrologTerm release(PrologTerm a, PrologTerm b);
	public abstract PrologTerm since(PrologTerm a, PrologTerm b);
	public abstract PrologTerm trigger(PrologTerm a, PrologTerm b);

}
