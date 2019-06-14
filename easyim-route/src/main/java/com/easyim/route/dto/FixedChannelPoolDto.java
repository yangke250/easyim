package com.easyim.route.dto;

import com.easyim.route.inputHandler.pool.RouteFixedChannelPool;

import io.netty.channel.pool.FixedChannelPool;
import lombok.Data;

@Data
public class FixedChannelPoolDto {
	private RouteFixedChannelPool fixedChannelPool;
	private String password;
}
