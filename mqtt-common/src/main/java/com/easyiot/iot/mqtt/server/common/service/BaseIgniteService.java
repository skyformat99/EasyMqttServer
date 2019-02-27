package com.easyiot.iot.mqtt.server.common.service;

import javax.cache.Cache;
import java.util.List;

/**
 * 用户Ignite查询的基础Service
 */
interface BaseIgniteService<T> {


    /**
     * 根据id查找当前Model
     *
     * @param id
     * @return
     */

    T findOneById(long id);


    /**
     * 根据id删除当前Model
     *
     * @param id
     */

    void deleteById(long id);

    /**
     * 批量删除
     *
     * @param ids
     */

    void deleteByIds(long[] ids);


    /**
     * 在数据库存储当前 Model
     *
     * @param T
     */

    void save(T T);

    /**
     * 更新Model
     *
     * @param T
     */

    void update(T T);

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    List<Cache.Entry<String, T>> listAll(int page, int size);

}
