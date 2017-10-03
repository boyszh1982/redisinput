package com.ehsure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ehsure.cluster.RWClusterWithSpringDataThread;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/spring-data-redis.xml" })
public class RedisClusterWithSpringDataTest {

	@Autowired
	private RedisTemplate<String, RedisInputDto> redisTemplate;

	@Test
	public void doTestAppExt() {
		
		Properties p = System.getProperties();
		int rw = Integer.parseInt(p.getProperty("rw"));
		
		String k = new SimpleDateFormat("yyyy-MM-dd#HH:mm").format(new Date());
		k = "TEMP";

		int poolsize = 20;
		CountDownLatch latch = new CountDownLatch(poolsize);
		List<RedisInputDto> dtoList = new ArrayList<>();
		for (int i = 0; i < 0; i++) {
			dtoList.add(new RedisInputDto("dto" + i, UUID.randomUUID().toString()));
		}
		RedisInputDto dto = null;
		for (int i = 0; i < poolsize; i++) {
			dto = new RedisInputDto();
			dto.setName(String.format("%s.%04d", k, i));
			dto.setDtos(dtoList);
			Thread t1 = new Thread(
					new RWClusterWithSpringDataThread<RedisInputDto>(latch, redisTemplate, dto.getName(), dto, rw));
			t1.start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
