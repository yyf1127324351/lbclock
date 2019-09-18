package com.lb.lbclock.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lb.lbclock.service.RedisTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("redisTemplateService")
public class RedisTemplateServiceImpl implements RedisTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(RedisTemplateServiceImpl.class);

    /**
     * 日志记录
     */

    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * setnx操作
     *
     * @param key
     * @param time
     * @return
     */
    public boolean setExpire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * setnx操作
     *
     * @param key
     * @param v
     * @return
     */
    public boolean setNX(String key, String v) {
        return setNX(key, v, -1);
    }

    /**
     * setnx操作
     *
     * @param key
     * @param v
     * @param time
     * @return
     */
    public boolean setNX(String key, String v, long time) {
        try {
            ValueOperations<String, String> valueOps = redisTemplate
                    .opsForValue();
            boolean b = valueOps.setIfAbsent(key, v);
            if (b && time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return b;
        } catch (Throwable t) {
            logger.error("Set[" + key + "]失败, value[" + v + "]" + t.getMessage());
        }
        return false;
    }

    /**
     * set操作
     *
     * @param key
     * @param v
     * @param time
     * @return
     */
    public boolean set(String key, String v, long time) {
        try {
            ValueOperations<String, String> valueOps = redisTemplate
                    .opsForValue();
            valueOps.set(key, v);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Throwable t) {
            logger.error("set[" + key + "]失败, value[" + v + "]" + t.getMessage());
        }
        return false;
    }

    /**
     * 缓存value操作
     *
     * @param k
     * @param v
     * @return
     */
    public boolean set(String k, String v) {
        return set(k, v, -1);
    }

    public boolean containsKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable t) {
            logger.error("判断key存在失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }

    /**
     * 获取值
     *
     * @param k
     * @return
     */
    public String get(String k) {
        try {
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            return valueOps.get(k);
        } catch (Throwable t) {
            logger.error("获取值失败key[" + k + ", error[" + t
                    + "]");
        }
        return null;
    }

    /**
     * 移除key
     *
     * @param key
     * @return
     */
    public boolean remove(String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Throwable t) {
            logger.error("删除值失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }

    /**
     * set add操作
     *
     * @param k
     * @param v
     * @param time
     * @return
     */
    public long sadd(String k, String v, long time) {
        try {
            SetOperations<String, String> valueOps = redisTemplate.opsForSet();
            long l = valueOps.add(k, v);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
            return l;
        } catch (Throwable t) {
            logger.error("Set Add[" + k + "]失败, value[" + v + "]" + t.getMessage());
        }
        return 0L;
    }

    /**
     * set
     *
     * @param k
     * @param v
     * @return
     */
    public long sadd(String k, String v) {
        return sadd(k, v, -1);
    }

    /**
     * set add Set集合
     *
     * @param k
     * @param v
     * @param time
     * @return
     */
    public long sadd(String k, Set<String> v, long time) {
        try {
            SetOperations<String, String> setOps = redisTemplate.opsForSet();
            long l = setOps.add(k, v.toArray(new String[v.size()]));
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
            return l;
        } catch (Throwable t) {
            logger.error("Set 批量ADD[" + k + "]失败, value[" + v + "]" + t.getMessage());
        }
        return 0L;
    }

    /**
     * set add set集合
     *
     * @param k
     * @param v
     * @return
     */
    public long cacheSet(String k, Set<String> v) {
        return sadd(k, v, -1);
    }

    /**
     * 获取set数据
     *
     * @param k
     * @return
     */
    public Set<String> sget(String k) {
        try {
            SetOperations<String, String> setOps = redisTemplate.opsForSet();
            return setOps.members(k);
        } catch (Throwable t) {
            logger.error("获取set失败key[" + k + ", error[" + t
                    + "]");
        }
        return null;
    }

    /**
     * 获取set数据
     *
     * @param k1
     * @param k2
     * @return
     */
    public Set<String> sdiff(String k1, String k2) {
        try {
            SetOperations<String, String> setOps = redisTemplate.opsForSet();
            return setOps.difference(k1, k2);
        } catch (Throwable t) {
            logger.error("合并失败key[" + k1 + "，" + k2 + ", error[" + t
                    + "]");
        }
        return null;
    }

    @Override
    public boolean scontains(String key, String value) {
        try {
            SetOperations<String, String> setOps = redisTemplate.opsForSet();
            return setOps.isMember(key, value);
        } catch (Throwable t) {
            logger.error("scontains失败key[" + key + "，" + value + ", error[" + t
                    + "]");
        }
        return false;
    }

    /**
     * List  left push
     *
     * @param k
     * @param v
     * @param time
     * @return
     */
    public long lpush(String k, String v, long time) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            long l = listOps.leftPush(k, v);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
            return l;
        } catch (Throwable t) {
            logger.error("List Rpush[" + k + "]失败, value[" + v + "]" + t.getMessage());
        }
        return 0L;
    }

    /**
     * List  left push
     *
     * @param k
     * @param v
     * @return
     */
    public long lpush(String k, String v) {
        return lpush(k, v, -1);
    }

    /**
     * List  right push
     *
     * @param k
     * @param v
     * @param time
     * @return
     */
    public long rpush(String k, String v, long time) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            long l = listOps.rightPush(k, v);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
            return l;
        } catch (Throwable t) {
            logger.error("List Rpush[" + k + "]失败, value[" + v + "]" + t.getMessage());
        }
        return 0L;
    }

    /**
     * List  right push
     *
     * @param k
     * @param v
     * @return
     */
    public long rpush(String k, String v) {
        return rpush(k, v, -1);
    }

    /**
     * List  right push List集合
     *
     * @param k
     * @param v
     * @param time
     * @return
     */
    public long rpush(String k, List<String> v, long time) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            long l = listOps.rightPushAll(k, v);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
            return l;
        } catch (Throwable t) {
            logger.error("List R push all[" + k + "]失败, value[" + JSONObject.toJSONString(v) + "]" + t.getMessage());
        }
        return 0L;
    }

    /**
     * List  right push List集合
     *
     * @param k
     * @param v
     * @return
     */
    public long rpush(String k, List<String> v) {
        return rpush(k, v, -1);
    }

    /**
     * 获取List subList
     *
     * @param k
     * @param start
     * @param end
     * @return
     */
    public List<String> getSubList(String k, long start, long end) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.range(k, start, end);
        } catch (Throwable t) {
            logger.error("获取list subList失败key[" + k + ", error["
                    + t + "]");
        }
        return null;
    }

    /**
     * 获取List总条数, 可用于分页
     *
     * @param k
     * @return
     */
    public long getListSize(String k) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.size(k);
        } catch (Throwable t) {
            logger.error("获取list长度失败key[" + k + "], error["
                    + t + "]");
        }
        return 0;
    }

    /**
     * 弹出list的右边第一个
     *
     * @param k
     * @return
     */
    public String rpop(String k) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.rightPop(k);
        } catch (Throwable t) {
            logger.error("List Rpop失败key[" + k + ", error["
                    + t + "]");
        }
        return null;
    }

    /**
     * 弹出list的左边第一个
     *
     * @param k
     * @return
     */
    public String lpop(String k) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.leftPop(k);
        } catch (Throwable t) {
            logger.error("List Lpop失败key[" + k + ", error["
                    + t + "]");
        }
        return null;
    }

    /**
     * Map mputAll
     *
     * @param k
     * @return
     */
    public boolean mputAll(String k, Map<String, String> map, long time) {
        try {
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            hashOps.putAll(k, map);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Throwable t) {
            logger.error("Hash PUT失败key[" + k + ", field[" + map + "]");
        }
        return false;
    }

    /**
     * Map put
     *
     * @param k
     * @return
     */
    public boolean mput(String k, String fieldKey, String fieldVal, long time) {
        try {
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            hashOps.put(k, fieldKey, fieldVal);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Throwable t) {
            logger.error("Hash PUT失败key[" + k + ", field[" + fieldKey + "]");
        }
        return false;
    }

    /**
     * Map get fieldVal
     *
     * @param k
     * @return
     */
    public String mget(String k, String fieldKey) {
        try {
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            return hashOps.get(k, fieldKey);
        } catch (Throwable t) {
            logger.error("Hash get field失败key[" + k + ", error["
                    + t + "]");
        }
        return null;
    }

    /**
     * Map get ALL
     *
     * @param k
     * @return
     */
    public Map<String, String> mget(String k) {
        try {
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            return hashOps.entries(k);
        } catch (Throwable t) {
            logger.error("Hash get ALL失败key[" + k + ", error[" + t + "]");
        }
        return null;
    }

    @Override
    public long getAndSet(String key, String val) {
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            return Long.valueOf(operations.getAndSet(key, val));
        } catch (Throwable t) {
            logger.error("getAndSet失败key[" + key + "] value[" + val + "], error[" + t + "]");
        }
        return 0;
    }

    @Override
    public Set<String> keys(String key) {
        try {
            String pattern = key + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            return keys;
        } catch (Throwable t) {
            logger.error("keys获取失败[" + key + "]");
        }
        return null;
    }

    @Override
    public List<String> getValues(String key) {
        List<String> valuesList = new ArrayList<String>();
        try {
            String pattern = key + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                valuesList.add(get(it.next().toString()));
            }
            return valuesList;
        } catch (Throwable t) {
            logger.error("keys获取失败[" + key + "]");
        }
        return null;
    }

    @Override
    public void remove(Set<String> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Throwable t) {
            logger.error("delete keys 失败[" + keys + "]");
        }
    }

    @Override
    public void batchDel(String redisKey) {
        try {
            redisTemplate.delete(redisTemplate.keys(redisKey + "*"));
        } catch (Throwable t) {
            logger.error("batchDel keys 失败[" + redisKey + "]");
        }
    }


}
