package com.strategicgains.hyperexpress.annotations;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.strategicgains.hyperexpress.annotation.AnnotationTokenBinder;
import com.strategicgains.hyperexpress.annotation.BindToken;
import com.strategicgains.hyperexpress.annotation.TokenBindings;
import com.strategicgains.hyperexpress.builder.DefaultTokenResolver;
import com.strategicgains.hyperexpress.builder.TokenResolver;

public class AnnotationTokenBinderTest
{
	@Test
	public void shouldBindViaTokenResolver()
	{
		Annotated a = new Annotated();
		AnnotationTokenBinder atb = new AnnotationTokenBinder();
		TokenResolver tr = new DefaultTokenResolver();
		tr.binder(atb);
		String result = tr.resolve("{string},{UUID},{intValue},{notBound},{bValue},{dId}", a);
		assertEquals("a string,6777a80b-88f1-4e66-88d6-c88ffc164050,42,{notBound},got_it,7630d885-0af8-428b-bfea-91a95d597932", result);
	}

	@Test
	public void shouldNotBind()
	{
		AnnotationTokenBinder atb = new AnnotationTokenBinder();
		TokenResolver tr = new DefaultTokenResolver();
		tr.binder(atb);
		String result = tr.resolve("{string},{UUID},{intValue},{notBound},{bValue},{dId}");
		assertEquals("{string},{UUID},{intValue},{notBound},{bValue},{dId}", result);
	}

	@Test
	public void shouldBindViaBinder()
	{
		Annotated a = new Annotated();
		AnnotationTokenBinder atb = new AnnotationTokenBinder();
		TokenResolver tr = new DefaultTokenResolver();
		atb.bind(a, tr);
		String result = tr.resolve("{string},{UUID},{intValue},{notBound},{bValue},{dId}");
		assertEquals("a string,6777a80b-88f1-4e66-88d6-c88ffc164050,42,{notBound},got_it,7630d885-0af8-428b-bfea-91a95d597932", result);
	}

	@TokenBindings({
		@BindToken(value = "dId", field = "d.id")
	})
	private class Annotated
	{
		@BindToken("string")
		private String string = "a string";

		@BindToken("UUID")
		private UUID uuid = UUID.fromString("6777a80b-88f1-4e66-88d6-c88ffc164050");

		@BindToken("intValue")
		private int primitiveInt = 42;

		// Don't bind this.
		@SuppressWarnings("unused")
		private int notBound = 43;

		@BindToken(value = "bValue", field = "c.value")
		private B b = new B();
	
		// Bind this via a class-level annotation.
		@SuppressWarnings("unused")
		private D d = new D();
	}

	private class B
	{
		@SuppressWarnings("unused")
		private C c = new C();
	}

	private class C
	{
		@SuppressWarnings("unused")
		private String value = "got_it";
	}

	private class D
	{
		@SuppressWarnings("unused")
		UUID id = UUID.fromString("7630d885-0af8-428b-bfea-91a95d597932");
	}
}
