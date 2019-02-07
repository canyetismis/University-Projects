package com.g52aim.lab04;

import java.util.Random;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.genetics.CrossoverHeuristic;

/**
 * Uniform Crossover
 * @author Warren G. Jackson
 *
 */
public class UniformXO extends CrossoverHeuristic {

	public UniformXO(SAT problem, Random random) {

		super(problem, random);
	}

	/*
	 * PSEUDOCODE
	 *
	 * INPUTS: p1, p2, c1, c2
	 * memory[c1] = copyOf(p1);
	 * memory[c2] = copyOf(p2);
	 * FOR 0 -> chromosome_length
	 *     IF random < 0.5 THEN
	 *         swap(c1, c2, j); // swap the j’th bit between offspring
	 *     ENDIF
	 * ENDFOR
	 */
	public void applyHeuristic(int parent1Index, int parent2Index,
			int child1Index, int child2Index) {

		problem.copySolution(parent1Index, child1Index);
		problem.copySolution(parent2Index, child2Index);

		for(int i = 0; i < problem.getNumberOfVariables(); i++) {
			if(random.nextDouble() < 0.5) {
				problem.exchangeBits(child1Index, child2Index, i);
			}
		}
	}
}
