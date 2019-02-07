package com.g52aim.lab03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import com.g52aim.ExperimentalSettings;
import com.g52aim.RunData;
import com.g52aim.TestFrame;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.SATHeuristic;
import g52aim.searchmethods.SearchMethod;
import g52aim.statistics.BoxPlot;
import g52aim.statistics.LineGraph;

/**
 * 
 * @author Warren G. Jackson
 * 
 * Note - This will take about 6 times as long as previous algorithms since
 * the termination criterion is evaluation-based (not time-based), hence the 
 * computationally expensive problem.copy() method significantly increases
 * the actual run time with only one evaluation being performed per copy.
 *
 */
public class Lab_03_Runner extends TestFrame {
	
	public Lab_03_Runner(Lab03TestFrameConfig config, long seed) {
		
		super(config, seed);
	}

	private static final int HEURISTIC_TESTS = 1;
	
	public void runTests() {
		
		double[][] data = new double[CFG.getTotalRuns()][HEURISTIC_TESTS];
		String[] names = new String[HEURISTIC_TESTS];
		
		if( ExperimentalSettings.ENABLE_PARALLEL_EXECUTION ) {
			
			Stream<Stream<RunData>> resultStream = rangeAsStream(0, HEURISTIC_TESTS - 1).parallel().map(this::runExperimentsForHeuristicId);
			resultStream.forEach( resultList -> {
				
				RunData[] result = resultList.toArray(RunData[]::new);
				updateData(data, names, result);
			});
			
		} else { // sequential execution without streams
			
			for(int i = 0; i < HEURISTIC_TESTS; i++) {
				
				RunData[] result = runExperimentsForHeuristic(i);
				updateData(data, names, result);
			}
		}
		
		
		if( ExperimentalSettings.ENABLE_GRAPHS ) {
			
			new BoxPlot(CFG.getBoxPlotTitle(), false).createPlot(data, names);
		}
	}
	
	public void updateData(double[][] data, String[] names, RunData[] result) {
			
		RunData best = Arrays.stream(result).min( (a, b) -> { return a.best.compareTo(b.best);}).get();
		RunData worst = Arrays.stream(result).max( (a, b) -> { return a.best.compareTo(b.best);}).get();
		Arrays.stream(result).forEach( r -> {
			data[r.trialId][r.heuristicId] = r.best;
			names[r.heuristicId] = r.heuristicName;
		});
		
		if( ExperimentalSettings.ENABLE_GRAPHS ) {
			
			new LineGraph(best.heuristicName + " Best Fitness Trace", CFG.getInstanceId()).createGraph(best.trace);
			new LineGraph(worst.heuristicName + " Worst Fitness Trace", CFG.getInstanceId()).createGraph(worst.trace);
		}
		
		Double[] results = Arrays.stream(result).map( r -> { return (r.best); }).toArray(Double[]::new);
		String resultsString = Arrays.toString(results);
		saveData(best.heuristicName + "_" + CFG.getTotalRuns() + "Runs.csv",
				"Heuristic,Run Time,Instance ID,t_0,alpha",
				best.heuristicName + "," + CFG.getRunTime() + "," + CFG.getInstanceId() + "," +
				resultsString.substring(1, resultsString.length() - 1) + "," + best.solution);
	}
	
	public RunData[] runExperimentsForHeuristic(int heuristicId) {
		
		RunData[] dat = new RunData[CFG.getTotalRuns()];
		
		for(int run = 0; run < CFG.getTotalRuns(); run++) {
			
			Random random = new Random(SEEDS[run]);
			SAT sat = new SAT(CFG.getInstanceId(), CFG.getRunTime(), random);
			ArrayList<Double> fitnessTrace = new ArrayList<Double>();
			
			double initialSolutionFitness = sat.getObjectiveFunctionValue(0);
			SearchMethod searchMethod = new SimulatedAnnealing(((Lab03TestFrameConfig)CFG)
					.getCoolingSchedule(initialSolutionFitness), sat, random);
			
			while(!sat.hasTimeExpired()) {
				
				searchMethod.run();
				double fitness = sat.getObjectiveFunctionValue(SATHeuristic.CURRENT_SOLUTION_INDEX);
				fitnessTrace.add(fitness);
			}
			
			logResult(searchMethod.toString(), run, sat.getBestSolutionValue(), sat.getBestSolutionAsString());
			
			dat[run] = new RunData(fitnessTrace, sat.getBestSolutionValue(), searchMethod.toString(), heuristicId, run, sat.getBestSolutionAsString());
		}
		
		return dat;
	}
	
	public Stream<RunData> runExperimentsForHeuristicId(int heuristicId) {
		
		Stream<RunData> dat = rangeAsStream(0, CFG.getTotalRuns() - 1).parallel().map( run -> {
			Random random = new Random(SEEDS[run]);
			SAT sat = new SAT(CFG.getInstanceId(), CFG.getRunTime(), random);
			ArrayList<Double> fitnessTrace = new ArrayList<Double>();
			
			double initialSolutionFitness = sat.getObjectiveFunctionValue(0);
			SearchMethod searchMethod = new SimulatedAnnealing(((Lab03TestFrameConfig)CFG)
					.getCoolingSchedule(initialSolutionFitness), sat, random);
			
			while(!sat.hasTimeExpired()) {
				
				searchMethod.run();
				double fitness = sat.getObjectiveFunctionValue(SATHeuristic.CURRENT_SOLUTION_INDEX);
				fitnessTrace.add(fitness);
			}
			
			logResult(searchMethod.toString(), run, sat.getBestSolutionValue(), sat.getBestSolutionAsString());
			
			return new RunData(fitnessTrace, sat.getBestSolutionValue(), searchMethod.toString(), heuristicId, run, sat.getBestSolutionAsString());
		});
		
		return dat;
	}
	
	public static void main(String [] args) {

		long seed = 1022018l; // date of first lab session
		Lab03TestFrameConfig config = new Lab03TestFrameConfig();
		TestFrame runner = new Lab_03_Runner(config, seed);
		runner.runTests();
	}
}
