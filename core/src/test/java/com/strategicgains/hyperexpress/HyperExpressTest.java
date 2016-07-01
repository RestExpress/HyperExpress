package com.strategicgains.hyperexpress;

import static com.strategicgains.hyperexpress.RelTypes.ALTERNATE;
import static com.strategicgains.hyperexpress.RelTypes.NEXT;
import static com.strategicgains.hyperexpress.RelTypes.PREV;
import static com.strategicgains.hyperexpress.RelTypes.SELF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.strategicgains.hyperexpress.domain.Blog;
import com.strategicgains.hyperexpress.domain.Comment;
import com.strategicgains.hyperexpress.domain.Entry;
import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.domain.Namespace;
import com.strategicgains.hyperexpress.domain.Resource;

public class HyperExpressTest
{
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		HyperExpress.registerResourceFactoryStrategy(new NullResourceFactoryStrategy(), "*");
		HyperExpress.relationships()
		.addNamespaces(
			new Namespace("ea", "http://namespaces.example.com/{rel}"),
			new Namespace("blog", "http://namespaces.example.com/{rel}")
		)

		.forCollectionOf(Blog.class)
			.rels(SELF, "/blogs")
				.asRel("blogerrific")
				.withQuery("limit={selfLimit}")
				.withQuery("offset={selfOffset}")
			.rel(NEXT, "/blogs?limit={nextLimit}&offset={nextOffset}").optional()
			.rel(PREV, "/blogs?limit={prevLimit}&offset={prevOffset}").optional()

		.forClass(Entry.class)
			.rel(SELF, "/entries/{entryId}")
			.rel("edit", "/entries/{entryId}/edit")
				.attribute("method", "PUT")
				.ifBound("{adminRole}")
			.rels(ALTERNATE, "/blogs/{blogId}/entries/{entryId}").optional()

		.forCollectionOf(Comment.class)
			.rel(SELF, "/blogs/{blogId}/entries/{entryId}/comments")
			.rel(SELF, "/comments/{commentId}")

		.forClass(Comment.class)
			.rel(SELF, "/admin-role")
				.ifBound("adminRole")
				.ifNotBound("superUser")
			.rel(SELF, "/guest-role")
				.ifNotBound("adminRole")
			.rel(SELF, "/superuser-role")
				.ifBound("adminRole")
				.ifBound("superUser");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@After
	public void cleanup()
	{
		HyperExpress.clearTokenBindings();
	}

	@Test
	public void shouldIgnoreOptionalLinks()
	{
		Resource r = HyperExpress.createCollectionResource(Collections.EMPTY_LIST, Blog.class, "*");
		assertNotNull(r.getLinks());
		assertEquals(1, r.getLinks().size());
		Link link = r.getLinks().iterator().next();
		assertEquals(SELF, link.getRel());
		assertTrue(HyperExpress.relationships().isCollectionArrayRel(Blog.class, link.getRel()));
		assertFalse(HyperExpress.relationships().isArrayRel(Blog.class, link.getRel()));
		assertTrue(r.isMultipleLinks(link.getRel()));
		assertFalse(r.isMultipleResources(link.getRel()));
		assertEquals("/blogs", link.getHref());
		assertTrue(r.hasResources("blogerrific"));

		r = HyperExpress.createResource(new Entry(), "*");
		assertNotNull(r.getLinks());
		assertEquals(1, r.getLinks().size());
		link = r.getLinks().iterator().next();
		assertEquals(SELF, link.getRel());
		assertFalse(HyperExpress.relationships().isArrayRel(Entry.class, link.getRel()));
		assertFalse(HyperExpress.relationships().isCollectionArrayRel(Entry.class, link.getRel()));
		assertFalse(r.isMultipleLinks(link.getRel()));
		assertFalse(r.isMultipleResources(link.getRel()));
		assertEquals("/entries/{entryId}", link.getHref());		

		HyperExpress.bind("adminRole", "false");
		r = HyperExpress.createResource(new Entry(), "*");
		assertNotNull(r.getLinks());
		assertEquals(1, r.getLinks().size());
		link = r.getLinks().iterator().next();
		assertEquals(SELF, link.getRel());
		assertFalse(HyperExpress.relationships().isArrayRel(Entry.class, link.getRel()));
		assertFalse(HyperExpress.relationships().isCollectionArrayRel(Entry.class, link.getRel()));
		assertFalse(r.isMultipleLinks(link.getRel()));
		assertFalse(r.isMultipleResources(link.getRel()));
		assertEquals("/entries/{entryId}", link.getHref());		

		r = HyperExpress.createResource(new Comment(), "*");
		assertNotNull(r.getLinks());
		assertEquals(1, r.getLinks().size());
		link = r.getLinks().iterator().next();
		assertEquals(SELF, link.getRel());
		assertFalse(HyperExpress.relationships().isArrayRel(Comment.class, link.getRel()));
		assertFalse(HyperExpress.relationships().isCollectionArrayRel(Comment.class, link.getRel()));
		assertFalse(r.isMultipleLinks(link.getRel()));
		assertFalse(r.isMultipleResources(link.getRel()));
		assertEquals("/guest-role", link.getHref());		
	}

