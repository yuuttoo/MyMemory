package com.example.mymemory.models

enum class BoardSize(val numCards: Int) {//根據困難程度增加卡片張數
    EASY(8),
    MEDIUM(18),
    HARD(24);

    companion object {
        fun getByValue(value: Int) = values().first { it.numCards == value }
    }

    fun getWidth(): Int {//根據困難程度增加寬度（卡片較多）
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }

    fun getHeight() : Int { //高度為總卡片數量/寬度
        return numCards / getWidth()
    }

    fun getNumPairs(): Int {
        return numCards / 2
    }

}