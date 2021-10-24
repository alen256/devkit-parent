package cn.jiangzhou.devkit.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "devkit.security")
public class DevkitSecurityProperty {

    private EnableMethods enableMethods = new EnableMethods();

    private List<Authorize> authorizes = new ArrayList<>();

    private Boolean swagger = false;

    @Data
    public static class EnableMethods {

        private Boolean password = true;

        private Boolean message = false;

        private Boolean oauth = false;

    }

    @Data
    public static class Authorize {
        private String[] patterns;
        private String[] roles;
        private Boolean permitAll = false;
    }

}
