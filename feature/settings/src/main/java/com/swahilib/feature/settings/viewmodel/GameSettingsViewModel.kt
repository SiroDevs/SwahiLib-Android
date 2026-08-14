package com.swahilib.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.swahilib.core.ui.components.game.GameSoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameSettingsViewModel @Inject constructor(
    val soundPlayer: GameSoundPlayer,
) : ViewModel()
