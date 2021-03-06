package org.wsh.common.web.filter;

import java.net.URLEncoder;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.wsh.common.web.util.SessionUtil;
import org.wsh.common.enums.SessionKey;
import org.wsh.common.model.basic.UserBasicDO;
import org.wsh.common.service.api.UserService;

/**
 * 登录成功后调用
 * author: wsh
 * JDK-version:  JDK1.8
 * comments:  对此类的描述，可以引用系统设计中的描述
 * since Date： 2017/3/6 22:29
 */
@Component
public class LoginSuccessFilter extends FormAuthenticationFilter{
	
	@Autowired 
	private UserService userService;
	
	@Override
	public boolean onLoginSuccess(AuthenticationToken token,
			Subject currentUser, ServletRequest request, ServletResponse response)
			throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		currentUser = SecurityUtils.getSubject();
		String userName = (String)currentUser.getPrincipal();
		UserBasicDO user = userService.getUserBasicByUserName(userName);
		SessionUtil.setSession(httpServletRequest, SessionKey.user, user);
		String uName = URLEncoder.encode(userName, "UTF-8");
		//loginName放入cookie
		Cookie cookie = new Cookie("userName",uName);
		cookie.setMaxAge(-1);
		cookie.setPath(httpServletRequest.getContextPath().length()>0?httpServletRequest.getContextPath() : "/");
		cookie.setSecure(false);
		httpServletResponse.addCookie(cookie);
		return super.onLoginSuccess(token, currentUser, request, response);
	}
	
}
