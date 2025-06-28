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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cognifyteam.cognifyapp.data.AppContainer
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
    navController: NavController,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    // --- Konfigurasi UI Utama ---
    LaunchedEffect(Unit) {
        onFabStateChange(FabState(isVisible = false))
        onTopBarStateChange(TopBarState(
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
    LaunchedEffect(loggedInUser) {
        loggedInUser?.firebaseId?.let {
            searchViewModel.loadInitialFollowingState(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Gunakan theme
    ) {
        // Search Box
        SearchBox(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                searchViewModel.onSearchQueryChanged(it)
            },
            modifier = Modifier.padding(16.dp)
        )

        // Tampilkan hasil berdasarkan state
        when (val state = uiState) {
            is SearchUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SearchUiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }
            is SearchUiState.EmptyQuery -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type a name to search for users.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            is SearchUiState.EmptyResult -> {
                SearchResultsSection(users = emptyList(), searchQuery = searchQuery, onFollowClick = { _, _ -> }, onUserClick = {})
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
                            searchViewModel.toggleFollowState(userIdToFollow)
                        }
                    },
                    onUserClick = { userId ->
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
            Text("Search users...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "No results",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No users found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Try searching with different keywords",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    } else {
        if (searchQuery.isNotEmpty()) {
            Text(
                text = "${users.size} results for \"$searchQuery\"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users, key = { it.user.firebaseId }) { uiUser ->
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
    val user = uiUser.user

    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Default profile",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // if (user.isVerified) ...
                }
                Text(
                    text = "@${user.email.substringBefore('@')}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // if (user.bio.isNotEmpty()) ...
                Text(
                    text = "0 followers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Button(
                onClick = onFollowClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiUser.isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                    contentColor = if (uiUser.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp).widthIn(min = 80.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = if (uiUser.isFollowing) "Following" else "Follow",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 10_000 -> "${number / 1_000}K"
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}