package com.llmj.oss.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

/**
 * redis工具类
 * 
 * @author zjj
 *
 */
@Component
public class RedisTem {
	private String KEY_SPLIT = ":"; // 用于隔开缓存前缀与缓存键值

	@Autowired
	private JedisPool jedisPool;

	/***
	 * <p>
	 * Description: 返回资源
	 * </p>
	 * 
	 * @author zjj
	 * @date 2017年1月5日
	 * @param
	 */
	public void returnResource(JedisPool jedisPool, Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	/***
	 * <p>
	 * Description: 获取jedis 实例
	 * </p>
	 * 
	 * @author zjj
	 * @date 2017年1月5日
	 * @param
	 */
	public Jedis getJedis() throws Exception {
		return jedisPool.getResource();
	}
	
	public String get(String key) throws Exception {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = getJedis();
			value = jedis.get(key);
		} finally {
			// 返还到连接池
			returnResource(jedisPool, jedis);
		}

		return value;
	}
	
	public String set(String key, String value) throws Exception {
		Jedis jedis = null;
		String ans = null;
		try {
			jedis = getJedis();
			ans = jedis.set(key, value);
		} finally {
			// 返还到连接池
			returnResource(jedisPool, jedis);
		}

		return ans;
	}
	
	public String setPre(String prefix, String key, String value) throws Exception {
		Jedis jedis = null;
		String ans = null;
		try {
			jedis = getJedis();
			ans = jedis.set(prefix + KEY_SPLIT + key, value);
		} finally {
			// 返还到连接池
			returnResource(jedisPool, jedis);
		}

		return ans;
	}
	
	public String getPre(String prefix, String key) throws Exception {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = getJedis();
			value = jedis.get(prefix + KEY_SPLIT + key);
		} finally {
			// 返还到连接池
			returnResource(jedisPool, jedis);
		}
		return value;
	}
	
	public void delete(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.del(key);
		} finally {
			// 返还到连接池
			returnResource(jedisPool, jedis);
		}
	}
	
	public Long del(String prefix, String key) throws Exception {

		Jedis jedis = null;
		Long res = 0l;
		try {
			jedis = getJedis();
			res = jedis.del(prefix + KEY_SPLIT + key);
		} finally {
			// 返还到连接池
			returnResource(jedisPool, jedis);
		}
		return res;
	}
	
	/**
	 * 模糊删除
	 * @param prefix
	 * @param match
	 * @throws Exception
	 */
	public void vagueDel(String prefix, String match) throws Exception {

		Jedis jedis = null;
		try {
			jedis = getJedis();
			String key = prefix + KEY_SPLIT + match;
			Set<String> keys=jedis.keys(key);
			String[] tmp = new String[keys.size()];
			tmp = keys.toArray(tmp);
			if (tmp == null || tmp.length == 0) {
				return;
			}
			jedis.del(tmp);
		} finally {
			// 返还到连接池
			returnResource(jedisPool, jedis);
		}
	}
	
	/**
	 * <p>
	 * 通过key 和 field 获取指定的 value
	 * </p>
	 * @throws Exception 
	 * 
	 */
	public String hget(String key, String field) throws Exception {
		Jedis jedis = null;
		String res = null;
		try {
			jedis = getJedis();
			res = jedis.hget(key, field);
		} finally {
			returnResource(jedisPool, jedis);
		}
		return res;
	}
	
	public Long hset(String key, String field, String value) throws Exception {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.hset(key, field, value);
		} finally {
			returnResource(jedisPool, jedis);
		}
		return res;
	}
	
	public String hgetPrefix(String prefix, String key, String field) throws Exception {
		Jedis jedis = null;
		String res = null;
		String bkey = prefix + KEY_SPLIT + key;
		try {
			jedis = getJedis();
			res = jedis.hget(bkey, field);
		} finally {
			returnResource(jedisPool, jedis);
		}
		return res;
	}
	
	public void hsetPrefix(String prefix, String key, String field, String value) throws Exception {
		Jedis jedis = null;
		String bkey = prefix + KEY_SPLIT + key;
		try {
			jedis = getJedis();
			jedis.hset(bkey, field, value);
		} finally {
			returnResource(jedisPool, jedis);
		}
	}
	
	public Long hdelPrefix(String prefix, String key, String field) throws Exception {
		Jedis jedis = null;
		Long res = null;
		String bkey = prefix + KEY_SPLIT + key;
		try {
			jedis = getJedis();
			res = jedis.hdel(bkey, field);
		} finally {
			returnResource(jedisPool, jedis);
		}
		return res;
	}
	
	public Long lpushPre(String prefix, String key, String... str) throws Exception {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.lpush(prefix + KEY_SPLIT + key, str);
		} finally {
			returnResource(jedisPool, jedis);
		}
		return res;
	}
	
	public int llenPrefix(String prefix, String key) throws Exception {
		Jedis jedis = null;
		int res = 0;
		try {
			jedis = getJedis();
			res = jedis.llen(prefix + KEY_SPLIT + key).intValue();
		} finally {
			returnResource(jedisPool, jedis);
		}
		return res;
	}
	
	public List<String> lrangePre(String prefix, String key, long start, long end) throws Exception {
		Jedis jedis = null;
		List<String> res = null;
		try {
			jedis = getJedis();
			res = jedis.lrange(prefix + KEY_SPLIT + key, start, end);
		} finally {
			returnResource(jedisPool, jedis);
		}
		return res;
	}
	
	public Long lremWithPrefix(String prefix, String key, long count, String value) throws Exception {
		return lrem(prefix + KEY_SPLIT + key, count, value);
	}
	
	public Long lrem(String key, long count, String value) throws Exception {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.lrem(key, count, value);
		} finally {
			returnResource(jedisPool, jedis);
		}
		return res;
	}
}
