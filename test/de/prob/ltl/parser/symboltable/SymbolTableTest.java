package de.prob.ltl.parser.symboltable;

import org.junit.Assert;
import org.junit.Test;

import de.prob.ltl.parser.AbstractParserTest;
import de.prob.ltl.parser.semantic.PatternDefinition;

public class SymbolTableTest extends AbstractParserTest {

	@Test
	public void testDefineRoot() {
		SymbolTable st = new SymbolTable(null);

		Assert.assertTrue(st.define(new Variable("x", null)));
		Assert.assertTrue(st.define(new Variable("y", null)));
		Assert.assertTrue(st.define(new Variable("z", null)));
		Assert.assertFalse(st.define(new Variable("x", null)));
		Assert.assertFalse(st.define(new Variable("y", null)));
		Assert.assertFalse(st.define(new Variable("z", null)));

		Assert.assertTrue(st.define(new TestPattern("x")));
		Assert.assertTrue(st.define(new TestPattern("y")));
		Assert.assertTrue(st.define(new TestPattern("z")));
		Assert.assertFalse(st.define(new TestPattern("x")));
		Assert.assertFalse(st.define(new TestPattern("y")));
		Assert.assertFalse(st.define(new TestPattern("z")));
	}

	@Test
	public void testDefineWithParent() {
		SymbolTable st = new SymbolTable(null);
		SymbolTable st2 = new SymbolTable(st);
		SymbolTable st3 = new SymbolTable(st2, true);

		Assert.assertTrue(st.define(new Variable("x", null)));
		Assert.assertTrue(st2.define(new Variable("x", null)));
		Assert.assertFalse(st3.define(new Variable("x", null)));

		Assert.assertTrue(st.define(new TestPattern("x")));
		Assert.assertFalse(st2.define(new TestPattern("y")));
		Assert.assertFalse(st2.define(new TestPattern("z")));
	}

	@Test
	public void testResolveRoot() {
		SymbolTable st = new SymbolTable(null);

		Variable a = new Variable("a", null);
		Variable b = new Variable("b", null);
		Variable a2 = new Variable("a", null);

		st.define(a);
		st.define(b);
		st.define(a2);

		Assert.assertEquals(a, st.resolveVariable("a"));
		Assert.assertEquals(b, st.resolveVariable("b"));

		TestPattern x = new TestPattern("x");
		TestPattern y = new TestPattern("y");
		TestPattern x2 = new TestPattern("x");

		st.define(x);
		st.define(y);
		st.define(x2);

		Assert.assertEquals(x, st.resolvePattern("x"));
		Assert.assertEquals(y, st.resolvePattern("y"));
	}

	@Test
	public void testResolveWithParent() {
		SymbolTable st = new SymbolTable(null);
		SymbolTable st2 = new SymbolTable(st);
		SymbolTable st3 = new SymbolTable(st2, true);

		Variable a = new Variable("a", null);
		Variable b = new Variable("b", null);
		Variable a2 = new Variable("a", null);
		Variable b3 = new Variable("b", null);
		Variable a3 = new Variable("a", null);

		st.define(a);
		st2.define(b);
		st2.define(a2);
		st3.define(b3);
		st3.define(a3);

		Assert.assertEquals(a, st.resolveVariable("a"));
		Assert.assertNull(st.resolveVariable("b"));

		Assert.assertEquals(a2, st2.resolveVariable("a"));
		Assert.assertEquals(b, st2.resolveVariable("b"));

		Assert.assertEquals(a2, st3.resolveVariable("a"));
		Assert.assertEquals(b, st3.resolveVariable("b"));

		TestPattern x = new TestPattern("x");

		st.define(x);

		Assert.assertEquals(x, st.resolvePattern("x"));
		Assert.assertEquals(x, st2.resolvePattern("x"));
		Assert.assertEquals(x, st3.resolvePattern("x"));
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
