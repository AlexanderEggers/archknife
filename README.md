Archknife
=====
[![Download](https://api.bintray.com/packages/mordag/android/archknife-core/images/download.svg) ](https://bintray.com/mordag/android/archknife-core/_latestVersion)

Archknife is a android annotation library to generate the project's activity/fragment/viewmodel dagger structure. Dagger usually needs a certain amount of extra work in order to keep it's structure. Using this library, most of the structuring part won't be needed anymore.

Download
--------
```gradle
repositories {
  jcenter()
}

dependencies {
  //includes all library artifacts
  implementation 'org.archknife:archknife-annotation:0.3.1'
  //just base annotations
  implementation 'org.archknife:archknife-annotation:0.3.1'
  //just viewmodel helper classes
  implementation 'org.archknife:archknife-viewmodel:0.3.1'
  //just viewmodel annotation; useless for Dagger without the artifact 'archknife-viewmodel'
  implementation 'org.archknife:archknife-viewmodel-annotation:0.3.1'
  //just optional helper classes
  implementation 'org.archknife:archknife-extension:0.3.1'
  
  kapt 'org.archknife:archknife-processor:0.3.1'
}
```

How do I use Archknife? (Step-by-step introduction for 0.3.1)
-------------------
Coming soon! For now, use the [example project][3] as a reference.

Status
------
Version 1.0.0 is currently under development in the master branch.

Comments/bugs/questions/pull requests are always welcome!

Compatibility
-------------

 * The library requires at minimum Android 14.
 
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