	@Test
	public void shouldContainOptionalQueryStringParameters()
	{
		HyperExpress
			.bind("selfOffset", "40")
			.bind("selfLimit", "20");
		Resource r = HyperExpress.createCollectionResource(null, Blog.class, "*");
		assertNotNull(r.getLinks());
		assertEquals(1, r.getLinks().size());
		Link link = r.getLinks().iterator().next();
		assertEquals(SELF, link.getRel());
		assertEquals("/blogs?limit=20&offset=40", link.getHref());
		HyperExpress.clearTokenBindings();
	}

	@Test
	public void shouldContainOptionalLinks()
	{
		HyperExpress
			.bind("nextOffset", "40")
			.bind("nextLimit", "20");
		Resource r = HyperExpress.createCollectionResource(null, Blog.class, "*");
		assertNotNull(r.getLinks());
		assertEquals(2, r.getLinks().size());
		Iterator<Link> iter = r.getLinks().iterator();

		while(iter.hasNext())
		{
			Link link = iter.next();

			switch (link.getRel())
            {
				case SELF:
					assertEquals("/blogs", link.getHref());
					break;

				case NEXT:
					assertEquals("/blogs?limit=20&offset=40", link.getHref());
					break;

				default:
					fail("Unexpected 'rel' " + link.getRel());
					break;
			}
		}
	}

	@Test
	public void shouldContainOptionallyBoundLinksWithBoolean()
	{
		HyperExpress.bind("adminRole", Boolean.TRUE.toString());
		Resource r = HyperExpress.createResource(new Entry(), "*");
		assertNotNull(r.getLinks());
		assertEquals(2, r.getLinks().size());
		Iterator<Link> iter = r.getLinks().iterator();

		while(iter.hasNext())
		{
			Link link = iter.next();

			switch (link.getRel())
            {
				case SELF:
					assertEquals("/entries/{entryId}", link.getHref());
					assertFalse(r.isMultipleLinks(link.getRel()));
					assertFalse(r.isMultipleResources(link.getRel()));
					break;

				case "edit":
					assertEquals("/entries/{entryId}/edit", link.getHref());
					assertFalse(r.isMultipleLinks(link.getRel()));
					assertFalse(r.isMultipleResources(link.getRel()));
					break;

				default:
					fail("Unexpected 'rel' " + link.getRel());
					break;
			}
		}

		r = HyperExpress.createResource(new Comment(), "*");
		assertNotNull(r.getLinks());
		assertEquals(1, r.getLinks().size());
		Link link = r.getLinks().iterator().next();
		assertEquals(SELF, link.getRel());
		assertFalse(HyperExpress.relationships().isArrayRel(Comment.class, link.getRel()));
		assertFalse(HyperExpress.relationships().isCollectionArrayRel(Comment.class, link.getRel()));
		assertFalse(r.isMultipleLinks(link.getRel()));
		assertFalse(r.isMultipleResources(link.getRel()));
		assertEquals("/admin-role", link.getHref());		
	}

	@Test
	public void shouldContainOptionallyBoundLinksWithString()
	{
		HyperExpress.bind("adminRole", "admin")
			.bind("blogId", "42")
			.bind("entryId", "33");
		Resource r = HyperExpress.createResource(new Entry(), "*");
		assertNotNull(r.getLinks());
		assertEquals(3, r.getLinks().size());
		Iterator<Link> iter = r.getLinks().iterator();

		while(iter.hasNext())
		{
			Link link = iter.next();

			switch (link.getRel())
            {
				case SELF:
					assertEquals("/entries/33", link.getHref());
					assertFalse(r.isMultipleLinks(link.getRel()));
					assertFalse(r.isMultipleResources(link.getRel()));
					break;

				case "edit":
					assertEquals("/entries/33/edit", link.getHref());
					assertFalse(r.isMultipleLinks(link.getRel()));
					assertFalse(r.isMultipleResources(link.getRel()));
					break;

				case ALTERNATE:
					assertEquals("/blogs/42/entries/33", link.getHref());
					assertTrue(r.isMultipleLinks(ALTERNATE));
					assertFalse(r.isMultipleResources(ALTERNATE));
					break;

				default:
					fail("Unexpected 'rel' " + link.getRel());
					break;
			}
		}

		r = HyperExpress.createResource(new Comment(), "*");
		assertNotNull(r.getLinks());
		assertEquals(1, r.getLinks().size());
		Link link = r.getLinks().iterator().next();
		assertEquals(SELF, link.getRel());
		assertFalse(HyperExpress.relationships().isArrayRel(Comment.class, link.getRel()));
		assertFalse(HyperExpress.relationships().isCollectionArrayRel(Comment.class, link.getRel()));
		assertFalse(r.isMultipleLinks(link.getRel()));
		assertFalse(r.isMultipleResources(link.getRel()));
		assertEquals("/admin-role", link.getHref());		
	}

