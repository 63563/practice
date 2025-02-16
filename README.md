# 项目概述

本项目实现了一个基于HTTP的后端服务，用于管理不同客户对投注项目的赌注，并能够返回特定投注项目的最高赌注。该服务包括三个主要功能：获取会话、提交客户的赌注和获取最高赌注列表。

## 设计目标

1. **线程安全**：确保在多用户并发请求情况下数据的一致性和准确性。
2. **无持久化**：所有数据仅存储在内存中，不依赖外部数据库或其他持久化存储。
3. **高效处理大量并发请求**：考虑到高并发环境下的性能问题。

## 技术选型

- **Java**：使用Java语言进行开发。
- **com.sun.net.httpserver.HttpServer**：轻量级HTTP服务器，无需引入额外框架。
- **ConcurrentHashMap**：保证数据结构的线程安全性。

## 功能实现

### 1. 获取或创建会话（Get Session）

#### 实现要点
- 使用`ConcurrentHashMap`来存储和管理用户的会话信息。
- 定期清理过期的会话以释放资源。

```java
public class SessionManager {
    private final ConcurrentHashMap<Integer, String> sessions = new ConcurrentHashMap<>();
    
    public SessionManager() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        System.out.println("清除session定时任务启动");
        scheduler.scheduleAtFixedRate(this::cleanExpiredSessions, 1, 1, TimeUnit.MINUTES);
    }
    // 其他方法...
}
```

### 2. 提交客户投注

#### 实现要点
- 验证会话的有效性。
- 将赌注信息存储到ConcurrentHashMap中。

```java
public class BetOfferManager {
    private final ConcurrentHashMap<Integer, List<Bet>> betMap = new ConcurrentHashMap<>();

    public void betOffer(int betOfferId, int stake, String sessionKey, SessionManager sessionManager) throws Exception {
        Integer customerId = sessionManager.getAllSession().entrySet().stream()
                .filter(entry -> entry.getValue().getKey().equals(sessionKey))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new Exception("Invalid session key."));

        betMap.putIfAbsent(betOfferId, Collections.synchronizedList(new ArrayList<>()));
        betMap.get(betOfferId).add(new Bet(customerId, betOfferId, stake));
    }
}
```

### 3. 获取特定投注项目的高投注列表

#### 实现要点
- 对于每个客户，只保留其最高的赌注。
- 按照赌注金额降序排列，并限制结果数量为前20个

```java
public String getHighStakes(int betOfferId) {
    Map<Integer, Integer> topBetsPerCustomer = bets.getOrDefault(betOfferId, Collections.synchronizedList(new ArrayList<>()))
            .stream()
            .collect(Collectors.toMap(bet -> bet.customerId, bet -> bet.stake, Math::max));

    return topBetsPerCustomer.entrySet().stream()
            .sorted(Map.Entry.<Integer, Integer>comparingByValue(Comparator.reverseOrder()))
            .limit(20)
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(","));
}
```

### 4. 如何运行

#### 打包

```Bash
mvn clean package
```

#### 启动服务
将生成的JAR文件上传到服务器上，并通过以下命令启动服务：

```Bash
nohup java -jar practice-1.0-SNAPSHOT.jar &
```

默认情况下，服务将在http://localhost:8080上运行。

### 5. 测试接口

您可以使用curl或者Postman等工具测试API接口：

- 获取会话：GET http://localhost:8080<customerid>/session
- 提交赌注：POST http://localhost:8080<betofferid>/stake?sessionkey=<sessionkey> (body: <stake>)
- 获取最高赌注列表：GET http://localhost:8080<betofferid>/highstakes