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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan(basePackages = {"com.vtest.it.tskplatform.dao.vtptmt"},sqlSessionTemplateRef ="vtptmtSqlsessionTemplate")
public class DataSourceVtptmt {
    @Bean(value = "vtptmtDataSource",initMethod = "init",destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.vtptmt")
    @Primary
    public DruidDataSource DataSource() {
        DruidDataSource dataSource=new DruidDataSource();
        try {
            dataSource.setFilters("stat,wall,log4j");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }
    @Bean(value = "vtptmtSqlsessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("vtptmtDataSource") DruidDataSource druidDataSource) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(druidDataSource);
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:/mybatisConfig/vtptmt/config.xml"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mappers/vtptmt/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }
    @Bean("vtptmtSqlsessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("vtptmtSqlsessionFactory") SqlSessionFactory sqlSessionFactory){
        SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sqlSessionFactory);
        return  sqlSessionTemplate;
    }
    @Bean("vtptmtTransactionManager")
    @Primary
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("vtptmtDataSource") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}
