grammar Ltl;

/* -- Additional code -- */
@lexer::header {
package de.prob.ltl.parser;
}

@parser::header {
package de.prob.ltl.parser;

import de.prob.ltl.parser.prolog.LtlPrologTermGenerator;
import de.prob.ltl.parser.pattern.PatternManager;
import de.prob.ltl.parser.semantic.SemanticCheck;
import de.prob.ltl.parser.symboltable.SymbolTableManager;
import de.prob.parserbase.ProBParserBase;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.PrologTerm;
}

@parser::members {
private SymbolTableManager symbolTableManager = new SymbolTableManager();
private PatternManager patternManager;
private List<WarningListener> warningListeners = new ArrayList<WarningListener>();
private LtlLexer lexer;
private SemanticCheck semanticCheck;
private ParseTree lastAst;

public LtlParser(String input) {
	this(new CommonTokenStream(new LtlLexer(new ANTLRInputStream(input))));
	if (getTokenStream().getTokenSource() instanceof LtlLexer) {
		lexer = (LtlLexer) getTokenStream().getTokenSource();
	}
}

public void parse() {
	if (patternManager != null) {
		patternManager.updatePatterns(symbolTableManager);
	}
	StartContext ast = start();
	lastAst = ast;
	
	semanticCheck = new SemanticCheck(this);
	semanticCheck.check(ast.body());
}

public void parsePatternDefinition() {
	if (patternManager != null) {
		patternManager.updatePatterns(symbolTableManager);
	}
	Start_pattern_defContext ast = start_pattern_def();
	lastAst = ast;

	semanticCheck = new SemanticCheck(this);
	semanticCheck.check(ast);
}

public PrologTerm generatePrologTerm(String currentState, ProBParserBase parserBase) {
	StructuredPrologOutput pto = new StructuredPrologOutput();
	LtlPrologTermGenerator generator = new LtlPrologTermGenerator(this, currentState, parserBase);

	if (semanticCheck.getBody() != null) {
		generator.generatePrologTerm(semanticCheck.getBody(), pto);
		pto.fullstop();
		return pto.getSentences().get(0);
	} else {
		return null;
	}
}

public SymbolTableManager getSymbolTableManager() {
	return symbolTableManager;
}

public void setSymbolTableManager(SymbolTableManager symbolTableManager) {
	this.symbolTableManager = symbolTableManager;
}

public PatternManager getPatternManager() {
	return patternManager;
}

public void setPatternManager(PatternManager patternManager) {
	this.patternManager = patternManager;
}

public LtlLexer getLexer() {
	return lexer;
}

public ParseTree getAst() {
	return lastAst;
}

@Override
public void addErrorListener(@NotNull ANTLRErrorListener listener) {
	super.addErrorListener(listener);
	lexer.addErrorListener(listener);
}

@Override
public void removeErrorListener(@NotNull ANTLRErrorListener listener) {
	super.removeErrorListener(listener);
	lexer.removeErrorListener(listener);
}

@Override
public void removeErrorListeners() {
	super.removeErrorListeners();
	lexer.removeErrorListeners();
}

public void addWarningListener(WarningListener listener) {
	warningListeners.add(listener);
}

public List<WarningListener> getWarningListeners() {
	return warningListeners;
}

public void removeWarningListener(WarningListener listener) {
	warningListeners.remove(listener);
}

public void removeWarningListeners() {
	warningListeners.clear();
}

public void notifyWarningListeners(Token token, String message) {
	for (WarningListener listener : warningListeners) {
		listener.warning(token, message);
	}
}
}

/* -- Starting rule -- */
start
 : body? EOF
 ;
 
start_pattern_def
 : (pattern_def)* EOF
 ;

/* --- Common rules --- */
body
 : (pattern_def | var_def | var_assign | loop)* expr
 ;
 
argument
 : ID								# varArgument
 | NUM								# numArgument
 | seq_def							# seqArgument
 | LEFT_PAREN argument RIGHT_PAREN	# parArgument
 | expr								# exprArgument
 ;

/* --- Pattern rules --- */
pattern_def
 : PATTERN_DEF ID LEFT_PAREN (pattern_def_param (',' pattern_def_param)*)? RIGHT_PAREN ':' body
 ;
 
pattern_def_param
 : ID 				# varParam
 | ID ':' NUM_VAR	# numVarParam
 | ID ':' SEQ_VAR	# seqVarParam
 ;

pattern_call
 : ID LEFT_PAREN (argument (',' argument)*)? RIGHT_PAREN
 ;
 
/* --- Scope call rule --- */
scope_call
 : (BEFORE_SCOPE | AFTER_SCOPE | BETWEEN_SCOPE | UNTIL_SCOPE) LEFT_PAREN argument ',' argument (',' argument)? RIGHT_PAREN
 ;
    
