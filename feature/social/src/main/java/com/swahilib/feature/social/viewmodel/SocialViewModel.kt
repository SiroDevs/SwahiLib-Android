package com.swahilib.feature.social.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.social.dtos.LeaderboardEntry
import com.swahilib.core.social.models.AchievementFeedItem
import com.swahilib.core.social.models.Friend
import com.swahilib.core.social.models.FriendChallenge
import com.swahilib.core.social.models.SocialProfile
import com.swahilib.core.social.repos.SocialAuthRepo
import com.swahilib.core.social.repos.SocialRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LeaderboardScope { GLOBAL, FRIENDS }

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val authRepo: SocialAuthRepo,
    private val socialRepo: SocialRepo,
) : ViewModel() {

    val isSignedIn: StateFlow<Boolean> = authRepo.isSignedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _profile = MutableStateFlow<SocialProfile?>(null)
    val profile: StateFlow<SocialProfile?> = _profile.asStateFlow()

    private val _leaderboardScope = MutableStateFlow(LeaderboardScope.GLOBAL)
    val leaderboardScope: StateFlow<LeaderboardScope> = _leaderboardScope.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val _challenges = MutableStateFlow<List<FriendChallenge>>(emptyList())
    val challenges: StateFlow<List<FriendChallenge>> = _challenges.asStateFlow()

    private val _achievementFeed = MutableStateFlow<List<AchievementFeedItem>>(emptyList())
    val achievementFeed: StateFlow<List<AchievementFeedItem>> = _achievementFeed.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            authRepo.isSignedIn.collect { signedIn ->
                if (signedIn) refreshAll() else clearAll()
            }
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            _isLoading.value = true
            _profile.value = socialRepo.currentProfile()
            loadLeaderboard()
            refreshFriends()
            refreshChallenges()
            refreshAchievementFeed()
            _isLoading.value = false
        }
    }

    private fun clearAll() {
        _profile.value = null
        _leaderboard.value = emptyList()
        _friends.value = emptyList()
        _challenges.value = emptyList()
        _achievementFeed.value = emptyList()
    }

    fun setLeaderboardScope(scope: LeaderboardScope) {
        if (_leaderboardScope.value == scope) return
        _leaderboardScope.value = scope
        viewModelScope.launch { loadLeaderboard() }
    }

    private suspend fun loadLeaderboard() {
        _leaderboard.value = when (_leaderboardScope.value) {
            LeaderboardScope.GLOBAL -> socialRepo.globalLeaderboard()
            LeaderboardScope.FRIENDS -> socialRepo.friendsLeaderboard()
        }
    }

    fun refreshFriends() {
        viewModelScope.launch { _friends.value = socialRepo.friends() }
    }

    fun sendFriendRequest(friendCode: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            socialRepo.sendFriendRequest(friendCode).fold(
                onSuccess = {
                    refreshFriends()
                    onResult(true, null)
                },
                onFailure = { e -> onResult(false, e.message ?: "Imeshindwa kutuma ombi") },
            )
        }
    }

    fun respondToRequest(friendshipId: String, accept: Boolean) {
        viewModelScope.launch {
            socialRepo.respondToFriendRequest(friendshipId, accept)
            refreshFriends()
            if (accept) loadLeaderboard()
        }
    }

    fun removeFriend(friendshipId: String) {
        viewModelScope.launch {
            socialRepo.respondToFriendRequest(friendshipId, accept = false)
            refreshFriends()
        }
    }

    fun refreshChallenges() {
        viewModelScope.launch { _challenges.value = socialRepo.myFriendChallenges() }
    }

    fun createChallenge(opponentId: String, activityType: String, difficulty: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            socialRepo.createFriendChallenge(opponentId, activityType, difficulty).fold(
                onSuccess = {
                    refreshChallenges()
                    onResult(true, null)
                },
                onFailure = { e -> onResult(false, e.message ?: "Imeshindwa kuunda changamoto") },
            )
        }
    }

    fun submitScore(challengeId: String, score: Int, iAmChallenger: Boolean) {
        viewModelScope.launch {
            socialRepo.submitFriendChallengeScore(challengeId, score, iAmChallenger)
            refreshChallenges()
        }
    }

    fun declineChallenge(challengeId: String) {
        viewModelScope.launch {
            socialRepo.declineFriendChallenge(challengeId)
            refreshChallenges()
        }
    }

    fun refreshAchievementFeed() {
        viewModelScope.launch { _achievementFeed.value = socialRepo.friendsAchievementFeed() }
    }

    fun signOut() {
        viewModelScope.launch { authRepo.signOut() }
    }
}
