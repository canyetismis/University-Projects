package com.g52aim.lab04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.IntStream;

import com.g52aim.TestFrame;
import com.g52aim.TestFrameConfig;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.genetics.CrossoverHeuristic;
import g52aim.satheuristics.genetics.PopulationHeuristic;
import g52aim.satheuristics.genetics.PopulationReplacement;
import g52aim.searchmethods.SearchMethod;
import g52aim.statistics.BoxPlot;
import g52aim.statistics.LineGraph;


public class Lab_04_Runner extends TestFrame {
	
	public Lab_04_Runner(TestFrameConfig config, long seed) {
		super(config, seed);
	}

	private static final int HEURISTIC_TESTS = 1;
	
	public void runTests() {
		
		Lab04TestFrameConfig config = (Lab04TestFrameConfig) CFG;
		
		double[][] data = new double[CFG.getTotalRuns()][HEURISTIC_TESTS];
		String[] names = new String[HEURISTIC_TESTS];
		
		for(int heuristicTest = 0; heuristicTest < HEURISTIC_TESTS; heuristicTest++) {
			
			double bestRun = Double.MAX_VALUE;
			double worstRun = Double.MIN_VALUE;
			
			LinkedList<ArrayList<Double>> bestRunTrace = null;
			LinkedList<ArrayList<Double>> worstRunTrace = null;
			
			ArrayList<Double> runScores = new ArrayList<Double>();
			
			String bestSolutionRepresentation = null;
			
			for(int run = 0; run < CFG.getTotalRuns(); run++) {
				
				long start_time = System.currentTimeMillis();
				//generation based termination
				int POP_SIZE = ((Lab04TestFrameConfig)CFG).getPopulationSize();
				Random random = new Random(SEEDS[run]);
				SAT sat = new SAT(CFG.getInstanceId(), Integer.MAX_VALUE, random, POP_SIZE);
				LinkedList<ArrayList<Double>> fitnessTrace = new LinkedList<ArrayList<Double>>();
				for(int i = 0; i < POP_SIZE; i++) {
					fitnessTrace.add(new ArrayList<Double>());
				}
				
				CrossoverHeuristic crossover = new UniformXO(sat, random);
				PopulationHeuristic mutation = new BitMutation(sat, random);
				PopulationHeuristic localSearch = null;
				switch(config.MODE) {
				case GA:
					localSearch = new NoopHeuristic(sat, random);
					break;
				case MA:
					localSearch = new DavissBitHillClimbingIE(sat, random);
					break;
				default:
					System.err.println("Invalid Mode given in " + config.getClass().getSimpleName());
					System.exit(0);
					break;
				
				}
				 
				PopulationReplacement replacement = new TransGenerationalReplacementWithElitistReplacement();
				
				SearchMethod heuristic = new MemeticAlgorithm(sat, random, POP_SIZE, crossover, mutation, localSearch, replacement);
				
				int count = 0;
				while(!sat.hasTimeExpired() && count <= config.MODE.getGenerations()) {
					
					heuristic.run();
					
					//add all of population
					Double[] populationFitnesses = IntStream.range(0, POP_SIZE).boxed()
						 .map( sat::getObjectiveFunctionValue )
						 .sorted()
						 .toArray(Double[]::new);
					
					for(int i = 0; i < populationFitnesses.length; i++) {
						fitnessTrace.get(i).add(populationFitnesses[i]);
					}
					
					count++;
				}
				
				double currentBestSolution = sat.getBestSolutionValue();
				data[run][heuristicTest] = currentBestSolution;
				runScores.add(currentBestSolution);
				
				if(currentBestSolution < bestRun) {
					
					bestRun = currentBestSolution;
					bestRunTrace = fitnessTrace;
					bestSolutionRepresentation = sat.getBestSolutionAsString();
				}
				
				if(currentBestSolution > worstRun) {
					
					worstRun = currentBestSolution;
					worstRunTrace = fitnessTrace;
				}
				
				names[heuristicTest] = heuristic.toString();
				
				System.out.println("Time: " + ((System.currentTimeMillis() - start_time)/1e3));
				System.out.println("Heuristic: " + heuristic.toString());
				System.out.println("Run ID: " + run);
				System.out.println("Best Solution Value: " + sat.getBestSolutionValue());
				System.out.println("Best Solution: " + sat.getBestSolutionAsString());
				System.out.println();
			}
			
			//create fitness traces
			new LineGraph(names[heuristicTest] + "(" + config.MODE.toString() + "_Mode) Best Fitness Trace ", CFG.getInstanceId()).createGraph(bestRunTrace);
			new LineGraph(names[heuristicTest] + "(" + config.MODE.toString() + "_Mode) Worst Fitness Trace ", CFG.getInstanceId()).createGraph(worstRunTrace);
			
			//print or save results
			StringBuilder sb = new StringBuilder();
			sb.append(names[heuristicTest] + "," + config.MODE.getGenerations() + "," + CFG.getInstanceId());
			for(double ofv : runScores) {
				sb.append("," + ofv);
			}
			
			sb.append("," + bestSolutionRepresentation);
			
			System.out.println("Best Solution :" + bestSolutionRepresentation);
			saveData(names[heuristicTest] + "_" + config.MODE.toString() + "_Mode_" + CFG.getTotalRuns() + "Runs" + ".csv", sb.toString());
			
		}
		
		new BoxPlot(CFG.getBoxPlotTitle(), false).createPlot(data, names);
	}
	
	private void saveData(String filePath, String data) {
		
		Path path = Paths.get("./" + filePath);
		if(!Files.exists(path)) {
			try {
				Files.createFile(path);
				
				//add header
				String header = "Heuristic,Maximum Generations,Instance ID";
				for(int i = 0; i < CFG.getTotalRuns(); i++) {
					
					header += ("," + i);
				}
				
				header += ",Best Solution As String";
				
				Files.write(path, (header + "\r\n" + data).getBytes());
				
			} catch (IOException e) {
				System.err.println("Could not create file at " + path.toAbsolutePath());
				System.err.println("Printing data to screen instead...");
				System.out.println(data);
			}
			
		} else {
			
			try {
				byte[] currentData = Files.readAllBytes(path);
				data = "\r\n" + data;
				byte[] newData = data.getBytes();
				byte[] writeData = new byte[currentData.length + newData.length];
				System.arraycopy(currentData, 0, writeData, 0, currentData.length);
				System.arraycopy(newData, 0, writeData, currentData.length, newData.length);
				Files.write(path, writeData);
				
			} catch (IOException e) {
				System.err.println("Could not create file at " + path.toAbsolutePath());
				System.err.println("Printing data to screen instead...");
				System.out.println(data);
			}
			
		}
		
	}
	
	public static void main(String [] args) {
		
		long seed = 10022017l; // date of first lab session
		Lab04TestFrameConfig config = new Lab04TestFrameConfig();
		TestFrame runner = new Lab_04_Runner(config, seed);
		runner.runTests();
	}
}
