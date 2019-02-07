package com.g52aim.lab01;

import java.util.Random;

import com.g52aim.TestFrameConfig;

import g52aim.satheuristics.SATHeuristic;


public class Lab01TestFrameConfig extends TestFrameConfig {
	
	/*
	 * permitted total runs = 11
	 */
	protected final int TOTAL_RUNS  = 11;
	
	/*
	 * permitted instance ID's = 1, 9
	 */
	protected final int INSTANCE_ID = 1;
	
	/*
	 * permitted run times (seconds) = 1, 5, 10, 20
	 */
	protected final int RUN_TIME = 1;
	
	/*
	 * name to give to your box plot title
	 */
	protected final String BOXPLOT_TITLE = "Comparison of Heuristics A and B for instance "
			+ INSTANCE_ID + " given " + RUN_TIME + " seconds over " + TOTAL_RUNS + " runs.";
	
	public Lab01TestFrameConfig() {
		
	}
	
	/**
	 * This method should not be changed but is intended for personal use
	 * if you wish to try with other heuristics of your own making.
	 * 
	 * @param heuristicID 0 for the first heuristic, or 1 for the second.
	 * @param random The random number generator used by all SATHeuristic's
	 * @return The corresponding SAT heuristic
	 */
	public static SATHeuristic getSATHeuristic(int heuristicID, Random random) {
		
		SATHeuristic heuristic = null;
		
		switch(heuristicID) {
		case 0:
			heuristic = new DavissBitHC(random);
			break;
		case 1:
			heuristic = new SteepestDescentHC(random);
			break;
		default:
			System.err.println("Request for more than 2 heuristics at a time!");
			System.exit(0);
		}
		
		return heuristic;
	}

	@Override
	public int getTotalRuns() {
		return this.TOTAL_RUNS;
	}

	@Override
	public int getInstanceId() {
		return this.INSTANCE_ID;
	}

	@Override
	public int getRunTime() {
		return this.RUN_TIME;
	}

	@Override
	public String getMethodName() {
		return "Davis's Bit Hill Climbing and Steepest Descent";
	}

	@Override
	public String getConfigurationAsString() {
		return null;
	}

}
