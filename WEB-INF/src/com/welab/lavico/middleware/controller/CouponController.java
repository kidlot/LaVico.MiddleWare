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

import com.welab.lavico.middleware.controller.util.Paginator;
import com.welab.lavico.middleware.model.CouponListModel;
import com.welab.lavico.middleware.model.PromotionListModel;
import com.welab.lavico.middleware.service.CouponService;
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
		rspn.put("total",0) ;


		JdbcTemplate jdbcTpl = null ;
		try{
			Paginator.paginate(request,rspn) ;
			jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
		}catch(Throwable e){
			rspn.put("error",e.getMessage()) ;
			return rspn ;
		}
		
		PromotionListModel promotionModel = new PromotionListModel(jdbcTpl) ;

		rspn.put("list",promotionModel.queryPage((int)rspn.get("pageNum"),(int)rspn.get("perPage"),request.getParameter("code"))) ;
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
	 * @param openid 					微信id
	 * @param otherPromId 				第三方系统活动ID
	 * @param PROMOTION_CODE 			CRM活动代码
	 * @param qty 						优惠券金额
	 * @param point 					积分增减：>0 增加积分; <0 扣减积分; =0 无积分变化
	 * @param memo 						备注
	 * 
	 * 
	 * @return {success:true/false,error:"error message",coupon_id:"xxxxx"}
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Coupon/FetchCoupon")
    public @ResponseBody Map<String,Object> fetchCoupon(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		rspn.put("success",false) ;

		String openid = request.getParameter("openid") ;
		String otherPromId = request.getParameter("otherPromId") ;
		String promCode = request.getParameter("PROMOTION_CODE") ;
		String sQty = request.getParameter("qty") ;
		String sPoint = request.getParameter("point") ;
		float qty ;
		int point ;

		if(sQty==null||sQty.isEmpty()||sQty.equals("0")){
			sQty = "-1" ;
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

			String couponNo = new CouponService().GetCoupon(brand, openid, promCode, otherPromId, qty, point, request.getParameter("memo")) ;
			rspn.put("success",true) ;
			rspn.put("coupon_no",couponNo) ;
		} catch(Error e) {
			rspn.put("error",e.getMessage()) ;
			Logger.getLogger("Promotion-error").error("oops, got an Exception:",e);
			e.printStackTrace();
		}
		
		return rspn ;
	}
	

	/**
	 * 获取优惠券
	 * 
	 * Path Variables:
	 * @param {brand} 					品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param memberId 					会员MEMBER_ID
	 * @param coupon_no 				coupon_no
	 * 
	 * @return {success:true/false,error:"error message",coupon_id:"xxxxx"}
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Coupon/GetCoupons")
    public @ResponseBody Map<String,Object> getCoupons(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		

		String status = request.getParameter("status") ;
		if(status==null||status.isEmpty()){
			status = "02" ;
		}

		String coupon_no = request.getParameter("coupon_no") ;
		String sMemberId = request.getParameter("memberId") ;
		String promotionCode = request.getParameter("promotionCode") ;
		

		if( (coupon_no==null||coupon_no.isEmpty()) && (promotionCode==null||promotionCode.isEmpty()) && (sMemberId==null||sMemberId.isEmpty()) ){
			rspn.put("error","缺少参数 memberId 或 promotionCode 或 coupon_no") ;
			return rspn ;
		}
		
		if(promotionCode==null||promotionCode.isEmpty())
			promotionCode = "" ;

		if(sMemberId==null||sMemberId.isEmpty()){
			sMemberId = "0" ;
		}
		int iMemberId = 0 ;
		try{
			iMemberId = Integer.parseInt(sMemberId) ;
		} catch (NumberFormatException e) {
			rspn.put("error","parameter memberId is not valid format.") ;
			return rspn ;
		}
		

		
		JdbcTemplate jdbcTpl = null ;
		try{
			Paginator.paginate(request,rspn) ;
			jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
		}catch(Throwable e){
			rspn.put("error",e.getMessage()) ;
			return rspn ;
		}
		
		CouponListModel listModel = new CouponListModel(jdbcTpl) ;
		
		List<Map<String,Object>> list = null;
		list = listModel.queryCouponList(coupon_no,iMemberId,promotionCode,status,(int)rspn.get("pageNum"),(int)rspn.get("perPage")) ;
		

		rspn.put("list",list) ;
		rspn.put(
				"total"
				, listModel.totalLength(coupon_no,iMemberId,promotionCode,status)
		) ;
		
		return rspn ;
	}
}


