package com.bayunugraha.apiaccess.service;

import com.bayunugraha.apiaccess.model.Data;
import com.bayunugraha.apiaccess.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.javatuples.Pair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class AppService {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    private Response GetDataFromUrl(int userId, int page) throws IOException {
        HttpGet request = new HttpGet("https://jsonmock.hackerrank.com/api/transactions/search?userId=" + userId + "&page=" + page);
        request.addHeader("content-type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if(statusCode == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    Response resp = new ObjectMapper().readValue(result, Response.class);
                    return resp;
                }
            }
        }

        return null;
    }

    private int ConvertAmount(String money) {
        String sanitiseMoney = money.replaceAll("\\$", "");
        sanitiseMoney = sanitiseMoney.replaceAll("\\,", "");

        float moneyInFloat = parseFloat(sanitiseMoney);

        return (int)Math.round(moneyInFloat);
    }

    private int CalculateAmount(List<Data> datas, int locationId, int netStart, int netEnd) {
        int total = 0;
        for(Data data :datas) {
            if(data.getLocation().getId() == locationId) {
                String[] ips = data.getIp().split("\\.");
                int firstData =  parseInt(ips[0]);

                if(firstData >= netStart && firstData <= netEnd) {
                    total += ConvertAmount(data.getAmount());
                }
            }
        }

        return total;
    }

    private Pair<Response, Integer> HandleRequest(int userId, int locationId, int netStart, int netEnd, int page) throws IOException {
        Response resp = GetDataFromUrl(userId, page);
        int total = 0;
        if(resp.getTotal() > 0) {
            total += CalculateAmount(resp.getDataItems(), locationId, netStart, netEnd);
        }

        return Pair.with(resp, total);
    }

    public int GetTotalAmount(int userId, int locationId, int netStart, int netEnd) throws IOException, ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();

        Pair<Response, Integer> response = HandleRequest(userId, locationId, netStart, netEnd, 1);

        Response resp = response.getValue0();
        int total  = response.getValue1();

        if(resp.getTotalPages() > 1) {
            for(int page=2;page<= resp.getTotalPages();page++) {
                Pair<Response, Integer> tempResp = HandleRequest(userId, locationId, netStart, netEnd, page);
                total += tempResp.getValue1();
            }
        }

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in milliseconds: " + timeElapsed);
        return total;
    }

    public int GetTotalAmountParallel(int userId, int locationId, int netStart, int netEnd) throws IOException, ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();

        Pair<Response, Integer> response = HandleRequest(userId, locationId, netStart, netEnd, 1);

        Response resp = response.getValue0();
        AtomicInteger total  = new AtomicInteger(response.getValue1());

        if(resp.getTotalPages() > 1) {
            List<CompletableFuture<Pair<Response, Integer>>> tasks = new ArrayList<CompletableFuture<Pair<Response, Integer>>>();

            for(int page=2;page<= resp.getTotalPages();page++) {
                int finalPage = page;
                CompletableFuture<Pair<Response, Integer>> task = CompletableFuture.supplyAsync(() -> {
                    try {
                        return HandleRequest(userId, locationId, netStart, netEnd, finalPage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });

                tasks.add(task);
            }

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()]))
                    .thenAccept(ignore -> {
                        for(CompletableFuture<Pair<Response, Integer>> item : tasks) {
                            Pair<Response, Integer> tResult = item.join();
                            total.addAndGet(tResult.getValue1());
                        }
                    }).get();
        }

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in milliseconds: " + timeElapsed);
        return total.get();
    }
}
