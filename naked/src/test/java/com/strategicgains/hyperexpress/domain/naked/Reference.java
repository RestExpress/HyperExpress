package com.strategicgains.hyperexpress.domain.naked;

import java.util.UUID;

import com.strategicgains.hyperexpress.domain.ReferenceDefinition;

public class Reference<T extends AbstractEntity>
extends ReferenceDefinition<UUID>
{
	public Reference(T entity)
	{
		super(entity.getId());
	}
}
