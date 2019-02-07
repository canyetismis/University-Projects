package com.g52aim.lab05;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import com.g52aim.TestFrame;

import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.statistics.BoxPlot;
import g52aim.statistics.LineGraph;

public class Lab_05_Runner extends TestFrame {

	private static final int HEURISTIC_TESTS = 1;
	
	public Lab_05_Runner(Lab05TestFrameConfig config, long seed) {
		super(config, seed);
	}
	
	public void runTests() {
		
		double[][] data = new double[CFG.getTotalRuns()][HEURISTIC_TESTS];
		String[] names = new String[HEURISTIC_TESTS];
		Lab05TestFrameConfig config = (Lab05TestFrameConfig)CFG;
		
		for(int heuristicTest = 0; heuristicTest < HEURISTIC_TESTS; heuristicTest++) {
			
			double bestRun = Double.MAX_VALUE;
			double worstRun = Double.MIN_VALUE;
			
			LinkedList<ArrayList<Double>> bestRunTrace = null;
			LinkedList<ArrayList<Double>> worstRunTrace = null;
			
			ArrayList<Double> runScores = new ArrayList<Double>();
			
			String bestSolutionRepresentation = null;
			
			for(int run = 0; run < CFG.getTotalRuns(); run++) {
				
				//generation based termination
				Random random = new Random(SEEDS[run]);
				SAT sat = new SAT(CFG.getInstanceId(), CFG.getRunTime(), random, config.getPopulationSize(), config.getMemeCount(), config.getOptionsPerMeme());
				
				ArrayList<ArrayList<Long>> memeUsage = new ArrayList<ArrayList<Long>>();
				
				//initialise allele counters to 0
				for(int i = 0; i < config.getMemeCount(); i++) {
					
					memeUsage.add(i, new ArrayList<Long>());
					for(int j = 0; j < config.getOptionsPerMeme()[i]; j++) {
						
						memeUsage.get(i).add(j, 0L);
					}
				}
				
				LinkedList<ArrayList<Double>> fitnessTrace = new LinkedList<ArrayList<Double>>();
				for(int i = 0; i < config.getPopulationSize(); i++) {
					fitnessTrace.add(new ArrayList<Double>());
				}
				
				MultiMeme mma = new MultiMeme(sat, random, config.getPopulationSize(), config.INNOVATION_RATE);
				
				int count = 0;
				while(!sat.hasTimeExpired() && count <= config.MAX_GENERATIONS) {
					
					mma.run();
					
					//add all of population
					PriorityQueue<Double> pq = new PriorityQueue<>();
					for(int i = 0; i < config.getPopulationSize(); i++) {
						pq.add(sat.getObjectiveFunctionValue(i));
					}
					
					for(int i = 0; i < config.getPopulationSize(); i++) {
						fitnessTrace.get(i).add(pq.remove());
					}
					
					for(int i = 0; i < config.getMemeCount(); i++) {

						for(int j = 0; j < config.getPopulationSize(); j++) {
							int allele = sat.getMeme(j, i).getMemeOption();
							long c = memeUsage.get(i).get(allele);
							memeUsage.get(i).set(allele, c + 1);
						}
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
				
				names[heuristicTest] = mma.toString();
				
				System.out.println("Heuristic: " + mma.toString());
				System.out.println("Run ID: " + run);
				System.out.println("Best Solution Value: " + sat.getBestSolutionValue());
				System.out.println("Best Solution: " + sat.getBestSolutionAsString());
				
				for(int i = 0; i < config.getMemeCount(); i++) {
					
					System.out.println("MEME " + i + ":");
					for(int j = 0; j < config.getOptionsPerMeme()[i]; j++) {
						
						System.out.println("Allele " + j + " = " + memeUsage.get(i).get(j));
					}
				}
				
				System.out.println();
			}
			
			//create fitness traces
			new LineGraph(names[heuristicTest] + " Best Fitness Trace", CFG.getInstanceId()).createGraph(bestRunTrace);
			new LineGraph(names[heuristicTest] + " Worst Fitness Trace", CFG.getInstanceId()).createGraph(worstRunTrace);
			
			//print or save results
			StringBuilder sb = new StringBuilder();
			sb.append(names[heuristicTest] + "," + config.INNOVATION_RATE + "," + CFG.getRunTime() + "," + CFG.getInstanceId());
			for(double ofv : runScores) {
				sb.append("," + ofv);
			}
			
			sb.append("," + bestSolutionRepresentation);
			
			System.out.println("Best Solution :" + bestSolutionRepresentation);
			saveData(names[0] + " " + CFG.getTotalRuns() + "Runs.csv", sb.toString());
			
		}
		
		new BoxPlot(CFG.getBoxPlotTitle(), false).createPlot(data, names);
	}
	
	private void saveData(String filePath, String data) {
		
		Path path = Paths.get("./" + filePath);
		if(!Files.exists(path)) {
			try {
				Files.createFile(path);
				
				//add header
				String header = "Heuristic,Innovation Rate,Run Time,Instance ID";
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
		
		long seed = 10022017l;
		Lab05TestFrameConfig config = new Lab05TestFrameConfig();
		TestFrame runner = new Lab_05_Runner(config, seed);
		runner.runTests();
	}
}
