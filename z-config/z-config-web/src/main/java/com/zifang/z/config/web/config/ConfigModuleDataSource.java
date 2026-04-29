package com.zifang.z.config.web.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zifang.z.boot.datasource.starter.ModuleDataSourceTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.zifang.z.config.core.domain.mapper", sqlSessionFactoryRef = "sqlSessionFactoryConfig")
public class ConfigModuleDataSource extends ModuleDataSourceTemplate {

    @Bean("dataSourceConfig")
    public DataSource dataSource(org.springframework.core.env.Environment env) {
        return buildDataSource(env, "config");
    }

    @Bean("sqlSessionFactoryConfig")
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSourceConfig) throws Exception {
        return buildSqlSessionFactory(dataSourceConfig);
    }
}
