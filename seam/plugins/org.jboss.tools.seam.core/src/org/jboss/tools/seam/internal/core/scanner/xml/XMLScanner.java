/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.scanner.xml;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ModelUpdater;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.project.ext.impl.ValueInfo;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.NamespaceMapping;
import org.jboss.tools.jst.web.model.helpers.InnerModelHelper;
import org.jboss.tools.jst.web.model.project.ext.store.XMLValueInfo;
import org.jboss.tools.seam.core.ISeamNamespace;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.event.ISeamValue;
import org.jboss.tools.seam.core.event.ISeamValueString;
import org.jboss.tools.seam.internal.core.SeamImport;
import org.jboss.tools.seam.internal.core.SeamProperty;
import org.jboss.tools.seam.internal.core.SeamValueList;
import org.jboss.tools.seam.internal.core.SeamValueMap;
import org.jboss.tools.seam.internal.core.SeamValueMapEntry;
import org.jboss.tools.seam.internal.core.SeamValueString;
import org.jboss.tools.seam.internal.core.SeamXmlComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamXmlFactory;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.ScannerException;

/**
 * @author Viacheslav Kabanovich
 */
public class XMLScanner implements IFileScanner {
	
	private XModel model;
	private XModelObject o;

	public XMLScanner() {}

	/**
	 * Returns true if file is probable component source - 
	 * has components.xml name or *.component.xml mask.
	 * @param resource
	 * @return
	 */	
	public boolean isRelevant(IFile resource) {
		return resource.getName().equals("components.xml") || resource.getName().endsWith(".component.xml");
	}
	
	/**
	 * This method should be called only if isRelevant returns true;
	 * Makes simple check if this java file contains annotation Name. 
	 * @param resource
	 * @return
	 */
	public boolean isLikelyComponentSource(IFile f) {
		cleanState();
		boolean isComponentSource = false;
		if(f.isSynchronized(IFile.DEPTH_ZERO) && f.exists()) {
			model = InnerModelHelper.createXModel(f.getProject());
			if(model != null) {
				o = EclipseResourceUtil.getObjectByResource(model, f);
				if(o != null) {
					isComponentSource = o.getModelEntity().getName().startsWith("FileSeamComponent"); //$NON-NLS-1$
				}
			}
		}
		if(!isComponentSource) {
			cleanState();
		}
		return isComponentSource;
	}

	private void cleanState() {
		model = null;
		o = null;
	}
	
	/**
	 * Returns list of components
	 * @param f
	 * @return
	 * @throws ScannerException
	 */
	public LoadedDeclarations parse(IFile f, ISeamProject sp) throws ScannerException {
		model = InnerModelHelper.createXModel(f.getProject());
		o = EclipseResourceUtil.getObjectByResource(model, f);
		return parse(o, f.getFullPath(), sp);
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
		COMMON_ATTRIBUTES.add(ISeamXmlComponentDeclaration.STARTUP);
		COMMON_ATTRIBUTES.add(ISeamXmlComponentDeclaration.STARTUP_DEPENDS);
		
		INTERNAL_ATTRIBUTES.add("NAME"); //$NON-NLS-1$
		INTERNAL_ATTRIBUTES.add("EXTENSION"); //$NON-NLS-1$
		INTERNAL_ATTRIBUTES.add("#comment"); //$NON-NLS-1$
	}
	
	public LoadedDeclarations parse(XModelObject o, IPath source, ISeamProject sp) {
		NamespaceMapping nm = NamespaceMapping.load(o);
		
		if(o.getParent() instanceof FolderImpl) {
			IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(source);
			if(f != null && f.exists()) {
				try {
					((FolderImpl)o.getParent()).updateChildFile(o, f.getLocation().toFile());
				} catch (XModelException e) {
					ModelPlugin.getPluginLog().logError(e);
				}
				if(o.getParent() == null) {
					boolean b = isLikelyComponentSource(f);
					if(!b) return null;
					o = EclipseResourceUtil.getObjectByResource(o.getModel(), f);
					if(o == null) return null;
				}
			}
		}
		
		LoadedDeclarations ds = new LoadedDeclarations();
		String fileEntity = o.getModelEntity().getName();
		if(fileEntity.startsWith("FileSeamComponent") && !fileEntity.startsWith("FileSeamComponents")) { //$NON-NLS-1$
			parseComponent(o, source, nm, sp, ds);
			return ds;
		}
		XModelObject[] os = o.getChildren();
		for (int i = 0; i < os.length; i++) {
			XModelEntity componentEntity = os[i].getModelEntity();
			if(componentEntity.getAttribute("class") != null) { //$NON-NLS-1$
				parseComponent(os[i], source, nm, sp, ds);
			} else if(os[i].getModelEntity().getName().startsWith("SeamFactory")) { //$NON-NLS-1$
				SeamXmlFactory factory = new SeamXmlFactory();
				factory.setId(os[i]);
				factory.setSourcePath(source);
				factory.setName(new XMLValueInfo(os[i], ISeamXmlComponentDeclaration.NAME));
				factory.setScope(new XMLValueInfo(os[i], ISeamXmlComponentDeclaration.SCOPE));
				factory.setValue(new XMLValueInfo(os[i], "value")); //$NON-NLS-1$
				factory.setMethod(new XMLValueInfo(os[i], "method")); //$NON-NLS-1$
				ds.getFactories().add(factory);
			} else if(os[i].getModelEntity().getName().startsWith("SeamImport")) { //$NON-NLS-1$
				String v = os[i].getAttributeValue("import");
				if(v != null && v.length() > 0) {
					SeamImport s = new SeamImport();
					s.setSeamPackage(v);
					ds.getImports().add(s);
				}
			}
		}
		return ds;
	}
	
