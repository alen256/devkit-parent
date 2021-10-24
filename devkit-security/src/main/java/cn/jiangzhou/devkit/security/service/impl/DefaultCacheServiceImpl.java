package cn.jiangzhou.devkit.security.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.jiangzhou.devkit.security.service.CacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@ConditionalOnBean(CacheService.class)
@Service
public class DefaultCacheServiceImpl implements CacheService {

    private TimedCache<String, String> timedCache = CacheUtil.newTimedCache(3 * 60 * 1000);

    @Override
    public void set(String ns, String key, String value) {
        timedCache.put(ns + "-" + key, value);
    }

    @Override
    public void set(String ns, String key, String value, int second) {
        timedCache.put(ns + "-" + key, value, second * 1000L);
    }

    @Override
    public String get(String ns, String key) {
        return timedCache.get(ns + "-" + key);
    }

    @Override
    public void delete(String ns, String key) {
        timedCache.remove(ns + "-" + key);
    }
}
