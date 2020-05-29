# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

Ce Kata est une petite parenthèse dont le but est de présenter aux inconditionnels de Java/Spring comment intégrer un serveur GraphQL à une application Spring Boot :

# Let's go

0.	Le projet actuel est un projet maven Spring Boot avec par défaut un starter web installé.

1.	Rajoutez la bibliothèque de l'API GraphQL pour Java
```

<!-- Project properties -->
<properties>
    ...

    <!-- GraphQL Java version property -->
    <graphql-java.version>14.0</graphql-java.version>
    
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

5.  Créez ensuite un composant dont le but sera d'implémenter les opérations de Mutation et Query, liées aux photos C'est un `DataFecther`. Pour cela créer la classe `PictureDataFetcher` dans le package `graphql` ave le contenu suivant:
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
			
			// Build Picture from MAP
			Picture inputPicture = new ObjectMapper().convertValue(environment.getArgument("picture"), Picture.class);
			
			// Add picture in Database
			Picture addedPicture = pictureDB.addPicture(inputPicture);
			
			// Add and return added picture
			return addedPicture;
		};
	}
}
```
### NB : La prise en charge des `Subscriptions` est une autre histoire que nous verrons par la suite

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

7.	À ce stade, nous avons un moteur GraphQL configuré et prêt à exécuter les opérations qui lui seront demandées. Par contre, nus avons besoin d'exposer notre API vis un Endpoint RESt, afin que des clients GraphQL puissent la consommer. Pour cela, crééz un composant Spring d'exécution de requêtes GraphQL qui exploitera notre moteur GraphQL fraichement créé.
```
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
```
```
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
```

8.	Créez ensuite un contrôleur RESt proposant un Endpoint `POST` permettant de prendre en charge les requêtes des clients de notre API.
```
@RequestMapping(value = "/api")
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
	 * Json Mapper
	 */
	private ObjectMapper mapper = new ObjectMapper();
	
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
```

9.  Démarrez votre application Spring et faites pointez votre client GraphQL sur l'URL de votre serveur (`http://localhost:9090/api`). Vous pourrez visualiser la documentation de l'API comme dans les cata JS et exécutez des requêtes et mutations disponibles.
	*	Requête de listage des photos
		```
		query listAllPictures {
			totalPictures,
			allPictures {
				id,
				name,
				url,
				description,
				category
			}
		}
		```
	*	Mutation paramétrée d'ajout d'une nouvelle photo

		Mutation
		```
		mutation addNewPicture($picture: PictureInput!) {
			postPicture(picture: $picture) {
				id,
				name,
				url,
				description,
				category
			}
		}
		```
		Variables :
		```
		{
			"picture": {
				"name": "NewPicture01",
				"description": "New picture 01",
				"category": "GRAPHIC"
			}
		}
		```

10.	Concernant la prise en charge des souscriptions, un certains nombre d'aspects doivent être pris en considération:
	*	Le mécanisme de transport de la demande d'abonnement et de réception des évènements de modification.

		La plupart des clients GraphQL (Playground, Altair, etc...) utilisera le protocole de transport bidirectionnel `Websocket` pour l'envoie des requêtes de souscription et la réception des évènements de modification. Nous sommes cependant libres d'utiliser ce qu'il nous plaira (Par exemple Websocket + STOMP, ou encore HTTP pour l'envoi de la requête et Server Sent Event [SSE] pour le réception des évènements, ou même encore un mode `PULL` pour la réception des évènements)

	*	Le traitement de la requête d'abonnement et de détection des changments

		L'API `graphql-java` supporte nativement les `Subscription` par le biais des streams réactifs `reactive-stream`, permettant de s'abonner à des flux d'évènements liées à un objet donné.
		L'idée de cette approche est d'exécuter la requête de souscription, après avoir associé au moteur GraphQL une stratégie d'exécution de souscription permettant de récupérer un `Observer` construit sur le résultat diu `DataFecther` associé à la souscription et d'y enregistrer un `Subscriber` qui se chargera d'acheminer les résultats Streamés vers les clients Websocket.

11.	Dans notre formation, nous allons partir sur `Websesocket` comme protocole de transport afin d'acheminer les requêtes de demande d'abonnements ainsi que les évènements de modification.

