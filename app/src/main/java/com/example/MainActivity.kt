package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.AppDatabase
import com.example.data.repository.AppRepository
import com.example.ui.components.BottomNavBar
import com.example.ui.navigation.AppScreen
import com.example.ui.navigation.BottomNavTab
import com.example.ui.screens.*
import com.example.ui.theme.TestAppTheme
import com.example.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(this)
        val repository = AppRepository(db)

        setContent {
            TestAppTheme {
                val authViewModel = remember { AuthViewModel(repository) }
                val mainViewModel = remember { MainViewModel(repository) }
                val adminViewModel = remember { AdminViewModel(repository) }

                TestAppRoot(
                    authViewModel = authViewModel,
                    mainViewModel = mainViewModel,
                    adminViewModel = adminViewModel
                )
            }
        }
    }
}

@Composable
fun TestAppRoot(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    adminViewModel: AdminViewModel
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val mainUiState by mainViewModel.uiState.collectAsState()
    val adminUiState by adminViewModel.uiState.collectAsState()

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.MainFlow) }
    var currentTab by remember { mutableStateOf(BottomNavTab.HOME) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mainUiState.snackbarMessage) {
        mainUiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            mainViewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentScreen == AppScreen.MainFlow) {
                BottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (currentScreen == AppScreen.MainFlow) innerPadding else androidx.compose.foundation.layout.PaddingValues())
        ) {
            when (val screen = currentScreen) {
                is AppScreen.MainFlow -> {
                    when (currentTab) {
                        BottomNavTab.HOME -> {
                            HomeScreen(
                                mainViewModel = mainViewModel,
                                mainUiState = mainUiState,
                                currentUser = authUiState.currentUser,
                                onNavigateToTests = { catId ->
                                    if (catId != null) mainViewModel.selectCategory(catId)
                                    currentTab = BottomNavTab.TESTS
                                },
                                onNavigateToPdfs = { catId ->
                                    if (catId != null) mainViewModel.selectCategory(catId)
                                    currentTab = BottomNavTab.PDF_STORE
                                },
                                onNavigateToSubscriptions = {
                                    currentScreen = AppScreen.SubscriptionPlans
                                },
                                onStartTest = { test ->
                                    val user = authUiState.currentUser
                                    if (user != null) {
                                        mainViewModel.startTest(test, user.id)
                                        currentScreen = AppScreen.TestTaking
                                    } else {
                                        currentScreen = AppScreen.Login
                                    }
                                },
                                onBuyTest = { test ->
                                    val user = authUiState.currentUser
                                    if (user != null) {
                                        mainViewModel.initiateCheckout("TEST", test.id, test.title, test.price)
                                        currentScreen = AppScreen.PaymentCheckout
                                    } else {
                                        currentScreen = AppScreen.Login
                                    }
                                },
                                onOpenPdf = { pdf ->
                                    mainViewModel.openPdfReader(pdf)
                                    currentScreen = AppScreen.PdfReader
                                },
                                onBuyPdf = { pdf ->
                                    val user = authUiState.currentUser
                                    if (user != null) {
                                        mainViewModel.initiateCheckout("PDF", pdf.id, pdf.title, pdf.price)
                                        currentScreen = AppScreen.PaymentCheckout
                                    } else {
                                        currentScreen = AppScreen.Login
                                    }
                                }
                            )
                        }
                        BottomNavTab.TESTS -> {
                            TestsScreen(
                                mainViewModel = mainViewModel,
                                mainUiState = mainUiState,
                                currentUser = authUiState.currentUser,
                                onStartTest = { test ->
                                    val user = authUiState.currentUser
                                    if (user != null) {
                                        mainViewModel.startTest(test, user.id)
                                        currentScreen = AppScreen.TestTaking
                                    } else {
                                        currentScreen = AppScreen.Login
                                    }
                                },
                                onBuyTest = { test ->
                                    val user = authUiState.currentUser
                                    if (user != null) {
                                        mainViewModel.initiateCheckout("TEST", test.id, test.title, test.price)
                                        currentScreen = AppScreen.PaymentCheckout
                                    } else {
                                        currentScreen = AppScreen.Login
                                    }
                                }
                            )
                        }
                        BottomNavTab.PDF_STORE -> {
                            PdfStoreScreen(
                                mainViewModel = mainViewModel,
                                mainUiState = mainUiState,
                                currentUser = authUiState.currentUser,
                                onOpenPdfReader = { pdf ->
                                    mainViewModel.openPdfReader(pdf)
                                    currentScreen = AppScreen.PdfReader
                                },
                                onBuyPdf = { pdf ->
                                    val user = authUiState.currentUser
                                    if (user != null) {
                                        mainViewModel.initiateCheckout("PDF", pdf.id, pdf.title, pdf.price)
                                        currentScreen = AppScreen.PaymentCheckout
                                    } else {
                                        currentScreen = AppScreen.Login
                                    }
                                }
                            )
                        }
                        BottomNavTab.MY_PURCHASES -> {
                            MyPurchasesScreen(
                                currentUser = authUiState.currentUser,
                                mainViewModel = mainViewModel,
                                onStartTest = { test ->
                                    val user = authUiState.currentUser
                                    if (user != null) {
                                        mainViewModel.startTest(test, user.id)
                                        currentScreen = AppScreen.TestTaking
                                    }
                                },
                                onOpenPdf = { pdf ->
                                    mainViewModel.openPdfReader(pdf)
                                    currentScreen = AppScreen.PdfReader
                                },
                                onExploreTests = { currentTab = BottomNavTab.TESTS },
                                onExplorePdfs = { currentTab = BottomNavTab.PDF_STORE }
                            )
                        }
                        BottomNavTab.PROFILE -> {
                            val appSettings by mainViewModel.appSettings.collectAsState()
                            ProfileScreen(
                                currentUser = authUiState.currentUser,
                                settings = appSettings,
                                authViewModel = authViewModel,
                                onNavigateToHistory = { currentScreen = AppScreen.TestHistory },
                                onNavigateToPurchases = { currentTab = BottomNavTab.MY_PURCHASES },
                                onNavigateToSubscriptions = { currentScreen = AppScreen.SubscriptionPlans },
                                onNavigateToLogin = { currentScreen = AppScreen.Login },
                                onNavigateToAdminLogin = { currentScreen = AppScreen.AdminLogin },
                                onNavigateToAdminPanel = { currentScreen = AppScreen.AdminPanel }
                            )
                        }
                    }
                }

                is AppScreen.TestTaking -> {
                    val session = mainUiState.activeSession
                    val user = authUiState.currentUser
                    if (session != null && user != null) {
                        TestTakingScreen(
                            session = session,
                            mainViewModel = mainViewModel,
                            userId = user.id,
                            onFinishAndShowResult = {
                                currentScreen = AppScreen.TestResult
                            },
                            onExit = {
                                mainViewModel.exitTestSession()
                                currentScreen = AppScreen.MainFlow
                            }
                        )
                    } else {
                        currentScreen = AppScreen.MainFlow
                    }
                }

                is AppScreen.TestResult -> {
                    val session = mainUiState.activeSession
                    val result = session?.resultAttempt
                    if (session != null && result != null) {
                        TestResultScreen(
                            attempt = result,
                            questions = session.questions,
                            onBackToHome = {
                                mainViewModel.exitTestSession()
                                currentScreen = AppScreen.MainFlow
                            },
                            onViewHistory = {
                                mainViewModel.exitTestSession()
                                currentScreen = AppScreen.TestHistory
                            }
                        )
                    } else {
                        currentScreen = AppScreen.MainFlow
                    }
                }

                is AppScreen.TestHistory -> {
                    BackHandler { currentScreen = AppScreen.MainFlow }
                    val user = authUiState.currentUser
                    TestHistoryScreen(
                        mainViewModel = mainViewModel,
                        userId = user?.id ?: "",
                        onBack = { currentScreen = AppScreen.MainFlow }
                    )
                }

                is AppScreen.PdfReader -> {
                    BackHandler { currentScreen = AppScreen.MainFlow }
                    val pdf = mainUiState.activeReaderPdf
                    if (pdf != null) {
                        PdfReaderScreen(
                            pdf = pdf,
                            onClose = {
                                mainViewModel.closePdfReader()
                                currentScreen = AppScreen.MainFlow
                            }
                        )
                    } else {
                        currentScreen = AppScreen.MainFlow
                    }
                }

                is AppScreen.PaymentCheckout -> {
                    BackHandler { currentScreen = AppScreen.MainFlow }
                    val checkout = mainUiState.checkoutState
                    val settings by mainViewModel.appSettings.collectAsState()
                    if (checkout != null) {
                        PaymentScreen(
                            checkoutState = checkout,
                            settings = settings,
                            currentUser = authUiState.currentUser,
                            mainViewModel = mainViewModel,
                            onBack = {
                                mainViewModel.closeCheckout()
                                currentScreen = AppScreen.MainFlow
                            },
                            onViewPurchases = {
                                mainViewModel.closeCheckout()
                                currentTab = BottomNavTab.MY_PURCHASES
                                currentScreen = AppScreen.MainFlow
                            }
                        )
                    } else {
                        currentScreen = AppScreen.MainFlow
                    }
                }

                is AppScreen.SubscriptionPlans -> {
                    BackHandler { currentScreen = AppScreen.MainFlow }
                    SubscriptionPlansScreen(
                        mainViewModel = mainViewModel,
                        currentUser = authUiState.currentUser,
                        onBuyPlan = { plan ->
                            mainViewModel.initiateCheckout("SUBSCRIPTION", plan.id, plan.name, plan.price)
                            currentScreen = AppScreen.PaymentCheckout
                        },
                        onBack = { currentScreen = AppScreen.MainFlow }
                    )
                }

                is AppScreen.Login -> {
                    BackHandler { currentScreen = AppScreen.MainFlow }
                    LoginScreen(
                        authViewModel = authViewModel,
                        authUiState = authUiState,
                        onLoginSuccess = { currentScreen = AppScreen.MainFlow },
                        onAdminLoginClick = { currentScreen = AppScreen.AdminLogin },
                        onBack = { currentScreen = AppScreen.MainFlow }
                    )
                }

                is AppScreen.AdminLogin -> {
                    BackHandler { currentScreen = AppScreen.MainFlow }
                    AdminLoginScreen(
                        authViewModel = authViewModel,
                        authUiState = authUiState,
                        onAdminLoginSuccess = { currentScreen = AppScreen.AdminPanel },
                        onBack = { currentScreen = AppScreen.MainFlow }
                    )
                }

                is AppScreen.AdminPanel -> {
                    BackHandler { currentScreen = AppScreen.MainFlow }
                    AdminDashboardScreen(
                        adminViewModel = adminViewModel,
                        adminUiState = adminUiState,
                        onExitAdmin = { currentScreen = AppScreen.MainFlow }
                    )
                }
            }
        }
    }
}
