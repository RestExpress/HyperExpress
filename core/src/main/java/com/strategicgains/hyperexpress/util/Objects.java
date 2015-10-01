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
package com.strategicgains.hyperexpress.util;

import java.lang.reflect.Field;

/**
 * 
 * @author toddf
 * @since Oct 1, 2015
 */
public class Objects
{
	/**
	 * Find a field in a class. Utilizes a dot-separated path to find a property (field) in the object inheritence hierarchy.
	 * 
	 * @param propertyPath a dot-separated path to the desired property.
	 * @param object the object from which to extract the field value.
	 * @return this ObjectTokenBinder for method chaining.
	 * @throws NoSuchFieldException if one of the properties in the path doesn't exist.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static Object property(String propertyPath, Object object)
	throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		String[] properties = propertyPath.split("\\.");
		Class<?> objectClass = object.getClass();
		Object current = object;

		for (String property : properties)
		{
			Field field = objectClass.getDeclaredField(property);
			field.setAccessible(true);
			objectClass = field.getType();
			current = field.get(current);
		}

		return current;
	}
}
