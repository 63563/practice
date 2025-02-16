package com.ybean.manager;

import com.ybean.model.Bet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    public String getHighStakes(int betOfferId) {
        Map<Integer, Integer> topBetsPerCustomer = betMap.getOrDefault(betOfferId,
                        Collections.synchronizedList(new ArrayList<>()))
                .stream()
                .collect(Collectors.toMap(Bet::getUserId, Bet::getStake, Math::max));

        return topBetsPerCustomer.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(20)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","));
    }

}
