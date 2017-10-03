package com.ehsure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ehsure.cluster.RedisCache;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/spring-data-redis.xml" })
public class RedisClusterWithSpringDataIncrTest {


	@Test
	public void doTestAppExt() {
		
		RedisCache cache = RedisCache.getInstance();
		Long result = cache.increase("incr");
		System.out.println(result);
		
		Long result2 = cache.increase("incr",100L);
		System.out.println(result2);
	}
}
