package com.g52aim.lab03;

public class GeometricCooling implements CoolingSchedule {

	/**
	 * TODO - somehow change or set this in your experiments
	 */
	private double currentTemperature;

	/**
	 * TODO - somehow change or set this in your experiments
	 */
	private double alpha;

	/**
	 *
	 * @param initialSolutionFitness
	 *            The objective value of the initial solution. Maybe useful (or
	 *            not) for some setting?
	 */
	public GeometricCooling(double initialSolutionFitness) {

		// TODO settings for initial temperature (current temperature
		//      at iteration 0) and alpha

		double c = 1.0; // set to 100% of the initial solution cost for now
		this.currentTemperature = c * initialSolutionFitness;
		this.alpha = 0.9;
	}

	@Override
	public double getCurrentTemperature() {

		return this.currentTemperature;
	}

	/**
	 * DEFINITION: T_{i + 1} = alpha * T_i
	 */
	@Override
	public void advanceTemperature() {
		currentTemperature *= alpha;
	}

	public String toString() {

		return "Geometric Cooling";
	}
}
