package com.wolffo.train.ajaxclient6

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.util.Log
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import kotlinx.coroutines.*
import kotlin.concurrent.thread





class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener{

    private lateinit var textToSpeech: TextToSpeech

    private var genText : String = "default get text"
    private var translation : String = "default translation"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<Button>(R.id.buttonPush)
        val textView = findViewById<TextView>(R.id.textView1)

        button.setOnClickListener {
            textView.text = "Button was clicked!"
        }

        val buttonAjax = findViewById<Button>(R.id.buttonTestAjax)
        buttonAjax.setOnClickListener{testSendAjax()};

        textToSpeech = TextToSpeech(this, this)

        val buttonTestSpeak= findViewById<Button>(R.id.buttonTestTTS)
        buttonTestSpeak.setOnClickListener{speakOut("i am happy hope you're happy too")};
    }


    fun testSendAjax(){
        Log.d("Flow", "@testSendAjax()")
        val requestQueue = Volley.newRequestQueue(this)
        val textView = findViewById<TextView>(R.id.textView1)


        //val url = "http://10.0.2.2:3000/simpleCycle"
        val url = "https://lstream.onrender.com/simpleCycle"


        //const data = {lang:langInfo.langName, level:level, maxLen: maxLen};

        val jsonBody = JSONObject().apply {
            put("lang", "italian")
            put("level", "B1")
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                // Handle the successful response
                try {
                    Log.d("Flow", "Response: $response")
                    //val result = response.getString("result")
                    // Do something with the result
                    //val objResult = response.getJSONObject("result")
                    genText = response.getString("genText")
                    translation = response.getString("genText")
                    Log.d("flow", "getnText=$genText")
                    textView.text = genText
                    thread {
                        speakPart1()

                    }
                } catch (e: Exception) {
                    Log.e("api-err", e.toString())
                    e.printStackTrace()
                }
            },
            { error ->
                // Handle errors
                error.printStackTrace()
            }
        )
        Log.d("Flow", "sending it, i guess()")
        requestQueue.add(jsonObjectRequest)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set the language
            val result = textToSpeech.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle error
                println("Language is not supported")
            } else {
                // Speak out the text
                speakOut("Hello, this is a Text to Speech example.")
            }
        } else {
            // Initialization failed
            println("Initialization Failed!")
        }
    }




    private fun speakOut(text: String) {
        Log.d("Flow", "speeak out")
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun speakPart1(){
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // Called when speech starts
            }

            override fun onDone(utteranceId: String?) {
                // Called when speech is completed
                Log.d("flow", "Speech completed")
                thread {
                    speakPart2()
                }
            }

            override fun onError(utteranceId: String?) {
                // Called if an error occurs during speech
                println("Error during speech")
            }
        })

// Use the speak() function and pass a unique utterance ID
        textToSpeech.speak(genText, TextToSpeech.QUEUE_FLUSH, null, "utteranceID")
    }

    fun speakPart2(){
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // Called when speech starts
            }

            override fun onDone(utteranceId: String?) {
                // Called when speech is completed
                Log.d("flow","Speech part 2 completed")
                thread{
                    testSendAjax()
                }
            }

            override fun onError(utteranceId: String?) {
                // Called if an error occurs during speech
                println("Error during speech")
            }
    })

        textToSpeech.speak(translation, TextToSpeech.QUEUE_FLUSH, null, "utteranceID")
    }


    override fun onDestroy() {
        // Shutdown TTS when activity is destroyed
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

}