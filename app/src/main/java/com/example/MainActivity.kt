package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.PhysiologyScreen
import com.example.ui.screens.WeightTrackerScreen
import com.example.ui.theme.FastPulseTheme
import com.example.ui.theme.GoldAutophagy
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.FastingViewModel

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    TIMER("Timer", Icons.Filled.HourglassTop, Icons.Outlined.HourglassTop, "nav_tab_timer"),
    WEIGHT("Weight", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "nav_tab_weight"),
    PHYSIOLOGY("Physiology", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_tab_physiology"),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History, "nav_tab_history")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FastPulseTheme {
                val viewModel: FastingViewModel = viewModel()
                var currentTab by remember { mutableStateOf(AppTab.TIMER) }

                // Handle back button to return to Timer tab if on another screen
                BackHandler(enabled = currentTab != AppTab.TIMER) {
                    currentTab = AppTab.TIMER
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = SlateBackground,
                    bottomBar = {
                        NavigationBar(
                            containerColor = SlateSurface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            AppTab.entries.forEach { tab ->
                                val selected = currentTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SlateDark,
                                        selectedTextColor = TealGlow,
                                        indicatorColor = TealPrimary,
                                        unselectedIconColor = SlateTextSecondary,
                                        unselectedTextColor = SlateTextSecondary
                                    ),
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            AppTab.TIMER -> DashboardScreen(viewModel = viewModel)
                            AppTab.WEIGHT -> WeightTrackerScreen(viewModel = viewModel)
                            AppTab.PHYSIOLOGY -> PhysiologyScreen()
                            AppTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
