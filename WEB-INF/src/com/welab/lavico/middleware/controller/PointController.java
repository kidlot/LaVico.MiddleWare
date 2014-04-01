package com.welab.lavico.middleware.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.welab.lavico.middleware.model.PointLogModel;
import com.welab.lavico.middleware.service.DaoBrandError;
import com.welab.lavico.middleware.service.MemberCardInfoService;
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
	 * 获取会员的积分
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
	 *  	}
	 *  	...
	 *  ]
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Point/Log/{memberId}")
    public @ResponseBody Map<String,Object> getPointLog(@PathVariable String brand,@PathVariable int memberId,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		rspn.put("total",0) ;
		
		// 处理Get参数 pageNum
		String sPage = request.getParameter("pageNum") ;
		if(sPage==null){
			sPage = "1" ;		// 默认值
		}
		int iPage = 1 ;
		try{
			iPage = Integer.parseInt(sPage) ;
		} catch (NumberFormatException e) {
			rspn.put("error","parameter pageNum is not valid format.") ;
			return rspn ;
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
			rspn.put("error","parameter perPage is not valid format.") ;
			return rspn ;
		}

		rspn.put("pageNum", iPage) ;
		rspn.put("perPage", iPerPage) ;

		JdbcTemplate jdbcTpl = null ;
		try{
			jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
		}catch(DaoBrandError e){
			rspn.put("error",e.getMessage()) ;
			return rspn ;
		}

		PointLogModel logModel = new PointLogModel(jdbcTpl,memberId) ;

		List<Map<String,Object>> logList = logModel.queryPage(iPage,iPerPage) ;
		rspn.put("log", logList) ;
		
		// 总长度
		rspn.put("total", logModel.totalLength() ) ;
		
		return rspn ;
	}
}
