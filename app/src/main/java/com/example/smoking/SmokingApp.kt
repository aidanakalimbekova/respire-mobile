package com.example.smoking

import android.app.Activity.RESULT_OK
import android.graphics.pdf.PdfDocument.Page
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smoking.ui.theme.SmokingTheme
import com.example.smoking.ui.theme.gemini.PageScreen
import com.example.smoking.ui.theme.gemini.PageViewModel
import com.example.smoking.ui.theme.home.HomeScreen
import com.example.smoking.ui.theme.home.HomeViewModel
import com.example.smoking.ui.theme.profile.AddFriendScreen
import com.example.smoking.ui.theme.profile.AddFriendViewModel
import com.example.smoking.ui.theme.profile.FriendsScreen
import com.example.smoking.ui.theme.profile.FriendsViewModel
import com.example.smoking.ui.theme.profile.InvitationsScreen
import com.example.smoking.ui.theme.profile.InvitationsViewModel
import com.example.smoking.ui.theme.profile.ProfileScreen
import com.example.smoking.ui.theme.profile.ProfileViewModel
import com.example.smoking.ui.theme.quest.ChallengeDetailViewModel
import com.example.smoking.ui.theme.quest.ChallengeDetailsScreen
import com.example.smoking.ui.theme.quest.CreateChallengeViewModel
import com.example.smoking.ui.theme.quest.CreateQuestScreen
import com.example.smoking.ui.theme.quest.LineChartViewModel
import com.example.smoking.ui.theme.quest.QuestScreen
import com.example.smoking.ui.theme.signin.GoogleAuthUiClient
import com.example.smoking.ui.theme.signin.SignInScreen
import com.example.smoking.ui.theme.signin.SignInViewModel
import com.example.smoking.ui.theme.statistics.StatisticsScreen
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
//    object Statistics : BottomNavItem("statistics", Icons.Default.Insights, "Statistics")
    object Quest : BottomNavItem("quest", Icons.Default.Leaderboard, "Quest")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}
//@Preview
@Composable
fun SmokingApp() {

    SmokingTheme {

        val smokeNavController = rememberNavController()
        Scaffold (containerColor = Color(0xFFF9F9F9),modifier = Modifier.fillMaxSize(), bottomBar = {BottomNavigationBar(smokeNavController)}  ) {
            NavHost(
                navController = smokeNavController,
                startDestination = "home",
                Modifier.padding(it)
            ) {

                composable(BottomNavItem.Home.route){
                    HomeScreen(HomeViewModel(), navController = smokeNavController)
                }
//                composable(BottomNavItem.Statistics.route){
//                    StatisticsScreen()
//                }
                composable(BottomNavItem.Quest.route){
                    QuestScreen(LineChartViewModel(), onClick = { smokeNavController.navigate("createQuest") }, navController = smokeNavController)
                }
                composable(BottomNavItem.Profile.route){
                    ProfileScreen(ProfileViewModel(), onClick = { smokeNavController.navigate("addfriend") }, onFriends = { smokeNavController.navigate("friends") }, onRequests = {smokeNavController.navigate("invitations") })
                }
                composable("addfriend") {
                    AddFriendScreen(onClick = { smokeNavController.navigate("profile") }, AddFriendViewModel())
                }
                composable("friends") {
                    FriendsScreen(onClick = { smokeNavController.navigate("profile") }, FriendsViewModel())
                }
                composable("invitations") {
                    InvitationsScreen(onClick = { smokeNavController.navigate("profile") }, InvitationsViewModel())
                }
                composable("createQuest") {
                    CreateQuestScreen(onClick = {smokeNavController.navigate("quest")},
                        viewModel = CreateChallengeViewModel()
                    )
                }
                composable("challengeDetails/{challengeId}") {backStackEntry ->
                    val challengeId = backStackEntry.arguments?.getString("challengeId") ?: ""
                    ChallengeDetailsScreen( navController = smokeNavController,
                        viewModel = ChallengeDetailViewModel(challengeId)
                    )
                }
                composable("gemini") {
                    PageScreen(navController = smokeNavController, PageViewModel())
                }
            }
            }
        }
    }

//navController.navigate("challengeDetails/${it.id}")

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
//        BottomNavItem.Statistics,
        BottomNavItem.Quest,
        BottomNavItem.Profile,
    )
    NavigationBar (containerColor = Color.Transparent){
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach {
            item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }

    }
}