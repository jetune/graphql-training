# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

Ce Kata est une petite parenthèse dont le but est de présenter aux inconditionnels de Java/Spring comment intégrer un serveur GraphQL à une application Spring Boot :

# Let's go

0.	Le projet actuel est un projet maven Spring Boot avec par défaut un starter web installé.

1.	Rajoutez la bibliothèque de l'API GraphQL pour Java ainsi que le starter Spring Boot WebMVC qui permettra de configurer la servlet GraphQL
```

<!-- Project properties -->
<properties>
    ...

    <!-- GraphQL Java version property -->
    <graphql-java.version>14.0</graphql-java.version>
    
    <!-- GraphQL Spring Boot MVC Starter (Expose GraphQL Servlet) -->
    <graphql-java-spring-boot-starter-webmvc.version>2019-06-24T11-47-27-31ab4f9</graphql-java-spring-boot-starter-webmvc.version>
    
</properties>

<!-- Project dependencies -->
<dependencies>
    ...

    <!-- GraphQL Java dependency -->
    <dependency>
        <groupId>com.graphql-java</groupId>
        <artifactId>graphql-java</artifactId>
        <version>${graphql-java.version}</version>
    </dependency>
    
    <!-- GraphQL Spring Boot MVC Starter dependency -->
    <dependency>
        <groupId>com.graphql-java</groupId>
        <artifactId>graphql-java-spring-boot-starter-webmvc</artifactId>
        <version>${graphql-java-spring-boot-starter-webmvc.version}</version>
    </dependency>
    
    ...

</dependencies>

```
2.  Créer le fichier contenant le schéma de votre API GraphQL dans les resources `schema.grapql` et rajoutez lui le contenu suivant
```
enum PictureCategory {
    SELFIE,
    PORTRAIT,
    LANDSCAPE,
    GRAPHIC,
    ACTION
}

type Picture {
    id: ID!,
    name: String!,
    url: String!,
    description: String,
    category: PictureCategory!
}

input PictureInput {
    name: String!,
    description: String,
    category: PictureCategory=SELFIE
}

type Query {
    totalPictures: Int!,
    allPictures: [Picture!]!
}

type Mutation {
    postPicture(picture: PictureInput!): Picture!
}

type Subscription {
    pictureAdded: Picture
}
```

3.  Crééz ensuite, dans le package des modèles `fr.adservio.training.graphql.graphqlkata06pictureapispring.model` les énumération et classes nécessaires
    *   PictureCategory.java
    ```
    public enum PictureCategory {
	
        /**
        * Selfie picture
        */
        SELFIE,
        
        /**
        * Portrait picture
        */
        PORTRAIT,
        
        /**
        * Landscape picture
        */
        LANDSCAPE,
        
        /**
        * Graphic picture
        */
        GRAPHIC,
        
        /**
        * Action picture
        */
        ACTION
    }

    ```
    *   Picture.java
    ```
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Getter
    @Setter
    @ToString
    @SuperBuilder
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
    ```
4.  Nous allons ensuite créer une base de donnée `Mock` contenant la liste des `Picture` dans le package `repository`. Pour cela crééz la classe `InMemoryPictureDB` avec le contenu suivant
```
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
```

5.  Créez ensuite un composant dont le but sera d'implémenter les opérations de Mutation, Query, Souscriptions, etc... liées aux photos C'est un `DataFecther`. Pour cela créer la classe `PictureDataFetcher` dans le package `graphql` ave le contenu suivant:
```
@Component
public class PictureDataFetcher {
	
	/**
	 * Picture database
	 */
	@Autowired
	private InMemoryPictureDB pictureDB;
	
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
			
			// Get input from environment
			Map<String, Object> input = environment.getArgument("picture");
			
			// Build input picture
			Picture inputPicture = Picture.builder()
					.name(input.getOrDefault("name", "").toString())
					.description(input.getOrDefault("description", "").toString())
					.category(PictureCategory.valueOf(input.getOrDefault("category", PictureCategory.SELFIE.name()).toString()))
					.build();
			
			// Add and return added picture
			return pictureDB.addPicture(inputPicture);
		};
	}
}
```

6.  Une fois les `DaFetcher` en pltace, nous allons maintenant implémenter le fournisseur GraphQL

    *   Crééz une classe de configuration Spring `GraphQLProvider` dans le package `graphql` et injectez-y notre `DataFetcher`
    ```
    @Configuration
    public class GraphQLProvider {
        /**
         * Picture Data fetcher
         */
        @Autowired
        private PictureDataFetcher pictureDataFetcher;
    }
    ```
    *   Créez ensuite une méthode privée `queryBuilder` permettant de d'associer chacune des opérations de requêtage [`Query`] déclarées dans le schéma à un `DataFetcher`
    ```
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
    ```
    * De la même manière, créez une méthode privée `mutationBuilder` permettant de d'associer chacune des opérations de ficationm [`Mutation`] déclarées dans le schéma à un `DataFetcher`
    ```
	/**
	 * Method used to map Mutation operation to concrete Data Fetcher 
	 * @return	Mutation builder
	 */
	private TypeRuntimeWiring.Builder mutationBuilder() {
		
		// Construct and return the builder
		return TypeRuntimeWiring.newTypeWiring("Mutation")
				.dataFetcher("postPicture", pictureDataFetcher.addPictureFetcher());
	}
    ```
    *   Crééz une méthode permettant de construire, sur la base des deux précédentes, le plan d'exécution qui sera associé plus tard au schéma afin de créer un schéma exécutable (Déclaration + Implémentation)
    ```
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
    ```
    *   Créez maintenant une méthode privée permettant de construire un schéma exécutable en associant le contenu du fichier contenant le schéma GraphQL parsé en `TypeDefinitionRegistry` au plan d'exécution précédemment crée.
    ```
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
    ```
    *   Créez une méthode d'initialisation Spring `init`, dont le but sera de charger le contenu du fichier de définition du schéma GraphQL et d'initialiser une instance du moteur GraphQL qui servira de Bean
    ```
	/**
	 * Graph QL Engine
	 */
	private GraphQL graphQL;
	
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
		this.graphQL = GraphQL.newGraphQL(schema).build();
	}
    ```
    *   Enfin déclarez une méthode `graphQL` permettant de construre le bean Spring `graphQL`
    ```
	/**
	 * Method used to build GraphQL Engine bean 
	 * @return	GraphQL Engine bean
	 */
	@Bean
	public GraphQL graphQL() {
		
		// return the graphQL bean
		return graphQL;
	}
    ```

7.  Démarrez votre application Spring et faites pointez votre client GraphQL sur l'URL de votre serveur. Vous pourrez visualiser la documentation de l'API comme dans les cata JS et exécutez des requêtes et mutations disponibles
