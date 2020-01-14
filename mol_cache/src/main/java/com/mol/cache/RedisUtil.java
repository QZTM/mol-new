package com.mol.cache;

import redis.clients.jedis.*;

import java.io.IOException;
import java.util.*;

/**
 * @author tengfei
 * @version 1.0
 * @date 2018/7/13 下午4:15
 */
public class RedisUtil {
    private JedisCluster jedis = null;
    private volatile static RedisUtil redisUtil;
//    private  RedisUtil() {
//        // jedispool为null则初始化，
//        if (pool == null) {
//
//            Properties prop = new Properties();
//            try {
//                prop.load(RedisUtil.class.getResourceAsStream("/redis.properties"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String ip = prop.getProperty("host");
//            String password = prop.getProperty("password");
//            int port = Integer.parseInt(prop.getProperty("port"));
//            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//            // 如果赋值为-1，则表示不限制；如果pool已经分配了maxTotal个jedis实例，则此时pool的状态为exhausted(耗尽）.
//            jedisPoolConfig.setMaxTotal(300);
//            // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例
//            jedisPoolConfig.setMaxIdle(10);
//            jedisPoolConfig.setMinIdle(5);
//            // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
//            jedisPoolConfig.setMaxWaitMillis(1000*200);
//            // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
//            //jedisPoolConfig.setTestOnBorrow(true);
//            //jedisPoolConfig.setTestWhileIdle(true);
//            //jedisPoolConfig.setTestOnReturn(true);
//            //表示idle object evitor两次扫描之间要sleep的毫秒数
//            //表示idle object evitor每次扫描的最多的对象数
//            //jedisPoolConfig.setNumTestsPerEvictionRun(200);
//            //jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
//            //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
//            //jedisPoolConfig.setMinEvictableIdleTimeMillis(30000);
//            //pool = new JedisPool(jedisPoolConfig, ip, port, 10000, password);
//            // redis 未设置密码
//            pool = new JedisPool(jedisPoolConfig, ip, port, 10000 ,password);
//        }
//    }