Pour la détection des changements, nous allons simplifier en utilisant le Bus de gestion des évènements natif de Spring, nous permettant ainsi de faire lever des évènements par le `Datafetcher` chargé de l'enregistrement d'une nouvelle photo et de Brancher un `Listener` sera déclenché par cet évènement et qui le propagera vers les lients Websocket.

12.	Créez la classe `PictureAddedEvent` dans le package `fr.adservio.training.graphql.graphqlkata06pictureapispring.model.event` représentant un évènement d'ajout d'une nouvelle photo
```
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
```
Vous remarquerez que cette classe étend `ApplicationEvent`, ce qui n'est pas obligatoire dans l'absolu.
En effet, Spring supporte des objets POJO classiques comme évènement, nous avons explicitement utilisé une classe fille de `ApplicationEvent` parce que nous souhaitons définir et enregistrer dynamiquement des Listeners de cet évènement en fonction des session websocket client. Ce qui nous obligera à étende l'interface `ApplicationListener` qui s'applique à un type dérivé de `ApplicationEvent`.

13.	Rajoutez la publication de cet évènement dans le `DataFetcher` en charge de l'enregistrement d'une ouvelle photo.
```
public DataFetcher<Picture> addPictureFetcher() {
	
	// Return the Data fetcher
	return environment -> {
		...

		// Add picture in Database
		Picture addedPicture = pictureDB.addPicture(inputPicture);

		// Publish event
		eventPublisher.publishEvent(new PictureAddedEvent(this, addedPicture));
		
		// Add and return added picture
		return addedPicture;
	};
}
```

14.	Créez un `Listener`, représentant un client websocket abonné à cet évènement et qui écoutera s'abonera à cet évènement et utilisera la session websocket pour pousser l'évènement vers le client.
```
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class PictureAddedEventListener implements ApplicationListener<PictureAddedEvent> {
	
	/**
	 * Websocket session
	 */
	private WebSocketSession session;
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(PictureAddedEvent event) {
		
		// Message to send
		WebsocketResponsePayload response = WebsocketResponsePayload.builder()
				.id("1")
				.type(GraphQLWSMessageType.DATA.getValue())
				.payload(event.getPayload())
				.build();
		
		// ext message to push
		TextMessage message = new TextMessage(JSonTools.toJsonString(response));
		
		// Log
		log.info("Message to Push : [{}]", JSonTools.toJsonString(response));
		
		try {
			
			// Push message to client
			session.sendMessage(message);
			
		} catch (IOException e) {
			
			// Print exception stack trace
			e.printStackTrace();
		}
	}
}
```

13.	Créez un composant Spring `WebSocketHandler` dans le package `websocket` et qui aura entre autre une propriété de type `ApplicationEventMulticaster`, permettant d'enregistrer dynamiquement des listeners.

```
@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
	
	/**
	 * Listener registry
	 */
	@Autowired
	private ApplicationEventMulticaster applicationListenerRegistry;
	
	/**
	 * Map of Picture listener by connected session ID
	 */
	private Map<String, PictureAddedEventListener> listeners = new HashMap<>();
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionEstablished(org.springframework.web.socket.WebSocketSession)
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		// Parent call
		super.afterConnectionEstablished(session);
		
		// Log
		log.info("GraphQL Client connected : [Session ID : {}]", session.getId());
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionClosed(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.CloseStatus)
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		// Parent call
		super.afterConnectionClosed(session, status);
		
		// Add session in the MAP
		PictureAddedEventListener listener = listeners.remove(session.getId());
		
		// Unregister the listener
		if(listener != null) applicationListenerRegistry.removeApplicationListener(listener);
		
		// Log
		log.info("GraphQL Client disconnected : [Session ID : {}]", session.getId());
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#handleTextMessage(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.TextMessage)
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		// Parent call
		super.handleTextMessage(session, message);
		
		// Print the text
		log.info("Text Message to compute : [{}]", message.getPayload());
		
		// Map to Object
		WebsocketRequestPayload graphQLClientMessagePayload = JSonTools.fromJson(message.getPayload(), WebsocketRequestPayload.class);
		
		// If message type is 'start'
		if(graphQLClientMessagePayload.getType() != null && 
				graphQLClientMessagePayload.getType().trim().equalsIgnoreCase(GraphQLWSMessageType.START.getValue())) {
			
			// Instantiate a listener
			PictureAddedEventListener listener = new PictureAddedEventListener(session);
			
			// Add the listener to the map
			listeners.put(session.getId(), listener);
			
			// Register Listener with Spring listener context
			applicationListenerRegistry.addApplicationListener(listener);
			
		} else if (graphQLClientMessagePayload.getType() != null && 
				graphQLClientMessagePayload.getType().trim().equalsIgnoreCase(GraphQLWSMessageType.CONNECTION_INIT.getValue())) {

			// Message to send
			WebsocketResponsePayload response = WebsocketResponsePayload.builder()
					.type(GraphQLWSMessageType.CONNECTION_ACK.getValue())
					.build();
			
			// Text message to push
			TextMessage wsMessage = new TextMessage(JSonTools.toJsonString(response));
			
			// Sed the message
			session.sendMessage(wsMessage);
		}
	}
}
```
Ce composant va permettre de gérer les requêtes venant des clients Websocket de notre API GraphQL

