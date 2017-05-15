package de.prob.ltl.parser.pattern;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;

import de.prob.ltl.parser.WarningListener;
import de.prob.ltl.parser.semantic.PatternDefinition;
import de.prob.ltl.parser.LtlParser;


public class Pattern {

	private String name;
	private boolean builtin;
	private String description;
	private String code;
	private List<PatternDefinition> definitions;
	private List<BaseErrorListener> errorListeners = new LinkedList<BaseErrorListener>();
	private List<WarningListener> warningListeners = new LinkedList<WarningListener>();
	private List<PatternUpdateListener> updateListeners = new LinkedList<PatternUpdateListener>();

	public void updateDefinitions(PatternManager patternManager) {
		if (code != null) {
			if (definitions == null) {
				LtlParser parser = new LtlParser(code);
				parser.setPatternManager(patternManager);
				parser.removeErrorListeners();
				for (BaseErrorListener listener : errorListeners) {
					parser.addErrorListener(listener);
				}
				for (WarningListener listener : warningListeners) {
					parser.addWarningListener(listener);
				}

				parser.parsePatternDefinition();

				definitions = parser.getSymbolTableManager().getPatternDefinitions();
				checkPatternDefinitionNames(parser);
			}
		}
	}

	private void checkPatternDefinitionNames(LtlParser parser) {
		if (definitions != null && definitions.size() > 1) {
			String name = definitions.get(0).getSimpleName();
			boolean error = false;
			for (PatternDefinition definition : definitions) {
				if (!name.equals(definition.getSimpleName())) {
					error = true;
					break;
				}
			}
			if (error) {
				for (PatternDefinition definition : definitions) {
					parser.notifyWarningListeners(definition.getToken(), "Different pattern names in a single pattern definition.");
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBuiltin() {
		return builtin;
	}

	public void setBuiltin(boolean builtin) {
		this.builtin = builtin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		definitions = null;
		notifyUpdateListeners();
	}

	public List<PatternDefinition> getDefinitions() {
		return definitions;
	}

	public void addErrorListener(BaseErrorListener listener) {
		errorListeners.add(listener);
	}

	public void removeErrorListener(BaseErrorListener listener) {
		errorListeners.remove(listener);
	}

	public void removeErrorListeners() {
		errorListeners.clear();
	}

	public void addWarningListener(WarningListener listener) {
		warningListeners.add(listener);
	}

	public void removeWarningListener(WarningListener listener) {
		warningListeners.remove(listener);
	}

	public void removeWarningListeners() {
		warningListeners.clear();
	}

	public void addUpdateListener(PatternUpdateListener listener) {
		updateListeners.add(listener);
	}

	public void removeUpdateListener(PatternUpdateListener listener) {
		updateListeners.remove(listener);
	}

	public void removeUpdateListeners() {
		updateListeners.clear();
	}

	public void notifyUpdateListeners() {
		for (PatternUpdateListener listener: updateListeners) {
			listener.patternUpdated(this, null);
		}
	}

}
