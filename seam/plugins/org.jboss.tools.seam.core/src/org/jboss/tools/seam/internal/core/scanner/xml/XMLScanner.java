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
package org.jboss.tools.seam.internal.core.scanner.xml;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.internal.core.InnerModelHelper;
import org.jboss.tools.seam.internal.core.SeamProperty;
import org.jboss.tools.seam.internal.core.SeamValueList;
import org.jboss.tools.seam.internal.core.SeamValueMap;
import org.jboss.tools.seam.internal.core.SeamValueMapEntry;
import org.jboss.tools.seam.internal.core.SeamValueString;
import org.jboss.tools.seam.internal.core.SeamXmlComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamXmlFactory;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;

/**
 * @author Viacheslav Kabanovich
 */
public class XMLScanner implements IFileScanner {
	
	public XMLScanner() {}

	/**
	 * Returns true if file is probable component source - 
	 * has components.xml name or *.component.xml mask.
	 * @param resource
	 * @return
	 */	
	public boolean isRelevant(IFile resource) {
		if(resource.getName().equals("components.xml")) return true;
		if(resource.getName().endsWith(".component.xml")) return true;
		return false;
	}
	
	/**
	 * This method should be called only if isRelevant returns true;
	 * Makes simple check if this java file contains annotation Name. 
	 * @param resource
	 * @return
	 */
	public boolean isLikelyComponentSource(IFile f) {
		if(!f.isSynchronized(IFile.DEPTH_ZERO) || !f.exists()) return false;
		XModel model = InnerModelHelper.createXModel(f.getProject());
		if(model == null) return false;
		XModelObject o = EclipseResourceUtil.getObjectByResource(model, f);
		if(o == null) return false;
		if(o.getModelEntity().getName().startsWith("FileSeamComponent")) return true;
		return false;
	}

