package com.example.cinemavault.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cinemavault.presentation.components.MovieItem
import com.example.cinemavault.presentation.navigation.Screen

/**
 * A composable screen that allows the user to search for movies.
 *
 * @param navController The [NavController] used for navigation.
 * @param viewModel The [SearchViewModel] for this screen.
 */
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Search Movies...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(columns = GridCells.Adaptive(120.dp)) {
            items(results) { movie ->
                MovieItem(movie = movie) {
                    navController.navigate(Screen.Details.createRoute(it))
                }
            }
        }
    }
}
