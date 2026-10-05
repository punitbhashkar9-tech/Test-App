package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class AdminDashboardStats(
    val totalUsers: Int = 0,
    val activeUsers: Int = 0,
    val totalTests: Int = 0,
    val totalPdfs: Int = 0,
    val totalPurchases: Int = 0,
    val pendingPayments: Int = 0,
    val approvedPayments: Int = 0,
    val rejectedPayments: Int = 0,
    val totalRevenue: Double = 0.0,
    val todayRevenue: Double = 0.0
)

data class AdminUiState(
    val stats: AdminDashboardStats = AdminDashboardStats(),
    val paymentFilter: String = "ALL", // "ALL", "PENDING", "APPROVED", "REJECTED"
    val selectedPayment: PaymentEntity? = null,
    val rejectReasonInput: String = "",
    val isRejectDialogOpen: Boolean = false,
    val isAddTestDialogOpen: Boolean = false,
    val isAddPdfDialogOpen: Boolean = false,
    val isAddCategoryDialogOpen: Boolean = false,
    val toastMessage: String? = null
)

class AdminViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    val allPayments: StateFlow<List<PaymentEntity>> = repository.getAllPayments()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allTests: StateFlow<List<TestEntity>> = repository.getAllTests()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allPdfs: StateFlow<List<PdfEntity>> = repository.getAllPdfs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allPurchases: StateFlow<List<PurchaseEntity>> = repository.getAllPurchases()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val appSettings: StateFlow<AppSettingsEntity?> = repository.getAppSettings()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        // Compute dashboard metrics dynamically from collections
        viewModelScope.launch {
            combine(
                allUsers,
                allTests,
                allPdfs,
                allPayments,
                allPurchases
            ) { users, tests, pdfs, payments, purchases ->
                val approved = payments.filter { it.status == "APPROVED" }
                val pending = payments.filter { it.status == "PENDING" }
                val rejected = payments.filter { it.status == "REJECTED" }
                val totalRev = approved.sumOf { it.amount }

                val startOfDay = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 3600 * 1000))
                val todayRev = approved.filter { it.reviewedAt ?: it.createdAt >= startOfDay }.sumOf { it.amount }

                AdminDashboardStats(
                    totalUsers = users.size,
                    activeUsers = users.count { !it.isBlocked },
                    totalTests = tests.size,
                    totalPdfs = pdfs.size,
                    totalPurchases = purchases.size,
                    pendingPayments = pending.size,
                    approvedPayments = approved.size,
                    rejectedPayments = rejected.size,
                    totalRevenue = totalRev,
                    todayRevenue = todayRev
                )
            }.collect { calculatedStats ->
                _uiState.update { it.copy(stats = calculatedStats) }
            }
        }
    }

    fun setPaymentFilter(filter: String) {
        _uiState.update { it.copy(paymentFilter = filter) }
    }

    fun openPaymentReview(payment: PaymentEntity) {
        _uiState.update { it.copy(selectedPayment = payment, rejectReasonInput = "") }
    }

    fun closePaymentReview() {
        _uiState.update { it.copy(selectedPayment = null, isRejectDialogOpen = false) }
    }

    fun approvePayment(paymentId: String) {
        viewModelScope.launch {
            repository.approvePayment(paymentId)
            _uiState.update {
                it.copy(
                    selectedPayment = null,
                    toastMessage = "पेमेंट स्वीकृत (Approved)! यूजर का कोर्स/टेस्ट अनलॉक हो गया।"
                )
            }
        }
    }

    fun promptReject(payment: PaymentEntity) {
        _uiState.update { it.copy(selectedPayment = payment, isRejectDialogOpen = true) }
    }

    fun setRejectReason(reason: String) {
        _uiState.update { it.copy(rejectReasonInput = reason) }
    }

    fun confirmReject() {
        val payment = _uiState.value.selectedPayment ?: return
        val reason = _uiState.value.rejectReasonInput.ifBlank { "अमान्य UTR अथवा पेमेंट बैंक में प्राप्त नहीं हुआ (Invalid UTR/Unconfirmed payment)" }
        viewModelScope.launch {
            repository.rejectPayment(payment.id, reason)
            _uiState.update {
                it.copy(
                    selectedPayment = null,
                    isRejectDialogOpen = false,
                    toastMessage = "पेमेंट अस्वीकृत कर दी गई।"
                )
            }
        }
    }

    fun toggleTestPublish(test: TestEntity) {
        viewModelScope.launch {
            repository.toggleTestPublish(test.id, !test.isPublished)
            _uiState.update {
                it.copy(toastMessage = if (!test.isPublished) "टेस्ट प्रकाशित किया गया (Published)" else "टेस्ट अप्रकाशित किया गया (Unpublished)") }
        }
    }

    fun deleteTest(testId: String) {
        viewModelScope.launch {
            repository.deleteTest(testId)
            _uiState.update { it.copy(toastMessage = "टेस्ट हटा दिया गया (Deleted)") }
        }
    }

    fun createQuickTest(
        title: String,
        categoryId: String,
        examName: String,
        subject: String,
        isFree: Boolean,
        price: Double,
        durationMinutes: Int
    ) {
        viewModelScope.launch {
            val testId = "test_" + UUID.randomUUID().toString().take(6)
            val test = TestEntity(
                id = testId,
                title = title,
                categoryId = categoryId,
                examName = examName,
                subject = subject,
                totalQuestions = 3,
                totalMarks = 3.0,
                timeLimitMinutes = durationMinutes,
                negativeMarking = 0.25,
                isFree = isFree,
                price = if (isFree) 0.0 else price,
                isPublished = true,
                description = "अभ्यास हेतु नया ऑनलाइन टेस्ट।"
            )
            repository.saveTest(test)

            // Add 3 sample standard questions
            val qList = listOf(
                QuestionEntity(
                    id = "q_${testId}_1",
                    testId = testId,
                    questionNumber = 1,
                    questionText = "भारत की प्रथम महिला राष्ट्रपति कौन थीं?",
                    optionA = "प्रतिभा देवीसिंह पाटिल",
                    optionB = "द्रौपदी मुर्मू",
                    optionC = "सरोजिनी नायडू",
                    optionD = "इंदिरा गांधी",
                    correctOption = "A",
                    explanation = "श्रीमती प्रतिभा देवीसिंह पाटिल भारत की 12वीं तथा प्रथम महिला राष्ट्रपति थीं (2007-2012)।",
                    marks = 1.0,
                    negativeMarks = 0.25
                ),
                QuestionEntity(
                    id = "q_${testId}_2",
                    testId = testId,
                    questionNumber = 2,
                    questionText = "भारतीय संविधान सभा के प्रारूप समिति के अध्यक्ष कौन थे?",
                    optionA = "डॉ. राजेंद्र प्रसाद",
                    optionB = "डॉ. भीमराव अम्बेडकर",
                    optionC = "जवाहरलाल नेहरू",
                    optionD = "सरदार पटेल",
                    correctOption = "B",
                    explanation = "संविधान सभा की प्रारूप समिति (Drafting Committee) के अध्यक्ष डॉ. भीमराव अम्बेडकर थे।",
                    marks = 1.0,
                    negativeMarks = 0.25
                ),
                QuestionEntity(
                    id = "q_${testId}_3",
                    testId = testId,
                    questionNumber = 3,
                    questionText = "कर्क रेखा भारत के कितने राज्यों से होकर गुजरती है?",
                    optionA = "6 राज्य",
                    optionB = "7 राज्य",
                    optionC = "8 राज्य",
                    optionD = "9 राज्य",
                    correctOption = "C",
                    explanation = "कर्क रेखा 8 राज्यों से गुजरती है: गुजरात, राजस्थान, म.प्र., छत्तीसगढ़, झारखण्ड, प. बंगाल, त्रिपुरा, मिजोरम।",
                    marks = 1.0,
                    negativeMarks = 0.25
                )
            )
            for (q in qList) {
                repository.saveQuestion(q)
            }
            _uiState.update { it.copy(isAddTestDialogOpen = false, toastMessage = "नया टेस्ट सफलतापूर्वक जोड़ा गया!") }
        }
    }

    fun createPdf(
        title: String,
        categoryId: String,
        examName: String,
        subject: String,
        description: String,
        price: Double,
        isFree: Boolean,
        pages: Int,
        content: String
    ) {
        viewModelScope.launch {
            val pdfId = "pdf_" + UUID.randomUUID().toString().take(6)
            val pdf = PdfEntity(
                id = pdfId,
                title = title,
                categoryId = categoryId,
                examName = examName,
                subject = subject,
                description = description,
                price = if (isFree) 0.0 else price,
                isFree = isFree,
                downloadAllowed = true,
                pageCount = pages,
                content = content.ifBlank { "# $title\n\n- महत्वपूर्ण अध्ययन सामग्री एवं बिंदु।" }
            )
            repository.savePdf(pdf)
            _uiState.update { it.copy(isAddPdfDialogOpen = false, toastMessage = "नया PDF नोट्स जोड़ा गया!") }
        }
    }

    fun deletePdf(id: String) {
        viewModelScope.launch {
            repository.deletePdf(id)
            _uiState.update { it.copy(toastMessage = "PDF हटा दिया गया") }
        }
    }

    fun toggleUserBlock(user: UserEntity) {
        viewModelScope.launch {
            repository.setUserBlocked(user.id, !user.isBlocked)
            _uiState.update {
                it.copy(toastMessage = if (!user.isBlocked) "यूजर को ब्लॉक कर दिया गया" else "यूजर को अनब्लॉक किया गया") }
        }
    }

    fun createCategory(name: String, icon: String) {
        viewModelScope.launch {
            val id = name.trim().lowercase().replace(" ", "_")
            val cat = CategoryEntity(id, name.trim(), icon, (allCategories.value.size + 1), true)
            repository.saveCategory(cat)
            _uiState.update { it.copy(isAddCategoryDialogOpen = false, toastMessage = "नई श्रेणी जोड़ी गई!") }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id)
            _uiState.update { it.copy(toastMessage = "श्रेणी हटा दी गई") }
        }
    }

    fun updateSettings(settings: AppSettingsEntity) {
        viewModelScope.launch {
            repository.updateAppSettings(settings)
            _uiState.update { it.copy(toastMessage = "सेटिंग्स अपडेट कर दी गई हैं!") }
        }
    }

    fun openAddTestDialog(open: Boolean) {
        _uiState.update { it.copy(isAddTestDialogOpen = open) }
    }

    fun openAddPdfDialog(open: Boolean) {
        _uiState.update { it.copy(isAddPdfDialogOpen = open) }
    }

    fun openAddCategoryDialog(open: Boolean) {
        _uiState.update { it.copy(isAddCategoryDialogOpen = open) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
