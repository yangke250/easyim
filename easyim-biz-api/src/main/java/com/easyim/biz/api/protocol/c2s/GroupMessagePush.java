package com.easyim.biz.api.protocol.c2s;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import lombok.Data;

@Data
public class GroupMessagePush extends AbstractMessagePush{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8449620967796682943L;
    @Protobuf
	private long groupId;
	

}