	/**
	 * Returns list of components
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public LoadedDeclarations parse(IFile f) throws Exception {
		XModel model = InnerModelHelper.createXModel(f.getProject());
		if(model == null) return null;
		XModelObject o = EclipseResourceUtil.getObjectByResource(model, f);
		return parse(o, f.getFullPath());
	}
	
	static Set<String> COMMON_ATTRIBUTES = new HashSet<String>();
	static Set<String> INTERNAL_ATTRIBUTES = new HashSet<String>();
	
	static {
		COMMON_ATTRIBUTES.add(ISeamXmlComponentDeclaration.NAME);
		COMMON_ATTRIBUTES.add(ISeamXmlComponentDeclaration.CLASS);
		COMMON_ATTRIBUTES.add(ISeamXmlComponentDeclaration.SCOPE);
		COMMON_ATTRIBUTES.add(ISeamXmlComponentDeclaration.PRECEDENCE);
		COMMON_ATTRIBUTES.add(ISeamXmlComponentDeclaration.INSTALLED);
		COMMON_ATTRIBUTES.add(ISeamXmlComponentDeclaration.AUTO_CREATE);
		COMMON_ATTRIBUTES.add(ISeamXmlComponentDeclaration.JNDI_NAME);
		
		INTERNAL_ATTRIBUTES.add("NAME");
		INTERNAL_ATTRIBUTES.add("EXTENSION");
		INTERNAL_ATTRIBUTES.add("#comment");
	}
	
	public LoadedDeclarations parse(XModelObject o, IPath source) {
		if(o == null) return null;
		LoadedDeclarations ds = new LoadedDeclarations();
		if(o.getModelEntity().getName().equals("FileSeamComponent12")) {
			parseComponent(o, source, ds);
			return ds;
		}
		XModelObject[] os = o.getChildren();
		for (int i = 0; i < os.length; i++) {
			XModelEntity componentEntity = os[i].getModelEntity();
			if(componentEntity.getAttribute("class") != null) {
				parseComponent(os[i], source, ds);
			} else if(os[i].getModelEntity().getName().startsWith("SeamFactory")) {
				SeamXmlFactory factory = new SeamXmlFactory();
				factory.setId(os[i]);
				factory.setSourcePath(source);
				factory.setName(new XMLValueInfo(os[i], ISeamXmlComponentDeclaration.NAME));
				factory.setScope(new XMLValueInfo(os[i], ISeamXmlComponentDeclaration.SCOPE));
				factory.setValue(new XMLValueInfo(os[i], "value"));
				factory.setMethod(new XMLValueInfo(os[i], "method"));
				ds.getFactories().add(factory);
			}
		}
		return ds;
	}
	
	private void parseComponent(XModelObject c, IPath source, LoadedDeclarations ds) {
		SeamXmlComponentDeclaration component = new SeamXmlComponentDeclaration();
		
		component.setSourcePath(source);
		component.setId(c);

		component.setName(new XMLValueInfo(c, getComponentAttribute(c)));
		if(isClassAttributeSet(c)) {
			component.setClassName(new XMLValueInfo(c, ISeamXmlComponentDeclaration.CLASS));
		} else if(c.getModelEntity().getName().equals("FileSeamComponent12")) {
			component.setClassName(getImpliedClassName(c, source));
		} else {
			String className = getDefaultClassName(c);
			if(className != null) {
				component.setClassName(className);
			}
		}
		component.setScope(new XMLValueInfo(c, ISeamXmlComponentDeclaration.SCOPE));
		component.setPrecedence(new XMLValueInfo(c, ISeamXmlComponentDeclaration.PRECEDENCE));
		component.setInstalled(new XMLValueInfo(c, ISeamXmlComponentDeclaration.INSTALLED));
		component.setAutoCreate(new XMLValueInfo(c, ISeamXmlComponentDeclaration.AUTO_CREATE));
		component.setJndiName(new XMLValueInfo(c, ISeamXmlComponentDeclaration.JNDI_NAME));
		
		XAttribute[] attributes = c.getModelEntity().getAttributes();
		for (int ia = 0; ia < attributes.length; ia++) {
			XAttribute a = attributes[ia];
			String xml = a.getXMLName();
			if(xml == null || xml.length() == 0 || "#comment".equals(xml)) continue;
			if(COMMON_ATTRIBUTES.contains(xml)) continue;
			if(INTERNAL_ATTRIBUTES.contains(xml)) continue;
			if(xml.indexOf(":") >= 0) continue;
			if(xml.startsWith("xmlns")) continue;
			
			SeamProperty p = new SeamProperty();
			p.setId(xml);
			p.setName(new XMLValueInfo(c, "&" + a.getName()));
			p.setName(toCamelCase(xml, false));
			SeamValueString v = new SeamValueString();
			v.setId("value");
			p.setValue(v);
			v.setValue(new XMLValueInfo(c, a.getName()));
			component.addProperty(p);
		}

		XModelObject[] properties = c.getChildren();
		for (int j = 0; j < properties.length; j++) {
			XModelEntity entity = properties[j].getModelEntity();

			SeamProperty p = new SeamProperty();
			p.setId(properties[j]);
			p.setName(new XMLValueInfo(properties[j], "name"));
			String name = properties[j].getAttributeValue("name");
			String cname = toCamelCase(name, false);
			if(!cname.equals(name)) p.setName(cname);

			if(entity.getAttribute("value") != null) {
				//this is simple value;
				SeamValueString v = new SeamValueString();
				v.setId(properties[j]);
				v.setValue(new XMLValueInfo(properties[j], "value"));
				p.setValue(v);
			} else {
				XModelObject[] entries = properties[j].getChildren();
				if(entity.getChild("SeamListEntry") != null
					|| "list".equals(entity.getProperty("childrenLoader"))) {
					//this is list value
					
					SeamValueList vl = new SeamValueList();
					vl.setId(properties[j]);
					
					for (int k = 0; k < entries.length; k++) {
						SeamValueString v = new SeamValueString();
						v.setId(entries[k]);
						v.setValue(new XMLValueInfo(entries[k], "value"));
						vl.addValue(v);
					}
					p.setValue(vl);
				} else {
					//this is map value
					SeamValueMap vm = new SeamValueMap();
					vm.setId(properties[j]);
					for (int k = 0; k < entries.length; k++) {
						SeamValueMapEntry e = new SeamValueMapEntry();
						e.setId(entries[k]);
						SeamValueString key = new SeamValueString();
						key.setId(entries[k]);
						key.setValue(new XMLValueInfo(entries[k], "key"));
						e.setKey(key);
						SeamValueString value = new SeamValueString();
						value.setId(entries[k]);
						value.setValue(new XMLValueInfo(entries[k], "value"));
						e.setValue(value);
						vm.addEntry(e);
					}
					p.setValue(vm);
				}
			}
			component.addProperty(p);
		}

		ds.getComponents().add(component);
	}
	
	private String getComponentAttribute(XModelObject c) {
		if(c.getModelEntity().getAttribute("component-name") != null) {
			return "component-name";
		}
		return ISeamXmlComponentDeclaration.NAME;
	}
	
	private boolean isClassAttributeSet(XModelObject c) {
		String value = c.getAttributeValue(ISeamXmlComponentDeclaration.CLASS);
		return value != null && value.length() > 0;
	}
	
	private String getImpliedClassName(XModelObject c, IPath path) {
		if(path.toString().endsWith(".jar")) {
			String suffix = ".component";
			String cn = c.getAttributeValue("name");
			if(cn.endsWith(suffix)) cn = cn.substring(0, cn.length() - suffix.length());
			XModelObject p = c.getParent();
			while(p != null && p.getFileType() == XModelObject.FOLDER) {
				cn = p.getAttributeValue("name") + "." + cn;
				p = p.getParent();
			}
			return cn;
		} else {
			IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if(!f.exists()) return "";
			IResource root = EclipseResourceUtil.getJavaSourceRoot(f.getProject());
			if(!root.getLocation().isPrefixOf(f.getLocation())) return "";
			String relative = f.getLocation().toString().substring(root.getLocation().toString().length());
			String suffix = ".component.xml";
			if(relative.endsWith(suffix)) {
				relative = relative.substring(0, relative.length() - suffix.length());
				relative = relative.replace('\\', '/');
				if(relative.startsWith("/")) relative = relative.substring(1);
				return relative.replace('/', '.');
			}
		}
		return null;
	}
	
	/**
	 * This is only limited to supported namespaces provided by seam.
	 * @param c
	 * @return
	 */
	private String getDefaultClassName(XModelObject c) {
		String s = c.getModelEntity().getXMLSubPath();
		int d = s.indexOf(':');
		if(d < 0) return null;
		String namespace = s.substring(0, d);
		String tag = s.substring(d + 1);
		String className = "org.jboss.seam." + namespace + "." + toCamelCase(tag, true);
		return className;
	}

	/**
	 * Copied from org.jboss.seam.init.Initialization
	 * @param hyphenated
	 * @param initialUpper
	 * @return
	 */
	   private static String toCamelCase(String hyphenated, boolean initialUpper)
	   {
	      StringTokenizer tokens = new StringTokenizer(hyphenated, "-");
	      StringBuilder result = new StringBuilder( hyphenated.length() );
	      String firstToken = tokens.nextToken();
	      if (initialUpper)
	      {
	         result.append( Character.toUpperCase( firstToken.charAt(0) ) )
	         .append( firstToken.substring(1) );         
	      }
	      else
	      {
	         result.append(firstToken);
	      }
	      while ( tokens.hasMoreTokens() )
	      {
	         String token = tokens.nextToken();
	         result.append( Character.toUpperCase( token.charAt(0) ) )
	               .append( token.substring(1) );
	      }
	      return result.toString();
	   }

}