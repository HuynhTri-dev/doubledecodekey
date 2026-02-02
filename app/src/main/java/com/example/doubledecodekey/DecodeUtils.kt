package com.example.doubledecodekey

import android.util.Base64
import java.nio.charset.StandardCharsets

object DecodeUtils {

    /**
     * Giải mã Base64URL (không có padding)
     * @param input Chuỗi Base64URL cần giải mã
     * @return Chuỗi đã được giải mã hoặc null nếu lỗi
     */
    fun decodeBase64Url(input: String): Result<String> {
        return try {
            // Thêm padding
            val paddedInput = when (input.length % 4) {
                2 -> "$input=="
                3 -> "$input="
                else -> input
            }
            
            // Chuyển đổi Base64URL sang Base64 chuẩn
            val base64Standard = paddedInput
                .replace('-', '+')
                .replace('_', '/')
            
            // Decode Base64
            val decodedBytes = Base64.decode(base64Standard, Base64.DEFAULT)
            Result.success(String(decodedBytes, StandardCharsets.UTF_8))
        } catch (e: Exception) {
            Result.failure(Exception("Invalid Base64URL: ${e.message}"))
        }
    }

    /**
     * Giải mã Caesar Cipher với shift cho trước
     * @param input Chuỗi ciphertext cần giải mã
     * @param shift Số bước dịch chuyển (mặc định là 3)
     * @return Chuỗi plaintext đã được giải mã
     */
    fun caesarDecode(input: String, shift: Int = 3): Result<String> {
        return try {
            val result = StringBuilder()
            
            for (char in input) {
                val decodedChar = when {
                    char.isUpperCase() -> {
                        // Xử lý chữ hoa (A-Z)
                        val shiftedValue = (char - 'A' - shift).mod(26)
                        ('A' + shiftedValue)
                    }
                    char.isLowerCase() -> {
                        // Xử lý chữ thường (a-z)
                        val shiftedValue = (char - 'a' - shift).mod(26)
                        ('a' + shiftedValue)
                    }
                    else -> {
                        // Giữ nguyên các ký tự không phải chữ cái
                        char
                    }
                }
                result.append(decodedChar)
            }
            
            Result.success(result.toString())
        } catch (e: Exception) {
            Result.failure(Exception("Caesar Decode Error: ${e.message}"))
        }
    }

    /**
     * Trích xuất thông tin từ câu hướng dẫn
     * Pattern: Base64URL-decode "xxx" to get cipherText; then Caesar-decode (shift=n) to get KEY
     * @param instruction Câu hướng dẫn
     * @return Pair<cipherText, shift> hoặc null nếu không parse được
     */
    fun parseInstruction(instruction: String): Result<Pair<String, Int>> {
        return try {
            // Regex để trích xuất chuỗi trong dấu ngoặc kép và giá trị shift
            val encodedTextRegex = "\"([^\"]+)\"".toRegex()
            val shiftRegex = "shift\\s*=\\s*(\\d+)".toRegex()
            
            val encodedTextMatch = encodedTextRegex.find(instruction)
            val shiftMatch = shiftRegex.find(instruction)
            
            if (encodedTextMatch == null) {
                return Result.failure(Exception("Wrong Format: Cannot find encoded text in quotes"))
            }
            
            if (shiftMatch == null) {
                return Result.failure(Exception("Wrong Format: Cannot find shift value"))
            }
            
            val encodedText = encodedTextMatch.groupValues[1]
            val shift = shiftMatch.groupValues[1].toInt()
            
            Result.success(Pair(encodedText, shift))
        } catch (e: Exception) {
            Result.failure(Exception("Parse Instruction Error: ${e.message}"))
        }
    }

    /**
     * Thực hiện toàn bộ quy trình giải mã Double Decode
     * @param input Chuỗi Base64URL đầu vào
     * @return DecodeResult chứa kết quả trung gian và KEY cuối cùng
     */
    fun performDoubleDecodeProcess(input: String): DecodeResult {
        // Decode Base64URL để lấy Instruction
        val instructionResult = decodeBase64Url(input.trim())
        if (instructionResult.isFailure) {
            return DecodeResult(
                success = false,
                error = instructionResult.exceptionOrNull()?.message ?: "Unknown error in Step 1"
            )
        }
        val instruction = instructionResult.getOrThrow()
        
        // Parse instruction để lấy encoded text và shift value
        val parseResult = parseInstruction(instruction)
        if (parseResult.isFailure) {
            return DecodeResult(
                success = false,
                instruction = instruction,
                error = parseResult.exceptionOrNull()?.message ?: "Unknown error in Step 2"
            )
        }
        val (encodedCipherText, shift) = parseResult.getOrThrow()
        
        // Decode Base64URL lần 2 để lấy cipherText
        val cipherTextResult = decodeBase64Url(encodedCipherText)
        if (cipherTextResult.isFailure) {
            return DecodeResult(
                success = false,
                instruction = instruction,
                error = "Failed to decode cipherText: ${cipherTextResult.exceptionOrNull()?.message}"
            )
        }
        val cipherText = cipherTextResult.getOrThrow()
        
        // Caesar decode để lấy KEY cuối cùng
        val keyResult = caesarDecode(cipherText, shift)
        if (keyResult.isFailure) {
            return DecodeResult(
                success = false,
                instruction = instruction,
                cipherText = cipherText,
                error = keyResult.exceptionOrNull()?.message ?: "Unknown error in Step 4"
            )
        }
        val finalKey = keyResult.getOrThrow()
        
        return DecodeResult(
            success = true,
            instruction = instruction,
            cipherText = cipherText,
            finalKey = finalKey.uppercase()
        )
    }
}

/**
 * Data class chứa kết quả giải mã
 */
data class DecodeResult(
    val success: Boolean,
    val instruction: String? = null,
    val cipherText: String? = null,
    val finalKey: String? = null,
    val error: String? = null
)
