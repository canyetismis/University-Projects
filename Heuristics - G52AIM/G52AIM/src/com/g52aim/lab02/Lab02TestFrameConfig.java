package com.g52aim.lab02;

import com.g52aim.TestFrameConfig;

public class Lab02TestFrameConfig extends TestFrameConfig {

	/**
	 * permitted total runs = 11
	 */
	protected final int TOTAL_RUNS  = 11;
	
	/**
	 * permitted instance ID's = 2
	 */
	protected final int INSTANCE_ID = 2;
	
	/**
	 * permitted run times (seconds) = 15
	 */
	protected final int RUN_TIME = 15;
	
	/**
	 * permitted values = 1, 2, 3, 4, 5
	 */
	protected final int depthOfSearch = 2;
	
	/**
	 * permitted values = 1, 2, 3, 4, 5
	 */
	protected final int intensityOfMutation = 2;
	
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
		return "Iterated Local Search";
	}

	@Override
	public String getConfigurationAsString() {
		return "intensityOfMutation = " + intensityOfMutation + " and depthOfSearch = " + depthOfSearch;
	}
	
	public int getDepthOfSearch() {
		return this.depthOfSearch;
	}
	
	public int getIntensityOfMutation() {
		return this.intensityOfMutation;
	}

}
