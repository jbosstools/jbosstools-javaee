package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.files.handlers.CreateFileSupport;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesDiagramImpl;

public class CreateSeamPagesSupport extends CreateFileSupport {

	public CreateSeamPagesSupport() {}

	protected void execute() throws XModelException {
		Properties p = extractStepData(0);
		String path = p.getProperty("name");
		path = revalidatePath(path);
		XModelObject file = createFile(path);
		if(file == null) return;		

		SeamPagesDiagramImpl diagram = (SeamPagesDiagramImpl)file.getChildByPath(SeamPagesConstants.ELM_DIAGRAM);
		diagram.firePrepared();

//		register(file.getParent(), file, p0);

		open(file);	
	}

}
