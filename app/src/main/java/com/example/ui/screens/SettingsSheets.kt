package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.BladeStrings
import com.example.data.local.UserSettingsPrefs
import com.example.data.model.UserProfile
import com.example.ui.theme.BladeCrimson
import com.example.ui.theme.BladeEmerald
import com.example.ui.theme.TextWhite
import java.io.File
import java.util.Locale

enum class SettingsSheetSection {
    MANAGE_ACCOUNT,
    PRIVACY,
    CONTENT_PREFERENCES,
    NOTIFICATIONS,
    ACTIVITY_CENTRE,
    ADS,
    APP_LANGUAGE,
    SCREEN_TIME,
    FAMILY_PAIRING,
    AUDIENCE_CONTROL,
    DISPLAY_ACCESSIBILITY,
    FREE_UP_SPACE,
    DATA_SAVER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailSheet(
    section: SettingsSheetSection,
    currentUser: UserProfile?,
    isDarkMode: Boolean,
    languageCode: String,
    userSettings: UserSettingsPrefs,
    cacheSizeBytes: Long,
    strings: BladeStrings,
    onDismiss: () -> Unit,
    onEditProfile: () -> Unit,
    onToggleAccountPrivacy: (Boolean) -> Unit,
    onUpdateSettings: ((UserSettingsPrefs) -> UserSettingsPrefs) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onClearWatchHistory: () -> Unit,
    onClearAppCache: () -> Unit,
    onSignOut: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (section) {
                SettingsSheetSection.MANAGE_ACCOUNT -> {
                    SheetHeader(
                        title = "Manage account",
                        subtitle = "Account details, profile visibility, and session controls"
                    )
                    if (currentUser != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser.displayName.ifBlank { currentUser.username },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (currentUser.isVerified) {
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            contentDescription = "Verified",
                                            tint = BladeEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "@${currentUser.username}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (currentUser.isPrivate) "Private account" else "Public account",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Button(
                            onClick = {
                                onDismiss()
                                onEditProfile()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("settings_edit_profile_button")
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(strings.editProfile)
                        }
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onSignOut()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("settings_manage_signout_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(strings.signOut)
                        }
                    }
                }

                SettingsSheetSection.PRIVACY -> {
                    SheetHeader(
                        title = "Privacy",
                        subtitle = "Control who can view your profile and interact with your content"
                    )
                    SettingSwitchRow(
                        title = "Private account",
                        subtitle = "Only approved followers can see your private videos",
                        checked = currentUser?.isPrivate == true,
                        testTag = "privacy_private_account_switch",
                        onCheckedChange = { enabled ->
                            onToggleAccountPrivacy(enabled)
                        }
                    )
                    SettingSwitchRow(
                        title = "Allow comments",
                        subtitle = "Allow viewers to leave comments on your videos",
                        checked = userSettings.allowComments,
                        testTag = "privacy_allow_comments_switch",
                        onCheckedChange = { enabled ->
                            onUpdateSettings { it.copy(allowComments = enabled) }
                        }
                    )
                    SettingSwitchRow(
                        title = "Suggest account to others",
                        subtitle = "Let friends and contacts discover your creator profile",
                        checked = userSettings.suggestAccountToOthers,
                        testTag = "privacy_suggest_account_switch",
                        onCheckedChange = { enabled ->
                            onUpdateSettings { it.copy(suggestAccountToOthers = enabled) }
                        }
                    )
                }

                SettingsSheetSection.CONTENT_PREFERENCES -> {
                    SheetHeader(
                        title = "Content preferences",
                        subtitle = "Customize what appears in your For You feed"
                    )
                    SettingSwitchRow(
                        title = "Restricted Mode",
                        subtitle = "Limit videos that may contain sensitive themes",
                        checked = userSettings.restrictedMode,
                        testTag = "content_restricted_mode_switch",
                        onCheckedChange = { enabled ->
                            onUpdateSettings { it.copy(restrictedMode = enabled) }
                        }
                    )
                    OutlinedButton(
                        onClick = {
                            onClearWatchHistory()
                            onShowMessage("For You feed preferences refreshed")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("content_refresh_feed_button")
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Refresh your For You feed")
                    }
                }

                SettingsSheetSection.NOTIFICATIONS -> {
                    SheetHeader(
                        title = "Notifications",
                        subtitle = "Choose which activity updates you receive"
                    )
                    SettingSwitchRow(
                        title = "Likes",
                        subtitle = "Notify when someone likes your video",
                        checked = userSettings.notifyLikes,
                        testTag = "notify_likes_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(notifyLikes = v) } }
                    )
                    SettingSwitchRow(
                        title = "Comments",
                        subtitle = "Notify when someone comments on your video",
                        checked = userSettings.notifyComments,
                        testTag = "notify_comments_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(notifyComments = v) } }
                    )
                    SettingSwitchRow(
                        title = "New followers",
                        subtitle = "Notify when someone starts following you",
                        checked = userSettings.notifyFollowers,
                        testTag = "notify_followers_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(notifyFollowers = v) } }
                    )
                    SettingSwitchRow(
                        title = "Direct messages",
                        subtitle = "Notify when you receive a new chat message",
                        checked = userSettings.notifyMessages,
                        testTag = "notify_messages_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(notifyMessages = v) } }
                    )
                }

                SettingsSheetSection.ACTIVITY_CENTRE -> {
                    SheetHeader(
                        title = "Activity centre",
                        subtitle = "Review or clear your local watch and feed history"
                    )
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Watch history personalizes your For You feed order. Clearing it resets feed recommendations on this device.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = {
                            onClearWatchHistory()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("activity_clear_history_button")
                    ) {
                        Text("Clear watch & feed history")
                    }
                }

                SettingsSheetSection.ADS -> {
                    SheetHeader(
                        title = "Ads",
                        subtitle = "Manage how sponsored videos are selected"
                    )
                    SettingSwitchRow(
                        title = "Personalized ads",
                        subtitle = "Show relevant sponsored blades based on your interests",
                        checked = userSettings.personalizedAds,
                        testTag = "ads_personalized_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(personalizedAds = v) } }
                    )
                }

                SettingsSheetSection.APP_LANGUAGE -> {
                    SheetHeader(
                        title = "App language",
                        subtitle = "Select your preferred display language"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = languageCode == "en",
                            onClick = {
                                onSelectLanguage("en")
                                onShowMessage("Language changed to English (UK)")
                            },
                            label = { Text("English (UK)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BladeCrimson,
                                selectedLabelColor = TextWhite
                            ),
                            modifier = Modifier.testTag("lang_en_chip")
                        )
                        FilterChip(
                            selected = languageCode == "bn",
                            onClick = {
                                onSelectLanguage("bn")
                                onShowMessage("ভাষা বাংলা করা হয়েছে")
                            },
                            label = { Text("বাংলা (Bengali)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BladeCrimson,
                                selectedLabelColor = TextWhite
                            ),
                            modifier = Modifier.testTag("lang_bn_chip")
                        )
                    }
                }

                SettingsSheetSection.SCREEN_TIME -> {
                    SheetHeader(
                        title = "Screen time",
                        subtitle = "Set daily watch limits and sleep reminders"
                    )
                    Text(
                        text = "Daily screen time limit",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val options = listOf(0, 30, 60, 90, 120)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        options.forEach { mins ->
                            FilterChip(
                                selected = userSettings.dailyScreenTimeMinutes == mins,
                                onClick = {
                                    onUpdateSettings { it.copy(dailyScreenTimeMinutes = mins) }
                                },
                                label = { Text(if (mins == 0) "Off" else "${mins}m") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BladeCrimson,
                                    selectedLabelColor = TextWhite
                                ),
                                modifier = Modifier.testTag("screen_time_${mins}_chip")
                            )
                        }
                    }
                    SettingSwitchRow(
                        title = "Sleep reminders",
                        subtitle = "Remind you to take a break late at night",
                        checked = userSettings.sleepRemindersEnabled,
                        testTag = "screen_time_sleep_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(sleepRemindersEnabled = v) } }
                    )
                }

                SettingsSheetSection.FAMILY_PAIRING -> {
                    SheetHeader(
                        title = "Family Pairing",
                        subtitle = "Manage safety guardrails for younger viewers"
                    )
                    SettingSwitchRow(
                        title = "Safe viewing guardrails",
                        subtitle = "Apply stricter filtering on search and feeds",
                        checked = userSettings.familyRestrictedMode,
                        testTag = "family_restricted_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(familyRestrictedMode = v) } }
                    )
                    SettingSwitchRow(
                        title = "Restrict direct messages",
                        subtitle = "Limit incoming messages to mutual friends only",
                        checked = userSettings.familyRestrictDms,
                        testTag = "family_restrict_dms_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(familyRestrictDms = v) } }
                    )
                }

                SettingsSheetSection.AUDIENCE_CONTROL -> {
                    SheetHeader(
                        title = "Audience control",
                        subtitle = "Default audience and sharing permissions for your videos"
                    )
                    Text(
                        text = "Default video visibility",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Public", "Friends", "Private").forEach { option ->
                            FilterChip(
                                selected = userSettings.defaultPostPrivacy.equals(option, ignoreCase = true),
                                onClick = {
                                    onUpdateSettings { it.copy(defaultPostPrivacy = option) }
                                },
                                label = { Text(option) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BladeCrimson,
                                    selectedLabelColor = TextWhite
                                ),
                                modifier = Modifier.testTag("audience_privacy_${option.lowercase()}_chip")
                            )
                        }
                    }
                    SettingSwitchRow(
                        title = "Allow downloads",
                        subtitle = "Allow viewers to save or download your public videos",
                        checked = userSettings.allowDownloads,
                        testTag = "audience_downloads_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(allowDownloads = v) } }
                    )
                    SettingSwitchRow(
                        title = "Allow reactions & duets",
                        subtitle = "Allow creators to respond to your videos",
                        checked = userSettings.allowReactions,
                        testTag = "audience_reactions_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(allowReactions = v) } }
                    )
                }

                SettingsSheetSection.DISPLAY_ACCESSIBILITY -> {
                    SheetHeader(
                        title = "Display & accessibility",
                        subtitle = "Theme appearance, captions, and motion preferences"
                    )
                    SettingSwitchRow(
                        title = strings.darkMode,
                        subtitle = "Use dark obsidian theme across the app",
                        checked = isDarkMode,
                        testTag = "settings_dark_mode_switch",
                        onCheckedChange = onToggleDarkMode
                    )
                    SettingSwitchRow(
                        title = "Auto-generated captions",
                        subtitle = "Show captions automatically when available",
                        checked = userSettings.autoCaptions,
                        testTag = "display_auto_captions_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(autoCaptions = v) } }
                    )
                    SettingSwitchRow(
                        title = "Reduce motion",
                        subtitle = "Use gentler animations across feeds and transitions",
                        checked = userSettings.reduceMotion,
                        testTag = "display_reduce_motion_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(reduceMotion = v) } }
                    )
                }

                SettingsSheetSection.FREE_UP_SPACE -> {
                    SheetHeader(
                        title = "Free up space",
                        subtitle = "Clear temporary video exports and image cache"
                    )
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Cached media & temp files", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = formatCacheSizeMb(cacheSizeBytes),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Button(
                        onClick = onClearAppCache,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_clear_cache_button")
                    ) {
                        Icon(Icons.Filled.CleaningServices, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Clear cache")
                    }
                }

                SettingsSheetSection.DATA_SAVER -> {
                    SheetHeader(
                        title = "Data Saver",
                        subtitle = "Reduce cellular data consumption while watching videos"
                    )
                    SettingSwitchRow(
                        title = "Enable Data Saver",
                        subtitle = "Limit background video preloading on mobile networks",
                        checked = userSettings.dataSaverEnabled,
                        testTag = "data_saver_switch",
                        onCheckedChange = { v -> onUpdateSettings { it.copy(dataSaverEnabled = v) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}

fun calculateCacheSizeBytes(context: Context): Long {
    fun dirSize(file: File?): Long {
        if (file == null || !file.exists()) return 0L
        if (file.isFile) return file.length()
        return file.listFiles()?.sumOf { dirSize(it) } ?: 0L
    }
    return dirSize(context.cacheDir) + dirSize(context.externalCacheDir)
}

fun clearAppCacheFiles(context: Context): Long {
    fun deleteChildren(dir: File?) {
        if (dir == null || !dir.exists() || !dir.isDirectory) return
        dir.listFiles()?.forEach { child ->
            runCatching { child.deleteRecursively() }
        }
    }
    deleteChildren(context.cacheDir)
    deleteChildren(context.externalCacheDir)
    return calculateCacheSizeBytes(context)
}

fun formatCacheSizeMb(bytes: Long): String {
    val mb = bytes.toDouble() / (1024.0 * 1024.0)
    return String.format(Locale.US, "%.1f MB", mb.coerceAtLeast(0.0))
}
