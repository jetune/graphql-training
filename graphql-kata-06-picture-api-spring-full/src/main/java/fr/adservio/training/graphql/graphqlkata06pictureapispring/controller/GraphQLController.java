/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import fr.adservio.training.graphql.graphqlkata06pictureapispring.graphql.GraphQLQueryExecutor;
import fr.adservio.training.graphql.graphqlkata06pictureapispring.tools.JSonTools;
import lombok.extern.slf4j.Slf4j;

/**
 * GraphQL Controller 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 22 mai 2020 - 20:54:09
 */
@RequestMapping(value = "/api", headers = "!sec-websocket-version")
@RestController
@Slf4j
public class GraphQLController {
	
	/**
	 * GraphQL Executor
	 */
	@Autowired
	private GraphQLQueryExecutor executor;
	
	/**
	 * Query Parameter
	 */
	public static final String QUERY_PARAM = "query";
	
	/**
	 * Operation Parameter
	 */
	public static final String OPERATION_PARAM = "operationName";

	/**
	 * Variables Parameter
	 */
	public static final String VARIABLE_PARAM = "variables";
	
	/**
	 * GrapQL POST request endpoint  (compute graphql request from body or request parameters)
	 * @param contentType	Request content type
	 * @param query	GraphQL API Query
	 * @param operationName	GraphQL Operation name
	 * @param variablesJson	GraphQL Query variables
	 * @param body	Http request body
	 * @param webRequest	Http Request
	 * @return	GraphQL execution result data
	 * @throws IOException	Potential exception
	 */
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object computePost(
			@RequestBody(required = false) String body,
            WebRequest webRequest) throws IOException {
		
		// Log
		if(!body.contains("IntrospectionQuery")) {
			
			// Log
			log.info("====> GraphQL Body           : {}", body);
		}
		
		// If query is specified in the request body
		if(body != null && !body.isBlank()) {
			
			// Deserialize body
			GraphQLRequestBody requestBody = JSonTools.fromJson(body, GraphQLRequestBody.class);
			
			// Execute
			return executor.execute(requestBody);
		}
		
		// Return null
		return null;
	}

	/**
	 * GrapQL GET request endpoint 
	 * @param contentType	Request content type
	 * @param query	GraphQL API Query
	 * @param operationName	GraphQL Operation name
	 * @param variablesJson	GraphQL Query variables
	 * @param webRequest	Http Request
	 * @return	GraphQL execution result data
	 * @throws IOException	Potential exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object computeGet(@RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
            @RequestParam(value = QUERY_PARAM, required = true) String query,
            @RequestParam(value = OPERATION_PARAM, required = true) String operationName,
            @RequestParam(value = VARIABLE_PARAM, required = false) String variables,
            WebRequest webRequest) throws IOException {
		
		// Build Map variables
		Map<String, Object> mVariables = variables == null ? Collections.emptyMap() : JSonTools.fromJson(variables, Map.class);
		
		// Create Request Body
		GraphQLRequestBody requestBody = new GraphQLRequestBody(query, operationName, mVariables);
		
		// Execute
		return executor.execute(requestBody);
	}
}
