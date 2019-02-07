package com.g52aim.lab04;

import java.util.Random;

import g52aim.domains.chesc2014_SAT.SAT;

/**
 * @author Warren G. Jackson
 */
public class TournamentSelection {

	private Random rng;
	private int POPULATION_SIZE;
	private SAT problem;

	public TournamentSelection(SAT problem, Random rng, int POPULATION_SIZE) {

		this.problem = problem;
		this.rng = rng;
		this.POPULATION_SIZE = POPULATION_SIZE;
	}

	/**
	  * @return The index of the chosen parent solution.
	  *
	  * PSEUDOCODE
	  *
	  * INPUT: parent_pop, tournament_size
	  * solutions = getUniqueRandomSolutions(tournament_size);
	  * bestSolution = getBestSolution(solutions);
	  * index = indexOf(bestSolution);
	  * return index;
	  */
	public int tournamentSelection(int tournamentSize) {

		int bestIndex = -1;
		int tourS[] = new int[tournamentSize];
		double bestFitness = Double.MAX_VALUE;
		boolean nU = true;

		for(int i = 0; i < tournamentSize; i++) {
			nU = true;
			while(nU) {
				nU = false;
				tourS[i] = rng.nextInt(POPULATION_SIZE);
				for(int j = 0; j < i; j++) {
					if(tourS[i] == tourS[j]) {
						nU = true;
					}
				}
			}

			double tmp = problem.getObjectiveFunctionValue(tourS[i]);
			if(tmp < bestFitness) {
				bestFitness = tmp;
				bestIndex = tourS[i];
			}

		}
		return bestIndex;
	}
}
