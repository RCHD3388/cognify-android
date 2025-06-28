package com.cognifyteam.cognifyapp.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.remote.UserUiState
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.common.FollowViewModel
import com.cognifyteam.cognifyapp.ui.common.SearchUiState
import com.cognifyteam.cognifyapp.ui.common.SearchViewModel
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.navigation.AppNavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(
    appContainer: AppContainer,
     navController: NavController, // Mungkin akan dibutuhkan nanti
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        // --- Konfigurasi FAB untuk UserSearchScreen ---
        onFabStateChange(FabState(
            isVisible = false,
        ))
        onTopBarStateChange(TopBarState( // Konfigurasi eksplisit untuk Top Bar
            isVisible = true,
            title = "Search Users",
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = null
        ))
    }
    // --- Inisialisasi ViewModel ---
    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModel.provideFactory(
            followRepository = appContainer.followRepository
        )
    )
    val followViewModel: FollowViewModel = viewModel(
        factory = FollowViewModel.provideFactory(appContainer.followRepository)
    )
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(appContainer.authRepository)
    )

    // --- Amati State ---
    val uiState by searchViewModel.uiState.collectAsState()
    val loggedInUser by userViewModel.userState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // --- Efek Samping ---
    // Muat daftar 'following' dari user yang login sekali saja
    LaunchedEffect(loggedInUser) {
        loggedInUser?.firebaseId?.let {
            Log.d("SearchScreen", "User is available: $it. Loading initial state.")
            searchViewModel.loadInitialFollowingState(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header (tidak berubah)
//        TopAppBar(
//            title = {
//                Text(
//                    "Search Users",
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Medium
//                )
//            },
//            navigationIcon = {
//                IconButton(onClick = { /* Handle back navigation */ }) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                }
//            },
//            colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = Color.White
//            )
//        )

        // Search Box (sekarang terhubung ke ViewModel)
        SearchBox(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                searchViewModel.onSearchQueryChanged(it)
            },
            modifier = Modifier.padding(16.dp)
        )

        // Tampilkan hasil berdasarkan state dari ViewModel
        when (val state = uiState) {
            is SearchUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SearchUiState.Error -> {
                Text(state.message, color = Color.Red, modifier = Modifier.padding(16.dp))
            }
            is SearchUiState.EmptyQuery -> {
                // Tampilkan pesan awal
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type a name to search for users.")
                }
            }
            is SearchUiState.EmptyResult -> {
                SearchResultsSection(users = emptyList(), searchQuery = searchQuery, onFollowClick = {userId, isFollowing -> }, onUserClick = {userId -> })
            }
            is SearchUiState.Success -> {
                SearchResultsSection(
                    users = state.users,
                    searchQuery = searchQuery,
                    onFollowClick = { userIdToFollow, isCurrentlyFollowing ->
                        loggedInUser?.firebaseId?.let { followerId ->
                            if (isCurrentlyFollowing) {
                                followViewModel.unfollowUser(followerId, userIdToFollow)
                            } else {
                                followViewModel.followUser(followerId, userIdToFollow)
                            }
                            // Update UI secara optimis
                            searchViewModel.toggleFollowState(userIdToFollow)
                        }
                    },
                    onUserClick = { userId ->
                        // Buat rute yang benar dengan mengganti placeholder
                        val route = AppNavRoutes.USER_PROFILE.replace("{firebaseId}", userId)
                        navController.navigate(route)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Search users...",
                color = Color(0xFF6B6B6B)
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF6B6B6B)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color(0xFF6B6B6B)
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4A90E2),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun SearchResultsSection(
    users: List<UserUiState>,
    searchQuery: String,
    onFollowClick: (userId: String, isCurrentlyFollowing: Boolean) -> Unit,
    onUserClick: (userId: String) -> Unit,
) {
    if (users.isEmpty() && searchQuery.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "No results",
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFFBDBDBD)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No users found",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF6B6B6B)
                )
                Text(
                    text = "Try searching with different keywords",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users, key = { it.user.firebaseId }) { uiUser ->
                Log.d("test", uiUser.user.toString() + " ---- " + uiUser.isFollowing)
                UserCard(
                    uiUser = uiUser,
                    onClick = { onUserClick(uiUser.user.firebaseId) },
                    onFollowClick = { onFollowClick(uiUser.user.firebaseId, uiUser.isFollowing) }
                )
            }
        }
    }
}

@Composable
fun UserCard(
    uiUser: UserUiState,
    onClick: () -> Unit,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = uiUser.user // Ekstrak model domain asli

    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        // ...
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(56.dp)) { /* Gambar Profil */ }

            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = user.name) // Gunakan nama dari model User
                    // if (user.isVerified) ... // isVerified perlu ditambahkan ke model User jika ada
                }
                Text(text = "@${user.email.substringBefore('@')}") // Contoh username
                // if (user.bio.isNotEmpty()) ... // bio perlu ditambahkan ke model User jika ada
                Text(text = "0 followers") // followerCount perlu ditambahkan ke model User jika ada
            }

            // Gunakan isFollowing dari UserUiState
            Button(
                onClick = onFollowClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiUser.isFollowing) Color(0xFFE8F5E8) else Color(0xFF4A90E2),
                    contentColor = if (uiUser.isFollowing) Color(0xFF2E7D32) else Color.White
                ),
                // ...
            ) {
                Text(text = if (uiUser.isFollowing) "Following" else "Follow")
            }
        }
    }
}

// Helper function to format numbers
fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "${number / 1_000_000}M"
        number >= 1_000 -> "${number / 1_000}K"
        else -> number.toString()
    }
}
