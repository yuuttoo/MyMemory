package com.example.mymemory

import android.animation.ArgbEvaluator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.models.BoardSize
import com.example.mymemory.models.MemoryGame
import com.example.mymemory.utils.EXTRA_BOARD_SIZE
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity"
        private val CREATE_REQUEST_CODE = 248
    }
    private lateinit var clRoot: ConstraintLayout

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter

    private var boardSize: BoardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)

        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))

        //test purpose
        val intent = Intent(this, CreateActivity::class.java)
        intent.putExtra(EXTRA_BOARD_SIZE, BoardSize.EASY)//把這頁的資料傳過去
        startActivityForResult(intent, CREATE_REQUEST_CODE)
        //test purpose


        setupBoard()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()) {
                    showAlertDialog("Quit your current game?", null, View.OnClickListener {
                        setupBoard()
                    })
                } else {
                    //setup the game again
                    setupBoard()
                }

            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
            R.id.mi_custom -> {
                showCreationDialog()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCreationDialog() {//跟選擇難度的對話視窗類似
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        showAlertDialog("Create your own memory board", boardSizeView, View.OnClickListener {
            // set a new value for the board size
            val desiredBoardSize  = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            //導入新頁
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra(EXTRA_BOARD_SIZE, desiredBoardSize)//把這頁的資料傳過去
            startActivityForResult(intent, CREATE_REQUEST_CODE)

        })
    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (boardSize) {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose new size", boardSizeView, View.OnClickListener {
           // set a new value for the board size
           boardSize = when (radioGroupSize.checkedRadioButtonId) {
            R.id.rbEasy -> BoardSize.EASY
            R.id.rbMedium -> BoardSize.MEDIUM
            else -> BoardSize.HARD
           }
            setupBoard()
       })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") {_, _ -> //加上method
                positiveClickListener.onClick(null)
            }.show()
    }


    private fun setupBoard() {
        when (boardSize) {
            BoardSize.EASY -> {
                tvNumMoves.text = "Easy: 4 X 2"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "Medium: 6 X 3"
                tvNumPairs.text = "Pairs: 0 / 9"
            }
            BoardSize.HARD -> {
                tvNumMoves.text = "Hard: 6 X 4"
                tvNumPairs.text = "Pairs: 0 / 12"

            }
        }
        memoryGame = MemoryGame(boardSize)

        //adapter管理資料傳遞
        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener {
            override fun onCardClicker(position: Int) {
                updateGameWithFlip(position)
//                Log.i(TAG, "Card clicked $position")
            }
        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)//不因為資料太多而改變版面大小
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }



    private fun updateGameWithFlip(position: Int) {
        //error checking
        if (memoryGame.haveWonGame()) {
            //alert the user of an invalid move
            Snackbar.make(clRoot, "you already won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)) {
            //alert the user of an invalid move
            Snackbar.make(clRoot, "Invalid move!", Snackbar.LENGTH_SHORT).show()
            return
        }
        //卡片配對成功之後
        if (memoryGame.flipCard(position)) {
                Log.i(TAG, "Found a match! Num pairs found: ${memoryGame.numPairsFound}")
                val color = ArgbEvaluator().evaluate(
                    memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                    ContextCompat.getColor(this, R.color.color_progress_none),
                    ContextCompat.getColor(this, R.color.color_progress_full)
                ) as Int //color是int 類型
                tvNumPairs.setTextColor(color)

                tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
                if (memoryGame.haveWonGame()) {
                    Snackbar.make(clRoot, "You won! Congratulations.", Snackbar.LENGTH_LONG).show()
                }
            }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        //memoryGame.flipCard(position)
        adapter.notifyDataSetChanged()
    }
}