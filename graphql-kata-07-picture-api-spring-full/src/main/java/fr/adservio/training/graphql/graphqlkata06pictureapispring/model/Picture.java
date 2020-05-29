/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Picture class
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 20 mai 2020 - 13:57:30
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class Picture {
	
	/**
	 * Picture ID
	 */
	@EqualsAndHashCode.Include
	private Integer id;
	
	/**
	 * Picture name
	 */
    private String name;
    
    /**
     * Picture URL
     */
    private String url;
    
    /**
     * Picture description
     */
    private String description;
    
    /**
     * Picture category
     */
    @Builder.Default
    private PictureCategory category = PictureCategory.SELFIE;
}
