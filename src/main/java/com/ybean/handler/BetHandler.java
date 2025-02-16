package com.ybean.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ybean.constant.HttpMethodConstant;
import com.ybean.constant.MessageConstant;
import com.ybean.manager.BetOfferManager;
import com.ybean.manager.SessionManager;
import com.ybean.utils.ResponseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BetHandler implements HttpHandler {

    private final BetOfferManager betOfferManager;
    private final SessionManager sessionManager;

    public BetHandler(BetOfferManager betOfferManager,
                      SessionManager sessionManager) {
        this.betOfferManager = betOfferManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 校验请求方式是否正确
        String method = exchange.getRequestMethod();
        if (!method.equalsIgnoreCase(HttpMethodConstant.POST_METHOD)) {
            ResponseUtil.sendResponse(exchange, 405, MessageConstant.METHOD_ERROR_MESSAGE);
        }

        // 设置响应编码为UTF-8
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");

        try {
            // 获取请求的 URI 并提取路径部分
            String path = exchange.getRequestURI().getPath();
            String[] pathSegments = path.split("/");

            // 确保至少有一个有效的路径段（即 session id）
            if (pathSegments.length < 2) {
                ResponseUtil.sendResponse(exchange, 400, "Invalid request: missing betOfferId.");
                return;
            }

            // 解析参数
            String betOfferId = pathSegments[1];
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> paramMap = parseQueryParams(query);
            String sessionKey = paramMap.get("sessionkey");

            // 读取请求体
            InputStream inputStream = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }

            // 投注
            betOfferManager.betOffer(Integer.parseInt(betOfferId), Integer.parseInt(requestBody.toString()),
                    sessionKey.trim(), sessionManager);

            // 发送成功响应
            ResponseUtil.sendResponse(exchange, 200, MessageConstant.SUCCESS_MESSAGE);
        } catch (Exception e) {
            // 捕获所有异常并返回500错误
            ResponseUtil.sendResponse(exchange, 500, MessageConstant.SERVICE_ERROR_MESSAGE + e.getMessage());
        }
    }

    // 解析查询字符串的辅助方法
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                params.put(key, value);
            }
        }
        return params;
    }
}
