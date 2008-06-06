package org.jboss.tools.seam.ui.pages.editor.figures.xpl;

/** based on code in JavaElementLabel */
public class CompressNameUtil {

	/*
	 * Package name compression
	 */
	private static String fgNamePattern= ""; //$NON-NLS-1$
	private static String fgNamePrefix;
	private static String fgNamePostfix;
	private static int fgNameChars;
	private static int fgNameLength= -1;
	
	public static String getCompressedName(String className) {
		StringBuffer result = new StringBuffer();
		refreshPackageNamePattern();
		if (fgNameLength == 0) {
			result.append(className);
			return result.toString();
		}
		String name= className;
		int start= 0;
		int dot= name.indexOf('.', start);
		while (dot > 0) {
			if (dot - start > fgNameLength-1) {
				result.append(fgNamePrefix);
				if (fgNameChars > 0)
					result.append(name.substring(start, Math.min(start+ fgNameChars, dot)));
				result.append(fgNamePostfix);
			} else
				result.append(name.substring(start, dot + 1));
			start= dot + 1;
			dot= name.indexOf('.', start);
		}
		result.append(name.substring(start));
		return result.toString();
	}
	
	private static void refreshPackageNamePattern() {
		String pattern= "1.";//getPkgNamePatternForPackagesView(); TODO: put in a preference somewhere
		final String EMPTY_STRING= ""; //$NON-NLS-1$
		if (pattern.equals(fgNamePattern))
			return;
		else if (pattern.length() == 0) {
			fgNamePattern= EMPTY_STRING;
			fgNameLength= -1;
			return;
		}
		fgNamePattern= pattern;
		int i= 0;
		fgNameChars= 0;
		fgNamePrefix= EMPTY_STRING;
		fgNamePostfix= EMPTY_STRING;
		while (i < pattern.length()) {
			char ch= pattern.charAt(i);
			if (Character.isDigit(ch)) {
				fgNameChars= ch-48;
				if (i > 0)
					fgNamePrefix= pattern.substring(0, i);
				if (i >= 0)
					fgNamePostfix= pattern.substring(i+1);
				fgNameLength= fgNamePrefix.length() + fgNameChars + fgNamePostfix.length();
				return;
			}
			i++;
		}
		fgNamePrefix= pattern;
		fgNameLength= pattern.length();
	}

}
