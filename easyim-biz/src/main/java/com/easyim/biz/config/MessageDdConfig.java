package com.easyim.biz.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;

import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.algorithm.sharding.ShardingValue;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingjdbc.core.api.config.strategy.ShardingStrategyConfiguration;
import io.shardingjdbc.core.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingjdbc.core.routing.strategy.ShardingStrategy;

@Component
@Configuration
@MapperScan(basePackages={"com.easyim.biz.mapper.message"})
public class MessageDdConfig {
	
	@Value("${jdbc.url.msg0}")
	private String url0;
	
	@Value("${jdbc.username.msg0}")
	private String username0;
	
	@Value("${jdbc.password.msg0}")
	private String password0;
	
	@Value("${jdbc.url.msg1}")
	private String url1;
	
	@Value("${jdbc.username.msg1}")
	private String username1;
	
	@Value("${jdbc.password.msg1}")
	private String password1;
	
	@Value("${jdbc.url.msg2}")
	private String url2;
	
	@Value("${jdbc.username.msg2}")
	private String username2;
	
	@Value("${jdbc.password.msg2}")
	private String password2;
	
	
	@Value("${jdbc.url.msg3}")
	private String url3;
	
	@Value("${jdbc.username.msg3}")
	private String username3;
	
	@Value("${jdbc.password.msg3}")
	private String password3;
	
	@Value("${jdbc.url.msg4}")
	private String url4;
	
	@Value("${jdbc.username.msg4}")
	private String username4;
	
	@Value("${jdbc.password.msg4}")
	private String password4;
	
	@Value("${jdbc.url.msg5}")
	private String url5;
	
	@Value("${jdbc.username.msg5}")
	private String username5;
	
	@Value("${jdbc.password.msg5}")
	private String password5;
	
	@Value("${jdbc.url.msg6}")
	private String url6;
	
	@Value("${jdbc.username.msg6}")
	private String username6;
	
	@Value("${jdbc.password.msg6}")
	private String password6;
	
	@Value("${jdbc.url.msg7}")
	private String url7;
	
	@Value("${jdbc.username.msg7}")
	private String username7;
	
	@Value("${jdbc.password.msg7}")
	private String password7;
	
	@Value("${jdbc.url.msg8}")
	private String url8;
	
	@Value("${jdbc.username.msg8}")
	private String username8;
	
	@Value("${jdbc.password.msg8}")
	private String password8;
	
	@Value("${jdbc.url.msg9}")
	private String url9;
	
	@Value("${jdbc.username.msg9}")
	private String username9;
	
	@Value("${jdbc.password.msg9}")
	private String password9;
	
	
	private StatFilter getStatFilter(){
		StatFilter sf = new StatFilter();
		sf.setSlowSqlMillis(1000);
		sf.setLogSlowSql(true);
		sf.setMergeSql(true);
		return sf;
	}

	@Bean(name="easyim.msgDb0",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource0(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url0);
		dds.setUsername(username0);
		dds.setPassword(password0);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	@Bean(name="easyim.msgDb1",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource1(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url1);
		dds.setUsername(username1);
		dds.setPassword(password1);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	@Bean(name="easyim.msgDb2",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource2(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url2);
		dds.setUsername(username2);
		dds.setPassword(password2);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	@Bean(name="easyim.msgDb3",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource3(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url3);
		dds.setUsername(username3);
		dds.setPassword(password3);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	@Bean(name="easyim.msgDb4",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource4(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url4);
		dds.setUsername(username4);
		dds.setPassword(password4);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	@Bean(name="easyim.msgDb5",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource5(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url5);
		dds.setUsername(username5);
		dds.setPassword(password5);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	@Bean(name="easyim.msgDb6",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource6(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url6);
		dds.setUsername(username6);
		dds.setPassword(password6);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	@Bean(name="easyim.msgDb7",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource7(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url7);
		dds.setUsername(username7);
		dds.setPassword(password7);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	@Bean(name="easyim.msgDb8",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource8(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url8);
		dds.setUsername(username8);
		dds.setPassword(password8);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	
	@Bean(name="easyim.msgDb9",initMethod="init",destroyMethod="close")
	public DruidDataSource getDruidDataSource9(){
		DruidDataSource dds = new DruidDataSource();
		dds.setUrl(url9);
		dds.setUsername(username9);
		dds.setPassword(password9);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("set names utf8mb4;");
		dds.setConnectionInitSqls(arrayList);
		dds.setConnectionProperties("config.decrypt=false");
		dds.setInitialSize(ImDbConfig.INIT_SIZE);
		dds.setMinIdle(ImDbConfig.MIN_IDLE);
		dds.setMaxActive(ImDbConfig.MAX_SIZE);
		
		ArrayList<Filter> statFilterList = new ArrayList<Filter>();
		statFilterList.add(getStatFilter());
		
		
		dds.setProxyFilters(statFilterList);
		
		return dds;
	}
	
	
	
	public static void main(String[] args){
		System.out.println(2/10%10);
	}
	
	
	@Bean(name="easyim.msgShardingDb")
	public DataSource getDataSource(@Qualifier("easyim.msgMap") Map<String, DataSource> map) throws SQLException {
		
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getMessageTableRuleConfiguration());
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(
        		new StandardShardingStrategyConfiguration("proxy_cid","com.easyim.biz.db.ShardingDbAlgorithm"));
        		//new InlineShardingStrategyConfiguration("proxy_cid", "im_msg_0${(proxy_cid/10)%10}"));
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(
        		new StandardShardingStrategyConfiguration("proxy_cid","com.easyim.biz.db.ShardingTableAlgorithm"));
        		//new InlineShardingStrategyConfiguration("proxy_cid", "t_message_0${proxy_cid%10}"));
        
        return ShardingDataSourceFactory.createDataSource(map, shardingRuleConfig,new HashMap<String, Object>(),null);

    }
	
	private TableRuleConfiguration getMessageTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration();
        result.setLogicTable("t_message");
        result.setActualDataNodes("im_msg_0${0..9}.t_message_0${0..9}");
        return result;
    }
	
	
	@Bean(name="easyim.msgMap")
	public Map<String, DataSource> createDataSourceMap(
			@Qualifier("easyim.msgDb0") DruidDataSource msg0,
			@Qualifier("easyim.msgDb1") DruidDataSource msg1,
			@Qualifier("easyim.msgDb2") DruidDataSource msg2,
			@Qualifier("easyim.msgDb3") DruidDataSource msg3,
			@Qualifier("easyim.msgDb4") DruidDataSource msg4,
			@Qualifier("easyim.msgDb5") DruidDataSource msg5,
			@Qualifier("easyim.msgDb6") DruidDataSource msg6,
			@Qualifier("easyim.msgDb7") DruidDataSource msg7,
			@Qualifier("easyim.msgDb8") DruidDataSource msg8,
			@Qualifier("easyim.msgDb9") DruidDataSource msg9
			) {
         Map<String, DataSource> result = new HashMap<String, DataSource>();
         result.put("im_msg_00", msg0);
         result.put("im_msg_01", msg1);
         result.put("im_msg_02", msg2);
         result.put("im_msg_03", msg3);
         result.put("im_msg_04", msg4);
         result.put("im_msg_05", msg5);
         result.put("im_msg_06", msg6);
         result.put("im_msg_07", msg7);
         result.put("im_msg_08", msg8);
         result.put("im_msg_09", msg9);
         
         return result;
     }
	
	
}