	@Test
	public void shouldContainOptionalLinksWithMultipleBindings()
	{
		HyperExpress.bind("adminRole", "admin")
			.bind("superUser", "true");

		Resource r = HyperExpress.createResource(new Comment(), "*");
		assertNotNull(r.getLinks());
		assertEquals(1, r.getLinks().size());
		Link link = r.getLinks().iterator().next();
		assertEquals(SELF, link.getRel());
		assertFalse(HyperExpress.relationships().isArrayRel(Comment.class, link.getRel()));
		assertFalse(HyperExpress.relationships().isCollectionArrayRel(Comment.class, link.getRel()));
		assertFalse(r.isMultipleLinks(link.getRel()));
		assertFalse(r.isMultipleResources(link.getRel()));
		assertEquals("/superuser-role", link.getHref());		
	}

	@Test
	public void shouldHandleCollection()
	{
		ArrayList<Blog> blogs = new ArrayList<>();
		Resource r = HyperExpress.createCollectionResource(blogs, Blog.class, "*");
		assertNotNull(r);
	}

	@Test
	public void shouldCreateEmptyResourceFromResource()
	{
		Resource r = HyperExpress.createResource(new AgnosticResource(), "*");
		assertEmptyResource(r);
	}

	@Test
	public void shouldCreateResourcesFromObjectCollection()
	{
		Collection<Entry> resources = new ArrayList<Entry>();
		resources.add(new Entry());
		Collection<Resource> c = HyperExpress.createResources(resources, Entry.class, "*");
		assertNotNull(c);
		assertEquals(1, c.size());
		Resource r = c.iterator().next();
		assertTrue(r.hasNamespaces());
		assertEquals(2, r.getNamespaces().size());
		assertTrue(r.hasLinks());
		assertEquals(1, r.getLinks().size());
		assertEquals("self", r.getLinks().get(0).getRel());
		assertEquals("/entries/{entryId}", r.getLinks().get(0).getHref());
		assertFalse(r.hasProperties());
		assertFalse(r.hasResources());
	}

	@Test
	public void shouldCreateResourcesFromResourceCollection()
	{
		Collection<Resource> resources = new ArrayList<Resource>();
		resources.add(HyperExpress.createResource(new Entry(), "*"));
		Collection<Resource> c = HyperExpress.createResources(resources, Entry.class, "*");
		assertNotNull(c);
		assertEquals(1, c.size());
		Resource r = c.iterator().next();
		assertTrue(r.hasNamespaces());
		assertEquals(2, r.getNamespaces().size());
		assertTrue(r.hasLinks());
		assertEquals(1, r.getLinks().size());
		assertEquals("self", r.getLinks().get(0).getRel());
		assertEquals("/entries/{entryId}", r.getLinks().get(0).getHref());
		assertFalse(r.hasProperties());
		assertFalse(r.hasResources());
	}

	@Test
	public void shouldCreateCollectionResourceFromResourceCollection()
	{
		Collection<Resource> resources = new ArrayList<Resource>();
		resources.add(new AgnosticResource());
		Resource r = HyperExpress.createCollectionResource(resources, Blog.class, "blogs", "*");
		assertNotNull(r);
		assertTrue(r.hasNamespaces());
		assertEquals(2, r.getNamespaces().size());
		assertTrue(r.hasLinks());
		assertEquals(1, r.getLinks().size());
		assertEquals("self", r.getLinks().get(0).getRel());
		assertEquals("/blogs", r.getLinks().get(0).getHref());
		assertFalse(r.hasProperties());
		assertTrue(r.hasResources());
		assertEquals(1, r.getResources("blogs").size());
		assertEmptyResource(r.getResources("blogs").get(0));
	}

	@Test
	public void shouldCreateGenericCollection()
	{
		List<?> entities = getEntities();
		Resource resource = HyperExpress.createCollectionResource(entities, getType(), "*");
	}

	private List<?> getEntities()
	{
		return new ArrayList<Blog>();
	}

	private Class<?> getType()
	{
		return Blog.class;
	}

	@Test
	public void shouldReturnResourceClass()
	{
		Class<?> type = HyperExpress.getResourceType("*");
		assertEquals(AgnosticResource.class, type);
	}

	@Test
	public void shouldSupportMultipleLinksPerRel()
	{
		Resource r = HyperExpress.createCollectionResource(new ArrayList<Comment>(), Comment.class, "comments", "*");
		assertNotNull(r);
		assertTrue(r.hasNamespaces());
		assertEquals(2, r.getNamespaces().size());
		assertTrue(r.hasLinks());
		assertEquals(2, r.getLinks().size());
	}
	private void assertEmptyResource(Resource r)
	{
		assertNotNull(r);
		assertTrue(r.hasNamespaces());
		assertEquals(2, r.getNamespaces().size());
		assertFalse(r.hasLinks());
		assertFalse(r.hasProperties());
		assertFalse(r.hasResources());
	}
}
