package com.strategicgains.hyperexpress;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.strategicgains.hyperexpress.expand.Expansion;

public class ExpansionTest
{

	@Test
	public void shouldReturnFalseOnEmptyRels()
	{
		Expansion expansion = new Expansion("*");
		assertFalse(expansion.contains("anything"));
	}

}
