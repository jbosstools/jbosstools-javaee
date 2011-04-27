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
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IJavaAnnotation;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigConstants;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigExtension;
import org.jboss.tools.cdi.seam.config.core.ConfigDefinitionContext;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeanDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamFieldDefinition;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class SeamDefinitionsTest extends SeamConfigTest {
	public SeamDefinitionsTest() {}

	public void testReplacingAndModifyingBeans() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test602:Report");
		assertEquals(1, ds.size());
		SeamBeanDefinition report = ds.iterator().next();
		ITextSourceReference modifies = report.getModifiesLocation();
		assertNotNull(modifies);
		IJavaAnnotation annotation = report.getAnnotation("org.jboss.test602.report.NewQualifier");
		assertNotNull(annotation);
		
		ds = findBeanDefinitionByTagName(d, "test602:ReportDatasource");
		assertEquals(1, ds.size());
		SeamBeanDefinition reportDatasource = ds.iterator().next();
		ITextSourceReference replaces = reportDatasource.getReplacesLocation();
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

}
