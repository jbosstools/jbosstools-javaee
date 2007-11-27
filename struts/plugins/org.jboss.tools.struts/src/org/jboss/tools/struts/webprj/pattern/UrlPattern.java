/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.webprj.pattern;

import java.util.*;

public interface UrlPattern {
	public boolean isActionUrl(String path);
	public String getActionPath(String url);
	public String getActionUrl(String path);
	public String getModule(String path, Set modules, String thisModule);
	public String getContextRelativePath(String path, String module);
	public String getModuleRelativePath(String path, String module);

}
