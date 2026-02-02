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
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    
    // Chuỗi Base64URL đầu vào
    private val encodedInput = "QmFzZTY0VVJMLWRlY29kZSAiZVhodlptUngiIHRvIGdldCBjaXBoZXJUZXh0OyB0aGVuIENhZXNhci1kZWNvZGUgKHNoaWZ0PTMpIHRvIGdldCBLRVk"
    
    // Views
    private lateinit var etInput: android.widget.EditText
    private lateinit var tvInstruction: TextView
    private lateinit var tvCipherText: TextView
    private lateinit var tvFinalKey: TextView
    private lateinit var tvError: TextView
    private lateinit var btnNextStep: Button
    
    private lateinit var layoutInstruction: LinearLayout
    private lateinit var layoutCipher: LinearLayout
    private lateinit var layoutFinal: LinearLayout
    
    // ViewModel
    private lateinit var viewModel: MainViewModel
    
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
        initViewModel()
        setupLogic()
    }
    
    private fun initViews() {
        etInput = findViewById(R.id.etInput)
        tvInstruction = findViewById(R.id.tvInstruction)
        tvCipherText = findViewById(R.id.tvCipherText)
        tvFinalKey = findViewById(R.id.tvFinalKey)
        tvError = findViewById(R.id.tvError)
        btnNextStep = findViewById(R.id.btnNextStep)
        
        layoutInstruction = findViewById(R.id.layoutInstruction)
        layoutCipher = findViewById(R.id.layoutCipher)
        layoutFinal = findViewById(R.id.layoutFinal)
        
        // Init Input if empty
        if (etInput.text.isEmpty()) {
            etInput.setText(encodedInput)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }
    
    private fun setupLogic() {
        // Observe State
        viewModel.currentStep.observe(this) { step ->
            updateUI(step)
        }

        btnNextStep.setOnClickListener {
            handleNextAction()
        }
    }
    
    private fun handleNextAction() {
        val currentStep = viewModel.currentStep.value ?: 0
        if (currentStep == 0) {
            val input = etInput.text.toString().trim()
            if (input.isEmpty()) {
                etInput.error = "Please enter code"
                return
            }
            viewModel.startDecoding(input)
        } else {
            viewModel.nextStep()
        }
    }
    
    private fun updateUI(step: Int) {
        val result = viewModel.decodeResult.value
        
        // Always clear error first
        tvError.visibility = View.GONE
        
        // Update visibility based on step
        if (step == 0) {
            layoutInstruction.visibility = View.GONE
            layoutCipher.visibility = View.GONE
            layoutFinal.visibility = View.GONE
            btnNextStep.text = "Start Decoding"
            return
        }
        
        // Because steps accumulate (1 shows instruction, 2 shows instr+cipher, etc.)
        // We can just check >= checks or use the when block to set everything.
        
        // Step 1+: Instruction
        if (step >= 1) {
             layoutInstruction.visibility = View.VISIBLE
             if (result?.instruction != null) {
                 tvInstruction.text = result.instruction
             } else {
                 if (step == 1) showError(result?.error ?: "Failed to decode instruction")
             }
        }
        
        // Step 2+: Cipher
        if (step >= 2) {
             if (result?.cipherText != null) {
                 layoutCipher.visibility = View.VISIBLE
                 tvCipherText.text = result.cipherText
             } else {
                 if (step == 2) showError(result?.error ?: "Failed to get cipher text")
             }
        }
        
        // Step 3: Final Key
        if (step >= 3) {
             if (result?.finalKey != null) {
                 layoutFinal.visibility = View.VISIBLE
                 tvFinalKey.text = result.finalKey
                 btnNextStep.text = "Reset"
             } else {
                 showError(result?.error ?: "Failed to get final key")
                 return // showError handles button text
             }
        }
        
        // Button Text Updates (if not error)
        when (step) {
            1 -> btnNextStep.text = "Next: Decode Cipher"
            2 -> btnNextStep.text = "Next: Final Key"
            3 -> btnNextStep.text = "Reset"
        }
    }
    
    private fun showError(message: String) {
        tvError.visibility = View.VISIBLE
        tvError.text = "Error: $message"
        btnNextStep.text = "Reset"
        // Ensure state is 3 so next click resets
        if (viewModel.currentStep.value != 3) {
            viewModel.jumpToErrorState()
        }
    }
}