package com.industry.printer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
	
	public static ExecutorService mThreads = Executors.newFixedThreadPool(5);
	
}