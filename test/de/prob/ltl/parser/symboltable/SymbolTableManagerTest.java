package de.prob.ltl.parser.symboltable;

import org.junit.Assert;
import org.junit.Test;

import de.prob.ltl.parser.AbstractParserTest;
import de.prob.ltl.parser.semantic.PatternDefinition;

public class SymbolTableManagerTest extends AbstractParserTest {

	@Test
	public void testDefineRoot() {
		SymbolTableManager stm = new SymbolTableManager();

		stm.popScope();
		Assert.assertEquals(stm.getGlobalScope(), stm.getCurrentScope());

		Assert.assertTrue(stm.define(new Variable("a", null)));
		Assert.assertTrue(stm.define(new Variable("b", null)));
		Assert.assertTrue(stm.define(new Variable("c", null)));
		Assert.assertFalse(stm.define(new Variable("a", null)));
		Assert.assertFalse(stm.define(new Variable("b", null)));
		Assert.assertFalse(stm.define(new Variable("c", null)));

		Assert.assertTrue(stm.define(new TestPattern("x")));
		Assert.assertTrue(stm.define(new TestPattern("y")));
		Assert.assertTrue(stm.define(new TestPattern("z")));
		Assert.assertFalse(stm.define(new TestPattern("x")));
		Assert.assertFalse(stm.define(new TestPattern("y")));
		Assert.assertFalse(stm.define(new TestPattern("z")));

		Assert.assertTrue(stm.isDefinedVariable("a"));
		Assert.assertFalse(stm.isDefinedVariable("x"));
		Assert.assertTrue(stm.isDefinedPattern("x"));
		Assert.assertFalse(stm.isDefinedPattern("a"));
	}

	@Test
	public void testDefineWithParent() {
		SymbolTableManager stm = new SymbolTableManager();

		SymbolTable st2 = new SymbolTable(stm.getCurrentScope());
		SymbolTable st3 = new SymbolTable(st2, true);

		Assert.assertTrue(stm.define(new Variable("x", null)));
		Assert.assertTrue(stm.define(new TestPattern("x")));
		stm.pushScope(st2);
		Assert.assertTrue(stm.define(new Variable("x", null)));
		Assert.assertFalse(stm.define(new TestPattern("y")));
		stm.pushScope(st3);
		Assert.assertFalse(stm.define(new Variable("x", null)));
		Assert.assertFalse(stm.define(new TestPattern("z")));
	}

	@Test
	public void testResolveRoot() {
		SymbolTableManager stm = new SymbolTableManager();

		Variable a = new Variable("a", null);
		Variable b = new Variable("b", null);
		Variable a2 = new Variable("a", null);

		stm.define(a);
		stm.define(b);
		stm.define(a2);

		Assert.assertEquals(a, stm.resolveVariable("a"));
		Assert.assertEquals(b, stm.resolveVariable("b"));

		TestPattern x = new TestPattern("x");
		TestPattern y = new TestPattern("y");
		TestPattern x2 = new TestPattern("x");

		stm.define(x);
		stm.define(y);
		stm.define(x2);

		Assert.assertEquals(x, stm.resolvePattern("x"));
		Assert.assertEquals(y, stm.resolvePattern("y"));
	}

	@Test
	public void testResolveWithParent() {
		SymbolTableManager stm = new SymbolTableManager();

		SymbolTable st2 = new SymbolTable(stm.getCurrentScope());
		SymbolTable st3 = new SymbolTable(st2, true);

		Variable a = new Variable("a", null);
		Variable b = new Variable("b", null);
		Variable a2 = new Variable("a", null);
		Variable b3 = new Variable("b", null);
		Variable a3 = new Variable("a", null);

		stm.define(a);
		stm.pushScope(st2);
		stm.define(b);
		stm.define(a2);
		stm.pushScope(st3);
		stm.define(b3);
		stm.define(a3);

		stm.popScope();
		stm.popScope();
		Assert.assertEquals(a, stm.resolveVariable("a"));
		Assert.assertNull(stm.resolveVariable("b"));

		stm.pushScope(st2);
		Assert.assertEquals(a2, stm.resolveVariable("a"));
		Assert.assertEquals(b, stm.resolveVariable("b"));

		stm.pushScope(st3);
		Assert.assertEquals(a2, stm.resolveVariable("a"));
		Assert.assertEquals(b, stm.resolveVariable("b"));

		TestPattern x = new TestPattern("x");

		stm.popScope();
		stm.popScope();
		stm.define(x);

		Assert.assertEquals(x, stm.resolvePattern("x"));
		stm.pushScope(st2);
		Assert.assertEquals(x, stm.resolvePattern("x"));
		stm.pushScope(st3);
		Assert.assertEquals(x, stm.resolvePattern("x"));
	}

	// Helper
	public class TestPattern extends PatternDefinition {

		private String name;

		public TestPattern(String name) {
			super(createParser(""), null);
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

	}

}
