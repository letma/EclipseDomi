

import netcode.GameComGrpc;
import netcode.Netcode.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by rayt on 5/16/16.
 */
public class MyGrpcClient {
    public static void main(String[] args) throws InterruptedException {

        // Insert your data here
        String email = "dzenis.kajtazovic@student.tugraz.at";
        String fullname = "Dzenis Kajtazovic";
        String matrNr = "1231661";
        String secret = "biliciku";

        //Kommentar laut Thomas
        ManagedChannel channel = ManagedChannelBuilder.forAddress("129.27.202.46", 80)
                .usePlaintext()
                .build();

        GameComGrpc.GameComBlockingStub stub =
        		GameComGrpc.newBlockingStub(channel);
        UserRegistrationRequest request = UserRegistrationRequest.newBuilder()
        		.setFullname(fullname)
                .setEmail(email)
                .setMatrNumber(matrNr)
                .setSecret(secret).build();

        UserRegistrationResponse response = stub.userRegistration(request);
        System.out.println(response.getErrorCodeValue());

        channel.shutdown();
    }
}