/*
 * Copyright (C) 2013 Jeremy Gooch <http://www.linkedin.com/in/jeremygooch/>
 *
 * The licence covering the contents of this file is described in the file LICENCE.txt,
 * which should have been included as part of the distribution containing this file.
 */

package org.goochjs.glicko2;

import java.util.List;

/**
 * This is the main calculation engine based on the contents of Glickman's paper.
 * http://www.glicko.net/glicko/glicko2.pdf
 * 
 * @author Jeremy Gooch
 * 
 * @param <T>
 */
public class RatingCalculator {

	private final static double DEFAULT_RATING =  1500.0;
	private final static double DEFAULT_DEVIATION =  350;
	private final static double DEFAULT_VOLATILITY =  0.06;
	private final static double DEFAULT_TAU =  0.75;
	private final static double MULTIPLIER =  173.7178;
	private final static double CONVERGENCE_TOLERANCE =  0.000001;
	
	private double tau; // constrains volatility over time
	private double defaultVolatility;
	
	public RatingCalculator() {
		tau = DEFAULT_TAU;
		defaultVolatility = DEFAULT_VOLATILITY;
	}

	public RatingCalculator(
			double initVolatility,
			double tau) {
		
		this.defaultVolatility = initVolatility;
		this.tau = tau;
	}

	public void updateRatings(RatingPeriodResults results) {
		// the following will run through all players and calculate their new ratings
		for ( Rating player : results.getParticipants() ) {
			if ( results.getResults(player).size() > 0 ) {
				calculateNewRating(player, results.getResults(player));
			} else {
				// if a player does not compete during the rating period, then only Step 6 applies.
				// the player's rating and volatility parameters remain the same but deviation increases
				player.setWorkingRating(player.getGlicko2Rating());
				player.setWorkingRatingDeviation(calculateNewRD(player.getGlicko2RatingDeviation(), player.getVolatility()));
				player.setWorkingVolatility(player.getVolatility());
			}
		}
		
		// now iterate through the participants and confirm their new ratings
		for ( Rating player : results.getParticipants() ) {
			player.finaliseRating();
		}
		
		// lastly, clear the result set down in anticipation of the next rating period
		results.clear();
	}

	private void calculateNewRating(Rating player, List<Result> results) {
		// this is the function processing described in step 5 of Glickman's paper
		double phi = player.getGlicko2RatingDeviation();
		double sigma = player.getVolatility();
		double a = Math.log( Math.pow(sigma, 2) );
		double delta = delta(player, results);
		double v = v(player, results);
		
		// step 5.2 - set the initial values of the iterative algorithm to come in step 5.4
		double A = a;
		double B = 0.0;
 		if ( Math.pow(delta, 2) > Math.pow(phi, 2) + v ) {
			B = Math.log( Math.pow(delta, 2) - Math.pow(phi, 2) - v );			
		} else {
			double k = 1;
			B = a - ( k * Math.abs(tau));
			
			while ( f(B , delta, phi, v, a, tau) < 0 ) {
				k++;
				B = a - ( k * Math.abs(tau));
			}
		}

		// step 5.3
		double fA = f(A , delta, phi, v, a, tau);
		double fB = f(B , delta, phi, v, a, tau);

		// step 5.4
		while ( Math.abs(B - A) > CONVERGENCE_TOLERANCE ) {
 			double C = A + (( (A-B)*fA ) / (fB - fA));
 			double fC = f(C , delta, phi, v, a, tau);
 			
 			if ( fC * fB < 0 ) {
 				A = B;
 				fA = fB;
 			} else {	
 				fA = fA / 2.0;
 			}
 			
 			B = C;
 			fB = fC;
 		}
 		
		double newSigma = Math.exp( A/2.0 );
 		
		player.setWorkingVolatility(newSigma);

		// Step 6
		double phiStar = calculateNewRD( phi, newSigma );
		
		// Step 7
		double newPhi = 1.0 / Math.sqrt(( 1.0 / Math.pow(phiStar, 2) ) + ( 1.0 / v ));

		// note that the newly calculated rating values are stored in a "working" area in the Rating object
		// this avoids us attempting to calculate subsequent participants' ratings against a moving target
		player.setWorkingRating(
				player.getGlicko2Rating()
				+ ( Math.pow(newPhi, 2) * outcomeBasedRating(player, results)));
		player.setWorkingRatingDeviation(newPhi);
		player.incrementNumberOfResults(results.size());
	}
	
