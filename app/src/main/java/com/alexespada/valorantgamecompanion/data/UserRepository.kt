package com.alexespada.valorantgamecompanion.data

import android.content.Context
import com.alexespada.valorantgamecompanion.models.User

class UserRepository(
    private val firestoreDataSource: UserFirestoreDataSource = UserFirestoreDataSource(),
    private val localDataSource: UserLocalDataSource = UserLocalDataSource()
) {
    fun getUsername(context: Context, userId: String, resultListener: ((String?) -> Unit)) {
        localDataSource.getUsername(context)?.let { username ->
            resultListener(username)
        } ?: run {
            firestoreDataSource.getUser(userId) { user: User? ->
                localDataSource.saveUsername(context, user?.username ?: "")
                resultListener(user?.username)
            }
        }
    }
}