package com.zifang.z.ctc.web.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zifang.z.boot.datasource.starter.ModuleDataSourceTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.zifang.ctc.core.domain.mapper", sqlSessionFactoryRef = "sqlSessionFactoryCtc")
public class DataSourceConfigForCtc extends ModuleDataSourceTemplate {

    @Bean("dataSourceCtc")
    public DataSource dataSource(Environment env) {
        return buildDataSource(env, "oc");
    }

    @Bean("sqlSessionFactoryCtc")
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSourceCtc) throws Exception {
        return buildSqlSessionFactory(dataSourceCtc);
    }
}