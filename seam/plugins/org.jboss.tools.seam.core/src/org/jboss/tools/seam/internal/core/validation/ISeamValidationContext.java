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

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.w3c.dom.Element;

/**
 * Contains information for seam validators that must be saved between
 * validation invoking.
 * @author Alexey Kazakov
 */
public interface ISeamValidationContext {

	/**
	 * Save link between core resource and variable name.
	 * It's needed for incremental validation because we must save all linked resources of changed java file.
	 */
	void addLinkedCoreResource(String variableName, IPath linkedResourcePath, boolean declaration);

	/**
	 * Removes link between core resource and variable name.
	 * @param oldVariableName
	 * @param linkedResourcePath
	 */
	void removeLinkedCoreResource(String name, IPath linkedResourcePath);

	/**
	 * Removes link between core resources and variable names.
	 * @param linkedResources
	 */
	void removeLinkedCoreResources(Set<IPath> resources);

	/**
	 * Removes link between core resource and variable names.
	 * @param linkedResource
	 */
	void removeLinkedCoreResource(IPath resource);

	Set<IPath> getCoreResourcesByVariableName(String variableName, boolean declaration);

	Set<String> getVariableNamesByCoreResource(IPath fullPath, boolean declaration);

	/**
	 * Adds core resource without any link to any context variable name.
	 * @param fullPath
	 */
	void addUnnamedCoreResource(IPath fullPath);

	/**
	 * @return Set of coreresources without any link to any context variable name.
	 * @param fullPath
	 */
	Set<IPath> getUnnamedCoreResources();

	/**
	 * Removes unnamed EL resource.
	 * @param fullPath
	 */
	void removeUnnamedCoreResource(IPath fullPath);

	/**
	 * Adds EL resource without any link to any context variable name.
	 * @param fullPath
	 */
	void addUnnamedElResource(IPath fullPath);

	/**
	 * @return Set of EL resources without any link to any context variable name.
	 * @param fullPath
	 */
	Set<IPath> getUnnamedElResources();

	/**
	 * Removes unnamed EL resource.
	 * @param fullPath
	 */
	void removeUnnamedElResource(IPath fullPath);

	/**
	 * We should validate all EL resources which use these names.
	 * @param name
	 */
	void addVariableNameForELValidation(String name);

	void removeLinkedEls(Set<IFile> resorces);

	/**
	 * Get ELs which should be validated
	 * @param changedFiles
	 * @param onlyChangedVariables
	 * @return
	 */
	Set<ELReference> getElsForValidation(Set<IFile> changedFiles, boolean onlyChangedVariables);

	void clearAll();

	void clearAllResourceLinks();

	void clearRegisteredFiles();

	void clearElResourceLinks();

	void clearOldVariableNameForElValidation();

	/**
	 * Save link between EL and variable name.
	 * @param variableName
	 * @param el
	 */
	void addLinkedEl(String variableName, ELReference el);

	/**
	 * Removes link between EL and variable name.
	 * @param name
	 * @param el
	 */
	void removeLinkedEl(String name, ELReference el);

	/**
	 * Return ELs with given variable name
	 * @param variableName
	 * @return
	 */
	Set<ELReference> getElsByVariableName(String variableName);

	/**
	 * Stores context to XML element
	 * @param root
	 */
	void store(Element root);

	/**
	 * Loads context from XML element
	 * @param root
	 */
	void load(Element root);

	Set<IFile> getRemovedFiles();

	void addRemovedFile(IFile file);

	Set<IFile> getRegisteredFiles();

	void registerFile(IFile file);
}