package de.prob.ltl.parser.prolog.scope;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class BetweenScopeReplacer extends ScopeReplacer {
	/*
		between q and r
		G((q & !r & F(r) & Y(H(!q) | (!q S r))) => <term>)

		<term>
		G(a) : a U r
		F(a) : !r U (a & !r)
		X(a) : !r & X(a & !r)
		H(a) : a S (q & a)
		O(a) : !q S a
		Y(a) : !q & Y(a)

		a U b : (a & !r) U (b & !r)
		a R b : b U (r | (a & b))
		a W b : a U (r | b)
		a S b : (a & !q) S b
		a T b : b S (q & b | a & b)
	 */
	@Override
	public PrologTerm scopeFormula(PrologTerm term) {
		// G((q & !r & F(r) & Y(H(!q) | (!q S r))) => <term>)
		PrologTerm notQ = new CompoundPrologTerm("not", getQ());
		PrologTerm notR = new CompoundPrologTerm("not", getR());
		PrologTerm fR = new CompoundPrologTerm("finally", getR());
		PrologTerm h = new CompoundPrologTerm("historically", notQ);
		PrologTerm s = new CompoundPrologTerm("since", notQ, getR());
		PrologTerm yesterday = new CompoundPrologTerm("yesterday", new CompoundPrologTerm("or", h, s));
		PrologTerm and = new CompoundPrologTerm("and", getQ(), notR);
		and = new CompoundPrologTerm("and", and, fR);
		and = new CompoundPrologTerm("and", and, yesterday);

		return new CompoundPrologTerm("globally", new CompoundPrologTerm("implies", and, term));
	}

	@Override
	public PrologTerm globally(PrologTerm a) {
		// G(a) : a U r
		return new CompoundPrologTerm("until", a, getR());
	}

	@Override
	public PrologTerm finallyOp(PrologTerm a) {
		// F(a) : !r U (a & !r)
		PrologTerm notR = new CompoundPrologTerm("not", getR());
		return new CompoundPrologTerm("until", notR, new CompoundPrologTerm("and", a, notR));
	}

	@Override
	public PrologTerm next(PrologTerm a) {
		// X(a) : !r & X(a & !r)
		PrologTerm notR = new CompoundPrologTerm("not", getR());
		PrologTerm next = new CompoundPrologTerm("next", new CompoundPrologTerm("and", a, notR));
		return new CompoundPrologTerm("and", notR, next);
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
		// a U b : (a & !r) U (b & !r)
		PrologTerm notR = new CompoundPrologTerm("not", getR());
		return new CompoundPrologTerm("until", new CompoundPrologTerm("and", a, notR), new CompoundPrologTerm("and", b, notR));
	}

	@Override
	public PrologTerm weakuntil(PrologTerm a, PrologTerm b) {
		// a W b : a U (r | b)
		return new CompoundPrologTerm("until", a, new CompoundPrologTerm("or", getR(), b));
	}

	@Override
	public PrologTerm release(PrologTerm a, PrologTerm b) {
		// a R b : b U (r | (a & b))
		return new CompoundPrologTerm("until", b, new CompoundPrologTerm("or", getR(), new CompoundPrologTerm("and", a, b)));
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
