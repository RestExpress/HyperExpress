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
import java.util.HashMap;
import java.util.Map;

import com.strategicgains.hyperexpress.builder.TokenBinder;
import com.strategicgains.hyperexpress.builder.TokenResolver;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.exception.ResourceException;

/**
 * @author toddf
 * @since Aug 25, 2015
 */
public class AnnotationTokenBinder
implements TokenBinder<Object>
{
	private Map<Class<?>, TokenFormatter> formatters = new HashMap<>();

	@Override
	public void bind(Object object, TokenResolver resolver)
	{
		copyProperties0(object.getClass(), object, resolver);
	}

	/**
	 * Recursively binds properties from the Object, up the inheritance hierarchy, to
	 * the destination resolver.
	 * 
	 * @param type the Type of the object being bound. May be a super-type of the 'from' object.
	 * @param from the instance of Object being bound.
	 * @param to the destination TokenResolver instance.
	 */
	private void copyProperties0(Class<?> type, Object from, TokenResolver resolver)
	{
		if (type == null) return;
		if (Resource.class.isAssignableFrom(type))
		{
			return;
		}

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
					
					if (value != null)
					{
						bindAnnotation(annotation, value, resolver);
					}
				}
			}
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			throw new ResourceException(e);
		}

		copyProperties0(type.getSuperclass(), from, resolver);
	}

	private void bindAnnotation(BindToken annotation, Object field, TokenResolver resolver)
	throws InstantiationException, IllegalAccessException
	{
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
