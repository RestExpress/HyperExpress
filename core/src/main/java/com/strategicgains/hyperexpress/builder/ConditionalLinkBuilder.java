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

import java.util.List;
import java.util.function.Predicate;

import com.strategicgains.hyperexpress.builder.DefaultConditionalLinkBuilder.Conditional;

/**
 * Extends LinkBuilder, adding an 'optional' flag, where the value can either be "true" or
 * a token (e.g. "{role}").  If the flag is set to true and the resulting Link that is built
 * still contains a token (a URL token is not bound), then build() returns null.
 * <p/>
 * Otherwise, if the flag is set to a token and that token doesn't get bound from the object
 * during build(), then the resulting link is not returned (it is null).
 * 
 * @author toddf
 * @since Jul 9, 2014
 */
public interface ConditionalLinkBuilder
extends LinkBuilder
{
	public void optional();

	public void ifBound(String token);

	public void ifNotBound(String token);

	public void ifBound(String token, Predicate<String> predicate);

	public boolean isOptional();

	public boolean hasConditionals();

	public List<Conditional> getConditionals();
}
