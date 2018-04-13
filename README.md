Archknife
=====

Archknife is a android annotation library to generate the project's activity/fragment/viewmodel dagger structure. Dagger usually needs a certain amount of extra work in order to keep it's structure. Using this library, most of the structuring part won't be needed anymore.

* Artifact which is used to access the base annotations of this library.

  [![Download](https://api.bintray.com/packages/mordag/android/archknife-annotation/images/download.svg) ](https://bintray.com/mordag/android/archknife-annotation/_latestVersion)

* Artifact which includes the annotation processor.

  [![Download](https://api.bintray.com/packages/mordag/android/archknife-processor/images/download.svg) ](https://bintray.com/mordag/android/archknife-processor/_latestVersion) 

* Artifact for providing ViewModel objects to the structure (annotation + factory).

  [![Download](https://api.bintray.com/packages/mordag/android/archknife-viewmodel/images/download.svg) ](https://bintray.com/mordag/android/archknife-viewmodel/_latestVersion)

* Artifact for different helper classes which simplifies the usage of Dagger in general.

  [![Download](https://api.bintray.com/packages/mordag/android/archknife-extension/images/download.svg) ](https://bintray.com/mordag/android/archknife-extension/_latestVersion)

Download
--------
```gradle
repositories {
  jcenter()
}

dependencies {
  implementation 'org.archknife:archknife-annotation:0.2.0'
  kapt 'org.archknife:archknife-processor:0.2.0'
  
  implementation 'org.archknife:archknife-viewmodel:0.2.0' //ability to provide viewmodel to the structure
  implementation 'org.archknife:archknife-extension:0.2.0' //includes optional helper classes
}
```

How do I use Archknife? (Step-by-step introduction for 0.2.0)
-------------------
Coming soon! For now, use the [example project][3] as a reference.

Status
------
Version 1.0.0 is currently under development in the master branch.

Comments/bugs/questions/pull requests are always welcome!

Compatibility
-------------

 * The artifacts **archknife-annotation** and **archknife-processor** require at minimum Java 7 or Android 2.3.
 * The artifact **archknife-extension** and **archknife-viewmodel** requires at minimum Android 14.
 
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
