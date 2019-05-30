package com.easyim.biz.config;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;

@Component
@Configuration
@PropertySource(
value = {
		"classpath:application-easyim-biz.properties",
		"classpath:application-easyim-biz-${spring.profiles.active}.properties"},
ignoreResourceNotFound = true, encoding = "UTF-8")
public class DbConfig {
	@Value("${jdbc.url}")
	private String url;
	
	@Value("${jdbc.username}")
	private String username;
	
	@Value("${jdbc.password}")
	private String password;
	
	private StatFilter getStatFilter(){
		StatFilter sf = new StatFilter();
		sf.setSlowSqlMillis(1000);
		sf.setLogSlowSql(true);
		sf.setMergeSql(true);
		return sf;
	}

	private DruidDataSource dds = new DruidDataSource();

	@Bean(initMethod="init",destroyMethod="close")
	public synchronized DruidDataSource getDruidDataSource(){
		
		
		dds.setUrl(url);
		dds.setUsername(username);
		dds.setPassword(password);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(5);
		dds.setMinIdle(0);
		dds.setMaxActive(50);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		return dds;
	}
	
	@Bean
	public DataSourceTransactionManager transactionManager() {
	     return new DataSourceTransactionManager(getDruidDataSource());
	}

	
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		   SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
	       sessionFactory.setDataSource(getDruidDataSource());
	       return sessionFactory.getObject();
	}
}
