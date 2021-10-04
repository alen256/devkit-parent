package cn.jiangzhou.devkit.bean.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private Integer code;

    public BusinessException(Integer code) {
        this.code = code;
    }

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException(Throwable cause, Integer code) {
        super(cause);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Integer code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public BusinessException(ApiCode apiCode) {
        super(apiCode.getMsg());
        this.code = apiCode.getCode();
    }
}
