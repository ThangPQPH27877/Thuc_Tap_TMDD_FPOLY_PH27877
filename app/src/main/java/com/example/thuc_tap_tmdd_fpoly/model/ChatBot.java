package com.example.thuc_tap_tmdd_fpoly.model;

import java.util.ArrayList;
import java.util.List;

public class ChatBot {
    public String getResponse(String question) {
        String response;

        // Xử lý logic của chat bot dựa trên câu hỏi
        if (question.contains("Xin chào")) {
            response = "Xin chào! Tôi có thể giúp gì cho bạn?";
        } else if (question.contains("Thời tiết")) {
            response = "Hôm nay thời tiết rất đẹp!";
        } else if (question.contains("Tạm biệt")) {
            response = "Tạm biệt! Hãy quay lại nếu bạn cần thêm thông tin.";
        } else {
            response = "Xin lỗi, tôi không hiểu câu hỏi của bạn. Vui lòng hỏi một câu khác.";
        }

        return response;
    }

    public List<String> getQuestions() {
        List<String> questions = new ArrayList<>();
        questions.add("Xin chào");
        questions.add("Thời tiết");
        questions.add("Tạm biệt");
        return questions;
    }
}
