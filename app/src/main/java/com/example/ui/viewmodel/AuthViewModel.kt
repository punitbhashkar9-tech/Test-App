package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserEntity
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AuthUiState(
    val currentUser: UserEntity? = null,
    val isOtpSent: Boolean = false,
    val generatedOtp: String = "1234",
    val pendingMobile: String = "",
    val pendingName: String = "",
    val pendingEmail: String = "",
    val isAdmin: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Auto-login with default demo user for seamless start, or can switch anytime
        viewModelScope.launch {
            repository.checkAndPrepopulate()
            val defaultUser = repository.getUserByMobile("9876543210")
            if (defaultUser != null) {
                _uiState.update { it.copy(currentUser = defaultUser) }
            }
        }
    }

    fun sendOtp(mobile: String, name: String, email: String) {
        val trimmedMobile = mobile.trim()
        if (trimmedMobile.length < 10) {
            _uiState.update { it.copy(errorMessage = "कृपया 10 अंकों का वैध मोबाइल नंबर दर्ज करें (Enter 10-digit mobile)") }
            return
        }

        val otp = "1234" // Quick simulation OTP for reliable testing
        _uiState.update {
            it.copy(
                isOtpSent = true,
                generatedOtp = otp,
                pendingMobile = trimmedMobile,
                pendingName = name.ifBlank { "User ${trimmedMobile.takeLast(4)}" },
                pendingEmail = email.trim(),
                errorMessage = null,
                infoMessage = "OTP भेजा गया है: $otp (डेमो हेतु स्वचालित भरा गया)"
            )
        }
    }

    fun verifyOtp(enteredOtp: String) {
        if (enteredOtp.trim() != _uiState.value.generatedOtp) {
            _uiState.update { it.copy(errorMessage = "गलत OTP दर्ज किया गया है (Invalid OTP)") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val existing = repository.getUserByMobile(_uiState.value.pendingMobile)
            val user = if (existing != null) {
                existing
            } else {
                val newUser = UserEntity(
                    id = "user_" + UUID.randomUUID().toString().take(8),
                    name = _uiState.value.pendingName,
                    mobile = _uiState.value.pendingMobile,
                    email = _uiState.value.pendingEmail,
                    role = "user"
                )
                repository.saveUser(newUser)
                newUser
            }

            _uiState.update {
                it.copy(
                    currentUser = user,
                    isOtpSent = false,
                    isAdmin = user.role == "admin",
                    isLoading = false,
                    infoMessage = "लॉगिन सफल (Login Successful)"
                )
            }
        }
    }

    fun adminLogin(idOrEmail: String, pass: String): Boolean {
        val trimmed = idOrEmail.trim()
        // Admin credentials check (default: admin@testapp.com / admin123 or mobile 9999999999)
        if ((trimmed == "admin@testapp.com" || trimmed == "9999999999" || trimmed.equals("admin", ignoreCase = true))
            && (pass == "admin123" || pass == "admin")) {
            viewModelScope.launch {
                val adminUser = repository.getUserByMobile("9999999999") ?: UserEntity(
                    id = "admin_master",
                    name = "Admin Master",
                    mobile = "9999999999",
                    email = "admin@testapp.com",
                    role = "admin",
                    subscriptionPlan = "Pro Max"
                )
                _uiState.update {
                    it.copy(
                        currentUser = adminUser,
                        isAdmin = true,
                        errorMessage = null,
                        infoMessage = "एडमिन पैनल में आपका स्वागत है"
                    )
                }
            }
            return true
        } else {
            _uiState.update { it.copy(errorMessage = "अमान्य एडमिन क्रेडेंशियल्स (Invalid Admin Credentials). Use: admin@testapp.com / admin123") }
            return false
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                currentUser = null,
                isAdmin = false,
                isOtpSent = false,
                infoMessage = "लॉगआउट सफल"
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }
}
