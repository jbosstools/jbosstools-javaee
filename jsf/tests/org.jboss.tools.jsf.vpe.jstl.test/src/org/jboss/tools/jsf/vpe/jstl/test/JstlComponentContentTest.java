package org.jboss.tools.jsf.vpe.jstl.test;

import org.jboss.tools.vpe.base.test.ComponentContentTest;

public class JstlComponentContentTest extends ComponentContentTest {

    public JstlComponentContentTest(String name) {
	super(name);
	setCheckWarning(false);
    }

    public void testCatch() throws Throwable {
	performContentTest("components/catch.jsp");//$NON-NLS-1$
    }
    
    public void testChoose() throws Throwable {
	performContentTest("components/choose.jsp");//$NON-NLS-1$
    }
    
    public void testOtherwise() throws Throwable {
	performContentTest("components/otherwise.jsp");//$NON-NLS-1$
    }
    
    public void testOut() throws Throwable {
	performContentTest("components/out.jsp");//$NON-NLS-1$
    }
    
    public void testWhen() throws Throwable {
	performContentTest("components/when.jsp");//$NON-NLS-1$
    }
    
    public void testIf() throws Throwable {
	performContentTest("components/if.jsp");//$NON-NLS-1$
    }
    
    public void testImport() throws Throwable {
	performContentTest("components/import.jsp");//$NON-NLS-1$
    }
    
    public void testForEach() throws Throwable {
	performContentTest("components/forEach.jsp");//$NON-NLS-1$
    }
    
    public void testForTokens() throws Throwable {
	performContentTest("components/forTokens.jsp");//$NON-NLS-1$
    }
    
    public void testParam() throws Throwable {
	performInvisibleTagTest("components/param.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testRemove() throws Throwable {
	performInvisibleTagTest("components/remove.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testRedirect() throws Throwable {
	performInvisibleTagTest("components/redirect.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testSet() throws Throwable {
	performInvisibleTagTest("components/set.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testUrl() throws Throwable {
	performContentTest("components/url.jsp");//$NON-NLS-1$
    }
    
    public void testRequestEncoding() throws Throwable {
	performInvisibleTagTest("components/requestEncoding.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testSetLocale() throws Throwable {
	performInvisibleTagTest("components/setLocale.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testTimeZone() throws Throwable {
	performContentTest("components/timeZone.jsp"); //$NON-NLS-1$
    }
    
    public void testSetTimeZone() throws Throwable {
	performInvisibleTagTest("components/setTimeZone.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testBundle() throws Throwable {
	performContentTest("components/bundle.jsp");//$NON-NLS-1$
    }
    
    public void testSetBundle() throws Throwable {
	performInvisibleTagTest("components/setBundle.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testMessage() throws Throwable {
	performInvisibleTagTest("components/message.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }    
    
    public void testFmtParam() throws Throwable {
	performInvisibleTagTest("components/fmtParam.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testFormatNumber() throws Throwable {
	performInvisibleTagTest("components/formatNumber.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testParseNumber() throws Throwable {
	performInvisibleTagTest("components/parseNumber.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testFormatDate() throws Throwable {
	performInvisibleTagTest("components/formatDate.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testParseDate() throws Throwable {
	performInvisibleTagTest("components/parseDate.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testTransaction() throws Throwable {
	performContentTest("components/transaction.jsp");//$NON-NLS-1$
    }    
    
    public void testUpdate() throws Throwable {
	performInvisibleTagTest("components/update.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testQuery() throws Throwable {
	performInvisibleTagTest("components/query.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testSqlParam() throws Throwable {
	performInvisibleTagTest("components/sqlParam.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testDateParam() throws Throwable {
	performInvisibleTagTest("components/dateParam.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testSetDataSource() throws Throwable {
	performInvisibleTagTest("components/setDataSource.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testXParse() throws Throwable {
	performInvisibleTagTest("components/xParse.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testTransform() throws Throwable {
	performInvisibleTagTest("components/transform.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testXForEach() throws Throwable {
	performContentTest("components/xForEach.jsp");//$NON-NLS-1$
    }        
    
    public void testXIf() throws Throwable {
	performContentTest("components/xIf.jsp");//$NON-NLS-1$
    }        
    
    public void testXChoose() throws Throwable {
	performContentTest("components/xChoose.jsp");//$NON-NLS-1$
    }        
    
    public void testXWhen() throws Throwable {
	performContentTest("components/xWhen.jsp");//$NON-NLS-1$
    }        
    
    public void testXOtherwise() throws Throwable {
	performContentTest("components/xOtherwise.jsp");//$NON-NLS-1$
    }        
    
    public void testXOut() throws Throwable {
	performContentTest("components/xOut.jsp");//$NON-NLS-1$
    }        
    

    public void testXSet() throws Throwable {
	performInvisibleTagTest("components/xSet.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testXParam() throws Throwable {
	performInvisibleTagTest("components/xParam.jsp", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    @Override
    protected String getTestProjectName() {
	return JstlAllTests.IMPORT_PROJECT_NAME;
    }

}
