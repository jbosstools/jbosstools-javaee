package org.jboss.tools.cdi.core;

import org.eclipse.jdt.core.IType;

/**
 * Common interface for qualifier, stereotype, intercepror binding and scope objects.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface ICDIAnnotation extends ICDIElement {

	/**
	 * Returns the corresponding IType of the annotation type.
	 * 
	 * @return the corresponding IType
	 */
	public IType getSourceType();

	/**
	 * Returns the location of @Inherited declaration of this annotation type. If the bean
	 * doesn't have the @Inherited declaration then null will be returned.
	 * 
	 * @return the location of @Name declaration of this bean.
	 */
	public IAnnotationDeclaration getInheritedDeclaration();

}
