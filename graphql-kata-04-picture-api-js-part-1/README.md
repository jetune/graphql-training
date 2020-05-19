# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

Le but de ce Kata est de présenter la définition et l'implémentation d'opérations de base (selection, mutation, souscription) proposées par GraphQL

# Un peu de dynamisme dans nos photos

0.	Rendez-vous dans le répertoire du Kata

1.	Installez les dépendances du projet
	
	*	`npm install`

2.	Ajoutez dans la définition du schéma, la racine de définition des mutations
```
const typeDefs = `
    type Query {
        totalPictures: Int!
    }
	type Mutation {
        postPicture(name: String!, description: String!): Boolean!
    }
`
```

8.	Déclarer dans le fichier `index.js`, un tableau qui contiendra les photos postées depuis le client de l'API
```	
// Photos Array
const pictures = [];
```

9.	Adaptez le resolver de l'opération `totalPictures` afin de renvoyer le nombre exact de photos et implémentez un resolver pour l'opération de mutation rajoutée dans le schéma
```	

// define a resolver that fetch data on the preceding schema
const resolvers = {
    Query: {
        totalPictures: () => pictures.length
    },
    Mutation: {
        postPicture(_parent, args) {

            // Add the new photo in the tab
            pictures.push({
                name: args.name,
                description: args.description
            });

            // Return the result status
            return true;
        }
    }
}

```
NB : Le paramètre `_parent` de l'implémentation de la mutation représente en réalité l'obje mutation parent de la mutation en cours d'exécution.

11. Démarrez votre application

    *   `npm start`

12.	Ouvrez un nouvel onglet de votre client GraphQL (GraphQL Playgroud) et faites-le pointer sur l'URL de votre serveur GraphQL

	*	`http://localhost:5001`

13.	Dans la documentation sur cet onglet, vous verrez que l'API contient maintenant un nouveau type `Mutation` présentant l'opération que nous avons définit dans le schéma plus haut.

	*	Dans un onglet à part, exécutons tout d'abors la requête de décompte des photos: le résultat est 0

		```
		query countPictures {
			totalPictures
		}
		```
	*	Exécutons ensuite la mutation suivante, permettant de rajouter une nouvelle photo

		```
		mutation addPicture {
			postPicture(name: "photo-01", description: "Photo 01")
		}
		```
	
	* Exécutons de nouveau la requête de décompte, elle renvoie maintenant 1

14.	Il est aussi possible de créer des requêtes et mutations paramétrées, par exemple, on peut re-écrire la mutation précédente comme ceci:
```
mutation addPicture($name: String!, $description: String) {
	postPicture(name: $name, description: $description)
}
```
Pour l'exécuter, il suffira juste de luis définir des paramètres lors de l'exécution. Pour le faire avec GrapQL Playground, il suffit d'aller sur l'onglet `Query Variables` au bas de sa fenêtre et y rajouter des variables et re-éxécuter la requête.
```
{
  "name": "NewPicture",
  "description": "This is My new Picture"
}
```

15.	Type de données `Picture` : Données de sortie d'opération

	Nous allons refactorer notre service afin de consolider les données des photos dans un objet de type `Picture` et rajouter une requête permettant de lister toutes les photos.

	*	Rajout du type `Picture`
		```
		type Picture {
			id: ID!,
			name: String!,
			url: String!,
			description: String
		}
		```
	*	Rajout de la requête `allPictures` permettant d'accéder à toutes les photos
		```
		type Query {
			totalPictures: Int!,
			allPictures: [Picture!]!
		}
		```
	*	Refactoring de la mutation `postPicture` afin qu'elle retourne la photo nouvellement enregistrée avec son ID
		```
		type Mutation {
			postPicture(name: String!, description: String): Picture!
		}
		```
	*	Rajout du resolver permettant de prendre en charge les requêtes `allPictures`
		```
		Query: {
			totalPictures: () => pictures.length,
			allPictures: () => pictures
		}
		```
	*	Refactoring du resolver permettant de rajouter une photo dans la liste.
		```
		 Mutation: {
			postPicture(_parent, args) {

				// Instantiate the new picture
				const newPicture = {
					id: pictures.length + 1,
					name: args.name,
					description: args.description
				};

				// Add the new picture in the tab
				pictures.push(newPicture);

				// Return the registered picture
				return newPicture;
			}
		}
		```

