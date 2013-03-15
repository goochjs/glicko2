/**
 * 
 */
package org.goochjs.glicko2;

/**
 * @author Jeremy Gooch
 *
 */


public class Result {
	private static final double POINTS_FOR_WIN = 1.0;
	private static final double POINTS_FOR_LOSS = 0.0;
	private static final double POINTS_FOR_DRAW = 0.5;
	
	private boolean isDraw = false;
	private Glicko2Rating winner;
	private Glicko2Rating loser;
	
	public Result(Glicko2Rating winner, Glicko2Rating loser) {
		if ( invalidPlayers(winner, loser) ) {
			throw new IllegalArgumentException();
		}

		this.winner = winner;
		this.loser = loser;
	}
	
	public Result(Glicko2Rating player1, Glicko2Rating player2, boolean isDraw) {
		if (! isDraw || invalidPlayers(player1, player2) ) {
			throw new IllegalArgumentException();
		}
		
		this.winner = player1;
		this.loser = player2;
		this.isDraw = true;
	}

	private boolean invalidPlayers(Glicko2Rating player1, Glicko2Rating player2) {
		if (player1.equals(player2)) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean participated(Glicko2Rating player) {
		if ( winner.equals(player) || loser.equals(player) ) {
			return true;
		} else {
			return false;
		}
	}
	
	public double getScore(Glicko2Rating player) throws IllegalArgumentException {
		double score;
		
		if ( winner.equals(player) ) {
			score = POINTS_FOR_WIN;
		} else if ( loser.equals(player) ) {
			score = POINTS_FOR_LOSS;			
		} else {
			throw new IllegalArgumentException("Player " + player.getUid() + " did not participate in match");
		}
		
		if ( isDraw ) {
			score = POINTS_FOR_DRAW;
		}
		
		return score;
	}
	
	public Glicko2Rating getOpponent(Glicko2Rating player) {
		Glicko2Rating opponent;
		
		if ( winner.equals(player) ) {
			opponent = loser;
		} else if ( loser.equals(player) ) {
			opponent = winner;			
		} else {
			throw new IllegalArgumentException("Player " + player.getUid() + " did not participate in match");
		}
		
		return opponent;
	}
	
	public Glicko2Rating getWinner() {
		return this.winner;
	}

	public Glicko2Rating getLoser() {
		return this.loser;
	}
}
