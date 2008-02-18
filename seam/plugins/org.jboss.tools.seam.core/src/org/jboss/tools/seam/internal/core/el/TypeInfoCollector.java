/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.internal.core.el;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * This class helps to collect information of java elements used in Seam EL.
 * @author Viktor Rubezhny, Alexey Kazakov
 */
public class TypeInfoCollector {
	IType fType;
	MemberInfo fMember;
	List<MethodInfo> fMethods;
	List<FieldInfo> fFields;

	public abstract static class MemberInfo {
		private String fDeclaringTypeQualifiedName; 
		private String fName;
		private int fModifiers;
		private IType fSourceType;
		private MemberInfo fParentMember;
		private String[] fParametersOfType;
		private String[] fParametersNamesOfDeclaringType;
		private Map<String, String> fParametersOfDeclaringType;
		private IType fMemberType;
		private String fTypeName;
		private boolean fTypeIsArray;
		private String fTypeNameOfArray;
		private String fQualifiedTypeNameOfErrayElement;
		private boolean isDataModel;

		protected MemberInfo (
			IType sourceType,
			String declaringTypeQualifiedName, String name, int modifiers, MemberInfo parentMember, boolean dataModel) {
			setSourceType(sourceType);
			setDeclaringTypeQualifiedName(declaringTypeQualifiedName);
			setName(name);
			setModifiers(modifiers);
			setParentMember(parentMember);
			setDataModel(dataModel);
		}

		protected void initializeParametersOfDeclaringType() {
			if(fParametersOfDeclaringType==null && fParametersNamesOfDeclaringType!=null && fParametersNamesOfDeclaringType.length>0 && getParentMember()!=null) {
				fParametersOfDeclaringType = new HashMap<String, String>();
				// Set parameters from parent to return type.
				String type = getParentMember().getParameterOfDeclaringType(getTypeName());
				if(type!=null) {
					fParametersOfDeclaringType.put(getTypeName(), type);
				}
				for(int i=0; i<fParametersNamesOfDeclaringType.length; i++) {
					String paramName = getParameterNameFromType(fParametersNamesOfDeclaringType[i]);
					String paramType = getParentMember().getParameterOfType(i);
					if(paramType!=null) {
						String resolvedParamType = getParentMember().getParameterOfDeclaringType(paramType);
						if(resolvedParamType!=null) {
							paramType = resolvedParamType;
						}
						String fullQualifiedParamType = getParentMember().resolveTypeNameToFullQualifiedName(paramType);
						fParametersOfDeclaringType.put(paramName, fullQualifiedParamType);
					}
				}
			}
		}

		protected void setTypeName(String typeName) {
			fTypeName = typeName;
		}

		public String getTypeName() {
			return fTypeName;
		}

		public void setSourceType(IType sourceType) {
			fSourceType = sourceType;
		}

		public IType getSourceType() {
			return fSourceType;
		}

		protected void setName (String name) {
			this.fName = name;
		}

		public String getName() {
			return fName;
		}

		protected void setDeclaringTypeQualifiedName (String declaringTypeQualifiedName) {
			this.fDeclaringTypeQualifiedName = declaringTypeQualifiedName;
		}

		public String getDeclaringTypeQualifiedName() {
			return fDeclaringTypeQualifiedName;
		}

		protected void setModifiers (int modifiers) {
			this.fModifiers = modifiers;
		}

		public int getModifiers() {
			return fModifiers;
		}

		public boolean isPublic() {
			return Modifier.isPublic(fModifiers);
		}

		public boolean isStatic() {
			return Modifier.isStatic(fModifiers);
		}

		public boolean isJavaLangObject() {
			return "java.lang.Object".equals(getDeclaringTypeQualifiedName());
		}

		public MemberInfo getParentMember() {
			return fParentMember;
		}

		void setParentMember(MemberInfo parentMember) {
			fParentMember = parentMember;
		}

		public String resolveTypeNameToFullQualifiedName(String typeName) {
			if(fSourceType==null) {
				return typeName;
			}
			return EclipseJavaUtil.resolveType(fSourceType, typeName);
		}

		public String[] getParametersOfType() {
			return fParametersOfType;
		}

		public String getParameterOfType(int index) {
			if(fParametersOfType!=null && fParametersOfType.length>index) {
				return fParametersOfType[index];
			}
			return null;
		}

