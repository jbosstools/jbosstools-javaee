/*
 * StrutsGenerator.java
 *
 * Created on March 14, 2003, 12:33 PM
 */

package org.jboss.tools.struts.model.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.jst.web.WebModelPlugin;

public class StrutsGenerator implements StrutsConstants {
    
    public static final int OVER_FALSE   = 0;
    public static final int OVER_TRUE    = 1;
    public static final int OVER_ASK     = 2;
    
	public static final String CODEGEN = "templates/generation/"; //$NON-NLS-1$

    public static final String CODEGEN_10 = CODEGEN + "1.0/"; //$NON-NLS-1$
	public static final String CODEGEN_11 = CODEGEN + "1.1/"; //$NON-NLS-1$
	public static final String CODEGEN_12 = CODEGEN + "1.2/"; //$NON-NLS-1$

	public static final String ACTION_PATH = "action.vtl"; //$NON-NLS-1$
	public static final String FORMBEAN_PATH = "formBean.vtl"; //$NON-NLS-1$
	public static final String FORWARD_PATH = "forward.vtl"; //$NON-NLS-1$
	public static final String EXCEPTION_PATH = "exception.vtl"; //$NON-NLS-1$
	public static final String ACTION_CONFIG_PATH = "actionConfig.vtl"; //$NON-NLS-1$
	public static final String FORMBEAN_CONFIG_PATH = "formBeanConfig.vtl"; //$NON-NLS-1$
	public static final String FORWARD_CONFIG_PATH = "forwardConfig.vtl"; //$NON-NLS-1$
	public static final String EXCEPTION_CONFIG_PATH = "exceptionConfig.vtl"; //$NON-NLS-1$
	static VelocityEngine velocityEngine;
    
/*    private static String[] EXCLUDE_PKGS = {
        "java.", "javax.",
        "org.apache.struts.action.",
        "org.apache.struts.actions.",
        "org.apache.struts.config.",
        "org.apache.struts.util.",
        "org.apache.struts.validator."};
*/  

private static String[] EXCLUDE_PKGS = {
	"java.", "javax.", //$NON-NLS-1$ //$NON-NLS-2$
	"org.apache.struts."}; //$NON-NLS-1$

    private String basePackage = ""; //$NON-NLS-1$
    private File root;
    private int overwrite = OVER_FALSE;

