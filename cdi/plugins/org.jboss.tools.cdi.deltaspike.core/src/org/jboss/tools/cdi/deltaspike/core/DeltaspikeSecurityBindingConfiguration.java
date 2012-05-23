package org.jboss.tools.cdi.deltaspike.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;

public class DeltaspikeSecurityBindingConfiguration {
	String securityBindingTypeName;
	AnnotationDefinition securityBindingType;
	
	Map<AbstractMemberDefinition, IAnnotationDeclaration> boundMembers = new HashMap<AbstractMemberDefinition, IAnnotationDeclaration>();
	Set<DeltaspikeAuthorityMethod> authorizerMembers = new HashSet<DeltaspikeAuthorityMethod>();

	Set<IPath> involvedResources = new HashSet<IPath>();

	public DeltaspikeSecurityBindingConfiguration(String securityBindingTypeName) {
		this.securityBindingTypeName = securityBindingTypeName;
	}

	public void setSecurityBundingTypeDefinition(AnnotationDefinition securityBindingType, DeltaspikeSecurityDefinitionContext context) {
		this.securityBindingType = securityBindingType;
	}

	public void clear(IPath path) {
		involvedResources.remove(path);
	}

	public void clear(String typeName) {
		Iterator<AbstractMemberDefinition> it = boundMembers.keySet().iterator();
		while(it.hasNext()) {
			if(typeName.equals(it.next().getTypeDefinition().getQualifiedName())) {
				it.remove();
			}
		}
		Iterator<DeltaspikeAuthorityMethod> it2 = authorizerMembers.iterator();
		while(it2.hasNext()) {
			if(typeName.equals(it2.next().getDeclaringTypeName())) {
				it2.remove();
			}
		}
	}

	Map<AbstractMemberDefinition, IAnnotationDeclaration> getBoundMembers() {
		return boundMembers;
	}

	Set<DeltaspikeAuthorityMethod> getAuthorizerMembers() {
		return authorizerMembers;
	}

	public String getSecurityBindingTypeName() {
		return securityBindingTypeName;
	}

	public AnnotationDefinition getSecurityBindingTypeDefinition() {
		return securityBindingType;
	}

	public Set<IPath> getInvolvedTypes() {
		return involvedResources;
	}

}
