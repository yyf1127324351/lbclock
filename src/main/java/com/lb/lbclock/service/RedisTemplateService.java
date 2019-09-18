package com.lb.lbclock.service;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisTemplateService {

	RedisTemplate<String, String> getRedisTemplate();

	void setRedisTemplate(RedisTemplate<String, String> redisTemplate);

	/**
	 * setnx操作
	 *
	 * @param key
	 * @param time
	 * @return
	 */
	boolean setExpire(String key, long time);

	/**
	 * setnx操作
	 *
	 * @param key
	 * @param v
	 * @return
	 */
	boolean setNX(String key, String v);

	/**
	 * setnx操作
	 *
	 * @param key
	 * @param v
	 * @param time
	 * @return
	 */
	boolean setNX(String key, String v, long time);

	/**
	 * set操作
	 *
	 * @param key
	 * @param v
	 * @param time
	 * @return
	 */
	boolean set(String key, String v, long time);

	/**
	 * 缓存value操作
	 *
	 * @param k
	 * @param v
	 * @return
	 */
	boolean set(String k, String v);

	boolean containsKey(String key);

	/**
	 * 获取值
	 *
	 * @param k
	 * @return
	 */
	String get(String k);

	/**
	 * 移除key
	 *
	 * @param key
	 * @return
	 */
	boolean remove(String key);

	/**
	 * set add操作
	 *
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	long sadd(String k, String v, long time);

	/**
	 * set
	 *
	 * @param k
	 * @param v
	 * @return
	 */
	long sadd(String k, String v);

	/**
	 * set add Set集合
	 *
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	long sadd(String k, Set<String> v, long time);

	/**
	 * 获取set数据
	 *
	 * @param k
	 * @return
	 */
	Set<String> sget(String k);

	/**
	 * 获取set数据
	 *
	 * @param k1
	 * @param k2
	 * @return
	 */
	Set<String> sdiff(String k1, String k2);

	boolean scontains(String key, String key2);
	/**
	 * List  left push
	 *
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	long lpush(String k, String v, long time);

	/**
	 * List  left push
	 *
	 * @param k
	 * @param v
	 * @return
	 */
	long lpush(String k, String v);

	/**
	 * List  right push
	 *
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	long rpush(String k, String v, long time);

	/**
	 * List  right push
	 *
	 * @param k
	 * @param v
	 * @return
	 */
	long rpush(String k, String v);

	/**
	 * List  right push List集合
	 *
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	long rpush(String k, List<String> v, long time);

	/**
	 * List  right push List集合
	 *
	 * @param k
	 * @param v
	 * @return
	 */
	long rpush(String k, List<String> v);

	/**
	 * 获取List subList
	 *
	 * @param k
	 * @param start
	 * @param end
	 * @return
	 */
	List<String> getSubList(String k, long start, long end);

	/**
	 * 获取List总条数, 可用于分页
	 *
	 * @param k
	 * @return
	 */
	long getListSize(String k);

	/**
	 * 弹出list的右边第一个
	 *
	 * @param k
	 * @return
	 */
	String rpop(String k);

	/**
	 * 弹出list的左边第一个
	 *
	 * @param k
	 * @return
	 */
	String lpop(String k);

	/**
	 * Map mputAll
	 *
	 * @param k
	 * @return
	 */
	boolean mputAll(String k, Map<String, String> map, long time);

	/**
	 * Map put
	 *
	 * @param k
	 * @return
	 */
	boolean mput(String k, String fieldKey, String fieldVal, long time);

	/**
	 * Map get fieldVal
	 *
	 * @param k
	 * @return
	 */
	String mget(String k, String fieldKey);

	/**
	 * Map get ALL
	 *
	 * @param k
	 * @return
	 */
	Map<String, String> mget(String k);

    /**
     * getAndSet操作
     * @param key
     * @param val
     * @return
     */
	long getAndSet(String key, String val);


	Set<String> keys(String key);

	void remove(Set<String> keys);

	void batchDel(String redisKey);

	List<String> getValues(String s);
}