package com.example.a36food.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object RecentSearchManager {
    private val Context.dataStore by preferencesDataStore(name = "recent_searches")
    private val RECENT_SEARCHES_KEY = stringSetPreferencesKey("search_history")

    suspend fun saveSearch(context: Context, keyword: String) {
        context.dataStore.edit { preferences ->
            val currentSearches = preferences[RECENT_SEARCHES_KEY]?.toMutableSet() ?: mutableSetOf()

            // Add new search term at beginning (removing it first if it already exists)
            currentSearches.remove(keyword) // Remove if exists to avoid duplicates
            currentSearches.add(keyword)    // Add to set

            // Limit to most recent 10 searches
            if (currentSearches.size > 10) {
                val trimmedSearches = currentSearches.toList().takeLast(10).toSet()
                preferences[RECENT_SEARCHES_KEY] = trimmedSearches
            } else {
                preferences[RECENT_SEARCHES_KEY] = currentSearches
            }
        }
    }

    suspend fun getRecentSearches(context: Context): List<String> {
        return context.dataStore.data.map { preferences ->
            preferences[RECENT_SEARCHES_KEY]?.toList() ?: emptyList()
        }.first()
    }

    suspend fun clearSearches(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(RECENT_SEARCHES_KEY)
        }
    }
}