glicko2
=======

OSGi-compatible Java implementation of the [Glicko-2 rating algorithm](http://www.glicko.net/glicko/glicko2.pdf "Example of the Glicko-2 system")

To use:
*   instantiate a _RatingCalculator_ object
    *   at instantiation, you can set the default rating for a player's volatility and the system constant for your game ("τ", which constrains changes in volatility over time) or just accept the defaults
*   instantiate a _Rating_ object for each player
*   instantiate a _RatingPeriodResults_ object
*   add game results to the _RatingPeriodResults_ object until you reach the end of your rating period
    *   use _addResult(winner, loser)_ for games that had an outcome
    *   use _addDraw(player1, player2)_ for games that resulted in a draw
*   once you've reached the end of your rating period, call the _updateRatings_ method against the _RatingCalculator_; this takes the _RatingPeriodResults_ object as argument
    *   note that the _RatingPeriodResults_ object is cleared down of game results once the new ratings have been calculated
    *   participants remain within the _RatingPeriodResults_ object, however, and will have their rating deviations recalculated at the end of future rating periods even if they don't play any games.  This is in-line with Glickman's algorithm
*   access the _getRating_, _getRatingDeviation_ and _getVolatility_ methods of each player's _Rating_ to see the new values
*   repeat steps 4 and 5 for each future rating period




___________________

Copyright (C) 2013 Jeremy Gooch <http://www.linkedin.com/in/jeremygooch/>

The licence covering the contents of this file is described in the file LICENCE.txt, which should have been included as part of the distribution containing this file.

