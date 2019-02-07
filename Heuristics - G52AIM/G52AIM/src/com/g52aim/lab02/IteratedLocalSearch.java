package com.g52aim.lab02;

import java.util.Random;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.SATHeuristic;
import g52aim.searchmethods.SinglePointSearchMethod;

public class IteratedLocalSearch extends SinglePointSearchMethod {

	// local search / intensification heuristic
	private SATHeuristic ls;

	// mutation / perturbation heuristic
	private SATHeuristic mtn;

	// iom and dos parameter settings
	private int intensityOfMutation;

	private int depthOfSearch;

	/**
	 *
	 * @param problem The problem to be solved.
	 * @param random The random number generator, use this one, not your own!
	 * @param mtn The mutation heuristic.
	 * @param ls The local search heuristic.
	 * @param intensityOfMutation The parameter setting for intensity of mutation.
	 * @param depthOfSearch The parameter setting for depth of search.
	 */
	public IteratedLocalSearch(SAT problem, Random random, SATHeuristic mtn, SATHeuristic ls, int intensityOfMutation, int depthOfSearch) {

		super(problem, random);

		this.mtn = mtn;
		this.ls = ls;
		this.intensityOfMutation = intensityOfMutation;
		this.depthOfSearch = depthOfSearch;
	}

	/**
	 *
	 * Main loop for ILS. The experiment framework will continually call this loop until
	 * the allocated time has expired.
	 *
	 * -- ITERATED LOCAL SEARCH PSEUDO CODE --
	 *
	 * s <- currentSolution
	 * s' <- s
	 *
	 * // apply mutation heuristic "intensityOfMutation" times
	 * FOR 0 -> intensityOfMutation - 1 DO
	 *     s' <- mutation(s')
	 * END_FOR
	 *
	 * // apply local search heuristic "depthOfSearch" times
	 * FOR 0 -> depthOfSearch - 1 DO
	 *     s' <- localSearch(s')
	 * END_FOR
	 *
	 * IF f(s') ( < | <= ) f(s) THEN
	 *     accept();
	 * ELIF
	 *     reject();
	 * FI
	 */
	protected void runMainLoop() {

		for(int i = 0; i < intensityOfMutation - 1; i++) {
			mtn.applyHeuristic(problem);
		}

		for (int j = 0; j < depthOfSearch - 1; j++) {
			ls.applyHeuristic(problem);
		}

		if(problem.getObjectiveFunctionValue(CURRENT_SOLUTION_INDEX) < problem.getObjectiveFunctionValue(BACKUP_SOLUTION_INDEX) || problem.getObjectiveFunctionValue(CURRENT_SOLUTION_INDEX) <= problem.getObjectiveFunctionValue(BACKUP_SOLUTION_INDEX)){
			problem.copySolution(CURRENT_SOLUTION_INDEX, BACKUP_SOLUTION_INDEX);
		}else{
			problem.copySolution(BACKUP_SOLUTION_INDEX, CURRENT_SOLUTION_INDEX);
		}

	}

	public String toString() {
		return "Iterated Local Search";
	}
}
