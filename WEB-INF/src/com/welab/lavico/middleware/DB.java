package com.welab.lavico.middleware;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DB {
	public static JdbcTemplate getJdbcTemplate(String brand){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		JdbcDaoSupport dao = (JdbcDaoSupport)ctx.getBean("jdbcDao_"+brand);
		return dao.getJdbcTemplate() ;
	}
}