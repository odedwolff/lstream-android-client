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


class MainActivity : AppCompatActivity() {



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
                    val genText = response.getString("genText")
                    Log.d("flow", "getnText=$genText")
                    textView.text = genText
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

}