		void setParametersOfType(String[] parametersOfType) {
			fParametersOfType = parametersOfType;
		}

		public String[] getParametersNamesOfDeclaringType() {
			return fParametersNamesOfDeclaringType;
		}

		void setParametersNamesOfDeclaringType(
				String[] parametersNamesOfDeclaringType) {
			fParametersNamesOfDeclaringType = parametersNamesOfDeclaringType;
		}

		protected String getParameterOfDeclaringType(String parameterName) {
			if(fParametersOfDeclaringType!=null) {
				return fParametersOfDeclaringType.get(parameterName);
			}
			return null;
		}

		public IType getMemberType() {
			if(fMemberType==null) {
				initializeParametersOfDeclaringType();
				fMemberType = getMemberTypeInner();
				if(fMemberType==null && fParametersOfDeclaringType!=null) {
					// Maybe type name is parameter.
					String typeName = fParametersOfDeclaringType.get(fTypeName);
					if(typeName!=null) {
						try {
							fMemberType = getSourceType().getJavaProject().findType(typeName);
						} catch (JavaModelException e) {
							SeamCorePlugin.getPluginLog().logError(e);
						}
					}
				}
			}
			return fMemberType;
		}

		public boolean isTypeArray() {
			return fTypeIsArray;
		}

		void setTypeArray(boolean typeIsArray) {
			fTypeIsArray = typeIsArray;
		}

		public String getTypeNameOfArray() {
			return fTypeNameOfArray;
		}

		void setTypeNameOfArray(String typeNameOfArray) {
			fTypeNameOfArray = typeNameOfArray;
		}

		public String getQualifiedTypeNameOfArrayElement() {
			if(fQualifiedTypeNameOfErrayElement == null) {
				fQualifiedTypeNameOfErrayElement = EclipseJavaUtil.resolveType(getSourceType(), getTypeNameOfArray());
			}
			return fQualifiedTypeNameOfErrayElement;
		}

		public boolean isDataModel() {
			return isDataModel;
		}

		void setDataModel(boolean isDataModel) {
			this.isDataModel = isDataModel;
		}

		abstract protected IType getMemberTypeInner();

		abstract public IJavaElement getJavaElement();
	}

	public static class TypeInfo extends MemberInfo {
		private IType fType;

		public TypeInfo(IType type, MemberInfo parentMember, boolean dataModel) throws JavaModelException {
			super(type.getDeclaringType(), (type.getDeclaringType() == null ? null : type.getDeclaringType().getFullyQualifiedName()), type.getFullyQualifiedName(), type.getFlags(), parentMember, dataModel);
			this.fType = type;
		}

		public IType getType() {
			return fType;
		}

		@Override
		public IType getMemberTypeInner() {
			return getType();
		}

		@Override
		public IJavaElement getJavaElement() {
			return getType();
		}
	}

	public static class FieldInfo extends MemberInfo {
		private String fQualifiedTypeName;

		public FieldInfo(IType sourceType, String declaringTypeQualifiedName, String name, int modifiers, String typeQualifiedName, String[] parametersOfType, MemberInfo parentMember, boolean dataModel) {
			super(sourceType, declaringTypeQualifiedName, name, modifiers, parentMember, dataModel);
			setTypeName(typeQualifiedName);
			setParametersOfType(parametersOfType);
		}

		public FieldInfo(IField field, MemberInfo parentMember, boolean dataModel) throws JavaModelException {
			super(field.getDeclaringType(),
					(field.getDeclaringType() == null ? null : field.getDeclaringType().getFullyQualifiedName()),
					field.getElementName(),
					field.getFlags(),
					parentMember,
					dataModel);

			String fullTypeSignature = field.getTypeSignature();
			String typeErasureSignature = Signature.getTypeErasure(fullTypeSignature);
			String typeOfArray = String.valueOf(Signature.toString(Signature.getElementType(typeErasureSignature)));
			String type = String.valueOf(Signature.toString(typeErasureSignature));
			if(!type.equals(typeOfArray)) {
				// this is an array
				setTypeArray(true);
				setTypeNameOfArray(typeOfArray);
			}
			String[] signaturesOfParametersOfType = Signature.getTypeArguments(fullTypeSignature);
			String[] parametersOfType = getQualifiedClassNamesFromSignatureArray(signaturesOfParametersOfType);

			setTypeName(type);
			setParametersOfType(parametersOfType);
		
			setParametersNamesOfDeclaringType(getTypeErasureFromSignatureArray(field.getDeclaringType().getTypeParameterSignatures()));
		}

