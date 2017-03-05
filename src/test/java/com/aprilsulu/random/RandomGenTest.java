package com.aprilsulu.random;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Unit tests for random generator
 * The test strategy here is to prove the algorithm is correct instead of proving randomness. (Assume Random.nextFloat() generates random number in between 0 and 1)
 * Otherwise if we really want to test randomness, tests may include:
 * 1) Frequency and chi-square test (I added in this test class)
 * 2) Wilcoxon rank test in between ith and jth range to prove independence. 
 * There are other known randomness tests that are not suitable for unit tests e.g. DieHard and NIST test groups.
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
	
	//Assuming Random.nextFloat() generates random number in between 0 and 1,
	// test that the probabilities of generating numbers are as expected
	@Test
	public void testAlgorithm()
	{
		final int[] randomNums = { -3, 0, 1, 2, 5 };
		final float[] possibilities = new float[] { 0.01f, 0.3f, 0.58f, 0.1f,
				0.01f };
		final RandomGen randomGen = new RandomGen(randomNums, possibilities);
		
		//0 - 0.01 (probability = 0.01) gives -3
		assertEquals(-3,randomGen.nextNumFromRandom(0.f));
		assertEquals(-3,randomGen.nextNumFromRandom(0.0001f));
		assertEquals(-3,randomGen.nextNumFromRandom(0.005f));
		assertEquals(-3,randomGen.nextNumFromRandom(0.01f));
		
		//0.01-0.31 (probability = 0.3) gives 0
		assertEquals(0,randomGen.nextNumFromRandom(0.15f));
		assertEquals(0,randomGen.nextNumFromRandom(0.3f));
		assertEquals(0,randomGen.nextNumFromRandom(0.309f));
		assertEquals(0,randomGen.nextNumFromRandom(0.31f));

		//0.31-0.89 (probability = 0.58) gives 1
		assertEquals(1,randomGen.nextNumFromRandom(0.311f));
		assertEquals(1,randomGen.nextNumFromRandom(0.6f));
		assertEquals(1,randomGen.nextNumFromRandom(0.88f));
		assertEquals(1,randomGen.nextNumFromRandom(0.89f));

		//0.89-0.99 (probability = 0.1) gives 2
		assertEquals(2,randomGen.nextNumFromRandom(0.891f));
		assertEquals(2,randomGen.nextNumFromRandom(0.95f));
		assertEquals(2,randomGen.nextNumFromRandom(0.989f));
		assertEquals(2,randomGen.nextNumFromRandom(0.99f));

		//0.99-1 (probability = 0.01) gives 5
		assertEquals(5,randomGen.nextNumFromRandom(0.991f));
		assertEquals(5,randomGen.nextNumFromRandom(0.995f));
		assertEquals(5,randomGen.nextNumFromRandom(0.999f));
		assertEquals(5,randomGen.nextNumFromRandom(1.f));
	}

	@Test
	public void testDistribution()
	{
		final int[] randomNums = { -3, 0, 1, 2, 5 };
		final float[] possibilities = new float[] { 0.01f, 0.3f, 0.58f, 0.1f,
				0.01f };
		final RandomGen randomGen = new RandomGen(randomNums, possibilities);
		final long generateTimes = 10000000;
		final Map<Integer, Integer> counts = generateMap(randomGen,generateTimes);

		final double threshold = generateTimes*0.001;
		for (int i = 0; i < possibilities.length; i++)
		{
			assertEquals(possibilities[i]*generateTimes, counts.get(randomNums[i]), threshold);
		}
	}
	
	//Goodness of fit
	//Testing a null hypothesis that the sample being tested follows a specified distribution
	@Test
	public void testChiSquare()
	{
		final int[] randomNums = { -3, 0, 1, 22, 5, 2, 54, 23, 653, 12,7, 45};
		final float[] possibilities = new float[] { 0.01f, 0.3f, 0.058f, 0.1f, 0.023f, 0.07f, 0.0156f, 0.3f, 0.08f, 0.031f, 0.0024f,
				0.01f };
		final RandomGen randomGen = new RandomGen(randomNums, possibilities);
		final long generateTimes = 10000000;
		final Map<Integer, Integer> counts = generateMap(randomGen,generateTimes);
		
		final double[] expected = new double[12];
		final long[] observed = new long[12];
		for(int i =0;i<12;i++)
		{
			expected[i] = possibilities[i]*generateTimes;
			observed[i] =  counts.get(randomNums[i]);
		}

		final ChiSquareTest test = new ChiSquareTest();
		assertFalse(test.chiSquareTest(expected, observed, 0.01));
	}
	
	// Store counts for generated numbers
	private static Map<Integer, Integer> generateMap(final RandomGen randomGen, long generateTimes)
	{
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
		return counts;
	}
}
