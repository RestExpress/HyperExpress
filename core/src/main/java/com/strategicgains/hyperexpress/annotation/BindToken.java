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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author toddf
 * @since Feb 5, 2015
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface BindToken
{
	/**
	 * The name of the token in the URL
	 */
	String value();

	/**
	 * The dot-separated path to the field in the annotated object.
	 */
	String field() default "";

	/**
	 * The {@link TokenFormatter} to use when converting this property to a string.
	 * Otherwise, toString() will be used.
	 */
	Class<? extends TokenFormatter> formatter() default ToStringFormatter.class;
}
