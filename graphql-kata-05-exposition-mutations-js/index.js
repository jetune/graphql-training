const { ApolloServer } = require("apollo-server");

// Define a schema
const typeDefs = `
    type Query {
        totalPictures: Int!
    }
`

// define a resolver that implements previous schema definiion
const resolvers = {
    Query: {
        totalPictures: () => 12
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