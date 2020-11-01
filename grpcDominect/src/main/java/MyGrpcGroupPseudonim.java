

import netcode.GameComGrpc;
import netcode.Netcode.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by rayt on 5/16/16.
 */
public class MyGrpcGroupPseudonim {
    public static void main(String[] args) throws InterruptedException {

        // Insert your data here
        String matrNr = "1231661";
        String secret = "biliciku";
        String pseudonym = "dart";

        ManagedChannel channel = ManagedChannelBuilder.forAddress("129.27.202.46", 80)
                .usePlaintext()
                .build();

        GameComGrpc.GameComBlockingStub stub =
        		GameComGrpc.newBlockingStub(channel);
        AuthPacket auth = AuthPacket.newBuilder().setMatrNumber(matrNr).setSecret(secret).build();
        SetPseudonymRequest request = SetPseudonymRequest.newBuilder().setAuth(auth).setPseudonym(pseudonym).build();
        SetPseudonymResponse response;

        response = stub.setGroupPseudonym(request);
        System.out.println(response.getErrorCodeValue());

        channel.shutdown();
    }
}