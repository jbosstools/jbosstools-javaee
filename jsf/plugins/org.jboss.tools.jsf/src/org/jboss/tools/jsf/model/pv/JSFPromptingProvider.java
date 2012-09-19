/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.model.pv;

import java.util.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.common.model.java.handlers.OpenJavaSourceHandler;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.model.JSFELCompletionEngine;
import org.jboss.tools.jsf.model.helpers.converter.*;
import org.jboss.tools.jsf.model.helpers.pages.OpenCaseHelper;
import org.jboss.tools.jsf.model.helpers.pages.ResourceBundleHelper;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.JSFWebProject;
import org.jboss.tools.jsf.web.pattern.JSFUrlPattern;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;

public class JSFPromptingProvider implements IWebPromptingProvider {
	static Set<String> SUPPORTED_IDS = new HashSet<String>();
	static {
		SUPPORTED_IDS.add(JSF_BUNDLES);
		SUPPORTED_IDS.add(JSF_REGISTERED_BUNDLES);
		SUPPORTED_IDS.add(JSF_BUNDLE_PROPERTIES);
		SUPPORTED_IDS.add(JSF_MANAGED_BEANS);
		SUPPORTED_IDS.add(JSF_BEAN_PROPERTIES);
		SUPPORTED_IDS.add(JSF_BEAN_METHODS);
		SUPPORTED_IDS.add(JSF_BEAN_ADD_PROPERTY);
		SUPPORTED_IDS.add(JSF_VIEW_ACTIONS);
		SUPPORTED_IDS.add(JSF_BEAN_OPEN);
		SUPPORTED_IDS.add(JSF_GET_PATH);
		SUPPORTED_IDS.add(JSF_OPEN_ACTION);
		SUPPORTED_IDS.add(JSF_OPEN_CONVERTOR);
		SUPPORTED_IDS.add(JSF_OPEN_VALIDATOR);
		SUPPORTED_IDS.add(JSF_OPEN_RENDER_KIT);
		SUPPORTED_IDS.add(JSF_OPEN_CLASS_PROPERTY);
		SUPPORTED_IDS.add(JSF_OPEN_TAG_LIBRARY);
		SUPPORTED_IDS.add(JSF_OPEN_BUNDLE);
		SUPPORTED_IDS.add(JSF_OPEN_KEY);
		SUPPORTED_IDS.add(JSF_GET_URL);
		SUPPORTED_IDS.add(JSF_CONVERT_URL_TO_PATH);
		SUPPORTED_IDS.add(JSF_GET_TAGLIBS);
		SUPPORTED_IDS.add(JSF_CONVERTER_IDS);
		SUPPORTED_IDS.add(JSF_VALIDATOR_IDS);
		SUPPORTED_IDS.add(JSF_FACES_CONFIG);
	}
	public final static String PROVIDER_ID = "jsf"; //$NON-NLS-1$

	public boolean isSupporting(String id) {
		return id != null && SUPPORTED_IDS.contains(id);
	}

	public List<Object> getList(XModel model, String id, String prefix, Properties properties) {
		try {
			return getListInternal(model, id, prefix, properties);
		} catch (CoreException e) {
			if(properties != null) {
				String message = e.getMessage();
				if(message==null) {
					message = e.getClass().getName();
				}
				properties.setProperty(ERROR, message);
			}
			return EMPTY_LIST;
		}
	}
	
