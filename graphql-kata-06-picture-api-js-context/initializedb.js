// Connect to Mongo
const mongoConnexion = new Mongo("localhost:27017");

// User collection datas
const users = [
    { "login": "jetune", "name": "Jean-Jacques ETUNE NGI", "avatar": "" },
    { "login": "ryo", "name": "Sakazaki RYO", "avatar": "" },
    { "login": "eiji", "name": "Kisaragi EIJI", "avatar": "" },
    { "login": "ryuji", "name": "YAMAZAKI Ryuji", "avatar": "" },
    { "login": "terry", "name": "BOGARD Terry", "avatar": "" }
];

// Picture collections
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

// mongoUsers to create
const mongoUsers = [
    {
        db: "admin",
        name: "admin",
        password: "admin",
        roles: [
            {
                role: "userAdminAnyDatabase", db: "admin"
            },
            "readWriteAnyDatabase"
        ]
    },
    {
        db: "graphqltraining",
        name: "graphql",
        password: "graphql",
        roles: [
            {
                role: "readWrite", db: "graphqltraining"
            }
        ]
    }
];

// mongoCollections to create
const mongoCollections = [
    {
        db: "graphqltraining",
        name: "users",
        options: {
            autoIndexId: true
        },
        datas: users
    },
    {
        db: "graphqltraining",
        name: "pictures",
        options: {
            autoIndexId: true
        },
        datas: pictures
    }
];

// Create all mongoCollections
mongoCollections.forEach(collection => {

    // Select the Database
    const selectedDb = mongoConnexion.getDB(collection.db);

    // Find collection
    const foundedCollection = selectedDb.getCollection(collection.name);

    // Drop collection if exists
    if (foundedCollection !== undefined && foundedCollection !== null) foundedCollection.drop();

    // Create the collection
    selectedDb.createCollection(collection.name, collection.options);

    // Insert Datas
    selectedDb.getCollection(collection.name).insertMany(collection.datas);
});

// Create all mongoUsers
mongoUsers.forEach(user => {

    // Select the Database
    const selectedDb = mongoConnexion.getDB(user.db);

    // Drop user if exists
    selectedDb.dropUser(user.name);

    // Create the user
    selectedDb.createUser({
        user: user.name,
        pwd: user.password,
        roles: user.roles
    });
});