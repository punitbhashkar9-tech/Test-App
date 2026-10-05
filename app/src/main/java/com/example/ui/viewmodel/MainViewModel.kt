package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class ActiveTestSession(
    val test: TestEntity,
    val questions: List<QuestionEntity>,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, String> = emptyMap(), // questionId -> option ("A", "B", "C", "D")
    val markedForReview: Set<String> = emptySet(), // questionIds
    val visitedQuestions: Set<String> = emptySet(), // questionIds
    val remainingSeconds: Int = 0,
    val isFinished: Boolean = false,
    val resultAttempt: TestAttemptEntity? = null
)

data class PaymentCheckoutState(
    val productType: String = "TEST", // "TEST", "PDF", "SUBSCRIPTION"
    val productId: String = "",
    val productName: String = "",
    val originalPrice: Double = 0.0,
    val discount: Double = 0.0,
    val finalPrice: Double = 0.0,
    val couponCode: String = "",
    val couponMessage: String? = null,
    val isCouponApplied: Boolean = false,
    val utrNumber: String = "",
    val screenshotUri: String? = null,
    val isSubmitting: Boolean = false,
    val submittedPaymentId: String? = null
)

data class MainUiState(
    val selectedCategoryId: String = "all",
    val searchKeyword: String = "",
    val filterType: String = "ALL", // "ALL", "FREE", "PAID", "PDF", "SERIES"
    val activeSession: ActiveTestSession? = null,
    val checkoutState: PaymentCheckoutState? = null,
    val activeReaderPdf: PdfEntity? = null,
    val snackbarMessage: String? = null
)

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    val categories: StateFlow<List<CategoryEntity>> = repository.getActiveCategories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val publishedTests: StateFlow<List<TestEntity>> = repository.getPublishedTests()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allPdfs: StateFlow<List<PdfEntity>> = repository.getAllPdfs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val homeBanners: StateFlow<List<HomeBannerEntity>> = repository.getActiveBanners()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val subscriptionPlans: StateFlow<List<SubscriptionPlanEntity>> = repository.getActivePlans()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val appSettings: StateFlow<AppSettingsEntity?> = repository.getAppSettings()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun getUserPurchases(userId: String): Flow<List<PurchaseEntity>> {
        return repository.getPurchasesForUser(userId)
    }

    fun getUserPayments(userId: String): Flow<List<PaymentEntity>> {
        return repository.getPaymentsForUser(userId)
    }

    fun getUserAttempts(userId: String): Flow<List<TestAttemptEntity>> {
        return repository.getAttemptsForUser(userId)
    }

    fun isPurchased(userId: String, productId: String): Flow<Boolean> {
        return repository.isItemPurchased(userId, productId)
    }

    fun selectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setSearchKeyword(query: String) {
        _uiState.update { it.copy(searchKeyword = query) }
    }

    fun setFilterType(filter: String) {
        _uiState.update { it.copy(filterType = filter) }
    }

    fun showSnackbar(msg: String) {
        _uiState.update { it.copy(snackbarMessage = msg) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    // --- TEST TAKING ENGINE ---
    fun startTest(test: TestEntity, userId: String) {
        viewModelScope.launch {
            val questions = repository.getQuestionsForTestSync(test.id)
            if (questions.isEmpty()) {
                showSnackbar("इस टेस्ट में कोई प्रश्न उपलब्ध नहीं है (No questions in test)")
                return@launch
            }

            val totalSec = test.timeLimitMinutes * 60
            val session = ActiveTestSession(
                test = test,
                questions = questions,
                currentQuestionIndex = 0,
                selectedAnswers = emptyMap(),
                markedForReview = emptySet(),
                visitedQuestions = setOf(questions.first().id),
                remainingSeconds = totalSec,
                isFinished = false,
                resultAttempt = null
            )
            _uiState.update { it.copy(activeSession = session) }
            startTimer(userId)
        }
    }

    private fun startTimer(userId: String) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _uiState.value.activeSession ?: break
                if (current.isFinished) break

                val newSec = current.remainingSeconds - 1
                if (newSec <= 0) {
                    _uiState.update { it.copy(activeSession = current.copy(remainingSeconds = 0)) }
                    submitActiveTest(userId)
                    break
                } else {
                    _uiState.update { it.copy(activeSession = current.copy(remainingSeconds = newSec)) }
                }
            }
        }
    }

    fun selectOption(option: String) {
        val session = _uiState.value.activeSession ?: return
        val currentQ = session.questions.getOrNull(session.currentQuestionIndex) ?: return
        val updated = session.selectedAnswers.toMutableMap()
        updated[currentQ.id] = option
        _uiState.update { it.copy(activeSession = session.copy(selectedAnswers = updated)) }
    }

    fun clearOption() {
        val session = _uiState.value.activeSession ?: return
        val currentQ = session.questions.getOrNull(session.currentQuestionIndex) ?: return
        val updated = session.selectedAnswers.toMutableMap()
        updated.remove(currentQ.id)
        _uiState.update { it.copy(activeSession = session.copy(selectedAnswers = updated)) }
    }

    fun toggleMarkForReview() {
        val session = _uiState.value.activeSession ?: return
        val currentQ = session.questions.getOrNull(session.currentQuestionIndex) ?: return
        val currentMarks = session.markedForReview.toMutableSet()
        if (currentMarks.contains(currentQ.id)) {
            currentMarks.remove(currentQ.id)
        } else {
            currentMarks.add(currentQ.id)
        }
        _uiState.update { it.copy(activeSession = session.copy(markedForReview = currentMarks)) }
    }

    fun nextQuestion() {
        val session = _uiState.value.activeSession ?: return
        if (session.currentQuestionIndex < session.questions.lastIndex) {
            val nextIndex = session.currentQuestionIndex + 1
            val nextQ = session.questions[nextIndex]
            _uiState.update {
                it.copy(
                    activeSession = session.copy(
                        currentQuestionIndex = nextIndex,
                        visitedQuestions = session.visitedQuestions + nextQ.id
                    )
                )
            }
        }
    }

    fun prevQuestion() {
        val session = _uiState.value.activeSession ?: return
        if (session.currentQuestionIndex > 0) {
            val prevIndex = session.currentQuestionIndex - 1
            val prevQ = session.questions[prevIndex]
            _uiState.update {
                it.copy(
                    activeSession = session.copy(
                        currentQuestionIndex = prevIndex,
                        visitedQuestions = session.visitedQuestions + prevQ.id
                    )
                )
            }
        }
    }

    fun jumpToQuestion(index: Int) {
        val session = _uiState.value.activeSession ?: return
        if (index in session.questions.indices) {
            val targetQ = session.questions[index]
            _uiState.update {
                it.copy(
                    activeSession = session.copy(
                        currentQuestionIndex = index,
                        visitedQuestions = session.visitedQuestions + targetQ.id
                    )
                )
            }
        }
    }

    fun submitActiveTest(userId: String) {
        val session = _uiState.value.activeSession ?: return
        if (session.isFinished) return
        timerJob?.cancel()

        var correctCount = 0
        var wrongCount = 0
        var totalScore = 0.0

        for (q in session.questions) {
            val userSelected = session.selectedAnswers[q.id]
            if (userSelected != null) {
                if (userSelected.equals(q.correctOption, ignoreCase = true)) {
                    correctCount++
                    totalScore += q.marks
                } else {
                    wrongCount++
                    totalScore -= q.negativeMarks
                }
            }
        }

        val attemptedCount = session.selectedAnswers.size
        val unattemptedCount = session.questions.size - attemptedCount
        val totalMarks = session.test.totalMarks
        val percentage = if (totalMarks > 0) ((totalScore / totalMarks) * 100.0).coerceAtLeast(0.0) else 0.0
        val timeTaken = (session.test.timeLimitMinutes * 60) - session.remainingSeconds

        // Serialize user answers in simple json format
        val answersJson = session.selectedAnswers.entries.joinToString(separator = ",", prefix = "{", postfix = "}") {
            "\"${it.key}\":\"${it.value}\""
        }

        val attempt = TestAttemptEntity(
            id = "att_" + UUID.randomUUID().toString().take(8),
            testId = session.test.id,
            testTitle = session.test.title,
            userId = userId,
            score = totalScore,
            totalMarks = totalMarks,
            totalQuestions = session.questions.size,
            attempted = attemptedCount,
            correct = correctCount,
            wrong = wrongCount,
            unattempted = unattemptedCount,
            percentage = percentage,
            timeTakenSeconds = timeTaken.coerceAtLeast(1),
            completedAt = System.currentTimeMillis(),
            userAnswersJson = answersJson
        )

        viewModelScope.launch {
            repository.saveTestAttempt(attempt)
            _uiState.update {
                it.copy(
                    activeSession = session.copy(
                        isFinished = true,
                        resultAttempt = attempt
                    )
                )
            }
        }
    }

    fun exitTestSession() {
        timerJob?.cancel()
        _uiState.update { it.copy(activeSession = null) }
    }

    // --- PDF READER ---
    fun openPdfReader(pdf: PdfEntity) {
        _uiState.update { it.copy(activeReaderPdf = pdf) }
    }

    fun closePdfReader() {
        _uiState.update { it.copy(activeReaderPdf = null) }
    }

    // --- QR PAYMENT FLOW ---
    fun initiateCheckout(
        type: String, // "TEST", "PDF", "SUBSCRIPTION"
        id: String,
        name: String,
        price: Double
    ) {
        _uiState.update {
            it.copy(
                checkoutState = PaymentCheckoutState(
                    productType = type,
                    productId = id,
                    productName = name,
                    originalPrice = price,
                    discount = 0.0,
                    finalPrice = price,
                    couponCode = "",
                    couponMessage = null,
                    isCouponApplied = false,
                    utrNumber = "",
                    screenshotUri = null,
                    isSubmitting = false,
                    submittedPaymentId = null
                )
            )
        }
    }

    fun applyCoupon(code: String) {
        val checkout = _uiState.value.checkoutState ?: return
        if (code.isBlank()) return
        viewModelScope.launch {
            val (discount, error) = repository.applyCoupon(code, checkout.originalPrice)
            if (error != null) {
                _uiState.update {
                    it.copy(
                        checkoutState = checkout.copy(
                            couponMessage = error,
                            isCouponApplied = false
                        )
                    )
                }
            } else {
                val finalP = (checkout.originalPrice - discount).coerceAtLeast(0.0)
                _uiState.update {
                    it.copy(
                        checkoutState = checkout.copy(
                            couponCode = code.uppercase(),
                            discount = discount,
                            finalPrice = finalP,
                            couponMessage = "कूपन सफल! ₹$discount की छूट प्राप्त हुई",
                            isCouponApplied = true
                        )
                    )
                }
            }
        }
    }

    fun setUtrNumber(utr: String) {
        val checkout = _uiState.value.checkoutState ?: return
        _uiState.update { it.copy(checkoutState = checkout.copy(utrNumber = utr)) }
    }

    fun setScreenshot(uri: String?) {
        val checkout = _uiState.value.checkoutState ?: return
        _uiState.update { it.copy(checkoutState = checkout.copy(screenshotUri = uri)) }
    }

    fun submitPayment(user: UserEntity) {
        val checkout = _uiState.value.checkoutState ?: return
        if (checkout.utrNumber.trim().length < 6) {
            showSnackbar("कृपया कम से कम 6 से 12 अंकों का UTR / Transaction ID भरें")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(checkoutState = checkout.copy(isSubmitting = true)) }
            val paymentId = repository.submitPayment(
                userId = user.id,
                userName = user.name,
                userMobile = user.mobile,
                productId = checkout.productId,
                productType = checkout.productType,
                productName = checkout.productName,
                amount = checkout.finalPrice,
                utrNumber = checkout.utrNumber.trim(),
                screenshotUri = checkout.screenshotUri
            )
            _uiState.update {
                it.copy(
                    checkoutState = checkout.copy(
                        isSubmitting = false,
                        submittedPaymentId = paymentId
                    )
                )
            }
            showSnackbar("पेमेंट रिक्वेस्ट सफलतापूर्वक सबमिट हो गई! एडमिन वेरिफिकेशन के बाद अनलॉक हो जाएगा।")
        }
    }

    fun closeCheckout() {
        _uiState.update { it.copy(checkoutState = null) }
    }
}
