# Oomph Buildship import - building and developing

(work in progress)

[TOC]

## ~~Gitflow project~~

Please note that this is project is handled in a gitflow manner now and primary branch is 'develop'!

| command example | Purpose                           |
| --------------------------- | --------------------------------- |
| `git flow release start '1.0.0'`     | create release branch locally |
| `git flow release publish '1.0.0'`   | push release branch to origin |
| `git flow release finish '1.0.0'`    | merge to master, tag, backmerge to develop, remove release branch - *PUSH all local branches and tags after!* |

## Build

### 'build'

Task 'build' in ROOT - plugin [gradle-eclipsebuild](https://github.com/schwitzkroko/gradle-eclipsebuild) is used.


### Update site

Create a release version and deploy:

```bash
{ eclipse_oomph_buildshipimport } master » ./gradlew clean build./gradlew clean build -Prelease.type=release
{ eclipse_oomph_buildshipimport } master » cd net.ifao.oomph.buildshipimport.site/
{ net.ifao.oomph.buildshipimport.site } master » ../gradlew uploadUpdateSite -Prelease.type=release
```

For deploying a snapshot skip the last property "release" on each of the above gradle calls.


## Develop

RCP IDE eclipse... etc TBC

### TODO

(move this to issues?)

- ~~proper integration into the Oomph task lifecycle, maybe support `ImportWaitTime` to set a timeout~~ review?
- ~~support execution of an initial Gradle-Task (one per BuildshipImportTask setupTask-Element)~~ test
- support _more_ Buildship workspace settings override
- ~~introduce a category in generated update site~~ retest
- some automated testing using `eclipsebuild.TestBundlePlugin'
- configure CI build
- ~~mandatory release to community ([EPL 1.0](https://opensource.org/licenses/EPL-1.0)))~~
- release in [GitHub AmadeusITGroup group](https://github.com/AmadeusITGroup)?
- make semantic versioning compliant instead of `version.txt`


## Deployment

...to (existing) update sites:

1. check root project `version.txt` for the current version
2. check root project `gradle.properties` property `release.type` (`snapshot` or `release`). This determines the target repo location.
3. after or with build use the task `uploadUpdateSite` from the `*.site` subproject to upload and modify existing metadata












