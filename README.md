Archknife
=====

Archknife is a android annotation library to generate the project's activity/fragment/viewmodel dagger structure. Dagger usually needs a certain amount of extra work in order to keep it's structure. Using this library, most of the structuring part won't be needed anymore.

* Artifact which is used to access the base annotations of this library.

  [![Download](https://api.bintray.com/packages/mordag/android/archknife-annotation/images/download.svg) ](https://bintray.com/mordag/android/archknife-annotation/_latestVersion)

* Artifact which includes the annotation processor.

  [![Download](https://api.bintray.com/packages/mordag/android/archknife-processor/images/download.svg) ](https://bintray.com/mordag/android/archknife-processor/_latestVersion) 

* Artifact that includes helper classes for the viewmodel structure.

  [![Download](https://api.bintray.com/packages/mordag/android/archknife-viewmodel/images/download.svg) ](https://bintray.com/mordag/android/archknife-viewmodel/_latestVersion)
  
* Artifact that includes the annotation for the viewmodel structure.

  [![Download](https://api.bintray.com/packages/mordag/android/archknife-viewmodel-annotation/images/download.svg) ](https://bintray.com/mordag/android/archknife-viewmodel-annotation/_latestVersion)

* Artifact for different helper classes which simplifies the usage of Dagger in general.

  [![Download](https://api.bintray.com/packages/mordag/android/archknife-extension/images/download.svg) ](https://bintray.com/mordag/android/archknife-extension/_latestVersion)

Download
--------
```gradle
repositories {
  jcenter()
}

dependencies {
  //just base annotations
  implementation 'org.archknife:archknife-annotation:0.2.1'
  //just viewmodel helper classes
  implementation 'org.archknife:archknife-viewmodel:0.2.1'
  //just viewmodel annotation; you also need to use the artifact archknife-viewmodel if you want to use this dependency!
  implementation 'org.archknife:archknife-viewmodel-annotation:0.2.1'
  //just optional helper classes
  
  kapt 'org.archknife:archknife-processor:0.2.1'
}
```

How do I use Archknife? (Step-by-step introduction for 0.2.1)
-------------------
Coming soon! For now, use the [example project][3] as a reference.

Status
------
Version 1.0.0 is currently under development in the master branch.

Comments/bugs/questions/pull requests are always welcome!

Compatibility
-------------

 * The artifacts **archknife-annotation**, **archknife-viewmodel-annotation** and **archknife-processor** require at minimum Java 7 or Android 2.3.
 * The artifact **archknife-core**, **archknife-extension** and **archknife-viewmodel** requires at minimum Android 14.
 
TODO
-------------
* Unit testing
* Full documentation (source code and wiki)

Author
------
Alexander Eggers - [@mordag][2] on GitHub

License
-------
Apache 2.0. See the [LICENSE][1] file for details.


[1]: https://github.com/Mordag/archknife/blob/master/LICENSE
[2]: https://github.com/Mordag
[3]: https://github.com/Mordag/archknife/tree/master/examples
