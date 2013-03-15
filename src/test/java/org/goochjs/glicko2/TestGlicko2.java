/**
 * 
 */
package org.goochjs.glicko2;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jeremy Gooch
 *
 */
public class TestGlicko2 {

	private static Glicko2RatingCalculator ratingSystem = new Glicko2RatingCalculator(0.06, 0.5);
	private Glicko2Rating player = new Glicko2Rating("player", ratingSystem);
	private Glicko2Rating opp1 = new Glicko2Rating("opp1", ratingSystem);
	private Glicko2Rating opp2 = new Glicko2Rating("opp2", ratingSystem);
	private Glicko2Rating opp3 = new Glicko2Rating("opp3", ratingSystem);
	
	@Test
	public void test() {
		/**
		 * This test uses the values from Glickman's paper as a simple test of the calculation engine
		 */
		initialise();

		// test that the scaling works
		assertEquals( 0, player.getGlicko2Rating(), 0.00001 );
		assertEquals( 1.1513, player.getGlicko2RatingDeviation(), 0.00001 );

		RatingPeriodResults results = new RatingPeriodResults();
		
		results.addResult(player, opp1);
		results.addResult(opp2, player);
		results.addResult(opp3, player);
		
		ratingSystem.updateRatings(results);
		
		System.out.println("player " + player.getRating() + " / " + player.getRatingDeviation());
		System.out.println("opp1   " + opp1.getRating() + " / " + opp1.getRatingDeviation());
		System.out.println("opp2   " + opp2.getRating() + " / " + opp2.getRatingDeviation());
		System.out.println("opp3   " + opp3.getRating() + " / " + opp3.getRatingDeviation());

		// test that the player's new rating and deviation have been calculated correctly
		assertEquals( 1464.06, player.getRating(), 0.00001 );
		assertEquals( 151.52, player.getRatingDeviation(), 0.00001 );
	}
	
	private void initialise() {
		player.setRating(1500);
		opp1.setRating(1400);
		opp2.setRating(1550);
		opp3.setRating(1700);
		
		player.setRatingDeviation(200);
		opp1.setRatingDeviation(30);
		opp2.setRatingDeviation(100);
		opp3.setRatingDeviation(300);
	}

}
