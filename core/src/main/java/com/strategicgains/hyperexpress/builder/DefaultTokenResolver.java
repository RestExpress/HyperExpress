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

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.strategicgains.hyperexpress.annotation.AnnotationTokenBinder;
import com.strategicgains.hyperexpress.annotation.BindToken;
import com.strategicgains.hyperexpress.util.MapStringFormat;
import com.strategicgains.hyperexpress.util.Strings;

/**
 * DefaultTokenResolver is a utility class that replaces tokens (e.g. '{tokenName}')
 * in strings with values. It allows the addition of TokenBinder instances,
 * which are simply callbacks that can extract token values from Object
 * instances before replacing tokens in a string.
 * 
 * This class is not thread safe as object values are bound into it before
 * resolving tokens in URL(s).
 * 
 * @author toddf
 * @since Apr 28, 2014
 */
public class DefaultTokenResolver
implements TokenResolver
{
	private static final MapStringFormat FORMATTER = new MapStringFormat();

	private LinkedList<ScopeBindings> scopeStack = new LinkedList<>();
	private ScopeBindings globals = new ScopeBindings();
	private ScopeBindings scope = globals;
	private List<TokenBinder<?>> binders = new ArrayList<>();
	private boolean bindAnnotations = false;

	/**
	 * Maintains historical behavior that does not bind via Annotations.
	 * Same as DefaultTokenResolver(false).
	 */
	public DefaultTokenResolver()
	{
		this(false);
	}

	/**
	 * When bindAnnotations is true, inserts a TokenBinder that will bind an object's properties
	 * to tokens via the {@link BindToken} annotation.
	 * 
	 * @param bindAnnotations
	 */
	public DefaultTokenResolver(boolean bindAnnotations)
	{
		super();
		this.bindAnnotations = bindAnnotations;
		initialize();
	}

	private void initialize()
	{
		if (bindAnnotations)
		{
			binder(AnnotationTokenBinder.instance());
		}
	}

	/**
	 * Bind a token to a value. During resolve(), any token names matching
	 * the given token name here will be replaced with the given value.
	 * 
	 * Set a value to be substituted for a token in the string pattern. While
	 * tokens in the pattern are delimited with curly-braces, the token name
	 * does not contain the braces. The value is any string value.
	 * 
	 * @param tokenName the name of a token in the string pattern.
	 * @param value the string value to substitute for the token name in the URL pattern.
	 * @return this TokenResolver instance to facilitate method chaining.
	 */
	public DefaultTokenResolver bind(String tokenName, String value)
	{
		scope.bind(tokenName, value);
		return this;
	}

	public DefaultTokenResolver bind(String tokenName, String... multiValues)
	{
		if (multiValues == null || multiValues.length == 0)
		{
			remove(tokenName);
		}
		else
		{
			bind(tokenName, Arrays.asList(multiValues));
		}

		return this;
	}

	public DefaultTokenResolver bind(String tokenName, List<String> multiValues)
	{
		if (multiValues == null || multiValues.isEmpty())
		{
			remove(tokenName);
		}
		else
		{
			bind(tokenName, multiValues.get(0));
			bindExtras(tokenName, multiValues);
		}

		return this;
	}

	private void bindExtras(String tokenName, List<String> valueList)
    {
		scope.bindExtras(tokenName, valueList);
    }

	/**
	 * Removes all bound tokens. Does not remove token binder callbacks.
	 */
	public void clear()
	{
		scope.clear();
	}

	/**
	 * 'Unbind' a named substitution value from a token name.
	 * 
	 * @param tokenName the name of a previously-bound token name.
	 */
	public void remove(String tokenName)
	{
		scope.remove(tokenName);
	}

	/**
	 * Returns true if the given tokenName has an existing binding.
	 * 
	 * @param tokenName the name of a token.
	 * @return true if the token is bound in this TokenResolver. Otherwise, false.
	 */
	public boolean contains(String tokenName)
	{
		return scope.contains(tokenName);
	}

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
	public <T> DefaultTokenResolver binder(TokenBinder<T> callback)
	{
		if (callback == null) return this;

		binders.add(callback);
		return this;
	}

	/**
	 * Removes all user set token binder callbacks from this TokenResolver.
	 * If the internal setting 'bindAnnotations' is true, the annotation
	 * token binder is not removed.
	 */
	public void clearBinders()
	{
		binders.clear();
		initialize();
	}

	/**
	 * Removes all bound tokens and token binder callbacks from this
	 * TokenResolver, making it essentially empty. After reset() this
	 * TokenResolver's state is as if it was newly instantiated.
	 */
	public void reset()
	{
		clear();
		clearBinders();
	}

	/**
	 * Resolve the tokens in the pattern string.
	 * 
	 * @param pattern
	 * @return
	 */
	@Override
	public String resolve(String pattern)
	{
		return FORMATTER.format(pattern, scope.values);
	}

	@Override
	public String[] resolveMulti(String pattern)
	{
		List<String> resolved = new ArrayList<String>();
		String current = FORMATTER.format(pattern, scope.values);
		resolved.add(current);

		for(Entry<String, Set<String>> entry : scope.multiValues.entrySet())
		{
			String firstValue = scope.values.get(entry.getKey());
			Iterator<String> nextValue = entry.getValue().iterator();

			while(nextValue.hasNext())
			{
				scope.bind(entry.getKey(), nextValue.next());
				String bound = FORMATTER.format(pattern, scope.values);

				if (!Strings.hasToken(bound))
				{
					resolved.add(bound);
				}
			}

			scope.bind(entry.getKey(), firstValue);
		}

		return resolved.toArray(new String[0]);
	}

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
	public String resolve(String pattern, Object object)
	{
		if (object == null)
		{
			return FORMATTER.format(pattern, scope.values);		
		}

		pushScope();
		callTokenBinders(object);
		String url = FORMATTER.format(pattern, scope.values);
		popScope();
		return url;
	}

	/**
	 * Resolve the tokens in the collection of string patterns, returning a
	 * collection of resolved strings. The resulting strings may still contain tokens
	 * if they do not have token values bound.
	 * 
	 * @param patterns a list of string patterns
	 * @return a collection of strings with bound tokens substituted for values.
	 */
	public Collection<String> resolve(Collection<String> patterns)
	{
		List<String> resolved = new ArrayList<String>(patterns.size());

		for (String pattern : patterns)
		{
			resolved.add(resolve(pattern));
		}

		return resolved;
	}

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
	public Collection<String> resolve(Collection<String> patterns, Object object)
	{
		if (object == null)
		{
			return resolve(patterns);
		}

		pushScope();
		callTokenBinders(object);
		Collection<String> urls = resolve(patterns);
		popScope();
		return urls;
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
	    s.append("{");
		s.append("bindAnnotations=");
		s.append(bindAnnotations);

		for (Entry<String, String> entry : scope.values.entrySet())
		{
			s.append(", ");
			s.append(entry.getKey());
			s.append("=");
			s.append(entry.getValue());
		}

		return s.toString();
    }

	/**
	 * Call the installed TokenBinder instances, calling bind() for each one
	 * and passing the object so the TokenBinders can extract token values
	 * from the object.
	 * 
	 * @param object an object for which to extract token bindings.
	 */
	@SuppressWarnings({
        "rawtypes", "unchecked"
    })
    private void callTokenBinders(Object object)
	{
		if (object == null) return;

		for (TokenBinder tokenBinder : binders)
		{
			Class<?> binderClass = (Class<?>) ((ParameterizedType) tokenBinder
				.getClass()
				.getGenericInterfaces()[0])
				.getActualTypeArguments()[0];

			if (binderClass.isAssignableFrom(object.getClass()))
			{
				tokenBinder.bind(object, this);
			}
		}
	}

	/**
	 * Pushes a new object scope on the stack. A call to popScope() will remove it.
	 * 
	 * @return this token resolver to facilitate method chaining.
	 */
	private TokenResolver pushScope()
	{
		scope = new ScopeBindings(globals);
		scopeStack.push(scope);
		return this;
	}

	/**
	 * Remove all bound tokens that correspond to bindObject() calls. Does not remove
	 * values bound using bind() methods. Nor does it remove binder callbacks.
	 */
	private TokenResolver popScope()
	{
		// Remove current scope from the stack.
		if (!scopeStack.isEmpty())
		{
			scopeStack.pop();
		}

		// Reassign current scope to top of stack.
		if (!scopeStack.isEmpty())
		{
			scope = scopeStack.peek();
		}
		else scope = globals;
		return this;
	}

	private class ScopeBindings
	{
		private Map<String, String> values;
		private Map<String, Set<String>> multiValues;

		public ScopeBindings()
		{
			super();
			values = new HashMap<>();
			multiValues = new HashMap<>();
		}

		public ScopeBindings(ScopeBindings bindings)
		{
			super();
			values = new HashMap<>(bindings.values);
			multiValues = new HashMap<>(bindings.multiValues);
		}

		public void bind(String tokenName, String value)
		{
			if (value == null)
			{
				remove(tokenName);
			}
			else
			{
				values.put(tokenName, value);
			}
		}

		public void clear()
		{
			values.clear();
			multiValues.clear();
		}

		public void bindExtras(String tokenName, List<String> valueList)
	    {
			if (valueList.size() <= 1)
			{
				this.multiValues.remove(tokenName);
			}
			else
			{
				Set<String> extras = this.multiValues.get(tokenName);
		
				if (extras == null)
				{
					extras = new LinkedHashSet<String>(valueList.size() - 1);
					this.multiValues.put(tokenName, extras);
				}

				for (int i = 1; i < valueList.size(); ++i)
				{
					extras.add(valueList.get(i));
				}
			}
	    }

		public void remove(String tokenName)
		{
			values.remove(tokenName);
			multiValues.remove(tokenName);
		}

		public boolean contains(String tokenName)
		{
			if (values.containsKey(tokenName)) return true;
			return multiValues.containsKey(tokenName);
		}
	}
}
