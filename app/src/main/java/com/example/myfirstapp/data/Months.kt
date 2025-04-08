package com.example.myfirstapp.data

import android.content.Context
import com.example.myfirstapp.R

enum class Months(private val nameId: Int) {
    JANUARY(R.string.january),
    FEBRUARY(R.string.february),
    MARCH(R.string.march),
    APRIL(R.string.april),
    MAY(R.string.may),
    JUNE(R.string.june),
    JULY(R.string.july),
    AUGUST(R.string.august),
    SEPTEMBER(R.string.september),
    OCTOBER(R.string.october),
    NOVEMBER(R.string.november),
    DECEMBER(R.string.december);

    fun getLocalName(context: Context) = context.getString(nameId)

    companion object {
        fun getAllMonths(context: Context) = Months.entries.map { it.getLocalName(context) }
    }
}