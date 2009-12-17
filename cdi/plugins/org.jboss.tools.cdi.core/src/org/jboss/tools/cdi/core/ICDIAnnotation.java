package org.jboss.tools.cdi.core;

import org.eclipse.jdt.core.IType;

/**
 * Common interface for qualifier, stereotype, intercepror binding and scope objects.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface ICDIAnnotation extends ICDIElement {

	public IType getSourceType();

	public IAnnotationDeclaration getInheritedDeclaration();

}
