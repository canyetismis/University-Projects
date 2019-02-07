package com.g52aim;

/**
 * 
 * @author Warren G. Jackson
 *
 */
public abstract class TestFrameConfig {

	public abstract int getTotalRuns();
	
	public abstract int getInstanceId();
	
	public abstract int getRunTime();
	
	public abstract String getMethodName();
	
	public abstract String getConfigurationAsString();
	
	public String getBoxPlotTitle() {
		
		String config = getConfigurationAsString();
		return "Results for " + getMethodName() + " given " + getRunTime() +
				" seconds for solving instance ID " + getInstanceId() + " over " +
				getTotalRuns() + " runs" + (config == null ? "." : (" with " + config + "."));
	}
}
