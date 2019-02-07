package com.g52aim.lab03;


import java.util.Random;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.searchmethods.SinglePointSearchMethod;


public class SimulatedAnnealing extends SinglePointSearchMethod {

	private CoolingSchedule cs;

	public SimulatedAnnealing(CoolingSchedule schedule, SAT problem, Random random) {

		super(problem, random);

		this.cs = schedule;
	}

	/**
	 *
	 * PSEUDOCODE for Simulated Annealing:
	 *
	 * INPUT : T_0 and any other parameters of the cooling schedule
	 * s_0 = generateInitialSolution();
	 * Temp <- T_0;
	 * s_{best} <- s_0;
	 * s* <- s_0;
	 *
	 * REPEAT
	 *     s' <- bitFlip(s*);
	 *     delta <- f(s') - f(s*);
	 *     r <- random \in [0,1];
	 *     IF delta < 0 OR r < P(delta, Temp) THEN
	 *         s* <- s';
	 *     ENDIF
	 *     s_{best} <- updateBest();
	 *     Temp <- advanceTemperature();
	 * UNTIL termination conditions are satisfied;
	 *
	 * RETURN s_{best};
	 */
	protected void runMainLoop() {

		double sStar = problem.getObjectiveFunctionValue(CURRENT_SOLUTION_INDEX);
		int rand = random.nextInt(problem.getNumberOfVariables());
		problem.bitFlip(rand, CURRENT_SOLUTION_INDEX);
		double sPrime = problem.getObjectiveFunctionValue(CURRENT_SOLUTION_INDEX);

		double delta = sPrime - sStar;
		double r = random.nextDouble();
		double temp = cs.getCurrentTemperature();

		if( delta < 0 || r < Math.exp(-delta / temp)) {
			sStar = sPrime;
		} else {
			problem.bitFlip(rand, CURRENT_SOLUTION_INDEX);
		}

		cs.advanceTemperature();
	}

	public String toString() {
		return "Simulated Annealing with " + cs.toString();
	}
}
