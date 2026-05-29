package com.fintrack.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fintrack.presentation.ui.accounts.AccountsScreen
import com.fintrack.presentation.ui.accounts.AddAccountScreen
import com.fintrack.presentation.ui.analytics.AnalyticsScreen
import com.fintrack.presentation.ui.budgets.AddBudgetScreen
import com.fintrack.presentation.ui.budgets.BudgetsScreen
import com.fintrack.presentation.ui.goals.AddGoalScreen
import com.fintrack.presentation.ui.goals.GoalsScreen
import com.fintrack.presentation.ui.home.HomeScreen
import com.fintrack.presentation.ui.settings.SettingsScreen
import com.fintrack.presentation.ui.transactions.AddTransactionScreen
import com.fintrack.presentation.ui.transactions.TransactionsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        }
    ) {
        // Bottom nav destinations
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onNavigateToAccounts = { navController.navigate(Screen.Accounts.route) }
            )
        }

        composable(Screen.Transactions.route) {
            TransactionsScreen(
                onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Budgets.route) {
            BudgetsScreen(
                onNavigateToAddBudget = { navController.navigate(Screen.AddBudget.route) }
            )
        }

        composable(Screen.AddBudget.route) {
            AddBudgetScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }

        // More section destinations
        composable(Screen.Accounts.route) {
            AccountsScreen(
                onNavigateToAddAccount = { navController.navigate(Screen.AddAccount.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddAccount.route) {
            AddAccountScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Goals.route) {
            GoalsScreen(
                onNavigateToAddGoal = { navController.navigate(Screen.AddGoal.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddGoal.route) {
            AddGoalScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
