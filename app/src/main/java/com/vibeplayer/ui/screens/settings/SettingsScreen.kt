package com.vibeplayer.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    var darkTheme by remember { mutableStateOf(true) }
    var dynamicColor by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item { SettingsSectionHeader("Appearance") }
            item {
                SettingsSwitchItem(
                    title = "Dark Theme",
                    subtitle = "Use dark colors throughout the app",
                    icon = Icons.Rounded.DarkMode,
                    checked = darkTheme,
                    onCheckedChange = { darkTheme = it }
                )
            }
            item {
                SettingsSwitchItem(
                    title = "Dynamic Color",
                    subtitle = "Adapt colors from your wallpaper (Android 12+)",
                    icon = Icons.Rounded.Palette,
                    checked = dynamicColor,
                    onCheckedChange = { dynamicColor = it }
                )
            }
            item { SettingsSectionHeader("Library") }
            item {
                SettingsClickItem(
                    title = "Scan Library",
                    subtitle = "Refresh music from storage",
                    icon = Icons.Rounded.Refresh,
                    onClick = {}
                )
            }
            item { SettingsSectionHeader("About") }
            item {
                SettingsInfoItem(
                    title = "VibePlayer",
                    subtitle = "Version 1.0.0",
                    icon = Icons.Rounded.Info
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

@Composable
private fun SettingsClickItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        modifier = androidx.compose.foundation.clickable(onClick = onClick)
    )
}

@Composable
private fun SettingsInfoItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) }
    )
}
