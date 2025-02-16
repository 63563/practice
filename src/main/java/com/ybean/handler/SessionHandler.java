package com.ybean.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ybean.constant.HttpMethodConstant;
import com.ybean.constant.MessageConstant;
import com.ybean.manager.SessionManager;
import com.ybean.utils.ResponseUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SessionHandler implements HttpHandler {

    private final SessionManager sessionManager;

    public SessionHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 校验请求方式是否正确
        String method = exchange.getRequestMethod();
        if (!method.equalsIgnoreCase(HttpMethodConstant.GET_METHOD)) {
            ResponseUtil.sendResponse(exchange, 405, MessageConstant.METHOD_ERROR_MESSAGE);
        }

        // 设置响应编码为UTF-8
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");

        try {
            // 获取请求的 URI 并提取路径部分
            String path = exchange.getRequestURI().getPath();
            String[] pathSegments = path.split("/");

            // 确保至少有一个有效的路径段（即 session id）
            if (pathSegments.length < 2 || !pathSegments[1].matches("\\d+")) {
                sendErrorResponse(exchange, 400, "Invalid request: missing or invalid session ID.");
                return;
            }

            // 解析用户ID并获取对应的session key
            int userId = Integer.parseInt(pathSegments[1]);
            String sessionKey = sessionManager.getSession(userId);

            // 发送成功响应
            byte[] responseBytes = sessionKey.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } catch (Exception e) {
            // 捕获所有异常并返回500错误
            sendErrorResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

}
