package com.swahilib

import android.app.Application
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SwahiLibApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Purchases.configure(
            PurchasesConfiguration.Builder(this, BuildConfig.RcApiKey).build()
        )
    }
}
