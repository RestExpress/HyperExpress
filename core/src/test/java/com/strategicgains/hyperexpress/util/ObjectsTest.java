package com.strategicgains.hyperexpress.util;

import static org.junit.Assert.assertEquals;

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

	public class A
	{
		@SuppressWarnings("unused")
		private B b = new B();
	}

	public class B
	{
		@SuppressWarnings("unused")
		private C c = new C();
	}

	public class C
	{
		@SuppressWarnings("unused")
		private String value = "got it!";
	}
}
