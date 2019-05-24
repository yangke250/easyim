package com.wl.easyim.biz.config;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;

@Configuration
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

	@Bean(initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource(){
		
		
		DruidDataSource dds = new DruidDataSource();
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

}
