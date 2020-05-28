# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

L'objectif de ce Kata est de présenter la notion de contexte, permettant au serveur Apollo de stocker des variables au nuveau global et accessible depuis les `Resolvers`. Ce contexte pourra par exemple être utiliser afin de :
*	Stocker des Clients Rest
*	Stocker des connexions vers la base de données
*	etc...

Dans notre Kata, nous allons voir comment exploiter une base `NoSQL Mongo` et afin d'y stocker nos photos.

# Let's go

0.	Installez un serveur local `MongoDB` et initialiser notre base de donnée en utilisant lescript fourni dans le fichier `initializedb.js`
	*	Vérifiez que votre base de donnée `Mongo` est bien installée, démarrée et que le Shell `mongo` se connecte bien au serveur `Mongo`
	*	sortez du Shell `Ctrl+C` et initialisez la base de donnée avec la commande 
	```
	mongo --port 27017 ./initializedb.js
	```
	*	Ouvrez le fichier de configuration du service `Mongo` (`/etc/mongod.conf` pour Linux et `/usr/local/etc/mongod.conf` pour OSX) et activez l'authentification en rajoutant les lignes
	```
	security:
    	authorization: "enabled"
	```
	*	Redémarrez le service `Mongo` via la commande `sudo mongod restart` pour Linux et `sudo brew services restart mongodb-community` pour OSX (Pour ceux qui ont installé via brew)
	*	Testez la connexion avec l'utilisateur `graphql` sur le schéma qui lui est attribué
	```
	mongo --port 27017  --authenticationDatabase graphqltraining -u graphql -p graphql
	```
	*	Vérifiez l'initialisation de notre base de données et de nos collections et documents
	Listons les base de données et vérifions que la notre y est (`graphqltraining`)
	```
	show dbs
	```
	Connectons nous sur notre base de données
	```
	use graphqltraining
	```
	Listons les collections créées et vérifions que `pictures` et `users` y sont
	```
	show collections
	```
	Listons les documents contenus dans la collection `pictures`
	```
	db.pictures.find()
	```
	Listons les documents contenus dans la collection `users`
	```
	db.users.find()
	```

1.	Installez le packages
	*	Mongodb `npm install mongodb` pour l'accès à la base de données
	*	Dotenv	`npm install dotenv` pour la gestion des ficgiers de variables d'environnement

2.	Créez un fichier de variables d'environnements `.env` avec le contenu suivant
```
DATABASE_HOST=mongodb://graphql:graphql@localhost:27017/graphqltraining
```

3.	Dans le fichier `index.js`, importer la classe `MongoClient` ainsi que la fonction `dotenv`, initialisez le chargement du fichier de variables d'environnements
```
import dotenv from 'dotenv';
import { MongoClient } from 'mongodb';

// Load the config
dotenv.config();
```
4.	Initialisez la connexion au serveur `Mongo` et récupérer une instance du handler de notre base de données de training et stockez la dans une constante qui contiendra toutes les données de contexte
```

// Create connecion client to database
const dbClient = await MongoClient.connect(process.env.DATABASE_HOST, { useNewUrlParser: true });

// Extraction de la référence de base de données
const db = dbClient.db();

// Extraction de la référence de base de données et stockage dans une variable
const context = { db };

```

5.	Construisez le serveur `Apollo` en lui spécifiant les données de contexte
```

// Define a graphql server to expose typeDefs and resolvers
const server = new ApolloServer({
    typeDefs,
    resolvers,
    context,
    subscriptions: {
        path: "/",
        onConnect: () => console.log("=======> Connection to subscription")
    }
});
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

7.	Testez l'API en allant sur l'URL qu'elle expose : `http://localhost:5001/graphql``
