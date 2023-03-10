/**
 * 
 */
package com.boot.util;
import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author JX
 * 【redis】工具
 */
public class RedisUtil {
	/**
	 * 日志
	 */
	private static final Logger log = Logger.getLogger(RedisUtil.class);


	/**
	 * 键值对 SET
	 *
	 * @param key
	 * @param value
	 * @param redisTemplate
	 */
	public static void setKeyValue(String key, String value, RedisTemplate redisTemplate) {
		redisTemplate.boundValueOps(key).set(value);
	}

	/**
	 * 键值对 GET
	 *
	 * @param key
	 * @param redisTemplate
	 * @return
	 */
	public static String getKeyValue(String key, RedisTemplate redisTemplate) {
		Object value = redisTemplate.boundValueOps(key).get();
		if (value != null)
			return value.toString();
		return "";
	}



}
