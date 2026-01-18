package com.example.cinemavault.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cinemavault.domain.model.Movie
import com.example.cinemavault.presentation.components.MovieItem
import com.example.cinemavault.presentation.navigation.Screen

/**
 * The main screen of the CinemaVault app, which serves as the home screen.
 * It displays curated lists of movies, such as "Trending Today" and "Now Playing".
 *
 * @param navController The [NavController] for handling navigation to other screens.
 * @param viewModel The [HomeViewModel] that provides the state for this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "CinemaVault",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trending Section
            SectionHeader(title = "Trending Today")
            MovieRow(
                movies = state.trendingMovies,
                navController = navController
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Now Playing Section
            SectionHeader(title = "Now Playing")
            MovieRow(
                movies = state.nowPlayingMovies,
                navController = navController
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

/**
 * A composable that displays a header for a section of content.
 *
 * @param title The text to display as the section header.
 */
@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * A composable that displays a horizontal scrolling list of movies.
 *
 * @param movies The list of [Movie] objects to display in the row.
 * @param navController The [NavController] used to handle clicks on movie items, navigating to the details screen.
 */
@Composable
fun MovieRow(
    movies: List<Movie>,
    navController: NavController
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies) { movie ->
            MovieItem(
                movie = movie,
                modifier = Modifier.width(140.dp)
            ) {
                navController.navigate(Screen.Details.createRoute(it))
            }
        }
    }
}
