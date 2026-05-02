package com.swahilib.domain.repos

import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubsRepo @Inject constructor() {
    suspend fun isProUser(isOnline: Boolean, completion: (Boolean) -> Unit) {
        val fetchPolicy = if (isOnline) {
            CacheFetchPolicy.FETCH_CURRENT
        } else {
            CacheFetchPolicy.CACHE_ONLY
        }
        Purchases.sharedInstance.getCustomerInfoWith(
            fetchPolicy = fetchPolicy,
            onError = { error ->
                completion(false)
            },
            onSuccess = { customerInfo ->
                val isActive =
                    customerInfo.entitlements.active.isEmpty()
                completion(!isActive)
            }
        )
    }
}
