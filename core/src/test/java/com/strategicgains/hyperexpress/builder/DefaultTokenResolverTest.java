package com.strategicgains.hyperexpress.builder;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.strategicgains.hyperexpress.annotation.BindToken;

public class DefaultTokenResolverTest
{
	private static final String[] URLS = {
		"/a/{a}/b/{b}",
		"/c/{c}/d/{d}/e/{e}",
		"{f}"
	};

	private TokenResolver resolver = new DefaultTokenResolver(true)
		.bind("a", "a")
		.bind("b", "b")
		.bind("c", "c")
		.bind("d", "d")
		.binder(new TokenBinder<Resolvable>()
		{
			@Override
			public void bind(Resolvable object, TokenResolver r)
			{
				r.bind("e", String.valueOf(object.e));
			}
		});

	@Test
	public void testContains()
	{
		assertTrue(resolver.contains("a"));
		assertTrue(resolver.contains("b"));
		assertTrue(resolver.contains("c"));
		assertTrue(resolver.contains("d"));
		assertFalse(resolver.contains("e"));
	}

	@Test
	public void shouldHandleNullObject()
	{
		Collection<String> urls = resolver.resolve(Arrays.asList(URLS));
		assertNotNull(urls);
		verifyUrls(urls, "/a/a/b/b", "/c/c/d/d/e/{e}", "{f}");
	}

	@Test
	public void shouldResolveFromObject()
	{
		Resolvable r = new Resolvable();
		r.e = 42;
		Collection<String> urls = resolver.resolve(Arrays.asList(URLS), r);
		assertNotNull(urls);
		verifyUrls(urls, "/a/a/b/b", "/c/c/d/d/e/42", "{f}");
	}

	@Test
	public void shouldUseScopes()
	{
		Resolvable r = new Resolvable();
		r.e = 42;
		Collection<String> urls = resolver.resolve(Arrays.asList(URLS), r);
		assertNotNull(urls);
		verifyUrls(urls, "/a/a/b/b", "/c/c/d/d/e/42", "{f}");

		urls = resolver.resolve(Arrays.asList(URLS));
		assertNotNull(urls);
		verifyUrls(urls, "/a/a/b/b", "/c/c/d/d/e/{e}", "{f}");
	}

	@Test
	public void shouldResolveAdditionalToken()
	{
		Resolvable r = new Resolvable();
		r.e = 13;
		resolver.bind("f", "toddf");
		Collection<String> urls = resolver.resolve(Arrays.asList(URLS), r);
		resolver.remove("f");
		assertNotNull(urls);
		verifyUrls(urls, "/a/a/b/b", "/c/c/d/d/e/13", "toddf");

		urls = resolver.resolve(Arrays.asList(URLS), r);
		assertNotNull(urls);
		verifyUrls(urls, "/a/a/b/b", "/c/c/d/d/e/13", "{f}");
	}

	@Test
	public void shouldBindViaAnnotations()
	{
		String urlPattern = "/{annotated}/{formatted}/{nullable}";
		Resolvable r = new Resolvable();
		assertEquals("/0/0.00/{nullable}", resolver.resolve(urlPattern, r));

		r.anInt = 22;
		r.aDouble = 33.1234567;
		r.nullable = 1;
		assertEquals("/22/33.12/1", resolver.resolve(urlPattern, r));
	}

	private void verifyUrls(Collection<String> actual, String... expected)
    {
		assertEquals(expected.length, actual.size());
	    int i = 0;

		for (String url : actual)
		{
			assertEquals(expected[i++], url);
		}
    }

	private class Resolvable
	{
		public int e;

		@BindToken("annotated")
		public int anInt;

		@BindToken(value = "formatted", formatter = DoubleFormatter.class)
		public double aDouble;

		@BindToken("nullable")
		public Integer nullable;
	}
}