	private void parseComponent(XModelObject c, IPath source, NamespaceMapping nm, ISeamProject sp, LoadedDeclarations ds) {
		SeamXmlComponentDeclaration component = new SeamXmlComponentDeclaration();
		
		component.setSourcePath(source);
		component.setId(c);

		component.setName(new XMLValueInfo(c, getComponentAttribute(c)));
		if(isClassAttributeSet(c)) {
			component.setClassName(new XMLValueInfo(c, ISeamXmlComponentDeclaration.CLASS));
		} else if(c.getModelEntity().getName().equals("FileSeamComponent12")) { //$NON-NLS-1$
			component.setClassName(getImpliedClassName(c, source));
		} else {
			String className = getDefaultClassName(c, nm, sp);
			if(className != null) {
				component.setClassName(className);
				component.setClassNameGuessed(true);
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
			if(xml == null || xml.length() == 0 || "#comment".equals(xml)) continue; //$NON-NLS-1$
			if(COMMON_ATTRIBUTES.contains(xml)) continue;
			if(INTERNAL_ATTRIBUTES.contains(xml)) continue;
			if(xml.indexOf(":") >= 0) continue; //$NON-NLS-1$
			if(xml.startsWith("xmlns")) continue; //$NON-NLS-1$
			
			SeamProperty p = new SeamProperty();
			p.setId(xml);
			p.setName(new XMLValueInfo(c, "&" + a.getName())); //$NON-NLS-1$
			p.setName(toCamelCase(xml, false));
			SeamValueString v = new SeamValueString();
			v.setId("value"); //$NON-NLS-1$
			p.setValue(v);
			v.setValue(new XMLValueInfo(c, a.getName()));
			component.addProperty(p);
		}

		XModelObject[] properties = c.getChildren();
		for (int j = 0; j < properties.length; j++) {
			XModelEntity entity = properties[j].getModelEntity();

			SeamProperty p = new SeamProperty();
			p.setId(properties[j]);
			p.setName(new XMLValueInfo(properties[j], "name")); //$NON-NLS-1$
			String name = properties[j].getAttributeValue("name"); //$NON-NLS-1$
			if(name == null) {
				SeamCorePlugin.getPluginLog().logWarning("Entity " + entity.getName() + " has no 'name' attribute");
				continue;
			}
			String cname = toCamelCase(name, false);
			if(!cname.equals(name)) p.setName(cname);

			if(entity.getAttribute("value") != null) { //$NON-NLS-1$
				//this is simple value;
				SeamValueString v = new SeamValueString();
				v.setId(properties[j]);
				v.setValue(new XMLValueInfo(properties[j], "value")); //$NON-NLS-1$
				p.setValue(v);
			} else {
				XModelObject[] entries = properties[j].getChildren();
				if(entity.getChild("SeamListEntry") != null //$NON-NLS-1$
					|| "list".equals(entity.getProperty("childrenLoader"))) { //$NON-NLS-1$ //$NON-NLS-2$
					//this is list value
					
					SeamValueList vl = new SeamValueList();
					vl.setId(properties[j]);
					
					for (int k = 0; k < entries.length; k++) {
						SeamValueString v = new SeamValueString();
						v.setId(entries[k]);
						v.setValue(new XMLValueInfo(entries[k], "value")); //$NON-NLS-1$
						vl.addValue(v);
					}
					p.setValue(vl);
					
					//Sometimes there is an attribute for the same property
					SeamProperty pa = (SeamProperty)component.getProperty(cname);
					if(pa != null) {
						ISeamValue v = pa.getValue();
						if(v instanceof ISeamValueString) {
							ISeamValueString s = (ISeamValueString)v;
							if(s.getValue() != null) {
								String ss = s.getValue().getValue();
								if(ss != null && ss.length() > 0) {
									String[] qs = ss.split(",");
									for (int i = 0; i < qs.length; i++) {
										SeamValueString vi = new SeamValueString();
										vi.setId(pa.getId());
										ValueInfo info = new ValueInfo();
										info.setValue(qs[i]);
										vi.setValue(info);
										vl.addValue(vi);										
									}
								}
							}
						}
					}
					
				} else {
					//this is map value
					SeamValueMap vm = new SeamValueMap();
					vm.setId(properties[j]);
					for (int k = 0; k < entries.length; k++) {
						SeamValueMapEntry e = new SeamValueMapEntry();
						e.setId(entries[k]);
						SeamValueString key = new SeamValueString();
						key.setId(entries[k]);
						key.setValue(new XMLValueInfo(entries[k], "key")); //$NON-NLS-1$
						e.setKey(key);
						SeamValueString value = new SeamValueString();
						value.setId(entries[k]);
						value.setValue(new XMLValueInfo(entries[k], "value")); //$NON-NLS-1$
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
		if(c.getModelEntity().getAttribute("component-name") != null) { //$NON-NLS-1$
			return "component-name"; //$NON-NLS-1$
		}
		return ISeamXmlComponentDeclaration.NAME;
	}
	
	private boolean isClassAttributeSet(XModelObject c) {
		String value = c.getAttributeValue(ISeamXmlComponentDeclaration.CLASS);
		return value != null && value.length() > 0;
	}
	
	public static String getImpliedClassName(XModelObject c, IPath path) {
		if(EclipseResourceUtil.isJar(path.toString())) {
			String suffix = ".component"; //$NON-NLS-1$
			String cn = c.getAttributeValue("name"); //$NON-NLS-1$
			if(cn.endsWith(suffix)) cn = cn.substring(0, cn.length() - suffix.length());
			XModelObject p = c.getParent();
			while(p != null && p.getFileType() == XModelObject.FOLDER) {
				cn = p.getAttributeValue("name") + "." + cn; //$NON-NLS-1$ //$NON-NLS-2$
				p = p.getParent();
			}
			return cn;
		} else {
			IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if(!f.exists()) return ""; //$NON-NLS-1$
			IResource root = EclipseResourceUtil.getJavaSourceRoot(f.getProject());
			if(!root.getLocation().isPrefixOf(f.getLocation())) return ""; //$NON-NLS-1$
			String relative = f.getLocation().toString().substring(root.getLocation().toString().length());
			String suffix = ".component.xml"; //$NON-NLS-1$
			if(relative.endsWith(suffix)) {
				relative = relative.substring(0, relative.length() - suffix.length());
				relative = relative.replace('\\', '/');
				if(relative.startsWith("/")) relative = relative.substring(1); //$NON-NLS-1$
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
	public static String getDefaultClassName(XModelObject c, NamespaceMapping nm, ISeamProject sp) {
		String s = c.getModelEntity().getXMLSubPath();
		int d = s.indexOf(':');
		if(d < 0) return null;

		Map<String, Set<ISeamNamespace>> ns = sp == null ? null : sp.getNamespaces();

		String namespace = s.substring(0, d);
		String tag = s.substring(d + 1);

		String packageName = "org.jboss.seam." + namespace;
		String name = toCamelCase(tag, true);
		if(nm != null) {
			String uri = nm.getURIForDefaultNamespace(namespace);
			if(uri != null && ns != null && !ns.isEmpty()) {
				Set<ISeamNamespace> set = ns.get(uri);
				
				if(set != null && !set.isEmpty()) for (ISeamNamespace n: set) {
					String pn = n.getPackage();
					if(pn == null || pn.length() == 0) continue;
					String cn = pn + "." + name;
					packageName = pn;
					if(set.size() == 1) break;
					IJavaProject jp = EclipseResourceUtil.getJavaProject(sp.getProject());
					IType type = null;
					try {
						type = EclipseJavaUtil.findType(jp, cn);
					} catch (JavaModelException e) {
						ModelPlugin.getPluginLog().logError(e);
					}
					if(type != null) {
						break;
					}
				}
			}
		}
		String className = packageName + "." + name; //$NON-NLS-1$ 
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
	      StringTokenizer tokens = new StringTokenizer(hyphenated, "-"); //$NON-NLS-1$
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