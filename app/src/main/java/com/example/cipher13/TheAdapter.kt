package com.example.cipher13

//import androidx.core.content.ContextCompat.getSystemService

import android.content.*
import android.content.Intent.getIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.items.view.*


class TheAdapter(private val context: Context, private val theList: List<ItemsData>) :
    RecyclerView.Adapter<TheAdapter.TheViewHolder>() {

    // TODO: Unresolved reference: getSystemService
    // to load data
    private val sharedPreferences: SharedPreferences =  context.getSharedPreferences(
        "savedData",
        Context.MODE_PRIVATE
    )
    private val sharedNameValue = sharedPreferences.getString("text", null)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TheViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.items, parent, false)
        return TheViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TheViewHolder, position: Int) {
        val currentItem = theList[position]
        holder.textViewOg.text = currentItem.textOg
        holder.textViewCiphered.text = currentItem.textCiphered

        holder.deleteButton.setOnClickListener {
            val newText = sharedNameValue.replace(
                "${holder.textViewOg.text}${MainActivity().strBetween}${holder.textViewCiphered.text}${MainActivity().strEnd}",
                ""
            )
            editor.putString("text", newText)
            editor.apply()
            Toast.makeText(context, "Card deleted!", Toast.LENGTH_LONG).show()
            startActivity(context, Intent(context, HistoryActivity::class.java), null)
        }

        holder.copyButton.setOnClickListener {
            if (holder.textViewCiphered.text.toString() != "") {
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", holder.textViewCiphered.text)
                clipboardManager.primaryClip = clipData
                Toast.makeText(context, "Card copied!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    context, "wa.. how did y.." +
                            "\nThere's no text to copy!", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun getItemCount() = theList.size

    class TheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewOg: TextView = itemView.text_og
        val textViewCiphered: TextView = itemView.text_ciphered
        val deleteButton: Button = itemView.deleteCell
        val copyButton: Button = itemView.copyCell
    }
}