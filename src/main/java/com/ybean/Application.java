package com.ybean;

import com.sun.net.httpserver.HttpServer;
import com.ybean.handler.*;
import com.ybean.manager.BetOfferManager;
import com.ybean.manager.SessionManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Application {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, // 核心线程数
            4, // 最大线程数
            10L, // 空闲线程存活时间
            TimeUnit.SECONDS, // 时间单位
            new LinkedBlockingDeque<>(1000), // 任务队列，使用基于链表的队列
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
    );

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        HandlerAdapter adapter = new HandlerAdapter();

        SessionManager sessionManager = new SessionManager();
        BetOfferManager betOfferManager = new BetOfferManager();

        adapter.addRoute("session", new SessionHandler(sessionManager));
        adapter.addRoute("stake", new BetHandler(betOfferManager, sessionManager));
        adapter.addRoute("highstakes", new HighStakeHandler(betOfferManager));

        server.createContext("/", adapter);

        server.setExecutor(executor);
        server.start();

        System.out.println("Server is listening on port 8080");
    }

}
