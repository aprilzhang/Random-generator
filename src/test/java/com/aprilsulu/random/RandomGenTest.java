package com.aprilsulu.random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Unit tests for random generator
 */
public final class RandomGenTest
{
	@Test(expected = NullPointerException.class)
	public void testCreateGeneratorWithNull()
	{
		new RandomGen(null, new float[] { 0.2f, 0.5f, 0.3f });
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateGeneratorWithEmpty()
	{
		new RandomGen(new int[] {}, new float[] {});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateGeneratorWithDifferentLength()
	{
		new RandomGen(new int[] { 2 }, new float[] { 0.2f, 0.5f, 0.3f });
	}

	// Test the generated results are from the given random numbers
	@Test
	public void testWithinRange()
	{
		final int[] randomNums = { -3, 0, 1, 2, 5 };
		final ImmutableSet<Integer> numbers = ImmutableSet.of(-3, 0, 1, 2, 5);
		final RandomGen randomGen = new RandomGen(randomNums, new float[] {
				0.01f, 0.3f, 0.58f, 0.1f, 0.01f });
		for (int i = 0; i < 10000000; i++)
		{
			assertTrue(numbers.contains(randomGen.nextNum()));
		}
	}

	@Test
	public void testDistribution()
	{
		final int[] randomNums = { -3, 0, 1, 2, 5 };
		final float[] possibilities = new float[] { 0.01f, 0.3f, 0.58f, 0.1f,
				0.01f };
		final RandomGen randomGen = new RandomGen(randomNums, possibilities);

		final float generateTimes = 10000000;
		// Store counts for generated numbers
		final Map<Integer, Integer> counts = new HashMap<>();
		int number = 0;
		for (int i = 0; i < generateTimes; i++)
		{
			number = randomGen.nextNum();
			if (!counts.containsKey(number))
			{
				counts.put(number, 1);
			} else
			{
				counts.put(number, counts.get(number) + 1);
			}
		}

		final float threshold = 0.001f;
		for (int i = 0; i < possibilities.length; i++)
		{
			assertEquals(possibilities[i], counts.get(randomNums[i])
					/ generateTimes, threshold);
		}
	}
}
