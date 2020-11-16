package gamecontrol;

import netcode.GameComGrpc;
import netcode.Netcode.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.dominect.grpc.Dominect.GameParameter;
import com.dominect.grpc.Dominect.GameState;
import com.dominect.grpc.Dominect.GameTurn;

import gameboard.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
public class GameController {
	
	private static String userToken;
	private static String matchToken;
	static ArrayList<Positions> positions = new ArrayList<>();
	static ArrayList<Positions> allPositions;
	static GameComGrpc.GameComBlockingStub stub;
	static GameStatus currentStatus;
	static GameState currentState;
	static Boolean beginningPlayer;
	static int x1 = 0;
	static int y1 = 0;
	static int x2 = 0;
	static int y2 = 0;
	// Constructor
	public GameController(String userToken, GameComGrpc.GameComBlockingStub stub)
	{
		this.userToken = userToken;
		this.stub = stub;
	}
	
	//New Match
	public static void newMatch(int width, int height)
	{
		GameParameter gameParameters;
		MatchRequest matchRequest;
		MatchResponse matchResponse;
		String matchToken;
		
		
		gameParameters = GameParameter.newBuilder().setBoardWidth(width).setBoardHeight(height).build();
		matchRequest = MatchRequest.newBuilder()
				.setUserToken(userToken)
				.setGameToken("dom")
				.setDomGameParameters(gameParameters).build();
		
		matchResponse = stub.newMatch(matchRequest);
		String matchID = matchResponse.getMatchToken();
		setMatchToken(matchID); 
		beginningPlayer = matchResponse.getBeginningPlayer();
		System.out.println("New Match: " + getMatchToken());
		System.out.println("First player? " + ((beginningPlayer) ? "Yes" : "No"));
	}
	// Submit Turn
	public static TurnResponse submitTurn(GameTurn turn)
	{
		TurnRequest turnRequest;
		TurnResponse turnResponse;
		turnRequest = TurnRequest.newBuilder().setMatchId(createMatchID()).setDomGameTurn(turn).build();
		turnResponse = stub.submitTurn(turnRequest);
		printTurnStatus(turnResponse.getTurnStatusValue());
		return turnResponse;
		
	}
	public static void printTurnStatus(int status)
	{
		switch(status)
		{
		case netcode.Netcode.TurnStatus.OK_VALUE:
			System.out.println("Turn was OK!\n");
			break;
		case netcode.Netcode.TurnStatus.INVALID_TURN_VALUE:
			System.out.println("Turn was invalid!\n");
			break;
		case netcode.Netcode.TurnStatus.NOT_YOUR_TURN_VALUE:
			System.out.println("Not your turn!\n");
			break;
		case netcode.Netcode.TurnStatus.MATCH_OVER_VALUE:
			System.out.println("Match Over!\n");
			printGameStatus();
			break;
		default:
			assert(false);
		}
	}
	
	public static void printGameStatus()
	{
		switch(currentStatus)
		{
		case MATCH_NOT_STARTED:
			System.out.println("Game has not yet started. No one dares to approach you!\n");
			break;
		case YOUR_TURN:
			System.out.println("It's your turn :O\n");
			break;
		case OPPONENTS_TURN:
			System.out.println("It's not your turn!\n");
			break;
		case MATCH_WON:
			System.out.println("Game over, match won! :)\n");
			break;
		case MATCH_LOST:
			System.out.println("Game over, match lost! :(\n");
			break;
		case DRAW:
			System.out.println("Game over, it's a draw! :|\n");
			break;
		case MATCH_ABORTED:
			System.out.println("The match has been aborted.\n");
			break;
		default:
			assert(false);
		}
	}
	
