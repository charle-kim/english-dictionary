package com.example.englishdictionary.model

data class TranslationResponse(
    val responseData: ResponseData?
)

data class ResponseData(
    val translatedText: String?
)
