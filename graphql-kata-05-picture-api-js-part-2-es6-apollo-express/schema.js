import { gql } from 'apollo-server';

// Define a schema
const typeDefs = gql`
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
    type Subscription {
        pictureAdded: Picture
    }
`;

// Event type
const PICTURE_ADDED_EVENT_TYPE = "PictureAddedEvent";

// Export
export {
    PICTURE_ADDED_EVENT_TYPE,
    typeDefs
}
