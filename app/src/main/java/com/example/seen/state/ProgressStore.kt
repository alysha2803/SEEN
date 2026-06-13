package com.example.seen.state

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.seen.data.AppId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "seen_progress")

class ProgressStore(private val context: Context) {

    private object Keys {
        val ACCEPTED_WARNING = booleanPreferencesKey("accepted_warning")
        val HIGHEST_COMPLETED = intPreferencesKey("highest_completed")
        val FIRED_BEAT_IDS = stringSetPreferencesKey("fired_beat_ids")
        val VISITED_APPS = stringSetPreferencesKey("visited_apps")
        val FINISHED = booleanPreferencesKey("finished")
    }

    val progressFlow: Flow<ProgressState> = context.dataStore.data.map { prefs ->
        ProgressState(
            acceptedContentWarning = prefs[Keys.ACCEPTED_WARNING] ?: false,
            highestCompletedOrder = prefs[Keys.HIGHEST_COMPLETED] ?: 0,
            firedBeatIds = prefs[Keys.FIRED_BEAT_IDS] ?: emptySet(),
            visitedApps = (prefs[Keys.VISITED_APPS] ?: emptySet())
                .mapNotNull { runCatching { AppId.valueOf(it) }.getOrNull() }
                .toSet(),
            finished = prefs[Keys.FINISHED] ?: false
        )
    }

    suspend fun save(state: ProgressState) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCEPTED_WARNING] = state.acceptedContentWarning
            prefs[Keys.HIGHEST_COMPLETED] = state.highestCompletedOrder
            prefs[Keys.FIRED_BEAT_IDS] = state.firedBeatIds
            prefs[Keys.VISITED_APPS] = state.visitedApps.map { it.name }.toSet()
            prefs[Keys.FINISHED] = state.finished
        }
    }

    suspend fun reset() {
        context.dataStore.edit { it.clear() }
    }
}
