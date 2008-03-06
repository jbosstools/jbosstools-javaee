package org.jboss.tools.seam.ui.text.java.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.ScannerException;
import org.jboss.tools.seam.internal.core.scanner.Util;
import org.jboss.tools.seam.internal.core.scanner.java.ASTVisitorImpl;
import org.jboss.tools.seam.internal.core.scanner.java.AnnotatedASTNode;
import org.jboss.tools.seam.internal.core.scanner.java.ComponentBuilder;
import org.jboss.tools.seam.internal.core.scanner.java.JavaScanner;
import org.jboss.tools.seam.internal.core.scanner.java.ResolvedAnnotation;
import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;

public class JavaAnnotationScanner {
	
	public JavaAnnotationScanner() {}

	/**
	 * Returns component or list of component
	 * TODO change return type
	 * @param f
	 * @return
	 * @throws ScannerException
	 */
/*	public LoadedDeclarations parse(IFile f) throws ScannerException {
		ICompilationUnit u = null;
		try {
			u = getCompilationUnit(f);
		} catch (CoreException e) {
			throw new ScannerException(
					NLS.bind(SeamCoreMessages.JAVA_SCANNER_CANNOT_GET_COMPILATION_UNIT_FOR,f), e);
		}
		if(u == null) return null;
		ASTRequestorImpl requestor = new ASTRequestorImpl(f);
		ICompilationUnit[] us = new ICompilationUnit[]{u};
		ASTParser p = ASTParser.newParser(AST.JLS3);
		p.setSource(u);
		p.setResolveBindings(true);
		p.createASTs(us, new String[0], requestor, null);
		return requestor.getDeclarations();
	}
*/
	/**
	 * Returns component or list of component
	 * TODO change return type
	 * @param u
	 * @return
	 * @throws ScannerException
	 */
	public void parse(ICompilationUnit u) throws ScannerException {
		resolvedAnnotations = null;
		resolvedType = null;
		if(u == null) return;
		ASTRequestorImpl requestor = new ASTRequestorImpl(u);
		ICompilationUnit[] us = new ICompilationUnit[]{u};
		ASTParser p = ASTParser.newParser(AST.JLS3);
		p.setSource(u);
		p.setResolveBindings(true);
		p.createASTs(us, new String[0], requestor, null);
		resolvedType = requestor.getType();
		resolvedAnnotations = requestor.getAnnotations();
	}

	Map<ResolvedAnnotation, AnnotatedASTNode<ASTNode>> resolvedAnnotations = null;
	IType resolvedType = null;
	
	public Map<ResolvedAnnotation, AnnotatedASTNode<ASTNode>> getResolvedAnnotations() {
		return resolvedAnnotations;
	}

	public IType getResolvedType() {
		return resolvedType;
	}
	
	
	private ICompilationUnit getCompilationUnit(IFile f) throws CoreException {
		IProject project = f.getProject();
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		IResource[] rs = EclipseResourceUtil.getJavaSourceRoots(project);
		for (int i = 0; i < rs.length; i++) {
			if(rs[i].getFullPath().isPrefixOf(f.getFullPath())) {
				IPath path = f.getFullPath().removeFirstSegments(rs[i].getFullPath().segmentCount());
				IJavaElement e = javaProject.findElement(path);
				if(e instanceof ICompilationUnit) {
					return (ICompilationUnit)e;
				}
			}
		}
		return null;
	}


	class ASTRequestorImpl extends ASTRequestor {
		private ASTVisitorImpl visitor = new ASTVisitorImpl();
//		LoadedDeclarations ds = new LoadedDeclarations();
		List<ResolvedAnnotation> annotations = new ArrayList<ResolvedAnnotation>();
		IResource resource;
		IPath sourcePath;
		ICompilationUnit unit;
		
		public ASTRequestorImpl(IResource resource) {
			this.resource = resource;
			this.sourcePath = resource.getFullPath();
		}
		public ASTRequestorImpl(ICompilationUnit unit) {
			this.unit = unit;
			this.resource = unit.getResource();
			this.sourcePath = resource.getFullPath();
		}

//		public LoadedDeclarations getDeclarations() {
//			return ds;
//		}
		
		public Map<ResolvedAnnotation, AnnotatedASTNode<ASTNode>> getAnnotations() {
			return annotationMap;
		}
		
		public IType getType() {
			return type;
		}
		
		public void acceptAST(ICompilationUnit source, CompilationUnit ast) {
			IType[] ts = null;
			try {
				ts = source.getTypes();
			} catch (JavaModelException e) {
				//ignore
			}
			if(ts == null || ts.length == 0) return;
			for (int i = 0; i < ts.length; i++) {
				visitor.setType(null);
				int f = 0;
				try {
					f = ts[i].getFlags();
				} catch (JavaModelException e) {
					//ignore
					continue;
				}
				if(Flags.isPublic(f)) {
					visitor.setType(ts[i]);
					ast.accept(visitor);
					if(!visitor.hasSeamComponent()) continue;
					processTypeData(visitor.root);
				}
			}
		}
		
		IType type = null;
		AnnotatedASTNode<TypeDeclaration> annotatedType = null;
		Set<AnnotatedASTNode<FieldDeclaration>> annotatedFields = null;
		Set<AnnotatedASTNode<MethodDeclaration>> annotatedMethods = null;
		
		Map<ResolvedAnnotation, AnnotatedASTNode<ASTNode>> annotationMap = new HashMap<ResolvedAnnotation, AnnotatedASTNode<ASTNode>>();
		
		private void processTypeData(ASTVisitorImpl.TypeData data) {
			type = data.type;
			annotatedType = data.annotatedType;
			annotatedFields = data.annotatedFields;
			annotatedMethods = data.annotatedMethods;

			ResolvedAnnotation[] annotations = annotatedType.getAnnotations();
			for (int i = 0; annotations != null && i < annotations.length; i++) {
				annotationMap.put(annotations[i], (AnnotatedASTNode)annotatedType);
			}
			
			if (annotatedMethods != null) {
				for (AnnotatedASTNode annotatedMethod : annotatedMethods) {
					annotations = annotatedMethod.getAnnotations();
					for (int i = 0; annotations != null && i < annotations.length; i++) {
						annotationMap.put(annotations[i], (AnnotatedASTNode)annotatedMethod);
					}
				}
			}

			if (annotatedFields != null) {
				for (AnnotatedASTNode annotatedField : annotatedFields) {
					annotations = annotatedField.getAnnotations();
					for (int i = 0; annotations != null && i < annotations.length; i++) {
						annotationMap.put(annotations[i], (AnnotatedASTNode)annotatedField);
					}
				}
			}
//			this.annotations = annotations;
		}
	}
	
	static String getResolvedType(IType type, String n) {

		String[][] rs;
		try {
			rs = type.resolveType(n);
			if(rs != null && rs.length > 0) {
				return (rs[0][0].length() == 0) ? rs[0][1] : rs[0][0] + "." + rs[0][1]; //$NON-NLS-1$
			}
		} catch (JavaModelException e) {
			// ignore
		}

		return n;
	}

}
