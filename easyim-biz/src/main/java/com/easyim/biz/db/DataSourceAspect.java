package com.easyim.biz.db;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy=true)
@Component
public class DataSourceAspect {
    

    
    @Around("execution(* com.easyim.biz.mapper.*.*.*(..))")
    public Object setDataSourceKeyByAround(ProceedingJoinPoint point) throws Throwable{
    	MethodSignature signature = (MethodSignature) point.getSignature();  
    	
    	String name = signature.getDeclaringTypeName();
    	
    	boolean result = name.contains("com.easyim.biz.mapper.message");
    	
    	try{
    		if(result){
                DatabaseContextHolder.setDatabaseType(DatabaseType.message);
            }else{//连接点所属的类实例是UserDao（当然，这一步也可以不写，因为defaultTargertDataSource就是该类所用的mytestdb）
                DatabaseContextHolder.setDatabaseType(DatabaseType.im);
            }
            return point.proceed();//执行目标方法
    	}finally{
    		DatabaseContextHolder.remove();
    	}
    	
    }
    
}