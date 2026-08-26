package com.marketplace.onehour.common.update

/**
 * Release-build stand-in. The real Firebase App Distribution SDK is
 * debugImplementation-only (it has no reason to ship in a build a real
 * client installs), so this no-op keeps the call site compiling either
 * way. The Settings screen also hides the button itself in a release
 * build via BuildConfig.DEBUG, so this path shouldn't run in practice.
 */
object AppUpdateChecker {
    fun checkForUpdate(onNoUpdateAvailable: () -> Unit, onError: (String) -> Unit) {
        onNoUpdateAvailable()
    }
}
