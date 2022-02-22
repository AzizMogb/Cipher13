package com.example.cipher13

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.*
import android.view.View
import android.widget.Toast
import android.text.TextWatcher
import android.text.Editable

class MainActivity : AppCompatActivity() {
    private var diacritics = false
    // text separators for saving in sharedPreferences to use globally
    // random text to split safely, it has to be something users won't normally write
    open val strBetween = "@!e2l^&*"
    open val strEnd = "&!1e3t##%"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // to not add the same text to history twice
        var lastCiphered = ""

        // saving basic data in the user's device
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("savedData", Context.MODE_PRIVATE)


        // cipher key
        cipherButton.setOnClickListener {
            // take input, process it in fun rot13, output the result
            var takenText  = enterText.text.toString()
            val resultText : String = rot13(takenText)
            takeText.setText(resultText)
            val sharedNameValue = sharedPreferences.getString("text", null)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            // saving input and output
            if(takenText != "" && !sharedNameValue.contains(takenText)) {
                lastCiphered = takenText
                editor.putString("text", "$takenText$strBetween$resultText$strEnd$sharedNameValue")
                editor.apply()
            }
        }

        // paste button bg change based on input text
        // (the other one is for when you click the button)
        enterText.addTextChangedListener(object : TextWatcher {
            // after the input box is changed
            override fun afterTextChanged(s: Editable) {
                // if input is empty, show paste button
                if (s.toString() == "") {
                    pasteButton.setBackgroundResource(R.drawable.ic_content_paste_black_24dp)
                }

                // show delete button otherwise
                else {
                    pasteButton.setBackgroundResource(R.drawable.ic_delete_24dp)
                }
            }

            // these two aren't used, but needed to avoid errors
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        // paste or delete input
        pasteButton.setOnClickListener {
            if (enterText.text.toString()=="") {
                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                enterText.setText(clipboardManager.primaryClip?.getItemAt(0)?.text)
                pasteButton.setBackgroundResource(R.drawable.ic_delete_24dp)
            } else {
                enterText.setText("")
                pasteButton.setBackgroundResource(R.drawable.ic_content_paste_black_24dp)
            }

        }

        // making copy button appear only if there is text to copy
        copyButton.visibility = View.GONE
        takeText.addTextChangedListener(object : TextWatcher {
            // after the output box is changed
            override fun afterTextChanged(s: Editable) {
                // if output is empty, don't show the button
                if (s.toString() == "") {
                    copyButton.visibility = View.GONE
                }
                // show it otherwise
                else {
                    copyButton.visibility = View.VISIBLE
                }
            }

            // these two aren't used, but needed to avoid errors
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        // copy the output
        copyButton.setOnClickListener {
                if (takeText.text.toString() != "") {
                    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("text", takeText.text)
                    clipboardManager.primaryClip = clipData
                    Toast.makeText(this, "Text copied!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "wa.. how did y.." +
                            "\nThere's no text to copy!", Toast.LENGTH_LONG).show()
                }
        }

        // go to history page
        toHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // harakat (diacritics) switch checking
        switchKey.setOnClickListener {
            diacritics = switchKey.isChecked
        }

    }


    // ciphering and deciphering algorithm
    private fun rot13(text:String):String{
        var charArray : CharArray = text.toCharArray()

        var letter  = 'x'
        for(i in 0 until text.length) {
            letter = charArray[i]

            // Arabic diacritics support on
            if (diacritics) {
                if (letter.toInt() in 1560..1610) {

                    if (letter.toInt() > 1585) {
                        letter -= 25
                    } else {
                        letter += 25
                    }
                }
            } else{ // diacritics support off
                if (letter.toInt() in 1575..1611) {

                    if (letter.toInt() > 1592) {
                        letter -= 18
                    } else {
                        letter += 18
                    }
                }
            }
            // English
            if (letter.toInt() in 'a'.toInt()..'z'.toInt()) {
                if (letter.toInt() > 'm'.toInt()) {
                    letter -= 13
                } else {
                    letter += 13
                }
            } else if (letter.toInt() in 'A'.toInt()..'Z'.toInt()) {
                if (letter.toInt() > 'M'.toInt()) {
                    letter -= 13
                } else {
                    letter += 13
                }
            }

            charArray[i] = letter
        }
        return String(charArray)
    }
}
