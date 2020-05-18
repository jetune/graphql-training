const { gql } = require('apollo-server');

// Define a schema
const typeDefs = gql`
    type Query {
        totalPictures: Int!
    }
`;

module.exports.typeDefs = typeDefs;