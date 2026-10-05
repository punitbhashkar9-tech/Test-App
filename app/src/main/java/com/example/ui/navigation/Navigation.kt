package com.example.ui.navigation

enum class BottomNavTab {
    HOME,
    TESTS,
    PDF_STORE,
    MY_PURCHASES,
    PROFILE
}

sealed class AppScreen {
    object MainFlow : AppScreen()
    object TestTaking : AppScreen()
    object TestResult : AppScreen()
    object TestHistory : AppScreen()
    object PdfReader : AppScreen()
    object PaymentCheckout : AppScreen()
    object SubscriptionPlans : AppScreen()
    object Login : AppScreen()
    object AdminLogin : AppScreen()
    object AdminPanel : AppScreen()
}
