/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class used for JSon operations
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 23 mai 2020 - 19:17:37
 */
public class JSonTools {
	
	/**
	 * GSon Serializer instance (Important to serialize null for GraphQL copatibility)
	 */
	private static final Gson GSON = new GsonBuilder().serializeNulls().create();
	
	/**
	 * Method used to serialize object to String 
	 * @param object	Object to serialize
	 * @return	Serializes object
	 */
	public static String toJsonString(Object object) {
		
		// Return serialized value
		return GSON.toJson(object);
	}
	
	/**
	 * Method used to unmarshall JSon String to Object 
	 * @param <T>	Target type parameter
	 * @param jsonString	Json String
	 * @param targetClass	Target Class
	 * @return	Unmarshalled object
	 */
	public static <T> T fromJson(String jsonString, Class<T> targetClass) {
		
		// Unmarshall and return object
		return GSON.fromJson(jsonString, targetClass);
	}
}
