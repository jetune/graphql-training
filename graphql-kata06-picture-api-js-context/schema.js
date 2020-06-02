import { gql } from 'apollo-server-express';

// Define a schema
const typeDefs = gql`
    scalar DateTime
    enum PictureCategory {
        SELFIE,
        PORTRAIT,
        LANDSCAPE,
        GRAPHIC,
        ACTION
    }
    type User {
        login: String!,
		name: String,
		avatar: String,
		publishedPictures: [Picture!]!
    }
    type Picture {
        id: ID!,
        name: String!,
        url: String!,
        description: String,
        category: PictureCategory!,
        postBy: User!,
        postDate: DateTime
    }
    input UserInput {
        login: String!,
		name: String,
		avatar: String
    }
    input PictureInput {
        name: String!,
        description: String,
        category: PictureCategory=SELFIE,
        postBy: String!
    }
    type Query {
        totalPictures: Int!,
        allPictures: [Picture!]!,
        filterPictures(category: PictureCategory, first: Int! = 0, count: Int! = 5): [Picture!]!,
        allUsers: [User!]!,
        userByLogin(login: String!): User!
    }
    type Mutation {
        postPicture(picture: PictureInput!): Picture!,
        postUser(user: UserInput!): User!
    }
    type Subscription {
        pictureAdded: Picture,
        userAdded: User
    }
`;

// Event type
const PICTURE_ADDED_EVENT_TYPE = "PictureAddedEvent";

// Event type
const USER_ADDED_EVENT_TYPE = "UserAddedEvent";

// Export
export {

    // Export Add picture event type
    PICTURE_ADDED_EVENT_TYPE,

    // Export Add user event type
    USER_ADDED_EVENT_TYPE,

    // Export Typedef
    typeDefs
}
