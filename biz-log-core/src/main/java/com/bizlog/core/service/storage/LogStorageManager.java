package com.bizlog.core.service.storage;

import com.bizlog.core.log.BizLogProperties;
import com.bizlog.core.log.BizLogRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志存储管理器
 * 策略模式
 */
public class LogStorageManager {

    private final Map<String, AbstractLogStorage> storageMap = new HashMap<>();
    private final BizLogProperties prop;

    public LogStorageManager(List<AbstractLogStorage> storageList, BizLogProperties prop) {
        this.prop = prop;
        storageList.forEach(
                storage -> storageMap.put(storage.getBeanName(), storage)
        );
    }

    public void store(BizLogRecord records, String storageBeanName) {
        if (storageBeanName == null || storageBeanName.isBlank()) {
            storageBeanName = prop.getStorageBeanName();
        }
        if (!storageMap.containsKey(storageBeanName)) {
            throw new IllegalArgumentException("storageBeanName: " + storageBeanName + " is not exist");
        }
        storageMap.get(storageBeanName).doStore(records);
    }

    public String getDefaultStorageBeanName() {
        return prop.getStorageBeanName();
    }
}
