package de.prob.ltl.parser.pattern;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import de.prob.ltl.parser.WarningListener;
import de.prob.ltl.parser.semantic.PatternDefinition;
import de.prob.ltl.parser.symboltable.SymbolTableManager;

public class PatternManager extends BaseErrorListener implements WarningListener, PatternUpdateListener {

	private final String PATTERN_ID = 		"%% PATTERN";
	private final String DESCRIPTION_ID = 	"%% DESCRIPTION";
	private final String CODE_ID = 			"%% CODE";
	private final String BUILTIN_FILE =		"/builtins.ltlp";

	private List<Pattern> builtins = new LinkedList<Pattern>();
	private List<Pattern> patterns = new LinkedList<Pattern>();
	private List<String> ignorePatterns = new LinkedList<String>();
	private List<BaseErrorListener> errorListeners = new LinkedList<BaseErrorListener>();
	private List<WarningListener> warningListeners = new LinkedList<WarningListener>();
	private List<PatternUpdateListener> updateListeners = new LinkedList<PatternUpdateListener>();

	public PatternManager() {
		/*try {
			loadBuiltinPatternsFromFile(BUILTIN_FILE);
		} catch (IOException e) {
			// IGNORE
		}*/
	}

	public void addPattern(Pattern pattern) {
		addPattern(patterns, pattern);
	}

	private void addPattern(List<Pattern> patternList, Pattern pattern) {
		patternList.add(pattern);
		pattern.addErrorListener(this);
		pattern.addWarningListener(this);
		pattern.addUpdateListener(this);
		pattern.updateDefinitions(this);
		patternUpdated(pattern, null);
	}

	public void removePattern(Pattern pattern) {
		patterns.remove(pattern);
		pattern.removeErrorListener(this);
		pattern.removeWarningListener(this);
		pattern.removeUpdateListener(this);
		patternUpdated(pattern, null);
	}

	public void updatePatterns(SymbolTableManager symbolTableManager) {
		for (Pattern pattern : builtins) {
			if (!ignorePatterns.contains(pattern.getName()) && pattern.getDefinitions() != null) {
				for (PatternDefinition definition : pattern.getDefinitions()) {
					definition.setNewDefinition(false);
					symbolTableManager.define(definition);
				}
			}
		}
		for (Pattern pattern : patterns) {
			if (!ignorePatterns.contains(pattern.getName()) && pattern.getDefinitions() != null) {
				for (PatternDefinition definition : pattern.getDefinitions()) {
					definition.setNewDefinition(false);
					symbolTableManager.define(definition);
				}
			}
		}
	}

	public void loadPatternsFromFile(String filename) throws IOException {
		patterns.clear();
		loadPatternsFromFile(filename, patterns);
	}

	public void loadBuiltinPatternsFromFile(String filename) throws IOException {
		builtins.clear();
		loadPatternsFromFile(filename, builtins);
		for (Pattern pattern : builtins) {
			pattern.setBuiltin(true);
		}
	}

	private void loadPatternsFromFile(String filename, List<Pattern> patternList) throws IOException {
		BufferedReader reader = null;
		try {
			InputStream stream = getClass().getResourceAsStream(filename);
			if (stream == null) {
				stream = new FileInputStream(filename);
			}
			if (stream != null) {
				reader = new BufferedReader(new InputStreamReader(stream));

				String line = null;
				StringBuilder nameBuilder = null;
				StringBuilder descriptionBuilder = null;
				StringBuilder codeBuilder = null;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(PATTERN_ID)) {
						if (nameBuilder != null) {
							// Add last pattern
							addPattern(patternList, nameBuilder, descriptionBuilder, codeBuilder);
							nameBuilder = null;
							descriptionBuilder = null;
							codeBuilder = null;
						}
						nameBuilder = new StringBuilder();
					} else if (line.startsWith(DESCRIPTION_ID)) {
						descriptionBuilder = new StringBuilder();
					} else if (line.startsWith(CODE_ID)) {
						codeBuilder = new StringBuilder();
					} else {
						if (codeBuilder != null) {
							codeBuilder.append(line);
							codeBuilder.append('\n');
						} else if (descriptionBuilder != null) {
							descriptionBuilder.append(line);
							descriptionBuilder.append('\n');
						} else if (nameBuilder != null) {
							nameBuilder.append(line);
							nameBuilder.append('\n');
						} else {
							// IGNORE
						}
					}
				}
				addPattern(patternList, nameBuilder, descriptionBuilder, codeBuilder);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void addPattern(List<Pattern> patternList, StringBuilder nameBuilder, StringBuilder descriptionBuilder, StringBuilder codeBuilder) {
		if (nameBuilder == null || descriptionBuilder == null || codeBuilder == null) {
			return;
		}
		if (nameBuilder.length() > 0) {
			nameBuilder.setLength(nameBuilder.length() - 1);
		}
		if (descriptionBuilder.length() > 0) {
			descriptionBuilder.setLength(descriptionBuilder.length() - 1);
		}
		if (codeBuilder.length() > 0) {
			codeBuilder.setLength(codeBuilder.length() - 1);
		}

		Pattern pattern = new Pattern();
		pattern.setName(nameBuilder.toString());
		pattern.setDescription(descriptionBuilder.toString());
		pattern.setCode(codeBuilder.toString());
		addPattern(patternList, pattern);
	}

	public void savePatternsToFile(String filename) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(filename)));
			for (Pattern pattern : patterns) {
				writer.write(PATTERN_ID);
				writer.newLine();
				if (pattern.getName() != null && !pattern.getName().isEmpty()) {
					writer.write(pattern.getName());
					writer.newLine();
				}
				writer.write(DESCRIPTION_ID);
				writer.newLine();
				if (pattern.getDescription() != null && !pattern.getDescription().isEmpty()) {
					writer.write(pattern.getDescription());
					writer.newLine();
				}
				writer.write(CODE_ID);
				writer.newLine();
				if (pattern.getCode() != null) {
					writer.write(pattern.getCode());
					writer.newLine();
				}
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public Pattern getUserPattern(String name) {
		for (Pattern pattern : patterns) {
			if (pattern.getName().equals(name)) {
				return pattern;
			}
		}
		return null;
	}

	public boolean patternExists(String name) {
		for (Pattern pattern : patterns) {
			if (pattern.getName().equals(name)) {
				return true;
			}
		}
		for (Pattern pattern : builtins) {
			if (pattern.getName().equals(name)) {
				return true;
			}
		}
		return false;
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

	public List<Pattern> getBuiltins() {
		return builtins;
	}

	public List<Pattern> getPatterns() {
		return patterns;
	}

	public List<String> getIgnorePatterns() {
		return ignorePatterns;
	}

	public void addIgnorePattern(String ignorePattern) {
		ignorePatterns.add(ignorePattern);
	}

	public void clearIgnorePatterns() {
		ignorePatterns.clear();
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
			Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		for (BaseErrorListener listener: errorListeners) {
			listener.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
		}
	}

	@Override
	public void warning(Token token, String message) {
		for (WarningListener listener: warningListeners) {
			listener.warning(token, message);
		}
	}

	@Override
	public void patternUpdated(Pattern pattern, PatternManager patternManager) {
		pattern.updateDefinitions(this);
		for (PatternUpdateListener listener: updateListeners) {
			listener.patternUpdated(pattern, this);
		}
	}

}
