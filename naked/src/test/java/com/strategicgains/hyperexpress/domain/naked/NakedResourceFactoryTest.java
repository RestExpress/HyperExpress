package com.strategicgains.hyperexpress.domain.naked;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.junit.Test;

import com.strategicgains.hyperexpress.AbstractResourceFactoryStrategy;
import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.domain.Resource;

public class NakedResourceFactoryTest
{
	@Test
	public void shouldCreateBlogResourceWithDefaultFields()
	{
		NakedResourceFactory factory = new NakedResourceFactory();
		Blog b = new Blog();
		UUID blogId = UUID.randomUUID();
		User owner = new User(UUID.randomUUID());
		b.setDescription("Blog Description");
		b.setId(blogId);
		b.setName("Blog Name");
		b.setOwner(owner);
		Resource r = factory.createResource(b);
		assertEquals("Blog Description", r.getProperty("description"));
		assertEquals("Blog Name", r.getProperty("name"));
		assertEquals(blogId, r.getProperty("id"));
		assertNull(r.getProperty("owner"));
		assertNull(r.getProperty("somethingTransient"));
		assertNull(r.getProperty("somethingVolatile"));
		assertNull(r.getProperty("somethingStatic"));
		assertNull(r.getProperty("IGNORED"));

		List<Link> links = r.getLinks();
		assertNotNull(links);

		Optional<Link> o = links.stream().filter(new Predicate<Link>()
		{
			@Override
			public boolean test(Link t)
			{
				return "owner".equals(t.getRel());
			}
		}).findFirst();

		assertTrue(o.isPresent());
		NakedLink ownerLink = (NakedLink) o.get();
		assertEquals(owner.getId().toString(), ownerLink.getId());
		assertEquals("", ownerLink.getHref());
	}

	@Test
	public void shouldCreateBlogResourceWithAnnotatedFields()
	{
		AbstractResourceFactoryStrategy factory = new NakedResourceFactory()
			.includeAnnotations(Include.class)
			.excludeAnnotations(Exclude.class);
		Blog b = new Blog();
		UUID blogId = UUID.randomUUID();
		User owner = new User(UUID.randomUUID());
		b.setDescription("Blog Description");
		b.setId(blogId);
		b.setName("Blog Name");
		b.setOwner(owner);
		Resource r = factory.createResource(b);
		assertEquals("Blog Description", r.getProperty("description"));
		assertNull(r.getProperty("name"));
		assertEquals(blogId, r.getProperty("id"));
		assertNull(r.getProperty("owner"));
		assertNotNull(r.getProperty("somethingTransient"));
		assertNotNull(r.getProperty("somethingVolatile"));
		assertNotNull(r.getProperty("somethingStatic"));
		assertNotNull(r.getProperty("IGNORED"));

		List<Link> links = r.getLinks();
		assertNotNull(links);

		Optional<Link> o = links.stream().filter(new Predicate<Link>()
		{
			@Override
			public boolean test(Link t)
			{
				return "owner".equals(t.getRel());
			}
		}).findFirst();

		assertTrue(o.isPresent());
		NakedLink ownerLink = (NakedLink) o.get();
		assertEquals(owner.getId().toString(), ownerLink.getId());
		assertEquals("", ownerLink.getHref());
	}

	@Test
	public void shouldCreateResourceFromNull()
	{
		NakedResourceFactory factory = new NakedResourceFactory();
		Resource r = factory.createResource(null);
		assertNotNull(r);
		assertFalse(r.hasLinks());
		assertFalse(r.hasNamespaces());
		assertFalse(r.hasProperties());
		assertFalse(r.hasResources());
	}

	@Test
	public void shouldReturnNakedResourceClass()
	{
		NakedResourceFactory factory = new NakedResourceFactory();
		Class<?> type = factory.getResourceType();
		assertEquals(NakedResource.class, type);
	}
}
