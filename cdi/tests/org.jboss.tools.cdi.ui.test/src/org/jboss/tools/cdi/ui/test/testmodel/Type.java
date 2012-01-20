/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.test.testmodel;

import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.IWorkingCopy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

public class Type extends CDIElement implements IType {
	private String qualifiedName;
	private JavaProject project;
	
	public Type(String qualifiedName){
		this.qualifiedName = qualifiedName;
		project = new JavaProject();
	}

	@Override
	public String[] getCategories() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassFile getClassFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICompilationUnit getCompilationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType getDeclaringType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFlags() throws JavaModelException {
		return ClassFileConstants.AccPublic;
	}

	@Override
	public ISourceRange getJavadocRange() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOccurrenceCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ITypeRoot getTypeRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType getType(String name, int occurrenceCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBinary() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IJavaElement getAncestor(int ancestorType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttachedJavadoc(IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getCorrespondingResource() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getElementType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHandleIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaModel getJavaModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaProject getJavaProject() {
		return project;
	}

	@Override
	public IOpenable getOpenable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaElement getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaElement getPrimaryElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getUnderlyingResource() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStructureKnown() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSource() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISourceRange getSourceRange() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISourceRange getNameRange() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void copy(IJavaElement container, IJavaElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(boolean force, IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(IJavaElement container, IJavaElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String name, boolean replace, IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public IJavaElement[] getChildren() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IAnnotation getAnnotation(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAnnotation[] getAnnotations() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			ICompletionRequestor requestor) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			ICompletionRequestor requestor, WorkingCopyOwner owner)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor, IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor, WorkingCopyOwner owner)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor, WorkingCopyOwner owner,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public IField createField(String contents, IJavaElement sibling,
			boolean force, IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInitializer createInitializer(String contents,
			IJavaElement sibling, IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMethod createMethod(String contents, IJavaElement sibling,
			boolean force, IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType createType(String contents, IJavaElement sibling,
			boolean force, IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMethod[] findMethods(IMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaElement[] getChildrenForCategory(String category)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementName() {
		return getShortName(qualifiedName);
	}

	@Override
	public IField getField(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IField[] getFields() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		return qualifiedName;
	}

	@Override
	public String getFullyQualifiedName(char enclosingTypeSeparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedParameterizedName()
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInitializer getInitializer(int occurrenceCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInitializer[] getInitializers() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMethod getMethod(String name, String[] parameterTypeSignatures) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMethod[] getMethods() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPackageFragment getPackageFragment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSuperclassName() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSuperclassTypeSignature() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSuperInterfaceTypeSignatures() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSuperInterfaceNames() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getTypeParameterSignatures() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeParameter[] getTypeParameters() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType getType(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeParameter getTypeParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeQualifiedName(char enclosingTypeSeparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType[] getTypes() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAnonymous() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClass() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnum() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInterface() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAnnotation() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLocal() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMember() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ITypeHierarchy loadTypeHierachy(InputStream input,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newSupertypeHierarchy(IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newSupertypeHierarchy(
			ICompilationUnit[] workingCopies, IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newSupertypeHierarchy(IWorkingCopy[] workingCopies,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newSupertypeHierarchy(WorkingCopyOwner owner,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(IJavaProject project,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(IJavaProject project,
			WorkingCopyOwner owner, IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(ICompilationUnit[] workingCopies,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(IWorkingCopy[] workingCopies,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(WorkingCopyOwner owner,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[][] resolveType(String typeName) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[][] resolveType(String typeName, WorkingCopyOwner owner)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

}
