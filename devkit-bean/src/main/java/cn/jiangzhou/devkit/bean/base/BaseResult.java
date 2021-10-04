package cn.jiangzhou.devkit.bean.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseResult<T> {

    private int code;

    private String msg;

    private T data;

    public static <V> BaseResult<V> wrap(V data) {
        return new BaseResult<>(200, "操作成功", data);
    }

    public static <Void> BaseResult<Void> wrap(int code, String msg) {
        return new BaseResult<>(code, msg, null);
    }

    public static <V> BaseResult<V> wrap(int code, String msg, V data) {
        return new BaseResult<>(code, msg, data);
    }

}
