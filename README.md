Archknife
=====
[![Download](https://api.bintray.com/packages/mordag/android/archknife-core/images/download.svg) ](https://bintray.com/mordag/android/archknife-core/_latestVersion)

Archknife is a android annotation library to generate the project's activity/fragment/viewmodel Dagger-2 structure. Dagger usually needs a certain amount of extra work in order to create and keep a structure. This library helps you to focus on the what really matters: Your App! 

By using this library you need to use only a handful of annotation you can assign to your activities, fragments and viewmodels. Nothing more. Due to these annotations all of those classes will be added to the AndroidInjector (Dagger-2 class).

Download
--------
```gradle
repositories {
  jcenter()
}

dependencies {
  //includes all library artifacts
  implementation 'org.archknife:archknife-core:0.5.0'
  //just annotations
  implementation 'org.archknife:archknife-annotation:0.5.0'
  //just context helper classes
  implementation 'org.archknife:archknife-context:0.5.0'
  
  kapt 'org.archknife:archknife-processor:0.5.0'
  
  //keep in mind that you will need to use the dagger kapt dependency next to the library dependencies
  kapt "com.google.dagger:dagger-compiler:2.16"
  kapt "com.google.dagger:dagger-android-processor:2.16"
}
```

How do I use Archknife? (Step-by-step introduction for 0.5.0)
-------------------
Archknife has five different annotation types.

1. @ProvideApplication

Like "normal" Dagger-2 implementations, this one also requires you to create an application class. This class needs to be declared inside your manifest in order work. This library provides you with a base application class that includes most of the required Dagger-2 implementation. Simply extend your class with the ArchknifeApplication.

```kotlin
@ProvideApplication
class DemoApp : ArchknifeApplication()
```

2. @ProvideActivity

This annotation can be used to attach a certain activity class to Dagger-2. By doing so, you don't need to add this class to any module and declare even more things. Addition to that you have two different interfaces. You need to use at least one of those: Injectable or HasSupportFragmentInjector. Injectable should be used if this activity won't have any fragments attached to it and HasSupportFragmentInjector is required if you want (Dagger-2) fragments to be displayed within this activity. Those interfaces are also used to differ between Dagger-2 (using one of the interfaces) and non-Dagger-2 activities within the AppInjector (application lifecycle handler for Dagger-2).

```kotlin
@ProvideActivity
class DemoActivity: AppCompatActivity(), HasSupportFragmentInjector
```

3. @ProvideFragment

This annotation is responsible in attaching fragments to Dagger-2. Each @ProvideFragment needs at least one activity class that this fragment is working with. You can simply add the activity within the annotation. Addition to the annotation, your fragment needs to implement the Injectable interface. This is used to differ between Dagger-2 and non-Dagger-2 fragments within the AppInjector (application lifecycle handler for Dagger-2).

```kotlin
@ProvideFragment([DemoActivity::class])
class DemoFragment: Fragment(), Injectable
```

4. @ProvideViewModel

This annotation takes not only care to provide the viewmodel class to the Dagger-2 but also to provide it to the ViewModelProvider.Factory that is needed to correctly initialise viewmodels inside your app.

```kotlin
@ProvideViewModel
class DemoViewModel: ViewModel()
```

Here's an example in how to initialise the ViewModel later in your app:

```kotlin
@ProvideActivity
class DemoActivity: AppCompatActivity(), HasSupportFragmentInjector {

  @Inject
  lateinit var factory: ViewModelProvider.Factory
  
  private lateinit var viewModel: DemoViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(DemoViewModel::class.java)
  }
  
  ...
}
```

5. @ProvideModule

From time to time you probably will need a custom module to define certain dependencies. You can read more about @Modules at the [Dagger webpage][4]. Therefore this class also needs to use the @Module annotation which is provided by Dagger-2.

```kotlin
@ProvideModule
@Module
class DemoModule
```

Status
------
Version 1.0.0 is currently under development in the master branch.

Comments/bugs/questions/pull requests are always welcome!

Compatibility
-------------

 * The library requires at minimum Android 14.

Author
------
Alexander Eggers - [@mordag][2] on GitHub

License
-------
Apache 2.0. See the [LICENSE][1] file for details.


[1]: https://github.com/Mordag/archknife/blob/master/LICENSE
[2]: https://github.com/Mordag
[3]: https://github.com/Mordag/archknife/tree/master/examples
[4]: http://square.github.io/dagger/
