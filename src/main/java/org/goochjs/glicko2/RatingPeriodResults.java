/*
 * Copyright (C) 2013 Jeremy Gooch <http://www.linkedin.com/in/jeremygooch/>
 *
 * The licence covering the contents of this file is described in the file LICENCE.txt,
 * which should have been included as part of the distribution containing this file.
 */
package org.goochjs.glicko2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class holds the results accumulated over a rating period.
 * 
 * @author Jeremy Gooch
 */
public class RatingPeriodResults {
	private Map<Rating, List<Result>> results = new HashMap<Rating, List<Result>>();
	private Set<Rating> participants = new HashSet<Rating>();

	
	/**
	 * Create an empty resultset.
	 */
	public RatingPeriodResults() {}
	

	/**
	 * Constructor that allows you to initialise the list of participants.
	 * 
	 * @param participants (Set of Rating objects)
	 */
	public RatingPeriodResults(Set<Rating> participants) {
		this.participants = participants;
	}
	
	
	/**
	 * Add a result to the set.
	 * 
	 * @param winner
	 * @param loser
	 */
	public void addResult(Rating winner, Rating loser) {
		this.addResult(new Result(winner, loser));
	}
	
	
	/**
	 * Record a draw between two players and add to the set.
	 * 
	 * @param player1
	 * @param player2
	 */
	public void addDraw(Rating player1, Rating player2) {
		this.addResult(new Result(player1, player2, true));
	}


	private void addResult(Result result) {
		List<Result> playerResults = results.get(result.getWinner());

		if (playerResults == null) {
			playerResults = new ArrayList<Result>();
			results.put(result.getWinner(), playerResults);
		}

		playerResults.add(result);

		playerResults = results.get(result.getLoser());

		if (playerResults == null) {
			playerResults = new ArrayList<Result>();
			results.put(result.getLoser(), playerResults);
		}

		playerResults.add(result);
	}


	/**
	 * Get a list of the results for a given player.
	 * 
	 * @param player
	 * @return List of results
	 */
	public List<Result> getResults(Rating player) {
		List<Result> playerResults = results.get(player);

		if (playerResults != null) {
			return playerResults;
		}

		return Collections.emptyList();
	}

	
	/**
	 * Get all the participants whose results are being tracked.
	 * 
	 * @return set of all participants covered by the resultset.
	 */
	public Set<Rating> getParticipants() {
		// Run through the results and make sure all players have been pushed into the participants set.
		participants.addAll(results.keySet());

		return participants;
	}
	
	
	/**
	 * Add a participant to the rating period, e.g. so that their rating will
	 * still be calculated even if they don't actually compete.
	 *
	 * @param rating
	 */
	public void addParticipants(Rating rating) {
		participants.add(rating);
	}
	
	
	/**
	 * Clear the resultset.
	 */
	public void clear() {
		results.clear();
	}
}
