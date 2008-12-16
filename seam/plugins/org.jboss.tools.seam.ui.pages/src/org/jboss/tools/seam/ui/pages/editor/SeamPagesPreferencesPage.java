package org.jboss.tools.seam.ui.pages.editor;

import org.jboss.tools.common.model.ui.preferences.XMOBasedPreferencesPage;
import org.jboss.tools.common.model.ui.util.ModelUtilities;
import org.jboss.tools.seam.pages.xml.model.SeamPagesPreference;

public class SeamPagesPreferencesPage extends XMOBasedPreferencesPage {

	public SeamPagesPreferencesPage() {
		super(ModelUtilities.getPreferenceModel().getByPath(SeamPagesPreference.SEAM_PAGES_EDITOR_PATH));
	}

}
