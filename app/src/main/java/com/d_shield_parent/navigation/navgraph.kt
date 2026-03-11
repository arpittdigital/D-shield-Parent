import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.d_shield_parent.Dashboard.AddCustomerFlow
import com.d_shield_parent.Dashboard.ListScreen
import com.d_shield_parent.Dashboard.MainScreen
import com.d_shield_parent.Dashboard.MpinScreen
import com.d_shield_parent.Dashboard.viewModel.MpinViewmodel
import com.d_shield_parent.Profile.ProfileScreen
import com.d_shield_parent.Dashboard.ProfileViewModel
import com.d_shield_parent.NewService
import com.d_shield_parent.auth.LoginScreen
import com.d_shield_parent.presentation.auth.HomeScreen
import com.d_shield_parent.auth.Splash1
import com.d_shield_parent.auth.Splash2
import com.d_shield_parent.auth.Splash3
import com.d_shield_parent.auth.Splash4
import com.d_shield_parent.presentation.auth.HelpScreen
import com.d_shield_parent.presentation.auth.QrScreen

sealed class Routes(val route: String) {
    object Splash1 : Routes("splash_screen")
    object Splash2: Routes("splash2_screen")
    object Splash3: Routes("splash3_screen")
    object Splash4: Routes("splash4_screen")
    object Login : Routes("login_screen")
    object Register : Routes("register_screen")
    object Home : Routes("home_screen")
    object AddCustomer : Routes("add_customer_screen")

    object QrScanner : Routes("qr_screen/{deviceId}") {
        fun createRoute(deviceId: Int) = "qr_screen/$deviceId"  // ✅ Add this help
    }

    object CustomerList : Routes("customer_list_screen")
    object Dashboard : Routes("dashboard_screen")
    object Profile : Routes("profile_screen")
    object Service : Routes("service_screen")
    object Help : Routes("help_screen")
    object SetupMPin : Routes("setup_mpin_screen")
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Routes.Splash1.route
) {
    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screens - FIXED
        composable(route = Routes.Splash1.route) {
            Splash1(navController = navController)
        }

        composable(route = Routes.Splash2.route) {
            Splash2(navController = navController)
        }


        composable(route = Routes.Splash3.route) {
            Splash3(navController = navController)
        }

        composable(route = Routes.Splash4.route) {
            Splash4(navController = navController)
        }

        composable(route = Routes.Login.route) {
            LoginScreen(navController = navController)

        }

        // Register Screen
        composable(route = Routes.Register.route) {
            // RegisterScreen(navController = navController)
        }

        // Home Screen
        composable(route = Routes.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = profileViewModel
            )
        }

        // Add Customer Screen
        composable(route = Routes.AddCustomer.route) {
            AddCustomerFlow(navController = navController)
        }

        // Customer List Screen
        composable(route = Routes.CustomerList.route) {
            ListScreen(navController = navController)
        }

        // Profile Screen
        composable(route = Routes.Profile.route) {
            ProfileScreen(
                navController = navController,
                viewModel = profileViewModel
            )
        }

        // Service Screen
        composable(route = Routes.Service.route) {
            NewService(navController = navController)
        }
        // Dashboard Screen
        composable(route = Routes.Dashboard.route) {
            MainScreen(navController = navController)
        }
        // Distributor dashboard — same MainScreen, reads userType internally
        composable(route = "distributor_dashboard_screen") {
            MainScreen(navController = navController)
        }
        composable(route = Routes.Help.route) {
            HelpScreen(navController = navController)
        }

        composable(route = Routes.SetupMPin.route) {
            MpinScreen(navController = navController,
                viewmodel = MpinViewmodel(application = Application()))
        }
        composable(
            route = Routes.QrScanner.route,
            arguments = listOf(navArgument("deviceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getInt("deviceId") ?: 0
            QrScreen(
                navController = navController,
                deviceId = deviceId
            )
        }
    }
}