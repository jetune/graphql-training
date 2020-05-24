/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.websocket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import fr.adservio.training.graphql.graphqlkata06pictureapispring.model.event.PictureAddedEventListener;
import fr.adservio.training.graphql.graphqlkata06pictureapispring.tools.JSonTools;
import lombok.extern.slf4j.Slf4j;

/**
 * Websocket handler class used to manager bidirectional communication between GraphQL Client subscribers and GraphQL Server 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 21 mai 2020 - 19:21:55
 */
@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
	
	/**
	 * Listener registry
	 */
	@Autowired
	private ApplicationEventMulticaster applicationListenerRegistry;
	
	/**
	 * Map of Picture listener by connected session ID
	 */
	private Map<String, PictureAddedEventListener> listeners = new HashMap<>();
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionEstablished(org.springframework.web.socket.WebSocketSession)
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		// Parent call
		super.afterConnectionEstablished(session);
		
		// Log
		log.info("GraphQL Client connected : [Session ID : {}]", session.getId());
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionClosed(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.CloseStatus)
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		// Parent call
		super.afterConnectionClosed(session, status);
		
		// Add session in the MAP
		PictureAddedEventListener listener = listeners.remove(session.getId());
		
		// Unregister the listener
		if(listener != null) applicationListenerRegistry.removeApplicationListener(listener);
		
		// Log
		log.info("GraphQL Client disconnected : [Session ID : {}]", session.getId());
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#handleTextMessage(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.TextMessage)
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		// Parent call
		super.handleTextMessage(session, message);
		
		// Print the text
		log.info("Text Message to compute : [{}]", message.getPayload());
		
		// Map to Object
		WebsocketRequestPayload graphQLClientMessagePayload = JSonTools.fromJson(message.getPayload(), WebsocketRequestPayload.class);
		
		// If message type is 'start'
		if(graphQLClientMessagePayload.getType() != null && 
				graphQLClientMessagePayload.getType().trim().equalsIgnoreCase(GraphQLWSMessageType.START.getValue())) {
			
			// Instantiate a listener
			PictureAddedEventListener listener = new PictureAddedEventListener(session);
			
			// Add the listener to the map
			listeners.put(session.getId(), listener);
			
			// Register Listener with Spring listener context
			applicationListenerRegistry.addApplicationListener(listener);
			
		} else if (graphQLClientMessagePayload.getType() != null && 
				graphQLClientMessagePayload.getType().trim().equalsIgnoreCase(GraphQLWSMessageType.CONNECTION_INIT.getValue())) {

			// Message to send
			WebsocketResponsePayload response = WebsocketResponsePayload.builder()
					.type(GraphQLWSMessageType.CONNECTION_ACK.getValue())
					.build();
			
			// Text message to push
			TextMessage wsMessage = new TextMessage(JSonTools.toJsonString(response));
			
			// Sed the message
			session.sendMessage(wsMessage);
		}
	}
}
