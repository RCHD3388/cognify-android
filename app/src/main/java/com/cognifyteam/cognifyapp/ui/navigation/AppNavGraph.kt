package com.cognifyteam.cognifyapp.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.ui.learningpath.screen.MainLearningPathScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
import com.cognifyteam.cognifyapp.ui.course.CourseScreen
import com.cognifyteam.cognifyapp.ui.home.HomeScreen
import com.cognifyteam.cognifyapp.ui.profile.ProfilePage

// Hapus ProfileScreen dan ProfileNavigation dari file ini jika ada.
// Kita akan langsung memanggil ProfilePage.

// ... (HomeScreen dan SettingsScreen jika ada)

object AppNavRoutes {
    const val HOME = "home"
    const val SMART = "smart"
    const val PROFILE = "profile"
    // BARU: Tambahkan rute untuk detail kursus
    const val COURSE_DETAILS = "course_details/{courseId}"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    appContainer: AppContainer
) {
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.provideFactory(
            postsRepository = appContainer.authRepository
        )
    )

    NavHost(
        navController = navController,
        // DIUBAH: startDestination sekarang bisa jadi rute profil atau home
        startDestination = AppBottomNavItem.Home.route
    ) {
        composable(AppNavRoutes.HOME) {
            HomeScreen()
        }
        composable(AppNavRoutes.SMART) {
            MainLearningPathScreen()
        }

        // DIUBAH: Langsung panggil ProfilePage di sini
        composable(AppNavRoutes.PROFILE) {
            ProfilePage(
                // Gunakan NavController utama
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // BARU: Tambahkan rute detail kursus ke grafik utama
        composable(AppNavRoutes.COURSE_DETAILS) { backStackEntry ->
            // Anda mungkin perlu mengambil courseId jika dibutuhkan
            // val courseId = backStackEntry.arguments?.getString("courseId")
            CourseScreen()
        }
    }
}