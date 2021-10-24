package cn.jiangzhou.devkit.api.advice;

import cn.jiangzhou.devkit.api.anno.ApiResponse;
import cn.jiangzhou.devkit.bean.base.BaseResult;
import cn.jiangzhou.devkit.bean.base.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return methodParameter.getDeclaringClass().isAnnotationPresent(ApiResponse.class) || methodParameter.getMethod().isAnnotationPresent(ApiResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body instanceof BaseResult) {
            return body;
        }
        return BaseResult.wrap(body);
    }

    @ExceptionHandler(value = Exception.class)
    public BaseResult<Void> handlerReException(HttpServletRequest request, Exception e) {
        if (e instanceof BusinessException) {
            return BaseResult.wrap(((BusinessException) e).getCode(), e.getMessage());
        }
        else if (e instanceof MissingServletRequestParameterException) {
            return BaseResult.wrap(400, e.getMessage());
        }
        log.warn(e.getMessage(), e);
        return BaseResult.wrap(500, e.getMessage());
    }
}
