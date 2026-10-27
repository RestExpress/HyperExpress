package com.strategicgains.hyperexpress.domain.siren;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.strategicgains.hyperexpress.HyperExpress;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.domain.siren.SirenResource;
import com.strategicgains.hyperexpress.domain.siren.SirenResourceFactory;

public class HyperExpressSirenTest
{

	@BeforeAll
	public static void setUpBeforeClass() throws Exception
	{
		HyperExpress.registerResourceFactoryStrategy(new SirenResourceFactory(), "*");
	}

	@Test
	public void shouldCreateResourceFromNull()
	{
		Resource r = HyperExpress.createResource(null, "*");
		assertNotNull(r);
		assertFalse(r.hasLinks());
		assertFalse(r.hasNamespaces());
		assertFalse(r.hasProperties());
		assertFalse(r.hasResources());
	}

	@Test
	public void shouldCreateCollectionResourceFromNullWithRel()
	{
		Resource r = HyperExpress.createCollectionResource(null, Blog.class, "blogs", "*");
		assertNotNull(r);
		assertFalse(r.hasLinks());
		assertFalse(r.hasNamespaces());
		assertFalse(r.hasProperties());
		assertTrue(r.hasResources());
		assertTrue(r.getResources("blogs").isEmpty());
	}

	@Test
	public void shouldCreateCollectionResourceFromNullWithoutRel()
	{
		Resource r = HyperExpress.createCollectionResource(null, Blog.class, "*");
		assertNotNull(r);
		assertFalse(r.hasLinks());
		assertFalse(r.hasNamespaces());
		assertFalse(r.hasProperties());
		assertTrue(r.hasResources());
		assertTrue(r.getResources("blogs").isEmpty());
	}

	@Test
	public void shouldReturnHalResourceClass()
	{
		Class<?> type = HyperExpress.getResourceType("*");
		assertEquals(SirenResource.class, type);
	}
}
