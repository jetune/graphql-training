# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

Ce Kata est la continuité du Kata précédent et permet de présenter une utilisation un peu plus avancée des différentes opérations fournies par un schéma GraphQL et concernant :
*	L'implémentation de nouveaux types `scalar`
*	Les relations entre type (One-To-One, One-To-Many, Many-To-Many, etc..)
*	La pagination de résultats
*	Les filtres de souscription

# Let's go

0.	Rendez-vous dans le répertoire du Kata

1.	Installez les dépendances du projet
	
	*	`npm install`

2.	Déclarez dans le schéma un type `DateTime` qui représentera une Date et une heure
```
scalar DateTime;
```

3.	Rajoutez dans le schéma un type `User` représentant l'utilisateur ayant posté la photo
```
const typeDefs = `
	...
    type User {
        login: String!,
		name: String,
		avatar: String,
		publishedPictures: [Picture!]!
    }
	...
`
```

4.	Refactorez ensuite le type `Picture` afin d'y intégrer la date de publication de la photo ainsi que l'utilisateur ayant publié la photo
```
type Picture {
	id: ID!,
	name: String!,
	url: String!,
	description: String,
	category: PictureCategory!,
	postBy: User!,
	postDate: DateTime!
}
```

5.	Créez ensuite un `input` pour les utilisateurs (Attention à respecter la non cylcicité des références dans les Inputs - Suppression du champ de collection de `Picture` qui entrainerait une cyclicité -)
```	
input UserInput {
	login: String!,
	name: String,
	avatar: String
}
```

6.	Adaptez l'`input` des photos afin qu'il référence l'`input` utilisateur. La date de Post sera calculée lors du Post effectif de la photo.
```	
input PictureInput {
	name: String!,
	description: String,
	category: PictureCategory=SELFIE,
	postBy: String!
}
```

7.	Rajoutez deux requêtes permettant respectivement d'obtenir un utilisateur par son `login`, ainsi que la liste des utilisateurs
```
type Query {
	...
	allUsers: [User!]!,
	userByLogin(login: String!): User!
}
```

8.	Rajoutez dans le fichier `index.js` une liste d'utilisateurs pour nos besoins
```

// User array
const users = [
    { "login": "jetune", "name": "Jean-Jacques ETUNE NGI", "avatar": "" },
    { "login": "ryo", "name": "Sakazaki RYO", "avatar": "" },
    { "login": "eiji", "name": "Kisaragi EIJI", "avatar": "" },
    { "login": "ryuji", "name": "YAMAZAKI Ryuji", "avatar": "" },
    { "login": "terry", "name": "BOGARD Terry", "avatar": "" }
];
```

9.	Rajoutez l'implémentation des deux requêtes `allUsers` et `userByLogin` (Je vous laisse le faire)

10.	Adaptez le `resolver` de la mutation d'enregistrement des photos afin qu'elle prenne en compte les champs `postBy` et `postDate`

```
Mutation: {
	postUser(_parent, args) {...},
	postPicture(_parent, args) {

		// Instantiate the new picture
		const newPicture = {
			...
			postBy: users.find((user) => user.login === args.picture.postBy),
			postDate: new Date()
		};
		...
	}
}
```

11.	Rajoutez un `resolver` permettant de définir comment sera calculer le champ `publishedPictures` du type `User`

7.	Rajoutez un `resolver` permettant de prendre en charge les opérations de serialisation/deserialisation des champs de type `DateTime`. Pour cela n'oubliez pas de rajouter l'import de la classe `GraphQLScalarType` venant de la librairie `apollo-server`
```

// Define a resolver that fetch data on the preceding schema
const resolvers = {
    Query: {...},
    Mutation: {...},
    Picture: {...},
    Subscription: {...},
    DateTime: new GraphQLScalarType({
        name: "DateTime",
        description: "Datetime custom scalar type",
        parseValue: (value) => new Date(value),
        serialize: (value) => value.toISOString(),
        parseLiteral: (ast) => new Date(ast.value)
    })
}

```
Comme vous pouvez le voir, la classe `GraphQLScalarType` permet de décrire comment GraphQL va sérialiser/désérialiser un type de données `scalar` définit pat un développeur. Ici nous allons faire correspondre notre type de données à une `Date` Javascript.

