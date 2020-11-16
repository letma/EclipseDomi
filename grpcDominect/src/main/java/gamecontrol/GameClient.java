package gamecontrol;
import java.util.Random;
import netcode.GameComGrpc;
import netcode.Netcode.*;
import gamecontrol.GameController.*;
import gamecontrol.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.dominect.grpc.Dominect.GameTurn;

import gameboard.Position;

/**
 * Created by rayt on 5/16/16.
 */
public class GameClient {
	final static CountDownLatch finishLatch = new CountDownLatch(1);
	static GameComGrpc.GameComBlockingStub stub;
	public static void autoPlay(GameController client)
	{
		client.showGameState();
		
		if(client.hasMatchStarted())
		{
			ArrayList<Positions> positions = client.getAllPossiblePositions();
			System.out.println("Position size: " + positions.size());
			int index;
			Random randomGenerator = new Random();
			Positions nextPos;
			Positions copyPos;
			TurnResponse odg;
			System.out.println("Match Started");
			while(!client.isMatchOver())
			{
				index = randomGenerator.nextInt(positions.size());
				nextPos = positions.get(index);
				if(client.checkIntersectPosition(nextPos))
				{
					positions.remove(index);
					nextPos = null;
				}
				if(client.currentStatus == netcode.Netcode.GameStatus.YOUR_TURN && nextPos != null)
				{
					GameTurn first = GameTurn.newBuilder()
							.setX1(nextPos.getX1())
							.setY1(nextPos.getY1())
							.setX2(nextPos.getX2())
							.setY2(nextPos.getY2()).build();
					
					
					odg = client.submitTurn(first);
					client.checkPosition(nextPos);
					System.out.print("My turn: ");
					client.printTurnStatus(odg.getTurnStatusValue());
				}
				
				//client.checkPosition(nextPos);
				client.showGameState();
				
			}
			
		}
		

		//TODO: Now we can start to implement AI
		
	}
    public static void main(String[] args) throws InterruptedException {

        // Insert your user token
        String userToken = "";
        int boardWidth = 8;
        int boardHeight = 8;
        

        ManagedChannel channel = ManagedChannelBuilder.forAddress("129.27.202.46", 80)
                .usePlaintext()
                .build();
        //TODO: We need simple connection check/timeout on channel
        
        stub = GameComGrpc.newBlockingStub(channel);

        GameController client = new GameController(userToken, stub);
        
        System.out.println("Requesting new match\n");
        client.newMatch(boardWidth, boardHeight);
        
        System.out.println("Waiting for opponent...\n");
        while(!client.hasMatchStarted())
        {
        	finishLatch.await(2, TimeUnit.SECONDS);
        	System.out.println(".");
        }
        System.out.println("\n Opponent found! \n");
        client.queryOpponentInfo();
        client.queryTimeout();
        autoPlay(client);
        channel.shutdown();
    }
}