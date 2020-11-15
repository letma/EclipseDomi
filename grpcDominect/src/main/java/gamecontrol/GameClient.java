package gamecontrol;

import netcode.GameComGrpc;
import netcode.Netcode.*;
import gamecontrol.GameController.*;
import gamecontrol.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.dominect.grpc.Dominect.GameTurn;

/**
 * Created by rayt on 5/16/16.
 */
public class GameClient {
	final static CountDownLatch finishLatch = new CountDownLatch(1);
	static GameComGrpc.GameComBlockingStub stub;
	public static void autoPlay(GameController client)
	{
		client.showGameState();
		GameTurn first = GameTurn.newBuilder().setX1(1).setY1(2).setX2(1).setY2(3).build();
		TurnResponse odg = client.submitTurn(first);

		for(Positions d: client.positions)
		{
			System.out.println("X1: " + d.getX1() + " Y1: " + d.getY1() + " X2: " + d.getX2() + " Y2: " + d.getY2() + "\n");
		}

		//TODO: Now we can start to implement AI
		
	}
    public static void main(String[] args) throws InterruptedException {

        // Insert your user token
        String userToken = "";
        int boardWidth = 7;
        int boardHeight = 7;
        

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