11. Assurez vous que l'applucation est démmarée

    *   `npm start`

12.	Ouvrez un nouvel onglet de votre client GraphQL (GraphQL Playgroud) et faites-le pointer sur l'URL de votre serveur GraphQL

	*	`http://localhost:5001`

13.	Testez votre nouvelle API.

	*	Créez dans un onglet du client, une mutation paramétrée d'enregistrement de photos

		```
		mutation addPicture($picture: PictureInput!) {
			postPicture(picture: $picture) {
				id,
				name,
				url,
				description,
				category,
				postDate,
				postBy {
					login,
					name,
					avatar,
					publishedPictures {
						name,
						url
					}
				}
			}
		}
		```
	*	Exécutez la mutation avec les paramètres suivants

		```
		{
			"picture": {
				"name": "picture1",
				"description": "Pircture 1",
				"category": "LANDSCAPE",
				"category": "SELFIE",
				"postBy": "jetune"
			}
		}
		```

		```
		{
			"picture": {
				"name": "picture2",
				"description": "Pircture 2",
				"category": "SELFIE",
				"postBy": "jetune"
			}
		}
		```

		```
		{
			"picture": {
				"name": "picture3",
				"description": "Pircture 3",
				"category": "PORTRAIT",
				"postBy": "ryo"
			}
		}
		```
	
	* Exécutez ensuite une requête de recherche de toutes les photos
	```
	query listPictures {
		allPictures {
			id,
			name,
			url,
			description,
			category,
			postDate,
			postBy {
				login,
				name,
				avatar,
				publishedPictures {
					id,
					name,
					url,
					description,
					category,
					postDate
				}
			}
		}
	}
	```
	On peut y voir l'ensemble des photos, avec pour chacune la date de publication, ainsi que l'utilisateur ayant publié et même la liste de photos publié par cet utilisateur.
	
	*	Vu que la relation `Picture` -> `User` est bidirectionnelle, nous avons aussi la possibilité, de visualiser un `User` avec la liste des photos postées

	requête paramétrée
	```
	query userByLogin($login: String!) {
		userByLogin(login: $login) {
			login,
			name,
			avatar,
			publishedPictures {
				id,
				name,
				url,
				description,
				category,
				postDate
				}
			}
		}
	```
	Paramètres
	```
	{
		"login": "jetune"
	}
	```
	```
	{
		"login": "ryo"
	}
	```

14.	Je vous laisse rajouter, sur la base de ce que nous avons déjà développer dans les précédents KATAS
	*	Une souscription sur les évènements de création d'un utilisateur

