package com.easyim.biz.service.conversation.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.easyim.biz.api.service.conversation.IConversationService;
import com.easyim.biz.api.service.conversation.IProxyConversationService;
import com.easyim.biz.domain.ProxyConversationDo;
import com.easyim.biz.mapper.conversation.IProxyConversationMapper;


@Service(interfaceClass=IProxyConversationService.class)
public class ProxyConversationServiceImpl implements IProxyConversationService {

	@Resource
	private IProxyConversationMapper proxyConversationMapper;
	
	@Override
	public long getProxyCid(long tenementId, String proxyFromId, String proxyToId) {

		String proxySmallId;
		String proxyBigId;
		if(proxyFromId.compareTo(proxyToId)>0){
			proxyBigId   = proxyFromId;
			proxySmallId = proxyToId;
		}else{
			proxyBigId   = proxyToId;
			proxySmallId = proxyFromId;
		}
		ProxyConversationDo proxyConversation = proxyConversationMapper.getProxyConversation(tenementId, proxySmallId, proxyBigId);
		if(proxyConversation==null){
			proxyConversation = new ProxyConversationDo();
			proxyConversation.setTenementId(tenementId);
			proxyConversation.setProxySmallId(proxyFromId);
			proxyConversation.setProxyBigId(proxyToId);
			proxyConversationMapper.insertProxyConversationDo(proxyConversation);
		}
			return proxyConversation.getId();
	
	}

}
