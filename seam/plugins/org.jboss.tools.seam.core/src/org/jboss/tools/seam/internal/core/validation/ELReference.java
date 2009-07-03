/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.parser.SyntaxError;
import org.jboss.tools.common.el.core.resolver.ElVarSearcher;
import org.jboss.tools.common.model.project.ext.ITextSourceReference;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.w3c.dom.Element;

/**
 * @author Alexey Kazakov
 */
public class ELReference implements ITextSourceReference {

	private static SeamELCompletionEngine elEngine = new SeamELCompletionEngine();

	private IFile resource;
	private IPath path;
	private int length;
	private int startPosition;
	private ELExpression[] el;
	private ElVarSearcher varSearcher;
	private Set<IMarker> markers;
	private IMarker[] markerArray;
	private boolean needToInitMarkers = false;

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamTextSourceReference#getLength()
	 */
	public int getLength() {
		return length;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamTextSourceReference#getStartPosition()
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @param startPosition
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * @return the resource
	 */
	public IFile getResource() {
		if(resource==null) {
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			resource = wsRoot.getFile(path);
		}
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(IFile resource) {
		this.resource = resource;
		if(resource!=null) {
			this.path = resource.getFullPath();
		}
	}

	/**
	 * @return the path
	 */
	public IPath getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(IPath path) {
		this.path = path;
	}

	/**
	 * @return the el
	 */
	public ELExpression[] getEl() {
		if(el==null) {
			Set<ELExpression> exps = new HashSet<ELExpression>();
			try {
				String content = FileUtil.readStream(getResource().getContents());
				String elText = content.substring(startPosition, startPosition + length);
				int startEl = elText.indexOf("#{"); //$NON-NLS-1$
				if(startEl>-1) {
					ELParser parser = ELParserUtil.getJbossFactory().createParser();
					ELModel model = parser.parse(elText);
					List<SyntaxError> errors = model.getSyntaxErrors();
					if(!errors.isEmpty()) {
						SeamCorePlugin.getDefault().logWarning("ELObject hold incorrect information. Maybe resource " + getResource() + " has been changed.");
						return null;
					}
					List<ELInstance> is = model.getInstances();
					for (ELInstance i : is) {
						if(!i.getErrors().isEmpty()) {
							SeamCorePlugin.getDefault().logWarning("ELObject hold incorrect information. Maybe resource " + getResource() + " has been changed.");
							continue;
						}
						exps.add(i.getExpression());
					}
				}
			} catch (CoreException e) {
				SeamCorePlugin.getDefault().logError(e);
			}
			el = exps.toArray(new ELExpression[0]);
		}
		return el;
	}

	/**
	 * @param el the el to set
	 */
	public void setEl(ELExpression[] el) {
		this.el = el;
	}

	/**
	 * @param insts
	 */
	public void setEl(List<ELInstance> insts) {
		Set<ELExpression> exps = new HashSet<ELExpression>();
		for (ELInstance el : insts) {
			exps.add(el.getExpression());
		}
		el = exps.toArray(new ELExpression[0]);
	}

	/**
	 * @return the varSearcher
	 */
	public ElVarSearcher getVarSearcher() {
		if(varSearcher == null) {
			varSearcher = new ElVarSearcher(getResource(), elEngine);
		}
		return varSearcher;
	}

	/**
	 * @param varSearcher the varSearcher to set
	 */
	public void setVarSearcher(ElVarSearcher varSearcher) {
		this.varSearcher = varSearcher;
	}

	private IMarker[] EMPTY_MARKER_ARRAY = new IMarker[0];

	private void initMarkers() {
		if(markers==null) {
			markers = new HashSet<IMarker>();
			if(needToInitMarkers) {
				IFile file = getResource();
				if(file!=null) {
					IMarker[] markers = null;
					try {
						markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
					} catch (CoreException e) {
						SeamCorePlugin.getDefault().logError(e);
					}
					for(int i=0; i<markers.length; i++){
						String groupName = markers[i].getAttribute("groupName", null); //$NON-NLS-1$
						if(groupName!=null && (groupName.equals(groupName.equals(ISeamValidator.MARKED_SEAM_RESOURCE_MESSAGE_GROUP)))) {
							int start = markers[i].getAttribute(IMarker.CHAR_START, -1);
							int end = markers[i].getAttribute(IMarker.CHAR_END, -1);
							if(start>=startPosition && end<=startPosition+length) {
								addMarker(markers[i]);
							}
						}
					}
				}
			}
			needToInitMarkers = false;
		}
	}

	/**
	 * @param needToInitMarkers the needToInitMarkers to set
	 */
	public synchronized void setNeedToInitMarkers(boolean needToInitMarkers) {
		this.needToInitMarkers = needToInitMarkers;
	}

	public synchronized void setMarkers(Set<IMarker> markers) {
		this.markers = markers;
	}

	/**
	 * @return the markers
	 */
	public synchronized IMarker[] getMarkers() {
		initMarkers();
		if(markerArray==null) {
			if(markers.isEmpty()) {
				markerArray = EMPTY_MARKER_ARRAY;
			} else {
				markerArray = markers.toArray(new IMarker[0]);
			}
		}
		return markerArray;
	}

	/**
	 * @param markers the markers to set
	 */
	public synchronized void addMarker(IMarker marker) {
		if(marker==null) {
			return;
		}
		markerArray = null;
		if(markers==null) {
			markers = new HashSet<IMarker>();
		}
		markers.add(marker);
	}

	/**
	 * Removes all markers from this EL.
	 */
	public synchronized void deleteMarkers() {
		initMarkers();
		for (IMarker marker : markers) {
			try {
				marker.delete();
			} catch (CoreException e) {
				SeamCorePlugin.getDefault().logError(e);
			}
		}
		markers.clear();				
		markerArray = null;
	}

	/**
	 * Store this EL into XML element.
	 * @param element
	 */
	public synchronized void store(Element element) {
		element.setAttribute("path", path.toString()); //$NON-NLS-1$
		element.setAttribute("offset", "" + startPosition); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("length", "" + length); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Load this EL from XML element.
	 * @param element
	 */
	public synchronized void load(Element element) {
		path = new Path(element.getAttribute("path")); //$NON-NLS-1$
		startPosition = new Integer(element.getAttribute("offset")); //$NON-NLS-1$
		length = new Integer(element.getAttribute("length")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		ELReference el = (ELReference)obj;
		return this.path.equals(el.path) && this.startPosition == el.startPosition;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return path.hashCode() + startPosition;
	}
}