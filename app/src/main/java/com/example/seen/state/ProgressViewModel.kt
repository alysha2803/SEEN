package com.example.seen.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.seen.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val store = ProgressStore(application)

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    private val _monologueQueue = MutableStateFlow<List<String>>(emptyList())
    val monologueQueue: StateFlow<List<String>> = _monologueQueue.asStateFlow()

    init {
        // collect() on DataStore always emits the current persisted value first,
        // then subsequent updates — no need for a separate first() call.
        viewModelScope.launch {
            store.progressFlow.collect { saved -> _state.value = saved }
        }
    }

    fun acceptContentWarning() = update { it.copy(acceptedContentWarning = true) }

    fun markVisited(app: AppId) {
        if (app in _state.value.visitedApps) return
        update { it.copy(visitedApps = it.visitedApps + app) }
        enqueueBeat(Trigger.OnFirstOpen(app))
    }

    fun completeMiniGame(mg: MiniGame) {
        val current = _state.value
        if (mg.gateState(current.highestCompletedOrder) != GateState.UNLOCKED) return
        val newOrder = mg.order
        update { it.copy(
            highestCompletedOrder = newOrder,
            finished = newOrder == MiniGame.entries.maxOf { m -> m.order }
        ) }
        enqueueBeat(Trigger.OnMiniGameComplete(mg))
    }

    fun finish() = update { it.copy(finished = true) }

    fun reset() {
        viewModelScope.launch {
            store.reset()
            _state.value = ProgressState()
            _monologueQueue.value = emptyList()
        }
    }

    fun advanceMonologue() {
        val q = _monologueQueue.value
        if (q.isNotEmpty()) _monologueQueue.value = q.drop(1)
    }

    private fun enqueueBeat(trigger: Trigger) {
        val current = _state.value
        val unfired = ContentRepository.beatsFor(trigger).filter { it.id !in current.firedBeatIds }
        if (unfired.isEmpty()) return
        val newIds = unfired.map { it.id }.toSet()
        update { it.copy(firedBeatIds = it.firedBeatIds + newIds) }
        _monologueQueue.value = _monologueQueue.value + unfired.flatMap { it.lines }
    }

    private fun update(transform: (ProgressState) -> ProgressState) {
        val new = transform(_state.value)
        _state.value = new
        viewModelScope.launch { store.save(new) }
    }
}
