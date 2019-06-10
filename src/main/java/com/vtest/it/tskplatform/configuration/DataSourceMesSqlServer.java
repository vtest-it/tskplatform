package com.vtest.it.tskplatform.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan(value = "com.vtest.it.tskplatform.dao.mes",sqlSessionTemplateRef = "mesSqlSessionTemplate")
public class DataSourceMesSqlServer {
    @Bean(initMethod = "init",destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.mes")
    public DruidDataSource mesDataSource(){
        DruidDataSource dataSource=new DruidDataSource();
        try {
            dataSource.setFilters("stat,wall,log4j");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }
    @Bean
    public SqlSessionFactory mesSqlsessionFactory(@Qualifier("mesDataSource") DataSource dataSource)throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:/mybatisConfig/mes/config.xml"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mappers/mes/*.xml"));
        return  sqlSessionFactoryBean.getObject();
    }
    @Bean
    public SqlSessionTemplate mesSqlSessionTemplate(@Qualifier("mesSqlsessionFactory") SqlSessionFactory sqlSessionFactory){
        SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sqlSessionFactory);
        return sqlSessionTemplate;
    }
    @Bean
    public DataSourceTransactionManager mesDataSourceTransactionManager(@Qualifier("mesDataSource") DruidDataSource mesDataSource){
        return  new DataSourceTransactionManager(mesDataSource);
    }
}
