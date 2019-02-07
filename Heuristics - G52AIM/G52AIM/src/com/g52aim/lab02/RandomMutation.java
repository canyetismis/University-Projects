package com.g52aim.lab02;

import java.util.Random;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.SATHeuristic;

public class RandomMutation extends SATHeuristic {

	public RandomMutation(Random random) {

		super(random);
	}

	/**
	 * PSEUDO-CODE
	 * i <- random \in [0, N];
	 * s' <- flip(i, s);
	 *
	 * @param problem The problem to be solved.
	 */
	@Override
	public void applyHeuristic(SAT problem) {

		int i = random.nextInt(problem.getNumberOfVariables());
		problem.bitFlip(i, CURRENT_SOLUTION_INDEX);

	}

	@Override
	public String getHeuristicName() {

		return "Single-perturbative Random Mutation";
	}

}
