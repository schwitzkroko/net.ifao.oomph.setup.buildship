# i:FAO Gradle-plugin "buildship-eclipsebuild"

## Overview

This *buildship-eclipsebuild* project is just a Gradle plugin packaging and distribution of the *buildSrc* folder used by the Gradle build of [Eclipse Buildship plugin](https://projects.eclipse.org/projects/tools.buildship).

The project helps generally with building OSGi bundles for Eclipse plug-ins and Rich Client Applications. Specifically its plug-ins do this:

| Plugin `eclipsebuild.*`    	| Purpose     								|
| --------------------------- | --------------------------------- |
| `BuildDefinitionPlugin` 		| applied on root project 				|
| `BundlePlugin`          		| applied on plugin subproject    	|
| `FeaturePlugin`         		| applied on feature subproject    	|
| `UpdateSitePlugin`      		| applied on site subproject, refers plugin prijects and feature 	|
| `TestBundlePlugin` 			| can be used for a subproject that runs autometed tests 		|

Refer to the original documentation locally here: [Build Usage](./docs/Usage.md) - or at [buildship github project](https://github.com/eclipse/buildship/tree/master/docs/pluginbuild).

## TODO

- for Jenkins builds configure Nexus to let pass SDK downloads (or acquire from different location?****)
- see above, check how [Eclipse Tycho](https://www.eclipse.org/tycho/) does it
- check if there is a better way to package and announce contained Plugins, in code use I:FAO Java package name
- very simple demo project, that includes automated testing - look out the invisible hand ;-)