		public String getQualifiedTypeName() {
			if(fQualifiedTypeName == null) {
				fQualifiedTypeName = EclipseJavaUtil.resolveType(getSourceType(), getTypeName());
			}
			return fQualifiedTypeName;
		}

		public IType getType() {
			try {
				if(isDataModel() && isTypeArray()) {
					return getSourceType().getJavaProject().findType(getQualifiedTypeNameOfArrayElement());
				}
				return getSourceType().getJavaProject().findType(getQualifiedTypeName());
			} catch (JavaModelException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			return null;
		}

		public IJavaElement getJavaElement () {
			try {
				IType declType = getSourceType().getJavaProject().findType(getDeclaringTypeQualifiedName());
				return (declType == null ? null : declType.getField(getName()));
			} catch (JavaModelException e) {
				SeamCorePlugin.getPluginLog().logError(e);
				return null;
			}
		}

		public IType getMemberTypeInner() {
			return getType();
		}
	}

	public static class MethodInfo extends MemberInfo {
		private String[] fParameterTypeNames;
		private String[] fParameterTypeQualifiedNames;
		private String[] fParameterNames;
		private String fReturnTypeQualifiedName;

		public MethodInfo(IType sourceType, String declaringTypeQualifiedName, String name,
				int modifiers, String[] parameterTypeQualifiedNames, 
				String[] parameterNames,
				String returnTypeQualifiedName,
				String[] parametersOfReturnType,
				MemberInfo parentMember,
				boolean dataModel) {
			super(sourceType, declaringTypeQualifiedName, name, modifiers, parentMember, dataModel);
			setParameterTypeNames(parameterTypeQualifiedNames);
			setParameterNames(parameterNames);
			setTypeName(returnTypeQualifiedName);
			setParametersOfType(parametersOfReturnType);
		}

		public MethodInfo(IMethod method, MemberInfo parentMember, boolean dataModel) throws JavaModelException {
			super(method.getDeclaringType(),
					(method.getDeclaringType() == null ? null : method.getDeclaringType().getFullyQualifiedName()),
					method.getElementName(),
					method.getFlags(),
					parentMember,
					dataModel);

			setParameterNames(method.getParameterNames());
			setParameterTypeNames(resolveSignatures(method.getDeclaringType(), method.getParameterTypes()));

			String fullReturnTypeSignature = method.getReturnType();
			String returnTypeErasureSignature = Signature.getTypeErasure(fullReturnTypeSignature);
			String returnTypeOfArray = String.valueOf(Signature.toString(Signature.getElementType(returnTypeErasureSignature)));
			String returnType = String.valueOf(Signature.toString(returnTypeErasureSignature));
			String[] signaturesOfParametersOfReturnType = Signature.getTypeArguments(fullReturnTypeSignature);
			String[] parametersOfReturnType = getQualifiedClassNamesFromSignatureArray(signaturesOfParametersOfReturnType);

			if(!returnType.equals(returnTypeOfArray)) {
				// this is an array
				setTypeArray(true);
				setTypeNameOfArray(returnTypeOfArray);
			}

			setTypeName(returnType);
			setParametersOfType(parametersOfReturnType);
			setParametersNamesOfDeclaringType(getTypeErasureFromSignatureArray(method.getDeclaringType().getTypeParameterSignatures()));
		}

		protected void setParameterTypeNames(String[] parameterTypeNames) {
			fParameterTypeNames = (parameterTypeNames == null ?
					new String[0] : parameterTypeNames); 
		}

		public String[] getParameterTypeQualifiedNames() {
			if(fParameterTypeQualifiedNames==null) {
				fParameterTypeQualifiedNames = new String[fParameterTypeNames.length];
				for (int i = 0; i < fParameterTypeQualifiedNames.length; i++) {
					fParameterTypeQualifiedNames[i] = EclipseJavaUtil.resolveType(getSourceType(), fParameterTypeNames[i]);
				}
			}
			return fParameterTypeQualifiedNames; 
		} 

