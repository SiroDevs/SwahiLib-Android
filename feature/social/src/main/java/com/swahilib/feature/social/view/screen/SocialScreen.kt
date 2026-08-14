package com.swahilib.feature.social.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.social.models.FriendChallenge
import com.swahilib.core.social.models.FriendshipStatus
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.social.view.components.AddFriendDialog
import com.swahilib.feature.social.view.components.CreateChallengeDialog
import com.swahilib.feature.social.view.components.EnterScoreDialog
import com.swahilib.feature.social.view.components.ProfileHeader
import com.swahilib.feature.social.view.screen.tabs.achievementFeedTab
import com.swahilib.feature.social.view.screen.tabs.challengesTab
import com.swahilib.feature.social.view.screen.tabs.friendsTab
import com.swahilib.feature.social.view.screen.tabs.leaderboardTab
import com.swahilib.feature.social.viewmodel.SocialViewModel

private enum class SocialTab(val label: String) {
    LEADERBOARD("Ubao wa Vinara"),
    FRIENDS("Marafiki"),
    CHALLENGES("Michezo"),
    ACHIEVEMENTS("Mafanikio"),
}

@Composable
fun SocialScreen(
    navController: NavHostController,
    viewModel: SocialViewModel = hiltViewModel(),
) {
    val isSignedIn by viewModel.isSignedIn.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val leaderboardScope by viewModel.leaderboardScope.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val achievementFeed by viewModel.achievementFeed.collectAsState()

    val clipboard = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(SocialTab.LEADERBOARD) }
    var showAddFriend by remember { mutableStateOf(false) }
    var addFriendError by remember { mutableStateOf<String?>(null) }
    var addFriendSubmitting by remember { mutableStateOf(false) }
    var showCreateChallenge by remember { mutableStateOf(false) }
    var createChallengeError by remember { mutableStateOf<String?>(null) }
    var createChallengeSubmitting by remember { mutableStateOf(false) }
    var scoreDialogChallenge by remember { mutableStateOf<FriendChallenge?>(null) }

    LaunchedEffect(isSignedIn) {
        if (!isSignedIn) {
            navController.navigate(Routes.AUTH_SIGN_IN) {
                popUpTo(Routes.SOCIAL) { inclusive = true }
            }
        }
    }

    if (showAddFriend) {
        AddFriendDialog(
            onDismiss = { showAddFriend = false; addFriendError = null },
            isSubmitting = addFriendSubmitting,
            error = addFriendError,
            onSubmit = { code ->
                addFriendSubmitting = true
                viewModel.sendFriendRequest(code) { success, error ->
                    addFriendSubmitting = false
                    if (success) {
                        showAddFriend = false
                        addFriendError = null
                    } else {
                        addFriendError = error
                    }
                }
            },
        )
    }

    if (showCreateChallenge) {
        CreateChallengeDialog(
            friends = friends.filter { it.status == FriendshipStatus.ACCEPTED },
            isSubmitting = createChallengeSubmitting,
            error = createChallengeError,
            onDismiss = { showCreateChallenge = false; createChallengeError = null },
            onSubmit = { opponentId, activityType, difficulty ->
                createChallengeSubmitting = true
                viewModel.createChallenge(opponentId, activityType, difficulty) { success, error ->
                    createChallengeSubmitting = false
                    if (success) {
                        showCreateChallenge = false
                        createChallengeError = null
                    } else {
                        createChallengeError = error
                    }
                }
            },
        )
    }

    scoreDialogChallenge?.let { challenge ->
        EnterScoreDialog(
            onDismiss = { scoreDialogChallenge = null },
            onSubmit = { score ->
                viewModel.submitScore(challenge.id, score, challenge.isMine)
                scoreDialogChallenge = null
            },
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Jamii ya SwahiLib",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { viewModel.signOut() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Jiondoe")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            profile?.let { ProfileHeader(it.displayName, it.level, it.totalXp, it.currentStreak) }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(SocialTab.entries) { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label) },
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    SocialTab.LEADERBOARD -> leaderboardTab(
                        scope = leaderboardScope,
                        entries = leaderboard,
                        onScopeChange = { viewModel.setLeaderboardScope(it) },
                    )

                    SocialTab.FRIENDS -> friendsTab(
                        profile = profile,
                        friends = friends,
                        clipboard = clipboard,
                        onAddFriend = { showAddFriend = true },
                        onAccept = { viewModel.respondToRequest(it, accept = true) },
                        onDecline = { viewModel.respondToRequest(it, accept = false) },
                        onRemove = { viewModel.removeFriend(it) },
                    )

                    SocialTab.CHALLENGES -> challengesTab(
                        challenges = challenges,
                        onCreateChallenge = { showCreateChallenge = true },
                        onPlay = { route -> navController.navigate(route) },
                        onEnterScore = { scoreDialogChallenge = it },
                        onDecline = { viewModel.declineChallenge(it) },
                    )

                    SocialTab.ACHIEVEMENTS -> achievementFeedTab(achievementFeed)
                }
            }
        }
    }
}
