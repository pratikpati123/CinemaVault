package com.example.cinemavault.core.common

/**
 * A sealed class that encapsulates a resource with its success, and error states.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {

    class Success<T>(data: T) : Resource<T>(data)

    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

}