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
import com.welab.lavico.middleware.model.ShopListModel;
import com.welab.lavico.middleware.service.SpringJdbcDaoSupport;


@Controller
public class ShopController {

	/**
	 * 获取所有的门店
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * 
	 * @return
	 * {
	 * 	list:  [
	 * 			CODE:		<string>
	 * 			NAME:		<string>
	 * 			ADDR:		<string>
	 * 			TEL:		<string>
	 * 			CITY:		<string>
	 * 			PROVINCE:	<string>
	 * 			LOG:		<float>		经度
	 * 			LAT:		<float>		纬度
	 * 			PICURL:		<string>	门店图片地址
	 * 			ACT:		<string>	活动介绍
	 * 		},
	 * 		{
	 * 			CODE:		<string>
	 * 			NAME:		<string>
	 * 			ADDR:		<string>
	 * 			TEL:		<string>
	 * 			CITY:		<string>
	 * 			PROVINCE:	<string>
	 * 			LOG:		<float>		经度
	 * 			LAT:		<float>		纬度
	 * 			PICURL:		<string>	门店图片地址
	 * 			ACT:		<string>	活动介绍
	 * 		},
	 * 		...
	 * 	],
	 * 	total:		<int>
	 * 	pageNum:	<int>
	 * 	perPage:	<int>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Shops")
    public @ResponseBody Map<String,Object> getShops(@PathVariable String brand,HttpServletRequest request) {

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

		ShopListModel lstModel = new ShopListModel(jdbcTpl) ;

		rspn.put("list", lstModel.queryPage((int)rspn.get("pageNum"),(int)rspn.get("perPage"),request.getParameter("city"))) ;
		rspn.put("total", lstModel.totalLength(request.getParameter("city")) ) ;

		return rspn ;
	}
}