14.	Créez un intercepteur de `Handshake` dont le but sera d'accepter les sous protocoles éventuellements supporté par le client.
En effet, dans une communication `Websocket`, les participants peuvent s'accorder sur un sous-protocole de communication. Certains clients comme `GraphQL Playground` n'établssent pas correctement la communication si le serveur `Websocket` ne valide pas le protocole `graphql-ws` envoyé via le header `Sec-WebSocket-Protocol`.

Pour assurer le bon fonctionnement du client, créez un intercepteur `CustomHandshakeInterceptor` dans le package `websocket` avec le contenu suivant:
```
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

	/* (non-Javadoc)
	 * @see org.springframework.web.socket.server.HandshakeInterceptor#beforeHandshake(org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse, org.springframework.web.socket.WebSocketHandler, java.util.Map)
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		// Always return true
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.socket.server.HandshakeInterceptor#afterHandshake(org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse, org.springframework.web.socket.WebSocketHandler, java.lang.Exception)
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
		// Get the 'Sec-WebSocket-Protocol' header
		List<String> secHeaders = request.getHeaders().get("Sec-WebSocket-Protocol");
		
		// Add the header in the response
		secHeaders.forEach(h -> response.getHeaders().add("Sec-WebSocket-Protocol", h));
	}
}
```

15.	Créez ensuite la classe de configuration du Framework `Websocket`, permettant de customiser le fonctionnement de notre serveur Websocket
```
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	/**
	 * Handler
	 */
	@Autowired
	private WebSocketHandler handler;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.config.annotation.WebSocketConfigurer#registerWebSocketHandlers(org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry)
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		// Add the Handler
		registry.addHandler(handler, "/api").setAllowedOrigins("*").addInterceptors(new CustomHandshakeInterceptor());
		
		// Add the Handler SockJS
		registry.addHandler(handler, "/api").setAllowedOrigins("*").addInterceptors(new CustomHandshakeInterceptor()).withSockJS();
	}
}
```
Comme nous pouvons le constater, nous avons enregistré notre `Handler`précédent afin qu'il traite tous les message venant sur les abonnements à l'URI `/api`, ce qui permettra aux client de se connecter par exemple via l'URL `ws://localhost:9090/api`.

Nous avons aussi permit l'accès à cette websocket depuis n'importe quelle origine (CORS) et rajouté notre intercepteur afin qu'il traite les valeurs du header `Sec-WebSocket-Protocol`

De même nous avons activé `SockJS` pour la prise en charge des clients ne supportant pas `Websocket`

16.	Vous aurez remarqué que le `Handler` écoute sur le même `path` que le contrôleur Rest (`/api`): c'est un choix et non une obligation, mais certains client comme `GraphQL Playground` n'offre pas la possibilité de configurer l'URL de souscription et la déduise directement de l'URL de l'API graphQL. Dans ce cas, il serait bien que le gestionnaire `Websocket` et le contrôleur Rest écoutent sur le même `path`

Par contre, la précaution à prendre est d'daptez le controleur `GraphQLController` afin qu'il ne traite pas les requêtes présentant un header lié à `Websocket`.

Rajoutez donc dans l'annotation `@RequestMapping` le traitement de toute requête dont le header n'est pas `sec-websocket-version` ou `sec-websocket-protocol`

```
@RequestMapping(value = "/api", headers = "!sec-websocket-version")
```

17.	Démarrez et tester la souscription