		public String[] getParameterTypeNames() {
			return fParameterTypeNames;
		} 

		protected void setParameterNames(String[] parameterNames) {
			fParameterNames = (parameterNames == null ?
					new String[0] : parameterNames); 
		}

		public String[] getParameterNames() {
			return fParameterNames; 
		}

		public String getReturnTypeName() {
			return getTypeName(); 
		}

		public String getReturnTypeQualifiedName() {
			if(fReturnTypeQualifiedName==null) {
				fReturnTypeQualifiedName = EclipseJavaUtil.resolveType(getSourceType(), getReturnTypeName());
			}
			return fReturnTypeQualifiedName;
		}

		public int getNumberOfParameters() {
			return (getParameterNames() == null ? 0 : getParameterNames().length); 
		}

		public IType getReturnType() {
			try {
				if(isDataModel() && isTypeArray()) {
					return getSourceType().getJavaProject().findType(getQualifiedTypeNameOfArrayElement());
				}
				return getSourceType().getJavaProject().findType(getReturnTypeQualifiedName());
			} catch (JavaModelException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			return null;
		}

		public boolean isConstructor () {
			return getDeclaringTypeQualifiedName().equals(getName());
		}

		public boolean isGetter() {
			return (getName().startsWith("get") && !getName().equals("get")) || getName().startsWith("is");
		}

		public boolean isSetter() {
			return (getName().startsWith("set") && !getName().equals("set"));
		}

		@Override
		public IType getMemberTypeInner() {
			return getReturnType();
		}

		public String[] getParametersOfReturnType() {
			return getParametersOfType();
		}

		@Override
		public IJavaElement getJavaElement () {
			try {
				IType declType = getSourceType().getJavaProject().findType(getDeclaringTypeQualifiedName());

				IMethod[] allMethods = declType.getMethods();

				// filter methods by name
				List<IMethod> methods = new ArrayList<IMethod>();
				for (int i = 0; allMethods != null && i < allMethods.length; i++) {
					if (allMethods[i].getElementName().equals(getName())) {
						methods.add(allMethods[i]);
					}
				}
				if (methods.isEmpty())
					return null;
				if (methods.size() == 1)
					return methods.get(0);

				// filter methods by number of parameters
				List<IMethod> filteredMethods = new ArrayList<IMethod>();
				for (IMethod method : methods) {
					if (method.getNumberOfParameters() == getNumberOfParameters())
						filteredMethods.add(method);
				}
				if (filteredMethods.isEmpty())
					return null;
				if (filteredMethods.size() == 1)
					return filteredMethods.get(0);

				methods = filteredMethods;

				// filter methods by parameter types
				for(IMethod method : methods) {
					String[] methodParameterTypes = 
						resolveSignatures(method.getDeclaringType(), 
								method.getParameterTypes());
					String[] parameterTypes = getParameterTypeQualifiedNames();

					boolean equal = true;
					for (int i = 0; parameterTypes != null && i < parameterTypes.length; i++) {
						// simple types must be equal, but complex types may not 
						if (!parameterTypes[i].equals(methodParameterTypes[i])) {
							// sure - it's Complex Type
							if (! (parameterTypes[i].indexOf('.') != -1) 
									&& (methodParameterTypes[i].indexOf('.') == -1)) {
								equal = false;
								break;
							}
						}
					}
					if (equal)
						return method;
				}
				return null;
			} catch (JavaModelException e) {
				SeamCorePlugin.getPluginLog().logError(e);
				return null;
			}
		}
	}

	public TypeInfoCollector(MemberInfo member) {
		this.fMember = member;
		this.fType = member.getMemberType();
	}

	public IType getType() {
		return this.fType;
	}

