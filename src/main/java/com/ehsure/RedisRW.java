package com.ehsure;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import redis.clients.jedis.Jedis;

public class RedisRW {

	public static Jedis getJedisConnection() {
		Jedis jedis = new Jedis("124.126.15.61", 6379, 200000);
		jedis.auth("123456");
		return jedis ;
	}
	
	public static void main(String[] args) {
		//ExecutorService pool = Executors.newFixedThreadPool(20);
		
		String k = new SimpleDateFormat("yyyy-MM-dd#HH:mm").format(new Date());
		
		int poolsize = 20;
		List<Jedis> jedisList = new ArrayList<>();
		for(int i=0;i<poolsize;i++) {
			jedisList.add(getJedisConnection());
		}
		CountDownLatch latch = new CountDownLatch(poolsize);
		
		List<RedisInputDto> dtoList = new ArrayList<>();
		for(int i=0;i<10;i++) {
			dtoList.add(new RedisInputDto("dto"+i,UUID.randomUUID().toString()) );
		}
		
		RedisInputDto dto = null;
		for(Jedis jedis:jedisList) {
			dto = new RedisInputDto();
			dto.setName(k+"."+jedisList.indexOf(jedis));
			dto.setDtos(dtoList);
			Thread t1 = new Thread(new RWThread(latch,jedis,dto));
			t1.start();
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}