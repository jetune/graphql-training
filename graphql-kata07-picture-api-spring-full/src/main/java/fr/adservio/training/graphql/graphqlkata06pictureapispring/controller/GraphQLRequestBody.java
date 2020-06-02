/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.controller;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class used to store GraphQL HTTP Request body send by client 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 23 mai 2020 - 07:59:01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphQLRequestBody {
	
	/**
	 * GrapQL Query
	 */
	private String query;
	
	/**
	 * GrapQL Operation name
	 */
	private String operationName;
	
	/**
	 * GrapQL Operation variables
	 */
	@JsonProperty(required = false)
	private Map<String, Object> variables;
}
