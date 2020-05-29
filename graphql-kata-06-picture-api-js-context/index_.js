import { ApolloServer, PubSub } from 'apollo-server-express';
import express from 'express';
import { typeDefs, PICTURE_ADDED_EVENT_TYPE, USER_ADDED_EVENT_TYPE } from './schema';

// Création d'une instance applicative express
const app = express();

// Instantiate a publisher/subscriber
const pubsub = new PubSub();

// Picture Array
const pictures = [];

// Define a resolver that fetch data on the preceding schema
const resolvers = {
    Query: {
        totalPictures: () => pictures.length,
        allPictures: () => pictures
    },
    Mutation: {
        postPicture(_parent, args) {

            // Instantiate the new picture
            const newPicture = {
                id: pictures.length + 1,
                name: args.picture.name,
                description: args.picture.description,
                category: args.picture.category
            };

            // Add the new picture in the tab
            pictures.push(newPicture);

            // Publish event
            pubsub.publish(PICTURE_ADDED_EVENT_TYPE, { pictureAdded: newPicture });

            // Return the registered picture
            return newPicture;
        }
    },
    Picture: {
        url: (_parent) => `http://lab.adservio.fr/media/${_parent.id}.jpg`
    },
    Subscription: {
        pictureAdded: {
            subscribe: () => pubsub.asyncIterator([PICTURE_ADDED_EVENT_TYPE])
        }
    }
}

// Define a graphql server to expose typeDefs and resolvers
const server = new ApolloServer({
    typeDefs,
    resolvers,
    subscriptions: {
        path: "/",
        onConnect: () => console.log("=======> Connection to subscription")
    }
});

// Mount Express App Middleware on apollo server
server.applyMiddleware({ app, path: '/graphql' });

// Create home route
app.get('/', (request, response) => response.send('Welcome to PhotoShare API'));

// Define port
const port = process.env.PORT || 5001;

// Start GraphQL Server
app.listen({ port: port }, () => console.log(`Serveur GraphQL démarré : [ PATH = http://localhost:${port}${server.graphqlPath} ]`));