package com.fintrack.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * All navigation routes in the app.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Transactions : Screen("transactions")
    data object AddTransaction : Screen("add_transaction")
    data object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(id: Long) = "edit_transaction/$id"
    }
    data object Budgets : Screen("budgets")
    data object AddBudget : Screen("add_budget")
    data object Analytics : Screen("analytics")
    data object Accounts : Screen("accounts")
    data object AddAccount : Screen("add_account")
    data object Goals : Screen("goals")
    data object AddGoal : Screen("add_goal")
    data object Settings : Screen("settings")
    data object Onboarding : Screen("onboarding")
}

/**
 * Bottom navigation items.
 */
enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME(
        route = Screen.Home.route,
        label = "Home",
        icon = Icons.Rounded.Home,
        selectedIcon = Icons.Rounded.Home
    ),
    TRANSACTIONS(
        route = Screen.Transactions.route,
        label = "Transactions",
        icon = Icons.Rounded.SwapHoriz,
        selectedIcon = Icons.Rounded.SwapHoriz
    ),
    BUDGETS(
        route = Screen.Budgets.route,
        label = "Budgets",
        icon = Icons.Rounded.PieChart,
        selectedIcon = Icons.Rounded.PieChart
    ),
    ANALYTICS(
        route = Screen.Analytics.route,
        label = "Analytics",
        icon = Icons.Rounded.BarChart,
        selectedIcon = Icons.Rounded.BarChart
    ),
    MORE(
        route = "more",
        label = "More",
        icon = Icons.Rounded.MoreHoriz,
        selectedIcon = Icons.Rounded.MoreHoriz
    );
}
