/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import fr.adservio.training.graphql.graphqlkata06pictureapispring.controller.GraphQLRequestBody;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;

/**
 * Class used to execute GraphQL query
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 23 mai 2020 - 08:19:09
 */
@Component
public class GraphQLQueryExecutor {
	
	/**
	 * GraphQL Engine
	 */
	@Autowired
	private GraphQL graphQL;
	
	/**
	 * Executor method 
	 * @param body	GrqphQL Query body
	 * @return	Execution result
	 */
	public Object execute(GraphQLRequestBody body) {
		
		// Build the execution input
		ExecutionInput input = ExecutionInput.newExecutionInput()
				.query(body.getQuery())
				.operationName(body.getOperationName())
				.variables(body.getVariables())
				.build();
		
		// Execute the query
		ExecutionResult executionResult = graphQL.execute(input);
		
		// Si la liste d'erreurs est non vide
		if(!CollectionUtils.isEmpty(executionResult.getErrors())) {
			
			// Throw exception
			throw new RuntimeException("Problems occurs when execute query");
		}
		
		// Get the result Data
		return executionResult.toSpecification();
	}
}