	private List<Object> getListInternal(XModel model, String id, String prefix, Properties properties) throws CoreException {
		String error = null;
		if(JSF_BUNDLES.equals(id)) return getBundles(model);
		if(JSF_REGISTERED_BUNDLES.equals(id)) {
			return ResourceBundleHelper.getRegisteredResourceBundles(model);
		}
		if(JSF_BUNDLE_PROPERTIES.equals(id)) return getBundleProperties(model, prefix);
		if(JSF_MANAGED_BEANS.equals(id)) return getBeans(model);
		if(JSF_BEAN_PROPERTIES.equals(id)) {
			String type = (properties == null) ? null : properties.getProperty(PROPERTY_TYPE);
			String sBeanOnly = (properties == null) ? null : properties.getProperty(PROPERTY_BEAN_ONLY);
			boolean beanOnly = "true".equals(sBeanOnly); //$NON-NLS-1$
			return getBeanProperties(model, prefix, type, beanOnly);
		} else if(JSF_BEAN_METHODS.equals(id)) {
			String[] parameterTypes = (String[])properties.get(PARAMETER_TYPES);
			String returnType = properties.getProperty(RETURN_TYPE);
			return getBeanMethods(model, prefix, parameterTypes, returnType);
		} else if(JSF_BEAN_ADD_PROPERTY.equals(id)) {
			XModelObject property = addPropertyToBean(model, prefix);
			if(property == null) return EMPTY_LIST;
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(property);
			return list;
		} else if(JSF_VIEW_ACTIONS.equals(id)) {
			String viewPath = properties.getProperty(VIEW_PATH);
			return getViewActions(model, viewPath);
		} else if(JSF_BEAN_OPEN.equals(id)) {
			openBean(model, prefix);
		} else if(JSF_GET_PATH.equals(id)) {
			return getPathAsList(model, prefix); 
		} else if(JSF_OPEN_ACTION.equals(id)) {
			IFile file = (IFile)properties.get(FILE);
			String action = prefix;
			OpenCaseHelper h = new OpenCaseHelper();
			error = h.run(model, file, action);
		} else if(JSF_OPEN_CONVERTOR.equals(id)) {
			String converterId = prefix;
			OpenConverterHelper h = new OpenConverterHelper();
			error = h.run(model, converterId);
		} else if(JSF_OPEN_VALIDATOR.equals(id)) {
			String validatorId = prefix;
			OpenValidatorHelper h = new OpenValidatorHelper();
			error = h.run(model, validatorId);
		} else if(JSF_OPEN_RENDER_KIT.equals(id)) {
			String renderkitId = prefix;
			OpenRenderKitHelper h = new OpenRenderKitHelper();
			error = h.run(model, renderkitId);
		} else if(JSF_OPEN_CLASS_PROPERTY.equals(id)) {
			OpenJavaSourceHandler.open(model, prefix, properties);
		} else if(JSF_OPEN_TAG_LIBRARY.equals(id)) {
			String uri = prefix;
			String tagName = properties.getProperty(NAME);
			String attributeName = properties.getProperty(ATTRIBUTE);
			OpenTagLib h = new OpenTagLib();
			error = h.run(model, uri, tagName, attributeName);
		} else if(JSF_OPEN_BUNDLE.equals(id)) {
			String bundle = properties.getProperty(BUNDLE);
			String locale = properties.getProperty(LOCALE);
			OpenKeyHelper h = new OpenKeyHelper();
			error = h.run(model, bundle, locale);
		} else if(JSF_OPEN_KEY.equals(id)) {
			String bundle = properties.getProperty(BUNDLE);
			String key = properties.getProperty(KEY);
			String locale = properties.getProperty(LOCALE);
			OpenKeyHelper h = new OpenKeyHelper();
			error = h.run(model, bundle, key, locale);
		} else if(JSF_GET_URL.equals(id)) {
			if(!EclipseResourceUtil.hasNature(model, JSFNature.NATURE_ID)) return EMPTY_LIST;
			ArrayList<Object> list = new ArrayList<Object>();
			String url = JSFWebProject.getInstance(model).getUrlPattern().getJSFUrl(prefix);
			if(url != null && url.length() > 0) list.add(url);
			return list;
		} else if(JSF_CONVERT_URL_TO_PATH.equals(id)) {
			if(!EclipseResourceUtil.hasNature(model, JSFNature.NATURE_ID)) {
				XModelObject webxml = WebAppHelper.getWebApp(model);
				if(webxml != null) {
					JSFWebProject.getInstance(model).getPatternLoader().revalidate(webxml);
				} else {
					return EMPTY_LIST;
				}
			}
			ArrayList<Object> list = new ArrayList<Object>();
			List<String> paths = JSFWebProject.getInstance(model).getUrlPattern().getJSFPaths(prefix);
			if(!paths.isEmpty()) {
				list.addAll(paths);
			} else {
				String path = JSFWebProject.getInstance(model).getUrlPattern().getJSFPath(prefix);
				if(path != null && path.length() > 0) list.add(path);
			}
			return list;
		} else if(JSF_GET_TAGLIBS.equals(id)) {
			ArrayList<Object> list = new ArrayList<Object>();
			WebProject p = WebProject.getInstance(model);
			Map<String,XModelObject> map = p.getTaglibMapping().getTaglibObjects();
			list.addAll(map.keySet());
			return list;
		} else if(JSF_CONVERTER_IDS.equals(id)) {
			return new OpenConverterHelper().getConverterIDs(model);
		} else if(JSF_VALIDATOR_IDS.equals(id)) {
			return new OpenValidatorHelper().getValidatorIDs(model);
		} else if(JSF_FACES_CONFIG.equals(id)) {
			XModelObject fc = findFacesConfig(model);
			if(fc == null) return EMPTY_LIST;
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(fc);
			return list;
		}
		if(error != null) throw new XModelException(error);
		return EMPTY_LIST;
	}
	
