# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

# Exposition d'opérations de mutation

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

8.	Déclarer un tableau qui contiendra les photos postées depuis le client de l'API
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

15.	Resolvers et types de données utilisateur.
En plus des types de données de base fournit de base, GraphQL offre aussi la possibilité de créer des types de composite et de construire des requêtes et mutations sur cette base.
Nous allons donc refactorer notre service afin de consolider les données des photos dans un objet de type `Picture` et rajouter une requête permettant de lister toutes les photos.

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

	Pour que es choses se passent correctement, nous avons 2 possibilités :
	*	Calculer et stocker le cham lors de l'enregistrement (ce qui ne nous arrange pas toujours vu que dans notre cas c'est un champ calculé à partir des autres champs et donc on a pas besoin de le stocker)
	*	Calculer le champ lorsque le client en a besoin et uniquement à ce moment là: c'est la solution que nous allons mettre en place.

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


