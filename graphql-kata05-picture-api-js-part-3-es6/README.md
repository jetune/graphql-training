# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>

Ce Kata a pour objectif de vous présenter l'adapration de notre application à la spécification ES6 (Ecma Script 6)

# Let's go

0.	Rendez-vous dans le répertoire du Kata

1.	Installez les bibliothèques du transmilateur (Compilateur de source à source) `Babel` permettant d'utiliser une syntaxe de code ES6, qui sera transformée et code compatible ES5 et compréhensible par la grande majorité des navigateurs.
	
	*	`npm  install --save-dev @babel/core @babel/node @babel/cli @babel/preset-env`

2.	Créez le fichier `.babelrc` avec le contenu suivant
```
{
    "presets": [
        "@babel/preset-env"
    ]
}
```

3.	Adaptez le script `start` du fichier `package.json` afin qu'il puisse démarrer l'application par le billet de `Babel`
```
"start": "nodemon --exec babel-node index.js"
```

4.	Dans le fichier `schema.js`, remplacez les `require` par des `import` et adaptez l'export
```
import { gql } from 'apollo-server';

...
...

// Export
export {

     // Export Add picture event type
    PICTURE_ADDED_EVENT_TYPE,

    // Export Add user event type
    USER_ADDED_EVENT_TYPE,

    // Export Typedef
    typeDefs
}

```

5.	Dans le fichier `index.js` remplacez les `require` par des `import`
```
import { ApolloServer, PubSub } from 'apollo-server';
import { typeDefs, PICTURE_ADDED_EVENT_TYPE, USER_ADDED_EVENT_TYPE } from './schema';
import { GraphQLScalarType } from 'graphql';
```

6.	Supprimez les consante de noms d'évènements et de type de définition de tye
```

// Define a schema
const typeDefs = gqlSchema.typeDefs;

// Get the event type for added pcture
const PICTURE_ADDED_EVENT_TYPE = gqlSchema.PICTURE_ADDED_EVENT_TYPE;

// Get the event type for added user
const USER_ADDED_EVENT_TYPE = gqlSchema.USER_ADDED_EVENT_TYPE;

```

7.	Démarrez l'application et testez l'API