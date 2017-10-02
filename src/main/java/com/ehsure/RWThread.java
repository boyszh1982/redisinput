package com.ehsure;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import redis.clients.jedis.Jedis;

public class RWThread implements Runnable {
	private CountDownLatch latch;
	private Jedis jedis;
	private RedisInputDto dto;
	
	public RWThread(CountDownLatch latch,Jedis jedis,RedisInputDto dto) {
		this.latch = latch;
		this.jedis = jedis;
		this.dto = dto;
	}
	
	@Override
	public void run() {
		try {
			long t1 = System.currentTimeMillis();
			byte[] bytes = SerializeUtil.serialize(dto);
			long t2 = System.currentTimeMillis();
			System.out.println(String.format("%s trans to byte[] cost %s ms , %s s", dto.getName(), t2-t1, (double)(t2-t1)/(double)1000 ));
			jedis.set(dto.getName().getBytes(),bytes);
			jedis.expire(dto.getName().getBytes(), 10);
			long t3 = System.currentTimeMillis();
			System.out.println(String.format("%s set to redis cost %s ms , %s s", dto.getName(), t2-t1, (double)(t3-t2)/(double)1000 ));
			/*
			byte[] result = jedis.get(dto.getName().getBytes());
			long t4 = System.currentTimeMillis();
			System.out.println(String.format("%s get from redis cost %s", dto.getName(), t4-t3));
			RedisInputDto resultDto = SerializeUtil.unserialize(result, RedisInputDto.class);
			long t5 = System.currentTimeMillis();
			System.out.println(String.format("%s trnas to Object cost %s", dto.getName(), t5-t4));
			*/
		} catch (IOException e) {
			e.printStackTrace();
			/*
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			*/
		} finally {
			latch.countDown();
		}
	}
	
}