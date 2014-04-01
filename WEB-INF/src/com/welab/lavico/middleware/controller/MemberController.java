package com.welab.lavico.middleware.controller;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.welab.lavico.middleware.service.MemberCardService;


@Controller
public class MemberController {

	/**
	 * 申请会员卡
	 * 
	 * Path Variables:
	 * @param {brand} 				品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param openid 				微信id
	 * @param MEM_PSN_CNAME 		会员姓名
	 * @param MOBILE_TELEPHONE_NO 	手机号码
	 * @param MEM_PSN_SEX		 	性别 0=女, 1=男
	 * @param MEM_PSN_BIRTHDAY	 	生日
	 * 
	 * @return
	 * {
	 * 	MEMBER_ID:   <int>
	 *  issuccessed: <boolean>
	 *  error:		 <string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/Apply")
	public @ResponseBody Map<String,Object> doApply(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();

		Logger.getLogger("Member").info(brand+"/Member/Apply");
		
		try{
			int memid = new MemberCardService().apply(
					brand
					, request.getParameter("openid")
					, request.getParameter("MEM_PSN_CNAME")
					, request.getParameter("MOBILE_TELEPHONE_NO")
					, request.getParameter("MEM_PSN_SEX")
					, request.getParameter("MEM_PSN_BIRTHDAY")
			) ;
			rspn.put("MEMBER_ID",memid) ;
			rspn.put("issuccessed", true) ;
			
		} catch(Throwable e) {
			rspn.remove("MEMBER_ID") ;
			rspn.put("issuccessed", false) ;
			rspn.put("error", e.getMessage()) ;

			Logger.getLogger("Member-error").error("oops, got an Exception:",e);
		}

		return rspn ;
    }

	/**
	 * 绑定老会员卡
	 * 
	 * Path Variables:
	 * @param {brand} 				品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param openid 				微信id
	 * @param MEM_PSN_CNAME 		会员姓名
	 * @param MOBILE_TELEPHONE_NO 	手机号码
	 * @param MEM_OLDCARD_NO		老会员开卡号
	 * 
	 * @return
	 * {
	 * 	MEMBER_ID:   <int>
	 *  issuccessed: <boolean>
	 *  error:		 <string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/Bind")
	public @ResponseBody Map<String,Object> doBind(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();

		try{
			int memid = new MemberCardService().bind(
					brand
					, request.getParameter("openid")
					, request.getParameter("MEM_PSN_CNAME")
					, request.getParameter("MOBILE_TELEPHONE_NO")
					, request.getParameter("MEM_OLDCARD_NO")
			) ;
			rspn.put("MEMBER_ID",memid) ;
			rspn.put("issuccessed", true) ;

		} catch(Throwable e) {
			rspn.remove("MEMBER_ID") ;
			rspn.put("issuccessed", false) ;
			rspn.put("error", e.getMessage()) ;

			Logger.getLogger("Member-error").error("oops, got an Exception:",e);
		}

		return rspn ;
	}


	/**
	 * 会员卡解除绑定接口
	 * 
	 * Path Variables:
	 * @param {brand} 				品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param openid 				微信id
	 * @param MEMBER_ID 			会员ID
	 * 
	 * @return
	 * {
	 *  issuccessed: <boolean>
	 *  error:		 <string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/Unbind")
	public @ResponseBody Map<String,Object> doUnbind(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{
			boolean succ = new MemberCardService().unbind(
					brand
					, request.getParameter("openid")
					, request.getParameter("MEMBER_ID")
			) ;
			rspn.put("issuccessed", succ) ;
		} catch(Throwable e) {
			rspn.put("issuccessed", false) ;
			rspn.put("error", e.getMessage()) ;
			Logger.getLogger("Member-error").error("oops, got an Exception:",e);
		}

		return rspn ;
	}
}