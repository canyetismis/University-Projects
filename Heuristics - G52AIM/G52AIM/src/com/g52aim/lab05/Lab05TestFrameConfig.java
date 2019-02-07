package com.g52aim.lab05;

import com.g52aim.TestFrameConfig;

public class Lab05TestFrameConfig extends TestFrameConfig {

	/*
	 * permitted total runs = 30
	 */
	protected final int TOTAL_RUNS  = 30;
	
	/*
	 * permitted instance ID's = 1, 7, 9
	 */
	protected final int INSTANCE_ID = 1;
	
	/*
	 * permitted run times (seconds) = infinite, use generations instead.
	 */
	protected final int RUN_TIME = Integer.MAX_VALUE;
	
	/*
	 * permitted number of generations = 75
	 */
	protected final int MAX_GENERATIONS = 75;
	 
	/*
	 * permitted population size = 16
	 */
	protected final int POP_SIZE = 16;
	
	/*
	 * The number of memes in each memeplex
	 */
	protected final int MEMES = 2;
	
	/*
	 * The number of options that each meme can have.
	 * 
	 * 	Meme [0] represents intensity of mutation
	 *  Meme [1] represents choice of hill climbing operator
	 */
	protected final int[] OPTIONS_PER_MEME = new int[]{ 5, 4 };
	
	/*
	 * permitted innovation rates, 0.0 <= INNOVATION_RATE <= 1.0
	 * TODO - this may not be a good setting...
	 */
	protected final double INNOVATION_RATE = 0.0;
	
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
		return Integer.MAX_VALUE;
	}

	@Override
	public String getMethodName() {
		return "Multimeme Memetic Algorithm";
	}
	
	public int getPopulationSize() {
		return this.POP_SIZE;
	}
	
	public int getMemeCount() {
		return this.MEMES;
	}
	
	public int[] getOptionsPerMeme() {
		return this.OPTIONS_PER_MEME;
	}

	@Override
	public String getConfigurationAsString() {
		return "Population size = " + getPopulationSize();
	}
	
	@Override
public String getBoxPlotTitle() {
		
		String config = getConfigurationAsString();
		return "Results for " + getMethodName() + " given " + MAX_GENERATIONS +
				" generations for solving instance ID " + getInstanceId() + " over " +
				getTotalRuns() + " runs" + (config == null ? "." : (" with " + config + "."));
	}
}
