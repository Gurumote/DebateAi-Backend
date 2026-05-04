package org.gam.dedatebackend.Config;


import io.livekit.server.RoomServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiveKitConfig {
    @Value("${livekit.api.key}")
    String apiKey;

    @Value("${livekit.api.host}")
    String host;

    @Value("${livekit.api.secretKey}")
    String apiSecret;

    @Bean
    public RoomServiceClient roomServiceClient(){
     return  RoomServiceClient.createClient(host, apiKey, apiSecret);
    }
}
