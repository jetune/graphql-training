/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * GraphQL message type enumeration
 * 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE
 *         NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 23 mai 2020 - 21:25:42
 */
@AllArgsConstructor
@Getter
public enum GraphQLWSMessageType {

    /**
     * Connexion init
     */
    CONNECTION_INIT("connection_init"),

    /**
     * Connexion Acknowlegment
     */
    CONNECTION_ACK("connection_ack"),

    /**
     * Connexion error
     */
    CONNECTION_ERROR("connection_error"),

    /**
     * Connexion Keep Alive
     */
    CONNECTION_KEEP_ALIVE("ka"),

    /**
     * Start send message
     */
    START("start"),

    /**
     * Stop Send message
     */
    STOP("stop"),

    /**
     * Connexion terminate
     */
    CONNECTION_TERMINATE("connection_terminate"),

    /**
     * Send data
     */
    DATA("data"),

    /**
     * Send error
     */
    ERROR("error"),

    /**
     * Complete
     */
    COMPLETE("complete");

    /**
     * Enumeration value
     */
    private String value;
}
