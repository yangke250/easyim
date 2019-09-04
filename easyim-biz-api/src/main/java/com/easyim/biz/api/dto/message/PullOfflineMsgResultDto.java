package com.easyim.biz.api.dto.message;

import java.util.ArrayList;
import java.util.List;

import com.easyim.biz.api.dto.protocol.C2sProtocol;

import lombok.Data;

/**
 * 未同步消息返回
 * @author wl
 *
 */
@Data
public class PullOfflineMsgResultDto {
	private List<C2sProtocol> list = new ArrayList<C2sProtocol>();//消息数
	private boolean more = false;//是否有更多的未同步消息
}
