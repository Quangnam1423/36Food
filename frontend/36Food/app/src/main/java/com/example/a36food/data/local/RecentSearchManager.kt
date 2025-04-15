package com.example.a36food.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object RecentSearchManager {
    private val Context.dataStore by preferencesDataStore(name = "recent_searches")
    private val KEY_SEARCHES = stringSetPreferencesKey("search_history")

    suspend fun addSearch(context: Context, keyword: String) {
        context.dataStore.edit {
            prefs ->
            val current = prefs[KEY_SEARCHES]?.toMutableSet() ?: mutableSetOf()
            current.add(keyword)
            prefs[KEY_SEARCHES] = current.toList().takeLast(10).toSet()
        }
    }

    suspend fun clearSearches(context: Context) {
        context.dataStore.edit { it.remove(KEY_SEARCHES)}
    }

    suspend fun getSearches(context: Context) : List<String> {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_SEARCHES]?.toList() ?: emptyList()
    }
}