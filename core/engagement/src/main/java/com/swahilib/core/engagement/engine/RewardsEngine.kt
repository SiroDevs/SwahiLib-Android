package com.swahilib.core.engagement.engine

import com.swahilib.core.engagement.model.AwardResult
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Grants coins and multi-source login rewards. XP awards flow through
 * [XpEngine] so the ledger stays complete.
 *
 * `grantDailyLogin` is idempotent per calendar day - the caller (typically
 * MainViewModel on app resume) can call it every launch without worry.
 */
@Singleton
class RewardsEngine @Inject constructor(
    private val store: ProgressStore,
    private val xpEngine: XpEngine,
) {

    /** Add [amount] coins to the user's balance. Refuses to go negative. */
    suspend fun grantCoins(amount: Int): Long {
        val progress = store.loadOrInitProgress()
        val next = (progress.coins + amount).coerceAtLeast(0)
        store.writeProgress(progress.copy(coins = next))
        return next
    }

    /** Try to spend [amount] coins. Returns true iff there was enough. */
    suspend fun spendCoins(amount: Int): Boolean {
        val progress = store.loadOrInitProgress()
        if (progress.coins < amount) return false
        store.writeProgress(progress.copy(coins = progress.coins - amount))
        return true
    }

    /**
     * Grants the daily-login XP + coin bonus and (if the caller has already
     * ticked the streak forward for today) any streak-milestone bonus.
     *
     * Returns the underlying XP award result so callers can show a celebration.
     * Returns null if the reward has already been granted today.
     */
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
