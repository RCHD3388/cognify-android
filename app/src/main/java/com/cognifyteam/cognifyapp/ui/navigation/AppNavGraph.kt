package com.cognifyteam.cognifyapp.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.ui.learningpath.screen.MainLearningPathScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.course.CourseScreen
import com.cognifyteam.cognifyapp.ui.home.HomeScreen
import com.cognifyteam.cognifyapp.ui.profile.ProfilePage
import com.cognifyteam.cognifyapp.ui.profile.ProfileViewModel
import com.cognifyteam.cognifyapp.ui.profile.UserCoursesViewModel
import kotlin.math.log

object AppNavRoutes {
    const val HOME = "home"
    const val SMART = "smart"
    const val PROFILE = "profile"
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

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(
            authRepository = appContainer.authRepository
        )
    )

    val currentUser by userViewModel.userState.collectAsState()

    NavHost(
        navController = navController,
        // DIUBAH: startDestination sekarang bisa jadi rute profil atau home
        startDestination = AppBottomNavItem.Home.route
    ) {
        composable(AppNavRoutes.HOME) {
            HomeScreen(appContainer)
        }
        composable(AppNavRoutes.SMART) {
            MainLearningPathScreen()
        }

        composable(AppNavRoutes.PROFILE) {

            // Dapatkan ID user dari state otentikasi
            val firebaseId = currentUser?.firebaseId

            // Hanya tampilkan halaman profil jika user sudah terotentikasi dan kita punya ID-nya
            if (firebaseId != null) {

                // Buat ProfileViewModel menggunakan factory
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.provideFactory(
                        postsRepository = appContainer.profileRepository
                    )
                )

                val userCoursesViewModel: UserCoursesViewModel = viewModel(
                    factory = UserCoursesViewModel.provideFactory(
                        courseRepository = appContainer.courseRepository
                    )
                )

                // Panggil ProfilePage dengan semua parameter yang dibutuhkan
//                Log.d("ProfileViewModel", "ProfileViewModel created with firebaseId: $firebaseId")
                ProfilePage(
                    navController = navController,
                    authViewModel = authViewModel, // Untuk aksi logout
                    profileViewModel = profileViewModel, // Untuk data profil
                    userCoursesViewModel = userCoursesViewModel,
                    firebaseId = firebaseId // Berikan ID user
                )
            } else {
//                Log.d("ProfileViewModel", "User not authenticated or firebaseId is null")
//                authViewModel.logout()
            }
        }
        
        composable(AppNavRoutes.COURSE_DETAILS) { backStackEntry ->
            // Anda mungkin perlu mengambil courseId jika dibutuhkan
            // val courseId = backStackEntry.arguments?.getString("courseId")
            CourseScreen()
        }
    }
}