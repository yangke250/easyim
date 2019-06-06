package com.easyim.biz.db;

import java.util.Collection;

import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShardingTableAlgorithm implements PreciseShardingAlgorithm<Long>{

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {

		String result =  "t_message_0"+shardingValue.getValue()%10;
		log.info("ShardingTableAlgorithm:{}",result);
		return result;
	}


}
