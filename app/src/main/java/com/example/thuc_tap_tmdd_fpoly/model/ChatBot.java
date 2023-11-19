package com.example.thuc_tap_tmdd_fpoly.model;

import java.util.ArrayList;
import java.util.List;

public class ChatBot {
    public String getResponse(String question) {
        String response;

        // Xử lý logic của chat bot dựa trên câu hỏi
        if (question.contains("Xin chào")) {
            response = "Xin chào! Tôi có thể giúp gì cho bạn?";
        } else if (question.contains("Giới thiệu về app của bạn")) {
            response = "App Style là một app bán hàng quần áo, bao gồm áo quần và các phụ kiện đi kem như mũ giầy dép và balo,..., cách hoạt động app của chúng tôi rất dễ hiểu, tương dối giống với app Shopee khá là phổ biến trên thị trường hiện nay," +
                    " tuy nhiên tôi có biến tấu cải tiến thêm 1 chút đó là mọi người đều có thể nạp tiền bằng mã thẻ vào tài khoản, đăng sản phẩm của mình lên app và đồng thời cũng có thể tham khảo và mua sản phẩm của mọi người đăng bán, trong tương lai chúng tôi sẽ phát truyển và hoàn thiện app này cải tiến thêm nữa, Xin cảm ơn!!!";
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
        questions.add("Giới thiệu về app của bạn");
        questions.add("Tạm biệt");
        return questions;
    }
}
