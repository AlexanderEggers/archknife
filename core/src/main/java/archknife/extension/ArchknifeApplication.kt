package archknife.extension

/**
 * Abstract class which takes care of the initialising of the Dagger related components. It also
 * starts the AppInjector which is injecting any dependencies inside project classes.
 *
 * @since 1.0.0
 */
abstract class ArchknifeApplication : ArchknifeApplicationGen<AppInjector>()