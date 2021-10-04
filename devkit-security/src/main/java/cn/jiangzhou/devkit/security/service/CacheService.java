package cn.jiangzhou.devkit.security.service;

public interface CacheService {

    void set(String ns, String key, String value);

    void set(String ns, String key, String value, int second);

    String get(String ns, String key);

    void delete(String ns, String key);

}
