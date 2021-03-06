package com.strategicgains.hyperexpress.domain.siren;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import com.strategicgains.hyperexpress.AbstractResourceFactoryStrategy;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.domain.siren.SirenResource;
import com.strategicgains.hyperexpress.domain.siren.SirenResourceFactory;

public class SirenResourceFactoryTest
{
	@Test
	public void shouldCreateBlogResourceWithDefaultFields()
	{
		SirenResourceFactory factory = new SirenResourceFactory();
		Blog b = new Blog();
		UUID blogId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		b.setDescription("Blog Description");
		b.setId(blogId);
		b.setName("Blog Name");
		b.setOwnerId(ownerId);
		Resource r = factory.createResource(b);
		assertEquals("Blog Description", r.getProperty("description"));
		assertEquals("Blog Name", r.getProperty("name"));
		assertEquals(blogId, r.getProperty("id"));
		assertEquals(ownerId, r.getProperty("ownerId"));
		assertNull(r.getProperty("somethingTransient"));
		assertNull(r.getProperty("somethingVolatile"));
		assertNull(r.getProperty("somethingStatic"));
		assertNull(r.getProperty("IGNORED"));
	}

	@Test
	public void shouldCreateBlogResourceWithAnnotatedFields()
	{
		AbstractResourceFactoryStrategy factory = new SirenResourceFactory()
			.includeAnnotations(Include.class)
			.excludeAnnotations(Exclude.class);
		Blog b = new Blog();
		UUID blogId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		b.setDescription("Blog Description");
		b.setId(blogId);
		b.setName("Blog Name");
		b.setOwnerId(ownerId);
		Resource r = factory.createResource(b);
		assertEquals("Blog Description", r.getProperty("description"));
		assertNull(r.getProperty("name"));
		assertEquals(blogId, r.getProperty("id"));
		assertEquals(ownerId, r.getProperty("ownerId"));
		assertNotNull(r.getProperty("somethingTransient"));
		assertNotNull(r.getProperty("somethingVolatile"));
		assertNotNull(r.getProperty("somethingStatic"));
		assertNotNull(r.getProperty("IGNORED"));
	}

	@Test
	public void shouldCreateResourceFromNull()
	{
		SirenResourceFactory factory = new SirenResourceFactory();
		Resource r = factory.createResource(null);
		assertNotNull(r);
		assertFalse(r.hasLinks());
		assertFalse(r.hasNamespaces());
		assertFalse(r.hasProperties());
		assertFalse(r.hasResources());
	}

	@Test
	public void shouldReturnHalResourceClass()
	{
		SirenResourceFactory factory = new SirenResourceFactory();
		Class<?> type = factory.getResourceType();
		assertEquals(SirenResource.class, type);
	}
}
