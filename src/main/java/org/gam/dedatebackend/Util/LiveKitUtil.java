package org.gam.dedatebackend.Util;


import io.livekit.server.*;
import org.gam.dedatebackend.Enum.Team;
import org.gam.dedatebackend.Model.Contest.Room.ContestRoom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LiveKitUtil {
    @Value("${livekit.api.key}")
    String apiKey;
    @Value("${livekit.api.secretKey}")
    String apiSecret;
    public String generateToken(ContestRoom contestRoom, String userName, Team team) {
        AccessToken token = new AccessToken(apiKey, apiSecret);
        token.setIdentity(userName);
        token.setName(userName);
        token.addGrants(
                new RoomJoin(true),
                new RoomName(contestRoom.getId()), // This links the token to your Room ID
                new CanPublish(true),
                new CanSubscribe(true),
                new CanPublishData(true)
        );
        token.setMetadata("{\"Team\":\""+team+"\", \"UserName\":\""+userName+"\"}");
        return token.toJwt();
    }
    public String generateTokenForAudience(ContestRoom contestRoom, String userName, Team team) {
        AccessToken token = new AccessToken(apiKey, apiSecret);
        token.setIdentity(userName);
        token.setName(userName);
        token.addGrants(
                new RoomJoin(true),
                new RoomName(contestRoom.getId()), // This links the token to your Room ID
                new CanPublish(false),
                new CanSubscribe(true),
                new CanPublishData(false)
        );
        token.setMetadata("{\"Team\":\""+team+"\", \"UserName\":\""+userName+"\"}");
        return token.toJwt();
    }
}