    /** Creates a new instance of StrutsGenerator */
    public StrutsGenerator(File root, int overwrite) {
        this.root = root;
        this.overwrite = overwrite;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
    
    public String generateForward(XModelObject forward, String baseClass, String imports, boolean genProps) {
		String templatePath = getVersionTemplateBase(forward) + FORWARD_PATH;

		String className = forward.getAttributeValue(ATT_CLASSNAME);
		if (!checkClassName(className)) return null;

		String targetPath = new Path(className.replace('.', '/')+".java").toOSString(); //$NON-NLS-1$
		File source = new File(root, targetPath);
		if (!checkOverwrite(source, forward.getModel())) return null;
		targetPath = source.getAbsolutePath();
		
		ArrayList<PropertyDescriptor> properties = getProperties(forward, genProps);
		Map<String,Object> parameters = getParameters(forward, className, baseClass, properties);
		executeTemplate(parameters, templatePath, targetPath);

		if (genProps && properties.size() > 0)  {
			templatePath = getVersionTemplateBase(forward) + FORWARD_CONFIG_PATH;
			className = executePropertiesTemplate(parameters, forward.getModel(), templatePath, className);
		} 

		return className;
    }
    
    public String generateException(XModelObject exception, String baseClass, String imports, boolean genProps) {
		String templatePath = getVersionTemplateBase(exception) + EXCEPTION_PATH;

		String className = exception.getAttributeValue(ATT_TYPE);
		if (!checkClassName(className)) return null;

		String targetPath = new Path(className.replace('.', '/')+".java").toOSString(); //$NON-NLS-1$
		File source = new File(root, targetPath);
		if (!checkOverwrite(source, exception.getModel())) return null;
		targetPath = source.getAbsolutePath();
		
		ArrayList<PropertyDescriptor> properties = getProperties(exception, genProps);
		Map<String,Object> parameters = getParameters(exception, className, baseClass, properties);
		parameters.put("constructors", getConstructors(exception, baseClass)); //$NON-NLS-1$
		
		executeTemplate(parameters, templatePath, targetPath);

		if (genProps && properties.size() > 0)  {
			templatePath = getVersionTemplateBase(exception) + EXCEPTION_CONFIG_PATH;			
			className = executePropertiesTemplate(parameters, exception.getModel(), templatePath, className);
		} 

		return className;
    }
    
    public String generateFormBean(XModelObject formBean, String baseClass, String imports, boolean genProps) {
		String templatePath = getVersionTemplateBase(formBean) + FORMBEAN_PATH;			

		String className = formBean.getAttributeValue(ATT_TYPE);
		if (!checkClassName(className)) return null;

		String targetPath = new Path(className.replace('.', '/')+".java").toOSString(); //$NON-NLS-1$
		File source = new File(root, targetPath);
		if (!checkOverwrite(source, formBean.getModel())) return null;
		targetPath = source.getAbsolutePath();
		
		ArrayList<PropertyDescriptor> properties = getProperties(formBean, genProps);
		Map<String,Object> parameters = getParameters(formBean, className, baseClass, properties);	
		executeTemplate(parameters, templatePath, targetPath);

		if (genProps && properties.size() > 0)  {
			templatePath = getVersionTemplateBase(formBean) + FORMBEAN_CONFIG_PATH;			
			className = executePropertiesTemplate(parameters, formBean.getModel(), templatePath, className);
		} 

		return className;
    }

    VelocityEngine getVelocityEngine() throws Exception {
    	if(velocityEngine == null) {
    		velocityEngine = new VelocityEngine();
			Properties properties = new Properties();
			IPath defaultPath = WebModelPlugin.getTemplateStatePath();
//				new Path(EclipseResourceUtil.getInstallPath(Platform.getBundle("org.jboss.tools.common.projecttemplates")));
			///defaultPath = defaultPath.removeLastSegments(3);
			properties.put("file.resource.loader.path", defaultPath.toOSString()); //$NON-NLS-1$
			String logFileName = Platform.getLocation().append(".metadata").append(".plugins").append(StrutsModelPlugin.PLUGIN_ID).append("velocity.log").toFile().getAbsolutePath(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			properties.put("runtime.log", logFileName); //$NON-NLS-1$
			Velocity.clearProperty("file.resource.loader.path"); //$NON-NLS-1$
			velocityEngine.init(properties);
    	}
    	return velocityEngine;
    }

	private void executeTemplate(Map<String,Object> parameters, String templatePath, String targetPath) {
		ServiceDialog d = PreferenceModelUtilities.getPreferenceModel().getService();

		ClassLoader c = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		try {
			VelocityContext context = new VelocityContext(parameters);
			final Template template;
			
			try {
				template = getVelocityEngine().getTemplate(templatePath);

				File file = new File(targetPath);
				if (file.exists()) {
					if (!file.delete()) {
						throw new RuntimeException("Unable to delete file " + file.getAbsolutePath());
					}
				}
				// Make directories
				File folder = file.getParentFile();
				folder.mkdirs();
				if (!folder.exists() || !folder.isDirectory()) {
					throw new RuntimeException("Unable to create folder " + folder.getAbsolutePath());
				}
				//// BufferedWriter writer = writer = new BufferedWriter(new OutputStreamWriter(System.out));
				Writer writer = new BufferedWriter(new FileWriter(file));

				if ( template != null) template.merge(context, writer);

				writer.flush();
				writer.close();

			} catch (ResourceNotFoundException rnfe) {
				StrutsModelPlugin.getPluginLog().logError(rnfe);
				d.showDialog(StrutsUIMessages.ERROR, rnfe.getMessage(), new String[]{StrutsUIMessages.OK}, null, ServiceDialog.ERROR);
///				ErrorDialog.openError(ModelPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),rnfe);
			} catch (ParseErrorException pee) {
				StrutsModelPlugin.getPluginLog().logError(pee);
				d.showDialog(StrutsUIMessages.ERROR, pee.getMessage(), new String[]{StrutsUIMessages.OK}, null, ServiceDialog.ERROR);
///				ErrorDialog.openError(ModelPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),pee);
			}

		} catch (Exception e) {
			StrutsModelPlugin.getPluginLog().logError(e);
			d.showDialog(StrutsUIMessages.ERROR, e.getMessage(), new String[]{StrutsUIMessages.OK}, null, ServiceDialog.ERROR);
///			ErrorDialog.openError(ModelPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),e);
		} finally {
			Thread.currentThread().setContextClassLoader(c);
		}
	};

    public String generateAction(XModelObject action, String baseClass, String imports, boolean genProps, boolean genGlobal, boolean genLocal) {
		int version = getVersion(action);
		String templatePath = getVersionTemplateBase(action) + ACTION_PATH;

		String className = action.getAttributeValue(ATT_TYPE);
		if (!checkClassName(className)) return null;

		String targetPath = new Path(className.replace('.', '/')+".java").toOSString(); //$NON-NLS-1$
		File source = new File(root, targetPath);
		if (!checkOverwrite(source, action.getModel())) return null;
		targetPath = source.getAbsolutePath();
		
		ArrayList<PropertyDescriptor> properties = getProperties(action, genProps);

		ArrayList<Object> globalForwards = null;
		if (genGlobal) {
			globalForwards = new ArrayList<Object>();
			XModelObject[] globs = action.getParent().getParent().getChildByPath(ELM_GLOBALFORW).getChildren();
			for (int i = 0; i < globs.length; i++) {
				String name = globs[i].getAttributeValue(ATT_NAME);
				globalForwards.add(name);
			}
		}
		ArrayList<Object> localForwards = null;
		if (genLocal) {
			localForwards = new ArrayList<Object>();
			XModelObject[] locs = action.getChildren(ENT_FORWARD + version);
			for (int i = 0; i < locs.length; i++) {
				String name = locs[i].getAttributeValue(ATT_NAME);
				localForwards.add(name);
			}
		}

		Map<String,Object> parameters = getParameters(action, className, baseClass, properties);
		if (genGlobal) parameters.put("globalForwards", globalForwards); //$NON-NLS-1$
		if (genLocal)  parameters.put("localForwards", localForwards); //$NON-NLS-1$
		
		executeTemplate(parameters, templatePath, targetPath);

		if (genProps && properties.size() > 0)  {
			templatePath = getVersionTemplateBase(action) + ACTION_CONFIG_PATH;			
			className = executePropertiesTemplate(parameters, action.getModel(), templatePath, className);
		} 

        return className;
    }
    
    private List<MethodDescriptor> getConstructors(XModelObject object, String baseClass) {
    	List<MethodDescriptor> constructors = new ArrayList<MethodDescriptor>();
    	
    	try {
    		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(EclipseResourceUtil.getProject(object));
    		IType type = EclipseJavaUtil.findType(javaProject, baseClass);
    		if(type == null) return constructors;
    		IJavaElement[] ch = type.getChildren();
			for (int i = 0; i < ch.length; i++) {
				if(!(ch[i] instanceof IMethod)) continue;
				IMethod method = (IMethod)ch[i];
				if(!method.isConstructor()) continue;
				constructors.add(new MethodDescriptor(method));
				
			}
		} catch (JavaModelException ex)	{
			StrutsModelPlugin.getPluginLog().logError(ex);
		}
    	
    	return constructors;
    }
    
    private boolean checkClassName(String className) {
        if (className.length() == 0 || !className.startsWith(basePackage)) {
            return false;
        }
        for (int i = 0; i < EXCLUDE_PKGS.length; i++) {
            if (className.startsWith(EXCLUDE_PKGS[i])) return false;
        }
        return true;
    }
    
    private boolean checkOverwrite(File source, XModel model) {
        if (source.exists()) {
            switch (overwrite) {
                case OVER_TRUE: return true;
                case OVER_FALSE: return false;
                case OVER_ASK:
                    ServiceDialog d = model.getService();
                    int r = d.showDialog(StrutsUIMessages.GENERATE_JAVA_CODE,
                        NLS.bind(StrutsUIMessages.FILE_ALREADY_EXISTS, source.getAbsolutePath()), //$NON-NLS-2$
                        new String[] {StrutsUIMessages.ABORT, StrutsUIMessages.CONTINUE}, null, ServiceDialog.WARNING);
                    return r == 1;
            }
        }
        return true;
    }

    public static int getVersion(XModelObject object) {
        String entity = object.getModelEntity().getName();
        return entity.endsWith(VER_SUFFIX_10) ? 10 : entity.endsWith(VER_SUFFIX_12) ? 12 : 11;
    }
    
    private static String getVersionTemplateBase(XModelObject object) {
    	int v = getVersion(object);
    	return v == 10 ? CODEGEN_10 : v == 11 ? CODEGEN_11 : v == 12 ? CODEGEN_12 : CODEGEN_11;
    }
    
	private String getClassName(String fullName) {
		int ind = fullName.lastIndexOf('.');
		return (ind > 0) ? fullName.substring(ind+1) : fullName;
	}
	
	private String getPackageName(String fullName) {
		if (basePackage != null && basePackage.length() > 0) return basePackage;
		int ind = fullName.lastIndexOf('.');
		return (ind > 0) ? fullName.substring(0, ind) : ""; //$NON-NLS-1$
	}
	
	private Map<String,Object> getParameters(XModelObject o, String className, String baseClass, ArrayList<PropertyDescriptor> properties) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("package", getPackageName(className)); //$NON-NLS-1$
		parameters.put("className", getClassName(className)); //$NON-NLS-1$
		parameters.put("extends", baseClass); //$NON-NLS-1$
		if (properties != null) parameters.put("properties", properties); //$NON-NLS-1$
		return parameters;
	}
	
	private ArrayList<PropertyDescriptor> getProperties(XModelObject o, boolean genProps) {
		ArrayList<PropertyDescriptor> p = null;
		if (genProps) {
			p = new ArrayList<PropertyDescriptor>();
			XModelObject[] props = o.getChildren(ENT_SETPROPERTY); /// one entity for both versions
			for (int i = 0; i < props.length; i++) {
				String name = props[i].getAttributeValue(ATT_PROPERTY);
				p.add(new PropertyDescriptor(name));
			}
		}
		return p;
	}
	
	private String executePropertiesTemplate(Map<String,Object> parameters, XModel model, String templatePath, String className) {
		className = className + "Config"; //$NON-NLS-1$
		parameters.put("className", getClassName(className)); //$NON-NLS-1$
		String targetPath = new Path(className.replace('.', '/')+".java").toOSString(); //$NON-NLS-1$
		File source = new File(root, targetPath);
		if (!checkOverwrite(source, model)) return null;
		targetPath = source.getAbsolutePath();
		executeTemplate(parameters, templatePath, targetPath);
		return className;
	}
    
}
