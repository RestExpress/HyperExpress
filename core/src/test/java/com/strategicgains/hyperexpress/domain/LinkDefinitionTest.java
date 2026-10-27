package com.strategicgains.hyperexpress.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LinkDefinitionTest
{
	@Test
	public void shouldHaveTemplate()
	{
		Link link = new LinkDefinition("self", "/something/{id}");
		assertEquals("self", link.getRel());
		assertEquals("/something/{id}", link.getHref());
		assertTrue(link.hasToken());
	}

	@Test
	public void shouldSetArbitraryProperty()
	{
		Link link = new LinkDefinition("rel", "href");
		link.set("arbitrary", "value");
		assertEquals("rel", link.getRel());
		assertEquals("href", link.getHref());
		assertEquals("value", link.get("arbitrary"));
		assertFalse(link.hasToken());
	}
}
