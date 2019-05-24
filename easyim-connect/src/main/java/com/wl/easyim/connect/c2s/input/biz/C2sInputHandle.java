package com.wl.easyim.connect.c2s.input.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.wl.easyim.biz.api.dto.user.UserDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.wl.easyim.biz.api.protocol.enums.c2s.Result;
import com.wl.easyim.biz.api.protocol.protocol.c2s.AbstractAckProtocol;
import com.wl.easyim.biz.api.protocol.protocol.c2s.Auth;
import com.wl.easyim.biz.api.protocol.protocol.c2s.AuthAck;
import com.wl.easyim.biz.api.protocol.protocol.c2s.PingAck;
import com.wl.easyim.biz.api.service.protocol.IC2sHandleService;
import com.wl.easyim.connect.session.Session;
import com.wl.easyim.connect.session.SessionManager;
import com.wl.easyim.connect.session.SessionTimeWheel;
import com.wl.easyim.connect.session.Session.SessionStatus;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @author wl
 *
 */
@Service
public class C2sInputHandle extends AbstractC2sInputHandle {

	@Reference(check=false)
	private IC2sHandleService c2sHandleService;

	
	private static C2sProtocol authError = C2sProtocol.builder().type(C2sCommandType.kickOff).build();

	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);

		Session session = new Session();
		session.setChc(ctx);

		SessionManager.addSession(session);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		String str = (String) msg;

		C2sProtocol c2sProtocol = JSON.parseObject(str, C2sProtocol.class);
		C2sCommandType type = c2sProtocol.getType();

		Session session = SessionManager.getSession(ctx);
		if (SessionStatus.anonymous == session.getSessionStatus() && C2sCommandType.auth != type) {

			SessionManager.removeSession(ctx, authError);
			return;
		}

		
		C2sProtocol ackProtocol = c2sHandleService.
				handleProtocol(SessionManager.getUserDto(ctx),c2sProtocol,new HashMap<String,String>());
		C2sCommandType ackType = ackProtocol.getType();

		switch (ackType) {
		case pingAck:
			PingAck pingAck = JSON.parseObject(ackProtocol.getBody(), PingAck.class);

			if (pingAck.getResult() == Result.success) {
				SessionTimeWheel.resetTimeWheel(session);

				ctx.writeAndFlush(JSON.toJSONString(ackProtocol));
			} else {
				SessionManager.removeSession(ctx, ackProtocol);
			}
			return;
		case authAck:
			Auth auth = JSON.parseObject(c2sProtocol.getBody(), Auth.class);
			
			AuthAck authAck = JSON.parseObject(ackProtocol.getBody(), AuthAck.class);

			if (authAck.getResult() == Result.success) {
				SessionManager.updateSessionStatus(ctx,authAck,auth.getTimeOutCycle());
				
				ctx.writeAndFlush(JSON.toJSONString(ackProtocol));
			} else {
				SessionManager.removeSession(ctx, ackProtocol);
			}
			return;
		default:
			ctx.writeAndFlush(JSON.toJSONString(ackProtocol));
		}
	}

}
