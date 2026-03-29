package com.lfj.messfox.server.service

import com.lfj.messfox.protocol.Response
import com.lfj.messfox.protocol.request.*
import com.lfj.messfox.protocol.response.*
import com.lfj.messfox.server.dao.UserDataTable
import java.time.Instant

class UserService (private val userDAO: UserDataTable) {
    fun registrationUser(request: RegisterRequest) : Response{
        return userDAO.registerUser(request.displayName, request.email, request.password).fold(
            onSuccess = { user ->
                RegisterResponse(request.requestId, user, Instant.now())
            }, onFailure = { exception -> ErrorResponse(request.requestId, exception, Instant.now()) }
        )
    }
    fun authorizationUser(request: AuthRequest) : Response{
        return userDAO.authorizationUser(request.email, request.password).fold(
            onSuccess = { user ->
                AuthResponse(request.requestId, user, Instant.now())
            }, onFailure = { exception -> ErrorResponse(request.requestId, exception, Instant.now()) }
        )
    }

    fun findUserById(request: FindUserByIdRequest) : Response{
        return userDAO.findUserById(request.userId).fold(
            onSuccess = { user ->
                FindUserByIdResponse(request.requestId, user, Instant.now())
            }, onFailure = { exception -> ErrorResponse(request.requestId, exception, Instant.now()) }
        )
    }

    fun findUserByUsername(request: FindUserByUsernameRequest) : Response{
        return userDAO.findUserByUserName(request.userName).fold(
            onSuccess = { users ->
                FindUserByUsernameResponse(request.requestId, users, Instant.now())
            }, onFailure = { exception -> ErrorResponse(request.requestId, exception, Instant.now()) }
        )
    }

    fun findUserByDisplayName(request: FindUserByDisplayNameRequest) : Response{
        return userDAO.findUserByDisplayName(request.displayName).fold(
            onSuccess = { users ->
                FindUserByDisplayNameResponse(request.requestId, users, Instant.now())
            }, onFailure = { exception -> ErrorResponse(request.requestId, exception, Instant.now()) }
        )
    }

}
