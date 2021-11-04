package com.example.mymemory.models

import com.example.mymemory.utils.DEFAULT_ICONS

class MemoryGame (private val boardSize: BoardSize) {


    val cards: List<MemoryCard>
    var numPairsFound = 0

    private var numCardFlips = 0 //每次翻牌就計算次數

    private var indexOfSingleSelectedCard: Int? = null

    init {
        val chosenImages: List<Int> = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages: List<Int> = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCard(it) }
    }

    fun flipCard(position: Int) : Boolean {
        numCardFlips++
        val card = cards[position]
        // three cases:
        // 0 cards previously flipped over => restore cards + flip over the selected card
        // 1 cards previously flipped over -> flip over the selected card + check if the images match
        // 2 cards previously flipped over => restore cards + flip over the selected card
        //可以簡化成２個
        // 0 cards previously flipped over => restore cards(上一次沒有存的話就不用恢復蓋牌） + restore cards + flip over the selected card
        // 1 cards previously flipped over -> flip over the selected card + check if the images match
        var foundMatch = false
        if (indexOfSingleSelectedCard == null) {
            //表示剛剛沒有翻卡 或剛翻完2張卡片
            restoreCards()
            indexOfSingleSelectedCard = position //存入目前翻起來的卡
        } else {
            //剛剛有翻一張牌 用目前的position跟前面那張存取的比對 檢查配對是否成功
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)//!!可以為空
            indexOfSingleSelectedCard = null

        }
        card.isFaceUp = !card.isFaceUp
        return  foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        //兩張卡id不相同 配對失敗
        if (cards[position1].identifier != cards[position2].identifier) {
            return false
        } //配對成功
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }


    private fun restoreCards() {//卡片恢復蓋牌
       for (card in cards) {
           if (!card.isMatched) {//配對失敗
               card.isFaceUp = false //恢復蓋牌
           }
       }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardFlips / 2 //翻２張算是一次move
    }


}