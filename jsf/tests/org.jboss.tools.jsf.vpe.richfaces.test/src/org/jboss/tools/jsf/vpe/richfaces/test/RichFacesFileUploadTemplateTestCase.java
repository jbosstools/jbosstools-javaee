

package org.jboss.tools.jsf.vpe.richfaces.test;


import org.eclipse.core.resources.IFile;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.vpe.ui.test.TestUtil;


/**
 * Test case for testing {@link RichFacesFileUploadTemplateTestCase}.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesFileUploadTemplateTestCase extends CommonRichFacesTestCase {

    /** The Constant COMPONENTS_FILE_UPLOAD_FILE_UPLOAD_BASE. */
    private static final String COMPONENTS_FILE_UPLOAD_FILE_UPLOAD_BASE = "components/fileUpload/fileUpload.xhtml";

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public RichFacesFileUploadTemplateTestCase(String name) {
        super(name);
        setCheckWarning(false);
    }

    /**
     * Test file upload.
     * 
     * @throws PartInitException the part init exception
     * @throws Throwable the throwable
     */
    public void testBaseFileUpload() throws PartInitException, Throwable {
        performTestForVpeComponent((IFile) TestUtil.getComponentPath(COMPONENTS_FILE_UPLOAD_FILE_UPLOAD_BASE,
                RichFacesComponentTest.IMPORT_PROJECT_NAME));
    }

}
