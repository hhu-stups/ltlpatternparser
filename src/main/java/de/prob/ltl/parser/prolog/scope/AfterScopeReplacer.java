package de.prob.ltl.parser.prolog.scope;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class AfterScopeReplacer extends ScopeReplacer {
	/*
		after q
		G(!q) | !q U (q & <term>)

		<term>
		G(a) : G(a)
		F(a) : F(a)
		X(a) : X(a)
		H(a) : a S (q & a)
		O(a) : !q S a
		Y(a) : !q & Y(a)

		a U b : a U b
		a R b : a R b
		a W b : a W b
		a S b : (a & !q) S b
		a T b : b S (q & b | a & b)
	 */
	@Override
	public PrologTerm scopeFormula(PrologTerm term) {
		// G(!q) | !q U (q & <term>)
		PrologTerm notQ = new CompoundPrologTerm("not", getQ());
		PrologTerm never = new CompoundPrologTerm("globally", notQ);
		PrologTerm until = new CompoundPrologTerm("until", notQ, new CompoundPrologTerm("and", getQ(), term));
		return new CompoundPrologTerm("or", never, until);
	}

	@Override
	public PrologTerm globally(PrologTerm a) {
		// G(a) : G(a)
		return new CompoundPrologTerm("globally", a);
	}

	@Override
	public PrologTerm finallyOp(PrologTerm a) {
		// F(a) : F(a)
		return new CompoundPrologTerm("finally", a);
	}

	@Override
	public PrologTerm next(PrologTerm a) {
		// X(a) : X(a)
		return new CompoundPrologTerm("next", a);
	}

	@Override
	public PrologTerm historically(PrologTerm a) {
		// H(a) : a S (q & a)
		return new CompoundPrologTerm("since", a, new CompoundPrologTerm("and", getQ(), a));
	}

	@Override
	public PrologTerm once(PrologTerm a) {
		// O(a) : !q S a
		PrologTerm notQ = new CompoundPrologTerm("not", getQ());
		return new CompoundPrologTerm("since", notQ, a);
	}

	@Override
	public PrologTerm yesterday(PrologTerm a) {
		// Y(a) : !q & Y(a)
		PrologTerm notQ = new CompoundPrologTerm("not", getQ());
		return new CompoundPrologTerm("and", notQ, new CompoundPrologTerm("yesterday", a));
	}

	@Override
	public PrologTerm until(PrologTerm a, PrologTerm b) {
		// a U b : a U b
		return new CompoundPrologTerm("until", a, b);
	}

	@Override
	public PrologTerm weakuntil(PrologTerm a, PrologTerm b) {
		// a W b : a W b
		return new CompoundPrologTerm("weakuntil", a, b);
	}

	@Override
	public PrologTerm release(PrologTerm a, PrologTerm b) {
		// a R b : a R b
		return new CompoundPrologTerm("release", a, b);
	}

	@Override
	public PrologTerm since(PrologTerm a, PrologTerm b) {
		// a S b : (a & !q) S b
		PrologTerm notQ = new CompoundPrologTerm("not", getQ());
		return new CompoundPrologTerm("since", new CompoundPrologTerm("and", a, notQ), b);
	}

	@Override
	public PrologTerm trigger(PrologTerm a, PrologTerm b) {
		// a T b : b S (q & b | a & b)
		PrologTerm qAndB = new CompoundPrologTerm("and", getQ(), b);
		PrologTerm aAndB = new CompoundPrologTerm("and", a, b);
		return new CompoundPrologTerm("since", b, new CompoundPrologTerm("or", qAndB, aAndB));
	}

}
