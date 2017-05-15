package de.prob.ltl.parser.semantic;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.BodyContext;
import de.prob.ltl.parser.LtlParser.Pattern_defContext;
import de.prob.ltl.parser.LtlParser.Start_pattern_defContext;

public class SemanticCheck extends AbstractSemanticObject {

	private Body body;

	public SemanticCheck(LtlParser parser) {
		super(parser);
	}

	public void check(BodyContext ast) {
		if (ast != null) {
			// Collect all pattern definitions and check them
			collectAndCheckPatternDefinitions(ast);

			// Check body
			body = new Body(parser, ast);
			checkUnusedVariables();
		}
	}

	public void check(Start_pattern_defContext ast) {
		// Collect all pattern definitions and check them
		collectAndCheckPatternDefinitions(ast);
	}

	private void collectAndCheckPatternDefinitions(ParserRuleContext ast) {
		List<PatternDefinition> patternDefinitions = new LinkedList<PatternDefinition>();
		// Collect and define all pattern definitions
		for (ParseTree child : ast.children) {
			if (child instanceof Pattern_defContext) {
				patternDefinitions.add(new PatternDefinition(parser, (Pattern_defContext) child));
			}
		}
		// Check pattern definitions after all patterns are defined
		for (PatternDefinition definition : patternDefinitions) {
			definition.checkBody();
		}
	}

	public Body getBody() {
		return body;
	}

}
