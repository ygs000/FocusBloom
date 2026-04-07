package com.focusbloom.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var pomodoroDuration by remember { mutableIntStateOf(25) }
    var breakDuration by remember { mutableIntStateOf(5) }
    var longBreakDuration by remember { mutableIntStateOf(15) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        // Timer Settings
        SettingsSection(title = "计时器设置") {
            SettingsSliderItem(
                icon = Icons.Default.Timer,
                title = "番茄时长",
                subtitle = "$pomodoroDuration 分钟",
                value = pomodoroDuration.toFloat(),
                onValueChange = { pomodoroDuration = it.toInt() },
                valueRange = 5f..60f,
                steps = 10
            )
            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
            SettingsSliderItem(
                icon = Icons.Default.Coffee,
                title = "短休息时长",
                subtitle = "$breakDuration 分钟",
                value = breakDuration.toFloat(),
                onValueChange = { breakDuration = it.toInt() },
                valueRange = 1f..30f,
                steps = 5
            )
            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
            SettingsSliderItem(
                icon = Icons.Default.Weekend,
                title = "长休息时长",
                subtitle = "$longBreakDuration 分钟",
                value = longBreakDuration.toFloat(),
                onValueChange = { longBreakDuration = it.toInt() },
                valueRange = 10f..60f,
                steps = 9
            )
        }

        // Notifications
        SettingsSection(title = "通知") {
            SettingsSwitchItem(
                icon = Icons.Default.VolumeUp,
                title = "提示音",
                subtitle = "计时完成时播放声音",
                checked = soundEnabled,
                onCheckedChange = { soundEnabled = it }
            )
            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
            SettingsSwitchItem(
                icon = Icons.Default.Vibration,
                title = "振动",
                subtitle = "计时完成时振动提醒",
                checked = vibrationEnabled,
                onCheckedChange = { vibrationEnabled = it }
            )
        }

        // Appearance
        SettingsSection(title = "外观") {
            SettingsSwitchItem(
                icon = Icons.Default.DarkMode,
                title = "深色模式",
                subtitle = "使用深色主题",
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
        }

        // About
        SettingsSection(title = "关于") {
            SettingsInfoItem(
                icon = Icons.Default.Info,
                title = "版本",
                subtitle = "1.0.0"
            )
            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
            SettingsInfoItem(
                icon = Icons.Default.Code,
                title = "关于 FocusBloom",
                subtitle = "100% 免费、无广告、无内购的番茄钟应用"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(content = content)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsSliderItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.padding(start = 40.dp)
        )
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
