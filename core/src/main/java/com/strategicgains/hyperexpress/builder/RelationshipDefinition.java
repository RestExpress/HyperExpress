/*
    Copyright 2014, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.strategicgains.hyperexpress.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.strategicgains.hyperexpress.BuilderFactory;
import com.strategicgains.hyperexpress.domain.Namespace;
import com.strategicgains.hyperexpress.exception.RelationshipException;
import com.strategicgains.hyperexpress.util.Strings;

/**
 * @author toddf
 * @since Apr 8, 2014
 */
public class RelationshipDefinition
{
	private static final String COLLECTION_SUFFIX = ".Collection";
	private static final String TITLE = "title";
	private static final String TYPE = "type";
	private static final String HREFLANG = "hreflang";

	// HAL-specific
	private static final String TEMPLATED = "templated";

	// HAL-specific
	private static final String DEPRECATION = "deprecation";

	// HAL-Specific
	private static final String NAME = "name";

	// HAL-Specific
	private static final String PROFILE = "profile";

	// Atom-specific
	private static final String LENGTH = "length";

	private BuilderFactory factory = new DefaultBuilderFactory();
	private Map<String, Namespace> namespaces = new LinkedHashMap<>();
	private Map<String, Set<String>> arrayRelsByClass = new HashMap<String, Set<String>>();
	private Map<String, List<ConditionalLinkBuilder>> linkBuildersByClass = new LinkedHashMap<>();
	private List<ConditionalLinkBuilder> linkBuildersForClass;
	private ConditionalLinkBuilder linkBuilder;
	private Set<String> arrayRels;
	private String lastClassName;
	private Map<String, String> relNamesByClass = new HashMap<String, String>();

	
	public RelationshipDefinition builderFactory(BuilderFactory factory)
	{
		this.factory = factory;
		return this;
	}

	/**
	 * Adds one or more namespaces to this relationship builder.
	 * 
	 * @param namespaces one or more Namespace instances.
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition addNamespaces(Namespace... namespaces)
	{
		if (namespaces == null) return this;

		for (Namespace namespace : namespaces)
		{
			addNamespace(namespace);
		}

		return this;
	}

	/**
	 * Add a single Namespace to this relationship builder.
	 * 
	 * @param namespace a Namespace. Cannot be null.
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition addNamespace(Namespace namespace)
	{
		if (namespace == null) return this;

		if (namespaces.containsKey(namespace.name()))
		{
			throw new RelationshipException("Duplicate namespace: " + namespace.name());
		}

		namespaces.put(namespace.name(), namespace.clone());
		return this;
	}

	public RelationshipDefinition forCollectionOf(Class<?> forClass)
	{
		if (forClass == null) return this;

		return forClassName(forClass.getName() + COLLECTION_SUFFIX);
	}

	public RelationshipDefinition asRel(String name)
	{
		if (lastClassName == null)
		{
			throw new RelationshipException("Attempt to call asRel() before forCollectionOf()");
		}

		relNamesByClass.put(lastClassName, name);
		return this;
	}

	/**
	 * Returns a 'rel' name for a collection of the given class. If set via asRel(),
	 * that name is returned. Otherwise, a pluralized form of the given class name
	 * is used. In the later case, the simple name is pluralized, set to lower-case.
	 * 
	 * @param forClass
	 * @return
	 */
	public String getCollectionRelFor(Class<?> forClass)
	{
		String rel = relNamesByClass.get(forClass.getName() + COLLECTION_SUFFIX);

		if (rel == null)
		{
			rel = Strings.pluralize(((Class<?>) forClass).getSimpleName().toLowerCase());
		}

		return rel;
	}

	public RelationshipDefinition forClass(Class<?> forClass)
	{
		if (forClass == null) return this;

		return forClassName(forClass.getName());
	}

	private RelationshipDefinition forClassName(String name)
	{
		if (name == null) return this;

		lastClassName = name;
		linkBuildersForClass = linkBuildersByClass.get(name);

		if (linkBuildersForClass == null)
		{
			linkBuildersForClass = new ArrayList<>();
			linkBuildersByClass.put(name, linkBuildersForClass);
		}

		arrayRels = arrayRelsByClass.get(name);

		if (arrayRels == null)
		{
			arrayRels = new HashSet<String>();
			arrayRelsByClass.put(name, arrayRels);
		}

		return this;
	}

