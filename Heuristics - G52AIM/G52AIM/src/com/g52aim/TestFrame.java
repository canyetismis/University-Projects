package com.g52aim;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 
 * @author Warren G. Jackson
 *
 */
public abstract class TestFrame {
	
	protected final long[] SEEDS;
	
	protected final String NEW_LINE = System.lineSeparator();
	
	protected final TestFrameConfig CFG;

	public TestFrame(TestFrameConfig config, long seed) {
		
		this.CFG = config;
		
		// use a seeded random number generator to generate "TOTAL_RUNS" seeds
		Random random = new Random(seed);
		SEEDS = new long[config.getTotalRuns()];
		
		for(int i = 0; i < config.getTotalRuns(); i++) {
			SEEDS[i] = random.nextLong();
		}
	}

	protected void saveData(String filePath, String header, String data) {
		
		Path path = Paths.get("./" + filePath);
		if(!Files.exists(path)) {
			try {
				Files.createFile(path);
				
				//add header
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
	
	public Stream<Integer> rangeAsStream(int start, int end) {
		
		return IntStream.rangeClosed(start, end).boxed();
	}
	
	public void logResult(String methodName, int runId, double bestSolutionValue, String solution) {
		
		System.out.println("Heuristic: " + methodName + NEW_LINE +
				"Run ID: " + runId + NEW_LINE +
				"Best Solution Value: " + bestSolutionValue + NEW_LINE +
				"Best Solution: " + solution + NEW_LINE);
	}
	
	public abstract void runTests();
	
}
