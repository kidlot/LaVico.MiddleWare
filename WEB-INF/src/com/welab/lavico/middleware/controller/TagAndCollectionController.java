package com.welab.lavico.middleware.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.welab.lavico.middleware.service.TagAndCollectionService;

@Controller
public class TagAndCollectionController {


	/**
	 * 为会员添加标签
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param memberId		会员 MEMBER_ID
	 * @param tag			标签文本
	 * 
	 * @return
	 * {
	 * 	success:  	<bool>
	 * 	error:		<string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Tag/Add")
    public @ResponseBody Map<String,Object> addTag(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{
			String sMemberId = request.getParameter("memberId") ;
			if(sMemberId==null || sMemberId.isEmpty()){
				throw new Error("缺少参数 memberId") ;
			}
			int memberId = Integer.parseInt(sMemberId) ;
			
			String tagName = request.getParameter("tag") ;
			if(tagName==null || tagName.isEmpty()){
				throw new Error("缺少参数 tagName") ;
			}
			
			String user = request.getParameter("user") ;
			if(user==null || user.isEmpty()){
				user = brand + "999" ;
			}
			
			if( new TagAndCollectionService(brand).addTag(memberId,tagName,user) ){
				rspn.put("success",true) ;
			}
			else {
				rspn.put("success",false) ;
				rspn.put("error","添加标签操作失败，可能给定的 memberId 无效") ;
			}
		}catch(Throwable e){
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}
	

	/**
	 * 删除会员标签
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param memberId			会员 MEMBER_ID
	 * @param tag				标签文本
	 * 
	 * @return
	 * {
	 * 	success:  	<bool>
	 * 	error:		<string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Tag/Remove")
    public @ResponseBody Map<String,Object> removeTag(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{
			String sMemberId = request.getParameter("memberId") ;
			if(sMemberId==null || sMemberId.isEmpty()){
				throw new Error("缺少参数 memberId") ;
			}
			int memberId = Integer.parseInt(sMemberId) ;
			
			String tag = request.getParameter("tag") ;
			if(tag==null || tag.isEmpty()){
				throw new Error("缺少参数 tag") ;
			}
			
			if( new TagAndCollectionService(brand).removeTag(memberId,tag) ){
				rspn.put("success",true) ;
			}
			else {
				rspn.put("success",false) ;
				rspn.put("error","删除用户标签操作失败，可能给定的 memberId 和 tag 无效") ;
			}
		}catch(Throwable e){
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}
	

	/**
	 * 会员收藏商品
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param memberId			会员 MEMBER_ID
	 * @param goodsCode			品号代码
	 * 
	 * @return
	 * {
	 * 	success:  	<bool>
	 * 	error:		<string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Collection/Add")
    public @ResponseBody Map<String,Object> addCollection(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{
			String sMemberId = request.getParameter("memberId") ;
			if(sMemberId==null || sMemberId.isEmpty()){
				throw new Error("缺少参数 memberId") ;
			}
			int memberId = Integer.parseInt(sMemberId) ;
			
			String goodsCode = request.getParameter("goodsCode") ;
			if(goodsCode==null || goodsCode.isEmpty()){
				throw new Error("缺少参数 goodsCode") ;
			}
			
			String user = request.getParameter("user") ;
			if(user==null || user.isEmpty()){
				user = brand + "999" ;
			}
			
			if( new TagAndCollectionService(brand).addCollection(memberId,goodsCode,user) ){
				rspn.put("success",true) ;
			}
			else {
				rspn.put("success",false) ;
				rspn.put("error","添加收藏操作失败，可能给定的 memberId 无效") ;
			}
		}catch(Throwable e){
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}

	/**
	 * 删除会员收藏商品
	 * 
	 * Path Variables:
	 * @param {brand} 			品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param memberId			会员 MEMBER_ID
	 * @param goodsCode			品号代码
	 * 
	 * @return
	 * {
	 * 	success:  	<bool>
	 * 	error:		<string>
	 * }
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Collection/Remove")
    public @ResponseBody Map<String,Object> removeCollection(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{
			String sMemberId = request.getParameter("memberId") ;
			if(sMemberId==null || sMemberId.isEmpty()){
				throw new Error("缺少参数 memberId") ;
			}
			int memberId = Integer.parseInt(sMemberId) ;
			
			String goodsCode = request.getParameter("goodsCode") ;
			if(goodsCode==null || goodsCode.isEmpty()){
				throw new Error("缺少参数 goodsCode") ;
			}
			
			if( new TagAndCollectionService(brand).removeCollection(memberId,goodsCode) ){
				rspn.put("success",true) ;
			}
			else {
				rspn.put("success",false) ;
				rspn.put("error","删除收藏操作失败，可能给定的 memberId 和 goodsCode 无效") ;
			}
		}catch(Throwable e){
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}

}
