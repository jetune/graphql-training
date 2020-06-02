/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.model.event;

import java.io.IOException;

import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import fr.adservio.training.graphql.graphqlkata06pictureapispring.tools.JSonTools;
import fr.adservio.training.graphql.graphqlkata06pictureapispring.websocket.GraphQLWSMessageType;
import fr.adservio.training.graphql.graphqlkata06pictureapispring.websocket.WebsocketResponsePayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class used to listen picture added event for a given websocket session that subscribe to that event 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 24 mai 2020 - 14:44:54
 */
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class PictureAddedEventListener implements ApplicationListener<PictureAddedEvent> {
	
	/**
	 * Websocket session
	 */
	private WebSocketSession session;
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(PictureAddedEvent event) {
		
		// Message to send
		WebsocketResponsePayload response = WebsocketResponsePayload.builder()
				.id("1")
				.type(GraphQLWSMessageType.DATA.getValue())
				.payload(event.getPayload())
				.build();
		
		// ext message to push
		TextMessage message = new TextMessage(JSonTools.toJsonString(response));
		
		// Log
		log.info("Message to Push : [{}]", JSonTools.toJsonString(response));
		
		try {
			
			// Push message to client
			session.sendMessage(message);
			
		} catch (IOException e) {
			
			// Print exception stack trace
			e.printStackTrace();
		}
	}
}