	/**
	 * Define a relationship for the given rel name to a URL.
	 * 
	 * @param rel the relationship name (rel name).
	 * @param href the URL, possibly templated.
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition rel(String rel, String href)
	{
		return rel(rel, factory.newLinkBuilder(href));
	}

	/**
	 * Define a relationship for the give rel name to a LinkBuilder.
	 * 
	 * @param rel the relationship name (rel name).
	 * @param builder an OptionalLinkBuilder instance.
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition rel(String rel, ConditionalLinkBuilder builder)
	{
		builder.rel(rel);
		this.linkBuilder = builder;

		if (linkBuildersForClass == null)
		{
			throw new RelationshipException("Attempt to call rel() before forClass() or forCollectionOf()");
		}

		linkBuildersForClass.add(builder);
		return this;
	}

	/**
	 * Define a relationship for the given rel name to a URL. If supported,
	 * the output rendering (serialization) will be an array (or list) instead
	 * of a single object for this rel name.
	 * 
	 * @param name the relationship name (rel name).
	 * @param href the URL, possibly templated.
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition rels(String name, String href)
	{
		return rels(name, factory.newLinkBuilder(href));
	}

	/**
	 * Define a relationship for the give rel name to a LinkBuilder. If supported,
	 * the output rendering (serialization) will be an array (or list) instead
	 * of a single object for this rel name.
	 * 
	 * @param name the relationship name (rel name).
	 * @param builder an OptionalLinkBuilder instance.
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition rels(String name, ConditionalLinkBuilder builder)
	{
		arrayRels.add(name);
		return rel(name, builder);
	}

	/**
	 * Set the title on the latest rel().
	 * 
	 * @param title
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition title(String title)
	{
		return attribute(TITLE, title);
	}

	/**
	 * Set the 'hreflang' property on the latest rel().
	 * 
	 * @param value
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition hreflang(String value)
	{
		return attribute(HREFLANG, value);
	}

	/**
	 * Set the 'type' property on the latest rel().
	 * 
	 * @param type
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition type(String type)
    {
    	return attribute(TYPE, type);
    }

	/**
	 * HAL-specific. Set the 'name' property of the latest rel().
	 * 
	 * @param name
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition name(String name)
	{
		return attribute(NAME, name);
	}

	/**
	 * HAL-specific. Set the 'templated' property of the latest rel().
	 * 
	 * @param value
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition templated(boolean value)
	{
		if (value)
		{
			return attribute(TEMPLATED, Boolean.TRUE.toString());
		}

		return attribute(TEMPLATED, null);
	}

	/**
	 * HAL-specific.
	 * 
	 * @param value
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition deprecation(String value)
	{
		return attribute(DEPRECATION, value);
	}

	/**
	 * HAL-specific.
	 * 
	 * @param value
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition profile(String value)
	{
		return attribute(PROFILE, value);
	}

	/**
	 * Atom-specific.
	 * 
	 * @param value
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition length(String value)
	{
		return attribute(LENGTH, value);
	}

	/**
	 * Indicates that the link should not be included in a response if it would be marked 'templated'.
	 * This is useful for links that are 'pagination' related, or otherwise conditional in the response.
	 * 
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition optional()
	{
		if (linkBuilder == null) throw new RelationshipException("Attempt to set optional on null link. Call 'rel()' first.");

		linkBuilder.optional();
		return this;
	}

	/**
	 * Indicates that the link should be included in a response if the provided token
	 * gets bound. This is useful for links that are conditional in the response depending
	 * on information NOT in the link URL.
	 * <p/>
	 * For example: definition.optional("{adminRole}");
	 * <p/>
	 * Then for binding, anything other than "false" or null will include the link in the output.
	 * <p/>
	 * For example: HyperExpress.bind("adminRole", (role.equals("admin") ? "true" : "false"));<br/>
	 * or HyperExpress.bind("adminRole", (role.equals("admin") ? "admin" : null));
	 * 
	 * @param token a URL token name, with or without beginning and ending curly-braces.
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition ifBound(String token)
	{
		if (linkBuilder == null) throw new RelationshipException("Attempt to set ifBound() on null link: " + token + ". Call 'rel()' first.");

		linkBuilder.ifBound(token);
		return this;
	}

	/**
	 * Indicates that the link should be included in a response if the provided token
	 * gets bound and matches the given predicate. This is useful for links that are
	 * conditional in the response depending on information NOT in the link URL.
	 * <p/>
	 * For example: definition.ifBound("{role}", r -> "superadmin".equals(r);
	 * <p/>
	 * Then for binding, anything other than "false" or null will include the link in the output.
	 * <p/>
	 * For example:<br/>
	 * String role = "superadmin";<br/>
	 * HyperExpress.bind("role", role);<br/>
	 * 
	 * Now this link will only be included for resources where the 'role' token gets
	 * bound to the value 'superadmin'.
	 * 
	 * @param token a URL token name, with or without beginning and ending curly-braces.
	 * @param predicate the predicate to test on the value of the bound token.
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition ifBound(String token, Predicate<String> predicate)
	{
		if (linkBuilder == null) throw new RelationshipException("Attempt to set ifBound() on null link: " + token + ". Call 'rel()' first.");

		linkBuilder.ifBound(token, predicate);
		return this;
	}

	public RelationshipDefinition ifNotBound(String token)
	{
		if (linkBuilder == null) throw new RelationshipException("Attempt to set ifNotBound() on null link: " + token + ". Call 'rel()' first.");

		linkBuilder.ifNotBound(token);
		return this;
	}

	/**
	 * General-purpose. Can be used to set arbitrary string-value properties on the link definition.
	 * May not actually show up in the output depending on whether the out-bound link format supports
	 * the attribute.
	 * </p>
	 * Must call 'rel()' before attribute() is called to create a new link.
	 * 
	 * @param name
	 * @param value
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition attribute(String name, String value)
    {
		if (linkBuilder == null) throw new RelationshipException("Attempt to set attribute on null link: " + name + ". Call 'rel()' first.");

		linkBuilder.set(name, value);
    	return this;
    }

	/**
	 * Add an optional query-string argument to the latest rel(). If the query-string segment contains
	 * tokens and is not fully populate, it will not appear in the generated link.
	 * 
	 * @param querySegment an optional query-string segment. Optionally contains tokens.
	 * @return this relationship definition instance to facilitate method chaining.
	 */
	public RelationshipDefinition withQuery(String querySegment)
	{
		if (linkBuilder == null) throw new RelationshipException("Attempt to set query-string segment on null link: " + querySegment + ". Call 'rel()' first.");

		linkBuilder.withQuery(querySegment);
		return this;
	}

