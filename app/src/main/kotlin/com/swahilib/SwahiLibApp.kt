package com.swahilib

import android.app.Application
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp
import io.sentry.ProfileLifecycle
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid

@HiltAndroidApp
class SwahiLibApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.BUILD_TYPE != "debug" || BuildConfig.BUILD_TYPE != "staging") {
            Purchases.configure(
                PurchasesConfiguration.Builder(this, BuildConfig.RcApiKey)
                    .build(),
            )
        }

        SentryAndroid.init(this) { options ->
            options.dsn = BuildConfig.SentryDsn
            options.isSendDefaultPii = true
            options.isEnableUserInteractionTracing = true
            options.isAttachScreenshot = true
            options.isAttachViewHierarchy = true
            options.tracesSampleRate = 1.0
            options.profileSessionSampleRate = 1.0
            options.profileLifecycle = ProfileLifecycle.TRACE
            options.isStartProfilerOnAppStart = true
            options.sessionReplay.sessionSampleRate = 0.1
            options.sessionReplay.onErrorSampleRate = 1.0
            options.logs.isEnabled = true;
            options.isTombstoneEnabled = true;
            options.beforeSend =
                SentryOptions.BeforeSendCallback { event, hint ->
                    if (SentryLevel.DEBUG == event.level) {
                        null
                    } else {
                        event
                    }
                }
        }
    }
}