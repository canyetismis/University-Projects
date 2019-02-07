package com.g52aim.lab03;

import com.g52aim.TestFrameConfig;

public class Lab03TestFrameConfig extends TestFrameConfig {

	public enum Schedule {
		GEOMETRIC, LUNDY_AND_MEES;
	}
	
	/*
	 * permitted schedules = Schedule.GEOMETRIC, Schedule.LUNDY_AND_MEES
	 * Change this to use the respective cooling schedule during experiments.
	 */
	protected final Schedule schedule = Schedule.GEOMETRIC;
	
	/*
	 * permitted total runs = 11
	 */
	protected final int TOTAL_RUNS  = 11;
	
	/*
	 * permitted instance ID's = 1
	 */
	protected final int INSTANCE_ID = 1;
	
	/*
	 * permitted run times (seconds) = 20
	 */
	protected final int RUN_TIME = 20;
	
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
		return "Simulated Annealing";
	}

	@Override
	public String getConfigurationAsString() {
		return "using " + schedule.name() + " cooling";
	}
	
	public CoolingSchedule getCoolingSchedule(double initialSolutionFitness) {
		
		switch(schedule) {
		case GEOMETRIC:
			return new GeometricCooling(initialSolutionFitness);
		case LUNDY_AND_MEES:
			return new LundyAndMees(initialSolutionFitness);
		default:
			return null;
		}
		
	}
}
