package io.github.renhaowan.bizlog.core.service.storage;

import io.github.renhaowan.bizlog.core.log.BizLogProperties;
import io.github.renhaowan.bizlog.core.log.BizLogRecord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wan
 * 日志存储管理器
 * 策略模式
 */
public class LogStorageManager {

    private final Map<String, AbstractLogStorage> storageMap = new HashMap<>();
    private final BizLogProperties prop;

    /**
     * 构造函数
     *
     * @param storageList 日志存储器列表
     * @param prop        日志配置属性
     */
    public LogStorageManager(List<AbstractLogStorage> storageList, BizLogProperties prop) {
        this.prop = prop;
        storageList.forEach(
                storage -> storageMap.put(storage.getBeanName(), storage)
        );
    }

    /**
     * 存储日志
     *
     * @param records             日志记录
     * @param storageBeanName     存储器名称
     */
    public void store(BizLogRecord records, String... storageBeanName) {
        if (storageBeanName == null || storageBeanName.length == 0) {
            storageBeanName = prop.getStorageBeanName();
        }
        for (String beanName : Arrays.stream(storageBeanName).distinct().toList()) {
            if (!storageMap.containsKey(beanName)) {
                throw new IllegalArgumentException("storageBeanName: " + beanName + " is not exist");
            }
            storageMap.get(beanName).doStore(records);
        }
    }

    /**
     * 获取默认存储器名称
     *
     * @return 默认存储器名称
     */
    public String getDefaultStorageBeanName() {
        return Arrays.toString(prop.getStorageBeanName());
    }
}
