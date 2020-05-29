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
4.	Créez une fonction asynchrone d'initialisation de l'application qui se charge 
	*	D'obtenir une connexion vers la base de donnée
	*	Initialiser le contexte Apollo
	*	Demarrer le serveur express et autres composants
	*	Adapter les `Query` et `Mutation` afin d'exploiter la base de données `Mongo`
```
// Fonction asynchrone d'initialization de la connexion à la base de données
async function start() {

    // Create connecion client to database
    const dbClient = await MongoClient.connect(process.env.DATABASE_HOST, { useNewUrlParser: true });

    // Log
    console.log('Connexion to database OK');

    // Extract db instance
    const db = dbClient.db();

    // Création d'une instance applicative express
    const app = express();

    // Instantiate a publisher/subscriber
    const pubsub = new PubSub();

    // Define a resolver that fetch data on the preceding schema
    const resolvers = {
        Query: {
            totalPictures: () => db.collection('pictures').estimatedDocumentCount(),
            allPictures: () => db.collection('pictures').find().toArray(),
            allUsers: () => db.collection('users').find().toArray(),
            userByLogin: (_parent, args) => db.collection('users').findOne({ login: args.login }),
            filterPictures: (_parent, args) => db.collection('pictures').find({ category: args.category }).limit(args.count).skip(args.first).toArray()
        },
        Mutation: {
            async postUser(_parent, args) {

                // Instantiate a user
                const newUser = {
                    login: args.user.login,
                    name: args.user.name,
                    avatar: args.user.avatar,
                    publishedPictures: []
                };

                // Add the new user in the tab
                db.collection('users').insertOne(newUser);

                // Publish event
                pubsub.publish(USER_ADDED_EVENT_TYPE, { userAdded: newUser });

                // return user created
                return newUser;
            },
            async postPicture(_parent, args) {

                // Get the picture count
                const pictureCount = await db.collection('pictures').estimatedDocumentCount();

                // get user by name
                const owner = await db.collection('users').findOne({ login: args.picture.postBy });

                // Instantiate the new picture
                const newPicture = {
                    id: pictureCount + 1,
                    name: args.picture.name,
                    description: args.picture.description,
                    category: args.picture.category,
                    url: `http://lab.adservio.fr/media/${pictureCount + 1}.jpg`,
                    postBy: owner,
                    postDate: new Date()
                };

                // Add the new picture in collection
                db.collection('pictures').insertOne(newPicture);

                // Publish event
                pubsub.publish(PICTURE_ADDED_EVENT_TYPE, { pictureAdded: newPicture });

                // Return the registered picture
                return newPicture;
            }
        },
        User: {
            publishedPictures: (_parent) => db.collection('pictures').find({ "postBy.login": _parent.login }).toArray()
        },
        Subscription: {
            pictureAdded: {
                subscribe: () => pubsub.asyncIterator([PICTURE_ADDED_EVENT_TYPE])
            },
            userAdded: {
                subscribe: () => pubsub.asyncIterator([USER_ADDED_EVENT_TYPE])
            }
        },
        DateTime: new GraphQLScalarType({
            name: "DateTime",
            description: "Datetime custom scalar type",
            parseValue: (value) => new Date(value),
            serialize: (value) => value.toISOString(),
            parseLiteral: (ast) => new Date(ast.value)
        })
    }

    // Define a graphql server to expose typeDefs and resolvers
    const server = new ApolloServer({
        typeDefs,
        resolvers,
        context: { db },
        subscriptions: {
            onConnect: () => console.log("=======> Connection to subscription")
        }
    });

    // Mount Express App Middleware on apollo server
    server.applyMiddleware({ app });

    // Create an HTTP Server
    const httpServer = http.createServer(app);

    // Install Subscription handlers
    server.installSubscriptionHandlers(httpServer);

    // Define port
    const port = process.env.PORT || 5001;

    // Start GraphQL Server
    httpServer.listen(port, () => {
        console.log(`🚀 Server ready at http://localhost:${port}${server.graphqlPath}`)
        console.log(`🚀 Subscriptions ready at ws://localhost:${port}${server.subscriptionsPath}`)
    });
};
```

5.	Démarrez et testez la nouvelle version de l'API
