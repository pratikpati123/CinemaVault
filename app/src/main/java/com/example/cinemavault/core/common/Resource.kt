package com.example.cinemavault.core.common

/**
 * A sealed class that encapsulates a resource with its loading, success, and error states.
 *
 * @param T The type of the data held by the resource.
 * @property data The data of the resource.
 * @property message A message associated with the resource, typically for errors.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    /**
     * Represents a successful state with data.
     *
     * @param T The type of the data.
     * @param data The successfully loaded data.
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Represents an error state with a message and optional data.
     *
     * @param T The type of the data.
     * @param message The error message.
     * @param data Optional data that might be available even in an error state (e.g., cached data).
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Represents a loading state.
     *
     * @param T The type of the data being loaded.
     * @property isLoading A boolean indicating if the resource is currently loading.
     */
    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(null)
}
