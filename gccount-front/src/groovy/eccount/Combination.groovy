//http://stackoverflow.com/a/5167621/432903

("gccount" as List).subsequences()*.permutations().inject( [] ) { list, set ->
  list.addAll( set )
  list
}*.join().sort { it.length() }
