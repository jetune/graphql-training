/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.model.event;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEvent;

import fr.adservio.training.graphql.graphqlkata06pictureapispring.model.Picture;
import lombok.Getter;

/**
 * Event Class used to inform that new Picture is added to the database 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 21 mai 2020 - 19:27:32
 */
@Getter
public class PictureAddedEvent extends ApplicationEvent {
	
	/**
	 * Generatd ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Added Picture
	 */
	private Picture payload;
	
	/**
	 * Event timestamp
	 */
	private LocalDateTime time = LocalDateTime.now();

	/**
	 * Constructeur avec initialisation de parametres
	 */
	public PictureAddedEvent(Object source, Picture payload) {
		
		// Parent Call
		super(source);
		
		// Initialize payload
		this.payload = payload;
	}
}
