package com.mediscan.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.mediscan.app.core.constants.ApiEndpoints
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuthRepository — wraps all Firebase Auth operations.
 * Handles email/password login, Google Sign-In, registration,
 * Firestore user profile read/write, and sign-out.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    /** Sign in with email and password */
    suspend fun signInWithEmail(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            NetworkResult.Success(result.user!!)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Login failed")
        }
    }

    /** Register with email and password, then save user profile to Firestore */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        user: User,
    ): NetworkResult<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            // Save user profile to Firestore
            val userWithId = user.copy(id = firebaseUser.uid, email = email)
            saveUserToFirestore(userWithId)
            NetworkResult.Success(firebaseUser)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Sign up failed")
        }
    }

    /** Sign in with Google credential obtained from Google Sign-In flow */
    suspend fun signInWithGoogle(idToken: String): NetworkResult<Pair<FirebaseUser, Boolean>> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user!!
            val isNewUser = result.additionalUserInfo?.isNewUser ?: false
            NetworkResult.Success(Pair(firebaseUser, isNewUser))
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Google Sign-In failed")
        }
    }

    /** Save user profile to Firestore */
    suspend fun saveUserToFirestore(user: User) {
        firestore.collection(ApiEndpoints.USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .await()
    }

    /** Fetch user profile from Firestore */
    suspend fun getUserFromFirestore(uid: String): NetworkResult<User> {
        return try {
            val doc = firestore.collection(ApiEndpoints.USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            if (doc.exists()) {
                val user = doc.toObject(User::class.java)!!
                NetworkResult.Success(user)
            } else {
                NetworkResult.Error("User profile not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch user profile")
        }
    }

    /** Check if user profile exists in Firestore */
    suspend fun userProfileExists(uid: String): Boolean {
        return try {
            val doc = firestore.collection(ApiEndpoints.USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    /** Send password reset email */
    suspend fun sendPasswordResetEmail(email: String): NetworkResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to send reset email")
        }
    }

    /** Sign out current user */
    fun signOut() {
        auth.signOut()
    }
}
