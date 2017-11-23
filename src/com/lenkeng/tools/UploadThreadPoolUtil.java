package com.lenkeng.tools;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/*
 * $Id: ThreadPoolUtil.java 4 2013-12-12 04:19:52Z kf $
 */
public class UploadThreadPoolUtil {
	private static  ExecutorService pool;
	public static void execute(Runnable command) {
	    if(pool==null){
	    	pool=Executors.newFixedThreadPool(1);
	    }
		pool.execute(command);
	}
	
	public static <T> Future<T> submit(Callable<T> task) {
		return pool.submit(task);
	}

	public static Future<?> submit(Runnable task) {
		return pool.submit(task);
	}
	
	public static <T> Future<T> submit(Runnable task, T result) {
		return pool.submit(task, result);
	}
}
