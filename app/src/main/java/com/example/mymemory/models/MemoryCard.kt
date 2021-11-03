package com.example.mymemory.models

data class MemoryCard(
    val identifier: Int, //給定獨有id
    var isFaceUp: Boolean = false, //是否朝上
    var isMatched: Boolean = false//是否配對
)

