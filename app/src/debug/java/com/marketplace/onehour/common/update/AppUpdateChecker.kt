package com.marketplace.onehour.common.update

import com.google.firebase.appdistribution.FirebaseAppDistribution

/**
 * Debug-build implementation: checks Firebase App Distribution for a newer
 * release than what's installed, and if one exists, shows Firebase's own
 * confirmation/download/install-prompt dialogs. The first call per device
 * also handles tester sign-in (a one-time browser hop to pick a Google
 * account) — that's Firebase's flow, not something this wraps further.
 *
 * A release build (see the sibling src/release implementation) never
 * reaches this class — Gradle picks one or the other per build type, so
 * this stays out of anything a real client would install.
 */
object AppUpdateChecker {
    fun checkForUpdate(onNoUpdateAvailable: () -> Unit, onError: (String) -> Unit) {
        FirebaseAppDistribution.getInstance()
            .updateIfNewReleaseAvailable()
            .addOnSuccessListener {
                onNoUpdateAvailable()
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Couldn't check for updates.")
            }
    }
}
