package org.jboss.tools.seam.ui.text.java.scanner;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.internal.core.scanner.ScannerException;
import org.jboss.tools.seam.internal.core.scanner.java.ASTVisitorImpl;
import org.jboss.tools.seam.internal.core.scanner.java.AnnotatedASTNode;
import org.jboss.tools.seam.internal.core.scanner.java.ResolvedAnnotation;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

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
				SeamGuiPlugin.getDefault().logError(e);
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
		AnnotatedASTNode<AbstractTypeDeclaration> annotatedType = null;
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
	
	public ResolvedAnnotation findAnnotationByValueOffset(int offset) {
		if (resolvedAnnotations == null)
			return null;

		for (ResolvedAnnotation a : resolvedAnnotations.keySet()) {
			if (a.getAnnotation() instanceof SingleMemberAnnotation) {
				SingleMemberAnnotation sma = (SingleMemberAnnotation)a.getAnnotation();
				Object vpd = sma.getStructuralProperty(SingleMemberAnnotation.VALUE_PROPERTY);
				if (vpd instanceof ASTNode) {
					ASTNode node = (ASTNode)vpd;
					int start = node.getStartPosition();
					int length = node.getLength();
					if (offset >= start && offset < start + length) {
						return a;
					}
				}
			} else if (a.getAnnotation() instanceof NormalAnnotation) {
				NormalAnnotation na = (NormalAnnotation)a.getAnnotation();
				Object vpd = na.getStructuralProperty(NormalAnnotation.VALUES_PROPERTY);
				if (vpd instanceof List) {
					for (Object item : (List)vpd) {
						if (item instanceof ASTNode) {
							ASTNode node = (ASTNode)item;
							if (node.getNodeType() != ASTNode.MEMBER_VALUE_PAIR) 
								continue;
							MemberValuePair mvp = (MemberValuePair)node;
							SimpleName name = mvp.getName();
							if (!"value".equals(name.getIdentifier())) {
								continue;
							}
							int start = node.getStartPosition();
							int length = node.getLength();
							if (offset >= start && offset < start + length) {
								return a;
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the annotation's value text
	 * 
	 * @param annotation
	 * @return
	 */
	public String getAnnotationValue(ResolvedAnnotation annotation) {
		if (annotation.getAnnotation() instanceof SingleMemberAnnotation) {
			SingleMemberAnnotation sma = (SingleMemberAnnotation)annotation.getAnnotation();
			Object vpd = sma.getStructuralProperty(SingleMemberAnnotation.VALUE_PROPERTY);
			if (vpd instanceof StringLiteral) {
				return ((StringLiteral)vpd).getLiteralValue();
			} 
			return vpd.toString();
		} else if (annotation.getAnnotation() instanceof NormalAnnotation) {
			NormalAnnotation na = (NormalAnnotation)annotation.getAnnotation();
			Object vpd = na.getStructuralProperty(NormalAnnotation.VALUES_PROPERTY);
			if (vpd instanceof List) {
				for (Object item : (List)vpd) {
					if (item instanceof ASTNode) {
						ASTNode node = (ASTNode)item;
						if (node.getNodeType() != ASTNode.MEMBER_VALUE_PAIR) 
							continue;
						MemberValuePair mvp = (MemberValuePair)node;
						SimpleName name = mvp.getName();
						if (!"value".equals(name.getIdentifier())) {
							continue;
						}
						return ((StringLiteral)mvp.getValue()).getLiteralValue();
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * Returns the annotation's value region
	 * 
	 * @param annotation
	 * @return
	 */
	public IRegion getAnnotationValueRegion(ResolvedAnnotation annotation) {
		if (annotation.getAnnotation() instanceof SingleMemberAnnotation) {
			SingleMemberAnnotation sma = (SingleMemberAnnotation)annotation.getAnnotation();
			Object vpd = sma.getStructuralProperty(SingleMemberAnnotation.VALUE_PROPERTY);
			if (vpd instanceof StringLiteral) {
				StringLiteral sl = (StringLiteral)vpd;
				return new Region(sl.getStartPosition() + 1, sl.getLength());
			}
			if (vpd instanceof ASTNode) {
				ASTNode astNode = (ASTNode)vpd;
				return new Region(astNode.getStartPosition(),astNode.getLength()); 
			} 
			return null;
		} else if (annotation.getAnnotation() instanceof NormalAnnotation) {
			NormalAnnotation na = (NormalAnnotation)annotation.getAnnotation();
			Object vpd = na.getStructuralProperty(NormalAnnotation.VALUES_PROPERTY);
			if (vpd instanceof List) {
				for (Object item : (List)vpd) {
					if (item instanceof ASTNode) {
						ASTNode node = (ASTNode)item;
						if (node.getNodeType() != ASTNode.MEMBER_VALUE_PAIR) 
							continue;
						MemberValuePair mvp = (MemberValuePair)node;
						SimpleName name = mvp.getName();
						if (!"value".equals(name.getIdentifier())) {
							continue;
						}
						Object sValuesObj = mvp.getStructuralProperty(MemberValuePair.VALUE_PROPERTY);
						if (sValuesObj instanceof StringLiteral) {
							StringLiteral sl = (StringLiteral)sValuesObj;
							return new Region(sl.getStartPosition() + 1, sl.getLength());
						}
						if (sValuesObj instanceof ASTNode) {
							ASTNode astNode = (ASTNode)sValuesObj;
							return new Region(astNode.getStartPosition(),astNode.getLength()); 
						} 
						return null;
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * Detects if the type of annotation equals to the selected SeamAnnotations' type
	 * 
	 * @param annotation
	 * @param typeName
	 * 
	 * @return 
	 */
	public boolean isAnnotationOfType(ResolvedAnnotation annotation, String typeName) {
		if (annotation == null || typeName == null)
			return false;
		
		return (typeName.equals(annotation.getType()));
	}

	static String getResolvedType(IType type, String n) {

		String[][] rs;
		try {
			rs = type.resolveType(n);
			if(rs != null && rs.length > 0) {
				return (rs[0][0].length() == 0) ? rs[0][1] : rs[0][0] + "." + rs[0][1]; //$NON-NLS-1$
			}
		} catch (JavaModelException e) {
			SeamGuiPlugin.getDefault().logError(e);
		}

		return n;
	}

}
