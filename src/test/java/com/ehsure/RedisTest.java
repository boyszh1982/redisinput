package com.ehsure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/spring.xml" })
public class RedisTest {

	@Autowired
	private JedisPoolConfigExt jedisPoolConfigExt;

	@Test
	public void testHello() {
		;
	}
	
	@Ignore
	public void testApp() {
		for (int i = 0; i < 1; i++) {
			//doTestApp();
			doTestAppExt();
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
				;
			}
		}
	}

	public void doTestApp() {
		String k = new SimpleDateFormat("yyyy-MM-dd#HH:mm").format(new Date());
		int poolsize = 5;
		List<Jedis> jedisList = new ArrayList<>();
		for (int i = 0; i < poolsize; i++) {
			jedisList.add(getJedisConnection());
		}
		CountDownLatch latch = new CountDownLatch(poolsize);
		List<RedisInputDto> dtoList = new ArrayList<>();
		for (int i = 0; i < 500000; i++) {
			dtoList.add(new RedisInputDto("dto" + i, UUID.randomUUID().toString()));
		}
		RedisInputDto dto = null;
		for (Jedis jedis : jedisList) {
			dto = new RedisInputDto();
			// dto.setName(k+"."+jedisList.indexOf(jedis));
			dto.setName(String.format("%s.%04d", k, jedisList.indexOf(jedis)));
			dto.setDtos(dtoList);
			Thread t1 = new Thread(new RWThread(latch, jedis, dto));
			t1.start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void doTestAppExt() {
		String k = new SimpleDateFormat("yyyy-MM-dd#HH:mm").format(new Date());
		k = "TEMP";
		JedisCluster jedisCluster = getJedisConnectionWithCluster();
		int poolsize = 20;
		CountDownLatch latch = new CountDownLatch(poolsize);
		List<RedisInputDto> dtoList = new ArrayList<>();
		for (int i = 0; i < 0; i++) {
			dtoList.add(new RedisInputDto("dto" + i, UUID.randomUUID().toString()));
		}
		RedisInputDto dto = null;
		for (int i=0;i<poolsize;i++) {
			dto = new RedisInputDto();
			// dto.setName(k+"."+jedisList.indexOf(jedis));
			dto.setName(String.format("%s.%04d", k, i ));
			dto.setDtos(dtoList);
			Thread t1 = new Thread(new RWClusterThread(latch, jedisCluster, dto , (byte)1));
			t1.start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public JedisCluster getJedisConnectionWithCluster() {
		/**
		 * 使用JedisCluster 客户端命令查询 ,必须使用 -c -a 123456 ,不然会验证失败
		 * redis-cli -p 7001 -c -a 123456
		 */
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		jedisClusterNodes.add(new HostAndPort("192.168.1.112", 7001));
		jedisClusterNodes.add(new HostAndPort("192.168.1.112", 7002));
		jedisClusterNodes.add(new HostAndPort("192.168.1.112", 7003));
		jedisClusterNodes.add(new HostAndPort("192.168.1.129", 7001));
		jedisClusterNodes.add(new HostAndPort("192.168.1.129", 7002));
		jedisClusterNodes.add(new HostAndPort("192.168.1.129", 7003));
		JedisCluster jc = new JedisCluster(jedisClusterNodes, jedisPoolConfigExt.getConnectionTimeout(),
				jedisPoolConfigExt.getSoTimeout(), jedisPoolConfigExt.getMaxAttempts(), jedisPoolConfigExt.getAuth(),
				jedisPoolConfigExt);
		return jc;
	}

	public Jedis getJedisConnection() {
		// Jedis jedis = new Jedis("124.126.15.61", 6379, 200000);
		Jedis jedis = new Jedis("192.168.1.129", 6379, 200000);
		jedis.auth("123456");
		return jedis;
	}

}
