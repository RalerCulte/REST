package com.lutalic.backend.exceptions

open class AppException(message: String? = null) : RuntimeException(message)

class PasswordMismatchException : AppException()

class AccountAlreadyExistsException : AppException()

class NoSuchUserException : AppException()