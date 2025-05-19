package com.example.domain.types

enum class DiskTypes {
    CD,
    DVD;

    companion object {
        fun getAllDiskTypes() = DiskTypes.entries.map { it.name }
    }
}