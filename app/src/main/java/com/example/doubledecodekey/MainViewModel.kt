package com.example.doubledecodekey

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _currentStep = MutableLiveData(0)
    val currentStep: LiveData<Int> = _currentStep

    private val _decodeResult = MutableLiveData<DecodeResult?>(null)
    val decodeResult: LiveData<DecodeResult?> = _decodeResult

    /**
     * Called when the user clicks 'Start Decoding' (Step 0).
     */
    fun startDecoding(input: String) {
        val result = DecodeUtils.performDoubleDecodeProcess(input)
        _decodeResult.value = result
        // Move to the first result step
        _currentStep.value = 1
    }

    /**
     * Called to advance to the next step.
     */
    fun nextStep() {
        val current = _currentStep.value ?: 0
        // Steps: 1 (Instruction) -> 2 (Cipher) -> 3 (Final) -> Next is Reset (0)
        if (current >= 3) {
            reset()
        } else {
            _currentStep.value = current + 1
        }
    }

    /**
     * Resets the process to the beginning.
     */
    fun reset() {
        _currentStep.value = 0
        _decodeResult.value = null
    }

    /**
     * If an error occurs, we can jump to the error state (similar to the final step but with error).
     */
    fun jumpToErrorState() {
        _currentStep.value = 3
    }
}
