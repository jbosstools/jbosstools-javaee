package org.jboss.tools.jsf.model.pv;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class JSFBeanSearcher {
	WebProjectNode beans;
	WebProjectNode conf;
	
	XModelObject bean;
	JSFProjectBean beanClass;
	String property;
	
	public JSFBeanSearcher(XModel model) {
		WebProjectNode root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return;
		beans = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.BEANS);
		conf = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
	}
	
	public void parse(String path) {
		String[] a = toArray(path);
		if(a.length == 0) return;
		bean = JSFPromptingProvider.findBean(conf, a[0]);
		beanClass = (JSFProjectBean)JSFPromptingProvider.findBeanClass(beans, bean);
		if(bean == null || a.length < 2) return;
		property = a[1];
		for (int i = 1; i < a.length - 1; i++) {
			if(!next()) return;
			property = a[i + 1];
		}
	}
	
	String[] toArray(String path) {
		ArrayList<String> l = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(path, ".");
		while(st.hasMoreElements()) l.add(st.nextToken());
		return l.toArray(new String[0]);
	}
	
	private boolean next() {
		if(bean == null || beanClass == null || property == null) return false;
		XModelObject p = beanClass.getChildByPath(property);
		if(p == null) return false;
		String cls = p.getAttributeValue("class name");
		XModelObject b = JSFPromptingProvider.findBeanClassByClassName(beans, cls);
		if(!(b instanceof JSFProjectBean)) {
			b = JSFPromptingProvider.buildBean(p.getModel(), cls);
			if(!(b instanceof JSFProjectBean)) return false;
		}
		beanClass = (JSFProjectBean)b;
		XModelObject[] beanList = beanClass.getBeanList();
		if(beanList.length > 0) {
			bean = beanList[0];
		}
		//bean = beanList.length == 0 ? null : beanList[0];
		return true;
	}
	
	public XModelObject getBean() {
		return bean;
	}
	
	public XModelObject getBeanClass() {
		return beanClass;
	}
	
	public String getProperty() {
		return property;
	}
	
}
