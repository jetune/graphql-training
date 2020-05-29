/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.graphql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.adservio.training.graphql.graphqlkata06pictureapispring.model.Picture;
import fr.adservio.training.graphql.graphqlkata06pictureapispring.model.event.PictureAddedEvent;
import fr.adservio.training.graphql.graphqlkata06pictureapispring.repository.InMemoryPictureDB;
import graphql.schema.DataFetcher;

/**
 * Picture Data fetcher used to map Picture type operation on graphQL client queries 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 20 mai 2020 - 15:16:06
 */
@Component
public class PictureDataFetcher {
	
	/**
	 * Picture database
	 */
	@Autowired
	private InMemoryPictureDB pictureDB;

	/**
	 * Producteur d'évènements applicatif
	 */
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	
	/**
	 * Method used to build a data fetcher for "allPictures" query operation 
	 * @return	Data fetcher for "allPictures" query operation
	 */
	public DataFetcher<List<Picture>> getAllPictureFetcher() {
		
		// Return the Data fetcher
		return environment -> pictureDB.getAllPictures();
	}
	
	/**
	 * Method used to build a data fetcher for "totalPictures" query operation 
	 * @return	Data fetcher for "totalPictures" query operation
	 */
	public DataFetcher<Integer> getPictureCountFetcher() {
		
		// Return the Data fetcher
		return environment -> pictureDB.getPictureCount();
	}
	
	/**
	 * Method used to build a data fetcher for "totalPictures" mutation operation 
	 * @return	Data fetcher for "totalPictures" mutation operation
	 */
	public DataFetcher<Picture> addPictureFetcher() {
		
		// Return the Data fetcher
		return environment -> {
			
			// Build Picture from MAP
			Picture inputPicture = new ObjectMapper().convertValue(environment.getArgument("picture"), Picture.class);
			
			// Add picture in Database
			Picture addedPicture = pictureDB.addPicture(inputPicture);

			// Publish event
			eventPublisher.publishEvent(new PictureAddedEvent(this, addedPicture));
			
			// Add and return added picture
			return addedPicture;
		};
	}
}
