package com.strategicgains.hyperexpress.annotations;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.strategicgains.hyperexpress.annotation.AnnotationTokenBinder;
import com.strategicgains.hyperexpress.annotation.BindToken;
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
		String result = tr.resolve("{string},{UUID},{intValue},{notBound}", a);
		assertEquals("a string,6777a80b-88f1-4e66-88d6-c88ffc164050,42,{notBound}", result);
	}

	@Test
	public void shouldNotBind()
	{
		AnnotationTokenBinder atb = new AnnotationTokenBinder();
		TokenResolver tr = new DefaultTokenResolver();
		tr.binder(atb);
		String result = tr.resolve("{string},{UUID},{intValue},{notBound}");
		assertEquals("{string},{UUID},{intValue},{notBound}", result);
	}

	@Test
	public void shouldBindViaBinder()
	{
		Annotated a = new Annotated();
		AnnotationTokenBinder atb = new AnnotationTokenBinder();
		TokenResolver tr = new DefaultTokenResolver();
		atb.bind(a, tr);
		String result = tr.resolve("{string},{UUID},{intValue},{notBound}");
		assertEquals("a string,6777a80b-88f1-4e66-88d6-c88ffc164050,42,{notBound}", result);
	}

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
	}
}