	public static void showGameState()
	{
		queryGameState();
		int value;
		boolean firstCord = true;
		int width = currentState.getBoardWidth();
		int height = currentState.getBoardHeight();
		byte[] data = currentState.getBoardData().toByteArray();
		// opponent gets always other value
		int num = (getBeginningPlayer()) ? 50 : 49;
		System.out.println("-------------------------------\n");
		for (int y = 0; y < height; ++y)
		{
			for (int x = 0; x < width; ++x)
			{
				value = data[x + y * width];
				// we check that value for an opponent and than check if it's inserted in played positions
				if(value == num && (!checkCoordination(x, y)))
				{
					if(firstCord)
					{
						x1 = x;
						y1 = y;
						firstCord = false;
					}
					else
					{
						x2 = x;
						y2 = y;
					}
				}
				System.out.print((char)data[x + y * width]);
			}
			System.out.print("\n");
		}
		System.out.println("-------------------------------\n");
		System.out.println("Found: " + y1 + x1 + y2 + x2);
		Positions newEntry = new Positions(x1,y1,x2,y2);
		checkPosition(newEntry);
		printGameStatus();
	}
	// Check if the position was already inserted in an array!
	public static boolean checkCoordination(int x, int y)
	{
		Iterator<Positions> it = positions.iterator();
		while(it.hasNext())
		{
			Positions position = it.next();
			if((position.getX1() == x && position.getY1() == y) 
			|| (position.getX2() == x && position.getY2() == y))
			{
				return true;
			}
		}
		return false;
	}
	// check if the given position is a valid move in all possible positions.
	// IF it is add that position to played positions and remove played position
	// from all possible positions
	public static void checkPosition(Positions newEntry)
	{
		allPositions = getAllPossiblePositions();
		Iterator<Positions> it = allPositions.iterator();
		while(it.hasNext())
		{
			Positions position = it.next();
			if(position.equals(newEntry))
			{
				System.out.println("Position  Y1: " + position.getY1() + " X1: " + position.getX1() +  " Y2: " 
			+ position.getY2() + " X2: " + position.getX2() +  " is valid\n");
				positions.add(position);
				it.remove();
			}
		}
	}
	// check if any coordination of the given position intersects in ANY way
	// with the Position we want to play ( additional check so that we don't get invalid move ) 
	public static boolean checkIntersectPosition(Positions newEntry)
	{
		Iterator<Positions> it = positions.iterator();
		while(it.hasNext())
		{
			Positions position = it.next();
			if(position.intersects(newEntry))
			{
				return true;
			}
		}
		return false;
	}
	

	public static void queryGameState()
	{
		//TODO: Probly need some pointer shit or something
		MatchIDPacket matchIDPacket = createMatchID();
		GameStateResponse gameStateResponse;
		
		gameStateResponse = stub.getGameState(matchIDPacket);
		//TODO: Error check?
		currentState = gameStateResponse.getDomGameState();
		currentStatus = gameStateResponse.getGameStatus();
	}
	
	public static Boolean isMatchOver()
	{
		return (currentStatus == netcode.Netcode.GameStatus.MATCH_WON) ||
			   (currentStatus == netcode.Netcode.GameStatus.MATCH_LOST)||
			   (currentStatus == netcode.Netcode.GameStatus.DRAW)      ||
			   (currentStatus == netcode.Netcode.GameStatus.MATCH_ABORTED);
	}
	
	public static Boolean isTurnPlayable()
	{
		switch(currentStatus)
		{
		case YOUR_TURN:
			return true;
		case OPPONENTS_TURN:
			return false;
		case MATCH_WON:
		case MATCH_LOST:
		case DRAW:
		case MATCH_NOT_STARTED:
		case MATCH_ABORTED:
		default: 
			assert(false);
		}
		return null;
	}
	
	public static MatchIDPacket createMatchID()
	{
		MatchIDPacket matchIDPacket = MatchIDPacket.newBuilder().setUserToken(getUserToken()).setMatchToken(getMatchToken()).build();
		return(matchIDPacket);
	}
	
	public static void queryTimeout()
	{
		MatchIDPacket matchIDPacket = createMatchID();
		GetTimeoutResponse timeoutResponse = stub.getTimeout(matchIDPacket);
		System.out.println("Negotiated timeout: " + timeoutResponse.getTimeoutSeconds() + " seconds\n");
	}
	
	public static void queryOpponentInfo()
	{
		MatchIDPacket matchIDPacket = createMatchID();
		OpponentInfoResponse opponentInfoResponse = stub.getOpponentInfo(matchIDPacket);	
		System.out.println("Opponent: " + opponentInfoResponse.getUserPseudonym() + 
				" from group" + opponentInfoResponse.getGroupPseudonym());
		
	}
	
	public static Boolean hasMatchStarted()
	{
		queryGameState();
		return (currentStatus != netcode.Netcode.GameStatus.MATCH_NOT_STARTED);
	}
	
	public static ArrayList<Positions>  getAllPossiblePositions() {
		int width = currentState.getBoardWidth();
		int height = currentState.getBoardHeight();
        ArrayList<Positions> allPositions = new ArrayList<>(width*height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	if(x+1 < width)
            	{
            		allPositions.add(new Positions(x, y, x + 1, y));
            	}
            	if(y+1 < height)
            	{
            		allPositions.add(new Positions(x, y, x, y + 1));
            	}
            	
            }

        }
        return allPositions;
    }
	// GETTERS
	public static String getMatchToken()
	{
		return matchToken;
	}
	
	public static String getUserToken()
	{
		return userToken;
	}
	
	public static Boolean getBeginningPlayer()
	{
		return beginningPlayer;
	}
	
	//SETTERS
	
	public static void setMatchToken(String matchID)
	{
		matchToken = matchID;
	}
	
	public static void setBeginningPlayer(boolean ply)
	{
		beginningPlayer = ply;
	}

}