16.	Enregistrez et rendez-vous sur le client GraphQL Playground

	*	Exécutez tout d'abord la requête suivante, permettant de lister les photos, ainsi que leur nombre
	```
	query listAllPictures {
		totalPictures,
		allPictures {
			id,
			name,
			description
		}
	}
	```
	*	Ensuite exécutez (3 fois) la mutation paramétrée d'enregistrement d'une nouvelle photo
	```
	mutation addPhoto($name: String!, $description: String) {
		postPicture(name: $name, description: $description) {
			id,
			name,
			description
		}
	}
	```
	*	Re-exécutez la requête de listage des photos, On verra toutes les photos enregistrées ainsi que leur décompte.
	```
	query listAllPictures {
		totalPictures,
		allPictures {
			id,
			name,
			description
		}
	}
	```

17.	Maintenant tentons d'exécuter la requete de listage des photos en demandant le champ `url`
	```
	query listAllPictures {
		totalPictures,
		allPictures {
			id,
			name,
			description,
			url
		}
	}
	```
	Nous avons une erreur qui apparait. En effet, le champ URL n'as pas de valeur lors de la requête, alors qu'il est marqué comme non null. Ce contrôle se fait lors de la restitution de l'objet et non lors de son enregistrement.

	Pour que es choses se passent correctement, nous avons plusieurs possibilités parmis lesquelles :
	*	Postez la valeur du champ `url` depuis le client pour qu'il soit stocké avec les autres informations de la photo
	*	Calculer et stocker le champ `url` lors de l'enregistrement
	*	Calculer le champ lorsque le client en a besoin et uniquement à ce moment là: c'est la solution que nous allons mettre en place afin d'illustrer le concept de resolver de champ à la demande.

