package com.shanfu.app.config;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * PageHelper 分页插件手动配置
 * 不依赖 pagehelper-spring-boot-starter 的自动装配，避免 Spring Boot 4.x 兼容问题
 */
@Configuration
public class PageHelperConfig {

    @Bean
    public Interceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties props = new Properties();
        // 数据库方言，MySQL 专用
        props.setProperty("helperDialect", "mysql");
        // 页码越界自动修正：pageNum<=0 时修正为第 1 页，pageNum>总页数时修正为最后一页
        props.setProperty("reasonable", "true");
        // 支持通过 Mapper 方法参数传递分页参数
        props.setProperty("supportMethodsArguments", "true");
        // 使用 RowBounds 分页时也进行 count 查询
        props.setProperty("rowBoundsWithCount", "true");
        pageInterceptor.setProperties(props);
        return pageInterceptor;
    }
}
