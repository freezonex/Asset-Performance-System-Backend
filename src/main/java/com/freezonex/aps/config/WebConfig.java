package com.freezonex.aps.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.core.io.Resource;
import java.io.IOException;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 这里配置了静态资源的位置和自定义资源解析
        registry.addResourceHandler("/apsfrontend/**")
                .addResourceLocations("classpath:/static/apps/freezonex-aps/apsfrontend/")
                .resourceChain(true)
                .addResolver(new HtmlPathResourceResolver());
        registry.addResourceHandler("/apsbackend/**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

    }
    // 自定义资源解析器
    class HtmlPathResourceResolver extends PathResourceResolver {
        @Override
        protected Resource getResource(String resourcePath, Resource location) throws IOException {
            // 尝试获取不带.html后缀的资源
            Resource requestedResource = location.createRelative(resourcePath);
            if (requestedResource.exists() && requestedResource.isReadable()) {
                return requestedResource;
            }
            // 尝试获取带.html后缀的资源
            requestedResource = location.createRelative(resourcePath + ".html");
            if (requestedResource.exists() && requestedResource.isReadable()) {
                return requestedResource;
            }
            return null;
        }
    }
}
