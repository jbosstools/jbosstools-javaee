package org.jboss.tools.cdi.seam.solder.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.SourceRange;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AnnotationLiteral;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.common.model.XModelObject;

public class CDISeamSolderLoggerExtension implements ICDIExtension, IBuildParticipantFeature {
	CDICoreNature project;
	LoggerDefinitionContext context = new LoggerDefinitionContext();

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void setProject(CDICoreNature n) {
		project = n;
	}

	public IDefinitionContextExtension getContext() {
		return context;
	}

	public void beginVisiting() {
	}

	public void visitJar(IPath path, IPackageFragmentRoot root, XModelObject beansXML) {
	}

	public void visit(IFile file, IPath src, IPath webinf) {
	}

	public void buildDefinitions() {
	}

	public void buildDefinitions(FileSet fileSet) {
		LoggerDefinitionContext workingCopy = context.getWorkingCopy();
		
		Map<IPath, Set<IType>> is = fileSet.getInterfaces();
		for (IPath path: is.keySet()) {
			Set<IType> ts = is.get(path);
			for (IType t: ts) {
				InterfaceDefinition i = new InterfaceDefinition(t);
				if(i.isAnnotationPresent(CDISeamSolderConstants.MESSAGE_LOGGER_ANNOTATION_TYPE_NAME)) {
					TypeDefinition d = new TypeDefinition();
					d.setType(t, workingCopy.getRootContext());
					workingCopy.addMessageLogger(path, d);
				} else if(i.isAnnotationPresent(CDISeamSolderConstants.MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME)) {
					TypeDefinition d = new TypeDefinition();
					d.setType(t, workingCopy.getRootContext());
					workingCopy.addMessageBundle(path, d);
					AnnotationDeclaration ad = d.getAnnotation(CDISeamSolderConstants.MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME);
					if(ad.getMemberValue("projectCode") != null && ad.getMemberValue("projectCode").toString().length() > 0) {
						String text = d.getContent();
						int st = ad.getStartPosition();
						int le = ad.getLength();
						String source = text.substring(st, st + le);
						AnnotationLiteral l = new AnnotationLiteral(d.getResource(), source, new SourceRange(st, le), null, ad.getType());
						d.removeAnnotation(ad);
						d.addAnnotation(l, workingCopy.getRootContext());
					}				
					
				}
			}
		}
	}

	public void buildBeans() {
		CDIProject p = ((CDIProject)project.getDelegate());
		Map<IPath, TypeDefinition> loggers = context.getMessageLoggers();
		for (TypeDefinition d: loggers.values()) {
			ClassBean b = new ClassBean();
			b.setDefinition(d);
			b.setParent(p);
			p.addBean(b);
		}

		Map<IPath, TypeDefinition> bundles = context.getMessageBundles();
		for (TypeDefinition d: bundles.values()) {
			ClassBean b = new ClassBean();
			b.setDefinition(d);
			b.setParent(p);
			p.addBean(b);
		}

	}

	class LoggerDefinitionContext implements IDefinitionContextExtension {
		IRootDefinitionContext root;

		Map<IPath, TypeDefinition> messageLoggers = new HashMap<IPath, TypeDefinition>();
		Map<IPath, TypeDefinition> messageBundles = new HashMap<IPath, TypeDefinition>();
	
		LoggerDefinitionContext original;
		LoggerDefinitionContext workingCopy;

		private LoggerDefinitionContext copy(boolean clean) {
			LoggerDefinitionContext copy = new LoggerDefinitionContext();
			copy.root = root;
			if(!clean) {
				copy.messageLoggers.putAll(messageLoggers);
				copy.messageBundles.putAll(messageBundles);
			}

			return copy;
		}

		public void newWorkingCopy(boolean forFullBuild) {
			if(original != null) return;
			workingCopy = copy(forFullBuild);
			workingCopy.original = this;
		}

		public void applyWorkingCopy() {
			if(original != null) {
				original.applyWorkingCopy();
				return;
			}
			if(workingCopy == null) {
				return;
			}
			messageLoggers = workingCopy.messageLoggers;
			messageBundles = workingCopy.messageBundles;
			
			workingCopy = null;
		}

		public void clean() {
			messageLoggers.clear();
			messageBundles.clear();			
		}

		public void clean(IPath path) {
			messageLoggers.remove(path);
			messageBundles.remove(path);
		}

		public void setRootContext(IRootDefinitionContext context) {
			root = context;
		}

		public IRootDefinitionContext getRootContext() {
			return root;
		}

		public LoggerDefinitionContext getWorkingCopy() {
			if(original != null) {
				return this;
			}
			if(workingCopy != null) {
				return workingCopy;
			}
			workingCopy = copy(false);
			workingCopy.original = this;
			return workingCopy;
		}
	
		public void addMessageLogger(IPath path, TypeDefinition def) {
			messageLoggers.put(path, def);
			root.addToParents(path);
		}
		
		public void addMessageBundle(IPath path, TypeDefinition def) {
			messageBundles.put(path, def);
			root.addToParents(path);
		}
	
		public Map<IPath, TypeDefinition> getMessageLoggers() {
			return messageLoggers;
		}
		
		public Map<IPath, TypeDefinition> getMessageBundles() {
			return messageBundles;
		}
		
	}

	class InterfaceDefinition extends AbstractMemberDefinition {
		InterfaceDefinition(IType type) {
			setAnnotatable(type, type, context.getRootContext());
		}
	}

}
