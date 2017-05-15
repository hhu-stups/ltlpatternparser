package de.prob.ltl.parser.prolog.scope;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class BeforeScopeReplacer extends ScopeReplacer {
	/*
		before r
		F(r) => <term>

		<term>
		G(a) : a U r
		F(a) : !r U (a & !r)
		X(a) : !r & X(a & !r)
		H(a) : H(a)
		O(a) : O(a)
		Y(a) : Y(a)

		a U b : (a & !r) U (b & !r)
		a R b : b U (r | (a & b))
		a W b : a U (r | b)
		a S b : a S b
		a T b : a T b
	 */

	@Override
	public PrologTerm scopeFormula(PrologTerm term) {
		// F(r) => <term>
		PrologTerm f = new CompoundPrologTerm("finally", getR());
		return new CompoundPrologTerm("implies", f, term);
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
		// H(a) : H(a)
		return new CompoundPrologTerm("historically", a);
	}

	@Override
	public PrologTerm once(PrologTerm a) {
		// O(a) : O(a)
		return new CompoundPrologTerm("once", a);
	}

	@Override
	public PrologTerm yesterday(PrologTerm a) {
		// Y(a) : Y(a)
		return new CompoundPrologTerm("yesterday", a);
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
		// a S b : a S b
		return new CompoundPrologTerm("since", a, b);
	}

	@Override
	public PrologTerm trigger(PrologTerm a, PrologTerm b) {
		// a T b : a T b
		return new CompoundPrologTerm("trigger", a, b);
	}

}
