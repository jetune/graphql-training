/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Websocket configurer class 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 21 mai 2020 - 20:03:08
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	/**
	 * Handler
	 */
	@Autowired
	private WebSocketHandler handler;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.config.annotation.WebSocketConfigurer#registerWebSocketHandlers(org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry)
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		// Add the Handler
		registry.addHandler(handler, "/api").setAllowedOrigins("*").addInterceptors(new CustomHandshakeInterceptor());
	}
}
