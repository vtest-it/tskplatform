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
@MapperScan(basePackages = {"com.vtest.it.tskplatform.dao.prober"},sqlSessionTemplateRef ="probermtSqlsessionTemplate")
public class DataSourceProber {
    @Bean(value = "proberDataSource",initMethod = "init",destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.prober")
    public DruidDataSource DataSource() {
        DruidDataSource dataSource=new DruidDataSource();
        try {
            dataSource.setFilters("stat,wall,log4j");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }
    @Bean(value = "proberSqlsessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("proberDataSource") DruidDataSource druidDataSource) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(druidDataSource);
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:/mybatisConfig/prober/config.xml"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mappers/prober/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }
    @Bean("probermtSqlsessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("proberSqlsessionFactory") SqlSessionFactory sqlSessionFactory){
        SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sqlSessionFactory);
        return  sqlSessionTemplate;
    }
    @Bean("proberTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("proberDataSource") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}
