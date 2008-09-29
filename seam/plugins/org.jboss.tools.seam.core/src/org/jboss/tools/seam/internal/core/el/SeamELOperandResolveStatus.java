package org.jboss.tools.seam.internal.core.el;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.model.util.TypeInfoCollector;
import org.jboss.tools.seam.core.ISeamContextVariable;

/**
 * Status of EL resolving.
 * @author Jeremy
 */
public class SeamELOperandResolveStatus {
	private ELInvocationExpression tokens;
	public List<ISeamContextVariable> usedVariables;
	Map<String, TypeInfoCollector.MethodInfo> unpairedGettersOrSetters;
	Set<String> proposals;
	private ELInvocationExpression lastResolvedToken;
	private boolean isMapOrCollectionOrBundleAmoungTheTokens = false;
	private TypeInfoCollector.MemberInfo memberOfResolvedOperand;

	/**
	 * @return MemberInfo of last segment of EL operand. Null if El is not resolved.
	 */
	public TypeInfoCollector.MemberInfo getMemberOfResolvedOperand() {
		return memberOfResolvedOperand;
	}

	/**
	 * Sets MemberInfo for last segment of EL operand.
	 * @param lastResolvedMember
	 */
	public void setMemberOfResolvedOperand(
			TypeInfoCollector.MemberInfo lastResolvedMember) {
		this.memberOfResolvedOperand = lastResolvedMember;
	}

	/**
	 * Constructor
	 * @param tokens Tokens of EL
	 */
	public SeamELOperandResolveStatus(ELInvocationExpression tokens) {
		this.tokens = tokens;
	}

	/**
	 * @return true if EL contains any not parametrized Collection or ResourceBundle.
	 */
	public boolean isMapOrCollectionOrBundleAmoungTheTokens() {
		return this.isMapOrCollectionOrBundleAmoungTheTokens;
	}

	public void setMapOrCollectionOrBundleAmoungTheTokens() {
		this.isMapOrCollectionOrBundleAmoungTheTokens = true;
	}

	/**
	 * @return true if EL is resolved.
	 */
	public boolean isOK() {
		return !getProposals().isEmpty() || isMapOrCollectionOrBundleAmoungTheTokens(); 
	}

	/**
	 * @return false if El is not resolved.
	 */
	public boolean isError() {
		return !isOK();
	}

	/**
	 * @return List of resolved tokens of EL. Includes separators "."
	 */
	public ELInvocationExpression getResolvedTokens() {
		return lastResolvedToken;
	}

	/**
	 * @return List of unresolved tokens of EL.
	 */
	public ELInvocationExpression getUnresolvedTokens() {
		if(lastResolvedToken == tokens) return null;
		if(lastResolvedToken != null && (lastResolvedToken.getParent() instanceof ELInvocationExpression)) {
			return (ELInvocationExpression)lastResolvedToken.getParent();
		}
		ELInvocationExpression l = tokens;
		while(l != null && l.getLeft() != lastResolvedToken) {
			l = l.getLeft();
		}
		return l;
	}

	/**
	 * @return Last resolved token of EL. Can be separator "."  // no, change it
	 */
	public ELInvocationExpression getLastResolvedToken() {
		return lastResolvedToken;
	}

	/**
	 * @param lastResolvedToken Last resolved token of EL. Can be separator "."
	 */
	public void setLastResolvedToken(ELInvocationExpression lastResolvedToken) {
		this.lastResolvedToken = lastResolvedToken;
	}

	/**
	 * @return Tokens of EL.
	 */
	public ELInvocationExpression getTokens() {
		return tokens;
	}

	/**
	 * @param tokens Tokens of EL.
	 */
	public void setTokens(ELInvocationExpression tokens) {
		this.tokens = tokens;
	}

	/**
	 * @return Set of proposals for EL.
	 */
	public Set<String> getProposals() {
		return proposals == null ? new TreeSet<String>() : proposals;
	}

	/**
	 * @param proposals Set of proposals.
	 */
	public void setProposals(Set<String> proposals) {
		this.proposals = proposals;
	}

	/**
	 * @return List of Seam Context Variables used in EL.  
	 */
	public List<ISeamContextVariable> getUsedVariables() {
		return (usedVariables == null ? new ArrayList<ISeamContextVariable>() : usedVariables);
	}

	/**
	 * @param usedVariables List of Seam Context Variables used in EL.
	 */
	public void setUsedVariables(List<ISeamContextVariable> usedVariables) {
		this.usedVariables = usedVariables;
	}

	/**
	 * @return Map of unpaired getters and setters (getters/setters without proper setters/getters).
	 * of all properties used in EL.
	 * Key - name of property.
	 * Value - MethodInfo of existed getter/setter.
	 */
	public Map<String, TypeInfoCollector.MethodInfo> getUnpairedGettersOrSetters() {
		if (unpairedGettersOrSetters == null) {
			unpairedGettersOrSetters = new HashMap<String, TypeInfoCollector.MethodInfo>();
		}
		return unpairedGettersOrSetters;
	}

	/**
	 * Clear Map of unpaired getters and setters.
	 */
	public void clearUnpairedGettersOrSetters() {
		getUnpairedGettersOrSetters().clear();
	}

}
