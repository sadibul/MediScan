package com.mediscan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.User
import com.mediscan.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AuthViewModel — manages all authentication state.
 * Used by SplashScreen, LoginScreen, and SignUpScreen.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    // Current authenticated Firebase user
    val currentUser: FirebaseUser? get() = authRepository.currentUser

    // Login state
    private val _loginState = MutableStateFlow<NetworkResult<FirebaseUser>>(NetworkResult.Idle)
    val loginState: StateFlow<NetworkResult<FirebaseUser>> = _loginState

    // Sign up state
    private val _signUpState = MutableStateFlow<NetworkResult<FirebaseUser>>(NetworkResult.Idle)
    val signUpState: StateFlow<NetworkResult<FirebaseUser>> = _signUpState

    // User profile state (fetched after login to determine role)
    private val _userProfileState = MutableStateFlow<NetworkResult<User>>(NetworkResult.Idle)
    val userProfileState: StateFlow<NetworkResult<User>> = _userProfileState

    // Password reset state
    private val _resetPasswordState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val resetPasswordState: StateFlow<NetworkResult<Unit>> = _resetPasswordState

    /** Sign in with email and password */
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = NetworkResult.Loading
            _loginState.value = authRepository.signInWithEmail(email, password)
        }
    }

    /** Register new user with email/password */
    fun signUpWithEmail(email: String, password: String, user: User) {
        viewModelScope.launch {
            _signUpState.value = NetworkResult.Loading
            _signUpState.value = authRepository.signUpWithEmail(email, password, user)
        }
    }

    /** Handle Google Sign-In with the ID token from Google */
    fun signInWithGoogle(idToken: String, onNewUser: () -> Unit, onExistingUser: (User) -> Unit) {
        viewModelScope.launch {
            _loginState.value = NetworkResult.Loading
            when (val result = authRepository.signInWithGoogle(idToken)) {
                is NetworkResult.Success -> {
                    val (firebaseUser, isNewUser) = result.data
                    if (isNewUser) {
                        // New Google user — needs role selection
                        _loginState.value = NetworkResult.Success(firebaseUser)
                        onNewUser()
                    } else {
                        // Existing user — fetch profile to get role
                        fetchUserProfile(firebaseUser.uid) { user ->
                            onExistingUser(user)
                        }
                    }
                }
                is NetworkResult.Error -> _loginState.value = result
                else -> {}
            }
        }
    }

    /** Save a Google user's profile to Firestore (called after role is selected) */
    fun saveGoogleUserProfile(user: User) {
        viewModelScope.launch {
            authRepository.saveUserToFirestore(user)
        }
    }

    /** Fetch user profile from Firestore to determine role */
    fun fetchUserProfile(uid: String, onSuccess: ((User) -> Unit)? = null) {
        viewModelScope.launch {
            _userProfileState.value = NetworkResult.Loading
            val result = authRepository.getUserFromFirestore(uid)
            _userProfileState.value = result
            if (result is NetworkResult.Success) {
                onSuccess?.invoke(result.data)
            }
        }
    }

    /** Send password reset email */
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _resetPasswordState.value = NetworkResult.Loading
            _resetPasswordState.value = authRepository.sendPasswordResetEmail(email)
        }
    }

    /** Sign out and reset all states */
    fun signOut() {
        authRepository.signOut()
        _loginState.value = NetworkResult.Idle
        _signUpState.value = NetworkResult.Idle
        _userProfileState.value = NetworkResult.Idle
    }

    /** Reset login state (e.g. when navigating away) */
    fun resetLoginState() {
        _loginState.value = NetworkResult.Idle
    }

    /** Reset sign up state */
    fun resetSignUpState() {
        _signUpState.value = NetworkResult.Idle
    }
}
