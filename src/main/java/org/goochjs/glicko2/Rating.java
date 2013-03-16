/*
 * Copyright (C) 2013 Jeremy Gooch <http://www.linkedin.com/in/jeremygooch/>
 *
 * The licence covering the contents of this file is described in the file LICENCE.txt,
 * which should have been included as part of the distribution containing this file.
 */

package org.goochjs.glicko2;

/**
 * Holds an individual's Glicko-2 rating.
 *
 * @author Jeremy Gooch
 *
 * @param <T>
 * 
 * Glicko-2 ratings are an average skill value, a standard deviation and a volatility (how consistent the player is).
 * His paper on the algorithm allows scaling of these values to be more directly comparable with existing rating
 * systems such as Elo or USCF's derivation thereof.
 * Our implementation of the rating records them at the original scale.
 */

public class Rating {

	private String uid; // not actually used by the calculation engine but useful to track whose rating is whose
	private double rating;
	private double ratingDeviation;
	private double volatility;
	private int numberOfResults = 0; // the number of results from which the rating has been calculated

	 // the following variables are used to hold values temporarily whilst running calculations
	private double workingRating;
	private double workingRatingDeviation;
	private double workingVolatility;
	
	public Rating(String uid, RatingCalculator ratingSystem) {
		this.uid = uid;
		this.rating = ratingSystem.getDefaultRating();
		this.ratingDeviation = ratingSystem.getDefaultRatingDeviation();
		this.volatility = ratingSystem.getDefaultVolatility();
	}

	public Rating(String uid, RatingCalculator ratingSystem, double initRating, double initRatingDeviation, double initVolatility) {
		this.uid = uid;
		this.rating = initRating;
		this.ratingDeviation = initRatingDeviation;
		this.volatility = initVolatility;
	}

	public double getRating() {
		return this.rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public double getGlicko2Rating() {
		// return the average skill value of the player scaled down appropriate to the Glicko2 algorithm.
		return RatingCalculator.convertRatingToGlicko2Scale(this.rating);
	}

	public void setGlicko2Rating(double rating) {
		// take the rating in Glicko2 scale and scale it up to the original Glicko scale.
		this.rating = RatingCalculator.convertRatingToOriginalGlickoScale(rating);
	}

	public double getVolatility() {
		return volatility;
	}

	public void setVolatility(double volatility) {
		this.volatility = volatility;
	}

	public double getRatingDeviation() {
		return ratingDeviation;
	}

	public void setRatingDeviation(double ratingDeviation) {
		this.ratingDeviation = ratingDeviation;
	}

	public double getGlicko2RatingDeviation() {
		// return the rating deviation of the player scaled down appropriate to the Glicko2 algorithm.
		return RatingCalculator.convertRatingDeviationToGlicko2Scale( ratingDeviation );
	}

	public void setGlicko2RatingDeviation(double ratingDeviation) {
		// take the rating deviation in Glicko2 scale and scale it up to the original Glicko scale.
		this.ratingDeviation = RatingCalculator.convertRatingDeviationToOriginalGlickoScale( ratingDeviation );
	}

	public void finaliseRating() {
		this.setGlicko2Rating(workingRating);
		this.setGlicko2RatingDeviation(workingRatingDeviation);
		this.setVolatility(workingVolatility);
		
		this.setWorkingRatingDeviation(0);
		this.setWorkingRating(0);
		this.setWorkingVolatility(0);
	}
	
	public int getNumberOfResults() {
		return numberOfResults;
	}

	public void incrementNumberOfResults(int increment) {
		this.numberOfResults = numberOfResults + increment;
	}

	public String getUid() {
		return uid;
	}

	public void setWorkingVolatility(double workingVolatility) {
		this.workingVolatility = workingVolatility;
	}

	public void setWorkingRating(double workingRating) {
		this.workingRating = workingRating;
	}

	public void setWorkingRatingDeviation(double workingRatingDeviation) {
		this.workingRatingDeviation = workingRatingDeviation;
	}
}
