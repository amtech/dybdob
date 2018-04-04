Libraries to help track project warning signs ('broken windows') over time and enforce their cleanup.

Releasing
=========

Requirements: Java7 ...

Java8's Javadoc configuration by default has stricter standards for javadoc
through `doclint`.  There are errors during the `mvn release..` process
when running under Java8 (see [1]).

At the moment the parent `pom` for this module configures an older version of the
`maven-javadoc-plugin` and `site` plugin configuration which does not explictly
configure any override for `doclint` and so running `mvn release` under Java8 _*WILL*_
error.  Please set `JAVA_HOME` to a Java7 until this is resolved.

TODO: We need to remove/update the parent pom definition to update plugin configuration
and release profiles to maintain the original "less strict" javadoc configuration.

[1] - Java8/Javadoc/doclint problem: https://stackoverflow.com/questions/15886209/maven-is-not-working-in-java-8-when-javadoc-tags-are-incomplete