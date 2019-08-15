package com.easyim.biz.service.user.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.easyim.biz.Launch;
import com.easyim.biz.api.dto.user.UserAuthDto;
import com.easyim.biz.api.dto.user.UserConnectDto;
import com.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.easyim.biz.api.service.user.IUserService;
import com.easyim.route.server.ServerDiscover;
import com.easyim.route.service.IUserRouteService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 用户服务
 * @author wl
 *
 */
@Service(interfaceClass=IUserService.class)
public class UserServiceImpl implements IUserService{

	public static final String PASSWORD="yangke250";

	@Resource
	private IUserRouteService userRouteService;
	
	@Override
	public UserConnectDto getConnectInfo(UserAuthDto userAuthDto) {
		
		String auth = authEncode(userAuthDto);
		
		long tenementId  = userAuthDto.getTenementId();
		String userId    = userAuthDto.getUserId();
		
		String connectServer = userRouteService.getUserRoute(tenementId, userId);
		if(StringUtils.isEmpty(connectServer)){
			connectServer =  ServerDiscover.pollingConnectServer();
		}
		UserConnectDto userConnectDto = new UserConnectDto();
		userConnectDto.setToken(auth);
		userConnectDto.setConnectServer(connectServer.split(":")[0]);
		
		return userConnectDto;
	}
	
	@Override
	public String authEncode(UserAuthDto userAuthDto) {
		Launch.doValidatorDoError(userAuthDto);
		
		long tenementId =userAuthDto.getTenementId();
		ResourceType  resourceType =userAuthDto.getResourceType();
		String userId =userAuthDto.getUserId();
		
		//指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成JWT的时间
        Date now = new Date(System.currentTimeMillis());

        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("tenementId",tenementId);
        claims.put("resourceType",resourceType);


        //这里其实就是new一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUID.randomUUID().toString())
                //iat: jwt的签发时间
                .setIssuedAt(now)
                //代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(userId)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm,PASSWORD);
        //token 1天过期
        Date exp = new Date(now.getTime()+1000*60*60*24);
        builder.setExpiration(exp);
        
        return builder.compact();
	}

	@Override
	public UserAuthDto authDecode(String jwt) {

        //得到DefaultJwtParser
        Claims claims = Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(PASSWORD)
                //设置需要解析的jwt
                .parseClaimsJws(jwt).getBody();
        
        String userId      = claims.getSubject();
        long   tenementId  = claims.get("tenementId",Long.class);
        ResourceType resourceType = ResourceType.valueOf(claims.get("resourceType",String.class));
        
        UserAuthDto userBo = new UserAuthDto();
        userBo.setUserId(userId);
        userBo.setTenementId(tenementId);
        userBo.setResourceType(resourceType);
        return userBo;
	}



}
