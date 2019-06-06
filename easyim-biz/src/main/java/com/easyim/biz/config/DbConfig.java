package com.easyim.biz.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.easyim.biz.db.DatabaseType;
import com.easyim.biz.db.DynamicDataSource;

@Component
@Configuration
public class DbConfig {

	@Bean("datasource")
	public DynamicDataSource dataSource(@Qualifier("im") DataSource im,
	              @Qualifier("message") DataSource message) {
	          Map<Object,Object> targetDataSources = new HashMap<>();
	          targetDataSources.put(DatabaseType.im, im);
	          targetDataSources.put(DatabaseType.message, message);
	  
	          DynamicDataSource dataSource = new DynamicDataSource();
	          dataSource.setTargetDataSources(targetDataSources);// 该方法是AbstractRoutingDataSource的方法
	          dataSource.setDefaultTargetDataSource(im);// 默认的datasource设置为myTestDbDataSource
	  
	          return dataSource;
	 }
	
	@Bean
	public DataSourceTransactionManager transactionManager(@Qualifier("datasource") DynamicDataSource dynamicDataSource) {
	     return new DataSourceTransactionManager(dynamicDataSource);
	}

	
	@Bean
	public SqlSessionFactory sqlSessionFactory(@Qualifier("datasource") DynamicDataSource dynamicDataSource) throws Exception {
		   SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
	       sessionFactory.setDataSource(dynamicDataSource);
	       return sessionFactory.getObject();
	}
}
