const { ApolloServer } = require("apollo-server");

// Define a schema
const typeDefs = `
    enum PictureCategory {
        SELFIE,
        PORTRAIT,
        LANDSCAPE,
        GRAPHIC,
        ACTION
    }
    type Picture {
        id: ID!,
        name: String!,
        url: String!,
        description: String,
        category: PictureCategory!
    }
    input PictureInput {
        name: String!,
        description: String,
        category: PictureCategory=SELFIE
    }
    type Query {
        totalPictures: Int!,
        allPictures: [Picture!]!
    }
    type Mutation {
        postPicture(picture: PictureInput!): Picture!
    }
`
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

            // Return the registered picture
            return newPicture;
        }
    },
    Picture: {
        url: (_parent) => `http://lab.adservio.fr/media/${_parent.id}.jpg`
    }
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