package com.cognifyteam.cognifyapp.ui.navigation

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.course.AddCourseScreen
import com.cognifyteam.cognifyapp.ui.course.CourseScreen
import com.cognifyteam.cognifyapp.ui.course.CourseViewModel
import com.cognifyteam.cognifyapp.ui.course.SeeAllCoursesScreen
import com.cognifyteam.cognifyapp.ui.home.HomeScreen
import com.cognifyteam.cognifyapp.ui.home.UserSearchScreen
import com.cognifyteam.cognifyapp.ui.learningpath.screen.MainLearningPathScreen
import com.cognifyteam.cognifyapp.ui.learningpath.screen.AddNewLearningPathScreen
import com.cognifyteam.cognifyapp.ui.learningpath.screen.LearningPathDetailScreen
import com.cognifyteam.cognifyapp.ui.profile.ProfilePage
import com.cognifyteam.cognifyapp.ui.profile.ProfileViewModel
import com.cognifyteam.cognifyapp.ui.profile.UserCoursesViewModel

object AppNavRoutes {
    const val HOME = "home"

    const val SMART = "smart"
    // Rute untuk profil pengguna yang sedang login
    const val ADDLP = "add_learning_path"
    const val LP_DETAIL = "learning_path_details/{lpId}"

    const val PROFILE = "profile"
    const val COURSE_DETAILS = "course_details/{courseId}"
    const val SEARCH = "search"
    const val COURSE = "course"
    const val ALLCOURSE = "allcourse"
    // Rute BARU untuk melihat profil pengguna lain
    const val USER_PROFILE = "user_profile/{firebaseId}"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit
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
        startDestination = AppBottomNavItem.Home.route
    ) {
        // ... (composable lain seperti HOME, SMART, SEARCH, COURSE, ALLCOURSE tetap sama)
        composable(AppNavRoutes.HOME) {
            HomeScreen(navController, appContainer,onFabStateChange = onFabStateChange,
                onTopBarStateChange = onTopBarStateChange,
                onShowSnackbar = onShowSnackbar)
        }
        composable(AppNavRoutes.SMART) {
            MainLearningPathScreen(
                // --- Meneruskan callbacks ke MainLearningPathScreen ---
                appContainer = appContainer,
                navController = navController,
                onFabStateChange = onFabStateChange,
                onTopBarStateChange = onTopBarStateChange,
                onShowSnackbar = onShowSnackbar
            )
        }
        composable(AppNavRoutes.ADDLP){
            AddNewLearningPathScreen(
                appContainer= appContainer,
                navController = navController,
                onFabStateChange = onFabStateChange,
                onTopBarStateChange = onTopBarStateChange,
                onShowSnackbar = onShowSnackbar // Meneruskan
            )
        }
        composable(
            route = AppNavRoutes.LP_DETAIL,
            arguments = listOf(navArgument("lpId") { type = NavType.StringType })
        ) { backStackEntry ->
            val learningpathId = backStackEntry.arguments?.getString("lpId")
            Log.d("CourseDetails", "Navigating to CourseScreen with courseId: $learningpathId")
            if (learningpathId != null) {
                // Panggil CourseScreen hanya dengan parameter yang dibutuhkan
                LearningPathDetailScreen(
                    navController = navController,
                    onFabStateChange = onFabStateChange,
                    onTopBarStateChange = onTopBarStateChange,
                    onShowSnackbar = onShowSnackbar,
                    learningPathId = learningpathId.toInt()
                )
            }
        }

        composable(AppNavRoutes.SEARCH) {
            UserSearchScreen(
                appContainer,
                navController,
                onFabStateChange = onFabStateChange,
                onTopBarStateChange = onTopBarStateChange,
                onShowSnackbar = onShowSnackbar
            )
        }
        composable(AppNavRoutes.COURSE) {
            AddCourseScreen(navController, appContainer)
        }
        composable(AppNavRoutes.ALLCOURSE) {
            SeeAllCoursesScreen(onFabStateChange = onFabStateChange,
                onTopBarStateChange = onTopBarStateChange,
                onShowSnackbar = onShowSnackbar, appContainer, onBackClick = { navController.popBackStack() }, onCourseClick = { courseId -> navController.navigate("course_details/$courseId") })
        }


        // --- LOGIKA GABUNGAN UNTUK SEMUA HALAMAN PROFIL ---

        // Rute untuk profil milik sendiri (tanpa argumen)
        composable(AppNavRoutes.PROFILE) {
            val firebaseId = currentUser?.firebaseId
            if (firebaseId != null) {
                // Panggil ProfilePage dengan ID user yang sedang login
                ProfilePageComposable(
                    navController = navController,
                    authViewModel = authViewModel,
                    userViewModel = userViewModel,
                    appContainer = appContainer,
                    firebaseId = firebaseId,
                    onFabStateChange = onFabStateChange,
                    onTopBarStateChange = onTopBarStateChange,
                    onShowSnackbar = onShowSnackbar
                )
            }
        }

        // Rute untuk profil user lain (dengan argumen firebaseId)
        composable(
            route = AppNavRoutes.USER_PROFILE,
            arguments = listOf(navArgument("firebaseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val firebaseId = backStackEntry.arguments?.getString("firebaseId")
            if (firebaseId != null) {
                // Panggil ProfilePage yang sama, tetapi dengan ID dari argumen
                ProfilePageComposable(
                    navController = navController,
                    authViewModel = authViewModel,
                    userViewModel = userViewModel,
                    appContainer = appContainer,
                    firebaseId = firebaseId,
                    onFabStateChange = onFabStateChange,
                    onTopBarStateChange = onTopBarStateChange,
                    onShowSnackbar = onShowSnackbar
                )
            }
        }

        composable(
            route = AppNavRoutes.COURSE_DETAILS,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                CourseScreen(
                    navController = navController,
                    courseId = courseId,
                    appContainer = appContainer,
                    onFabStateChange = onFabStateChange,
                    onTopBarStateChange = onTopBarStateChange,
                    onShowSnackbar = onShowSnackbar
                )
            }
        }
    }
}

/**
 * Composable helper untuk menghindari duplikasi kode saat membuat instance
 * ViewModel untuk ProfilePage.
 */
@Composable
private fun ProfilePageComposable(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    appContainer: AppContainer,
    firebaseId: String,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    // Buat semua ViewModel yang dibutuhkan oleh ProfilePage di sini
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.provideFactory(
            postsRepository = appContainer.profileRepository
        )
    )
    val userCoursesViewModel: UserCoursesViewModel = viewModel(
        key = "enrolled_$firebaseId", // Key unik untuk enrolled courses
        factory = UserCoursesViewModel.provideFactory(
            courseRepository = appContainer.courseRepository
        )
    )
    val courseViewModel: CourseViewModel = viewModel(
        key = "created_$firebaseId", // Key unik untuk created courses
        factory = CourseViewModel.provideFactory(
            courseRepository = appContainer.courseRepository
        )
    )

    ProfilePage(
        navController = navController,
        authViewModel = authViewModel,
        profileViewModel = profileViewModel,
        userCoursesViewModel = userCoursesViewModel,
        userViewModel = userViewModel,
        courseViewModel = courseViewModel,
        firebaseId = firebaseId,
        onFabStateChange = onFabStateChange,
        onTopBarStateChange = onTopBarStateChange,
        onShowSnackbar = onShowSnackbar
    )
}