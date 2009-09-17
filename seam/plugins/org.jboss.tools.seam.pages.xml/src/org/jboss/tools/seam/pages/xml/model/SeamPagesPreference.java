package org.jboss.tools.seam.pages.xml.model;

import org.eclipse.swt.graphics.Font;
import org.jboss.tools.common.model.options.Preference;
import org.jboss.tools.jst.web.WebPreference;

public class SeamPagesPreference extends WebPreference {
	public static String SEAM_PAGES_EDITOR_PATH = Preference.EDITOR_PATH + "/Seam Pages Diagram";

	public static final Preference ENABLE_CONTROL_MODE_ON_TRANSITION_COMPLETED = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "enableControlModeOnTransitionCompleted");
	public static final Preference SHOW_SHORTCUT_ICON = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "showShortcutIcon");
	public static final Preference SHOW_SHORTCUT_PATH = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "showShortcutPath");
	public static final Preference SHOW_GRID = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "Show Grid");
	public static final Preference GRID_STEP = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "Grid Step");
	public static final Preference LINK_PATH_FONT = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "Link Path Font");
	public static final Preference VIEW_PATH_FONT = new SeamPagesPreference(SEAM_PAGES_EDITOR_PATH, "View Path Font");

	protected SeamPagesPreference(String optionPath, String attributeName) {
		super(optionPath, attributeName);
	}
	
	public static Font getFont(String preferenceValue, Font font) {
		String name;
		int size = 8, style = 1;
		int pos, pos2, pos3;
		pos = preferenceValue.indexOf(",");
		if (pos < 0)
			name = preferenceValue;
		else {
			name = preferenceValue.substring(0, pos);
			pos2 = preferenceValue.indexOf("size=");
			if (pos2 >= 0) {
				pos3 = preferenceValue.indexOf(",", pos2);
				if (pos3 < 0)
					size = Integer.parseInt(preferenceValue
							.substring(pos2 + 5, preferenceValue.length()));
				else
					size = Integer.parseInt(preferenceValue.substring(pos2 + 5, pos3));
			}
			pos2 = preferenceValue.indexOf("style=");
			if (pos2 >= 0) {
				pos3 = preferenceValue.indexOf(",", pos2);
				if (pos3 < 0)
					style = Integer.parseInt(preferenceValue.substring(pos2 + 6, preferenceValue
							.length()));
				else
					style = Integer.parseInt(preferenceValue.substring(pos2 + 6, pos3));
			}

		}
		
		if (font == null) {
			font = new Font(null, name, size, style);
		} else {
			if (!font.getFontData()[0].getName().equals(name)
					|| font.getFontData()[0].getHeight() != size
					|| font.getFontData()[0].getStyle() != style) {
				font = new Font(null, name, size, style);
			}
		}
		
		return font;
	}
}
