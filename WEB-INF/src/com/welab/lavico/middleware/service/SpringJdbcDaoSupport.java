package com.welab.lavico.middleware.service;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SpringJdbcDaoSupport extends JdbcDaoSupport{
	
	public static JdbcTemplate getJdbcTemplate(String brand)
		throws DaoBrandError
	{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		JdbcDaoSupport dao = null ;
		try{
			dao = (JdbcDaoSupport)ctx.getBean("jdbcDao_"+brand);
		} catch(NoSuchBeanDefinitionException e) {
			throw new DaoBrandError() ;
		}
		return dao.getJdbcTemplate() ;
	}
}
