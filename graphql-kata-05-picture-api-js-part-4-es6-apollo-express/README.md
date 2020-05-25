# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

Le but de cette petite parenthèse est de vous montrer comment intégrer le serveur GraphQL `Apollo` avec l'outil `Express`.
`Express` est un framework permettant le développement rapide d'application `NodeJS`. Il propose un ensemble de fonctionnalités éprouvées prenant en charge un ensemble de probématique générales et spécifiques d'application Web.
*	Le `routage` qui va permettre de gérer les `Endpoints` d'une application web
*	Les `middlewares` qui permettent d'implémenter une chaîne d'intercepteurs afin de traiter la requête et produire la réponse. Ils se comportent comme des `Servlet Filter (pour les javaistes)`. Ces `middlewares` donne accès aux objets `request` et `response`, ainsi qu'à une fonction `next` permettant de passer la main au prochain `middleware`
*	Les `Templates` qui permettent de générer des vues dynamiquement à partir d'un modèle.
*	La gestion des erreurs
*	Le `débogage`
*	etc...

# Let's go

0.	Rendez-vous dans le répertoire du Kata

1.	Supprimer l'utilisation du package `apollo-server` et installez les packages `apollo-server-express` et `express`
	
	*	`npm remove apollo-server`
	*	`npm install apollo-server-express express --save-prod`

2.	Dans le schéma `schema.js` changez la bibliothèque d'import de l'objet `gql` et importez-le depuis `apollo-server-express`

3.	De même, dans le fichier `index.js`, adaptez l'import des classes `ApolloSerer` et `PubSub` et importez-le depuis `apollo-server-express` et importez la fonction `express` depuis le package `express`

```
import { ApolloServer, PubSub } from 'apollo-server-express';
import express from 'express';
```

4.	Instanciez une nouvelle application middleware `express`
```
// Création d'une instance applicative express
const app = express();
```

4.	Associez l'application middleware `express` au serveur `Apollo` après l'avoir configuré
```

// Define a graphql server to expose typeDefs and resolvers
const server = new ApolloServer({
    ...
});

// Mount Express App Middleware on apollo server
server.applyMiddleware({ app });
```

5.	Définissez le routage de la page d'accueil de l'application et configurez le port d'écoute
```	
// Create home route
app.get('/', (request, response) => response.send('Welcome to PhotoShare API'));

// Define port
const port = process.env.PORT || 5001;

// Start GraphQL Server
app.listen({ port: port }, () => console.log(`Serveur GraphQL démarré : [ PATH = http://localhost:${port}${server.graphqlPath} ]`));
```

6.	Démarrez votre application après avoir installé les dépendances
```	
npm install && npm start
```

7.	Testez l'API en allant sur l'URL qu'elle expose : `http://localhost:5001/graphql`
