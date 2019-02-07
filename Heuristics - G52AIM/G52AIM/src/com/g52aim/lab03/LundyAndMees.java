package com.g52aim.lab03;

public class LundyAndMees implements CoolingSchedule {

	/**
	 * TODO - somehow change or set this in your experiments
	 */
	private double currentTemperature;

	/**
	 * TODO - somehow change or set this in your experiments
	 */
	private double beta;

	/**
	 *
	 * @param initialSolutionFitness
	 *            The objective value of the initial solution. Maybe useful (or
	 *            not) for some setting?
	 */
	public LundyAndMees(double initialSolutionFitness) {

		// TODO set initial temperature and "beta".
		double c = 1.0;
		this.currentTemperature = c * initialSolutionFitness;
		this.beta = 0.00001;
	}

	@Override
	public double getCurrentTemperature() {

		return currentTemperature;
	}

	/**
	 * DEFINITION: T_{i + 1} = T_i / ( 1 + beta * T_i )
	 */
	@Override
	public void advanceTemperature() {

		currentTemperature = currentTemperature / (1 + currentTemperature + 1);
	}

	@Override
	public String toString() {

		return "Lundy and Mees";
	}

}
