package gruppe_b.quizduell.lobbyserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketSecurityConfig.class);

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        // messages.simpTypeMatchers(SimpMessageType.CONNECT,
        // SimpMessageType.DISCONNECT, SimpMessageType.OTHER).permitAll()
        // .anyMessage().authenticated();
        // messages.anyMessage().authenticated();
        messages.anyMessage().permitAll(); // TODO authentication will aktuell
        // nicht...
        logger.info("--> Message security set");
    }

    @Override
    protected boolean sameOriginDisabled() {
        logger.info("--> Same Origin policy set");
        return true;
    }
}
