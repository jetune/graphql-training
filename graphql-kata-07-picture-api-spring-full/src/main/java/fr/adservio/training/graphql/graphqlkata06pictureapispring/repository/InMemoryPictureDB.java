/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.repository;


import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import fr.adservio.training.graphql.graphqlkata06pictureapispring.model.Picture;
import fr.adservio.training.graphql.graphqlkata06pictureapispring.model.PictureCategory;

/**
 * In mmemory picture database 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 20 mai 2020 - 14:21:49
 */
@Component
public class InMemoryPictureDB {
	
	/**
	 * Picture list
	 */
	private List<Picture> pictures;
	
	/**
	 * Picture URL Model
	 */
	private static String PICTURE_URL_MODEL = "http://lab.adservio.fr/media/%s.jpg";
	
	/**
	 * Database initialisation method
	 */
	@PostConstruct
	public void init() {
		
		// Picture count
		int count = 100;
		
		// Categories array
		PictureCategory[] categories = PictureCategory.values();
		
		// Random generator
		Random random = new Random();
		
		// Intialize picture list
		pictures = IntStream.iterate(0, i -> i < count, i -> i+1)
				.mapToObj(i -> {
					
					// Return tranformed picture
					return Picture.builder()
							.id(i)
							.name(String.format("Picture %s", i))
							.description(String.format("This is the Picture %s", i))
							.url(String.format(PICTURE_URL_MODEL, i))
							.category(categories[random.ints(1, 0, categories.length).findFirst().getAsInt()])
							.build();
				
				}).collect(Collectors.toList());
	}
	
	/**
	 * Method used to get all database pictures 
	 * @return	Picture list
	 */
	public List<Picture> getAllPictures() {
		
		// Return the picture ist
		return pictures;
	}
	
	/**
	 * Method used to get the picture count 
	 * @return	Picture count
	 */
	public int getPictureCount() {
		
		// Return the picture count
		return pictures.size();
	}
	
	/**
	 * Method used to add a new picture 
	 * @param picture	Picture to add
	 * @return	Added picture
	 */
	public Picture addPicture(Picture picture) {
		
		// Set the ID
		picture.setId(pictures.size());
		
		// Set the URL
		picture.setUrl(String.format(PICTURE_URL_MODEL, picture.getId()));
		
		// Add the picture
		pictures.add(picture);
		
		// Return the added picture
		return picture;
	}
}
