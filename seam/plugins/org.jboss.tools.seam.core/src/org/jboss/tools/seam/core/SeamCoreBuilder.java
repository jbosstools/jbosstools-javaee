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
package org.jboss.tools.seam.core;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.tools.seam.internal.core.SeamComponent;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.java.JavaScanner;
import org.jboss.tools.seam.internal.core.scanner.lib.LibraryScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SeamCoreBuilder extends IncrementalProjectBuilder {
	static IFileScanner[] FILE_SCANNERS = {
		new JavaScanner(), 
		new XMLScanner(), 
		new LibraryScanner()
	};
	SampleResourceVisitor RESOURCE_VISITOR = new SampleResourceVisitor();
	
	SeamProject getSeamProject() {
		IProject p = getProject();
		try {
			return p == null ? null : (SeamProject)p.getNature(ISeamProject.NATURE_ID);
		} catch (CoreException e) {
			//TODO
			return null;
		}
	}

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				RESOURCE_VISITOR.visit(resource);
				break;
			case IResourceDelta.REMOVED:
				SeamProject p = getSeamProject();
				if(p != null) p.pathRemoved(resource.getFullPath());
				break;
			case IResourceDelta.CHANGED:
				RESOURCE_VISITOR.visit(resource);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			if(resource instanceof IFile) {
				IFile f = (IFile)resource;
				for (int i = 0; i < FILE_SCANNERS.length; i++) {
					IFileScanner scanner = FILE_SCANNERS[i];
					if(scanner.isRelevant(f)) {
						if(!scanner.isLikelyComponentSource(f)) return false;
						SeamComponentDeclaration[] c = null;
						try {
							c = scanner.parse(f);
						} catch (Exception e) {
							SeamCorePlugin.getDefault().logError(e);
						}
						if(c != null) componentsLoaded(c, f);
					}
				}
			}
			//return true to continue visiting children.
			return true;
		}
	}
	
	void componentsLoaded(SeamComponentDeclaration[] c, IFile resource) {
		if(c == null || c.length == 0) return;
		SeamProject p = getSeamProject();
		if(p == null) return;
		p.registerComponents(c, resource.getFullPath());
	}

	class XMLErrorHandler extends DefaultHandler {
		
		private IFile file;

		public XMLErrorHandler(IFile file) {
			this.file = file;
		}

		private void addMarker(SAXParseException e, int severity) {
			SeamCoreBuilder.this.addMarker(file, e.getMessage(), e
					.getLineNumber(), severity);
		}

		public void error(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void warning(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "org.jboss.tools.seam.core.seam.core";

	private static final String MARKER_TYPE = "org.jboss.tools.seam.core.xmlProblem";

	private SAXParserFactory parserFactory;

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	/*
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void checkXML(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".xml")) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			XMLErrorHandler reporter = new XMLErrorHandler(file);
			try {
				getParser().parse(file.getContents(), reporter);
			} catch (Exception e1) {
			}
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(RESOURCE_VISITOR);
		} catch (CoreException e) {
		}
	}

	private SAXParser getParser() throws ParserConfigurationException,
			SAXException {
		if (parserFactory == null) {
			parserFactory = SAXParserFactory.newInstance();
		}
		return parserFactory.newSAXParser();
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}
	
	/**
	 * Access to xml scanner for test.
	 * @return
	 */
	public static IFileScanner getXMLScanner() {
		return new XMLScanner();
	}

	/**
	 * Access to java scanner for test.
	 * @return
	 */
	public static IFileScanner getJavaScanner() {
		return new JavaScanner();
	}

	/**
	 * Access to library scanner for test.
	 * @return
	 */
	public static IFileScanner getLibraryScanner() {
		return new LibraryScanner();
	}

}
