package com.ehsure;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
全部6个节点启用，插入数据，
将某个有数据的节点停掉，再次读取KEY会报错, 等待一段时间，再次获取,
发现路由规则已经指向 挂掉MASTER的SLAVE节点。
redis mast-slave 为非抢占式 
 
#安装ruby2.2.2
[root@ecs-9cc2-0001 etc]# yum -y install ruby ruby-devel rubygems rpm-build
[root@ecs-9cc2-0001 etc]# gpg2 --keyserver hkp://keys.gnupg.net --recv-keys D39DC0E3
[root@ecs-9cc2-0001 etc]# curl -L get.rvm.io | bash -s stable
[root@ecs-9cc2-0001 etc]# find / -name rvm -print
[root@ecs-9cc2-0001 etc]# source /usr/local/rvm/scripts/rvm
[root@ecs-9cc2-0001 etc]# rvm list known
[root@ecs-9cc2-0001 etc]# rvm install 2.3.3
#集群创建
[root@ecs-9cc2-0001 etc]# gem install redis
[root@ecs-9cc2-0001 etc]# vi redis.conf
	masterauth 123456
[root@ecs-9cc2-0001 local]# find / -name "*.rb" -exec grep -i ":password" {} \; -print
	/usr/local/rvm/gems/ruby-2.3.3/gems/redis-4.0.1/lib/redis/client.rb
[root@ecs-9cc2-0001 local]# vi /usr/local/rvm/gems/ruby-2.3.3/gems/redis-4.0.1/lib/redis/client.rb

require_relative "errors"
require "socket"
require "cgi"

class Redis
  class Client

    DEFAULTS = {
      :url => lambda { ENV["REDIS_URL"] },
      :scheme => "redis",
      :host => "127.0.0.1",
      :port => 6379,
      :path => nil,
      :timeout => 5.0,
      :password => 123456,

redis-server /usr/local/redis/etc/redis-7001.conf
redis-server /usr/local/redis/etc/redis-7002.conf
redis-server /usr/local/redis/etc/redis-7003.conf

[root@ecs-9cc2-0001 etc]# redis-trib.rb create --replicas 1 192.168.1.112:7001 192.168.1.112:7002 192.168.1.112:7003 192.168.1.129:7001 192.168.1.129:7002 192.168.1.129:7003
>>> Creating cluster
>>> Performing hash slots allocation on 6 nodes...
Using 3 masters:
192.168.1.112:7001
192.168.1.129:7001
192.168.1.112:7002
Adding replica 192.168.1.129:7002 to 192.168.1.112:7001
Adding replica 192.168.1.112:7003 to 192.168.1.129:7001
Adding replica 192.168.1.129:7003 to 192.168.1.112:7002
M: 7fd90cbc74cab08b57210217f42ef1b46c2e7557 192.168.1.112:7001
   slots:0-5460 (5461 slots) master
M: 5f4b9e6f093d5edf01753c8571babf2dbe3e1c6d 192.168.1.112:7002
   slots:10923-16383 (5461 slots) master
S: c5b96520a043b946839b6363d2462ea6ce62296e 192.168.1.112:7003
   replicates c87474bfa73d789d2415d6adfa48f5cc02a3a5c7
M: c87474bfa73d789d2415d6adfa48f5cc02a3a5c7 192.168.1.129:7001
   slots:5461-10922 (5462 slots) master
S: 7434ae1ee00e4b3e01b820f547aaa6714ab46139 192.168.1.129:7002
   replicates 7fd90cbc74cab08b57210217f42ef1b46c2e7557
S: 25d343cfece4faa8e7b6565d310786af680aa00a 192.168.1.129:7003
   replicates 5f4b9e6f093d5edf01753c8571babf2dbe3e1c6d
Can I set the above configuration? (type 'yes' to accept): yes
>>> Nodes configuration updated
>>> Assign a different config epoch to each node
>>> Sending CLUSTER MEET messages to join the cluster
Waiting for the cluster to join....
>>> Performing Cluster Check (using node 192.168.1.112:7001)
M: 7fd90cbc74cab08b57210217f42ef1b46c2e7557 192.168.1.112:7001
   slots:0-5460 (5461 slots) master
   1 additional replica(s)
S: 25d343cfece4faa8e7b6565d310786af680aa00a 192.168.1.129:7003
   slots: (0 slots) slave
   replicates 5f4b9e6f093d5edf01753c8571babf2dbe3e1c6d
M: c87474bfa73d789d2415d6adfa48f5cc02a3a5c7 192.168.1.129:7001
   slots:5461-10922 (5462 slots) master
   1 additional replica(s)
S: c5b96520a043b946839b6363d2462ea6ce62296e 192.168.1.112:7003
   slots: (0 slots) slave
   replicates c87474bfa73d789d2415d6adfa48f5cc02a3a5c7
S: 7434ae1ee00e4b3e01b820f547aaa6714ab46139 192.168.1.129:7002
   slots: (0 slots) slave
   replicates 7fd90cbc74cab08b57210217f42ef1b46c2e7557
M: 5f4b9e6f093d5edf01753c8571babf2dbe3e1c6d 192.168.1.112:7002
   slots:10923-16383 (5461 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
 * @author Thinkpad
 *
 */
public class RWClusterThread implements Runnable {
	private CountDownLatch latch;
	private JedisCluster jedisCluster;
	private RedisInputDto dto;
	private byte rw;
	
	public RWClusterThread(CountDownLatch latch,JedisCluster jedisCluster,RedisInputDto dto,byte rw) {
		this.latch = latch;
		this.jedisCluster = jedisCluster;
		this.dto = dto;
		this.rw = rw;
	}
	
	@Override
	public void run() {
		boolean show = false;
		try {
			// write
			if(this.rw == 0 ) {
				long t1 = System.currentTimeMillis();
				byte[] bytes = SerializeUtil.serialize(dto);
				long t2 = System.currentTimeMillis();
				if(show) System.out.println(String.format("%s trans to byte[] cost %s ms , %s s", dto.getName(), t2-t1, (double)(t2-t1)/(double)1000 ));
				jedisCluster.set(dto.getName().getBytes(),bytes);
				jedisCluster.expire(dto.getName().getBytes(), 1800);
				long t3 = System.currentTimeMillis();
				if(show) System.out.println(String.format("%s set to redis cost %s ms , %s s", dto.getName(), t2-t1, (double)(t3-t2)/(double)1000 ));
			}
			else if(this.rw == 1 ) {
				long t3 = System.currentTimeMillis();
				byte[] resultBytes = jedisCluster.get(dto.getName().getBytes());
				long t4 = System.currentTimeMillis();
				if(show) System.out.println(String.format("%s get from redis cost %s", dto.getName(), t4-t3));
				RedisInputDto resultDto = SerializeUtil.unserialize(resultBytes, RedisInputDto.class);
				long t5 = System.currentTimeMillis();
				if(show) System.out.println(String.format("%s trnas to Object cost %s", resultDto.getName(), t5-t4));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			latch.countDown();
		}
	}
	
}