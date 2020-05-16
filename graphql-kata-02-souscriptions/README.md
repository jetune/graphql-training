# Formation d'introduction à GraphQL
<a href="http://www.adservio.fr/"><img width="150" src="https://pbs.twimg.com/profile_images/1057285534459015169/s1_C47ND_400x400.jpg" /></a>
<a href="https://graphql.org/"><img width="400" src="https://blog.soat.fr/wp-content/uploads/2019/01/GraphQL-600x210.png" /></a>


# Composition de données : Unions et Interfaces

1.	Exécutons la requête GrapQL `allLifts` permettant d'obtenir la liste des `Lifts` et récupérons uniquement le nom de chaque `Lift`
```
query{
  allLifts {
    name
  }
}
```
2.	Nous nous rendons compte que finalement nous avons besoin aussi du statut de chaque `Lisft` : 
Modifions notre requête afin de sélectionner aussi le statut de chaque `Lift`
```
query{
  allLifts {
    name,
	status
  }
}
```

3.	Nous pouvons nommer nos requêtes: Le nom sera précisé juste après le mot clé `query`
```
query lifts {
  allLifts {
    name,
	status
  }
}
```

4.	L'editeur de requêtes permet d'exécuter une requête à la fois : Si nous rajoutons une autre requête dans l'éditeur et que nous essayons d'exécuter, il nous demandera de choisir la requête à exécuter.

```
query lifts {
  allLifts {
    name,
	status
  }
}

query trails {
  allTrails {
    name,
    difficulty
  }
}
```

5.	Si nous avons tout de même besoin de requêter l'API afin de ramener plusieurs types d'objets, nous devons préciser les requêtes à exécuter dans la même query

```
query lifts {
  allLifts {
    name,
	status
  },
  allTrails {
    name,
    difficulty
  }
}
```

6.	À noter:
	*	Une `Query` est un type racice dans le référentiel GraphQL. Cela signifie qu'il doit être unique et ne peut exister qu'en un seul exemplaire.
	*	Les requêtes (opérations) disponibles, ainsi que leur paramétrages sont définit dans la documentation et le schéma de l'API GraphQL
	*	GrapQL propose un ensemble de type de données de base parmis lesquels : `String, Int, ID`
	*	GrapQL propose aussi une syntaxe permetant de préciser des contraintes sur une propriété, par exemple `!` qui signifie `non null`

7.	Si l'API GrapQL côté serveur expose des requêtes de recherche paramétrées, nous pouvons lui passer des paramètres de recherche. Par exemple, voici une requête permettant de décompter le nombre de `Lift` ouverts
```
query openedLifts {
  liftCount(status: OPEN)
}
```

8.	GrapQL permet aussi de sélectionner procéder à des sélections en profondeur dans les propriétés d'un objet de base. Par exemple, dans le cas d'un objet `Lift` contenant une liste de `Trail`, nous avons la possibilité de sélectionner les champs de chaque instance d'objet `Trail` contenu dans la liste de l'objet `Lift` parent.

```
query trailsAccessedByJazzCat {
	Lift(id:"jazz-cat") {
		capacity,
		trailAccess {
			name,
			difficulty
		}
	}
}
```

9.	GraphQL permet aussi de définir des groupe de sélections de propriétés réutilisables afin déviter la duplication de code, C'est la notion de `frangment`. Par exemple, Imaginons que nous souhaitions, dans plusieurs requêtes sur des `Lifts`, sélectionner les champs:

	*	`name, status, capacity, night, elevationGain`

	Nous pouvons:
	*	créer un fragment regroupant ces propriétés

	```
	fragment liftInfo on Lift {
		name,
		status,
		capacity,
		night,
		elevationGain
	}
	```

	*	Appliquer ce frangment lors de nos divers requetages

	```
	query {
		Lift(id: "jazz-cat") {
			...liftInfo
			trailAccess {
				name
				difficulty
			}
		}
		Trail(id: "river-run") {
			name
			difficulty
			accessedByLifts {
				...liftInfo
			}
		}
	}
	```

10.	Dans l'exemple précédent, nous avons créer un fragment de sélection qui s'applique uniquement aux objets de type `Lift` (`fragment liftInfo on Lift {...}`). Ils ne sera donc pas applicable à des objets de type Trails. Si nous le souhaitons, nous pouvons aussi créer un fragment pour ce type d'objet et l'appliquer de la même manière.

```
fragment trailInfo on Trail {
	name
	difficulty
	accessedByLifts {
		...liftInfo
	}
}
```
