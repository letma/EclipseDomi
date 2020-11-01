

import netcode.GameComGrpc;
import netcode.Netcode.*;

import java.util.Iterator;
import java.util.Vector;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by rayt on 5/16/16.
 */
public class MyGrpcRegGroup {
    public static void main(String[] args) throws InterruptedException {

        // Insert your data here
        String matrNr = "1231661";
        String secret = "biliciku";
        Vector<String> groupMemberMatrNumbers = new Vector<>();
        groupMemberMatrNumbers.add("1231648");
        groupMemberMatrNumbers.add("1331614");
        groupMemberMatrNumbers.add("1231855");
        groupMemberMatrNumbers.add("1231661");
        

        ManagedChannel channel = ManagedChannelBuilder.forAddress("129.27.202.46", 80)
                .usePlaintext()
                .build();

        GameComGrpc.GameComBlockingStub stub =
        		GameComGrpc.newBlockingStub(channel);
        

        AuthPacket auth = AuthPacket.newBuilder().setMatrNumber(matrNr).setSecret(secret).build();
        
        GroupRegistrationRequest request = GroupRegistrationRequest.newBuilder().setAuth(auth).addAllMatrNumber(groupMemberMatrNumbers).build();
        
        GroupRegistrationResponse response = stub.groupRegistration(request);
        System.out.println(response.getErrorCodeValue());

        channel.shutdown();
    }
}