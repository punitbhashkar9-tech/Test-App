package com.example.data.db

import com.example.data.model.*

object PrepopulateData {
    val defaultCategories = listOf(
        CategoryEntity("ctet", "CTET", "school", 1, true),
        CategoryEntity("uptet", "UPTET", "menu_book", 2, true),
        CategoryEntity("up_police", "UP Police", "local_police", 3, true),
        CategoryEntity("ssc", "SSC", "assignment", 4, true),
        CategoryEntity("railway", "Railway", "train", 5, true),
        CategoryEntity("upsssc_pet", "UPSSSC PET", "psychology", 6, true),
        CategoryEntity("neet", "NEET", "medical_services", 7, true),
        CategoryEntity("upsc", "UPSC", "account_balance", 8, true),
        CategoryEntity("other", "Other Exams", "more_horiz", 9, true)
    )

    val defaultTests = listOf(
        TestEntity(
            id = "test_ctet_cdp_1",
            title = "CTET Teaching Aptitude & Pedagogy Practice 01",
            categoryId = "ctet",
            examName = "CTET",
            subject = "Pedagogy & Education",
            totalQuestions = 4,
            totalMarks = 4.0,
            timeLimitMinutes = 10,
            negativeMarking = 0.0,
            isFree = true,
            price = 0.0,
            isPublished = true,
            description = "शिक्षण विधियों और बाल मनोविज्ञान का अभ्यास सेट।"
        ),
        TestEntity(
            id = "test_up_police_hindi_1",
            title = "UP Police Constable सामान्य ज्ञान व भाषा सेट 01",
            categoryId = "up_police",
            examName = "UP Police",
            subject = "Language & GK",
            totalQuestions = 4,
            totalMarks = 8.0,
            timeLimitMinutes = 10,
            negativeMarking = 0.5,
            isFree = false,
            price = 49.0,
            isPublished = true,
            description = "पुलिस परीक्षा अभ्यास श्रृंखला का महत्वपूर्ण सेट।"
        ),
        TestEntity(
            id = "test_ssc_cgl_gk_1",
            title = "SSC General Reasoning & Awareness Mock 01",
            categoryId = "ssc",
            examName = "SSC",
            subject = "General Studies & Logic",
            totalQuestions = 4,
            totalMarks = 8.0,
            timeLimitMinutes = 8,
            negativeMarking = 0.5,
            isFree = false,
            price = 79.0,
            isPublished = true,
            description = "तार्किक क्षमता और सामान्य अध्ययन का अभ्यास।"
        ),
        TestEntity(
            id = "test_uptet_evs_1",
            title = "UPTET सामान्य पर्यावरण अध्ययन प्रैक्टिस",
            categoryId = "uptet",
            examName = "UPTET",
            subject = "Environmental Science",
            totalQuestions = 4,
            totalMarks = 4.0,
            timeLimitMinutes = 10,
            negativeMarking = 0.0,
            isFree = true,
            price = 0.0,
            isPublished = true,
            description = "पर्यावरण विज्ञान और पारिस्थितिकी का बुनियादी अभ्यास।"
        )
    )

