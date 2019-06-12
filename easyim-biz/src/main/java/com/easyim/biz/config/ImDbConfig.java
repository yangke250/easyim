package com.easyim.biz.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;

@Component
@Configuration
public class ImDbConfig {

	@Value("${jdbc.url}")
	private String url;
	
	@Value("${jdbc.username}")
	private String username;
	
	@Value("${jdbc.password}")
	private String password;
	
	public static final int INIT_SIZE =2;
	public static final int MIN_IDLE =2;
	public static final int MAX_SIZE =5;
	
	
	private StatFilter getStatFilter(){
		StatFilter sf = new StatFilter();
		sf.setSlowSqlMillis(1000);
		sf.setLogSlowSql(true);
		sf.setMergeSql(true);
		return sf;
	}


	@Bean(name="esayim.imDb",initMethod="init",destroyMethod="close")
	@Primary
	public DruidDataSource getDruidDataSource(){
		
		DruidDataSource dds = new DruidDataSource();

		dds.setUrl(url);
		dds.setUsername(username);
		dds.setPassword(password);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(INIT_SIZE);
		dds.setMinIdle(MIN_IDLE);
		dds.setMaxActive(MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		return dds;
	}
	
	

}
