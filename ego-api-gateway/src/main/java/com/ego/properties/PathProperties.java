package com.ego.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 *
 **/
@Data
@ConfigurationProperties(prefix = "ego.filter")
public class PathProperties {
    private List<String> allowPaths;
}
