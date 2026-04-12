package org.gam.dedatebackend.Config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //client to direct server
        registry.setApplicationDestinationPrefixes("/api");//all the request coming from the api goes to controller not to handler
        //simple Broker
        registry.enableSimpleBroker("/broker1","/broker2");


        //client to RabbitMQ broker
//        registry.enableStompBrokerRelay("/topic", "/queue")
//                .setRelayHost("localhost")
//                .setRelayPort(61613)
//                .setClientLogin("guest")
//                .setClientPasscode("guest")
//                .setSystemLogin("guest")
//                .setSystemPasscode("guest");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173", "http://localhost:3000")
                .withSockJS();
    }
}
