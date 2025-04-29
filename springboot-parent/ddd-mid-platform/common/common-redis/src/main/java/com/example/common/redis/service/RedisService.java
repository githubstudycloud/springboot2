package com.example.common.redis.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis操作服务接口
 */
public interface RedisService {

    /**
     * 保存属性
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    void set(String key, Object value, long time);

    /**
     * 保存属性
     *
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 获取属性
     *
     * @param key 键
     * @return 值
     */
    Object get(String key);

    /**
     * 删除属性
     *
     * @param key 键
     * @return 是否成功
     */
    Boolean delete(String key);

    /**
     * 批量删除属性
     *
     * @param keys 键集合
     * @return 删除数量
     */
    Long delete(Collection<String> keys);

    /**
     * 设置过期时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 是否成功
     */
    Boolean expire(String key, long time);

    /**
     * 设置过期时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     * @return 是否成功
     */
    Boolean expire(String key, long time, TimeUnit timeUnit);

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 时间(秒) 返回0代表永久有效
     */
    Long getExpire(String key);

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    Boolean hasKey(String key);

    /**
     * 按delta递增
     *
     * @param key   键
     * @param delta 递增因子
     * @return 递增后的值
     */
    Long increment(String key, long delta);

    /**
     * 按delta递减
     *
     * @param key   键
     * @param delta 递减因子
     * @return 递减后的值
     */
    Long decrement(String key, long delta);

    /**
     * 获取Hash结构中的属性
     *
     * @param key     键
     * @param hashKey 属性键
     * @return 属性值
     */
    Object hGet(String key, String hashKey);

    /**
     * 向Hash结构中放入一个属性
     *
     * @param key     键
     * @param hashKey 属性键
     * @param value   值
     * @param time    过期时间(秒)
     * @return 是否成功
     */
    Boolean hSet(String key, String hashKey, Object value, long time);

    /**
     * 向Hash结构中放入一个属性
     *
     * @param key     键
     * @param hashKey 属性键
     * @param value   值
     */
    void hSet(String key, String hashKey, Object value);

    /**
     * 直接设置整个Hash结构
     *
     * @param key  键
     * @param map  值
     * @param time 过期时间(秒)
     */
    void hSetAll(String key, Map<String, Object> map, long time);

    /**
     * 直接设置整个Hash结构
     *
     * @param key 键
     * @param map 值
     */
    void hSetAll(String key, Map<String, ?> map);

    /**
     * 获取整个Hash结构
     *
     * @param key 键
     * @return 对应的多个键值
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 删除Hash结构中的属性
     *
     * @param key      键
     * @param hashKeys 属性键数组
     * @return 删除数量
     */
    Long hDelete(String key, Object... hashKeys);

    /**
     * 判断Hash结构中是否有该属性
     *
     * @param key     键
     * @param hashKey 属性键
     * @return 是否存在
     */
    Boolean hHasKey(String key, String hashKey);

    /**
     * Hash结构中属性递增
     *
     * @param key     键
     * @param hashKey 属性键
     * @param delta   递增因子
     * @return 递增后的值
     */
    Long hIncrement(String key, String hashKey, Long delta);

    /**
     * Hash结构中属性递减
     *
     * @param key     键
     * @param hashKey 属性键
     * @param delta   递减因子
     * @return 递减后的值
     */
    Long hDecrement(String key, String hashKey, Long delta);

    /**
     * 获取Set结构
     *
     * @param key 键
     * @return 值集合
     */
    Set<Object> sMembers(String key);

    /**
     * 向Set结构中添加属性
     *
     * @param key    键
     * @param values 值
     * @return 添加数量
     */
    Long sAdd(String key, Object... values);

    /**
     * 向Set结构中添加属性
     *
     * @param key    键
     * @param time   过期时间(秒)
     * @param values 值
     * @return 添加数量
     */
    Long sAdd(String key, long time, Object... values);

    /**
     * 是否为Set中的属性
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    Boolean sIsMember(String key, Object value);

    /**
     * 获取Set结构的长度
     *
     * @param key 键
     * @return 长度
     */
    Long sSize(String key);

    /**
     * 删除Set结构中的属性
     *
     * @param key    键
     * @param values 值
     * @return 删除数量
     */
    Long sRemove(String key, Object... values);

    /**
     * 获取List结构中的属性
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 值列表
     */
    List<Object> lRange(String key, long start, long end);

    /**
     * 获取List结构的长度
     *
     * @param key 键
     * @return 长度
     */
    Long lSize(String key);

    /**
     * 根据索引获取List中的属性
     *
     * @param key   键
     * @param index 索引
     * @return 值
     */
    Object lIndex(String key, long index);

    /**
     * 向List结构中添加属性
     *
     * @param key   键
     * @param value 值
     * @return 添加后的长度
     */
    Long lPush(String key, Object value);

    /**
     * 向List结构中添加属性
     *
     * @param key   键
     * @param value 值
     * @param time  过期时间(秒)
     * @return 添加后的长度
     */
    Long lPush(String key, Object value, long time);

    /**
     * 向List结构中批量添加属性
     *
     * @param key    键
     * @param values 值
     * @return 添加后的长度
     */
    Long lPushAll(String key, Object... values);

    /**
     * 向List结构中批量添加属性
     *
     * @param key    键
     * @param time   过期时间(秒)
     * @param values 值
     * @return 添加后的长度
     */
    Long lPushAll(String key, Long time, Object... values);

    /**
     * 从List结构中移除属性
     *
     * @param key   键
     * @param count 数量
     * @param value 值
     * @return 移除数量
     */
    Long lRemove(String key, long count, Object value);
}
