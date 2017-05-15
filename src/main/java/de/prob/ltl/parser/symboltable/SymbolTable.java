package de.prob.ltl.parser.symboltable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.prob.ltl.parser.semantic.PatternDefinition;

public class SymbolTable {

	private SymbolTable parent;
	private boolean parentLookup;
	private Map<String, Variable> variables = new HashMap<String, Variable>();
	private Map<String, PatternDefinition> patternDefinitions;

	/**
	 * Creates a symbol table with a parent, so that patterns are defined in the root parent
	 * 
	 * @param parent default is null
	 */
	public SymbolTable(SymbolTable parent) {
		this(parent, false);
	}

	/**
	 * Creates a symbol table with a parent, so that patterns are defined in the root parent
	 * 
	 * @param parent default is null
	 * @param parentLookup true to do variable lookups also in the parent scope; default is false
	 */
	public SymbolTable(SymbolTable parent, boolean parentLookup) {
		this.parent = parent;
		this.parentLookup = parentLookup;

		if (this.parent == null) {
			// Only needed in root symbol table
			patternDefinitions = new HashMap<String, PatternDefinition>();
		}
	}

	/**
	 * Defines a variable
	 * 
	 * @param var
	 * @return false, if a variable with the same name was already defined; otherwise true
	 */
	public boolean define(Variable var) {
		if (isDefinedVariable(var.getName())) {
			return false;
		}
		variables.put(var.getName(), var);
		return true;
	}

	/**
	 * Defines a pattern
	 * 
	 * @param pattern
	 * @return false, if a pattern with the same name was already defined
	 * or this symbol table is not the root; otherwise true
	 */
	public boolean define(PatternDefinition pattern) {
		if (parent == null) {
			if (isDefinedPattern(pattern.getName())) {
				return false;
			}
			patternDefinitions.put(pattern.getName(), pattern);
			return true;
		}
		return false;
	}

	/**
	 * Resolves a defined variable by name
	 * 
	 * @param name
	 * @return Variable with this name; null, if no variable was found with this name
	 */
	public Variable resolveVariable(String name) {
		Variable var = variables.get(name);
		if (parentLookup && var == null && parent != null) {
			var = parent.resolveVariable(name);
		}
		return var;
	}

	/**
	 * Resolves a defined pattern by name
	 * 
	 * @param name
	 * @return Pattern with this name; null, if no pattern was found with this name
	 */
	public PatternDefinition resolvePattern(String name) {
		if (parent == null) {
			return patternDefinitions.get(name);
		}
		return parent.resolvePattern(name);
	}

	/**
	 * Checks whether a variable with the given name was defined
	 * 
	 * @param name
	 * @return true, if variable with the given name was defined; otherwise false
	 */
	public boolean isDefinedVariable(String name) {
		return resolveVariable(name) != null;
	}

	/**
	 * Checks whether a pattern with the given name was defined
	 * 
	 * @param name
	 * @return true, if pattern with the given name was defined; otherwise false
	 */
	public boolean isDefinedPattern(String name) {
		return resolvePattern(name) != null;
	}

	public List<Variable> getUnusedVariables() {
		List<Variable> unused = new LinkedList<Variable>();

		for (Variable var : variables.values()) {
			if (!var.wasCalled()) {
				unused.add(var);
			}
		}

		return unused;
	}

	public List<PatternDefinition> getPatternDefinitions() {
		List<PatternDefinition> result = new LinkedList<PatternDefinition>();
		for (PatternDefinition definition : patternDefinitions.values()) {
			if (definition.isNewDefinition()) {
				result.add(definition);
			}
		}
		return result;
	}

	public List<PatternDefinition> getAllPatternDefinitions() {
		return new LinkedList<PatternDefinition>(patternDefinitions.values());
	}

}
