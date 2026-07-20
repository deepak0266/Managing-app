package com.personal.lifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.personal.lifeos.expenses.ui.ExpensesScreen
import com.personal.lifeos.reminders.ui.RemindersScreen
import com.personal.lifeos.memory.ui.MemoryScreen
import com.personal.lifeos.habits.ui.HabitsScreen
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.personal.lifeos.sync.SettingsScreen
import com.personal.lifeos.ui.theme.LifeOSTheme
import com.personal.lifeos.ui.theme.ThemeMode
import com.personal.lifeos.ui.theme.AccentColor
import com.personal.lifeos.ui.components.PremiumBottomNav
import com.personal.lifeos.ui.components.FloatingAIButton
import com.personal.lifeos.ui.animation.premiumEnterTransition
import com.personal.lifeos.ui.animation.premiumExitTransition
import com.personal.lifeos.ui.animation.premiumPopEnterTransition
import com.personal.lifeos.ui.animation.premiumPopExitTransition
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            // ── Theme State (hoisted above theme for persistence) ──
            var themeMode by remember { mutableStateOf(ThemeMode.Dark) }
            var accentColor by remember { mutableStateOf(AccentColor.Purple) }

            LifeOSTheme(themeMode = themeMode, accentColor = accentColor) {
                var isAuthenticated by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    authenticate(
                        onSuccess = { isAuthenticated = true },
                        onError = { /* Handle error, maybe close app */ }
                    )
                }

                if (isAuthenticated) {
                    LifeOSApp(
                        themeMode = themeMode,
                        accentColor = accentColor,
                        onThemeModeChange = { themeMode = it },
                        onAccentColorChange = { accentColor = it }
                    )
                } else {
                    // ── Premium Lock Screen ──
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val infiniteTransition = rememberInfiniteTransition(label = "lock")
                            val lockAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1500),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "lock_alpha"
                            )

                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Locked",
                                modifier = Modifier
                                    .size(48.dp)
                                    .alpha(lockAlpha),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                "Authenticating...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    private fun authenticate(onSuccess: () -> Unit, onError: () -> Unit) {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            onSuccess()
                        }
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            onError()
                        }
                    })

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Unlock Life OS")
                    .setSubtitle("Authenticate to access your personal data")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    .build()

                biometricPrompt.authenticate(promptInfo)
            }
            else -> {
                // Biometrics not available, skip for MVP
                onSuccess()
            }
        }
    }
}

@Composable
fun LifeOSApp(
    themeMode: ThemeMode,
    accentColor: AccentColor,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAccentColorChange: (AccentColor) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                PremiumBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "reminders",
                enterTransition = { premiumEnterTransition() },
                exitTransition = { premiumExitTransition() },
                popEnterTransition = { premiumPopEnterTransition() },
                popExitTransition = { premiumPopExitTransition() },
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("reminders") {
                    val viewModel: com.personal.lifeos.reminders.ui.RemindersViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    RemindersScreen(viewModel = viewModel)
                }
                composable("money") {
                    val viewModel: com.personal.lifeos.expenses.ui.ExpensesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    ExpensesScreen(viewModel = viewModel)
                }
                composable("memory") {
                    val viewModel: com.personal.lifeos.memory.ui.MemoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    MemoryScreen(viewModel = viewModel)
                }
                composable("habits") {
                    val viewModel: com.personal.lifeos.habits.ui.HabitsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    HabitsScreen(viewModel = viewModel)
                }
                composable("settings") {
                    SettingsScreen(
                        themeMode = themeMode,
                        accentColor = accentColor,
                        onThemeModeChange = onThemeModeChange,
                        onAccentColorChange = onAccentColorChange
                    )
                }
            }
        }

        // ── Floating AI Button (overlayed) ──
        FloatingAIButton(
            onVoiceClick = {
                navController.navigate("reminders") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            onTextClick = {
                navController.navigate("reminders") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            onCameraClick = {
                navController.navigate("money") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            onQuickExpenseClick = {
                navController.navigate("money") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 96.dp)
        )
    }
}