	public List<LinkBuilder> getLinkBuilders(Class<?> forClass)
	{
		if (forClass == null) return Collections.emptyList();

		if (forClass.isArray())
		{
			return getLinkBuildersForName(forClass.getComponentType().getName() + COLLECTION_SUFFIX);
		}

		return getLinkBuildersForName(forClass.getName());
	}

	public List<LinkBuilder> getCollectionLinkBuilders(Class<?> componentType)
	{
		if (componentType == null) return Collections.emptyList();

		return getLinkBuildersForName(componentType.getName() + COLLECTION_SUFFIX);
	}

	public Map<String, Namespace> getNamespaces()
	{
		return Collections.unmodifiableMap(namespaces);
	}

	public boolean isArrayRel(Class<?> objectType, String rel)
	{
		return isArrayRel(objectType.getName(), rel);
	}

	public boolean isCollectionArrayRel(Class<?> objectType, String rel)
	{
		return isArrayRel(objectType.getName() + COLLECTION_SUFFIX, rel);
	}

	private boolean isArrayRel(String className, String rel)
	{
		Set<String> rels = arrayRelsByClass.get(className);
		return (rels == null ? false : rels.contains(rel));
	}

    private List<LinkBuilder> getLinkBuildersForName(String className)
	{
		List<ConditionalLinkBuilder> builders = linkBuildersByClass.get(className);

		return (builders != null 
			? Collections.<LinkBuilder> unmodifiableList(builders)
			: Collections.<LinkBuilder> emptyList());
	}
}
