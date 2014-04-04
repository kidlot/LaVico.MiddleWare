package com.welab.lavico.middleware.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.welab.lavico.middleware.model.PromotionListModel;
import com.welab.lavico.middleware.service.CouponService;
import com.welab.lavico.middleware.service.DaoBrandError;
import com.welab.lavico.middleware.service.SpringJdbcDaoSupport;

@Controller
public class CouponController {

	/**
	 * 返回活动-优惠券列表
	 * 
	 * Path Variables:
	 * @param {brand} 					品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param pageNum=1 				第几页
	 * @param perPage=20 				每页多少行
	 * 
	 * @return
	 * {
	 * 	total:   <int>					所有有效活动总数
	 *  pageNum: <int>					第几页
	 *  perPage: <int>					每页多少行
	 *  list: [
	 *  	{
	 *  		QTY:	<signed float>	优惠券金额
	 *  		COUNT:	<string/time>	该金额优惠券总数
	 *  		USED:	<string>		该金额优惠券已发放数量
	 *  	}
	 *  	...
	 *  ]
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Coupon/Promotions")
    public @ResponseBody Map<String,Object> getPromotions(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();

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
		
		PromotionListModel promotionModel = new PromotionListModel(jdbcTpl) ;
		List<Map<String,Object>> list = promotionModel.queryPage(iPage,iPerPage) ;

		rspn.put("list",list) ;
		rspn.put("total",promotionModel.totalLength()) ;

    	return rspn ;
    }
	
	/**
	 * 获取优惠券
	 * 
	 * Path Variables:
	 * @param {brand} 					品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param pageNum=1 				第几页
	 * @param perPage=20 				每页多少行
	 * @param request
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Coupon/GetCoupon")
    public @ResponseBody Map<String,Object> getCoupon(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		rspn.put("issuccessed",false) ;

		String openid = request.getParameter("openid") ;
		String otherPromId = request.getParameter("otherPromId") ;
		String promCode = request.getParameter("PROMOTION_CODE") ;
		String sQty = request.getParameter("qty") ;
		String sPoint = request.getParameter("point") ;
		float qty ;
		int point ;
		
		if(sQty==null||sQty.isEmpty()){
			sQty = "0" ;
		}
		if(sPoint==null||sPoint.isEmpty()){
			sPoint = "0" ;
		}
		
		try{
			qty = Float.parseFloat(sQty) ;
		} catch(NumberFormatException e) {
			rspn.put("error","qty is not valid format") ;
			Logger.getLogger("Promotion-error").error("oops, got an Exception:",e);
			return rspn ;
		}

		try{
			point = Integer.parseInt(sPoint) ;
		} catch(NumberFormatException e) {
			rspn.put("error","point is not valid format") ;
			Logger.getLogger("Promotion-error").error("oops, got an Exception:",e);
			return rspn ;
		}
		
		try{
			int couponNo = new CouponService().GetCoupon(brand, openid, promCode, otherPromId, qty, point) ;
			rspn.put("issuccessed",true) ;
			rspn.put("coupon_no",couponNo) ;
		} catch(Error e) {
			rspn.put("error",e.getMessage()) ;
			Logger.getLogger("Promotion-error").error("oops, got an Exception:",e);
			e.printStackTrace();
		}
		
		return rspn ;
	}
}


