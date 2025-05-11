package com.example.myfirstapp.mappers

import android.content.Context
import com.example.data.mappers.AppError
import com.example.domain.types.ItemTypes
import com.example.domain.types.Months
import com.example.myfirstapp.R

class ResourceMapper(private val context: Context) {

    fun getItemTypeName(type: ItemTypes): String {
        return when (type) {
            ItemTypes.BOOK -> context.getString(R.string.book)
            ItemTypes.NEWSPAPER -> context.getString(R.string.newspaper)
            ItemTypes.DISK -> context.getString(R.string.disk)
        }
    }

    fun getItemTypeIcon(type: ItemTypes): Int {
        return when (type) {
            ItemTypes.BOOK -> R.drawable.bookicon
            ItemTypes.NEWSPAPER -> R.drawable.newspapericon
            ItemTypes.DISK -> R.drawable.diskicon
        }
    }

    fun getMonthName(month: Months): String {
        return when (month) {
            Months.JANUARY -> context.getString(R.string.january)
            Months.FEBRUARY -> context.getString(R.string.february)
            Months.MARCH -> context.getString(R.string.march)
            Months.APRIL -> context.getString(R.string.april)
            Months.MAY -> context.getString(R.string.may)
            Months.JUNE -> context.getString(R.string.june)
            Months.JULY -> context.getString(R.string.july)
            Months.AUGUST -> context.getString(R.string.august)
            Months.SEPTEMBER -> context.getString(R.string.september)
            Months.OCTOBER -> context.getString(R.string.october)
            Months.NOVEMBER -> context.getString(R.string.november)
            Months.DECEMBER -> context.getString(R.string.december)
        }
    }

    fun getItemIcon(itemType: ItemTypes): Int {
        return when (itemType) {
            ItemTypes.BOOK -> R.drawable.bookicon
            ItemTypes.NEWSPAPER -> R.drawable.newspapericon
            ItemTypes.DISK -> R.drawable.diskicon
        }
    }

    fun getErrorMessage(errorCode: String): String {
        return when (errorCode) {
            AppError.ConnectionError.errorCode -> context.getString(R.string.error_connection)
            AppError.TimeoutError.errorCode -> context.getString(R.string.error_timeout)
            AppError.Error400.errorCode -> context.getString(R.string.error_400)
            AppError.Error403.errorCode -> context.getString(R.string.error_403)
            AppError.Error404.errorCode -> context.getString(R.string.error_404)
            AppError.Error500.errorCode -> context.getString(R.string.error_500)
            AppError.Error502.errorCode -> context.getString(R.string.error_502)
            AppError.Error503.errorCode -> context.getString(R.string.error_503)
            AppError.IdError.errorCode -> context.getString(R.string.error_id)
            AppError.UnknownError.errorCode -> context.getString(R.string.error_unknown)
            AppError.EmptySearchError.errorCode -> context.getString(R.string.empty_search)
            else -> context.getString(R.string.error_unknown)
        }
    }
}