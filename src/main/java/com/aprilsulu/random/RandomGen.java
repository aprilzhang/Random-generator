package com.aprilsulu.random;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Random;

import com.google.common.annotations.VisibleForTesting;
/**
 * Generates random numbers based on their probabilities
 * @author yzhang
 */
public final class RandomGen
{
	private final Random random = new Random();
	// Values that may be returned by nextNum();
	private final int[] randomNums;
	// Cumulative probability of the occurrence of randomNums
	private final float[] cumulativeProbabilities;

	/**
	 * Constructor
	 *
	 * @param randomNums
	 *            not null
	 * @param probabilities
	 *            not null, and the sum should be 1
	 * @throws IllegalArgumentException
	 *             if randomNums and probabilities have difference length
	 */
	RandomGen(final int[] randomNums, final float[] probabilities)
	{
		checkNotNull(randomNums, "randomNums");
		checkNotNull(probabilities, "probabilities");
		checkArgument(randomNums.length != 0
				&& randomNums.length == probabilities.length);

		//Create cumulative probabilities array
		cumulativeProbabilities = new float[randomNums.length];
		cumulativeProbabilities[0] = probabilities[0];
		for (int i = 1; i < randomNums.length; i++)
		{
			cumulativeProbabilities[i] += cumulativeProbabilities[i - 1]
					+ probabilities[i];
		}
		checkArgument(cumulativeProbabilities[randomNums.length - 1] == 1);

		this.randomNums = randomNums;
	}

	/**
	 * Returns one of the randomNums. When this method is called multiple times
	 * over a long period, it should return the numbers roughly with the
	 * Initialized probabilities
	 */
	public int nextNum()
	{
		return nextNumFromRandom( random.nextFloat());
	}
	
	@VisibleForTesting
	int nextNumFromRandom(float random)
	{
		//Iterate through the numbers until the cumulative probability of 
		// the visited elements is greater than the random float
		for (int i = 0; i < randomNums.length; i++)
		{
			if (cumulativeProbabilities[i] >= random)
			{
				return randomNums[i];
			}
		}
		throw new AssertionError();
	}
}
