package com.ehsure.cluster;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;

/**
 * @author Thinkpad
 *
 */
public class RWClusterWithSpringDataThread<T extends Serializable> implements Runnable {
	private CountDownLatch latch;
	private RedisTemplate<String ,T> redisTemplate;
	private String key;
	private T t;
	private int rw;
	
	public RWClusterWithSpringDataThread(CountDownLatch latch,RedisTemplate<String ,T> redisTemplate,String key,T t,int rw) {
		this.latch = latch;
		this.redisTemplate = redisTemplate;
		this.key = key;
		this.t = t;
		this.rw = rw;
	}
	
	@Override
	public void run() {
		try {
			// write
			if(this.rw == 0 ) {
				System.out.println("set to key : " + key);
				redisTemplate.opsForValue().set(key,t,1800,TimeUnit.SECONDS);
			}
			else if(this.rw == 1 ) {
				T result = redisTemplate.opsForValue().get(key);
				System.out.println(JSONObject.toJSONString(result));
			}
		} finally {
			latch.countDown();
		}
	}
	
}