	public List<Object> getBundles(XModel model) {
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return EMPTY_LIST;
		WebProjectNode n = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.RESOURCE_BUNDLES);
		if(n == null) return EMPTY_LIST;
		XModelObject[] os = n.getTreeChildren();
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < os.length; i++) {
			String p = XModelObjectLoaderUtil.getResourcePath(os[i]);
			if(p == null || !p.endsWith(".properties")) continue; //$NON-NLS-1$
			p = p.substring(1, p.length() - 11).replace('/', '.');
			list.add(p);			
		}
		return list;
	}
	
	public List<Object> getBundleProperties(XModel model, String bundle) {
		if(bundle == null || bundle.length() == 0) return EMPTY_LIST;
		
		OpenKeyHelper helper = new OpenKeyHelper();
		XModelObject[] bundleObjects = helper.findBundles(model, bundle, null);
		if (bundleObjects == null) 
			return EMPTY_LIST;
//			XModelObject b = model.getByPath("/" + bundle.replace('.', '/') + ".properties");
		Set properties = new TreeSet(String.CASE_INSENSITIVE_ORDER);
		for (XModelObject b : bundleObjects) {
			if(b == null) continue;
			XModelObject[] os = b.getChildren();
			for (int i = 0; i < os.length; i++) {
//				list.add(os[i].getAttributeValue("name"));
				properties.add(os[i].getAttributeValue("name"));
			}
		}
		List<Object> list = new ArrayList<Object>();
		list.addAll(properties);
		
		return list;
	}
	
	public List<Object> getBeans(XModel model) {
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return EMPTY_LIST;
		WebProjectNode n = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		if(n == null) return EMPTY_LIST;
		XModelObject[] os = n.getTreeChildren();
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < os.length; i++) {
			if(!os[i].getModelEntity().getName().startsWith(JSFConstants.ENT_FACESCONFIG)) continue;
			getBeans(os[i], BeanConstants.MANAGED_BEAN_CONSTANTS, list);
			getBeans(os[i], BeanConstants.REFERENCED_BEAN_CONSTANTS, list);
		}
		return list;
	}
	
	private void getBeans(XModelObject o, BeanConstants constants, List<Object> list) {
		XModelObject mb = o.getChildByPath(constants.folder);
		if(mb == null) return;
		XModelObject[] bs = mb.getChildren();
		for (int j = 0; j < bs.length; j++) {
			list.add(bs[j].getAttributeValue(constants.nameAttribute));
		}						
	}

	public List<JSFELCompletionEngine.IJSFVariable> getVariables(XModel model) {
		List<JSFELCompletionEngine.IJSFVariable> result = new ArrayList<JSFELCompletionEngine.IJSFVariable>();
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return result;
		WebProjectNode n = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		WebProjectNode beans = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.BEANS);
		XModelObject[] os = n.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			if(!os[i].getModelEntity().getName().startsWith(JSFConstants.ENT_FACESCONFIG)) continue;
			getVariables(os[i], BeanConstants.MANAGED_BEAN_CONSTANTS, beans, result);
			getVariables(os[i], BeanConstants.REFERENCED_BEAN_CONSTANTS, beans, result);
		}
		
		return result;
	}

	private void getVariables(XModelObject o, BeanConstants constants, WebProjectNode beans, List<JSFELCompletionEngine.IJSFVariable> list) {
		XModelObject mb = o.getChildByPath(constants.folder);
		if(mb == null) return;
		XModelObject[] bs = mb.getChildren();
		for (XModelObject q : bs) {
		final String name = q.getAttributeValue(constants.nameAttribute);
			String className = q.getAttributeValue(constants.classAttribute);
			XModelObject c = findBeanClassByClassName(beans, className);
			if (c instanceof JSFProjectBean) {
				JSFProjectBean b = (JSFProjectBean) c;
				final IType type = b.getType();
				JSFELCompletionEngine.IJSFVariable var = new JSFELCompletionEngine.IJSFVariable() {
					public IMember getSourceMember() {
						return type;
					}

					public String getName() {
						return name;
					}
				};
				list.add(var);
			}
		}
	}

	public List<Object> getBeanProperties(XModel model, String prefix, String type, boolean beanOnly) {
		if(prefix.length() == 0) return EMPTY_LIST;
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return EMPTY_LIST;
		IProject project = EclipseResourceUtil.getProject(root);
		int d = prefix.indexOf('.');
		String beanName = (d < 0) ? prefix : prefix.substring(0, d);
		String property = (d < 0) ? null : prefix.substring(d + 1);
		WebProjectNode n = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.BEANS);
		WebProjectNode conf = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		if(n == null || conf == null) return EMPTY_LIST;
		XModelObject bean = findBean(conf, beanName);
		if(bean == null) return EMPTY_LIST;
		List<Object> list = new ArrayList<Object>();
		XModelObject beanClass = findBeanClass(n, bean);
		if(beanClass == null) {
			if(property.length() > 0) return EMPTY_LIST;
			XModelObject[] cs = bean.getChildren("JSFManagedProperty");
			for (int i = 0; i < cs.length; i++) {
				String cn = cs[i].getAttributeValue("property-class");
				if(type != null && !EclipseJavaUtil.isDerivedClass(cn, type, project)) continue;
				if(beanOnly && isNotBean(cn)) continue;
				list.add(cs[i].getAttributeValue("property-name"));
			}
		} else {
			if(property != null && property.length() > 0) beanClass = beanClass.getChildByPath(property.replace('.', '/'));
			if(beanClass == null) return EMPTY_LIST;
			XModelObject[] os = beanClass.getChildren("JSFProjectBeanProperty");
			for (int i = 0; i < os.length; i++) {
				String cn = os[i].getAttributeValue("class name");
				if(type != null && !EclipseJavaUtil.isDerivedClass(cn, type, project)) continue;
				if(beanOnly && isNotBean(cn)) continue;
				list.add(os[i].getAttributeValue("name"));
			}
		}
		return list;
	}
	private boolean isNotBean(String cn) {
		if(cn == null || cn.length() == 0) return true;
		if(JSFProjectBeans.primitive.indexOf("!" + cn + "!") >= 0) return true;
		if(cn.startsWith("java.lang.")) return true;
		if(cn.startsWith("java.util.")) return true;
		return false;		
	}
	
	/**
	 * Called by ui that requested list of beans. 
	 * @param model
	 * @param bean
	 * @return
	 */	
	private XModelObject addPropertyToBean(XModel model, String beanName) {
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		WebProjectNode conf = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		XModelObject bean = findBean(conf, beanName);
		if(bean == null) return null;
		Properties p = new Properties();
		XActionInvoker.invoke("CreateActions.CreateProperty", bean, p);
		return (XModelObject)p.get("created");
	}
	
	/**
	 * Creates bean for any class.
	 * Returns list of property names
	 * @param model
	 * @param className
	 * @param type
	 * @return
	 */	
	public List buildBeanProperties(XModel model, String className, String type) {
		JSFProjectBean beanClass = buildBean(model, className);
		if(beanClass == null) return EMPTY_LIST;
		IProject project = EclipseResourceUtil.getProject(beanClass);
		ArrayList<Object> list = new ArrayList<Object>();
		XModelObject[] os = beanClass.getChildren("JSFProjectBeanProperty");
		for (int i = 0; i < os.length; i++) {
			if(type == null || 
				EclipseJavaUtil.isDerivedClass(os[i].getAttributeValue("class name"), type, project)
			) {
				list.add(os[i].getAttributeValue("name"));
			}
		}
		return list;		
	}
	
	public static JSFProjectBean buildBean(XModel model, String className) {
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return null;
		JSFProjectBeans n = (JSFProjectBeans)root.getChildByPath(JSFProjectTreeConstants.BEANS);
		IType cls = n.getType(className);
		if(cls == null) return null;
		JSFProjectBean beanClass = (JSFProjectBean)model.createModelObject("JSFProjectBean", null);
		beanClass.setBeans(n);
		beanClass.setType(cls);
		beanClass.setAttributeValue("class name", className);
		return beanClass;		
	}
	
	private boolean openBean(XModel model, String prefix) {
		if(prefix.length() == 0) return false;
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return false;
		
		JSFBeanSearcher s = new JSFBeanSearcher(model);
		s.parse(prefix);
		XModelObject bean = s.getBeanClass();
		String property = s.getProperty();
		if(bean == null) return false;
		
		Properties p = new Properties();
		if(property != null) {
			p.setProperty("property", property);
		}
		if(bean != null) XActionInvoker.invoke("Open", bean, p);
		return true;
	}
	
	static XModelObject findBean(WebProjectNode conf, String beanName) {
		XModelObject[] os = conf.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			if(!os[i].getModelEntity().getName().startsWith(JSFConstants.ENT_FACESCONFIG)) continue;
			XModelObject bean = os[i].getChildByPath("Managed Beans/" + beanName);
			if(bean != null) return bean;
			bean = os[i].getChildByPath("Referenced Beans/" + beanName);
			if(bean != null) return bean;
		}
		return null;
	}
	
	static XModelObject findBeanClass(WebProjectNode beans, XModelObject bean) {
		BeanConstants constants = BeanConstants.getConstants(bean);
		if(constants == null) return null;
		String className = bean.getAttributeValue(constants.classAttribute);
		return findBeanClassByClassName(beans, className);
	}
	
	static XModelObject findBeanClassByClassName(WebProjectNode beans, String className) {
		if(beans == null) return null;
		XModelObject[] os = beans.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			 if(className.equals(os[i].getAttributeValue("class name"))) return os[i];
		}
		return null;
	}
	
	public List<Object> getBeanMethods(XModel model, String prefix, String[] parameterTypes, String returnType) {
		if(prefix.length() == 0) return EMPTY_LIST;
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return EMPTY_LIST;
		int d = prefix.indexOf('.');
		String beanName = (d < 0) ? prefix : prefix.substring(0, d);
		String property = (d < 0) ? null : prefix.substring(d + 1);
		WebProjectNode n = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.BEANS);
		WebProjectNode conf = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		if(n == null || conf == null) return EMPTY_LIST;
		XModelObject bean = findBean(conf, beanName);
		if(bean == null) return EMPTY_LIST;
		List<Object> list = new ArrayList<Object>();
		XModelObject beanClass = findBeanClass(n, bean);
		if(beanClass == null) return EMPTY_LIST;
		if(property != null && property.length() > 0) beanClass = beanClass.getChildByPath(property.replace('.', '/'));
		if(beanClass == null) return EMPTY_LIST;
		XModelObject[] os = beanClass.getChildren("JSFProjectBeanMethod");
		for (int i = 0; i < os.length; i++) {
			JSFProjectBeanMember bm = (JSFProjectBeanMember)os[i];
			if(returnType == null || bm.hasMethodSignature(returnType, parameterTypes)) {
				list.add(os[i].getAttributeValue("name"));			
			}
		}
		return list;
	}

	public List<Object> getViewActions(XModel model, String viewPath) {
		if(viewPath == null) return EMPTY_LIST;
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return EMPTY_LIST;
		WebProjectNode n = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		if(n == null) return EMPTY_LIST;
		XModelObject[] os = n.getTreeChildren();
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < os.length; i++) {
			XModelObject r = os[i].getChildByPath(JSFConstants.FOLDER_NAVIGATION_RULES);
			if(r == null) continue;
			XModelObject[] rs = r.getChildren();
			for (int j = 0; j < rs.length; j++) {
				if(!OpenCaseHelper.isPatternMatches(rs[j].getAttributeValue(JSFConstants.ATT_FROM_VIEW_ID), viewPath)) continue;
				XModelObject[] cs = rs[j].getChildren();
				for (int k = 0; k < cs.length; k++) {
					String q = cs[k].getAttributeValue(JSFConstants.ATT_FROM_OUTCOME);
					if(!list.contains(q)) list.add(q); 
				}			
			}			
		}
		return list;
	}
	
	//JSF_GET_PATH
	
	List<Object> getPathAsList(XModel model, String url) {
		/*
		 * Fixes https://jira.jboss.org/jira/browse/JBIDE-5577
		 * List will return all possible paths for url. 
		 */
		List<Object> pathsList = new ArrayList<Object>();
		JSFWebProject p = JSFWebProject.getInstance(model);
		if(p == null) {
			pathsList.add(url);
		} else {
			JSFUrlPattern pattern = p.getUrlPattern();
			if (pattern != null) {
				pathsList.addAll(pattern.getJSFPaths(url));
			} else {
				pathsList.add(url);
			}
		}
		return pathsList;
		
	}
	
	String getPath(XModel model, String url) {
		JSFWebProject p = JSFWebProject.getInstance(model);
		if(p == null) {
			return url;
		}
		JSFUrlPattern pattern = p.getUrlPattern();
		return (pattern == null) ? url : pattern.getJSFPath(url);
	}

	public static XModelObject findFacesConfig(XModel model) {
		XModelObject facesConfig = null;
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if (root != null) {
			WebProjectNode n = (WebProjectNode) root
					.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
			if (n != null) {
				/*
				 * The array contains the all configuration files in the project
				 * including files from jar archives.
				 * Only editable object is be the necessary faces-config file.
				 */
				XModelObject[] os = n.getTreeChildren();
				for (XModelObject o : os) {
					if (o.isObjectEditable()) {
						facesConfig = o;
						break;
					}
				}
			}
		}
		/*
		 * When nothing has been found try the last straight-forward way.
		 */
		if (facesConfig == null) {
			facesConfig = model.getByPath("/faces-config.xml"); //$NON-NLS-1$
		}
		return facesConfig;
	}

}
