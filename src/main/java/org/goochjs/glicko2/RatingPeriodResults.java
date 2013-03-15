/**
 * 
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
	private static List<Result> results = new ArrayList<Result>();

	public void addResult(Glicko2Rating winner, Glicko2Rating loser) {
		Result result = new Result(winner, loser);
		
		results.add(result);
	}
	
	public void addDraw(Glicko2Rating player1, Glicko2Rating player2) {
		Result result = new Result(player1, player2, true);
		
		results.add(result);
	}
	
	public List<Result> getResults(Glicko2Rating player) {
		List<Result> filteredResults = new ArrayList<Result>();
		
		for ( Result result : results ) {
			if ( result.participated(player) ) {
				filteredResults.add(result);
			}
		}
		
		return filteredResults;
	}

	public Set<Glicko2Rating> getParticipants() {
		Set<Glicko2Rating> participants = new HashSet<Glicko2Rating>(); 
		
		for ( Result result : results ) {
			participants.add(result.getWinner());
			participants.add(result.getLoser());
		}

		return participants;
	}
	
	public void clear() {
		results.clear();
	}
}
