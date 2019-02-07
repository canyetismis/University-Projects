package com.g52aim.lab04;

import java.util.Random;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.genetics.CrossoverHeuristic;
import g52aim.satheuristics.genetics.PopulationHeuristic;
import g52aim.satheuristics.genetics.PopulationReplacement;
import g52aim.searchmethods.PopulationBasedSearchMethod;

/**
 * Memetic Algorithm ( local search should to be added per the report exercise ).
 *
 * @author Warren G. Jackson
 *
 */
public class MemeticAlgorithm extends PopulationBasedSearchMethod {

	private final CrossoverHeuristic crossover;
	private final PopulationHeuristic mutation;
	private final PopulationHeuristic localSearch;
	private final PopulationReplacement replacement;
	private final TournamentSelection selection;

	public MemeticAlgorithm(SAT problem, Random rng, int populationSize, CrossoverHeuristic crossover,
			PopulationHeuristic mutation, PopulationHeuristic localSearch, PopulationReplacement replacement) {

		super(problem, rng, populationSize);

		this.crossover = crossover;
		this.mutation = mutation;
		this.localSearch = localSearch;
		this.replacement = replacement;
		this.selection = new TournamentSelection(problem, rng, populationSize);
	}

	/**
	  * Memetic Algorithm pseudocode
	  * Note there is no exact pseudocode since the purpose of this
	  * exercise is that you experiment with applying local search
	  * in different places of the MA.
	  *
	  * BASIC PSEUDO CODE (GA) not MA
	  * (population already initialised)
	  *
	  * FOR 0 -> populationSize / 2
	  *		select parents using tournament selection
	  *     apply crossover to generate offspring
	  *     apply mutation to offspring
	  * ENDFOR
	  *
	  * do population replacement
	  *
	  */
	public void runMainLoop() {
		int sizeTour = 3;
		int c1 = POPULATION_SIZE - 2;
		int c2 = 0;
		for(int i = 0; i < this.POPULATION_SIZE/2; i++) {
			c1 += 2;
			c2 = c1 + 1;

			int p1 =selection.tournamentSelection(sizeTour);
			int p2 = selection.tournamentSelection(sizeTour);

			crossover.applyHeuristic(p1, p2, c1, c2);

			mutation.applyHeuristic(c1);
			mutation.applyHeuristic(c2);
			this.localSearch.applyHeuristic(c1);
			this.localSearch.applyHeuristic(c2);
		}

		replacement.doReplacement(problem, this.POPULATION_SIZE);
	}

	public int tournamentSelection(int tournamentSize) {

		return selection.tournamentSelection(tournamentSize);
	}

	@Override
	public String toString() {

		return "Memetic Algorithm";
	}
}
