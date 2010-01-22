package org.jboss.tools.cdi.core;

import java.util.List;

import org.eclipse.jdt.core.IType;

/**
 * Common interface for an annotation interface.
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
	IType getSourceType();

	/**
	 * Returns the declaration of @Inherited declaration of this annotation
	 * type. If the interface doesn't have the @Inherited declaration then null
	 * will be returned.
	 * 
	 * @return the declaration of @Inherited declaration of this bean
	 */
	IAnnotationDeclaration getInheritedDeclaration();

	/**
	 * Returns all the available annotations which are declared for this
	 * interface.
	 * 
	 * @return all the available annotations which are declared for this
	 *         interface
	 */
	List<IAnnotationDeclaration> getAnnotationDeclarations();

	/**
	 * Returns the annotations with given type name.
	 * 
	 * @param typeName
	 * @return the annotations with given type name
	 */
	IAnnotationDeclaration getAnnotationDeclaration(String typeName);
}