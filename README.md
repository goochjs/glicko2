glicko2
=======

Java implementation of the Glicko-2 rating algorithm

To use:
*   instantiate a _Glicko2RatingCalculator_ object
    *   you can tune the rating volatilities for your game at instantiation or just accept the defaults
*   instantiate a _Glicko2Rating_ object for each player
*   instantiate a _RatingPeriodResults_ object
*   add match results to the _RatingPeriodResults_ object until you reach the end of your rating period
    *   use _addResult(winner, loser)_ for matches that had an outcome
    *   use _addDraw(player1, player2)_ for matches that resulted in a draw or stalemate
*   once you've reached the end of your rating period, call the _updateRatings_ method against the _Glicko2RatingCalculator_, which takes the _RatingPeriodResults_ object as argument
    *   note that the _RatingPeriodResults_ object is cleared down once the new ratings have been calculated
    