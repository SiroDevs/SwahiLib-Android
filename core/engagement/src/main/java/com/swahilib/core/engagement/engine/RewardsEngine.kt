package com.swahilib.core.engagement.engine

import com.swahilib.core.engagement.model.AwardResult
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardsEngine @Inject constructor(
    private val store: ProgressStore,
    private val xpEngine: XpEngine,
) {

    suspend fun grantCoins(amount: Int): Long {
        val progress = store.loadOrInitProgress()
        val next = (progress.coins + amount).coerceAtLeast(0)
        store.writeProgress(progress.copy(coins = next))
        return next
    }

    suspend fun spendCoins(amount: Int): Boolean {
        val progress = store.loadOrInitProgress()
        if (progress.coins < amount) return false
        store.writeProgress(progress.copy(coins = progress.coins - amount))
        return true
    }

    suspend fun grantDailyLogin(streakDay: Int, alreadyClaimedToday: Boolean): AwardResult? {
        if (alreadyClaimedToday) return null

        val coinReward = RewardRules.dailyLoginCoins(streakDay)
        val baseXp = RewardRules.dailyLoginXp(streakDay)
        val milestoneXp = RewardRules.streakMilestoneXp(streakDay)

        if (coinReward > 0) grantCoins(coinReward)

        val loginAward = xpEngine.award(
            XpAward(
                source = XpSource.DAILY_LOGIN,
                amount = baseXp,
            )
        )
        if (milestoneXp > 0) {
            xpEngine.award(
                XpAward(
                    source = XpSource.STREAK_BONUS,
                    amount = milestoneXp,
                    referenceId = "streak_$streakDay",
                )
            )
        }
        return loginAward.copy(coinsAwarded = coinReward)
    }
}