	private double f(double x, double delta, double phi, double v, double a, double tau) {
		return ( Math.exp(x) * ( Math.pow(delta, 2) - Math.pow(phi, 2) - v - Math.exp(x) ) /
				(2.0 * Math.pow( Math.pow(phi, 2) + v + Math.exp(x), 2) )) - 
				( ( x - a ) / Math.pow(tau, 2) );
	}
	
	private double g(double deviation) {
		// this is the first sub-function of step 3 of Glickman's paper
		return 1.0 / ( Math.sqrt( 1.0 + ( 3.0 * Math.pow(deviation, 2) / Math.pow(Math.PI,2) )));
	}
	
	private double E(double playerRating, double opponentRating, double opponentDeviation) {
		// this is the second sub-function of step 3 of Glickman's paper
		return 1.0 / (1.0 + Math.exp( -1.0 * g(opponentDeviation) * ( playerRating - opponentRating )));
	}
	
	private double v(Rating player, List<Result> results) {
		// this is the main function in step 3 of Glickman's paper
		double v = 0.0;
		
		for ( Result result: results ) {
			v = v + (
					( Math.pow( g(result.getOpponent(player).getGlicko2RatingDeviation()), 2) )
					* E(player.getGlicko2Rating(),
							result.getOpponent(player).getGlicko2Rating(),
							result.getOpponent(player).getGlicko2RatingDeviation())
					* ( 1.0 - E(player.getGlicko2Rating(),
							result.getOpponent(player).getGlicko2Rating(),
							result.getOpponent(player).getGlicko2RatingDeviation())
					));
		}
		
		return Math.pow(v, -1);
	}
	
	private double delta(Rating player, List<Result> results) {
		// this is the formula in step 4 of Glickman's paper
		return v(player, results) * outcomeBasedRating(player, results);
	}
	
	private double outcomeBasedRating(Rating player, List<Result> results) {
		// this is the formula in step 4 of Glickman's paper
		double outcomeBasedRating = 0;
		
		for ( Result result: results ) {
			outcomeBasedRating = outcomeBasedRating
					+ ( g(result.getOpponent(player).getGlicko2RatingDeviation())
						* ( result.getScore(player) - E(
								player.getGlicko2Rating(),
								result.getOpponent(player).getGlicko2Rating(),
								result.getOpponent(player).getGlicko2RatingDeviation() ))
				);
		}
		
		return outcomeBasedRating;
	}
	
	private double calculateNewRD(double phi, double sigma) {
		// this is the formula defined in step 6 and also used for players
		// who have not competed during the rating period
		return Math.sqrt( Math.pow(phi, 2) + Math.pow(sigma, 2) );
	}

	public static double convertRatingToOriginalGlickoScale(double rating) {
		// converts from the value used within the algorithm to a rating in the same range as traditional Elo et al
		return ( ( rating  * MULTIPLIER ) + DEFAULT_RATING );
	}
	
	public static double convertRatingToGlicko2Scale(double rating) {
		// converts from a rating in the same range as traditional Elo et al to the value used within the algorithm 
		return ( ( rating  - DEFAULT_RATING ) / MULTIPLIER ) ;
	}
	
	public static double convertRatingDeviationToOriginalGlickoScale(double ratingDeviation) {
		// converts from the value used within the algorithm to a rating deviation in the same range as traditional Elo et al
		return ( ratingDeviation * MULTIPLIER ) ;
	}
	
	public static double convertRatingDeviationToGlicko2Scale(double ratingDeviation) {
		// converts from a rating deviation in the same range as traditional Elo et al to the value used within the algorithm 
		return ( ratingDeviation / MULTIPLIER );
	}

	public double getDefaultRating() {
		return DEFAULT_RATING;
	}

	public double getDefaultVolatility() {
		return defaultVolatility;
	}

	public double getDefaultRatingDeviation() {
		return DEFAULT_DEVIATION;
	}
}



