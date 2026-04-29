package com.zifang.z.boot.datasource.starter;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

public class ModuleDataSourceTemplate {

    protected DataSource buildDataSource(org.springframework.core.env.Environment env, String module) {
        Binder binder = Binder.get(env);
        String modulePrefix = "z.base.db." + module;
        String defaultPrefix = "z.base.db.default";

        String host = getOrDefault(binder, modulePrefix + ".host", defaultPrefix + ".host", "localhost");
        Integer port = getOrDefault(binder, modulePrefix + ".port", defaultPrefix + ".port", 3306);
        String username = getOrDefault(binder, modulePrefix + ".username", defaultPrefix + ".username", "root");
        String password = getOrDefault(binder, modulePrefix + ".password", defaultPrefix + ".password", "");
        String database = getOrDefault(binder, modulePrefix + ".database", defaultPrefix + ".database", "");

        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(String.format("jdbc:mysql://%s:%d/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8", host, port, database));
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setInitialSize(getOrDefault(binder, modulePrefix + ".initial-size", defaultPrefix + ".initial-size", 5));
        ds.setMinIdle(getOrDefault(binder, modulePrefix + ".min-idle", defaultPrefix + ".min-idle", 5));
        ds.setMaxActive(getOrDefault(binder, modulePrefix + ".max-active", defaultPrefix + ".max-active", 20));
        ds.setMaxWait(getOrDefault(binder, modulePrefix + ".max-wait", defaultPrefix + ".max-wait", 60000L));

        return ds;
    }

    protected MybatisSqlSessionFactoryBean buildSqlSessionFactory(DataSource ds) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(ds);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/**/*.xml"));
        factoryBean.setTypeAliasesPackage("com.zifang.ctc.core.domain.entity");
        return factoryBean;
    }

    protected <T> T getOrDefault(Binder binder, String moduleKey, String defaultKey, T defaultValue) {
        return binder.bind(moduleKey, (Class<T>) defaultValue.getClass())
                .orElseGet(() -> binder.bind(defaultKey, (Class<T>) defaultValue.getClass()).orElse(defaultValue));
    }
}
