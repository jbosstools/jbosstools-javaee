/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.core.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.BeanField;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigConstants;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigExtension;
import org.jboss.tools.cdi.seam.config.core.ConfigDefinitionContext;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeanDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamFieldDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamMethodDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamParameterDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamVirtualFieldDefinition;
import org.jboss.tools.cdi.seam.config.core.xml.Location;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderConstants;
import org.jboss.tools.common.java.IJavaAnnotation;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class SeamDefinitionsTest extends SeamConfigTest {
	public SeamDefinitionsTest() {}

	public void testLineNumber() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test602:Report");
		assertEquals(1, ds.size());
		SeamBeanDefinition report = ds.iterator().next();
		assertEquals(20, report.getElement().getLocation().getLine());
	}

	public void testReplacingAndModifyingBeans() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test602:Report");
		assertEquals(1, ds.size());
		SeamBeanDefinition report = ds.iterator().next();
		Location modifies = report.getModifiesLocation();
		assertNotNull(modifies);
		IJavaAnnotation annotation = report.getAnnotation("org.jboss.test602.report.NewQualifier");
		assertNotNull(annotation);
		
		ds = findBeanDefinitionByTagName(d, "test602:ReportDatasource");
		assertEquals(1, ds.size());
		SeamBeanDefinition reportDatasource = ds.iterator().next();
		Location replaces = reportDatasource.getReplacesLocation();
		assertNotNull(replaces);
	}

	public void testApplyingAnnotations() throws CoreException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test603:QualifiedBean1");
		assertEquals(1, ds.size());
		SeamBeanDefinition b = ds.iterator().next();
		IJavaAnnotation a = b.getAnnotation("org.jboss.test603.OtherQualifier");
		Map<String, Object> ps = toMap(a.getMemberValuePairs());
		assertEquals(3, ps.size());
		assertEquals("AA", "" + ps.get("value1"));
		assertEquals("1", "" + ps.get("value2"));
		assertEquals("false", "" + ps.get("value"));
		
		ds = findBeanDefinitionByTagName(d, "test603:QualifiedBean2");
		assertEquals(1, ds.size());
		b = ds.iterator().next();
		a = b.getAnnotation("org.jboss.test603.OtherQualifier");
		ps = toMap(a.getMemberValuePairs());
		assertEquals(3, ps.size());
		assertEquals("BB", "" + ps.get("value1"));
		assertEquals("2", "" + ps.get("value2"));
		assertEquals("true", "" + ps.get("value"));
	}

	Map<String, Object> toMap(IMemberValuePair[] ps) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (IMemberValuePair p: ps) result.put(p.getMemberName(), p.getValue());
		return result;
	}

	public void testInitialFieldValues() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		/*
<test6041:RobotFactory>
    <test6041:robot>
        <s:Produces/>
    </test6041:robot>
</test6041:RobotFactory>
		 */
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test6041:RobotFactory");
		assertEquals(1, ds.size());
		SeamBeanDefinition b = ds.iterator().next();
		SeamFieldDefinition f = b.getField("robot");
		assertNotNull(f);
		IJavaAnnotation a = f.getAnnotation("javax.enterprise.inject.Produces");
		assertNotNull(a);

