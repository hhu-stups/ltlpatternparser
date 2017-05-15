package de.prob.ltl.parser.symboltable;

import org.antlr.v4.runtime.Token;

import de.prob.ltl.parser.semantic.SeqDefinition;
import de.prob.prolog.term.PrologTerm;


public class Variable {

	private String name;
	private VariableTypes type;
	private boolean wasCalled;

	private PrologTerm value;
	private SeqDefinition seqValue;

	private Token token;

	public Variable(String name, VariableTypes type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public VariableTypes getType() {
		return type;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public PrologTerm getValue() {
		return value;
	}

	public void setValue(PrologTerm value) {
		this.value = value;
	}

	public SeqDefinition getSeqValue() {
		return seqValue;
	}

	public void setSeqValue(SeqDefinition seqValue) {
		this.seqValue = seqValue;
	}

	public void setWasCalled(boolean wasCalled) {
		this.wasCalled = wasCalled;
	}

	public boolean wasCalled() {
		return wasCalled;
	}

	public Variable copy() {
		Variable copy = new Variable(getName(), getType());
		copy.seqValue = seqValue;
		copy.token = token;
		copy.value = value;
		return copy;
	}

	@Override
	public String toString() {
		return String.format("%s:%s", name, type);
	}

}
