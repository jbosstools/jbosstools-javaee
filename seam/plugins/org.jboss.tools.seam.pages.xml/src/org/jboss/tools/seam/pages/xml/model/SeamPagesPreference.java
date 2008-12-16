package org.jboss.tools.seam.pages.xml.model;

import org.jboss.tools.common.model.options.Preference;
import org.jboss.tools.jst.web.WebPreference;

public class SeamPagesPreference extends WebPreference {
	public static String SEAM_PAGES_EDITOR_PATH = "%Options%/Struts Studio/Editors/Seam Pages Diagram";

	public static final Preference ENABLE_CONTROL_MODE_ON_TRANSITION_COMPLETED = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "enableControlModeOnTransitionCompleted");
	public static final Preference SHOW_SHORTCUT_ICON = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "showShortcutIcon");
	public static final Preference SHOW_SHORTCUT_PATH = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "showShortcutPath");
	public static final Preference SHOW_GRID = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "Show Grid");
	public static final Preference GRID_STEP = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "Grid Step");

	protected SeamPagesPreference(String optionPath, String attributeName) {
		super(optionPath, attributeName);
	}

}
