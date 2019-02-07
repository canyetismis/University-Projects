package com.g52aim.lab04;

import java.util.Random;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.genetics.PopulationHeuristic;

/**
 * Bit Mutation for performing Genetic Mutation of solutions
 * @author Warren G. Jackson
 *
 */
public class BitMutation extends PopulationHeuristic {

	private final double MUTATION_RATE;

	public BitMutation(SAT problem, Random random) {

		super(problem, random);
		this.MUTATION_RATE = 1/problem.getNumberOfVariables();
	}

	/*
	 * PSEUDOCODE
	 *
	 * INPUT: s
     * FOR 0 -> chromosome_length
     *     IF random < 1 / chromosome_length THEN
     *         bitFlip(s,j) // flip the jth bit of solution s
     *      ENDIF
     * ENDFOR
	 */
	public void applyHeuristic(int solutionIndex) {
		for(int i = 0; i < problem.getNumberOfVariables(); i++) {
			if(random.nextDouble() < this.MUTATION_RATE) {
				problem.bitFlip(i, solutionIndex);
			}
		}
	}
}
