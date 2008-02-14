package org.jboss.tools.seam.internal.core.el;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.CompletionRequestor;
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

		protected MemberInfo (
			IType sourceType,
			String declaringTypeQualifiedName, String name, int modifiers) {
			setSourceType(sourceType);
			setDeclaringTypeQualifiedName(declaringTypeQualifiedName);
			setName(name);
			setModifiers(modifiers);
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

		abstract public IType getMemberType(); 

		abstract public IJavaElement getJavaElement();
	}

	public static class TypeInfo extends MemberInfo {
		IType fType;

		public TypeInfo(IType type) throws JavaModelException {
			super(type.getDeclaringType(), (type.getDeclaringType() == null ? null : type.getDeclaringType().getFullyQualifiedName()), type.getFullyQualifiedName(), type.getFlags());
			this.fType = type;
		}

		public IType getType() {
			return fType;
		}

		@Override
		public IType getMemberType() {
			return getType();
		}

		@Override
		public IJavaElement getJavaElement() {
			return getType();
		}
	}

	public static class FieldInfo extends MemberInfo {
		private String fTypeQualifiedName;
		private String[] fParametersOfReturnType;
		private IType fType;

		public FieldInfo(IType sourceType, String declaringTypeQualifiedName, String name, int modifiers, String typeQualifiedName, String[] parametersOfReturnType) {
			super(sourceType, declaringTypeQualifiedName, name, modifiers);
			setTypeQualifiedName(typeQualifiedName);
			setParametersOfReturnType(parametersOfReturnType);
		}

		public FieldInfo(IField field) throws JavaModelException {
			this (field.getDeclaringType(), (field.getDeclaringType() == null ? null : field.getDeclaringType().getFullyQualifiedName()), 
					field.getElementName(),
					field.getFlags(), 
					Signature.toString(Signature.getTypeErasure(field.getTypeSignature())),
					getQualifiedClassNamesFromSignatureArray(Signature.getTypeArguments(field.getTypeSignature())));
		}

		protected void setTypeQualifiedName(String typeQualifiedName) {
			fTypeQualifiedName = typeQualifiedName;
		}

		public String getTypeQualifiedName() {
			return fTypeQualifiedName;
		}

		public IType getType() {
			if(fType==null) {
				try {
					fType = getSourceType().getJavaProject().findType(getTypeQualifiedName());
				} catch (JavaModelException e) {
					SeamCorePlugin.getPluginLog().logError(e);
				}
			}
			return fType;
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

		public IType getMemberType() {
			return getType();
		}

		public String[] getParametersOfReturnType() {
			return fParametersOfReturnType;
		}

		protected void setParametersOfReturnType(String[] parametersOfReturnType) {
			fParametersOfReturnType = parametersOfReturnType;
		}
	}

	public static class MethodInfo extends MemberInfo {
		private String[] fParameterTypeQualifiedNames;
		private String[] fParameterNames;
		private String fReturnTypeQualifiedName;
		private String[] fParametersOfReturnType;
		private MemberInfo fParentMember;
		private String[] fParametersOfDeclaringType;
		private IType fReturnType;

		public MethodInfo(IType sourceType, String declaringTypeQualifiedName, String name,
				int modifiers, String[] parameterTypeQualifiedNames, 
				String[] parameterNames,
				String returnTypeQualifiedName,
				String[] parametersOfReturnType) {
			super(sourceType, declaringTypeQualifiedName, name, modifiers);
			setParameterTypeQualifiedNames(parameterTypeQualifiedNames);
			setParameterNames(parameterNames);
			setReturnTypeQualifiedName(returnTypeQualifiedName);
			setParametersOfReturnType(parametersOfReturnType);
		}

		public MethodInfo(IMethod method, MemberInfo parentMember) throws JavaModelException {
			this (method.getDeclaringType(), (method.getDeclaringType() == null ? null : method.getDeclaringType().getFullyQualifiedName()), 
					method.getElementName(),
					method.getFlags(),
					resolveSignatures(method.getDeclaringType(), method.getParameterTypes()),
					method.getParameterNames(),
					null,
					null);
			String returnTypeSignature = Signature.getReturnType(method.getSignature());
//			String[] signaturesOfParametersOfReturnType = Signature.getTypeArguments(returnTypeSignature);
//			String[] parametersOfReturnType = getQualifiedClassNamesFromSignatureArray(signaturesOfParametersOfReturnType);
			setReturnTypeQualifiedName(Signature.toString(returnTypeSignature));
			setParametersOfReturnType(Signature.getTypeArguments(method.getReturnType()));
			this.fParentMember = parentMember;
			this.fParametersOfDeclaringType = getTypeErasureFromSignatureArray(method.getDeclaringType().getTypeParameterSignatures());
		}

		protected void setParameterTypeQualifiedNames(String[] parameterTypeQualifiedNames) {
			fParameterTypeQualifiedNames = (parameterTypeQualifiedNames == null ?
					new String[0] : parameterTypeQualifiedNames); 
		}

		public String[] getParameterTypeQualifiedNames() {
			return fParameterTypeQualifiedNames; 
		} 

		protected void setParameterNames(String[] parameterNames) {
			fParameterNames = (parameterNames == null ?
					new String[0] : parameterNames); 
		}

		public String[] getParameterNames() {
			return fParameterNames; 
		}

		protected void setReturnTypeQualifiedName(String returnTypeQualifiedName) {
			fReturnTypeQualifiedName = returnTypeQualifiedName; 
		}

		public String getReturnTypeQualifiedName() {
			return fReturnTypeQualifiedName; 
		}

		public int getNumberOfParameters() {
			return (getParameterNames() == null ? 0 : getParameterNames().length); 
		}

		public IType getReturnType() {
			if(fReturnType==null) {
				try {
					fReturnType = getSourceType().getJavaProject().findType(getReturnTypeQualifiedName());
				} catch (JavaModelException e) {
					SeamCorePlugin.getPluginLog().logError(e);
				}
			}
			return fReturnType;
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
		public IType getMemberType() {
			return getReturnType();
		}

		public String[] getParametersOfReturnType() {
			return fParametersOfReturnType;
		}

		protected void setParametersOfReturnType(String[] parametersOfReturnType) {
			fParametersOfReturnType = parametersOfReturnType;
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

	CompletionRequestor fRequestor = new CompletionRequestor() {

		@Override
		public void accept(CompletionProposal proposal) {
			switch(proposal.getKind()) {
				case CompletionProposal.FIELD_REF:
					{
						char[] declarationSignature = proposal.getDeclarationSignature();
						String declarationType = String.valueOf(Signature.toCharArray(declarationSignature));

						char[] fullSignature = proposal.getSignature();
						String type = String.valueOf(Signature.toCharArray(Signature.getTypeErasure(fullSignature)));
						char[][] signaturesOfParametersOfType = Signature.getTypeArguments(fullSignature);
						String[] parametersOfType = getQualifiedClassNameFromCharArray(signaturesOfParametersOfType);

						FieldInfo info = new FieldInfo(TypeInfoCollector.this.fType,
							declarationType,
							String.valueOf(proposal.getName()), proposal.getFlags(), 
							type,
							parametersOfType);
						fFields.add(info);
					}
					break;
				case CompletionProposal.METHOD_REF:
					{
						String[] parameterNames = convertToStringArray(proposal.findParameterNames(null));

						char[] declarationSignature = proposal.getDeclarationSignature();
						String declarationType = String.valueOf(Signature.toCharArray(Signature.getTypeErasure(declarationSignature)));

						char[] signature = proposal.getSignature();
						char[][] parametesSignatures = Signature.getParameterTypes(signature);
						String[] parametersTypes = getQualifiedClassNameFromCharArray(parametesSignatures);

						char[] fullReturnTypeSignature = Signature.getReturnType(signature);
						String returnType = String.valueOf(Signature.toCharArray(Signature.getTypeErasure(fullReturnTypeSignature)));
						char[][] signaturesOfParametersOfReturnType = Signature.getTypeArguments(fullReturnTypeSignature);
						String[] parametersOfReturnType = getQualifiedClassNameFromCharArray(signaturesOfParametersOfReturnType);

						MethodInfo info = new MethodInfo(TypeInfoCollector.this.fType,
							declarationType,
							String.valueOf(proposal.getName()), proposal.getFlags(), 
							parametersTypes,
							parameterNames,
							returnType,
							parametersOfReturnType);
						fMethods.add(info);
					}
					break;
				case CompletionProposal.KEYWORD:
				case CompletionProposal.PACKAGE_REF:
				case CompletionProposal.TYPE_REF:
				case CompletionProposal.METHOD_DECLARATION:
				case CompletionProposal.ANONYMOUS_CLASS_DECLARATION:
				case CompletionProposal.LABEL_REF :
				case CompletionProposal.LOCAL_VARIABLE_REF:
				case CompletionProposal.VARIABLE_DECLARATION:
				case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
					break;
			}
		}
	};

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
			fType.codeComplete("".toCharArray(), -1, 0, new char[0][0], new char[0][0], new int[0], false, fRequestor);
			IType binType = fType;
			while (binType != null) {
				IMethod[] binMethods = binType.getMethods();
				for (int i = 0; binMethods != null && i < binMethods.length; i++) {
					if (binMethods[i].isConstructor()) continue;
					MethodInfo[] infos = findMethodInfos(binMethods[i]);
					if (infos == null || infos.length == 0) {
						fMethods.add(new MethodInfo(binMethods[i], fMember));
					}
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

	public static boolean isMapOrNotParameterizedCollection(IType type) {
		try {
			String name = type.getFullyQualifiedParameterizedName();
			return isInstanceofType(type, "java.util.Map") || (isInstanceofType(type, "java.util.Collection") && name.indexOf('<')==-1);
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

	void addInfoForDataModelObject() {
		fMethods.add(new MethodInfo(fType,
				fType.getFullyQualifiedName(),
				"size", Modifier.PUBLIC, 
				new String[0],
				new String[0],
				"int",
				new String[0]));
		fMethods.add(new MethodInfo(fType,
				fType.getFullyQualifiedName(),
				"isEmpty", Modifier.PUBLIC, 
				new String[0],
				new String[0],
				"boolean",
				new String[0]));
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

	public static MemberInfo createMemberInfo(IMember member) {
		try {
			if (member instanceof IType)
				return new TypeInfo((IType)member);
			else if (member instanceof IField)
				return new FieldInfo((IField)member);
			else if (member instanceof IMethod)
				return new MethodInfo((IMethod)member, null);
		} catch (JavaModelException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}

		return null;
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
}