import http from 'http';
import { ApolloServer, PubSub } from 'apollo-server-express';
import { typeDefs, PICTURE_ADDED_EVENT_TYPE, USER_ADDED_EVENT_TYPE } from './schema';
import { GraphQLScalarType } from 'graphql';
import express from 'express';

// Création d'une instance applicative express
const app = express();

// Instantiate a publisher/subscriber
const pubsub = new PubSub();

// User array
const users = [
    { "login": "jetune", "name": "Jean-Jacques ETUNE NGI", "avatar": "" },
    { "login": "ryo", "name": "Sakazaki RYO", "avatar": "" },
    { "login": "eiji", "name": "Kisaragi EIJI", "avatar": "" },
    { "login": "ryuji", "name": "YAMAZAKI Ryuji", "avatar": "" },
    { "login": "terry", "name": "BOGARD Terry", "avatar": "" }
];

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

// Define a resolver that fetch data on the preceding schema
const resolvers = {
    Query: {
        totalPictures: () => pictures.length,
        allPictures: () => pictures,
        allUsers: () => users,
        userByLogin: (_parent, args) => users.find((user) => user.login === args.login),
        filterPictures: (_parent, args) => pictures.filter(picture => picture.category === args.category).slice(args.first, Math.min(args.first + args.count, pictures.length + 1))
    },
    Mutation: {
        postUser(_parent, args) {

            // Instantiate a user
            const newUser = {
                login: args.user.login,
                name: args.user.name,
                avatar: args.user.avatar,
                publishedPictures: []
            };

            // Add the new user in the tab
            users.push(newUser);

            // Publish event
            pubsub.publish(USER_ADDED_EVENT_TYPE, { userAdded: newUser });

            // return user created
            return newUser;
        },
        postPicture(_parent, args) {

            // Instantiate the new picture
            const newPicture = {
                id: pictures.length + 1,
                name: args.picture.name,
                description: args.picture.description,
                category: args.picture.category,
                postBy: users.find((user) => user.login === args.picture.postBy),
                postDate: new Date()
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
    User: {
        publishedPictures: (_parent) => pictures.filter(picture => picture.postBy.login === _parent.login)
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
