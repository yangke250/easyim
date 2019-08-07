package com.easyim.biz.task;

import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.service.message.IMessageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SynMessageTask {
	private static final LinkedBlockingQueue<SendMsgDto> SYN_QUEUE = new LinkedBlockingQueue<SendMsgDto>();

	@Resource
	private IMessageService service;
	
	public static void addTask(SendMsgDto dto){
		SYN_QUEUE.add(dto);
	}
	
	@PostConstruct
	public void init(){
		for(int i=0;i<Runtime.getRuntime().availableProcessors()*2;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(true){
						try {
							SendMsgDto dto = SYN_QUEUE.take();
							service.sendMsg(dto);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("exception:{}",e);
						}
					}
				}
			}).start();
		}
	}
	
}
