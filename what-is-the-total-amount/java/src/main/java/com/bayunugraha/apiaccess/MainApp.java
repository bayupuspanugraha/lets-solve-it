package com.bayunugraha.apiaccess;
import com.bayunugraha.apiaccess.service.AppService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainApp {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        AppService mp = new AppService();
        System.out.println("Normal way - Total Amount: " + mp.GetTotalAmount(2, 8, 5, 50));
        System.out.println("Concurrent way - Total Amount: " + mp.GetTotalAmountParallel(2, 8, 5, 50));
    }
}
