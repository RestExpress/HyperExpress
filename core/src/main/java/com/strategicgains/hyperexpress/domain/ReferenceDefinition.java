package com.strategicgains.hyperexpress.domain;

public class ReferenceDefinition<T>
implements Reference<T>
{
	private T id;

	public ReferenceDefinition()
	{
		super();
	}

	public ReferenceDefinition(T id)
	{
		super();
		setId(id);
	}

	@Override
	public ReferenceDefinition<T> clone()
	{
		return null;
	}

	@Override
	public T getId()
	{
		return id;
	}

	@Override
	public void setId(T id)
	{
		this.id = id;
	}
}
