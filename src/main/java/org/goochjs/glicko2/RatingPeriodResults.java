/*
 * Copyright (C) 2013 Jeremy Gooch <http://www.linkedin.com/in/jeremygooch/>
 *
 * The licence covering the contents of this file is described in the file LICENCE.txt,
 * which should have been included as part of the distribution containing this file.
 */

package org.goochjs.glicko2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jeremy Gooch
 *
 * This class holds the results accumulated over a rating period
 * 
 */
public class RatingPeriodResults {
	private List<Result> results = new ArrayList<Result>();
	private Set<Rating> participants = new HashSet<Rating>();

	public RatingPeriodResults() {}
	
	public RatingPeriodResults(Set<Rating> participants) {
		// this constructor allows you to initialise the list of participants
		this.participants = participants;
	}
	
	public void addResult(Rating winner, Rating loser) {
		Result result = new Result(winner, loser);
		
		results.add(result);
	}
	
	public void addDraw(Rating player1, Rating player2) {
		Result result = new Result(player1, player2, true);
		
		results.add(result);
	}
	
	public List<Result> getResults(Rating player) {
		List<Result> filteredResults = new ArrayList<Result>();
		
		for ( Result result : results ) {
			if ( result.participated(player) ) {
				filteredResults.add(result);
			}
		}
		
		return filteredResults;
	}

	public Set<Rating> getParticipants() {
		// run through the results and make sure all players have been pushed into the participants set
		for ( Result result : results ) {
			participants.add(result.getWinner());
			participants.add(result.getLoser());
		}

		return participants;
	}
	
	public void addParticipants(Rating rating) {
		// add a participant to the rating period, so that their rating will start to be calculated even if they don't compete
		participants.add(rating);
	}
	
	public void clear() {
		results.clear();
	}
}
