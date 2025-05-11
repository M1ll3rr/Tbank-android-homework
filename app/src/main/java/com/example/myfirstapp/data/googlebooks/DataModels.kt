package com.example.myfirstapp.data.googlebooks

data class GoogleBooksResponse(
    val items: List<GoogleBookItem>
)

data class GoogleBookItem(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val pageCount: Int?,
    val industryIdentifiers: List<IndustryIdentifier>?
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)