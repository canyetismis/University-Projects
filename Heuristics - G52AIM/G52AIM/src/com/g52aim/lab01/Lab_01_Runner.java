package com.g52aim.lab01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.g52aim.RunData;
import com.g52aim.TestFrame;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.SATHeuristic;
import g52aim.statistics.BoxPlot;
import g52aim.statistics.LineGraph;


public class Lab_01_Runner extends TestFrame
{
	private static final int HEURISTIC_TESTS = 2;
	
	double[][] data = new double[CFG.getTotalRuns()][HEURISTIC_TESTS];
	String[] names = new String[HEURISTIC_TESTS];
	
	public Lab_01_Runner(Lab01TestFrameConfig config, long seed) {
		
		super(config, seed);
	}
	
	public void runTests() {
		
		// execute experiments in parallel (ok when using iteration based termination criteria)
		rangeAsStream(0, HEURISTIC_TESTS - 1)
			.parallel()
			.map(this::runExperimentsForHeuristicId)
			.forEach( this::collateData );
		
		new BoxPlot(CFG.getBoxPlotTitle(), false).createPlot(data, names);
	}
	
	public Stream<RunData> runExperimentsForHeuristicId(int heuristicId) {
		
		return rangeAsStream(0, CFG.getTotalRuns() - 1)
					.parallel()
					.map( run -> runExperiment(run, heuristicId));
	}
	
	public RunData runExperiment(int run, int heuristicId) {
		
		Random random = new Random(SEEDS[run]);
		SAT sat = new SAT(CFG.getInstanceId(), CFG.getRunTime(), random);
		ArrayList<Double> fitnessTrace = new ArrayList<Double>();
		
		SATHeuristic heuristic = Lab01TestFrameConfig.getSATHeuristic(heuristicId, random);
		
		while(!sat.hasTimeExpired()) {
			
			heuristic.applyHeuristic(sat);
			double fitness = sat.getObjectiveFunctionValue(SATHeuristic.CURRENT_SOLUTION_INDEX);
			fitnessTrace.add(fitness);
		}
		
		logResult(heuristic.getHeuristicName(), run, sat.getBestSolutionValue(), sat.getBestSolutionAsString());
		
		return new RunData(fitnessTrace, sat.getBestSolutionValue(), heuristic.getHeuristicName(), heuristicId, run, sat.getBestSolutionAsString());
	}
	
	public void collateData(Stream<RunData> resultList) {
		
		List<RunData> result = resultList.collect(Collectors.toList());
		
		RunData best = result.stream().min( (a, b) -> { return a.best.compareTo(b.best);}).get();
		RunData worst = result.stream().max( (a, b) -> { return a.best.compareTo(b.best);}).get();
		result.stream().forEach( r -> {
			data[r.trialId][r.heuristicId] = r.best;
			names[r.heuristicId] = r.heuristicName;
		});
		
		new LineGraph(best.heuristicName + " Best Fitness Trace", CFG.getInstanceId()).createGraph(best.trace);
		new LineGraph(worst.heuristicName + " Worst Fitness Trace", CFG.getInstanceId()).createGraph(worst.trace);
		
		Double[] results = result.stream().map( r -> { return (r.best); }).toArray(Double[]::new);
		String resultsString = Arrays.toString(results);
		saveData(best.heuristicName + "_" + CFG.getTotalRuns() + "Runs.csv",
				"Heuristic,Run Time,Instance ID",
				best.heuristicName + "," + CFG.getRunTime() + "," + CFG.getInstanceId() + "," + resultsString.substring(1, resultsString.length() - 1) + "," + best.solution);

	}
	
	public static void main(String [] args) {
		
		long parentSeed = 10022017l; // date of first lab session
		Lab01TestFrameConfig config = new Lab01TestFrameConfig();
		TestFrame runner = new Lab_01_Runner(config, parentSeed);
		runner.runTests();
	}
}
