package com.strategicgains.hyperexpress.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DefaultUrlBuilderTest
{
	private static final String URL_PATTERN = "/{id}";
	private static final String URL_PATTERN2 = "/{rootId}/{secondaryId}/{id}";

	@Test
	public void shouldBuildSimpleUrl()
	{
		assertEquals("/todd:was,here", new DefaultUrlBuilder(URL_PATTERN)
			.build(new DefaultTokenResolver()
				.bind("id", "todd:was,here")
			));
	}

	@Test
	public void shouldBuildComplexUrl()
	{
		assertEquals("/something/else/12345", new DefaultUrlBuilder(URL_PATTERN2)
			.build(new DefaultTokenResolver()
				.bind("rootId", "something")
				.bind("secondaryId", "else")
				.bind("id", "12345")
			));	
	}

	@Test
	public void shouldBuildMultipleUrls()
	{
		UrlBuilder b = new DefaultUrlBuilder(URL_PATTERN2);
		TokenResolver r = new DefaultTokenResolver()
			.bind("rootId", "something")
			.bind("secondaryId", "else")
			.bind("id", "12345");

		assertEquals("/something/else/12345", b.build(r));

		r.bind("rootId", "anything")
			.bind("secondaryId", "maybe")
			.bind("id", "54321");

		assertEquals("/anything/maybe/54321", b.build(r));

		r.bind("secondaryId", "wonderful");

		assertEquals("/anything/wonderful/54321", b.build(r));
	}

	@Test
	public void shouldExcludeQueryString()
	{
		assertEquals("/something/else/12345", new DefaultUrlBuilder(URL_PATTERN2)
			.withQuery("limit={selfLimit}")
			.withQuery("offset={selfOffset}")
			.build(new DefaultTokenResolver()
				.bind("rootId", "something")
				.bind("secondaryId", "else")
				.bind("id", "12345")
			));	
	}

	@Test
	public void shouldIncludeEntireQueryString()
	{
		assertEquals("/something/else/12345?limit=20&offset=40", new DefaultUrlBuilder(URL_PATTERN2)
			.withQuery("limit={selfLimit}")
			.withQuery("offset={selfOffset}")
			.build(new DefaultTokenResolver()
				.bind("rootId", "something")
				.bind("secondaryId", "else")
				.bind("id", "12345")
				.bind("selfLimit", "20")
				.bind("selfOffset", "40")
			));	
	}

	@Test
	public void shouldIncludeQueryString()
	{
		assertEquals("/something/else/12345?offset=40", new DefaultUrlBuilder(URL_PATTERN2)
			.withQuery("limit={selfLimit}")
			.withQuery("offset={selfOffset}")
			.build(new DefaultTokenResolver()
				.bind("rootId", "something")
				.bind("secondaryId", "else")
				.bind("id", "12345")
				.bind("selfOffset", "40")
			));	
	}

	@Test
	public void shouldIncludeOptionalQueryString()
	{
		assertEquals("/something/else/12345?offset=40&limit=20", new DefaultUrlBuilder(URL_PATTERN2 + "?offset={nextOffset}")
			.withQuery("limit={limit}")
			.build(new DefaultTokenResolver()
				.bind("rootId", "something")
				.bind("secondaryId", "else")
				.bind("id", "12345")
				.bind("nextOffset", "40")
				.bind("limit", "20")
			));	
	}

	@Test
	public void shouldExcludeOptionalQueryString()
	{
		assertEquals("/something/else/12345?offset=40", new DefaultUrlBuilder(URL_PATTERN2 + "?offset={nextOffset}")
			.withQuery("limit={limit}")
			.build(new DefaultTokenResolver()
				.bind("rootId", "something")
				.bind("secondaryId", "else")
				.bind("id", "12345")
				.bind("nextOffset", "40")
			));	
	}

	@Test
	public void shouldUseExternalTokenResolver()
	{
		assertEquals("/something/else/12345?limit=20&offset=40", new DefaultUrlBuilder(URL_PATTERN2)
			.withQuery("limit={selfLimit}")
			.withQuery("offset={selfOffset}")
			.build(new DefaultTokenResolver()
				.bind("rootId", "something")
				.bind("secondaryId", "else")
				.bind("id", "12345")
				.bind("selfLimit", "20")
				.bind("selfOffset", "40")
			));	
	}
}
