package com.lb.lbclock.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseController {
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
//		binder.registerCustomEditor(Date.class, new DateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
	}

	/**
	 * 获得request中的参数
	 *
	 * @param request
	 * @return string object类型的map
	 */
	public HashMap<String, Object> getParametersMap(HttpServletRequest request) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if (request == null) {
			request = ((ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes()).getRequest();
		}
		Map req = request.getParameterMap();
		if ((req != null) && (!req.isEmpty())) {
			Map<String, Object> p = new HashMap<String, Object>();
			Collection keys = req.keySet();
			for (Iterator i = keys.iterator(); i.hasNext();) {
				String key = (String) i.next();
				Object value = req.get(key);
				Object v = null;
				if ((value.getClass().isArray())
						&& (((Object[]) value).length > 0)) {
					if (((Object[]) value).length > 1) {
						v = ((Object[]) value);
					} else {
						v = ((Object[]) value)[0];
					}
				} else {
					v = value;
				}
				if ((v != null) && ((v instanceof String))) {
					String s = ((String) v).trim();
					if (s.length() > 0) {
						p.put(key, s);
					}
				}
			}
			hashMap.putAll(p);
			// 读取cookie
//			hashMap.putAll(ReadCookieMap(request));
		}
		if (StringUtils.isNotBlank(request.getParameter("rows")) && StringUtils.isNotBlank(request.getParameter("page"))){
			int rows = Integer.valueOf(request.getParameter("rows"));
			int page = Integer.valueOf(request.getParameter("page"));
			int nowPage =(page - 1) * rows;
			hashMap.put("page", nowPage);
			hashMap.put("rows", rows);
		}
		return hashMap;
	}
	/**
	 * 将cookie封装到Map里面
	 *
	 * @param request
	 * @return
	 */
	private static Map<String, String> ReadCookieMap(HttpServletRequest request) {
		Map<String, String> cookieMap = new HashMap<String, String>();
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				cookieMap.put(cookie.getName(), cookie.getValue());
			}
		}
		return cookieMap;
	}

}
