package com.ehsure.cluster;


import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Stanley on 2017/10/3.
 */
public class RedisCache {

	// private static final String hosts =
	// ReadProperties.getString("primarykey-redis.host",
	// "primarykey-redis.properties");
	// private static final String port =
	// ReadProperties.getString("primarykey-redis.port",
	// "primarykey-redis.properties");
	// private static final String password =
	// ReadProperties.getString("primarykey-redis.password",
	// "primarykey-redis.properties");

	private static final String hosts = "192.168.1.112:7001,192.168.1.112:7002,192.168.1.112:7003,"
			+ "192.168.1.129:7001,192.168.1.129:7002,192.168.1.129:7003,"
			+ "192.168.1.249:7001,192.168.1.249:7002,192.168.1.249:7003";
	private static final int connectionTimeout = 2000;
	private static final int soTimeout = 2000;
	private static final int maxAttempts = 2000;
	@Deprecated
	private static final String port = "";
	private static final String password = "123456";
	protected static int maxTotal = 1000;
	protected static int maxIdle = 10;
	protected static int maxWaitMillis = 3000;
	//protected static JedisPool pool = null;
	protected static JedisCluster jedisCluster = null;
	protected static RedisCache redisCache = new RedisCache();

	static {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(true);
		jedisCluster = getJedisConnectionWithCluster(config);
		// pool = new JedisPool(config, hosts, Integer.valueOf(port).intValue(), 10000,
		// password);
		// Jedis jedis = pool.getResource();
	}

	public static RedisCache getInstance() {
		return redisCache;
	}

	public Long increase(String key) {
		return jedisCluster.incr(key);
	}

	public Long increase(String key, Long num) {
		return jedisCluster.incrBy(key, num);
	}

	public Long increaseWithExpire(String key, int seconds) {
		synchronized (this) {
			Long r = jedisCluster.incr(key);
			jedisCluster.expire(key, seconds);
			return r;
		}
	}

	public void delCache(String... keys) {
		jedisCluster.del(keys);
	}

	public static JedisCluster getJedisConnectionWithCluster(JedisPoolConfig config) {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		String[] address = hosts.split(",");
		for (String hostAndIp : address) {
			jedisClusterNodes.add(new HostAndPort(hostAndIp.split(":")[0], Integer.parseInt(hostAndIp.split(":")[1])));
		}
		JedisCluster jc = new JedisCluster(jedisClusterNodes, connectionTimeout, soTimeout, maxAttempts, password,
				config);
		return jc;
	}
}
