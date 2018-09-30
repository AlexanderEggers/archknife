Changelog
==========

Version 0.7.0 *(2018-08-29)*
----------------------------
- **BREAKING**: Moved Injectable interface to annotation artifact.
- **NEW:** Support for the latest dagger version 2.17.
- **NEW:** Added default module for the application context.
- **MISC**: Changed the visibility of several methods to allow a custom implementation of those.
- **MISC:** Updated kotlin to version 1.2.61

Version 0.6.0 *(2018-06-13)*
----------------------------
- **NEW:** Added more source code documentation to the library.
- **NEW:** Refactored all processor related classes to allow a much cleaner code structure and error handling.
- **NEW:** Added new class "ArchknifeApplicationGen". This class allows you to inject your custom AppInjector into the default dagger initialisation. The existing "ArchknifeApplication" can be used as before without the need to change your code. The only difference, this class is extending the ArchknifeApplicationGen using the AppInjector default class as a generic type.
- **NEW:** Moved dagger and lifecycle compiler to the archknife processor to reduce the needed dependencies when using this library.
- **NEW:** Added new method 'getActivity' to the ContextProvider that will try to cast the current saved context to an Activity object.
- **NEW:** Added new method 'addListener' to the ContextProvider which requires the new class 'OnContextChangedListener'. This implementation allows you to attach listeners to the ContextProvider which will inform you if the context has been changed. That can be useful for debugging, or if your singleton objects content is depending a specific context instance (and should be cleaned if the context is changed).
- **BUGFIX:** Fixed possible NPE inside the AppInjector class.

Version 0.5.0 *(2018-05-31)*
----------------------------
- Tweaking processor error messages
- Added new artifact 'context' which includes the class ContextProvider. This class is replacing the ActivityContextProvider which was inside the 'core' artifact.

Version 0.4.0 *(2018-05-13)*
----------------------------
- Added the class ActivityContextProvider which can help you to retrieve the current activity context. Class is provided by the Dagger structure.
- Moved project from org.archknife.* to archknife.*
- Removed the artifacts archknife-viewmodel, archknife-viewmodel-annotation and archknife-extension to simplify the library usage.
- Renamed several generated classes to ensure there won't be any name clashes.
- Several processor improvements.

Version 0.3.2 *(2018-05-08)*
----------------------------
- Added 'open' attribute to some classes which prevented the user to extend those.
- Updated Kotlin to support the lastest version (1.2.41).

Version 0.3.1 *(2018-04-25)*
----------------------------
- Added new artifact "archknife-core" which bundles all library artifacts.
- Tweaking AppInjector: Activities should have the Injectable or HasSupportFragmentInjector in order to work for the Dagger injector.

Version 0.3.0 *(2018-04-14)*
----------------------------
- Fixed @ProvideModule processor which was throwing a ClassNotFoundException.
- Added javadoc to most classes.
- Added @MustBeDocumented and @Documented to all annotation to include those into the javadoc.

Version 0.2.1 *(2018-04-14)*
----------------------------
- Fixed dependency error when trying to use the artifact **archknife-viewmodel**
- Added new artifact **archknife-core**: This artifact bundles all library artifacts into just one dependency.
- Added new artifact **archknife-viewmodel-annotation**: The artifact includes the relevant annotation which is used for ViewModels that should be added to the Dagger structure.

Version 0.2.0 *(2018-04-13)*
----------------------------
- Added new artifact "archknife-viewmodel" which can be used to provide ViewModel to the Dagger structure.
- Added new annotations "ProvideApplication" and "ProvideModule" that can be used to define the AppComponent.
- Changed location for the generated files to use the App's application package
- Added the processor to create the Dagger Component >> the name is always "AppComponent". Therefore DaggerAppComponent needs to be used to initialise the component class.

Version 0.1.1 *(2018-04-09)*
----------------------------
- Fixed ClassNotFoundException inside ViewModelBuilderModule.

Version 0.1.0 *(2018-04-06)*
----------------------------
- Initial library release.
