/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package demo;

import javax.persistence.Entity;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.Component;

/**
 * Created by JBoss Developer Studio
 */

@Name("installWithoutPrecedence_JBIDE_2052")
@Scope(ScopeType.APPLICATION)
@Entity
@Install(false)
public class InstallTest {
	
}
