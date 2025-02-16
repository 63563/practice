package com.ybean.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ybean.constant.HttpMethodConstant;
import com.ybean.constant.MessageConstant;
import com.ybean.manager.BetOfferManager;
import com.ybean.utils.ResponseUtil;

import java.io.IOException;

public class HighStakeHandler implements HttpHandler {

    private final BetOfferManager betOfferManager;

    public HighStakeHandler(BetOfferManager betOfferManager) {
        this.betOfferManager = betOfferManager;
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

            String betOfferId = pathSegments[1];
            String highStakes = betOfferManager.getHighStakes(Integer.parseInt(betOfferId));

            ResponseUtil.sendResponse(exchange, 200, highStakes);
        } catch (Exception e) {
            // 捕获所有异常并返回500错误
            ResponseUtil.sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }
}
