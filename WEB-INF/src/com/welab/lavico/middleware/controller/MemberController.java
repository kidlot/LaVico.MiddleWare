package com.welab.lavico.middleware.controller;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.welab.lavico.middleware.controller.util.Paginator;
import com.welab.lavico.middleware.model.MemberCardModel;
import com.welab.lavico.middleware.model.MemberModel;
import com.welab.lavico.middleware.model.MemberSpendingListModel;
import com.welab.lavico.middleware.model.PointLogModel;
import com.welab.lavico.middleware.service.DaoBrandError;
import com.welab.lavico.middleware.service.MemberCardInfoService;
import com.welab.lavico.middleware.service.MemberCardService;
import com.welab.lavico.middleware.service.SpringJdbcDaoSupport;


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
	 * 	MEMBER_ID:  <int>
	 *  success: 	<boolean>
	 *  error:		<string>
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
			rspn.put("success", true) ;
			
		} catch(Throwable e) {
			rspn.remove("MEMBER_ID") ;
			rspn.put("success", false) ;
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
	 * 	MEMBER_ID:  <int>
	 *  success: 	<boolean>
	 *  error:		<string>
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
			rspn.put("success", true) ;

		} catch(Throwable e) {
			rspn.remove("MEMBER_ID") ;
			rspn.put("success", false) ;
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
	 *  success: 	<boolean>
	 *  error:		<string>
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
			rspn.put("success", succ) ;
		} catch(Throwable e) {
			rspn.put("success", false) ;
			rspn.put("error", e.getMessage()) ;
			Logger.getLogger("Member-error").error("oops, got an Exception:",e);
		}

		return rspn ;
	}

	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/CheckMobileExists")
	public @ResponseBody Map<String,Object> checkMobileExists(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		String mobile = request.getParameter("mobile") ;
		
		try{
			if(mobile==null||mobile.isEmpty()){
				throw new Error("缺少参数 mobile") ;
			}
			JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
			int memberId = new MemberCardModel(jdbcTpl,0).getMemberIdByMobile(brand,mobile) ;

			rspn.put("exists", memberId!=0) ;
			rspn.put("memberId", memberId) ;
			
		} catch(Throwable e) {
			rspn.put("error", e.getMessage()) ;
			Logger.getLogger("Member-error").error("oops, got an Exception:",e);
		}

		return rspn ;
	}

	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/IsMobileChecked")
	public @ResponseBody Map<String,Object> isMobileChecked(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		String mobile = request.getParameter("mobile") ;
		
		try{
			if(mobile==null||mobile.isEmpty()){
				throw new Error("缺少参数 mobile") ;
			}
			JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
			boolean checked = new MemberCardModel(jdbcTpl,0).isMobileChecked(brand,mobile) ;
			
			rspn.put("checked", checked) ;
			
		} catch(Throwable e) {
			rspn.put("error", e.getMessage()) ;
			Logger.getLogger("Member-error").error("oops, got an Exception:",e);
		}

		return rspn ;
	}


	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/getMobileBindOpenid")
	public @ResponseBody Map<String,Object> getMobileBindOpenid(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		String mobile = request.getParameter("mobile") ;
		
		try{
			if(mobile==null||mobile.isEmpty()){
				throw new Error("缺少参数 mobile") ;
			}
			JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
			boolean checked = new MemberCardModel(jdbcTpl,0).getMobileBindOpenid(brand,mobile) ;
			
			rspn.put("checked", checked) ;
			
		} catch(Throwable e) {
			rspn.put("error", e.getMessage()) ;
			Logger.getLogger("Member-error").error("oops, got an Exception:",e);
		}

		return rspn ;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/IsMobileAndOldcardValid")
	public @ResponseBody Map<String,Object> isMobileAndOldcardValid(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();

		String mobile = request.getParameter("mobile") ;
		String oldcard = request.getParameter("oldcard") ;
		
		try{
			if(mobile==null||mobile.isEmpty()){
				throw new Error("缺少参数 mobile") ;
			}
			if(oldcard==null||oldcard.isEmpty()){
				throw new Error("缺少参数 oldcard") ;
			}
			JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
			boolean isValid = new MemberCardModel(jdbcTpl,0).isMobileAndOldcardValid(brand,mobile,oldcard) ;
			
			rspn.put("valid", isValid) ;
			
		} catch(Throwable e) {
			rspn.put("error", e.getMessage()) ;
			Logger.getLogger("Member-error").error("oops, got an Exception:",e);
		}

		return rspn ;
	}
	

	/**
	 * 获取会员的消费记录明细
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * @param {memberId} 		会员的MEMBER_ID
	 * 
	 * HTTP Get Query Variables:
	 * @param pageNum=1 				第几页
	 * @param perPage=20 				每页多少行
	 * 
	 * @return
	 * {
	 * 	list:  [
	 * 			AMT:			<int>
	 * 			DATE:			<int:UNIX TIME STAMPE>
	 * 			POINT:			<int>
	 * 			SHOP_NAME:		<string>
	 * 			PRODUCT_NAME:	<string>
	 * 		},
	 * 		{
	 * 			AMT:			<int>
	 * 			DATE:			<int:UNIX TIME STAMPE>
	 * 			POINT:			<int>
	 * 			SHOP_NAME:		<string>
	 * 			PRODUCT_NAME:	<string>
	 * 		},
	 * 		...
	 * 	],
	 * 	total:		<int>
	 * 	pageNum:	<int>
	 * 	perPage:	<int>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/Spending/{memberId}")
	public @ResponseBody Map<String,Object> getSpending(@PathVariable String brand,@PathVariable String memberId,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();

		JdbcTemplate jdbcTpl = null ;
		
		try{
			
			Paginator.paginate(request,rspn) ;
			
			jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
			
		}catch(Throwable e){
			rspn.put("error",e.getMessage()) ;
			return rspn ;
		}

		MemberSpendingListModel lstModel = new MemberSpendingListModel(jdbcTpl,memberId) ;

		rspn.put("log", lstModel.queryPage((int)rspn.get("pageNum"),(int)rspn.get("perPage"))) ;
		rspn.put("total", lstModel.totalLength() ) ;

		return rspn ;
	}

	/**
	 * 获取会员卡的等级
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * @param {memberId}		会员 MEMBER_ID
	 * 
	 * @return
	 * {
	 * 	level:  <int>
	 * 	error:	<string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/Level/{memberId}")
    public @ResponseBody Map<String,Object> getPoint(@PathVariable String brand,@PathVariable int memberId) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{
			JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
			String level = new MemberCardModel(jdbcTpl,memberId).queryLevel() ;
			if(level.isEmpty()){
				rspn.put("error","会员卡号不存在") ;
			}
			else{
				rspn.put("level",level) ;
			}
		} catch(DaoBrandError e) {
			rspn.put("level",null) ;
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}
	

	/**
	 * 保存会员资料
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * @param {memberId}		会员 MEMBER_ID
	 * 
	 * HTTP Get Query Variables:
	 * @param email 			电子邮箱
	 * @param industry 			行业
	 * @param province 			省份
	 * @param city 				城市
	 * @param addr 				地址
	 * @param hoppy 			喜好款式
	 * @param color 			喜好颜色
	 * 
	 * 
	 * @return
	 * {
	 * 	success:  	<bool>
	 * 	error:		<string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/SaveInfo/{memberId}")
    public @ResponseBody Map<String,Object> saveInfo(@PathVariable String brand,@PathVariable int memberId,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();

		String email = request.getParameter("email") ;
		String industry = request.getParameter("industry") ;
		String province = request.getParameter("province") ;
		String city = request.getParameter("city") ;
		String addr = request.getParameter("addr") ;
		String hoppy = request.getParameter("hoppy") ;
		String color = request.getParameter("color") ;
		String sex = request.getParameter("sex") ;
		String birthday = request.getParameter("birthday") ;
		
		try{
			JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
			
			// 检查参数
			if( email==null || email.isEmpty() || !Pattern.compile(emailRegexp).matcher(email).matches() ) 
				throw new Error("email不是有效的电子邮箱地址") ;
			if( industry==null || industry.isEmpty() ) 
				throw new Error("缺少参数industry") ;
			if( province==null || province.isEmpty() ) 
				throw new Error("缺少参数province") ;
			if( city==null || city.isEmpty() ) 
				throw new Error("缺少参数city") ;
			if( addr==null || addr.isEmpty() ) 
				throw new Error("缺少参数addr") ;
			if( hoppy==null || hoppy.isEmpty() ) 
				throw new Error("缺少参数hoppy") ;
			if( color==null || color.isEmpty() ) 
				throw new Error("缺少参数color") ;

			int aff = new MemberModel(jdbcTpl,memberId).save(brand,email,industry,province,city,addr,hoppy,color,sex,birthday) ;
			System.out.println(aff);
			
			if(aff<1)
				throw new Error("保存会员资料失败，memberId对应的会员可能不存在") ;

			rspn.put("success",true) ;
			
		} catch(Throwable e) {
			rspn.put("success",false) ;
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}
	

	/**
	 * 查询会员资料
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * @param {memberId}		会员 MEMBER_ID
	 * 
	 * @return
	 * {
	 * 	success:  	<bool>
	 * 	error:		<string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/Info/{memberId}")
    public @ResponseBody Map<String,Object> getInfo(@PathVariable String brand,@PathVariable int memberId) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{
			JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
			
			rspn.put("info",new MemberModel(jdbcTpl,memberId).query()) ;
		} catch(DaoBrandError e) {
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}
		
	
	private static String  emailRegexp = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$" ;
}