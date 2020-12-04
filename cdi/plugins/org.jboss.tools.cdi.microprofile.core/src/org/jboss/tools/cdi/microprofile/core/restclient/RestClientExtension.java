/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.microprofile.core.restclient;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature;

/**
 * @author Red Hat Developers
 *
 */
public class RestClientExtension implements ICDIExtension, IInjectionPointValidatorFeature {
  

  public static final String MICROPROFILE_REST_CLIENT_REST_CLIENT_TYPE = "org.eclipse.microprofile.rest.client.inject.RestClient";

  @Override
  public boolean shouldIgnoreInjection(IType typeOfInjectionPoint, IInjectionPoint injection) {
    return injection.getAnnotation(MICROPROFILE_REST_CLIENT_REST_CLIENT_TYPE) != null;
  }
}
