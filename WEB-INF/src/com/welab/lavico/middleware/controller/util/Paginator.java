package com.welab.lavico.middleware.controller.util;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class Paginator {
	
	public static void paginate(HttpServletRequest request,Map<String,Object> out) throws Error {

		// 处理Get参数 pageNum
		String sPage = request.getParameter("pageNum") ;
		if(sPage==null){
			sPage = "1" ;		// 默认值
		}
		int iPage = 1 ;
		try{
			iPage = Integer.parseInt(sPage) ;
		} catch (NumberFormatException e) {
			throw new Error("parameter pageNum is not valid format.",e) ;
		}

		// 处理Get参数 perPage
		String nPerPage = request.getParameter("perPage") ;
		if(nPerPage==null){
			nPerPage = "20" ;	// 默认值
		}
		int iPerPage = 20 ;
		try{
			iPerPage = Integer.parseInt(nPerPage) ;
		} catch (NumberFormatException e) {
			throw new Error("parameter perPage is not valid format.",e) ;
		}

		out.put("pageNum", iPage) ;
		out.put("perPage", iPerPage) ;
		
	}
}
