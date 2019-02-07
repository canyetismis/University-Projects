
package com.g52aim.lab04;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.genetics.PopulationReplacement;

/**
 * Trans-generational Replacement with Elitism
 * @author Warren G. Jackson
 *
 */
public class TransGenerationalReplacementWithElitistReplacement extends PopulationReplacement {

	/**
	 * Replaces the current population with the offspring and replaces the worst
	 * offspring with the best solution if the best is not contained in the offspring.
	 *
	 * @return The indices of the solutions to use in the next generation.
	 *
	 * PSEUDOCODE
	 *
	 * INPUT current_pop, offspring_pop
	 * fitnesses <- evaluate( current_pop U offspring_pop );
	 * best <- min( fitnesses );
	 * next_pop <- indicesOf( offspring_pop );
	 * IF best \notin offspring_pop THEN
	 *     next_pop.replace( worst, best );
	 * ENDIF
	 * OUTPUT: next_pop; // return the indices of the next population
	 */
	@Override
	protected int[] getNextGeneration(SAT problem, int populationSize) {

		int[] population = new int[populationSize];
		double bestFitness = Double.MAX_VALUE;
		double worstFitness = 0;
		int bestIndex = 0;
		int worstIndex = 0;

		double[] fitnessList = new double[populationSize*2];
		for(int i = 0; i < populationSize*2; i++) {
			fitnessList[i] = problem.getObjectiveFunctionValue(i);
			if(fitnessList[i] < bestFitness) {
				bestFitness = fitnessList[i];
				bestIndex = i;
			}
			if (i >= populationSize) {
				population[i-populationSize] = i;
				if(fitnessList[i] >worstFitness) {
					worstFitness = fitnessList[i];
					worstIndex = i;
				}
			}
		}
		if(bestIndex < populationSize) {
			population[worstIndex-populationSize] = bestIndex;
		}


		return population;
	}

}
