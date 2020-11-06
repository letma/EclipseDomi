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

public class GameController {
	
	private static String userToken;
	private static String matchToken;
	
	static GameComGrpc.GameComBlockingStub stub;
	static GameStatus currentStatus;
	static GameState currentState;
	
	
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
		Boolean beginningPlayer;
		
		gameParameters = GameParameter.newBuilder().setBoardWidth(width).setBoardHeight(height).build();
		matchRequest = MatchRequest.newBuilder()
				.setUserToken(userToken)
				.setGameToken("dom")
				.setDomGameParameters(gameParameters).build();
		
		matchResponse = stub.newMatch(matchRequest);
		matchToken = matchResponse.getMatchToken();
		beginningPlayer = matchResponse.getBeginningPlayer();
		
		System.out.println("New Match: " + matchToken);
		System.out.println("First player? " + ((beginningPlayer) ? "Yes" : "No"));
	}
	// Submit Turn
	public static void submitTurn(GameTurn turn)
	{
		TurnRequest turnRequest;
		TurnResponse turnResponse;
		turnRequest = TurnRequest.newBuilder().setMatchId(createMatchID()).setDomGameTurn(turn).build();
		turnResponse = stub.submitTurn(turnRequest);
		//TODO: Expand error handling try/catch
		System.out.println("Turn Status: " + turnResponse.getTurnStatusValue());
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
		//TODO: This needs solution???
		queryGameState();
		
		int width = currentState.getBoardWidth();
		int height = currentState.getBoardHeight();
		byte[] data = currentState.getBoardData().toByteArray();
		System.out.println("-------------------------------\n");
		for (int y = 0; y < height; ++y)
		{
			for (int x = 0; x < width; ++x)
			{
				System.out.println(data[x + y * width]);
			}
			System.out.println("\n");
		}
		System.out.println("-------------------------------\n");
		printGameStatus();
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
		MatchIDPacket matchIDPacket = MatchIDPacket.newBuilder().setUserToken(userToken).setMatchToken(matchToken).build();
		return(matchIDPacket);
	}
	
	public static void queryTimeout()
	{
		MatchIDPacket matchIDPacket = createMatchID();
		GetTimeoutResponse timeoutResponse = stub.getTimeout(matchIDPacket);
		System.out.println("Negotiated timeout: " + timeoutResponse.getTimeoutSeconds());
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

}