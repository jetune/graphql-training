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
        totalPhotos: Int!
    }
	type Mutation {
        postPhoto(name: String!, description: String!): Boolean!
    }
`
```

8.	Déclarer un tableau qui contiendra les photos postées depuis le client de l'API
```	
// Photos Array
const photos = [];
```

9.	Adaptez le resolver de l'opération `totalPhotos` afin de renvoyer le nombre exact de photos et implémentez un resolver pour l'opération de mutation rajoutée dans le schéma
```	

// define a resolver that fetch data on the preceding schema
const resolvers = {
    Query: {
        totalPhotos: () => photos.length
    },
    Mutation: {
        postPhoto(parent, args) {

            // Add the new photo in the tab
            photos.push({
                name: args.name,
                description: args.description
            });

            // Return the result status
            return true;
        }
    }
}

```

11. Démarrez votre application

    *   `npm start`

12.	Ouvrez un nouvel onglet de votre client GraphQL (GraphQL Playgroud) et faites-le pointer sur l'URL de votre serveur GraphQL

	*	`http://localhost:5001`

13.	Dans la documentation sur cet onglet, vous verrez que l'API contient maintenant un nouveau type `Mutation` présentant l'opération que nous avons définit dans le schéma plus haut.

	*	Dans un onglet à part, exécutons tout d'abors la requête de décompte des photos: le résultat est 0

		```
		query myQuery {
			totalPhotos
		}
		```
	*	Exécutons ensuite la mutation suivante, permettant de rajouter une nouvelle photo

		```
		mutation addPhoto {
			postPhoto(name: "photo-01", description: "Photo 01")
		}
		```
	
	* Exécutons de nouveau la requête de décompte, elle renvoie maintenant 1
