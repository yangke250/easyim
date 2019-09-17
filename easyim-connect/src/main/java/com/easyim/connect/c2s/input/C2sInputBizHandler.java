package com.easyim.connect.c2s.input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.Auth;
import com.easyim.biz.api.protocol.c2s.AuthAck;
import com.easyim.biz.api.protocol.c2s.PingAck;
import com.easyim.biz.api.protocol.enums.c2s.C2sType;
import com.easyim.biz.api.protocol.enums.c2s.EasyImC2sType;
import com.easyim.biz.api.protocol.enums.c2s.Result;
import com.easyim.biz.api.service.c2s.handle.IC2sHandleService;
import com.easyim.connect.c2s.server.WebsocketC2sServer;
import com.easyim.connect.session.Session;
import com.easyim.connect.session.SessionManager;
import com.easyim.connect.session.Session.SessionStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.channel.ChannelHandler.Sharable;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author wl
 *
 */
@Service
@Slf4j
@Sharable
public class C2sInputBizHandler extends AbstractC2sInputHandler {

	@Reference(lazy=true,check = false,retries=1)
	private IC2sHandleService c2sHandleService;

	private static C2sProtocol authError = new C2sProtocol(EasyImC2sType.kickOff);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		SessionManager.removeSession(ctx,null);
	}
	
	
	private void doProtocol(ChannelHandlerContext ctx,String str){
		log.info("doProtocol:{}",str);
		
		C2sProtocol c2sProtocol = JSON.parseObject(str, C2sProtocol.class);
		String type = c2sProtocol.getType();

		Session session = SessionManager.getSession(ctx);
		if (session==null && !EasyImC2sType.auth.getValue().equals(type)) {

			SessionManager.removeSession(ctx, authError);
			return;
		}

		C2sProtocol ackProtocol = c2sHandleService.handleProtocol(SessionManager.getUserDto(ctx), c2sProtocol,
				new HashMap<String, String>());
		if(ackProtocol==null){
			return;
		}
		
		String ackType = ackProtocol.getType();

		switch (ackType) {
		case "pingAck":
			PingAck pingAck = JSON.parseObject(ackProtocol.getBody(),PingAck.class);
			
			ctx.channel().writeAndFlush(JSON.toJSONString(ackProtocol));

			if (pingAck.getResult() != Result.success) {
				SessionManager.removeSession(ctx, ackProtocol);
			}
			return;
		case "authAck":
			Auth auth = JSON.parseObject(c2sProtocol.getBody(),Auth.class);
			
			AuthAck authAck = JSON.parseObject(ackProtocol.getBody(),AuthAck.class);
			
			ctx.channel().writeAndFlush(JSON.toJSONString(ackProtocol));
			log.info("authAck:{}", JSON.toJSONString(authAck));

			if (authAck.getResult() == Result.success) {
				ctx.pipeline().remove(C2sInputTimeoutHandler.class);

				ctx.pipeline()
						.addFirst(new C2sInputTimeoutHandler(auth.getTimeoutCycle() * WebsocketC2sServer.DEFAULT_TIMEOUT));

				SessionManager.addSession(ctx, authAck, auth.getTimeoutCycle());
			} else {
				SessionManager.removeSession(ctx, ackProtocol);
			}
			return;
		default:
			ctx.channel().writeAndFlush(JSON.toJSONString(ackProtocol));
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		String str = (String) msg;
		try{
			doProtocol(ctx,str);
		}catch(Exception e){
			log.error("channelRead exception:"+str,e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			super.exceptionCaught(ctx, cause);
			log.error("exceptionCaught:{}",cause);
	}
}
