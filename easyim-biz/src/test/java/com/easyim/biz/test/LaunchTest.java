package com.easyim.biz.test;


import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import com.easyim.biz.Launch;


@RunWith(SpringRunner.class) //SpringJUnit支持，由此引入Spring-Test框架支持！ 
@SpringBootTest(classes={
//		cn.linkedcare.springboot.redis.Launch.class,
//		com.easy.springboot.c2s.Launch.class,
//		com.easyim.route.Launch.class,
		Launch.class})
public abstract class LaunchTest {
	
}