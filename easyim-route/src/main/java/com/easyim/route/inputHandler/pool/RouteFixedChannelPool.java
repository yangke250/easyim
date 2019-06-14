package com.easyim.route.inputHandler.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import lombok.Data;

@Data
public class RouteFixedChannelPool extends FixedChannelPool {

	private volatile boolean close = false;
	
	public RouteFixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int maxConnections) {
		super(bootstrap, handler, maxConnections);
	}

	
	
	public void close(){
		super.releaseHealthCheck();
		
		close = true;
		super.close();
	}
}
