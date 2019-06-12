package com.easyim.connect.c2s.input;

import com.easyim.connect.session.SessionManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class C2sInputTimeoutHandler extends ReadTimeoutHandler{
	
    private boolean closed;

	
	public C2sInputTimeoutHandler(int timeoutSeconds) {
		super(timeoutSeconds);
	}
	
	/**
     * Is called when a read timeout was detected.
     */
    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        if (!closed) {
            SessionManager.removeSession(ctx,SessionManager.TIMEOUT);
            closed = true;
        }
    }

}
