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

import java.util.Collection;
import java.util.List;

/**
 * TokenResolver is a utility class that replaces tokens (e.g. '{tokenName}')
 * in strings with values. It allows the addition of TokenBinder instances,
 * which are simply callbacks that can extract token values from Object
 * instances before replacing tokens in a string.
 * 
 * @author toddf
 * @since Apr 28, 2014
 */
public interface TokenResolver
{
	/**
	 * Bind a single value to a token name.
	 * 
	 * @param tokenName the name of the token.
	 * @param value the value to be substituted for the token.
	 * @return this token resolver to facilitate method chaining.
	 */
	public TokenResolver bind(String tokenName, String value);

	/**
	 * Bind multiple values to a single token name.
	 * 
	 * @param tokenName the name of the token.
	 * @param multiValues the values to be substituted for the token.
	 * @return this token resolver to facilitate method chaining.
	 */
	public TokenResolver bind(String tokenName, String... multiValues);


	/**
	 * Bind multiple values to a single token name.
	 * 
	 * @param tokenName the name of the token.
	 * @param multiValues the values to be substituted for the token.
	 * @return this token resolver to facilitate method chaining.
	 */
	public TokenResolver bind(String tokenName, List<String> multiValues);

	/**
	 * Removes all bound tokens. Does not remove token binder callbacks.
	 */
	public void clear();

	/**
	 * 'Unbind' a named substitution value from a token name.
	 * 
	 * @param tokenName the name of a previously-bound token name.
	 */
	public void remove(String tokenName);

	/**
	 * Returns true if the given tokenName has an existing binding.
	 * 
	 * @param tokenName the name of a token.
	 * @return true if the token is bound in this TokenResolver. Otherwise, false.
	 */
	public boolean contains(String tokenName);

	/**
	 * Install a callback TokenBinder instance. During the resolve() methods that
	 * take an Object instance such as, resolve(String, Object) and
	 * resolve(Collection<String>, Object), the TokenBinder.bind(Object) method
	 * is called to bind additional tokens that may come from the object.
	 * <p/>
	 * <Strong>LIMITATION:</strong> As TokenBinder is typed, calling binder()
	 * with TokenBinder instances for different generic types will cause
	 * a {@link ClassCastException} during resolve().
	 * 
	 * @param callback a TokenBinder implementation.
	 * @return this instance of TokenResolver to facilitate method chaining.
	 */
	public <T> TokenResolver binder(TokenBinder<T> callback);

	/**
	 * Removes all token binder callbacks from this TokenResolver.
	 */
	public void clearBinders();

	/**
	 * Removes all bound tokens and token binder callbacks from this
	 * TokenResolver, making it essentially empty. After reset() this
	 * TokenResolver's state is as if it was newly instantiated.
	 */
	public void reset();

	/**
	 * Resolve the tokens in the pattern string.
	 * 
	 * @param pattern
	 * @return the pattern with tokens resolved
	 */
	public String resolve(String pattern);

	public String[] resolveMulti(String pattern);

	/**
	 * Resolve the tokens in a string pattern, binding additional token values from
	 * the given Object first. Any TokenBinder callbacks are called for the
	 * object before resolving the tokens. If object is null, no token binders
	 * are called.
	 * <p/>
	 * <Strong>LIMITATION:</strong> As TokenBinder is typed, calling binder()
	 * with TokenBinder instances for different generic types will cause
	 * a {@link ClassCastException} during resolve().
	 * 
	 * @param pattern a pattern string optionally containing tokens.
	 * @param object an instance for which to call TokenBinders.
	 * @return a string with bound tokens substituted for values.
	 * @throws ClassCastException if binder() called with TokenBinder instances for different generic types.
	 */
	public String resolve(String pattern, Object object);

	/**
	 * Resolve the tokens in the collection of string patterns, returning a
	 * collection of resolved strings. The resulting strings may still contain tokens
	 * if they do not have token values bound.
	 * 
	 * @param patterns a list of string patterns
	 * @return a collection of strings with bound tokens substituted for values.
	 */
	public Collection<String> resolve(Collection<String> patterns);

	/**
	 * Resolve the tokens in a collection of string patterns, binding additional
	 * token values from the given Object first. Any TokenBinder callbacks are
	 * called for the object before resolving the tokens. If object is null, no
	 * token binders are called. The resulting strings may still contain tokens if
	 * they do not have values bound.
	 * 
	 * @param patterns a collection of string patterns optionally containing tokens.
	 * @param object an instance for which to call TokenBinders.
	 * @return a collection of strings with bound tokens substituted for values.
	 */
	public Collection<String> resolve(Collection<String> patterns, Object object);
}
