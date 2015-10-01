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
		private B b = new B();
	}

	public class B
	{
		private C c = new C();
	}

	public class C
	{
		private String value = "got it!";
	}
}
