package com.ybean.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ybean.constant.MessageConstant;
import com.ybean.utils.ResponseUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HandlerAdapter implements HttpHandler {

    private final Map<String, HttpHandler> handlerMap = new HashMap<>();

    public void addRoute(String path, HttpHandler handler) {
        handlerMap.put(path, handler);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        String[] split = requestPath.split("/");

        // 尝试找到匹配的处理器
        for (Map.Entry<String, HttpHandler> entry : handlerMap.entrySet()) {
            if (split[2].startsWith(entry.getKey())) {
                entry.getValue().handle(exchange);
                return;
            }
        }

        ResponseUtil.sendResponse(exchange, 404, MessageConstant.NOT_FOUND_ERROR_MESSAGE);
    }
}