    val defaultQuestions = listOf(
        // Questions for CTET
        QuestionEntity(
            id = "q_ctet_1",
            testId = "test_ctet_cdp_1",
            questionNumber = 1,
            questionText = "कक्षा शिक्षण में विद्यार्थियों की सक्रिय सहभागिता बढ़ाने के लिए सबसे प्रभावी तरीका कौन सा है?",
            optionA = "समूह परिचर्चा और परियोजना कार्य (Group discussion and project work)",
            optionB = "केवल व्याख्यान देना",
            optionC = "कठोर गृहकार्य देना",
            optionD = "रटकर याद कराना",
            correctOption = "A",
            explanation = "विद्यार्थी जब समूह परिचर्चा और गतिविधियों में भाग लेते हैं तो उनका अधिगम अधिक प्रभावी और स्थायी होता है।",
            marks = 1.0,
            negativeMarks = 0.0
        ),
        QuestionEntity(
            id = "q_ctet_2",
            testId = "test_ctet_cdp_1",
            questionNumber = 2,
            questionText = "सतत एवं व्यापक मूल्यांकन (CCE) का मुख्य उद्देश्य क्या होता है?",
            optionA = "छात्रों की सीखने की प्रक्रिया में सुधार और समग्र विकास का आकलन",
            optionB = "छात्रों को उत्तीर्ण या अनुत्तीर्ण घोषित करना",
            optionC = "केवल वार्षिक परीक्षा में अंक देना",
            optionD = "छात्रों में प्रतिस्पर्धा की भावना बढ़ाना",
            correctOption = "A",
            explanation = "CCE का प्राथमिक लक्ष्य विद्यार्थी के संज्ञानात्मक, भावात्मक और कौशलात्मक पक्षों का निरंतर मूल्यांकन करना है।",
            marks = 1.0,
            negativeMarks = 0.0
        ),
        QuestionEntity(
            id = "q_ctet_3",
            testId = "test_ctet_cdp_1",
            questionNumber = 3,
            questionText = "छात्रों में सृजनात्मकता (Creativity) को बढ़ावा देने के लिए किस प्रकार के प्रश्न पूछे जाने चाहिए?",
            optionA = "मुक्त अंत वाले प्रश्न (Open-ended questions)",
            optionB = "सत्य/असत्य वाले प्रश्न",
            optionC = "तथ्यात्मक वस्तुनिष्ठ प्रश्न",
            optionD = "एक शब्द के उत्तर वाले प्रश्न",
            correctOption = "A",
            explanation = "मुक्त अंत वाले प्रश्न बच्चों को कई दृष्टिकोणों से सोचने और नवाचारी समाधान खोजने का अवसर देते हैं।",
            marks = 1.0,
            negativeMarks = 0.0
        ),
        QuestionEntity(
            id = "q_ctet_4",
            testId = "test_ctet_cdp_1",
            questionNumber = 4,
            questionText = "एक समावेशी कक्षा में शिक्षक की सबसे महत्वपूर्ण भूमिका क्या होती है?",
            optionA = "प्रत्येक बच्चे की व्यक्तिगत सीखने की गति का सम्मान करना",
            optionB = "केवल उच्च अंक प्राप्त करने वाले छात्रों पर ध्यान देना",
            optionC = "कक्षा में सख्त अनुशासन बनाए रखना",
            optionD = "सभी बच्चों के लिए एक समान गति से पाठ्यक्रम पूरा कराना",
            correctOption = "A",
            explanation = "समावेशी शिक्षा में हर बच्चे की विविधता और सीखने की व्यक्तिगत गति को स्वीकार किया जाता है।",
            marks = 1.0,
            negativeMarks = 0.0
        ),

        // Questions for UP Police
        QuestionEntity(
            id = "q_up_1",
            testId = "test_up_police_hindi_1",
            questionNumber = 1,
            questionText = "'पवन' शब्द का सही संधि विच्छेद निम्न में से कौन सा है?",
            optionA = "पो + अन",
            optionB = "पौ + अन",
            optionC = "प + वन",
            optionD = "पा + वन",
            correctOption = "A",
            explanation = "पो + अन = पवन (अयादि स्वर संधि का नियम : ओ + अ = अव)।",
            marks = 2.0,
            negativeMarks = 0.5
        ),
        QuestionEntity(
            id = "q_up_2",
            testId = "test_up_police_hindi_1",
            questionNumber = 2,
            questionText = "भारतीय संविधान की 8वीं अनुसूची में कुल कितनी आधिकारिक भाषाएं शामिल हैं?",
            optionA = "18 भाषाएं",
            optionB = "20 भाषाएं",
            optionC = "22 भाषाएं",
            optionD = "24 भाषाएं",
            correctOption = "C",
            explanation = "वर्तमान में भारतीय संविधान की आठवीं अनुसूची में 22 भाषाएं मान्यता प्राप्त हैं।",
            marks = 2.0,
            negativeMarks = 0.5
        ),
        QuestionEntity(
            id = "q_up_3",
            testId = "test_up_police_hindi_1",
            questionNumber = 3,
            questionText = "भारत में राष्ट्रीय मतदाता दिवस (National Voters' Day) किस तारीख को मनाया जाता है?",
            optionA = "25 जनवरी",
            optionB = "26 जनवरी",
            optionC = "15 अगस्त",
            optionD = "2 अक्टूबर",
            correctOption = "A",
            explanation = "भारत निर्वाचन आयोग की स्थापना 25 जनवरी 1950 को हुई थी, इसी उपलक्ष्य में यह दिवस मनाया जाता है।",
            marks = 2.0,
            negativeMarks = 0.5
        ),
        QuestionEntity(
            id = "q_up_4",
            testId = "test_up_police_hindi_1",
            questionNumber = 4,
            questionText = "शुद्ध वर्तनी वाले शब्द का चयन कीजिए:",
            optionA = "उज्ज्वल",
            optionB = "उज्वल",
            optionC = "उज्जवल",
            optionD = "उज्व्वल",
            correctOption = "A",
            explanation = "सही और शुद्ध वर्तनी 'उज्ज्वल' (उत + ज्वल) है जिसमें दो बार आधा 'ज' आता है।",
            marks = 2.0,
            negativeMarks = 0.5
        ),

        // Questions for SSC
        QuestionEntity(
            id = "q_ssc_1",
            testId = "test_ssc_cgl_gk_1",
            questionNumber = 1,
            questionText = "Complete the series: 3, 7, 15, 31, ?",
            optionA = "63",
            optionB = "61",
            optionC = "58",
            optionD = "65",
            correctOption = "A",
            explanation = "Pattern: (x * 2) + 1. (3*2+1=7, 7*2+1=15, 15*2+1=31, 31*2+1=63).",
            marks = 2.0,
            negativeMarks = 0.5
        ),
        QuestionEntity(
            id = "q_ssc_2",
            testId = "test_ssc_cgl_gk_1",
            questionNumber = 2,
            questionText = "Which unit is used to measure electric resistance in SI system?",
            optionA = "Ohm",
            optionB = "Volt",
            optionC = "Ampere",
            optionD = "Watt",
            correctOption = "A",
            explanation = "Ohm is the standard unit of electrical resistance, named after Georg Simon Ohm.",
            marks = 2.0,
            negativeMarks = 0.5
        ),
        QuestionEntity(
            id = "q_ssc_3",
            testId = "test_ssc_cgl_gk_1",
            questionNumber = 3,
            questionText = "In computers, what is the full form of URL?",
            optionA = "Uniform Resource Locator",
            optionB = "Universal Resource Link",
            optionC = "Uniform Reference Language",
            optionD = "Unified Retrieval Locator",
            correctOption = "A",
            explanation = "URL stands for Uniform Resource Locator, which specifies addresses on the Internet.",
            marks = 2.0,
            negativeMarks = 0.5
        ),
        QuestionEntity(
            id = "q_ssc_4",
            testId = "test_ssc_cgl_gk_1",
            questionNumber = 4,
            questionText = "Which river is widely known as the 'Sorrow of Bengal' due to historic floods?",
            optionA = "Damodar River",
            optionB = "Hooghly River",
            optionC = "Kosi River",
            optionD = "Brahmaputra River",
            correctOption = "A",
            explanation = "The Damodar River was formerly known as the Sorrow of Bengal before modern canal and dam control projects.",
            marks = 2.0,
            negativeMarks = 0.5
        ),

        // Questions for UPTET EVS
        QuestionEntity(
            id = "q_evs_1",
            testId = "test_uptet_evs_1",
            questionNumber = 1,
            questionText = "वायुमंडल में सबसे प्रचुर मात्रा में पाई जाने वाली गैस कौन सी है?",
            optionA = "नाइट्रोजन (लगभग 78%)",
            optionB = "ऑक्सीजन (लगभग 21%)",
            optionC = "कार्बन डाइऑक्साइड",
            optionD = "आर्गन",
            correctOption = "A",
            explanation = "पृथ्वी के वायुमंडल में लगभग 78.08% नाइट्रोजन और 20.95% ऑक्सीजन गैस उपस्थित होती है।",
            marks = 1.0,
            negativeMarks = 0.0
        ),
        QuestionEntity(
            id = "q_evs_2",
            testId = "test_uptet_evs_1",
            questionNumber = 2,
            questionText = "पौधे प्रकाश संश्लेषण (Photosynthesis) प्रक्रिया में किस गैस का अवशोषण करते हैं?",
            optionA = "कार्बन डाइऑक्साइड (CO2)",
            optionB = "ऑक्सीजन (O2)",
            optionC = "नाइट्रोजन (N2)",
            optionD = "मीथेन (CH4)",
            correctOption = "A",
            explanation = "हरे पौधे सूर्य के प्रकाश में जल और कार्बन डाइऑक्साइड का उपयोग करके ग्लूकोज और ऑक्सीजन बनाते हैं।",
            marks = 1.0,
            negativeMarks = 0.0
        ),
        QuestionEntity(
            id = "q_evs_3",
            testId = "test_uptet_evs_1",
            questionNumber = 3,
            questionText = "विश्व पर्यावरण दिवस प्रतिवर्ष किस तिथि को मनाया जाता है?",
            optionA = "5 जून",
            optionB = "22 अप्रैल",
            optionC = "11 जुलाई",
            optionD = "16 सितम्बर",
            correctOption = "A",
            explanation = "संयुक्त राष्ट्र द्वारा पर्यावरण जागरूकता बढ़ाने हेतु हर वर्ष 5 जून को विश्व पर्यावरण दिवस मनाया जाता है।",
            marks = 1.0,
            negativeMarks = 0.0
        ),
        QuestionEntity(
            id = "q_evs_4",
            testId = "test_uptet_evs_1",
            questionNumber = 4,
            questionText = "किसी खाद्य श्रृंखला (Food Chain) में उत्पादक (Producer) का कार्य कौन करते हैं?",
            optionA = "हरे पौधे (स्वपोषी)",
            optionB = "शाकाहारी जंतु",
            optionC = "मांसाहारी जीव",
            optionD = "अपघटक",
            correctOption = "A",
            explanation = "हरे पौधे सौर ऊर्जा को रासायनिक ऊर्जा में बदलकर स्वयं भोजन का निर्माण करते हैं, इसलिए इन्हें प्राथमिक उत्पादक कहते हैं।",
            marks = 1.0,
            negativeMarks = 0.0
        )
    )

