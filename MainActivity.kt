package com.nayeemai.agent

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var output: TextView
    private lateinit var commandInput: EditText
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(0xFFF3F6FA.toInt())
        }

        val title = TextView(this).apply {
            text = "Nayeem AI"
            textSize = 28f
            setTextColor(0xFF12345A.toInt())
            gravity = Gravity.CENTER
        }
        root.addView(title, matchWrap())

        val subtitle = TextView(this).apply {
            text = "তোমার Personal Mobile Assistant"
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 20)
        }
        root.addView(subtitle, matchWrap())

        output = TextView(this).apply {
            text = "আসসালামু আলাইকুম! আমি Nayeem AI।\n\nতুমি লিখে অথবা মাইক্রোফোনে নির্দেশ দিতে পারো।"
            textSize = 17f
            setTextColor(0xFF202A35.toInt())
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFFFFFFFF.toInt())
        }
        val scroll = ScrollView(this)
        scroll.addView(output)
        root.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        commandInput = EditText(this).apply {
            hint = "যেমন: Facebook খোলো"
            textSize = 16f
            singleLine = true
        }
        root.addView(commandInput, matchWrap())

        val send = Button(this).apply {
            text = "নির্দেশ পাঠাও"
            setOnClickListener { handleCommand(commandInput.text.toString()) }
        }
        root.addView(send, matchWrap())

        val voice = Button(this).apply {
            text = "🎙 ভয়েস কমান্ড"
            setOnClickListener { startVoiceInput() }
        }
        root.addView(voice, matchWrap())

        setContentView(root)
    }

    private fun matchWrap() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private fun startVoiceInput() {
        if (!android.speech.SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "এই ফোনে Speech Recognition পাওয়া যাচ্ছে না।", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "আপনার নির্দেশ বলুন")
        }
        try {
            startActivityForResult(intent, 101)
        } catch (_: Exception) {
            Toast.makeText(this, "Voice input চালু করা যায়নি।", Toast.LENGTH_LONG).show()
        }
    }

    @Deprecated("Deprecated by Android; retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                commandInput.setText(spoken)
                handleCommand(spoken)
            }
        }
    }

    private fun handleCommand(raw: String) {
        val command = raw.trim().lowercase(Locale.ROOT)
        if (command.isBlank()) {
            reply("আগে একটি নির্দেশ লিখো অথবা বলো।")
            return
        }

        val targets = listOf(
            Triple(listOf("facebook", "ফেসবুক"), "com.facebook.katana", "Facebook"),
            Triple(listOf("whatsapp", "হোয়াটসঅ্যাপ", "হোয়াটসঅ্যাপ"), "com.whatsapp", "WhatsApp"),
            Triple(listOf("youtube", "ইউটিউব"), "com.google.android.youtube", "YouTube"),
            Triple(listOf("chrome", "ক্রোম", "browser", "ব্রাউজার"), "com.android.chrome", "Chrome"),
            Triple(listOf("settings", "সেটিংস"), "com.android.settings", "Settings"),
            Triple(listOf("phone", "dialer", "ফোন"), "com.google.android.dialer", "Phone"),
            Triple(listOf("camera", "ক্যামেরা"), "com.android.camera2", "Camera")
        )

        val target = targets.firstOrNull { item -> item.first.any { command.contains(it) } }
        if (target != null) {
            val launch = packageManager.getLaunchIntentForPackage(target.second)
            if (launch != null) {
                reply("${target.third} খুলছি।")
                startActivity(launch)
            } else {
                reply("${target.third} অ্যাপটি পাওয়া যায়নি অথবা এই ফোনে অন্য package ব্যবহার করছে।")
            }
            return
        }

        when {
            command.contains("সময়") || command.contains("time") ->
                reply("সময় দেখার জন্য ফোনের Clock অ্যাপ খুলে দাও।")
            command.contains("বন্ধ") || command.contains("off") ->
                reply("ফোনের Power বা Screen বন্ধ করার অনুমতি এই প্রথম সংস্করণে নেই।")
            else -> reply("আমি নির্দেশটি বুঝতে পারিনি। আপাতত Facebook, WhatsApp, YouTube, Chrome, Settings, Phone অথবা Camera খোলার নির্দেশ দিতে পারি।")
        }
    }

    private fun reply(message: String) {
        output.text = "তুমি: ${commandInput.text}\n\nNayeem AI: $message"
        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "nayeem_reply")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("bn", "BD")
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
