# EasyLinkerMqttServerV0.0.1：开发文档.
# 1.开发注意事项
1. 遵守规范
2. 面向接口
# 2. 设计思路
# 3. 二次开发
1. Service层实现:首先，实现一个标准的CURD模板，如下所示:
```text

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

```
然后再对应的类里面应用,```Cache.Entry<String, T>```这个内部类表示的是存进Ignite的数据
# 4. 数据结构
## 1.一个在线设备的描述
```text
[{
	"key": "00000000000000e0-000024b8-00000004-3e59026e2753953e-8630061e",
	"value": {
		"channelId": "00000000000000e0-000024b8-00000004-3e59026e2753953e-8630061e",
		"channelToJson": {
			"active": true,
			"address": {
				"address": "0:0:0:0:0:0:0:1",
				"port": 8885
			},
			"id": "00000000000000e0-000024b8-00000004-3e59026e2753953e-8630061e"
		},
		"cleanSession": true,
		"clientId": "0.2808018",
		"willMessageToJson": {
			"will": {
				"willRetain": false,
				"cleanSession": true,
				"willFlag": false
			}
		}
	}
}]
```
key:ChannelID，value:channel对应的客户端信息
# 这里有个设计思路：根据Netty的设计哲学，每一个链接进来的客户端，Netty都认为它是一个Channel，并且给这个Channel一个ID，从而实现标识。EasyMqttServer在设计的过程中参考了这种哲学吗，用ChannelId来表示每一个链接进来的客户端。