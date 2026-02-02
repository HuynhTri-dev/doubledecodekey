package com.example.doubledecodekey

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    
    // Chuỗi Base64URL đầu vào
    private val encodedInput = "QmFzZTY0VVJMLWRlY29kZSAiZVhodlptUngiIHRvIGdldCBjaXBoZXJUZXh0OyB0aGVuIENhZXNhci1kZWNvZGUgKHNoaWZ0PTMpIHRvIGdldCBLRVk"
    
    // Views
    private lateinit var tvInput: TextView
    private lateinit var tvInstruction: TextView
    private lateinit var tvCipherText: TextView
    private lateinit var tvFinalKey: TextView
    private lateinit var tvError: TextView
    private lateinit var btnNextStep: Button
    
    private lateinit var layoutInstruction: LinearLayout
    private lateinit var layoutCipher: LinearLayout
    private lateinit var layoutFinal: LinearLayout
    
    // Logic state
    private var currentStep = 0
    private var decodeResult: DecodeResult? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initViews()
        setupLogic()
    }
    
    private fun initViews() {
        tvInput = findViewById(R.id.tvInput)
        tvInstruction = findViewById(R.id.tvInstruction)
        tvCipherText = findViewById(R.id.tvCipherText)
        tvFinalKey = findViewById(R.id.tvFinalKey)
        tvError = findViewById(R.id.tvError)
        btnNextStep = findViewById(R.id.btnNextStep)
        
        layoutInstruction = findViewById(R.id.layoutInstruction)
        layoutCipher = findViewById(R.id.layoutCipher)
        layoutFinal = findViewById(R.id.layoutFinal)
        
        // Init Input
        tvInput.text = encodedInput
    }
    
    private fun setupLogic() {
        // Pre-calculate result
        decodeResult = DecodeUtils.performDoubleDecodeProcess(encodedInput)
        
        btnNextStep.setOnClickListener {
            handleNextStep()
        }
    }
    
    private fun handleNextStep() {
        val result = decodeResult ?: return
        
        currentStep++
        
        when (currentStep) {
            1 -> {
                // Show Instruction
                layoutInstruction.visibility = View.VISIBLE
                if (result.instruction != null) {
                    tvInstruction.text = result.instruction
                    btnNextStep.text = "Next: Decode Cipher"
                } else {
                    showError(result.error ?: "Failed to decode instruction")
                }
            }
            2 -> {
                // Show Cipher Text
                if (result.cipherText != null) {
                    layoutCipher.visibility = View.VISIBLE
                    tvCipherText.text = result.cipherText
                    btnNextStep.text = "Next: Final Key"
                } else {
                    showError(result.error ?: "Failed to get cipher text")
                }
            }
            3 -> {
                // Show Final Key
                if (result.finalKey != null) {
                    layoutFinal.visibility = View.VISIBLE
                    tvFinalKey.text = result.finalKey
                    btnNextStep.text = "Reset"
                    // Change button style or text to indicate done
                } else {
                    showError(result.error ?: "Failed to get final key")
                }
            }
            4 -> {
                // Reset
                resetProcess()
            }
        }
    }
    
    private fun showError(message: String) {
        tvError.visibility = View.VISIBLE
        tvError.text = "Error: $message"
        btnNextStep.text = "Reset"
        currentStep = 3 // Jump to end state effectively
    }
    
    private fun resetProcess() {
        currentStep = 0
        layoutInstruction.visibility = View.GONE
        layoutCipher.visibility = View.GONE
        layoutFinal.visibility = View.GONE
        tvError.visibility = View.GONE
        btnNextStep.text = "Start Decoding"
    }
}