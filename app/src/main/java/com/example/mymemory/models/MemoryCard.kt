package com.example.mymemory.models

data class MemoryCard(
    val identifier: Int, //給定獨有id
    val imageUrl: String? = null,
    var isFaceUp: Boolean = false, //是否朝上
    var isMatched: Boolean = false//是否配對
)

