/**
 * KUBECLOUD :: DT :: CLOUD :: KIS
 */
package fr.adservio.training.graphql.graphqlkata06pictureapispring.graphql;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import graphql.GraphQL;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

/**
 * GraphQL provider class used to consolidate all GraphQL fragments 
 * @author <a href="mailto:jeanjacques.etunengi@adservio.fr">Jean-Jacques ETUNE NGI (Java EE Technical Lead / Enterprise Architect)</a>
 * @since 20 mai 2020 - 15:44:32
 */
@Configuration
public class GraphQLProvider {
	
	/**
	 * Picture Data fetcher
	 */
	@Autowired
	private PictureDataFetcher pictureDataFetcher;
	
	/**
	 * Graph QL Engine
	 */
	private GraphQL graphQL;
	
	/**
	 * Method used to build GraphQL Engine bean 
	 * @return	GraphQL Engine bean
	 */
	@Bean
	public GraphQL graphQL() {
		
		// return the graphQL bean
		return graphQL;
	}
	
	/**
	 * Method used to initialize GraphQL Engine bean
	 * @throws IOException Potential exception
	 */
	@PostConstruct
	public void init() throws IOException {
		
		// Get the schema as resource from classpath
		URL schemaUrl = Resources.getResource("schema.graphql");
		
		// Load the text file content
		String schemaAsString = Resources.toString(schemaUrl, Charsets.UTF_8);
		
		// Build the GraphQL Schema
		GraphQLSchema schema = buildSchema(schemaAsString);
		
		// Build the GraphQL Engine instance
		this.graphQL = GraphQL.newGraphQL(schema)
				.subscriptionExecutionStrategy(new SubscriptionExecutionStrategy())
				.build();
	}
	
	/**
	 * Method used to build a GraphQL Executable Schema Object from text-based schema 
	 * @param schemaString	Text-based GraphQL Schema
	 * @return	GraphQL executable Schema Object
	 */
	private GraphQLSchema buildSchema(String schemaString) {
		
		// Instantiate a Type definition Registry from Text parsed
		TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaString);
		
		// get the Runtime Wiring (Implementations)
		RuntimeWiring runtimeWiring = buildWiring();
		
		// Instantiate a schema generator
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		
		// Build the executable Schema
		return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
	}
	
	/**
	 * Method used to build all Together in a GrapQL Runtime Wiring 
	 * @return	GraphQL Runtime Wiring
	 */
	private RuntimeWiring buildWiring() {
		
		// Build and return the Runtime
		return RuntimeWiring.newRuntimeWiring()
				.type(mutationBuilder())
				.type(queryBuilder())
				.build();
	}
	
	/**
	 * Method used to map Mutation operation to concrete Data Fetcher 
	 * @return	Mutation builder
	 */
	private TypeRuntimeWiring.Builder mutationBuilder() {
		
		// Construct and return the builder
		return TypeRuntimeWiring.newTypeWiring("Mutation")
				.dataFetcher("postPicture", pictureDataFetcher.addPictureFetcher());
	}
	
	/**
	 * Method used to map Query operation to concrete Data Fetcher 
	 * @return	Query builder
	 */
	private TypeRuntimeWiring.Builder queryBuilder() {
		
		// Construct and return the builder
		return TypeRuntimeWiring.newTypeWiring("Query")
				.dataFetcher("totalPictures", pictureDataFetcher.getPictureCountFetcher())
				.dataFetcher("allPictures", pictureDataFetcher.getAllPictureFetcher());
	}
}
