package org.jboss.servlet;

import javax.inject.Inject;

import org.jboss.solder.servlet.http.CookieParam;
import org.jboss.solder.servlet.http.DefaultValue;
import org.jboss.solder.servlet.http.HeaderParam;
import org.jboss.solder.servlet.http.RequestParam;

public class Validation {

	@Inject @RequestParam("id")	@MyQualifier String bookId; 
	@Inject @RequestParam @MyQualifier String id;
	@Inject @RequestParam @DefaultValue("25") String pageSize;
	@Inject @HeaderParam("User-Agent") @MyQualifier String userAgent;
	@Inject @HeaderParam @MyQualifier String userAgent2;
	@Inject @HeaderParam @DefaultValue("25") @MyQualifier String userAgent3;
	@Inject @CookieParam("User-Agent") String userAgent4;
	@Inject @CookieParam @MyQualifier String userAgent5;
	@Inject @CookieParam @DefaultValue("25") @MyQualifier String userAgent6;
	@Inject @RequestParam("id")	MyBean1 bookId1;
	@Inject @RequestParam MyBean1 id1;
	@Inject @RequestParam @DefaultValue("25") MyBean1 pageSize1;
	@Inject @HeaderParam("User-Agent") MyBean1 userAgent7;
	@Inject @HeaderParam MyBean1 userAgent8;
	@Inject @HeaderParam @DefaultValue("25") MyBean1 userAgent9;
	@Inject @CookieParam("User-Agent") MyBean1 userAgent10;
	@Inject @CookieParam MyBean1 userAgent11;
	@Inject @CookieParam @DefaultValue("25") MyBean1 userAgent12;
	@Inject @RequestParam public void setMyBeanOk(MyBean1 bean, @MyQualifier String bean2) {}
	@Inject @RequestParam("id")	public void setMyBeanOk1(MyBean1 bean, @MyQualifier String bean2) {}
	@Inject @RequestParam @DefaultValue("25") public void setMyBeanOk2(MyBean1 bean, @MyQualifier String bean2) {}
	@Inject @HeaderParam public void setMyBeanOk3(MyBean1 bean, @MyQualifier String bean2) {}
	@Inject @HeaderParam("id") public void setMyBeanOk4(MyBean1 bean, String bean2) {}
	@Inject @HeaderParam @DefaultValue("25") public void setMyBeanOk5(MyBean1 bean, @MyQualifier String bean2) {}
	@Inject @CookieParam public void setMyBeanOk6(MyBean1 bean, @MyQualifier String bean2) {}
	@Inject @CookieParam("id") public void setMyBeanOk7(MyBean1 bean, @MyQualifier String bean2) {}
	@Inject @CookieParam @DefaultValue("25") public void setMyBeanOk8(MyBean1 bean, @MyQualifier String bean2) {}

	@Inject MyBean1 beanBroken;
	@Inject @MyQualifier String bean2Broken;
	@Inject	public void setMyBeanBroken(MyBean1 bean) {}
	@Inject	public void setMyBeanBroken(@MyQualifier String bean) {}
}