14.	Rajoutez des filtres et de la pagination dans les requêtes de recherche

	*	Dans le schéma, rajoutons la description d'une opération de filtre des `Picture` par `categorie` avec deux autres paramètre indiquant le nombre d'éléments à retourner ainsi que l'index du premer élément à retourner
	```
	filterPictures(category: PictureCategory, first: Int! = 0, count: Int! = 5): [Picture!]!
	```
	*	Dans le fichier `index.js` rajoutez un resolver d'implémentation de la requête de filtre
	```
	Query: {
        ...
        filterPictures: (_parent, args) => pictures.filter(picture => picture.category === args.category).slice(args.first, Math.min(args.first + args.count, pictures.length + 1))
    }
	```
	*	Initialisez la liste des utilisateurs et des photos comme suit
	```
	// User array
	const users = [...];
	
	// Picture Array
	const pictures = [
		{ id: "1", "name": "picture 01", "description": "Pircture 01", url: "http://lab.adservio.fr/media/1.jpg", "category": "PORTRAIT", "postBy": users[0], "postDate": new Date("2020-05-18 16:10:22") },
		{ id: "2", "name": "picture 02", "description": "Pircture 02", url: "http://lab.adservio.fr/media/2.jpg", "category": "SELFIE", "postBy": users[1], "postDate": new Date("2020-05-18 16:30:22") },
		{ id: "3", "name": "picture 03", "description": "Pircture 03", url: "http://lab.adservio.fr/media/3.jpg", "category": "LANDSCAPE", "postBy": users[0], "postDate": new Date("2020-05-18 16:40:22") },
		{ id: "4", "name": "picture 04", "description": "Pircture 04", url: "http://lab.adservio.fr/media/4.jpg", "category": "PORTRAIT", "postBy": users[2], "postDate": new Date("2020-05-18 17:00:22") },
		{ id: "5", "name": "picture 05", "description": "Pircture 05", url: "http://lab.adservio.fr/media/5.jpg", "category": "PORTRAIT", "postBy": users[2], "postDate": new Date("2020-05-18 18:10:22") },
		{ id: "6", "name": "picture 06", "description": "Pircture 06", url: "http://lab.adservio.fr/media/6.jpg", "category": "ACTION", "postBy": users[3], "postDate": new Date("2020-05-18 18:15:22") },
		{ id: "7", "name": "picture 07", "description": "Pircture 07", url: "http://lab.adservio.fr/media/7.jpg", "category": "PORTRAIT", "postBy": users[1], "postDate": new Date("2020-05-18 18:20:22") },
		{ id: "8", "name": "picture 08", "description": "Pircture 08", url: "http://lab.adservio.fr/media/8.jpg", "category": "GRAPHIC", "postBy": users[4], "postDate": new Date("2020-05-18 19:00:22") },
		{ id: "9", "name": "picture 09", "description": "Pircture 09", url: "http://lab.adservio.fr/media/9.jpg", "category": "PORTRAIT", "postBy": users[3], "postDate": new Date("2020-05-18 19:03:22") },
		{ id: "10", "name": "picture 10", "description": "Pircture 10", url: "http://lab.adservio.fr/media/10.jpg", "category": "GRAPHIC", "postBy": users[1], "postDate": new Date("2020-05-18 19:05:22") },
		{ id: "11", "name": "picture 11", "description": "Pircture 11", url: "http://lab.adservio.fr/media/11.jpg", "category": "SELFIE", "postBy": users[2], "postDate": new Date("2020-05-18 19:10:22") },
		{ id: "12", "name": "picture 12", "description": "Pircture 12", url: "http://lab.adservio.fr/media/12.jpg", "category": "PORTRAIT", "postBy": users[1], "postDate": new Date("2020-05-18 19:20:22") },
		{ id: "13", "name": "picture 13", "description": "Pircture 13", url: "http://lab.adservio.fr/media/13.jpg", "category": "PORTRAIT", "postBy": users[1], "postDate": new Date("2020-05-18 19:30:22") },
		{ id: "14", "name": "picture 14", "description": "Pircture 14", url: "http://lab.adservio.fr/media/14.jpg", "category": "GRAPHIC", "postBy": users[3], "postDate": new Date("2020-05-18 19:40:22") },
		{ id: "15", "name": "picture 15", "description": "Pircture 15", url: "http://lab.adservio.fr/media/15.jpg", "category": "LANDSCAPE", "postBy": users[4], "postDate": new Date("2020-05-18 19:50:22") },
		{ id: "16", "name": "picture 16", "description": "Pircture 16", url: "http://lab.adservio.fr/media/16.jpg", "category": "SELFIE", "postBy": users[0], "postDate": new Date("2020-05-19 07:20:22") },
		{ id: "17", "name": "picture 17", "description": "Pircture 17", url: "http://lab.adservio.fr/media/17.jpg", "category": "PORTRAIT", "postBy": users[4], "postDate": new Date("2020-05-19 07:30:22") },
		{ id: "18", "name": "picture 18", "description": "Pircture 18", url: "http://lab.adservio.fr/media/18.jpg", "category": "SELFIE", "postBy": users[4], "postDate": new Date("2020-05-19 07:40:22") },
		{ id: "19", "name": "picture 19", "description": "Pircture 19", url: "http://lab.adservio.fr/media/19.jpg", "category": "LANDSCAPE", "postBy": users[1], "postDate": new Date("2020-05-19 08:30:22") },
		{ id: "20", "name": "picture 20", "description": "Pircture 20", url: "http://lab.adservio.fr/media/20.jpg", "category": "GRAPHIC", "postBy": users[2], "postDate": new Date("2020-05-19 08:40:22") },
		{ id: "21", "name": "picture 21", "description": "Pircture 21", url: "http://lab.adservio.fr/media/21.jpg", "category": "PORTRAIT", "postBy": users[4], "postDate": new Date("2020-05-19 09:10:22") },
		{ id: "22", "name": "picture 22", "description": "Pircture 22", url: "http://lab.adservio.fr/media/22.jpg", "category": "PORTRAIT", "postBy": users[0], "postDate": new Date("2020-05-19 09:20:22") },
		{ id: "23", "name": "picture 23", "description": "Pircture 23", url: "http://lab.adservio.fr/media/23.jpg", "category": "SELFIE", "postBy": users[1], "postDate": new Date("2020-05-19 09:30:22") },
		{ id: "24", "name": "picture 24", "description": "Pircture 24", url: "http://lab.adservio.fr/media/24.jpg", "category": "SELFIE", "postBy": users[3], "postDate": new Date("2020-05-19 09:40:22") },
		{ id: "25", "name": "picture 25", "description": "Pircture 25", url: "http://lab.adservio.fr/media/25.jpg", "category": "PORTRAIT", "postBy": users[3], "postDate": new Date("2020-05-20 09:30:22") },
		{ id: "26", "name": "picture 26", "description": "Pircture 26", url: "http://lab.adservio.fr/media/26.jpg", "category": "LANDSCAPE", "postBy": users[0], "postDate": new Date("2020-05-20 10:30:22") },
		{ id: "27", "name": "picture 27", "description": "Pircture 27", url: "http://lab.adservio.fr/media/27.jpg", "category": "GRAPHIC", "postBy": users[0], "postDate": new Date("2020-05-20 11:30:22") },
		{ id: "28", "name": "picture 28", "description": "Pircture 28", url: "http://lab.adservio.fr/media/28.jpg", "category": "PORTRAIT", "postBy": users[0], "postDate": new Date("2020-05-21 12:30:22") },
		{ id: "29", "name": "picture 29", "description": "Pircture 29", url: "http://lab.adservio.fr/media/29.jpg", "category": "PORTRAIT", "postBy": users[0], "postDate": new Date("2020-05-22 13:30:22") },
		{ id: "30", "name": "picture 30", "description": "Pircture 30", url: "http://lab.adservio.fr/media/30.jpg", "category": "SELFIE", "postBy": users[0], "postDate": new Date("2020-05-23 14:30:22") }
	];
	```
	*	Relancez votre application pour tester la nouvelle API avec la requête paramétrée suivante
	```
	query filterPictures($category: PictureCategory!, $first: Int! = 0, $count: Int! = 5) {
		filterPictures(category: $category, first: $first, count: $count) {
			id,
			name,
			url,
			description,
			category,
			postDate,
				postBy {
				login,
				name
			}
		}
	}
	```
	Paramètrage 1 : Vérifions que on aura au max 3 photos `SELFIE` renvoyées
	```
	{
		"category": "SELFIE",
		"first": 0,
		"count": 3
	}
	```
	Paramètrage 2 : Vérifions que on aura au max 5 photos `SELFIE` renvoyées
	```
	{
		"category": "SELFIE"
	}
	```
	Paramètrage 3 : Vérifions que on aura au max 10 photos `SELFIE` renvoyées (dans ce cas 7 vu que c'est le nombre de SELFIES)
	```
	{
		"category": "SELFIE",
		"first": 0,
		"count": 10
	}
	```

15.	Les filtres de souscription : Je vous laisse le faire en vous inspirant de ce qu'on a fait jusqu'ici.
