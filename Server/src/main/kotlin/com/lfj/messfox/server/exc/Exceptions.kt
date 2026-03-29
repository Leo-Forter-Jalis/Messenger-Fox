package com.lfj.messfox.server.exc

import com.lfj.messfox.exceptions.MessFoxException

// Database Exceptions
open class DatabaseOperationException (override val message: String,
                          override val cause: Throwable? = null)
    : MessFoxException(message, cause)
// User
class UserAlreadyExistsException(override val message: String,
                                 override val cause: Throwable? = null)
    : DatabaseOperationException(message, cause)
class RegistrationFailedException(override val message: String,
                                  override val cause: Throwable? = null)
    : DatabaseOperationException(message, cause)
class AuthorizationFailedException(override val message: String,
                                   override val cause: Throwable? = null)
    : DatabaseOperationException(message, cause)
class FindUserOperationException(override val message: String,
                                 override val cause: Throwable? = null)
    : DatabaseOperationException(message, cause)



// Service