    private  RedisUtil() {
        // jedisCluster为null则初始化，
        if (jedis == null) {
            Properties prop = new Properties();
            try {
                prop.load(RedisUtil.class.getResourceAsStream("/redis.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 第一步：使用JedisCluster对象。需要一个Set<HostAndPort>参数。Redis节点的列表。
            Set<HostAndPort> nodes = new HashSet<>();
             nodes.add(new HostAndPort(prop.getProperty("1host"), Integer.parseInt(prop.getProperty("1port"))));
             nodes.add(new HostAndPort(prop.getProperty("2host"), Integer.parseInt(prop.getProperty("2port"))));
             nodes.add(new HostAndPort(prop.getProperty("3host"), Integer.parseInt(prop.getProperty("3port"))));
             nodes.add(new HostAndPort(prop.getProperty("4host"), Integer.parseInt(prop.getProperty("4port"))));
             nodes.add(new HostAndPort(prop.getProperty("5host"), Integer.parseInt(prop.getProperty("5port"))));
             nodes.add(new HostAndPort(prop.getProperty("6host"), Integer.parseInt(prop.getProperty("6port"))));
            //jedisCluster = new JedisCluster(nodes);
            // Jedis连接池配置
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            // 最大空闲连接数, 默认8个
            jedisPoolConfig.setMaxIdle(100);
            // 最大连接数, 默认8个
            jedisPoolConfig.setMaxTotal(500);
            //最小空闲连接数, 默认0
            jedisPoolConfig.setMinIdle(0);
            // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
            // 设置2秒
            jedisPoolConfig.setMaxWaitMillis(2000);
            //对拿到的connection进行validateObject校验
            jedisPoolConfig.setTestOnBorrow(true);
            jedis = new JedisCluster(nodes,10000,1000,1000,"ald377",jedisPoolConfig);
            //jedis = new JedisCluster(nodes, jedisPoolConfig);
        }
    }







    /**
     * 获取指定key的值,如果key不存在返回null，如果该Key存储的不是字符串，会抛出一个错误
     *
     * @param key
     * @return
     */
    public String get(String key) {
        String value = jedis.get(key);
        return value;
    }

    /**
     * 设置key的值为value
     *
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
        return jedis.set(key, value);
    }

    /**
     * 删除指定的key,也可以传入一个包含key的数组
     *
     * @param keys
     * @return
     */
    public Long del(String... keys) {
        return jedis.del(keys);
    }

    /**
     * 通过key向指定的value值追加值
     *
     * @param key
     * @param str
     * @return
     */
    public Long append(String key, String str) {
        return jedis.append(key, str);
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(String key) {
        return jedis.exists(key);
    }

    /**
     * 设置key value,如果key已经存在则返回0
     *
     * @param key
     * @param value
     * @return
     */
    public Long setnx(String key, String value) {
        return jedis.setnx(key, value);
    }

    /**
     * 设置key value并指定这个键值的有效期
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public String setex(String key, int seconds, String value) {
        return jedis.setex(key, seconds, value);
    }

    /**
     * 通过key 和offset 从指定的位置开始将原先value替换
     *
     * @param key
     * @param offset
     * @param str
     * @return
     */
    public Long setrange(String key, int offset, String str) {
        return jedis.setrange(key, offset, str);
    }

    /**
     * 通过批量的key获取批量的value
     *
     * @param keys
     * @return
     */
    public List<String> mget(String... keys) {
        return jedis.mget(keys);
    }

    /**
     * 批量的设置key:value,也可以一个
     *
     * @param keysValues
     * @return
     */
    public String mset(String... keysValues) {
        return jedis.mset(keysValues);
    }

    /**
     * 批量的设置key:value,可以一个,如果key已经存在则会失败,操作会回滚
     *
     * @param keysValues
     * @return
     */
    public Long msetnx(String... keysValues) {
        return jedis.msetnx(keysValues);
    }

    /**
     * 设置key的值,并返回一个旧值
     *
     * @param key
     * @param value
     * @return
     */
    public String getSet(String key, String value) {
        return jedis.getSet(key, value);
    }

    /**
     * 通过下标 和key 获取指定下标位置的 value
     *
     * @param key
     * @param startOffset
     * @param endOffset
     * @return
     */
    public String getrange(String key, int startOffset, int endOffset) {
        return jedis.getrange(key, startOffset, endOffset);
    }

    /**
     * 通过key 对value进行加值+1操作,当value不是int类型时会返回错误,当key不存在是则value为1
     *
     * @param key
     * @return
     */
    public Long incr(String key) {
        return jedis.incr(key);
    }

    /**
     * 通过key给指定的value加值,如果key不存在,则这是value为该值
     *
     * @param key
     * @param integer
     * @return
     */
    public Long incrBy(String key, long integer) {
        return jedis.incrBy(key, integer);
    }

    /**
     * 对key的值做减减操作,如果key不存在,则设置key为-1
     *
     * @param key
     * @return
     */
    public Long decr(String key) {
        return jedis.decr(key);
    }

    /**
     * 减去指定的值
     *
     * @param key
     * @param integer
     * @return
     */
    public Long decrBy(String key, long integer) {
        return jedis.decrBy(key, integer);
    }

    /**
     * 通过key获取value值的长度
     *
     * @param key
     * @return
     */
    public Long strLen(String key) {
        return jedis.strlen(key);
    }

    /**
     * 通过key给field设置指定的值,如果key不存在则先创建,如果field已经存在,返回0
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hsetnx(String key, String field, String value) {
        return jedis.hsetnx(key, field, value);
    }

    /**
     * 通过key给field设置指定的值,如果key不存在,则先创建
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hset(String key, String field, String value) {
        return jedis.hset(key, field, value);
    }

    /**
     * 通过key同时设置 hash的多个field
     *
     * @param key
     * @param hash
     * @return
     */
    public String hmset(String key, Map<String, String> hash) {
        return jedis.hmset(key, hash);
    }

    /**
     * 通过key 和 field 获取指定的 value
     *
     * @param key
     * @param failed
     * @return
     */
    public String hget(String key, String failed) {
        return jedis.hget(key, failed);
    }

    /**
     * 设置key的超时时间为seconds
     *
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(String key, int seconds) {
        return jedis.expire(key, seconds);
    }

    /**
     * 通过key 和 fields 获取指定的value 如果没有对应的value则返回null
     *
     * @param key
     * @param fields 可以是 一个String 也可以是 String数组
     * @return
     */
    public List<String> hmget(String key, String... fields) {
        return jedis.hmget(key, fields);
    }

    /**
     * 通过key给指定的field的value加上给定的值
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hincrby(String key, String field, Long value) {
        return jedis.hincrBy(key, field, value);
    }

    /**
     * 通过key和field判断是否有指定的value存在
     *
     * @param key
     * @param field
     * @return
     */
    public Boolean hexists(String key, String field) {
        return jedis.hexists(key, field);
    }

    /**
     * 通过key返回field的数量
     *
     * @param key
     * @return
     */
    public Long hlen(String key) {
        return jedis.hlen(key);
    }

    /**
     * 通过key 删除指定的 field
     *
     * @param key
     * @param fields 可以是 一个 field 也可以是 一个数组
     * @return
     */
    public Long hdel(String key, String... fields) {
        return jedis.hdel(key, fields);
    }

    /**
     * 通过key返回所有的field
     *
     * @param key
     * @return
     */
    public Set<String> hkeys(String key) {
        return jedis.hkeys(key);
    }

    /**
     * 通过key返回所有和key有关的value
     *
     * @param key
     * @return
     */
    public List<String> hvals(String key) {
        return jedis.hvals(key);
    }

    /**
     * 通过key获取所有的field和value
     *
     * @param key
     * @return
     */
    public Map<String, String> hgetall(String key) {
        return jedis.hgetAll(key);
    }

    /**
     * 通过key向list头部添加字符串
     *
     * @param key
     * @param strs 可以是一个string 也可以是string数组
     * @return 返回list的value个数
     */
    public Long lpush(String key, String... strs) {
        return jedis.lpush(key, strs);
    }

    /**
     * 通过key向list尾部添加字符串
     *
     * @param key
     * @param strs 可以是一个string 也可以是string数组
     * @return 返回list的value个数
     */
    public Long rpush(String key, String... strs) {
        return jedis.rpush(key, strs);
    }

    /**
     * 通过key在list指定的位置之前或者之后 添加字符串元素
     *
     * @param key
     * @param where LIST_POSITION枚举类型
     * @param pivot list里面的value
     * @param value 添加的value
     * @return
     */
    public Long linsert(String key, BinaryClient.LIST_POSITION where,
                        String pivot, String value) {
        return jedis.linsert(key, where, pivot, value);
    }

    /**
     * 通过key设置list指定下标位置的value
     * 如果下标超过list里面value的个数则报错
     *
     * @param key
     * @param index 从0开始
     * @param value
     * @return 成功返回OK
     */
    public String lset(String key, Long index, String value) {
        return jedis.lset(key, index, value);
    }

    /**
     * 通过key从对应的list中删除指定的count个 和 value相同的元素
     *
     * @param key
     * @param count 当count为0时删除全部
     * @param value
     * @return 返回被删除的个数
     */
    public Long lrem(String key, long count, String value) {
        return jedis.lrem(key, count, value);
    }

    /**
     * 通过key保留list中从strat下标开始到end下标结束的value值
     *
     * @param key
     * @param start
     * @param end
     * @return 成功返回OK
     */
    public String ltrim(String key, long start, long end) {
        return jedis.ltrim(key, start, end);
    }

    /**
     * 通过key从list的头部删除一个value,并返回该value
     *
     * @param key
     * @return
     */
    public synchronized String lpop(String key) {

        return jedis.lpop(key);
    }

    /**
     * 通过key从list尾部删除一个value,并返回该元素
     *
     * @param key
     * @return
     */
    synchronized public String rpop(String key) {
        return jedis.rpop(key);
    }

    /**
     * 通过key从一个list的尾部删除一个value并添加到另一个list的头部,并返回该value
     * 如果第一个list为空或者不存在则返回null
     *
     * @param srckey
     * @param dstkey
     * @return
     */
    public String rpoplpush(String srckey, String dstkey) {
        return jedis.rpoplpush(srckey, dstkey);
    }

    /**
     * 通过key获取list中指定下标位置的value
     *
     * @param key
     * @param index
     * @return 如果没有返回null
     */
    public String lindex(String key, long index) {
        return jedis.lindex(key, index);
    }

    /**
     * 通过key返回list的长度
     *
     * @param key
     * @return
     */
    public Long llen(String key) {
        return jedis.llen(key);
    }

    /**
     * 通过key获取list指定下标位置的value
     * 如果start 为 0 end 为 -1 则返回全部的list中的value
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lrange(String key, long start, long end) {
        return jedis.lrange(key, start, end);
    }

    /**
     * 通过key向指定的set中添加value
     *
     * @param key
     * @param members 可以是一个String 也可以是一个String数组
     * @return 添加成功的个数
     */
    public Long sadd(String key, String... members) {
        return jedis.sadd(key, members);
    }

    /**
     * 通过key删除set中对应的value值
     *
     * @param key
     * @param members 可以是一个String 也可以是一个String数组
     * @return 删除的个数
     */
    public Long srem(String key, String... members) {
        return jedis.srem(key, members);
    }

    /**
     * 通过key随机删除一个set中的value并返回该值
     *
     * @param key
     * @return
     */
    public String spop(String key) {
        return jedis.spop(key);
    }

    /**
     * 通过key获取set中的差集
     * 以第一个set为标准
     *
     * @param keys 可以 是一个string 则返回set中所有的value 也可以是string数组
     * @return
     */
    public Set<String> sdiff(String... keys) {
        return jedis.sdiff(keys);
    }

    /**
     * 通过key获取set中的差集并存入到另一个key中
     * 以第一个set为标准
     *
     * @param dstkey 差集存入的key
     * @param keys   可以 是一个string 则返回set中所有的value 也可以是string数组
     * @return
     */
    public Long sdiffstore(String dstkey, String... keys) {
        return jedis.sdiffstore(dstkey, keys);
    }

    /**
     * 通过key获取指定set中的交集
     *
     * @param keys 可以 是一个string 也可以是一个string数组
     * @return
     */
    public Set<String> sinter(String... keys) {
        return jedis.sinter(keys);
    }

    /**
     * 通过key获取指定set中的交集 并将结果存入新的set中
     *
     * @param dstkey
     * @param keys   可以 是一个string 也可以是一个string数组
     * @return
     */
    public Long sinterstore(String dstkey, String... keys) {
        return jedis.sinterstore(dstkey, keys);
    }

    /**
     * 通过key返回所有set的并集
     *
     * @param keys 可以 是一个string 也可以是一个string数组
     * @return
     */
    public Set<String> sunion(String... keys) {
        return jedis.sunion(keys);
    }

    /**
     * 通过key返回所有set的并集,并存入到新的set中
     *
     * @param dstkey
     * @param keys   可以 是一个string 也可以是一个string数组
     * @return
     */
    public Long sunionstore(String dstkey, String... keys) {
        return jedis.sunionstore(dstkey, keys);
    }

    /**
     * 通过key将set中的value移除并添加到第二个set中
     *
     * @param srckey 需要移除的
     * @param dstkey 添加的
     * @param member set中的value
     * @return
     */
    public Long smove(String srckey, String dstkey, String member) {
        return jedis.smove(srckey, dstkey, member);
    }

    /**
     * 通过key获取set中value的个数
     *
     * @param key
     * @return
     */
    public Long scard(String key) {
        return jedis.scard(key);
    }

    /**
     * 通过key判断value是否是set中的元素
     *
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(String key, String member) {
        return jedis.sismember(key, member);
    }

    /**
     * 通过key获取set中随机的value,不删除元素
     *
     * @param key
     * @return
     */
    public String srandmember(String key) {
        return jedis.srandmember(key);
    }

    /**
     * 通过key获取set中所有的value
     *
     * @param key
     * @return
     */
    public Set<String> smembers(String key) {
        return jedis.smembers(key);
    }


    /**
     * 通过key向zset中添加value,score,其中score就是用来排序的
     * 如果该value已经存在则根据score更新元素
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Long zadd(String key, double score, String member) {
        return jedis.zadd(key, score, member);
    }

    /**
     * 通过key删除在zset中指定的value
     *
     * @param key
     * @param members 可以 是一个string 也可以是一个string数组
     * @return
     */
    public Long zrem(String key, String... members) {
        return jedis.zrem(key, members);
    }

    /**
     * 通过key增加该zset中value的score的值
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Double zincrby(String key, double score, String member) {
        return jedis.zincrby(key, score, member);
    }

    /**
     * 通过key返回zset中value的排名
     * 下标从小到大排序
     *
     * @param key
     * @param member
     * @return
     */
    public Long zrank(String key, String member) {
        return jedis.zrank(key, member);
    }

    /**
     * 通过key返回zset中value的排名
     * 下标从大到小排序
     *
     * @param key
     * @param member
     * @return
     */
    public Long zrevrank(String key, String member) {
        return jedis.zrevrank(key, member);
    }

    /**
     * 通过key将获取score从start到end中zset的value
     * socre从大到小排序
     * 当start为0 end为-1时返回全部
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrevrange(String key, long start, long end) {
        return jedis.zrevrange(key, start, end);
    }

    /**
     * 通过key返回指定score内zset中的value
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    public Set<String> zrangebyscore(String key, String max, String min) {
        return jedis.zrevrangeByScore(key, max, min);
    }

    /**
     * 通过key返回指定score内zset中的value
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    public Set<String> zrangeByScore(String key, double max, double min) {
        return jedis.zrevrangeByScore(key, max, min);
    }

    /**
     * 返回指定区间内zset中value的数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zcount(String key, String min, String max) {
        return jedis.zcount(key, min, max);
    }

    /**
     * 通过key返回zset中的value个数
     *
     * @param key
     * @return
     */
    public Long zcard(String key) {
        return jedis.zcard(key);
    }

    /**
     * 通过key获取zset中value的score值
     *
     * @param key
     * @param member
     * @return
     */
    public Double zscore(String key, String member) {
        return jedis.zscore(key, member);
    }

    /**
     * 通过key删除给定区间内的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zremrangeByRank(String key, long start, long end) {
        return jedis.zremrangeByRank(key, start, end);
    }

    /**
     * 通过key删除指定score内的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zremrangeByScore(String key, double start, double end) {
        return jedis.zremrangeByScore(key, start, end);
    }


    /**
     * 通过key判断值得类型
     *
     * @param key
     * @return
     */
    public String type(String key) {
        return jedis.type(key);
    }


    private void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


    public static RedisUtil getRedisUtil() {
        if (redisUtil == null) {
            synchronized (RedisUtil.class) {
                if (redisUtil == null) {
                    redisUtil = new RedisUtil();
                }
            }
        }
        return redisUtil;
    }
}