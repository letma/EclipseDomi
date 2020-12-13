# Documentation

## How the game works till now:
First we check if the match has started.
Then the current positions are stored within an ArrayList localy using getAllPossiblePositions()
As long as the match is not over, we start generating a random index and position.
If the index is intersecting with an allready occupied position (avoid illegal moves), then we remove the index and position from our local position array.
Then the client waits till it is our turn to play.
When it is our turn, our turn is submitted.
After we received an answer, we check the position.
How checking the position works:
The client is calling checkPosition() with the next position.
We check if the given position is a valid move in all possible positions(Array we created at the start with size length x height). If it is, add that position to played position and remove the played position from all possible positions.
