/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.websocket;

import java.util.List;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * Class used to intercept handshake
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 23 mai 2020 - 11:05:09
 */
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

	/* (non-Javadoc)
	 * @see org.springframework.web.socket.server.HandshakeInterceptor#beforeHandshake(org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse, org.springframework.web.socket.WebSocketHandler, java.util.Map)
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		// Always return true (accept procced handshake)
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.socket.server.HandshakeInterceptor#afterHandshake(org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse, org.springframework.web.socket.WebSocketHandler, java.lang.Exception)
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
		// Get the 'Sec-WebSocket-Protocol' header
		List<String> secHeaders = request.getHeaders().get("Sec-WebSocket-Protocol");
		
		// Add the header in the response
		secHeaders.forEach(h -> response.getHeaders().add("Sec-WebSocket-Protocol", h));
	}
}
