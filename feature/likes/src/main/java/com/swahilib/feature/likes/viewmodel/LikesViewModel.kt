package com.swahilib.feature.likes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the "Vipendwa" (liked words/idioms/proverbs/sayings) screen. Independent from
 * [com.swahilib.feature.home.viewmodel.HomeViewModel] - each content repo is injected directly
 * here rather than shared through Home, since Likes now lives in its own module.
 */
@HiltViewModel
class LikesViewModel @Inject constructor(
    private val wordRepo: WordRepo,
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
) : ViewModel() {

    private val _likedWords = MutableStateFlow<List<WordEntity>>(emptyList())
    val likedWords: StateFlow<List<WordEntity>> = _likedWords.asStateFlow()

    private val _likedIdioms = MutableStateFlow<List<IdiomEntity>>(emptyList())
    val likedIdioms: StateFlow<List<IdiomEntity>> = _likedIdioms.asStateFlow()

    private val _likedProverbs = MutableStateFlow<List<ProverbEntity>>(emptyList())
    val likedProverbs: StateFlow<List<ProverbEntity>> = _likedProverbs.asStateFlow()

    private val _likedSayings = MutableStateFlow<List<SayingEntity>>(emptyList())
    val likedSayings: StateFlow<List<SayingEntity>> = _likedSayings.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _likedWords.value = wordRepo.fetchLocalData()
                .filter { it.liked }.sortedBy { it.title?.lowercase() }
            _likedIdioms.value = idiomRepo.fetchLocalData()
                .filter { it.liked }.sortedBy { it.title?.lowercase() }
            _likedProverbs.value = proverbRepo.fetchLocalData()
                .filter { it.liked }.sortedBy { it.title?.lowercase() }
            _likedSayings.value = sayingRepo.fetchLocalData()
                .filter { it.liked }.sortedBy { it.title?.lowercase() }
        }
    }

    fun likeWord(word: WordEntity) {
        viewModelScope.launch {
            val updated = word.copy(liked = !word.liked)
            wordRepo.updateWord(updated)
            _likedWords.value = if (updated.liked) {
                (_likedWords.value + updated).sortedBy { it.title?.lowercase() }
            } else {
                _likedWords.value.filterNot { it.rid == word.rid }
            }
        }
    }

    fun likeIdiom(idiom: IdiomEntity) {
        viewModelScope.launch {
            val updated = idiom.copy(liked = !idiom.liked)
            idiomRepo.updateIdiom(updated)
            _likedIdioms.value = if (updated.liked) {
                (_likedIdioms.value + updated).sortedBy { it.title?.lowercase() }
            } else {
                _likedIdioms.value.filterNot { it.rid == idiom.rid }
            }
        }
    }

    fun likeProverb(proverb: ProverbEntity) {
        viewModelScope.launch {
            val updated = proverb.copy(liked = !proverb.liked)
            proverbRepo.updateProverb(updated)
            _likedProverbs.value = if (updated.liked) {
                (_likedProverbs.value + updated).sortedBy { it.title?.lowercase() }
            } else {
                _likedProverbs.value.filterNot { it.rid == proverb.rid }
            }
        }
    }

    fun likeSaying(saying: SayingEntity) {
        viewModelScope.launch {
            val updated = saying.copy(liked = !saying.liked)
            sayingRepo.updateSaying(updated)
            _likedSayings.value = if (updated.liked) {
                (_likedSayings.value + updated).sortedBy { it.title?.lowercase() }
            } else {
                _likedSayings.value.filterNot { it.rid == saying.rid }
            }
        }
    }

    fun clearAllLikes() {
        viewModelScope.launch {
            wordRepo.clearAllLikes()
            idiomRepo.clearAllLikes()
            proverbRepo.clearAllLikes()
            sayingRepo.clearAllLikes()
            _likedWords.value = emptyList()
            _likedIdioms.value = emptyList()
            _likedProverbs.value = emptyList()
            _likedSayings.value = emptyList()
        }
    }
}
