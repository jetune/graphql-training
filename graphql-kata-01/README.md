# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

Présentation du concept, installation des outils et initialisation d'un projet de base


# Prerequis de la formation:

1.	Installez l'un des trois selon vos habitudes (moi j'utilise [GraphQL Playgound](https://github.com/prisma-labs/graphql-playground/releases))
    *   [GraphQL Altair (Firefox)](https://addons.mozilla.org/fr/firefox/addon/altair-graphql-client/)
    *   [GraphQL Altair (Chrome)](https://chrome.google.com/webstore/detail/altair-graphql-client/flnheeellpciglgpaodhkhmapeljopja)
    *   [GraphQL Playgound](https://github.com/prisma-labs/graphql-playground/releases)

2.	Installez [Node JS](https://nodejs.org/en/download/)

3.  Vérifier l'installation avec les commandes 
    
    *   `node -v`
    *   `npm -v`

# Initialisation d'un serveur GraphQL JS (Apollo Server)

1.	Initialisez le projet
	
	*	`npm init -y`
	
2.	Installez les packages graphql, apollo et nodemon (nodemon est utile en DevDependencies et permet de prendre à chaud les modifications)
	
	*	`npm install apollo-server graphql --save-prod`
	*	`npm install nodemon --save-dev`

3.	Ajoutez dans le fichier "package.json" un alias de script de demarrage permettant de profiter de nodemon
	
	*	`"start": "nodemon -e js,json,graphql"`
		
		Ceci permettra de demarrer nodemon en lui demandant de monitorer toute modification des fichier ayant les extensions
		js, json ou graphql et de redemarrer le serveur node dans ce cas

4.	Assurez-vous que le fichier de demarrage de votre application soit `index.js`

5.	Créez le fichier `index.js` à la racine du projet

6.	Chargez le serveur Apollo dans le fichier "index.js"
	
	`const { ApolloServer } = require("apollo-server");`

7.	Définissez le schémas de votre API GraphQL
```
const typeDefs = `
    type Query {
        totalPhotos: Int!
    }
`
```

8.	Implémentez le RESOLVER qui prendra en charge les requêtes et Mutations de votre schéma
```	
// define a resolver that fetch data on the preceding schema
const resolvers = {
	Query: {
		totalPhotos: () => 42
	}
}
```

9.	Instantiez et demarrez votre serveur Apollo sur un port de votre choix
```	
const server = new ApolloServer({
	typeDefs,
	resolvers
});
const port = process.env.PORT || 5001;
server.listen(port).then(({ url }) => console.log(`Serveur GraphQL démarré : [ URL = ${url} ]`));
```

11. Démarrez votre application

    *   `npm start`

12.	Ouvrez votre client GraphQL (GraphQL Playgroud) et faites-le pointer sur l'URL de votre serveur GraphQL

13.	Vous avez accès à la documentation de l'API GraphQL et vous pouvez tester votre requête

```
query myQuery {
    totalPhotos 
}
```