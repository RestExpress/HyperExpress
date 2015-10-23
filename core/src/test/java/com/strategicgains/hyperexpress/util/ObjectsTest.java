package com.strategicgains.hyperexpress.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ObjectsTest
{
	@Test
	public void shouldExtractField()
	throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		String v = (String) Objects.property("b.c.value", new A());
		assertEquals("got it!", v);
	}

	@Test
	public void shouldHandleNullField()
	throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		String v = (String) Objects.property("b.c.nullable", new A());
		assertNull(v);
	}

	@Test
	public void shouldHandleNullIntermediateField()
	throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		String v = (String) Objects.property("b.nullable.value", new A());
		assertNull(v);
	}

	@Test
	public void shouldHandleNullObject()
	throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		String v = (String) Objects.property("b.c.value", null);
		assertNull(v);
	}

	public class A
	{
		@SuppressWarnings("unused")
		private B b = new B();
	}

	@SuppressWarnings("unused")
	public class B
	{
		private C c = new C();
		private C nullable = null;
	}

	@SuppressWarnings("unused")
	public class C
	{
		private String value = "got it!";
		private String nullable = null;
	}
}
