package com.example.cipher13

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_history.*
// TODO add a delete button to the items, figure out a way to delete that
//  item's info from the array, you could then convert the array to a
//  string then update the sharedPreferences and the array after.
class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // to load data
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("savedData", Context.MODE_PRIVATE)
        val sharedNameValue = sharedPreferences.getString("text", null)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // transforming plain text history to an array
        // array of og, ciphered pairs, both in the same String
        val historyList = sharedNameValue.split(MainActivity().strEnd)

        // array of arrays, each inner array contains 2 strings, og in [0] and ciphered in [1]
        val itemList = arrayListOf<List<String>>()

        // splitting full strings from historyList to a pair of strings in an array,
        // then adding them, as an array, to the array of arrays itemList
        for (item in historyList) {
            itemList += item.split(MainActivity().strBetween)
        }

        // organizing the data in itemList to fit in the adapter
        val lastList = organizeData(itemList)

        // feeding the organized data to the adapter
        recyclerView.adapter = TheAdapter(this, lastList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)



        // go back to the main screen
        toMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        // delete history using alert dialog
        deleteHistory.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete History")
            builder.setMessage("Are you sure you want to delete your history?")

            // performing positive action
            builder.setPositiveButton("DELETE") { dialogInterface, which ->
                editor.putString("text", "")
                editor.apply()
                Toast.makeText(this, "History Deleted", Toast.LENGTH_LONG).show()
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            // performing cancel action
            builder.setNeutralButton("CANCEL") { dialogInterface, which ->
            }

            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            if (!sharedNameValue.isNullOrEmpty()) {
                alertDialog.show()
            } else {
                Toast.makeText(this, "No history to delete!", Toast.LENGTH_LONG).show()
            }
        }
    }

    // converting our data to a state that the adapter can take
    private fun organizeData(list: List<List<String>>): List<ItemsData> {
        val reList = ArrayList<ItemsData>()
        for (i in 0 until list.size - 1) {
            val og: String = list[i][0]
            val ciphered: String = list[i][1]
            val item = ItemsData(og, ciphered)
            reList += item
        }
        return reList
    }
}
