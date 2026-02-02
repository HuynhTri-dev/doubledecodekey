# 🎯 PRACTICAL TASK: DOUBLE DECODE KEY

### 1. INPUT DATA (Dữ liệu đầu vào)

Đây là chuỗi `Base64URL` (không có đệm/padding) mà bạn cần xử lý:

```text
QmFzZTY0VVJMLWRlY29kZSAiZVhodlptUngiIHRvIGdldCBjaXBoZXJUZXh0OyB0aGVuIENhZXNhci1kZWNvZGUgKHNoaWZ0PTMpIHRvIGdldCBLRVk

```

_(Lưu ý: Copy toàn bộ chuỗi này, không bao gồm khoảng trắng thừa)_

---

### 2. PROVIDED ALGORITHM (Thuật toán)

Bạn cần thực hiện quy trình giải mã theo 3 bước tuần tự sau:

1. **Bước 1:** Dữ liệu `INPUT` ở trên đang ở dạng **Base64URL**.
2. **Bước 2:** Thực hiện giải mã (Decode) `INPUT` để nhận được một **"Câu hướng dẫn" (Instruction)**.
3. **Bước 3:** Đọc và làm theo chính xác "Câu hướng dẫn" đó để tìm ra **KEY** cuối cùng.

---

### 3. TASK INSTRUCTIONS (Yêu cầu lập trình)

Bạn cần xây dựng một ứng dụng Android bằng **Kotlin** để thực hiện:

1. Viết hàm giải mã tự động theo quy trình trên.
2. Thực thi chính xác các bước mô tả trong "Câu hướng dẫn" (sau khi giải mã được ở bước 2).
3. Hiển thị **Kết quả trung gian** (Instruction) lên màn hình.
4. Hiển thị **KEY cuối cùng** (Final Key) lên màn hình.

---

### 4. REQUIRED OUTPUT (Kết quả hiển thị bắt buộc)

Giao diện ứng dụng sau khi chạy phải hiển thị được ít nhất 2 dòng thông tin sau:

> **Intermediate Result (after decode):** `<Giá trị câu hướng dẫn>`
> **Decoded KEY (final):** `<Giá trị KEY cuối cùng>`

_(Nếu có lỗi xảy ra ở bất kỳ bước nào, hãy hiển thị rõ lý do: ví dụ "Invalid Base64", "Wrong Format", v.v...)_

---

### tóm tắt nhanh bằng tiếng Việt cho bạn dễ hiểu:

1. **Đầu vào:** Lấy chuỗi mã loằng ngoằng ở mục 1.
2. **Xử lý:**

- Đem chuỗi đó đi giải mã **Base64URL** -> Ra một câu tiếng Anh.
- Đọc câu tiếng Anh đó (nó sẽ bảo bạn lấy chữ gì đó, dịch chuyển bao nhiêu đơn vị theo mã Caesar).
- Làm theo nó để ra đáp án cuối.

3. **App:** Hiện lên màn hình câu tiếng Anh đó và cái Đáp án cuối cùng.
