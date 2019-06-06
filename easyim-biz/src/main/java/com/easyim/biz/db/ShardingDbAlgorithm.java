package com.easyim.biz.db;

import java.util.Collection;

import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShardingDbAlgorithm implements PreciseShardingAlgorithm<Long>{

	public static void main(String[] args){
		System.out.println(22/10%10);
	}
	
	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
		
		String result =  "im_msg_0"+(shardingValue.getValue()/10%10);
		log.info("ShardingDbAlgorithm:{}",result);
		return result;
	}

//	@Override
//	public String doSharding(Collection availableTargetNames, PreciseShardingValue<Long> shardingValue) {
//		// TODO Auto-generated method stub
//		return "im_msg_0"+(Long.parseLong(shardingValue.getValue())/10%10);
//	}

}
