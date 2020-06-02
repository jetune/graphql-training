# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

Le but de ce Kata est de présenter certaines des constructions fournies par GraphQL pour nous permettre de mettre en place une API comme cette que nous avons découverte dans le Kata précédent.

# Quelques rappels importants

1. Un schéma d'API GraphQL représente l'ensemble des types de données (entrées/sortie) et des opérations exposés par l'API.

2. GraphQL fournit un langage SDL (Schema Definition Language) qui est une DSL (Domain Specific Language) permettant de décrire un schéma d'API (Type de données et Opérations).

3. Un `Type` de données, comme dans la plupart des langages de programmation, permet de représenter et caractériser un concept lié à notre contexte métier ou technique

4.	Un schéma GraphQL peut être définit de plusieurs manière (chaine de caractère, fichier texte souvent d'extension .graphql)

5.	Pour chacun des champs déclarés dans un schéma nous devront mettre en place un `Resolver` qui sera en quelque sorte son imlémentation.

# Définition de schémas d'API GraphQL

##	Types de données

1.	GraphQL fournit de base un ensemble de types de données

	###	Types Simples (Type de données n'ayant pas de champs)

	*	String		:	Chaines de caractère
	*	Int			:	Nombre entier
	*	Float		:	Nombre flottant
	*	Boolean		:	Booléen
	*	ID			:	Chaine de caractère unique pour un type de données
	```
	GraphQL permet la création de nouveua type de données simples.
	```

	### Autres Types
	*	Query		:	Représente un ensemble d'actions de recherche de données
	*	Mutation	:	Représente un ensemble d'actions de modification de données
	*	Subscription:	Représente un ensemble d'abonnements aux évènements de modification concernant un type de données
	*	Enumération	:	Représente une énumération de valeurs
	*	Liste		:	Représente une liste de données

| Déclaration de liste | Description |
| ------ | ------ |
| [ Int ] | Liste potentiellement nulle d'entiers potentiellement nuls |
| [ Int! ] | Liste potentiellement nulle d'entiers non null |
| [ Int ]! | Liste non nulle d'entier spotentiellement nuls |
| [ Int! ]! | Liste non nulle d'entiers non nul |

	*	Interface	:	Représente la définition d'un contrat de champs que devront avoir toutes les implémentations
	*	Union		:	Représente la définition d'un type dont les données peuvent être venir de plusieurs types différents
	*	InputType	:	Représente un type de données dédiés au passage d'argument de requêtes ou mutations

	```
	Tous ces concepts de base permettent au développeur de construire ses propres types de données, conformément aux besoins de son API.
	```
2.	Exemple de type de données du schema d'API d'une application factis de gestion des photos

	*	Création d'un type simple stockant l'horodatage de la photo
	```
	scalar DateTime;
	```
	*	Création d'une énumération des catégories de photos
	```
	enum PictureCategory {
        SELFIE,
        PORTRAIT,
        LANDSCAPE,
        GRAPHIC,
        ACTION
    }
	```
	*	Création d'une énumération des mode de tri de photos
	```
	enum PictureSortDirection {
        ASCENDING,
        DESCENDING
    }
	```
	*	Création d'une `interface` représentant une source de photographie, ontenant entre autres la liste des categories de photos prise en charge
	```
	interface PictureSource {
		id: ID!
		name: String!
		categories: [PictureCategory!]!
	}
	le symbole '!' permettra de définir une contrainte de non nullité. Dans notre cas nous en avons deux qui stipulent que le champ 'categories' devra être une liste non nulle dont chacune des entrée sera une énumérationnon nulle.
	```

	*	Création de deux implémentations de la source de photographie (humain et androide). À noter que chaque implémentation respectera au minimum les champs de linterface, mais pourra rajouter des champs spécifiques
	```
	type Human implements PictureSource {
		id: ID!
		name: String!
		categories: [PictureCategory!]!
		totalCredits: Int
	}

	type Droid implements PictureSource {
		id: ID!
		name: String!
		categories: [PictureCategory!]!
		primaryFunction: String
	}
	```
	*	Création d'une union permettant de stocker le résultat de la recherche des source photographiques de notre système.
	```
	union PictureSourceSearchResult = Human | Droid
	```
	À noter que lors dans la requete client de recherche, on devra spécifier les champs à récupérer en fonction du type spécifique.
	```
	search(text: "an") {
		__typename
			... on Human {
				name,
				totalCredits
			},
			... on Droid {
				name,
				primaryFunction
			}
	}
	```
	*	Création d'un type de donnée re présentant une photo
	```
	type Picture {
        id: ID!,
        name: String!,
        url: String!,
        description: String,
        category: PictureCategory!,
		source: PictureSource!
    }
	```
	Définit ainsi, ce type de données sera utilisé comme valeur de retour et jamais comme argument de requête ou mutation
	*	Définition d'un type d'entrée permettant d'enregistrer une nouvelle photo
	```
	enum PictureSourceType {
		HUMAN,
		DROID
	}
	type PictureSourceInput {
		id: ID!
		name: String!,
		type: PictureSourceType!,
		categories: [PictureCategory!]!,
		primaryFunction: String,
		totalCredits: Int
	}
	input PictureInput {
        name: String!,
        description: String,
        category: PictureCategory=SELFIE,
		source: PictureSourceInput!
    }
	```
	```
	Nous pouvons constater ici l'approche (très discutable) permettant de gérer le polymorphisme lié à la source de la photo.
	En effet, aucun paramètre d'entrée ne pouvant référencer une interface (impossible qu'ils aient des champs d'un type interface ou union), il nous a fallu créer une classe spéciale 'PictureSourceInput' contenant l'ensemble des champs communs et distinct aux deux implémentations de la source de photographie et ensuite rajouter une énumération oblogatoire qui permettra de les distinguer dans l'mplémentation afin de creer le type adéquat.
	```
	* Création d'un type `Query` permettant de lister quelques Opérations de requetage de photos
	```
	type Query {

		// Listage de toutes les photos
		allPictures: () [Picture!],

		// Requête de listage de photos, filtrés par categorie et paginé (les paramètres de pagination ayant des valeurs par defaut)
		allPicturesPaginated: (category: PictureCategory, start: Int=0, count: Int=50) [Picture!]

		// Requête de listage de photos, filtrés par categorie et paginé et trié par direction
		allPicturesPaginated: (category: PictureCategory, start: Int=0, count: Int=50, sort: PictureSortDirection=ASCENDING) [Picture!]
	}
	```
	* Création d'un type `Mutation` permettant de lister quelques opérations d'écriture sur les photos
	```
	type Mutation {

		// Enregistrement d'une photo (avec paramètres simples et valeur par défaut)
		postPicture(name: String!, description: String, category: PictureCategory=PORTRAIT): Picture!

		// Enregistrement d'une photo (avec paramètres input)
		postPicture(picture: PictureInput!): Picture!
	}
	```
	* Création d'un type `Subscription` exposant des évènements liés aux photos (Enregistrement/modification/Suppression)
	```
	type Subscription {

		// Déclaration d'une action de souscription paramétrée par la catégorie e photo
		pictureSubscription(category: PictureCategory): Picture
	}
	```
	```
	Tout comme les 'Query' ou les 'Mutation', les 'Subscription' peuvent être déclaré avec des paramètres. Ces paramètres, peuvent être utilisés par le "Resolver (L'implémentation)" de la souscription afin d'opérer des filtres sur les évènements à retourner au client.
	```

# Définition de schémas d'API GraphQL

1.	Nous pouvons définir un schéma GraphQL

	*	Dans un fichier dédié d'extension `.graphql` ou `.js`, et le charger via une directive `import` ou `require` selon les cas.
	Création d'un fichier js contenant le schema `schema.js`
	```
	const { gql } = require('apollo-server');

	// Define a schema
	const typeDefs = gql`...`;

	module.exports.typeDefs = typeDefs;
	```
	*	Importation du schema dans l'applicaion
	```
	const { ApolloServer, gql } = require('apollo-server');
	const gqlSchema = require("./schema");
	
	// Define a schema
	const typeDefs = gqlSchema.typeDefs;

	// Picture Array
	const pictures = [];

	// Define a resolver that fetch data on the preceding schema
	const resolvers = {
		Query: {...},
		Mutation: {...}
	}

	// Define a graphql server to expose typeDefs and resolvers
	const server = new ApolloServer({
		typeDefs,
		resolvers
	});

	// Define port
	const port = process.env.PORT || 5001;

	// Start GraphQL Server
	server.listen(port).then(({ url }) => console.log(`Serveur GraphQL démarré : [ URL = ${url} ]`));
	```

	*	Sous forme de chaine de caractère (pour des besoins d'apprentissage)

		Il suffira de ramener la définition des types dans le fichier principal