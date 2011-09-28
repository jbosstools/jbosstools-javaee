package org.jboss.tools.cdi.core.test;

import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.internal.core.impl.BeanField;
import org.jboss.tools.cdi.internal.core.impl.BeanMethod;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.EventBean;
import org.jboss.tools.cdi.internal.core.impl.InjectionPointField;
import org.jboss.tools.cdi.internal.core.impl.InjectionPointParameter;
import org.jboss.tools.cdi.internal.core.impl.InterceptorBindingElement;
import org.jboss.tools.cdi.internal.core.impl.QualifierElement;
import org.jboss.tools.cdi.internal.core.impl.ScopeElement;
import org.jboss.tools.cdi.internal.core.impl.StereotypeElement;

import junit.framework.TestCase;

public class CDIImagesTest extends TestCase {
	public CDIImagesTest(){
	}
	
	public void checkImageForCDIElement(ICDIElement element){
		assertNotNull("CDI Element must be not null", element);
		
		Image image = CDIImages.getImageByElement(element);
		
		assertNotNull("Icon image for CDI element - "+element.getClass()+" not loaded", image);
		
		assertTrue("Icon image for CDI element - "+element.getClass()+" not found", image != CDIImages.WELD_IMAGE);
	}
	
	public void testImageForBeanClass(){
		ClassBean element = new ClassBean();
		checkImageForCDIElement(element);
	}

	public void testImageForInjectionPointField(){
		InjectionPointField element = new InjectionPointField();
		checkImageForCDIElement(element);
	}

	public void testImageForInjectionPointParameter(){
		InjectionPointParameter element = new InjectionPointParameter();
		checkImageForCDIElement(element);
	}

	public void testImageForStereotype(){
		StereotypeElement element = new StereotypeElement();
		checkImageForCDIElement(element);
	}

	public void testImageForInterceptorBinding(){
		InterceptorBindingElement element = new InterceptorBindingElement();
		checkImageForCDIElement(element);
	}

	public void testImageForQualifier(){
		QualifierElement element = new QualifierElement();
		checkImageForCDIElement(element);
	}

	public void testImageForScope(){
		ScopeElement element = new ScopeElement();
		checkImageForCDIElement(element);
	}

	public void testImageForBeanMethod(){
		BeanMethod element = new BeanMethod();
		checkImageForCDIElement(element);
	}

	public void testImageForBeanField(){
		BeanField element = new BeanField();
		checkImageForCDIElement(element);
	}
}