/*
<test6041:MyBean>
    <test6041:SomeQualifier/>
    <test6041:company>Red Hat Inc</test6041:company>
</test6041:MyBean>

<test6041:MyBean>
    <test6041:company>
        <s:value>Red Hat Inc</s:value>
        <test6041:SomeQualifier/>
     </test6041:company>
</test6041:MyBean>
 */
		ds = findBeanDefinitionByTagName(d, "test6041:MyBean");
		assertEquals(2, ds.size());
		Iterator<SeamBeanDefinition> it = ds.iterator();
		SeamBeanDefinition b1 = it.next();
		SeamBeanDefinition b2 = it.next();
		String someQualifier = "org.jboss.test6041.SomeQualifier";
		boolean qb1 = b1.getAnnotation(someQualifier) != null;
		boolean qb2 = b2.getAnnotation(someQualifier) != null;
		assertFalse(qb1 == qb2);
		if(qb2) {
			//assign b1 to bean with qualifier; b2 to bean having field with qualifier. 
			SeamBeanDefinition bc = b1;
			b1 = b2;
			b2 = bc;
		}
		f = b1.getField("company");
		assertNotNull(f);
		assertNull(f.getAnnotation(someQualifier));
		assertEquals("Red Hat Inc", f.getValue());
		f = b2.getField("company");
		assertNotNull(f);
		assertNotNull(f.getAnnotation(someQualifier));
		assertEquals("Red Hat Inc", f.getValue());
	}

	/**
<test6041:ArrayFieldValue>
    <test6041:intArrayField>
        <s:value>1</s:value>
        <s:value>2</s:value>
    </test6041:intArrayField>
    <test6041:classArrayField>
        <s:value>java.lang.Integer</s:value>
        <s:value>java.lang.Long</s:value>
    </test6041:classArrayField>
    <test6041:stringArrayField>
        <s:value>hello</s:value>
        <s:value>world</s:value>
    </test6041:stringArrayField>
</test6041:ArrayFieldValue>
	 * 
	 */
	public void testInitialFieldListValues() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test6041:ArrayFieldValue");
		assertEquals(1, ds.size());
		SeamBeanDefinition b = ds.iterator().next();
		SeamFieldDefinition f = b.getField("intArrayField");
		assertNotNull(f);
		List<String> vs = f.getListValue();
		assertEquals(2, vs.size());
		assertEquals("1", vs.get(0));
		assertEquals("2", vs.get(1));
		
		f = b.getField("classArrayField");
		assertNotNull(f);
		vs = f.getListValue();
		assertEquals(2, vs.size());
		assertEquals("java.lang.Integer", vs.get(0));
		assertEquals("java.lang.Long", vs.get(1));
		
		f = b.getField("stringArrayField");
		assertNotNull(f);
		vs = f.getListValue();
		assertEquals(2, vs.size());
		assertEquals("hello", vs.get(0));
		assertEquals("world", vs.get(1));
		
	}

	/**
<test6042:Knight>
  <test6042:sword>
     <s:value>
        <test6042:Sword type="sharp"/>
     </s:value>
  </test6042:sword>
  <test6042:horse>
     <s:value>
        <test6042:Horse>
           <test6042:name>
              <value>billy</value>
           </test6042:name>
           <test6042:shoe>
              <Inject/>
           </test6042:shoe>
        </test6042:Horse>
     </s:value>
  </test6042:horse>
</test6042:Knight>
	 */
	public void testInlineBeanDeclarations() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test6042:Knight");
		assertEquals(1, ds.size());
		SeamBeanDefinition knight = ds.iterator().next();
	
		SeamFieldDefinition f = knight.getField("sword");
		assertNotNull(f);
		ds = findBeanDefinitionByTagName(d, "test6042:Sword");
		assertEquals(1, ds.size());
		SeamBeanDefinition sword = ds.iterator().next();
		assertTrue(sword.isInline());
		IJavaAnnotation a1 = sword.getAnnotation(CDISeamConfigConstants.INLINE_BEAN_QUALIFIER);
		assertNotNull(a1);
		IJavaAnnotation a2 = f.getAnnotation(CDISeamConfigConstants.INLINE_BEAN_QUALIFIER);
		assertTrue(a1 == a2);
		IJavaAnnotation inject = f.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
		assertNotNull(inject);
	
		SeamFieldDefinition swordType = sword.getField("type");
		assertNotNull(swordType);
		assertEquals("sharp", swordType.getValue());

		f = knight.getField("horse");
		assertNotNull(f);
		ds = findBeanDefinitionByTagName(d, "test6042:Horse");
		assertEquals(1, ds.size());
		SeamBeanDefinition horse = ds.iterator().next();
		assertTrue(horse.isInline());
		a1 = horse.getAnnotation(CDISeamConfigConstants.INLINE_BEAN_QUALIFIER);
		assertNotNull(a1);
		a2 = f.getAnnotation(CDISeamConfigConstants.INLINE_BEAN_QUALIFIER);
		assertTrue(a1 == a2);
		inject = f.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
		assertNotNull(inject);

		SeamFieldDefinition horseName = horse.getField("name");
		assertNotNull(horseName);
		assertEquals("billy", horseName.getValue());
		SeamFieldDefinition shoe = horse.getField("shoe");
		assertNotNull(shoe);
		inject = shoe.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
		assertNotNull(inject);
		
	}

	/**
<test605:MethodBean>
    <test605:doStuff>
        <s:Produces/>
    </test605:doStuff>      

    <test605:doStuff>
        <s:Produces/>
        <test605:Qualifier1/>
        <s:parameters>
            <s:Long>
                <test605:Qualifier2/>
            </s:Long>
        </s:parameters>
    </test605:doStuff>

    <test605:doStuff>
        <s:Produces/>
        <test605:Qualifier1/>
        <s:parameters>
            <s:array dimensions="2">
                <test605:Qualifier2/>
                <s:Long/>
            </s:array>
        </s:parameters>
    </test605:doStuff>
</test605:MethodBean>
	 */
	public void testConfiguringMethods() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test605:MethodBean");
		assertEquals(1, ds.size());
		SeamBeanDefinition b = ds.iterator().next();
		List<SeamMethodDefinition> ms = b.getMethods();
		assertEquals(3, ms.size());
		
		SeamMethodDefinition noParam = ms.get(0);
		assertEquals(0, noParam.getParameters().size());
		assertNotNull(noParam.getAnnotation(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME));
		
		SeamMethodDefinition oneParam = ms.get(1);
		assertEquals(1, oneParam.getParameters().size());
		assertNotNull(oneParam.getAnnotation(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME));
		assertNotNull(oneParam.getAnnotation("org.jboss.test605.Qualifier1"));
		SeamParameterDefinition param = oneParam.getParameters().get(0);
		assertEquals(0, param.getDimensions());
		assertEquals("java.lang.Long", param.getType().getFullyQualifiedName());
		assertNotNull(param.getAnnotation("org.jboss.test605.Qualifier2"));
		
		SeamMethodDefinition oneArrayParam = ms.get(2);
		assertEquals(1, oneParam.getParameters().size());
		assertNotNull(oneArrayParam.getAnnotation(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME));
		assertNotNull(oneArrayParam.getAnnotation("org.jboss.test605.Qualifier1"));
		param = oneArrayParam.getParameters().get(0);
		assertEquals(2, param.getDimensions());
		assertEquals("java.lang.Long", param.getType().getFullyQualifiedName());
		assertNotNull(param.getAnnotation("org.jboss.test605.Qualifier2"));
		
	}

	/**
<test605:MethodBean2>
    <test605:method>
        <s:array>
            <test605:String/>
        </s:array>
    </test605:method>
</test605:MethodBean2>
	 */
	public void testConfiguringMethods2() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test605:MethodBean2");
		assertEquals(1, ds.size());
		SeamBeanDefinition b = ds.iterator().next();
		List<SeamMethodDefinition> ms = b.getMethods();
		assertEquals(1, ms.size());
		
		SeamMethodDefinition m = ms.get(0);
		assertEquals(1, m.getParameters().size());
		SeamParameterDefinition param = m.getParameters().get(0);
		assertEquals(1, param.getDimensions());
		assertEquals("java.lang.String", param.getType().getFullyQualifiedName());
		
	}

	public void testResolvingBetweenFieldAndMethod() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test605:MethodBean3");
		assertEquals(1, ds.size());
		SeamBeanDefinition b = ds.iterator().next();
		SeamFieldDefinition f = b.getField("name");
		assertNotNull(f);
		
		ds = findBeanDefinitionByTagName(d, "test605:MethodBean4");
		assertEquals(1, ds.size());
		b = ds.iterator().next();
		List<SeamMethodDefinition> ms = b.getMethods();
		assertEquals(1, ms.size());
		SeamMethodDefinition m = ms.get(0);
		assertEquals("name", m.getMethod().getElementName());
		
	}

	/**
<test606:MyBean>
   <s:parameters>
       <s:Integer>
           <test606:MyQualifier/>
       </s:Integer>
   </s:parameters>
</test606:MyBean>
	 */
	public void testConfiguringConstructor() throws CoreException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test606:MyBean");
		assertEquals(1, ds.size());
		SeamBeanDefinition b = ds.iterator().next();
		List<SeamMethodDefinition> ms = b.getMethods();
		assertEquals(1, ms.size());
		
		SeamMethodDefinition m = ms.get(0);
		IMethod jm = m.getMethod();
		assertTrue(jm.isConstructor());
		assertEquals(1, m.getParameters().size());
		SeamParameterDefinition param = m.getParameters().get(0);
		assertEquals(0, param.getDimensions());
		assertEquals("java.lang.Integer", param.getType().getFullyQualifiedName());
		
	}

	/**
<test607:SomeBean>
    <test607:someField>
        <s:Inject/>
        <s:Exact>org.jboss.test607.MyInterface</s:Exact>
    </test607:someField>
</test607:SomeBean>
	 */
	public void testOverridingTypeOfAnInjectionPoint() throws CoreException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test607:SomeBean");
		assertEquals(1, ds.size());
		SeamBeanDefinition b = ds.iterator().next();
		SeamFieldDefinition f = b.getField("someField");
		assertNotNull(f);
		IJavaAnnotation inject = f.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
		assertNotNull(inject);
		IJavaAnnotation exact = f.getAnnotation(CDISeamSolderConstants.EXACT_ANNOTATION_TYPE_NAME);
		assertNotNull(exact);
		IMemberValuePair[] ps = exact.getMemberValuePairs();
		assertEquals(1, ps.length);
		assertEquals("org.jboss.test607.MyInterface", ps[0].getValue());
		
	}

	public void testConfiguringMetaAnnotations() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");

		/*
<test608:SomeQualifier>
    <s:Qualifier/>
</test608:SomeQualifier>
		 */
		context.getRootContext().getAnnotation("org.jboss.test608.SomeQualifier");
		IQualifier q = cdi.getQualifier("org.jboss.test608.SomeQualifier");
		assertNotNull(q);

		/*
<test608:SomeInterceptorBinding>
    <s:InterceptorBinding/>
</test608:SomeInterceptorBinding>
		 */
		IInterceptorBinding b = cdi.getInterceptorBinding("org.jboss.test608.SomeInterceptorBinding");
		assertNotNull(b);
	
		/*
<test608:SomeStereotype>
    <s:Stereotype/>
    <test608:MyInterceptorBinding/>
    <s:Named/>
</test608:SomeStereotype>
		 */
		IStereotype s = cdi.getStereotype("org.jboss.test608.SomeStereotype");
		assertNotNull(s);
		assertNotNull(s.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME));
		Set<IInterceptorBinding> bs = s.getInterceptorBindings();
		assertEquals(1, bs.size());
		
	}
	
	public void testVirtualProducerField() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamVirtualFieldDefinition> fs = d.getVirtualFieldDefinitions();
		assertFalse(fs.isEmpty());
		SeamVirtualFieldDefinition f = findVirtualField(fs, "java.lang.String", "org.jboss.test606.MyQualifier");
		assertNotNull(f);
		assertNotNull(f.getAnnotation(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME));
		assertEquals("Version 1.23", f.getValue());

	}

	private SeamVirtualFieldDefinition findVirtualField(Set<SeamVirtualFieldDefinition> fs, String typeName, String qualifier) {
		for (SeamVirtualFieldDefinition f: fs) {
			IType t = f.getType();
			if(typeName.equals(t.getFullyQualifiedName()) && f.getAnnotation(qualifier) != null) return f;
		}
		return null;
	}

}
