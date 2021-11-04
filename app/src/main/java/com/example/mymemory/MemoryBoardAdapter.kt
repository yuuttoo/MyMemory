package com.example.mymemory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.models.BoardSize
import com.example.mymemory.models.MemoryCard
import kotlin.math.min

class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListener: CardClickListener
) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {//ViewHolder 掌握view

    companion object {
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter"
    }

    interface CardClickListener {
        fun onCardClicker(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {

        val cardWidth: Int = parent.width / boardSize.getWidth() - (2 * MARGIN_SIZE)//橫的2排 所以以螢幕寬度/2
        val carHeight: Int = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE) //高4格
        val cardSideLength: Int = min(cardWidth, carHeight)

        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams  = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }


    override fun getItemCount() = boardSize.numCards

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private  val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)

        fun bind(position: Int) {
            val memoryCard  = cards[position]
                //如果是faceUp就根據id顯示 不然就背景
                imageButton.setImageResource(if (memoryCard.isFaceUp) memoryCard.identifier else R.drawable.ic_launcher_background)

                imageButton.alpha = if (memoryCard.isMatched) .4f else 1.0f //imageButton如果配對成功顏色變淡
                val colorStateList = if (memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray) else null
                ViewCompat.setBackgroundTintList(imageButton, colorStateList)
                imageButton.setOnClickListener {
                    Log.i(TAG, "Click on position $position") //Log.i表示以info形式印在log
                    cardClickListener.onCardClicker(position)
                }


        }

    }

}
