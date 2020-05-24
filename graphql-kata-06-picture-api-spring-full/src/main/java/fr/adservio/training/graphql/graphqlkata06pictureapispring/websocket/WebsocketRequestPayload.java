/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.adservio.training.graphql.graphqlkata06pictureapispring.controller.GraphQLRequestBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class used to store GraphQL Websocket Request body send by client
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 23 mai 2020 - 15:42:40
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Builder
public class WebsocketRequestPayload {
	
	/**
	 * Websocket message ID
	 */
	@JsonProperty(required = false)
	private String id;
	
	/**
	 * Messge type
	 */
	private String type;
	
	/**
	 * Message payload
	 */
	private GraphQLRequestBody payload;
}