    val defaultPdfs = listOf(
        PdfEntity(
            id = "pdf_ctet_cdp",
            title = "CTET Teaching Pedagogy Master Study Notes",
            categoryId = "ctet",
            examName = "CTET Paper 1 & 2",
            subject = "Educational Psychology",
            description = "बाल विकास, शिक्षण अधिगम प्रक्रिया और समावेशी शिक्षा के आवश्यक बिंदु।",
            price = 49.0,
            isFree = false,
            downloadAllowed = true,
            pageCount = 48,
            content = """
# बाल विकास एवं शिक्षण शास्त्र अध्ययन सारांश

## 1. वृद्धि एवं विकास की मूल अवधारणा
- वृद्धि शारीरिक परिवर्तनों जैसे भार, ऊंचाई एवं आकार में बढ़ोतरी को दर्शाती है।
- विकास एक सतत और व्यापक प्रक्रिया है जो आजीवन चलती है।
- विकास के मुख्य आयाम: शारीरिक, मानसिक (संज्ञानात्मक), सामाजिक और संवेगात्मक।

---

## 2. शिक्षण सूत्र एवं सिद्धांत
- सरल से जटिल की ओर (Simple to Complex)
- ज्ञात से अज्ञात की ओर (Known to Unknown)
- मूर्त से अमूर्त की ओर (Concrete to Abstract)
- पूर्ण से अंश की ओर (Whole to Part)

---

## 3. समावेशी शिक्षा के मुख्य बिंदु
- प्रत्येक बच्चे की सीखने की व्यक्तिगत गति का सम्मान करना।
- बिना किसी भेदभाव के सभी विद्यार्थियों को समान शैक्षणिक अवसर प्रदान करना।
- कक्षा में विविध शिक्षण सहायक सामग्री (TLM) का उपयोग।
            """.trimIndent()
        ),
        PdfEntity(
            id = "pdf_up_police_gk",
            title = "UP Police Exam General Awareness Notes",
            categoryId = "up_police",
            examName = "UP Police",
            subject = "General Knowledge",
            description = "भारतीय संविधान, सामान्य विज्ञान और राज्य सामान्य ज्ञान का संकलन।",
            price = 49.0,
            isFree = false,
            downloadAllowed = true,
            pageCount = 62,
            content = """
# सामान्य ज्ञान एवं अध्ययन गाइड

## 1. भारतीय संविधान के मूल तत्व
- संविधान सभा की पहली बैठक 9 दिसंबर 1946 को हुई थी।
- संविधान 26 जनवरी 1950 को पूरे देश में लागू हुआ।
- भारतीय संविधान में मौलिक अधिकारों का वर्णन भाग 3 में है।

---

## 2. प्रमुख राष्ट्रीय प्रतीक
- राष्ट्रीय ध्वज: तिरंगा (लंबाई और चौड़ाई का अनुपात 3:2)
- राष्ट्रीय चिन्ह: अशोक का सिंह स्तंभ (सारनाथ)
- राष्ट्रीय गान: जन गण मन (रवीन्द्रनाथ टैगोर)
- राष्ट्रीय गीत: वन्दे मातरम् (बंकिम चन्द्र चटर्जी)
            """.trimIndent()
        ),
        PdfEntity(
            id = "pdf_uptet_evs_free",
            title = "Environmental Studies Quick Revision Guide (Free)",
            categoryId = "uptet",
            examName = "UPTET Primary",
            subject = "Environmental Science",
            description = "पर्यावरण संरक्षण, पारिस्थितिकी तंत्र और ऊर्जा स्रोतों का सरल सारांश।",
            price = 0.0,
            isFree = true,
            downloadAllowed = true,
            pageCount = 24,
            content = """
# पर्यावरण अध्ययन त्वरित पुनरावलोकन

## 1. पारितंत्र के घटक
- **जैविक घटक (Biotic):** उत्पादक (पौधे), उपभोक्ता (जंतु), अपघटक (जीवाणु, कवक)।
- **अजैविक घटक (Abiotic):** जल, वायु, मृदा, सूर्य का प्रकाश, तापमान।

---

## 2. ऊर्जा के स्रोत
- **नवीकरणीय ऊर्जा (Renewable):** सौर ऊर्जा, पवन ऊर्जा, जल ऊर्जा, बायोमास।
- **अनवीकरणीय ऊर्जा (Non-renewable):** कोयला, पेट्रोलियम, प्राकृतिक गैस।

---

## 3. प्रमुख पर्यावरण दिवस
- 22 मार्च: विश्व जल दिवस
- 22 अप्रैल: पृथ्वी दिवस
- 5 जून: विश्व पर्यावरण दिवस
- 16 सितम्बर: विश्व ओजोन दिवस
            """.trimIndent()
        )
    )

