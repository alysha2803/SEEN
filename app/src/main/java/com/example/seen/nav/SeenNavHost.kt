package com.example.seen.nav

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.seen.data.*
import com.example.seen.state.ProgressViewModel
import com.example.seen.ui.apps.*
import com.example.seen.ui.minigames.*
import com.example.seen.ui.shell.*

@Composable
fun SeenNavHost(
    navController: NavHostController,
    vm: ProgressViewModel
) {
    val state by vm.state.collectAsState()

    // Determine start destination from saved progress
    val start = when {
        !state.acceptedContentWarning -> Routes.CONTENT_WARNING
        state.finished -> Routes.RESOURCES
        else -> Routes.LOCKSCREEN
    }

    NavHost(navController = navController, startDestination = start) {

        composable(Routes.CONTENT_WARNING) {
            ContentWarning(
                onContinue = {
                    vm.acceptContentWarning()
                    navController.navigate(Routes.LOCKSCREEN) {
                        popUpTo(Routes.CONTENT_WARNING) { inclusive = true }
                    }
                },
                onLeave = { navController.navigate(Routes.RESOURCES) }
            )
        }

        composable(Routes.LOCKSCREEN) {
            Lockscreen(
                onSwipeUp = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOCKSCREEN) { inclusive = true }
                    }
                },
                onHelp = { navController.navigate(Routes.RESOURCES) }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                progressState = state,
                onAppTap = { appId -> navController.navigate(Routes.app(appId.name)) },
                onHelp = { navController.navigate(Routes.RESOURCES) }
            )
        }

        composable(
            route = Routes.APP_ROUTE,
            arguments = listOf(navArgument("appId") { type = NavType.StringType })
        ) { entry ->
            val appId = AppId.valueOf(entry.arguments!!.getString("appId")!!)
            AppScaffold(
                appId = appId,
                vm = vm,
                onBack = { navController.popBackStack() },
                onHelp = { navController.navigate(Routes.RESOURCES) },
                onLaunchMiniGame = { mg -> navController.navigate(Routes.minigame(mg.name)) }
            )
        }

        composable(
            route = Routes.MINIGAME_ROUTE,
            arguments = listOf(navArgument("minigameId") { type = NavType.StringType })
        ) { entry ->
            val mg = MiniGame.valueOf(entry.arguments!!.getString("minigameId")!!)
            when (mg) {
                MiniGame.AFFECTION_OR_RED_FLAG -> AffectionSwipe(
                    vm = vm,
                    onComplete = { navController.popBackStack() }
                )
                MiniGame.LOCK_IT_DOWN -> MiniGameStub(
                    title = "Lock it down",
                    description = "Spot every piece of exposure in Aina's post. (Coming in Milestone 6.)",
                    mg = mg,
                    vm = vm,
                    onComplete = { navController.popBackStack() }
                )
                MiniGame.TRACE_IT_BACK -> MiniGameStub(
                    title = "Trace it back",
                    description = "Match each thing he knew to the post that leaked it. (Coming in Milestone 6.)",
                    mg = mg,
                    vm = vm,
                    onComplete = { navController.popBackStack() }
                )
                MiniGame.IS_THIS_PHONE_CLEAN -> MiniGameStub(
                    title = "Is this phone clean?",
                    description = "Spot the signs of device compromise. (Coming in Milestone 6.)",
                    mg = mg,
                    vm = vm,
                    onComplete = { navController.popBackStack() }
                )
                MiniGame.WHAT_DOES_SHE_DO -> MiniGameStub(
                    title = "What does she do now?",
                    description = "Guide the response. (Coming in Milestone 8.)",
                    mg = mg,
                    vm = vm,
                    onComplete = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.RESOURCES) {
            ResourcesScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
