package com.g52aim.lab01;

// potential packages which may need importing
import java.util.Collections;
import java.util.Random;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.helperfunctions.ArrayMethods;
import g52aim.satheuristics.SATHeuristic;

public class DavissBitHC extends SATHeuristic {

	public DavissBitHC(Random random) {

		super(random);
	}

	/**
	  * DAVIS's BIT HILL CLIMBING LECTURE SLIDE PSEUDO-CODE
	  *
	  * bestEval = evaluate(currentSolution);
	  * perm = createRandomPermutation();
	  * for(j = 0; j < length[currentSolution]; j++) {
	  *
	  *     bitFlip(currentSolution, perm[j]); 		//flips j^th bit from permutation of solution producing s' from s
	  *     tmpEval = evaluate(currentSolution);
	  *
	  *     if(tmpEval < bestEval) { 				// if there is improvement (strict improvement)
	  *
	  *         bestEval = tmpEval; 				// accept the best flip
	  *
	  *     } else { 								// if there is no improvement, reject the current bit flip
	  *
	  *          bitFlip(solution, perm[j]); 		//go back to s from s'
	  *     }
	  * }
	  *
	  * @param problem The problem to be solved.
	  */
	public void applyHeuristic(SAT problem) {
		double bestEval = problem.getObjectiveFunctionValue(CURRENT_SOLUTION_INDEX);
		int[] perm = new int[problem.getNumberOfVariables()];

		for(int i = 0; i < problem.getNumberOfVariables(); i++) {
			perm[i] = i;
		}

		perm = ArrayMethods.shuffle(perm, random);

		for(int j = 0; j < problem.getNumberOfVariables(); j++) {
			problem.bitFlip(perm[j], CURRENT_SOLUTION_INDEX);
			double tmpEval = problem.getObjectiveFunctionValue(CURRENT_SOLUTION_INDEX);

			if(tmpEval < bestEval) {
				bestEval = tmpEval;
			} else {
				problem.bitFlip(perm[j], CURRENT_SOLUTION_INDEX);
			}

		}

	}

	public String getHeuristicName() {

		return "Davis's Bit HC";
	}

}
