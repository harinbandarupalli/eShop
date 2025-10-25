package com.bilbo.store.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bilbo.security")
public class BilboSecurityProperties {

    private List<String> whiteListUrls;
    private String principalAttribute;
    private String resourceId;
}
