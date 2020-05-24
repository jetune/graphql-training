/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.websocket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.adservio.training.graphql.graphqlkata06pictureapispring.model.event.PictureAddedEvent;

/**
 * Websocket handler class used to manager bidirectional communication between GraphQL Client subscribers and GraphQL Server 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 21 mai 2020 - 19:21:55
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {
	
	/**
	 * Map of connected by ID
	 */
	private Map<String, WebSocketSession> sessions = new HashMap<>();
	
	/**
	 * JSon Mapper
	 */
	private ObjectMapper mapper = new ObjectMapper();
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionEstablished(org.springframework.web.socket.WebSocketSession)
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		// Parent call
		super.afterConnectionEstablished(session);
		
		// Add session in the MAP
		sessions.put(session.getId(), session);
		
		// Log
		System.out.println(String.format("GraphQL Client connected : [Session ID : %s]", session.getId()));
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionClosed(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.CloseStatus)
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		// Parent call
		super.afterConnectionClosed(session, status);
		
		// Add session in the MAP
		sessions.remove(session.getId());

		// Log
		System.out.println(String.format("GraphQL Client disconnected : [Session ID : %s]", session.getId()));
	}
	
	/**
	 * Picture Added method Listener 
	 * @param event	Picture added event
	 */
	@EventListener
	public void pictureAddedEventListener(PictureAddedEvent event) {
		
		// Iterate on sessions
		sessions.forEach((id, session) -> {
			
			try {
				
				// Log
				System.out.println(String.format("Picture created : [ID: %s, Name: %s, Category: %s, URL: %s]", 
						event.getPayload().getId(),
						event.getPayload().getName(),
						event.getPayload().getCategory(),
						event.getPayload().getUrl()));
				
				// Publish event to all websocket sessions
				session.sendMessage(new TextMessage(mapper.writeValueAsString(event.getPayload())));
				
			} catch (Exception e) {
				
				// Print exception stack trace
				e.printStackTrace();
			}
		});
	}
}
