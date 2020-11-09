

import netcode.GameComGrpc;
import netcode.Netcode.*;
import gamecontrol.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by rayt on 5/16/16.
 */
public class GameClient {
	final static CountDownLatch finishLatch = new CountDownLatch(1);
	static GameComGrpc.GameComBlockingStub stub;
	public static void autoPlay(GameController client)
	{
		//TODO: Now we can start to implement AI
		client.showGameState();
	}
    public static void main(String[] args) throws InterruptedException {

        // Insert your user token
        String userToken = "34ceec80d3066857ae38e494e9d2a832567f50b79faa475b1a845c2a3db72e9d";
        int boardWidth = 10;
        int boardHeight = 10;
        

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