    val defaultSubscriptionPlans = listOf(
        SubscriptionPlanEntity(
            id = "plan_free",
            name = "Free Plan",
            price = 0.0,
            durationDays = 365,
            features = "Free Tests Access, Free Study Notes, Basic Result Analysis, Leaderboard",
            isActive = true
        ),
        SubscriptionPlanEntity(
            id = "plan_pro",
            name = "Pro Plan",
            price = 199.0,
            durationDays = 90,
            features = "All Paid Tests Unlocked, 50+ Test Series, Detailed Solution Analytics, All India Rank Predictor, Ad-Free Experience",
            isActive = true
        ),
        SubscriptionPlanEntity(
            id = "plan_pro_max",
            name = "Pro Max Plan",
            price = 499.0,
            durationDays = 365,
            features = "Everything in Pro, All Premium Study PDF Notes, Offline PDF Download, Previous Papers, 24x7 Priority Support",
            isActive = true
        )
    )

    val defaultCoupons = listOf(
        CouponEntity(
            id = "coupon_welcome50",
            code = "WELCOME50",
            discountPercent = 50.0,
            maxDiscount = 100.0,
            minPurchase = 39.0,
            isActive = true
        ),
        CouponEntity(
            id = "coupon_first20",
            code = "FIRST20",
            discountPercent = 20.0,
            maxDiscount = 50.0,
            minPurchase = 29.0,
            isActive = true
        )
    )

