package com.example.cinemavault.presentation.navigation

/**
 * Represents the different screens in the application and their corresponding routes.
 *
 * @property route The route string for the screen.
 */
sealed class Screen(val route: String) {
    /**
     * Represents the Home screen.
     */
    data object Home : Screen("home_screen")

    /**
     * Represents the Search screen.
     */
    data object Search : Screen("search_screen")

    /**
     * Represents the Bookmarks screen.
     */
    data object Bookmarks : Screen("bookmarks_screen")

    /**
     * Represents the Details screen, which requires a movieId.
     */
    data object Details : Screen("details_screen/{movieId}") {
        /**
         * Creates the route for the Details screen with a specific movieId.
         * @param movieId The ID of the movie to display.
         * @return The complete route string for the Details screen.
         */
        fun createRoute(movieId: Int) = "details_screen/$movieId"
    }
}
