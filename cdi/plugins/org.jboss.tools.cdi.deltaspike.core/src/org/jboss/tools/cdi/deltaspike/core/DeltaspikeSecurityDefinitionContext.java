package org.jboss.tools.cdi.deltaspike.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

public class DeltaspikeSecurityDefinitionContext extends AbstractDefinitionContextExtension implements DeltaspikeConstants {

	Map<String, DeltaspikeSecurityBindingConfiguration> securityBindingConfigurations = new HashMap<String, DeltaspikeSecurityBindingConfiguration>();
	DeltaspikeSecurityBindingConfiguration allAuthorizerMethods = new DeltaspikeSecurityBindingConfiguration(""); //$NON-NLS-1$

	@Override
	protected AbstractDefinitionContextExtension copy(boolean clean) {
		DeltaspikeSecurityDefinitionContext copy = new DeltaspikeSecurityDefinitionContext();
		copy.root = root;
		if(!clean) {
			copy.securityBindingConfigurations.putAll(securityBindingConfigurations);
			allAuthorizerMethods = copy.allAuthorizerMethods;
		}		
		return copy;
	}

	@Override
	protected void doApplyWorkingCopy() {
		DeltaspikeSecurityDefinitionContext copy = (DeltaspikeSecurityDefinitionContext)workingCopy;
		securityBindingConfigurations = copy.securityBindingConfigurations;
		allAuthorizerMethods = copy.allAuthorizerMethods;
		
	}

	@Override
	public void clean() {
		securityBindingConfigurations.clear();
		allAuthorizerMethods.getAuthorizerMembers().clear();
	}

	@Override
	public void clean(IPath path) {
		for (DeltaspikeSecurityBindingConfiguration c: securityBindingConfigurations.values()) {
			c.clear(path);
		}
		allAuthorizerMethods.clear(path);
	}

	@Override
	public void clean(String typeName) {
		securityBindingConfigurations.remove(typeName);
		for (DeltaspikeSecurityBindingConfiguration c: securityBindingConfigurations.values()) {
			c.clear(typeName);
		}
		allAuthorizerMethods.clear(typeName);
	}

	@Override
	public void computeAnnotationKind(AnnotationDefinition annotation) {
		if(SECURES_ANNOTATION_TYPE_NAME.equals(annotation.getType().getFullyQualifiedName())) {
			annotation.setExtendedKind(SECURES_ANNOTATION_KIND);
		} else if(annotation.isAnnotationPresent(SECURITY_BINDING_ANNOTATION_TYPE_NAME)) {
			annotation.setExtendedKind(SECURITY_BINDING_ANNOTATION_KIND);
			String qn = annotation.getType().getFullyQualifiedName();
			DeltaspikeSecurityBindingConfiguration c = getConfiguration(qn);
			c.setSecurityBundingTypeDefinition(annotation, this);
			if(!annotation.getType().isBinary()) {
				IPath newPath = annotation.getType().getResource().getFullPath();
				Set<IPath> ps = c.getInvolvedTypes();
				for (IPath p: ps) {
					getRootContext().addDependency(p, newPath);
					getRootContext().addDependency(newPath, p);
				}
				ps.add(newPath);
			}
		}
	}

	public boolean isSecurityBindingTypeAnnotation(IType type) {
		return (securityBindingConfigurations.containsKey(type.getFullyQualifiedName()));
	}

	public Map<String, DeltaspikeSecurityBindingConfiguration> getConfigurations() {
		return securityBindingConfigurations;
	}

	public DeltaspikeSecurityBindingConfiguration getConfiguration(String typeName) {
		DeltaspikeSecurityBindingConfiguration result = securityBindingConfigurations.get(typeName);
		if(result == null) {
			result = new DeltaspikeSecurityBindingConfiguration(typeName);
			securityBindingConfigurations.put(typeName, result);
		}
		return result;
	}

	public Set<DeltaspikeAuthorityMethod> getAuthorityMethods(IPath path) {
		Set<DeltaspikeAuthorityMethod> result = new HashSet<DeltaspikeAuthorityMethod>();
		for(DeltaspikeAuthorityMethod m: allAuthorizerMethods.getAuthorizerMembers()) {
			if(path.equals(m.getPath())) {
				result.add(m);
			}
		}
		return result;
	}

}