    val defaultBanners = listOf(
        HomeBannerEntity(
            id = "banner_1",
            title = "Police & Teaching Exam Test Series 2025",
            subtitle = "अभ्यास सेट और मॉडल टेस्ट पेपर उपलब्ध हैं",
            tag = "TRENDING",
            actionType = "CATEGORY",
            actionTarget = "up_police",
            displayOrder = 1,
            isActive = true
        ),
        HomeBannerEntity(
            id = "banner_2",
            title = "CTET & State TET Pedagogy Notes",
            subtitle = "कोड WELCOME50 लगाएं और 50% छूट पाएं",
            tag = "HOT OFFER",
            actionType = "PDF",
            actionTarget = "pdf_ctet_cdp",
            displayOrder = 2,
            isActive = true
        ),
        HomeBannerEntity(
            id = "banner_3",
            title = "Pro Max 1-Year Membership",
            subtitle = "सभी टेस्ट सीरीज और पीडीएफ नोट्स एक साथ अनलॉक करें",
            tag = "BEST VALUE",
            actionType = "SUBSCRIPTION",
            actionTarget = "plan_pro_max",
            displayOrder = 3,
            isActive = true
        )
    )

    val defaultSettings = AppSettingsEntity(
        id = "default",
        appName = "Test App",
        supportPhone = "+91 98765 43210",
        supportEmail = "support@testapp.in",
        supportWhatsapp = "+91 98765 43210",
        upiId = "testapp@okaxis",
        upiReceiverName = "Test App Online Prep",
        paymentInstructions = "1. दिए गए QR कोड को किसी भी UPI ऐप (GPay, PhonePe, Paytm) से स्कैन करें।\n2. पेमेंट करने के पश्चात 12-अंकों का UTR / Transaction No. दर्ज करें।\n3. पेमेंट का स्क्रीनशॉट संलग्न कर सबमिट करें।\n4. एडमिन वेरिफिकेशन के बाद सामग्री स्वतः अनलॉक हो जाएगी।",
        noticeText = "📢 स्वागत है Test App में! नए टेस्ट सीरीज और ई-बुक्स नोट्स उपलब्ध हैं।",
        privacyPolicy = "आपकी व्यक्तिगत जानकारी व टेस्ट डेटा सुरक्षित है।",
        termsAndConditions = "सभी अध्ययन सामग्री एवं टेस्ट केवल निजी अध्ययन के लिए हैं।",
        refundPolicy = "पेमेंट कटने पर किसी भी समस्या में 48 घंटे में समाधान किया जाएगा।"
    )

    val defaultUsers = listOf(
        UserEntity(
            id = "user_demo_1",
            name = "राहुल शर्मा",
            mobile = "9876543210",
            email = "rahul@testapp.com",
            role = "user",
            subscriptionPlan = "Free"
        ),
        UserEntity(
            id = "admin_master",
            name = "Admin Master",
            mobile = "9999999999",
            email = "admin@testapp.com",
            role = "admin",
            subscriptionPlan = "Pro Max"
        )
    )
}
