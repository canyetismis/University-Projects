package com.g52aim;

import java.util.ArrayList;

/**
 * 
 * @author Warren G. Jackson
 *
 */
public class RunData {
		
	public final ArrayList<Double> trace;
	
	public final Double best;
	
	public final String heuristicName;
	
	public final int heuristicId;
	
	public final int trialId;
	
	public final String solution;
	
	public RunData(ArrayList<Double> trace, Double best, String heuristicName, int heuristicId, int trialId, String solution) {
		
		this.trace = trace;
		this.best = best;
		this.heuristicName = heuristicName;
		this.heuristicId = heuristicId;
		this.trialId = trialId;
		this.solution = solution;
	}
}
