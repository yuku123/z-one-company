package com.zifang.z.agent.skill.web.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zifang.z.boot.datasource.starter.ModuleDataSourceTemplate;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.zifang.z.agent.skill.core.domain.mapper", sqlSessionFactoryRef = "sqlSessionFactorySkill")
public class DataSourceConfigForSkill extends ModuleDataSourceTemplate {

    @Bean("dataSourceSkill")
    public DataSource dataSource(Environment env) {
        return buildDataSource(env, "oc");
    }

    @Bean("sqlSessionFactorySkill")
    public SqlSessionFactory sqlSessionFactorySkill(DataSource dataSourceSkill) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSourceSkill);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/**/*.xml"));
        factoryBean.setTypeAliasesPackage("com.zifang.z.agent.skill.core.domain.entity");
        return factoryBean.getObject();
    }
}
