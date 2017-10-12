/*
    Copyright 2015, Strategic Gains, Inc.

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
package com.strategicgains.hyperexpress.annotation;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.strategicgains.hyperexpress.builder.TokenBinder;
import com.strategicgains.hyperexpress.builder.TokenResolver;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.exception.ResourceException;
import com.strategicgains.hyperexpress.util.Objects;

/**
 * Binds tokens from a POJO annotated with {@link TokenBindings} and for those fields
 * annotated with {@link BindToken}. The singleton instance() method may be used to
 * optimize the usage of TokenFormatter instances, since they get cached after usage.
 * 
 * Usage can be as simple as adding the singleton instance to a {@link TokenResolver}
 * TokenResolver tr = new TokenResolver();
 * tr.binder(AnnotationTokenBinder.intstance());
 * tr.resolve(pattern, object);
 * 
 * @author toddf
 * @since Aug 25, 2015
 */
public class AnnotationTokenBinder
implements TokenBinder<Object>
{
	/**
	 * A general usage Singleton instance of this class.
	 */
	private static final AnnotationTokenBinder INSTANCE = new AnnotationTokenBinder();

	/**
	 * Each {@link BindToken} annotation has a TokenFormatter declared (which is defaulted to
	 * {@link ToStringFormatter}. Once newInstance() is called on a formatter, it is cached
	 * here to speed up usage for any other annotations using the same formatter.
	 */
	private Map<Class<?>, TokenFormatter> formatters = new ConcurrentHashMap<>();

	/**
	 * Acquire the Singleton instance of the AnnotationTokenBinder. This may be used to optimize
	 * the usage of TokenFormatter instances, since they get cached after usage.
	 * 
	 * @return
	 */
	public static AnnotationTokenBinder instance()
	{
		return INSTANCE;
	}

	/**
	 * Bind fields annotated with {@link BindToken} into the given TokenResolver.
	 */
	@Override
	public void bind(Object object, TokenResolver resolver)
	{
		bindProperties(object.getClass(), object, resolver);
	}

	/**
	 * Recursively binds properties from the Object, up the inheritance hierarchy, to
	 * the destination resolver.
	 * 
	 * @param type the Type of the object being bound. May be a super-type of the 'from' object.
	 * @param from the instance of Object being bound.
	 * @param to the destination TokenResolver instance.
	 */
	private void bindProperties(Class<?> type, Object from, TokenResolver resolver)
	{
		if (type == null) return;
		if (Resource.class.isAssignableFrom(type))
		{
			return;
		}

		performClassBindings(from, resolver);
		Field[] fields = type.getDeclaredFields();

		try
		{
			for (Field f : fields)
			{
				BindToken annotation = f.getAnnotation(BindToken.class);

				if (annotation != null)
				{
					f.setAccessible(true);
					Object value = f.get(from);

					if (hasPropertyPath(annotation))
					{
						value = Objects.property(annotation.field(), value);
					}
					
					bindAnnotatedProperty(annotation, value, resolver);
				}
			}
		}
		catch (IllegalAccessException | InstantiationException | NoSuchFieldException | IllegalArgumentException e)
		{
			throw new ResourceException(e);
		}

		bindProperties(type.getSuperclass(), from, resolver);
	}

	private void performClassBindings(Object from, TokenResolver resolver)
	{
		if (from == null) return;

		TokenBindings bindings = from.getClass().getAnnotation(TokenBindings.class);
		
		if (bindings == null) return;

		for (BindToken binding : bindings.value())
		{
			if (hasPropertyPath(binding))
			{
				try
				{
					Object value = Objects.property(binding.field(), from);
					bindAnnotatedProperty(binding, value, resolver);
				}
				catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException e)
				{
					throw new ResourceException(e);
				}
			}
			else
			{
				throw new ResourceException("Class-level BindToken annotations require 'field' path.");				
			}
		}
	}

	private boolean hasPropertyPath(BindToken annotation)
	{
		return !"".equals(annotation.field());
	}

	private void bindAnnotatedProperty(BindToken annotation, Object field, TokenResolver resolver)
	throws InstantiationException, IllegalAccessException
	{
		if (field == null)
		{
			resolver.remove(annotation.value());
			return;
		}

		Class<? extends TokenFormatter> formatterClass = annotation.formatter();
		TokenFormatter formatter = formatters.get(formatterClass);

		if (formatter == null)
		{
			formatter = formatterClass.newInstance();
			formatters.put(formatterClass, formatter);
		}

		resolver.bind(annotation.value(), formatter.format(field));
	}
}
