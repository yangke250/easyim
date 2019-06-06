package com.easyim.biz.api.protocol.c2s;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import lombok.Data;

/**
 * 消息协议的定义
 * @author wl
 *
 */
@Data
public abstract class AbstractMessagePush extends AbstractProtocol{
	private static final long serialVersionUID = 7545645657214366760L;
    @Protobuf
	private long tenementId;
    @Protobuf
    private long id;
    @Protobuf
	private int type;
    @Protobuf
	private String subType;
    @Protobuf
    private String fromId;
    @Protobuf
    private String toId;
    @Protobuf
    private String content;//消息内容
    @Protobuf
	private String time;//消息时间
    @Protobuf
    private String bizUuid;//业务uuid
	

	
	

	

}