	public void collectInfo() {
		if (fMethods == null) {
			fMethods = new ArrayList<MethodInfo>();
		} else {
			fMethods.clear();
		}

		if (fFields == null) {
			fFields = new ArrayList<FieldInfo>();
		} else {
			fFields.clear();
		}

		if (fType == null) 
			return;
		try {
			IType binType = fType;
			while (binType != null) {
				IMethod[] binMethods = binType.getMethods();
				for (int i = 0; binMethods != null && i < binMethods.length; i++) {
					if (binMethods[i].isConstructor()) continue;
					fMethods.add(new MethodInfo(binMethods[i], fMember, false));
				}
				binType = getSuperclass(binType);
			}

			// !!!!!!!
			// This inserts here methods "public int size()" and "public boolean isEmpty()" for javax.faces.model.DataModel 
			// as requested by Gavin in JBIDE-1256
			// !!!!!!! 
			if(isDataModelObject(fType)) {
				addInfoForDataModelObject();				
			}
			// This inserts here methods "public int getRowCount()" for @DataModel variables.
			if(fMember.isDataModel) {
				addInfoForDataModelVariable();
			}
		} catch (JavaModelException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	boolean isDataModelObject(IType type) throws JavaModelException {
		return isInstanceofType(type, "javax.faces.model.DataModel");
	}

	public static boolean isResourceBundle(IType type) {
		try {
			return isInstanceofType(type, "java.util.ResourceBundle");
		} catch (JavaModelException e) {
			return false;
		}
	}

	public static boolean isNotParameterizedCollection(TypeInfoCollector.MemberInfo mbr) {
		try {
			if(mbr.getParametersOfType()!=null && mbr.getParametersOfType().length>0) {
				return false;
			}
			IType type = mbr.getMemberType();
			if(type!=null) {
				return isInstanceofType(type, "java.util.Map") || isInstanceofType(type, "java.util.Collection");
			}
			return false;
		} catch (JavaModelException e) {
			return false;
		}
	}

	public static boolean isInstanceofType(IType type, String qualifiedTypeName) throws JavaModelException {
		if (qualifiedTypeName == null || type == null) return false;
		boolean isInstanceofType = qualifiedTypeName.equals(type.getFullyQualifiedName());
		if (!isInstanceofType) {
			ITypeHierarchy typeHierarchy = type.newSupertypeHierarchy(new NullProgressMonitor());
			IType[] superTypes = typeHierarchy == null ? null : typeHierarchy.getAllSupertypes(type);
			for (int i = 0; !isInstanceofType && superTypes != null && i < superTypes.length; i++) {
				if (qualifiedTypeName.equals(superTypes[i].getFullyQualifiedName())) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	void addInfoForDataModelVariable() {
		fMethods.add(new MethodInfo(fType,
				fType.getFullyQualifiedName(),
				"getRowCount", Modifier.PUBLIC, 
				new String[0],
				new String[0],
				"int",
				new String[0],
				fMember,
				false));
	}

	void addInfoForDataModelObject() {
		fMethods.add(new MethodInfo(fType,
				fType.getFullyQualifiedName(),
				"size", Modifier.PUBLIC, 
				new String[0],
				new String[0],
				"int",
				new String[0],
				fMember,
				false));
		fMethods.add(new MethodInfo(fType,
				fType.getFullyQualifiedName(),
				"isEmpty", Modifier.PUBLIC, 
				new String[0],
				new String[0],
				"boolean",
				new String[0],
				fMember,
				false));
	}

	private static IType getSuperclass(IType type) throws JavaModelException {
		String superclassName = type.getSuperclassName();
		if(superclassName!=null) {
			String fullySuperclassName = EclipseJavaUtil.resolveType(type, superclassName);
			if(fullySuperclassName!=null&&!fullySuperclassName.equals("java.lang.Object")) { //$NON-NLS-1$
				if(fullySuperclassName.equals(type.getFullyQualifiedName())) {
					//FIX JBIDE-1642
					return null;
				}
				IType superType = type.getJavaProject().findType(fullySuperclassName);
				return superType;
			}
		}
		return null;
	}

	public MethodInfo[] findMethodInfos(IMethod iMethod) {
		List<MethodInfo> methods = new ArrayList<MethodInfo>();

		// filter methods by name
		for (MethodInfo info : fMethods) {
			if (info.getName().equals(iMethod.getElementName())) {
				methods.add(info);
			}
		}
		if (methods.isEmpty())
			return new MethodInfo[0];

		EclipseJavaUtil.getMemberTypeAsString(iMethod);

		if (methods.size() == 1)
			return methods.toArray(new MethodInfo[0]);

		// filter methods by number of parameters
		List<MethodInfo> filteredMethods = new ArrayList<MethodInfo>();
		for (MethodInfo method : methods) {
			if (method.getNumberOfParameters() == iMethod.getNumberOfParameters())
				filteredMethods.add(method);
		}
		if (filteredMethods.isEmpty())
			return new MethodInfo[0];
		if (filteredMethods.size() == 1)
			return filteredMethods.toArray(new MethodInfo[0]);

		methods = filteredMethods;

		// filter methods by parameter types
		filteredMethods = new ArrayList<MethodInfo>(); 
		for(MethodInfo method : methods) {
			String[] methodParameterTypes = 
				resolveSignatures(iMethod.getDeclaringType(), 
						iMethod.getParameterTypes());
			String[] parameterTypes = method.getParameterTypeQualifiedNames();

			boolean equal = true;
			for (int i = 0; equal && parameterTypes != null && i < parameterTypes.length; i++) {
				// simple types must be equal, but complex types may not 
				if (!parameterTypes[i].equals(methodParameterTypes[i])) {
					// sure - it's Complex Type
					if ((parameterTypes[i].indexOf('.') != -1) 
							&& (methodParameterTypes[i].indexOf('.') == -1)) {
						equal = false;
					}
				}
			}
			if (equal) {
				filteredMethods.add(method);
			}
		}
		return filteredMethods.toArray(new MethodInfo[0]);
	}

	/**
	 * Returns the methods for the type specified  
	 * 
	 * @return
	 */
	public List<MemberInfo> getMethods() {
		List<MemberInfo> methods = new ArrayList<MemberInfo>();
		for (MethodInfo info : fMethods) {
			if (info.isPublic() && !info.isConstructor() 
					&& !info.isStatic() && !info.isJavaLangObject()
					&& !info.isGetter() && !info.isSetter())
				methods.add(info);
		}
		return methods;
	}

	/**
	 * Returns the method presentation strings for the type specified  
	 * 
	 * @param type
	 * @return
	 */
	public Set<String> getMethodPresentations() {
		Set<String> methods = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		List<MemberInfo> mthds = getMethods();
		for (MemberInfo info : mthds) {
			if (!(info instanceof MethodInfo))
				continue;

			MethodInfo method = (MethodInfo)info;

			StringBuffer name = new StringBuffer(method.getName());

			// Add method as 'foo'
			methods.add(name.toString());

			// Add method as 'foo(param1,param2)'
			name.append('(');
			String[] mParams = method.getParameterNames();
			for (int j = 0; mParams != null && j < mParams.length; j++) {
				if (j > 0) name.append(", "); //$NON-NLS-1$
				name.append(mParams[j]);
			}
			name.append(')');

			methods.add(name.toString());
		}
		return methods;
	}

	/**
	 * Returns the properties for the type specified  
	 * 
	 * @return
	 */
	public List<MemberInfo> getProperties() {
		List<MemberInfo> properties = new ArrayList<MemberInfo>();
		for (MethodInfo info : fMethods) {
			if (info.isPublic() && !info.isConstructor() 
					&& !info.isStatic() && !info.isJavaLangObject()
					&& (info.isGetter() || info.isSetter()))
				properties.add(info);
		}

		/*
		 * The following code was excluded due to the following issue: 
		 * 
		 * http://jira.jboss.com/jira/browse/JBIDE-1203#action_12385823
		 * 
		 * 
		for (FieldInfo info : fFields) {
			if (info.isPublic() 
					&& !info.isStatic() && !info.isJavaLangObject())
				properties.add(info);
		}
		*/

		return properties;
	}

	/**
	 * Returns the property presentation strings for the type specified  
	 * 
	 * @return
	 */
	public Set<String> getPropertyPresentations() {
		return getPropertyPresentations(null);
	}

	/**
	 * Returns the property presentation strings for the type specified  
	 * 
	 * @param unpairedGettersOrSetters - map of unpaired getters or setters of type's properties. 'key' is property name.
	 * @return
	 */
	public Set<String> getPropertyPresentations(Map<String, MethodInfo> unpairedGettersOrSetters) {
		Set<String> properties = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER); 
		List<MemberInfo> props = getProperties(); 
		HashMap<String, MethodInfo> getters = new HashMap<String, MethodInfo>();
		HashMap<String, MethodInfo> setters = new HashMap<String, MethodInfo>();
		for (MemberInfo info : props) {
			if (info instanceof MethodInfo) {
				MethodInfo m = (MethodInfo)info;

				if (m.isGetter() || m.isSetter()) {
					StringBuffer name = new StringBuffer(m.getName());
					if(m.getName().startsWith("i")) { //$NON-NLS-1$
						name.delete(0, 2);
					} else {
						name.delete(0, 3);
					}
					name.setCharAt(0, Character.toLowerCase(name.charAt(0)));
					String propertyName = name.toString();
					if(!properties.contains(propertyName)) {
						properties.add(propertyName);
					}
					if(unpairedGettersOrSetters!=null) {
						MethodInfo previousGetter = getters.get(propertyName);
						MethodInfo previousSetter = setters.get(propertyName);
						if((previousGetter!=null && m.isSetter())||(previousSetter!=null && m.isGetter())) {
							// We have both Getter and Setter
							unpairedGettersOrSetters.remove(propertyName);
						} else if(m.isSetter()) {
							setters.put(propertyName, m);
							unpairedGettersOrSetters.put(propertyName, m);
						} else if(m.isGetter()) {
							getters.put(propertyName, m);
							unpairedGettersOrSetters.put(propertyName, m);
						}
					}
				}
			} else {
				properties.add(info.getName());
			}
		}	
		return properties;
	}

	public static MemberInfo createMemberInfo(IMember member, boolean dataModel) {
		try {
			if (member instanceof IType)
				return new TypeInfo((IType)member, null, dataModel);
			else if (member instanceof IField)
				return new FieldInfo((IField)member, null, dataModel);
			else if (member instanceof IMethod)
				return new MethodInfo((IMethod)member, null, dataModel);
		} catch (JavaModelException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}

		return null;
	}

	public static MemberInfo createMemberInfo(IMember member) {
		return createMemberInfo(member, false);
	}

	static String[] resolveSignatures (IType type, String[] signatures) {
		if (signatures == null || signatures.length == 0) 
			return new String[0];

		String[] resolvedSignatures = new String[signatures.length];
		for (int i = 0; i < signatures.length; i++) {
			resolvedSignatures[i] = EclipseJavaUtil.resolveTypeAsString(type, signatures[i]);
		}
		return resolvedSignatures;
	}

	static String[] convertToStringArray(char[][] names) {
		if (names == null || names.length == 0) 
			return new String[0];
		String[] sNames = new String[names.length];
		for (int i = 0; i < sNames.length; i++) {
			sNames[i] = String.valueOf(names[i]);
		}
		return sNames;	
	}

	static String[] getTypeErasureFromSignatureArray(String[] signatures) {
		if (signatures == null || signatures.length == 0) 
			return new String[0];
		String[] result = new String[signatures.length];
		for (int i = 0; i < signatures.length; i++) {
			result[i] = Signature.getTypeErasure(signatures[i]);
		}
		return result;
	}

	static String[] getQualifiedClassNamesFromSignatureArray(String[] signatureTypes) {
		if (signatureTypes == null || signatureTypes.length == 0) 
			return new String[0];
		String[] qualifiedTypes = new String[signatureTypes.length];
		for (int i = 0; i < signatureTypes.length; i++) {
			qualifiedTypes[i] = Signature.toString(signatureTypes[i]);
		}
		return qualifiedTypes;
	}

	static String[] getQualifiedClassNameFromCharArray(char[][] signatureTypes) {
		if (signatureTypes == null || signatureTypes.length == 0) 
			return new String[0];
		String[] qualifiedTypes = new String[signatureTypes.length];
		for (int i = 0; i < signatureTypes.length; i++) {
			qualifiedTypes[i] = String.valueOf(Signature.toCharArray(signatureTypes[i]));
		}
		return qualifiedTypes;
	}

	static String getParameterNameFromType(String typeSignatures) {
		if(typeSignatures==null) {
			return null;
		}
		return Signature.getTypeVariable(typeSignatures);
	}

	static String[] getParameterNamesFromTypeArray(String[] typeSignatures) {
		if(typeSignatures==null || typeSignatures.length==0) {
			return new String[0];
		}
		String[] names = new String[typeSignatures.length];
		for (int i = 0; i < typeSignatures.length; i++) {
			names[i] = getParameterNameFromType(typeSignatures[i]);
			
		}
		return names;
	}
}