/* --- Variable rules --- */
var_def
 : (VAR | NUM_VAR | SEQ_VAR) ID ':' argument
 ;
 
var_assign
 : ID ':' argument
 ;
 
/* --- Sequence rules --- */
seq_def
 : LEFT_PAREN argument (',' argument)+ (SEQ_WITHOUT argument)? RIGHT_PAREN	# seqDefinition
 | ID SEQ_WITHOUT argument													# seqVarExtension
 ;
 
seq_call
 : SEQ_VAR LEFT_PAREN argument RIGHT_PAREN
 ;
 
/* --- Loop rules --- */
loop
 : LOOP_BEGIN (ID ':')? argument (UP | DOWN) TO argument ':' loop_body LOOP_END
 ;
 
loop_body
 : (var_def | var_assign)+
 ;
  
/* --- Ltl and boolean expr --- */
expr
 : NOT expr						# notExpr
 | GLOBALLY expr				# globallyExpr
 | FINALLY expr					# finallyExpr
 | NEXT expr					# nextExpr
 | HISTORICALLY expr			# historicallyExpr
 | ONCE expr					# onceExpr
 | YESTERDAY expr				# yesterdayExpr
 | UNARY_COMBINED expr			# unaryCombinedExpr
 | expr (UNTIL 
		| WEAKUNTIL
		| RELEASE
		| SINCE
		| TRIGGER
		)expr					# unaryLtlExpr
 | expr AND expr				# andExpr
 | expr OR expr					# orExpr
 | expr IMPLIES expr			# impliesExpr
 | atom							# atomExpr
 ;
 
atom
 : ID							# variableCallAtom	// Only type 'var' allowed
 | pattern_call					# patternCallAtom	
 | scope_call					# scopeCallAtom
 | seq_call						# seqCallAtom
 | PREDICATE					# predicateAtom
 | ACTION						# actionAtom
 | ENABLED						# enabledAtom
 | LEFT_PAREN expr RIGHT_PAREN	# parAtom
 | (TRUE | FALSE) 				# booleanAtom
 | (SINK | DEADLOCK | CURRENT)	# stateAtom
 ;

/* -- Token -- */

// Constants
TRUE			: 'true';
FALSE			: 'false';
SINK			: 'sink';
DEADLOCK		: 'deadlock';
CURRENT			: 'current';

// Unary Ltl operators
GLOBALLY		: 'G';
FINALLY			: 'F';
NEXT			: 'X';
HISTORICALLY	: 'H';
ONCE			: 'O';
YESTERDAY		: 'Y';
UNARY_COMBINED 	: [GFXHOY]+;

// Binary Ltl operators
UNTIL			: 'U';
WEAKUNTIL		: 'W';
RELEASE			: 'R';
SINCE			: 'S';
TRIGGER			: 'T';

// Boolean operators
NOT				: 'not' | '!'; 
AND				: 'and' | '&';
OR				: 'or' | '|';
IMPLIES			: '=>';

// Unparsed 
PREDICATE		: LEFT_CURLY (~('{' | '}') | PREDICATE)* RIGHT_CURLY;
ACTION			: LEFT_BRACKET (~('[' | ']') | ACTION)* RIGHT_BRACKET;
ENABLED			: 'e' ENABLED_PAREN;
fragment 
ENABLED_PAREN	: LEFT_PAREN (~('(' | ')') | ENABLED_PAREN)* RIGHT_PAREN;

// Others
LEFT_CURLY		: '{';
RIGHT_CURLY		: '}';
LEFT_BRACKET	: '[';
RIGHT_BRACKET	: ']';
LEFT_PAREN		: '(';
RIGHT_PAREN		: ')';

// Comments
COMMENT			: ('//' ~('\n')* 
				| '/*' .*? '*/') -> skip;
				
// Patterns
PATTERN_DEF		: 'def';

// Scopes
BEFORE_SCOPE	: 'before';
AFTER_SCOPE		: 'after';
BETWEEN_SCOPE	: 'between';
UNTIL_SCOPE		: 'after_until';

// Vars
VAR				: 'var';
NUM_VAR			: 'num';
SEQ_VAR			: 'seq';

// Sequence
SEQ_WITHOUT		: 'without';

// Loops
LOOP_BEGIN		: 'count';
LOOP_END		: 'end';
UP				: 'up';
DOWN			: 'down';
TO				: 'to';
			
// Whitespaces
NUM				: '0' | [1-9] [0-9]*;
ID				: [a-zA-Z] [a-zA-Z0-9_]*;
WS				: [ \t\r\n]+ -> skip;