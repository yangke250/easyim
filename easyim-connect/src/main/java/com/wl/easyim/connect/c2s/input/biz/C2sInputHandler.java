package com.wl.easyim.connect.c2s.input.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.wl.easyim.biz.api.dto.user.UserSessionDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.wl.easyim.biz.api.protocol.enums.c2s.Result;
import com.wl.easyim.biz.api.protocol.protocol.c2s.Auth;
import com.wl.easyim.biz.api.protocol.protocol.c2s.AuthAck;
import com.wl.easyim.biz.api.protocol.protocol.c2s.PingAck;
import com.wl.easyim.biz.api.service.protocol.IC2sHandleService;
import com.wl.easyim.connect.c2s.server.WebsocketC2sServer;
import com.wl.easyim.connect.session.Session;
import com.wl.easyim.connect.session.SessionManager;
import com.wl.easyim.connect.session.Session.SessionStatus;

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
public class C2sInputHandler extends AbstractC2sInputHandler {

	@Reference(check = false)
	private IC2sHandleService c2sHandleService;

	private static C2sProtocol authError = new C2sProtocol(C2sCommandType.kickOff);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
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
		if (session==null && C2sCommandType.auth != type) {

			SessionManager.removeSession(ctx, authError);
			return;
		}

		C2sProtocol ackProtocol = c2sHandleService.handleProtocol(SessionManager.getUserDto(ctx), c2sProtocol,
				new HashMap<String, String>());
		C2sCommandType ackType = ackProtocol.getType();

		switch (ackType) {
		case pingAck:
			PingAck pingAck = JSON.parseObject(ackProtocol.getBody(), PingAck.class);

			ctx.channel().writeAndFlush(JSON.toJSONString(ackProtocol));

			if (pingAck.getResult() != Result.success) {
				SessionManager.removeSession(ctx, ackProtocol);
			}
			return;
		case authAck:
			Auth auth = JSON.parseObject(c2sProtocol.getBody(), Auth.class);

			AuthAck authAck = JSON.parseObject(ackProtocol.getBody(), AuthAck.class);

			ctx.channel().writeAndFlush(JSON.toJSONString(ackProtocol));
			log.info("authAck:{}", JSON.toJSONString(authAck));

			if (authAck.getResult() == Result.success) {
				ctx.pipeline().remove(C2sTimeoutInputHandler.class);

				ctx.pipeline()
						.addLast(new C2sTimeoutInputHandler(auth.getTimeOutCycle() * WebsocketC2sServer.DEFAULT_TIMEOUT));

				SessionManager.addSession(ctx, authAck, auth.getTimeOutCycle());
			} else {
				SessionManager.removeSession(ctx, ackProtocol);
			}
			return;
		default:
			ctx.channel().writeAndFlush(JSON.toJSONString(ackProtocol));
		}
	}

//	@Override
//	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		if (cause instanceof ReadTimeoutException) {
//			SessionManager.removeSession(ctx, SessionManager.TIMEOUT);
//		} else {
//			super.exceptionCaught(ctx, cause);
//		}
//	}
}
