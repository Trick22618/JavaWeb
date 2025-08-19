package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/aichat")
public class AIChatServlet extends HttpServlet {

    // --- 建議：將這些設定移至設定檔 ---
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/chat";
    private static final String OLLAMA_MODEL = "qwen2.5:0.5b";
    // ------------------------------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/plain;charset=UTF-8"); // 回應純文字即可

        String userMessage = req.getParameter("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("錯誤：提問訊息不可為空。");
            return;
        }

        try {
            // --- 優化 1：使用 org.json 建構請求，更安全、更清晰 ---
            JSONObject messagePayload = new JSONObject();
            messagePayload.put("role", "user");
            messagePayload.put("content", userMessage);

            JSONArray messagesArray = new JSONArray();
            messagesArray.put(messagePayload);

            JSONObject rootPayload = new JSONObject();
            rootPayload.put("model", OLLAMA_MODEL);
            rootPayload.put("messages", messagesArray);
            rootPayload.put("stream", false);

            String payload = rootPayload.toString();

            // 建立連線
            URL url = new URL(OLLAMA_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 連線設定
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000); // 10秒連線超時
            conn.setReadTimeout(60000);   // 60秒讀取超時

            // 資料送出
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            // --- 優化 2：檢查 HTTP 回應狀態碼，進行錯誤處理 ---
            int responseCode = conn.getResponseCode();
            
            // 選擇讀取正常或錯誤的串流
            InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK) 
                                        ? conn.getInputStream() 
                                        : conn.getErrorStream();

            // 讀取回應
            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    responseBody.append(line);
                }
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // --- 優化 3：使用 org.json 解析回應，更穩健 ---
                JSONObject jsonResponse = new JSONObject(responseBody.toString());
                String aiContent = jsonResponse.getJSONObject("message").getString("content");
                
                // 印出結果
                resp.getWriter().write(aiContent);
            } else {
                // 如果 Ollama 服務回傳錯誤，將錯誤訊息回傳給前端
                resp.setStatus(responseCode);
                resp.getWriter().write("AI 服務端發生錯誤: " + responseBody.toString());
                // 也可以在伺服器端日誌中記錄錯誤
                System.err.println("Ollama Error: " + responseBody.toString());
            }

        } catch (Exception e) {
            // 處理其他可能的例外，例如網路問題、JSON 解析失敗等
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("處理請求時發生內部錯誤。");
            e.printStackTrace(); // 在伺服器控制台印出詳細錯誤
        }
    }
}
