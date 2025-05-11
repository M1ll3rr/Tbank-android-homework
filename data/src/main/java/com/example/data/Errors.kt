package com.example.data

sealed class AppError(val errorCode: String) {
    object ConnectionError : AppError("error_connection")
    object TimeoutError : AppError("error_timeout")
    object Error400 : AppError("error_400")
    object Error403 : AppError("error_403")
    object Error404 : AppError("error_404")
    object Error500 : AppError("error_500")
    object Error502 : AppError("error_502")
    object Error503 : AppError("error_503")
    object UnknownError : AppError("error_unknown")

    object IdError : AppError("error_id")
    object EmptySearchError : AppError("error_empty_search")
}