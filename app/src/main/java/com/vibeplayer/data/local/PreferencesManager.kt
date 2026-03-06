package com.vibeplayer.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.vibeplayer.data.model.AppSettings
import com.vibeplayer.data.model.SortBy
import com.vibeplayer.data.model.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("vibe_player_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val DEFAULT_TAB = intPreferencesKey("default_tab")
        val SORT_BY = stringPreferencesKey("sort_by")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val REPEAT_MODE = intPreferencesKey("repeat_mode")
        val SHUFFLE_ENABLED = booleanPreferencesKey("shuffle_enabled")
        val VOLUME = floatPreferencesKey("volume")
    }

    val settings: Flow<AppSettings> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs ->
            AppSettings(
                darkTheme = prefs[Keys.DARK_THEME] ?: true,
                dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
                defaultTab = prefs[Keys.DEFAULT_TAB] ?: 0,
                sortBy = SortBy.valueOf(prefs[Keys.SORT_BY] ?: SortBy.TITLE.name),
                sortOrder = SortOrder.valueOf(prefs[Keys.SORT_ORDER] ?: SortOrder.ASCENDING.name)
            )
        }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setDefaultTab(tab: Int) {
        context.dataStore.edit { it[Keys.DEFAULT_TAB] = tab }
    }

    suspend fun setSortBy(sortBy: SortBy) {
        context.dataStore.edit { it[Keys.SORT_BY] = sortBy.name }
    }

    suspend fun setSortOrder(order: SortOrder) {
        context.dataStore.edit { it[Keys.SORT_ORDER] = order.name }
    }
}