18.	Définir un `Resolver` permettant de s'occuper de la résolution de la propriété `url` et qui ne s'exécutera donc que lorsque le client en aura besoin
```
Picture: {
	url: (_parent) => `http://lab.adservio.fr/media/${_parent.id}.jpg`
}
```
Une fois définit et enregistré, on pourra désormais requêter les photos en demandant le champ `url`

*	Tout comme l'implémentation des fonctions de résolution de mutations, les fonctions de résolution des types accepte un paramètre représentant l'instance courante de l'objet de ce type. Dans notre cas, la définition de la fonction de résolution attendra un paramètre que nous avons appelé `_parent` et qui représentera l'instance de la photo en cours de résolution.

19.	Dans la continuité des types de données personalisés, GraphQL fournit aussi la possibilité de définir des `Énumérations` et des `Inputs`.
	*	Les `énumérations`, comme dans d'autres langages vont permettre de définir et contrôler le contenus de propriétés dont l'ensemble des valeurs possibles est fixe et connu à l'avance.
	*	Les `inputs`:
		Dans la spécification GraphQL, les types de données sont organisés en 2 groupes, les `Output Types (types de retour de requêtes ou de mutations)` et les `Input Types (type d'entré de requêtes ou de mutation)`.
		
		En effet, cette différence est dûe au fait que les Output Types peuvent contenir des propriétés qui induisent des références circulaires, ou encore peuvent référencer des interfaces ou des unions, ce qui serait inaproprié dans le traimenent d'un Input.
		
		GraphQL a pensé les Inputs avec des contraintes permettant de simplifier leur traitement.

		Un Input doit avoir des propriétés de type de base (Int, String, etc..), de type énumération, ou de type InputType uniquement.

		```
		NB : TOUTE TENTATIVE D'UTILISATION D'UN TYPE DE DONNÉES DIFFÉRENT D'UN INPUT EN ARGUMENT D'UNE MUTATION OU D'UNE QUERY SERA SANCTIONNÉE PAR UNE ERREUR LORS DU DÉMARRAGE DU SERVEUR GraphQL
		```
		
		Afin d'adapter notre petite API aux `InputType` et aux `Enumeration` nous allons :

	*	Créer une énumération des catégories de photos
	```
	enum PictureCategory {
        SELFIE,
        PORTRAIT,
        LANDSCAPE,
        GRAPHIC,
        ACTION
    }
	```
	*	Créer un `input type` permettant de regrouper les paramètres d'une photos comme argument d'emtré
	```
	input PictureInput {
        name: String!,
        description: String,
        category: PictureCategory=SELFIE
    }

	NB : À noter la définition d'une valeur par défaut pour le champ [category]
	```
	*	Refactorer le type Picture afin d'y rajouter une category non nulle (évaluation de la contrainte faite lors de la résolution de la propriété)
	```
	type Picture {
        id: ID!,
        name: String!,
        url: String!,
        description: String,
        category: PictureCategory!
    }
	```
	*	Refactorer la mutation d'enregistrement d'une nouvelle photo afin de prendre en entrée un argument de type `PictureInput`
	```
	type Mutation {
        postPicture(picture: PictureInput!): Picture!
    }
	```
	*	Adapter le resolver de la mutation d'ajout de photo pour la prise en compte du nouveau paramètre d'entrée
	```
	const resolvers = {
		Query: {...},
		Mutation: {
			postPicture(_parent, args) {

				// Instantiate the new picture
				const newPicture = {
					id: pictures.length + 1,
					name: args.picture.name,
					description: args.picture.description,
					category: args.picture.category
				};
				...
			}
		},
		Picture: {...}
	}
	```
	*	Une fois enregistré, nous pouvons désormais exploiter cette nouvelle version de la mutation via le client. Un exemple avec cette invocation de mutation paramétrée (en oubliant pas de passer les paramètre lors de l'exécution).
		```
		mutation addPhoto($name: String!, $description: String) {
			postPicture(picture: {name: $name, description: $description}) {
				id,
				name,
				description,
				category
			}
		}
		```
		correspondant à la définition de variables
		```
		{
			"name": "picture1",
			"description": "Picture of index 1"
		}
		```
		Une autre invocation en spécifiant une valeur de categorie
		```
		mutation addPhoto($name: String!, $description: String, $category: PictureCategory!) {
			postPicture(picture: {name: $name, description: $description, category: $category}) {
				id,
				name,
				description,
				category
			}
		}
		```
		correspondant à la définition de variables
		```
		{
			"name": "picture1",
			"description": "Picture of index 1",
			"category": "LANDSCAPE"
		}
		```
		Nous pouvons aussi utiliser cette définition d'invocation de mutation paramétrée:
		```
		mutation addPhoto($picture: PictureInput!) {
			postPicture(picture: $picture) {
				id,
				name,
				description,
				category
			}
		}
		```
		correspondant à la définition de variables
		```
		{
			"picture": {
				"name": "picture1",
				"description": "Picture of index 1",
				"category": "LANDSCAPE"
			}
		}
		```

20.	Nous allons pour terminer ce Kata, nous interesser aux souscriptions. En effet, GraphQL permet de définir des types d'objets permettant d'écouter en mode `PubSub` les évènements de mutation (Création, Modification, Suppression) concernant des types de données de l'API.

	*	Créons dans le schéma une `souscription` afin de suivre les évènements liés au cycle de vie des objets de type `picture`
	```
	type Subscription {
        pictureAdded: Picture
    }
	```
	* Rajoutons et exportons une constante qui contiendra le nom de l'évènement d'ajout de photo
	```
	// Event type name
	const PICTURE_ADDED_EVENT_TYPE = "PictureAddedEvent";

	// Export Add evet type name
	module.exports.PICTURE_ADDED_EVENT_TYPE = PICTURE_ADDED_EVENT_TYPE;
	```
	* Importons la classe `PubSub` depuis le module `apollo-server`: cette classe nous permettra de publier des évènements.
	```
	const { ApolloServer, PubSub } = require('apollo-server');
	```
	*	En effet, la classe `PubSub` est une implémentation `in-memory` et `non-production-ready` de l'interface `PubSubEngine` permettant de publier des évènements. Cette implémentation n'est pas recommendée pour les besoins de production, du fait qu'elle ne supporte pas la distribution d'évènements distribués (implémentation `in-memory`). Pour la production, il est conseillé d'utiliser des implémentations telles que: 
		-	[graphql-redis-subscriptions](https://github.com/davidyaha/graphql-redis-subscriptions)
		-	[graphql-kafka-subscriptions](https://github.com/ancashoria/graphql-kafka-subscriptions)
		-	[graphql-rabbitmq-subscriptions](https://github.com/cdmbase/graphql-rabbitmq-subscriptions)
		-	[graphql-postgres-subscriptions](https://github.com/GraphQLCollege/graphql-postgres-subscriptions)
		-	[graphql-google-pubsub](https://github.com/axelspringer/graphql-google-pubsub)
		-	etc...
	*	Récupérons le nom de l'évènement précédemment exporté et instantions un objet `PubSub`
	```	
	// Get the event type for added pcture
	const PICTURE_ADDED_EVENT_TYPE = gqlSchema.PICTURE_ADDED_EVENT_TYPE;

	// Instantiate a publisher/subscriber
	const pubsub = new PubSub();
	```
	* Rajoutons, dans le resolver d'implémentation de la mutation d'ajout de photo, la publication d'un évènement, juste après avoir effectué l'ajout de la novelle photo
	```
	Mutation: {
        postPicture(_parent, args) {
			...
            // Add the new picture in the tab
            pictures.push(newPicture);

            // Publish event
            pubsub.publish(PICTURE_ADDED_EVENT_TYPE, { pictureAdded: newPicture });
            ...
        }
    }
	```

	*	Rajoutons un `resolver` permettant de prendre en charge les requêtes de soucription venant des clients
	```
	Subscription: {
        pictureAdded: {
            subscribe: () => pubsub.asyncIterator([PICTURE_ADDED_EVENT_TYPE])
        }
    }
	```
	Comme vous le constaterez, l'implémentation d'une requête de souscription pour un évènement donné sera pris en charge par une fonction qui retournera un objet `AsyncIterator` dont le but est d'écouter les évènements de manière qsynchrone, via un protocole de transport `websocket`.
	La configuration du serveur `Websocket` est déjà intégrée au serveur `ApolloServer` et est confiuragle via la propriété `subscriptions` qui est un objet permettant de préciser entre autre le `path` sur lequel ApolloServer écoutera les requêtes de souscription.
	```
	// Define a graphql server to expose typeDefs and resolvers
	const server = new ApolloServer({
		typeDefs,
		resolvers,
		subscriptions: {
			path: "/",
			onConnect: () => console.log("=======> Connection to subscription")
		}
	});
	```
	*	Assurons nous que le serveur GraphQL est démarré
	```
	npm start
	```
	*	Exécutons une requête de souscription
	```
	subscription pictureEventListener {
		pictureAdded {
			id,
			name,
			category
		}
	}
	```
	Vous noterez que l'exécution de cette requête va mettre le client GraphQL en attente d'évènements `Listening`.
	*	Dans un autre onglet, executons une mutation (paramétrée) d'enregistrement d'une photo
	```
	  mutation addPicture($picture: PictureInput!) {
		postPicture(picture: $picture) {
			id,
			name,
			description,
			category
		}
	}
	```
	Variables de la mutation
	```
	{
		"picture": {
			"name": "picture1",
			"description": "Picture of index 1",
			"category": "PORTRAIT"
		}
	}
	```
	*	Si nous rentrons dans l'onglet de la souscription, nous verrons apparaitre l'évènement de creation de la photo précédente.