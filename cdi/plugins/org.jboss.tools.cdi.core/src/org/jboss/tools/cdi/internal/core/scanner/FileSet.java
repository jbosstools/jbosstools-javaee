package org.jboss.tools.cdi.internal.core.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;

public class FileSet {
	Set<IFile> nonmodel = new HashSet<IFile>();
	Map<IFile, ICompilationUnit> annotations = new HashMap<IFile, ICompilationUnit>();
	Map<IFile, ICompilationUnit> interfaces = new HashMap<IFile, ICompilationUnit>();
	Map<IFile, ICompilationUnit> classes = new HashMap<IFile, ICompilationUnit>();
	IFile beansXML = null;

	public Set<IFile> getNonModelFiles() {
		return nonmodel;
	}

	public Map<IFile, ICompilationUnit> getAnnotations() {
		return annotations;
	}

	public Map<IFile, ICompilationUnit> getInterfaces() {
		return interfaces;
	}

	public Map<IFile, ICompilationUnit> getClasses() {
		return classes;
	}

	public IFile getBeanXML() {
		return beansXML;
	}

	public void setBeanXML(IFile f) {
		beansXML = f;
	}
}
