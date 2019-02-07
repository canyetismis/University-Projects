package com.g52aim.lab05;

import java.util.Random;

import g52aim.domains.chesc2014_SAT.Meme;
import g52aim.domains.chesc2014_SAT.SAT;
import g52aim.satheuristics.genetics.CrossoverHeuristic;
import g52aim.satheuristics.genetics.PopulationHeuristic;
import g52aim.satheuristics.genetics.PopulationReplacement;
import g52aim.searchmethods.PopulationBasedSearchMethod;

public class MultiMeme extends PopulationBasedSearchMethod {

	/**
	 * The innovation rate setting
	 */
	private final double innovationRate;

	private final CrossoverHeuristic crossover;
	private final BitMutation mutation;
	private final PopulationReplacement replacement;
	private final TournamentSelection selection;

	private final MemeplexInheritanceMethod inheritance;

	/**
	 * The possible local search operators to use.
	 */
	private final PopulationHeuristic[] lss;

	// Constructor used for testing. Please do not remove!
	public MultiMeme(SAT problem, Random rng, int populationSize, double innovationRate, CrossoverHeuristic crossover,
			BitMutation mutation, PopulationReplacement replacement, TournamentSelection selection, MemeplexInheritanceMethod inheritance,
			PopulationHeuristic[] lss) {

		super(problem, rng, populationSize);

		this.innovationRate = innovationRate;
		this.crossover = crossover;
		this.mutation = mutation;
		this.replacement = replacement;
		this.selection = selection;
		this.inheritance = inheritance;
		this.lss = lss;
	}

	public MultiMeme(SAT problem, Random rng, int populationSize, double innovationRate) {

		this(
			problem,
			rng,
			populationSize,
			innovationRate,
			new PTX1(problem, rng), // crossover
			new BitMutation(problem, rng), // mutation
			new TransGenerationalReplacementWithElitism(), // replacement
			new TournamentSelection(populationSize, rng, problem), // parent selection
			new SimpleInheritanceMethod(problem, rng), // memeplex inheritance
			new PopulationHeuristic[] { // create mapping for local search operators used for meme in meme index 1
				new DBHC_OI(problem, rng), // [0]
				new DBHC_IE(problem, rng), // [1]
				new SDHC_OI(problem, rng), // [2]
				new SDHC_IE(problem, rng)  // [3]
			}
		);
	}

	/**
	 * MMA PSEUDOCODE:
	 *
	 * INPUT: PopulationSize, MaxGenerations, InnovationRate
	 *
	 * generateInitialPopulation();
	 * FOR 0 -> MaxGenerations
	 *
	 ####### BEGIN IMPLEMENTING HERE #######
	 *     FOR 0 -> PopulationSize / 2
	 *         select parents using tournament selection with tournament size = 3
	 *         apply crossover to generate offspring
	 *         inherit memeplex using simple inheritance method
	 *         mutate the memes within each memeplex of each child with  probability dependent on the innovation rate
	 *         apply mutation to offspring with intensity of mutation set for each solution dependent on its meme option
	 *         apply local search to offspring with choice of operator dependent on each solution’s meme option
	 *     ENDFOR
	 *     do population replacement
	 ####### STOP IMPLEMENTING HERE #######
	 * ENDFOR
	 * return s_best;
	 */
	public void runMainLoop() {
		int sizeTour = 3;
		int c1 = this.POPULATION_SIZE - 2;
		int c2 = 0;
		for(int i = 0; i < this.POPULATION_SIZE / 2; i++) {
			c1 += 2;
			c2 = c1 + 1;

			int p1 =selection.tournamentSelection(sizeTour);
			int p2 = selection.tournamentSelection(sizeTour);
			this.crossover.applyHeuristic(p1, p2, c1, c2);
			this.inheritance.performMemeticInheritance(p1, p2, c1, c2);

			performMutationOfMemeplex(c1);
			performMutationOfMemeplex(c2);

			applyMutationForChildDependentOnMeme(c1, 0);
			applyMutationForChildDependentOnMeme(c2, 0);

			applyLocalSearchForChildDependentOnMeme(c1, 1);
			applyLocalSearchForChildDependentOnMeme(c2, 1);

		}
		replacement.doReplacement(problem, this.POPULATION_SIZE);
	}

	/**
	 * Applies mutation to the child dependent on its current meme option for mutation.
	 * Mapping of meme option to IOM: IntensityOfMutation <- memeOption + 1
	 *
	 * @param childIndex The solution memory index of the child to mutate.
	 * @param memeIndex The meme index used for storing the meme relating to the intensity of mutation setting.
	 */
	public void applyMutationForChildDependentOnMeme(int childIndex, int memeIndex) {

		Meme meme = problem.getMeme(childIndex, memeIndex);
		int memeOpt = meme.getMemeOption();
		this.mutation.setMutationRate(memeOpt/problem.getNumberOfVariables());
		this.mutation.applyHeuristic(childIndex);
	}

	/**
	 * Applies the local search operator to the child as specified by its current meme option.
	 *
	 * @param childIndex The solution memory index of the child to mutate.
	 * @param memeIndex The meme index used for storing the meme relating to the local search operator setting.
	 */
	public void applyLocalSearchForChildDependentOnMeme(int childIndex, int memeIndex) {

		Meme meme = problem.getMeme(childIndex, memeIndex);
		int memeOpt = meme.getMemeOption();
		this.lss[memeOpt].applyHeuristic(childIndex);

	}

	/**
	 * Applies mutation to each meme within the memeplex of the specified solution with probability
	 * dependent on the innovation rate.
	 *
	 * @param solutionIndex The solution memory index of the solution to mutate the memeplex of.
	 */
	public void performMutationOfMemeplex(int solutionIndex) {

		double rand = this.rng.nextDouble();
		if(rand < this.innovationRate) {
			int memeNum = problem.getNumberOfMemes();
			Meme meme;

			for(int i = 0; i < memeNum; i++) {
				meme = problem.getMeme(solutionIndex, i);
				int optionNum = meme.getTotalOptions();
				boolean bool = true;
				int opt = 0;

				while(bool) {
					bool = false;
					opt = this.rng.nextInt(optionNum);
					if(opt == meme.getMemeOption()) {
						bool = true;
					}

				}
				meme.setMemeOption(opt);
			}
		}

	}

	public String toString() {

		return "Multimeme Memetic Algorithm";
	}

}
