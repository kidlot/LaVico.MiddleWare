package com.welab.lavico.middleware.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.welab.lavico.middleware.controller.util.Paginator;
import com.welab.lavico.middleware.model.PointLogModel;
import com.welab.lavico.middleware.service.DaoBrandError;
import com.welab.lavico.middleware.service.MemberCardInfoService;
import com.welab.lavico.middleware.service.PointService;
import com.welab.lavico.middleware.service.SpringJdbcDaoSupport;

@Controller
public class PointController {


	/**
	 * 获取会员的积分
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * @param {memberId}		会员 MEMBER_ID
	 * 
	 * @return
	 * {
	 * 	point:  <int>
	 * 	error:	<string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Point/{memberId}")
    public @ResponseBody Map<String,Object> getPoint(@PathVariable String brand,@PathVariable int memberId) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{
			int point = new MemberCardInfoService().getCurrentPoint(brand,memberId) ;
			rspn.put("point",point) ;
		} catch(DaoBrandError e) {
			rspn.put("point",-1) ;
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}


	/**
	 * 获取会员的积分明细
	 * 
	 * Path Variables:
	 * @param {brand} 					品牌名称
	 * @param {memberId}				会员 MEMBER_ID
	 * 
	 * HTTP Get Query Variables:
	 * @param pageNum=1 				第几页
	 * @param perPage=20 				每页多少行
	 * 
	 * @return
	 * {
	 * 	total:   <int>					该会员所有积分明细的行数
	 *  pageNum: <int>					第几页
	 *  perPage: <int>					每页多少行
	 *  log: [
	 *  	{
	 *  		value:	<signed int>	单笔记录积分值
	 *  		time:	<string/time>	记录时间
	 *  		memo:	<string>		备注
	 *  		source: <string>		来源：01.销售产生积分 02.微信修改积分 03.人工手动调整积分 
	 *  	}
	 *  	...
	 *  ]
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Point/Log/{memberId}")
    public @ResponseBody Map<String,Object> getPointLog(@PathVariable String brand,@PathVariable int memberId,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		rspn.put("total",0) ;
		
		JdbcTemplate jdbcTpl = null ;
		
		try{
			
			Paginator.paginate(request,rspn) ;
			
			jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
			
		}catch(Throwable e){
			rspn.put("error",e.getMessage()) ;
			return rspn ;
		}

		PointLogModel logModel = new PointLogModel(jdbcTpl,memberId) ;

		rspn.put("log", logModel.queryPage((int)rspn.get("pageNum"),(int)rspn.get("perPage"))) ;
		rspn.put("total", logModel.totalLength() ) ;
		
		return rspn ;
	}
	

	/**
	 * 增减会员的积分
	 * 
	 * Path Variables:
	 * @param {brand} 					品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param memberId					会员 MEMBER_ID
	 * @param qty		 				积分值，整数表示增加，负数表示减少
	 * @param memo						备注
	 * 
	 * @return
	 * {
	 * 	success:  	<bool>
	 * 	error:		<string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Point/Change")
    public @ResponseBody Map<String,Object> changePoint(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{

			String sMemberId = request.getParameter("memberId") ;
			if(sMemberId==null || sMemberId.isEmpty()){
				throw new Error("缺少参数 memberId") ;
			}
			int memberId = Integer.parseInt(sMemberId) ;

			String sQty= request.getParameter("qty") ;
			if(sQty==null || sQty.isEmpty()){
				throw new Error("缺少参数 qty") ;
			}
			int qty = Integer.parseInt(sQty) ;
			
			new PointService(brand,memberId).change(qty,request.getParameter("memo")) ;

			rspn.put("success",true) ;
			
		}catch(Throwable e){
			rspn.put("success",false) ;
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}
}
