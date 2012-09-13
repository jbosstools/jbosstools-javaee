/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.SourceRange;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.util.FileUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AbstractTypeDefinition extends AbstractMemberDefinition {
	protected String qualifiedName;
	protected IType type;
	protected ParametedType parametedType = null;
	protected boolean isVetoed = false;
	
	public AbstractTypeDefinition() {}

	public void veto() {
		isVetoed = true;
	}

	public void unveto() {
		isVetoed = false;
	}

	public boolean isVetoed() {
		return isVetoed;
	}

	@Override
	public AbstractTypeDefinition getTypeDefinition() {
		return this;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public IType getType() {
		return type;
	}

	public void setType(IType type, IRootDefinitionContext context, int flags) {
		super.setAnnotatable(type, type, context, flags);
	}

	@Override
	protected void init(IType contextType, IRootDefinitionContext context, int flags) throws CoreException {
		this.type = contextType;
		super.init(contextType, context, flags);
		qualifiedName = getType().getFullyQualifiedName();
		resetParametedType();
	}

	public ParametedType getParametedType() {
		return parametedType;
	}

	public void resetParametedType() {
		parametedType = project.getDelegate().getNature().getTypeFactory().newParametedType(type);
		if(type != null && !type.isBinary()) {
			parametedType.setPositionProvider(new PositionProviderImpl());
			parametedType.getInheritedTypes();
		}
	}

	public void setParametedType(IParametedType t) {
		parametedType = (ParametedType)t;
	}

	public Set<IParametedType> getInheritedTypes() {
		if(parametedType == null) {
			return Collections.emptySet(); 
		}
		return parametedType.getInheritedTypes();
	}

	public Collection<IParametedType> getAllTypes() {
		if(parametedType == null) {
			return Collections.emptyList(); 
		} 
		return parametedType.getAllTypes();
	}

	/**
	 * Returns strings that uniquely identifies this definition
	 * @return
	 */
	public String getKey() {
		String result = getQualifiedName();
		if(originalDefinition != null) {
			result += ":" + originalDefinition.getStartPosition() + ":" + originalDefinition.getLength();
		}
		return result;
	}

	public String getContent() {
		if(type == null || type.isBinary()) return null;
		if(resource instanceof IFile && resource.getName().endsWith(".java")) {
			return FileUtil.getContentFromEditorOrFile((IFile)resource);
		}
		return null;
	}
	
	class PositionProviderImpl implements ParametedType.PositionProvider {
		Map<String, ISourceRange> map = null;

		void init() throws CoreException {
			map = new HashMap<String, ISourceRange>();
			String content = getContent();
			if(content == null) return;

			//Any disagreement between content and range means that content is obsolete
			//and this build is obsolete (type was updated wile old build is proceeding).
			//New build is pending, and now we only have to go smoothly around inconsistencies.
			ISourceRange r = type.getNameRange();
			if(r == null) return;
			int b = r.getOffset() + r.getLength();
			if(b < 0) return;
			int e = content.indexOf('{', b);
			if(e < 0) e = content.length();
			if(e < b) return;

			String sup = content.substring(b, e);
			String sc = type.getSuperclassName();
			if(sc != null) checkRange(b, sup, sc);
			String[] is = type.getSuperInterfaceNames();
			if(is != null) for (int i = 0; i < is.length; i++) checkRange(b, sup, is[i]);
		}
	
		void checkRange(int offset, String sup, String sc) {
			int k = sup.indexOf(sc);
			if(k >= 0) {
				map.put(sc, new SourceRange(offset + k, sc.length()));
			}			
		}

		public boolean isLoaded() {
			return map != null;
		}

		public ISourceRange getRange(String superTypeName) {
			if(map == null) {
				try {
					init();
				} catch (CoreException e) {
					CDICorePlugin.getDefault().logError(e);
				}
			}

			return map.get(superTypeName);
		}
	}
}