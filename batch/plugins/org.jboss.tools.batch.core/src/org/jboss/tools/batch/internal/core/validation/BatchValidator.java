/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.validation;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProjectFactory;
import org.jboss.tools.batch.internal.core.preferences.BatchSeverityPreferences;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.validation.ContextValidationHelper;
import org.jboss.tools.common.validation.EditorValidationContext;
import org.jboss.tools.common.validation.IPreferenceInfo;
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IStringValidator;
import org.jboss.tools.common.validation.ITypedReporter;
import org.jboss.tools.common.validation.IValidatingProjectSet;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.common.validation.PreferenceInfoManager;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.common.validation.internal.ProjectValidationContext;
import org.jboss.tools.common.validation.internal.SimpleValidatingProjectTree;
import org.jboss.tools.common.validation.internal.ValidatingProjectSet;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchValidator extends KBValidator implements BatchConstants, IStringValidator {
	public static final String ID = "org.jboss.tools.batch.validator.BatchValidator"; //$NON-NLS-1$
	public static String SHORT_ID = "batch-verification"; //$NON-NLS-1$
	public static final String PREFERENCE_PAGE_ID = "org.jboss.tools.batch.ui.preferences.BatchValidationPreferencePage"; //$NON-NLS-1$
	public static final String PROPERTY_PAGE_ID = "org.jboss.tools.batch.ui.propertyPages.BatchValidationPreferencePage"; //$NON-NLS-1$
	
	String projectName;
	Map<IProject, IProjectValidationContext> contexts = new HashMap<IProject, IProjectValidationContext>();

	public BatchValidator() {}

	@Override
	protected String getPreference(IProject project, String preferenceKey) {
		return BatchSeverityPreferences.getInstance().getProjectPreference(project, preferenceKey);
	}

	@Override
	public int getMaxNumberOfMarkersPerFile(IProject project) {
		return BatchSeverityPreferences.getMaxNumberOfProblemMarkersPerFile(project);
	}

	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext context, IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, context, manager, reporter);
		setAsYouTypeValidation(false);
		projectName = project.getName();
		contexts.clear();
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getBuilderId() {
		return KbBuilder.BUILDER_ID;
	}

	@Override
	public IValidatingProjectTree getValidatingProjects(IProject project) {
		IProjectValidationContext rootContext = contexts.get(project);
		if(rootContext == null) {
			IKbProject kb = KbProjectFactory.getKbProject(project, true);
			if(kb != null) {
				rootContext = kb.getValidationContext();
			} else {
				rootContext = new ProjectValidationContext();
			}
			contexts.put(project, rootContext);
		}

		Set<IProject> projects = new HashSet<IProject>();
		projects.add(project);

		IValidatingProjectSet projectSet = new ValidatingProjectSet(project, projects, rootContext);
		return new SimpleValidatingProjectTree(projectSet);
	}

	@Override
	public boolean shouldValidate(IProject project) {
		return shouldValidate(project, false);
	}

	@Override
	public boolean shouldValidateAsYouType(IProject project) {
		return shouldValidate(project, true);
	}

	public boolean shouldValidate(IProject project, boolean asYouType) {
		try {
			return project.isAccessible() 
					&& BatchCorePlugin.getBatchProject(project, true) != null 
					&& isEnabled(project)
					&& (asYouType || validateBuilderOrder(project));
		} catch (CoreException e) {
			BatchCorePlugin.pluginLog().logError(e);
		}
		return false;
	}

	/**
	 * This method is present for completeness sake. Currently, Batch validat
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	private boolean validateBuilderOrder(IProject project) throws CoreException {
		return true;
	}

	@Override
	public IStatus validate(Set<IFile> changedFiles, IProject project,
			ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, context, manager, reporter);
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);

		Set<IPath> resourcesToClean = new HashSet<IPath>(); // Resource which we should remove from validation context
		for (IFile file: changedFiles) {
			resourcesToClean.add(file.getFullPath());
		}

		changedFiles = collectFiles(project, changedFiles, context);
		
		for(IFile file: changedFiles) {
			removeAllMessagesFromResource(file);
			resourcesToClean.add(file.getFullPath());
		}

		getValidationContext().removeLinkedCoreResources(SHORT_ID, resourcesToClean);

		Set<IFile> batchJobs = batchProject.getDeclaredBatchJobs();
		for (IFile file: changedFiles) {
			if(batchJobs.contains(file)) {
				validateJobFile(batchProject, file);
			}
			//TODO Java
		}

		cleanSavedMarkers();
		return OK_STATUS;
	}

	@Override
	public IStatus validateAll(IProject project,
			ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, context, manager, reporter);
		displaySubtask(BatchValidationMessages.VALIDATING_PROJECT, new String[]{projectName});
		
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		if(batchProject != null) {
			Set<IFile> batchJobs = batchProject.getDeclaredBatchJobs();
			for (IFile file: batchJobs) {
				validateJobFile(batchProject, file);
			}
			//TODO Java
		}

		cleanSavedMarkers();
		return OK_STATUS;
	}

	@Override
	public void validate(IValidator validatorManager, IProject rootProject, Collection<IRegion> dirtyRegions,
			IValidationContext helper, IReporter reporter, EditorValidationContext validationContext,
			IProjectValidationContext projectContext, IFile file) {
		ContextValidationHelper validationHelper = new ContextValidationHelper();
		validationHelper.setProject(rootProject);
		validationHelper.setValidationContextManager(validationContext);
		init(rootProject, validationHelper, projectContext, validatorManager, reporter);
		setAsYouTypeValidation(true);
		asYouTypeTimestamp++;
		this.document = validationContext.getDocument();

		IBatchProject batchProject = BatchCorePlugin.getBatchProject(file.getProject(), true);
		 if(batchProject != null && batchProject.getDeclaredBatchJobs().contains(file)) {
			 validateJobFile(batchProject, file);
		 }

		 // TODO other kinds of file

		if(reporter instanceof ITypedReporter) {
			((ITypedReporter)reporter).addTypeForFile(getProblemType());
		}
		disableProblemAnnotations(new IRegion() {
				@Override
				public int getOffset() {
					return 0;
				}
				
				@Override
				public int getLength() {
					return document.getLength();
				}
			}, reporter);
		
	}

	private void validateJobFile(IBatchProject batchProject, IFile file) {
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager != null) {
			IStructuredModel model = null;
			try {
				model = file != null ? 
						manager.getModelForRead(file) : 
							manager.getExistingModelForRead(document);
				if (model instanceof IDOMModel) {
					IDOMModel domModel = (IDOMModel) model;
					IDOMDocument document = domModel.getDocument();
					if(document != null) {
						Element element = document.getDocumentElement();
						if(TAG_JOB.equals(element.getNodeName())) {
							validateJobElement(batchProject, file, element);
						}
					}
				}
			} catch (CoreException e) {
				WebKbPlugin.getDefault().logError(e);
			} catch (IOException e) {
				WebKbPlugin.getDefault().logError(e);
			} finally {
				if (model != null) {
					model.releaseFromRead();
				}
			}
		}
	}

	IMarker addProblem(String message, String preferenceKey, Element element, String attr, IFile file, int quickfixId) {
		SimpleReference ref = new SimpleReference(element, attr, file);
		if(quickfixId == -1) {
			return addProblem(message, preferenceKey, new String[]{element.getAttribute(attr).trim()}, ref.getLength(), ref.getStartPosition(), file);
		} else {
			return addProblem(message, preferenceKey, new String[]{element.getAttribute(attr).trim()}, ref.getLength(), ref.getStartPosition(), file, quickfixId);
		}
	}

	private void validateJobElement(IBatchProject batchProject, IFile file, Element job) {
		ContextProperties cp = new ContextProperties(null, job, file);

		Element listeners = XMLUtilities.getUniqueChild(job, TAG_LISTENERS);
		if(listeners != null) {
			for (Element listener: XMLUtilities.getChildren(listeners, TAG_LISTENER)) {
				validateRefAndProperties(batchProject, file, cp, listener, BatchArtifactType.JOB_LISTENER, 
						BatchValidationMessages.JOB_LISTENER_IS_NOT_FOUND, BatchValidationMessages.JOB_LISTENER_IS_EXPECTED);
			}
		}

		//Job as flow has children decision, flow, split, step.
		validateFlowElement(batchProject, file, cp, job, null);
		
		cp.complete(null);
	}

	static String[] EXECUTION_ELEMENTS = {TAG_DECISION, TAG_FLOW, TAG_SPLIT, TAG_STEP};

	private void validateFlowElement(IBatchProject batchProject, IFile file, ContextProperties cp, Element flow, JobTransitionsValidator jobTransitions) {
		TransitionsValidator transitionValidator = null;
		if(jobTransitions == null) {
			transitionValidator = jobTransitions = new JobTransitionsValidator(this);
		} else {
			transitionValidator = new TransitionsValidator(this, jobTransitions);
		}
		for (String tag: EXECUTION_ELEMENTS) {
			for (Element decision: XMLUtilities.getChildren(flow, tag)) {
				transitionValidator.addFlowElement(decision);
			}
		}

		transitionValidator.validate(file);

		for (Element decision: XMLUtilities.getChildren(flow, TAG_DECISION)) {
			validateDecisionElement(batchProject, file, cp, decision);
		}
		for (Element flow1: XMLUtilities.getChildren(flow, TAG_FLOW)) {
			validateFlowElement(batchProject, file, cp, flow1, jobTransitions);
		}
		for (Element split: XMLUtilities.getChildren(flow, TAG_SPLIT)) {
			validateFlowElement(batchProject, file, cp, split, jobTransitions);
//			for (Element flow1: XMLUtilities.getChildren(split, TAG_FLOW)) {
//				validateFlowElement(batchProject, file, cp, flow1, jobTransitions);
//			}
		}
		for (Element step: XMLUtilities.getChildren(flow, TAG_STEP)) {
			validateStepElement(batchProject, file, cp, step);
		}

	}

	private void validateStepElement(IBatchProject batchProject, IFile file, ContextProperties cp, Element step) {
		ContextProperties cp1 = new ContextProperties(cp, step, file);

		Element batchlet = XMLUtilities.getUniqueChild(step, TAG_BATCHLET);
		Element chunk = XMLUtilities.getUniqueChild(step, TAG_CHUNK);
		if(batchlet != null) {
			validateBatchletElement(batchProject, file, cp1, batchlet);
		} else if(chunk != null) {
			validateChunkElement(batchProject, file, cp1, chunk);
		}
		Element partition = XMLUtilities.getUniqueChild(step, TAG_PARTITION);
		if(partition != null) {
			validatePartitionElement(batchProject, file, cp1, partition);
		}

		Element listeners = XMLUtilities.getUniqueChild(step, TAG_LISTENERS);
		if(listeners != null) {
			for (Element listener: XMLUtilities.getChildren(listeners, TAG_LISTENER)) {
				String ref = listener.getAttribute(ATTR_REF);
				if(ref != null && ref.trim().length() > 0) {
					Collection<IBatchArtifact> as = batchProject.getArtifacts(ref.trim());
					if(as.isEmpty()) {
						addProblem(BatchValidationMessages.STEP_LISTENER_IS_NOT_FOUND, BatchSeverityPreferences.UNKNOWN_ARTIFACT_NAME, listener, ATTR_REF, file, -1);
					} else {
						IBatchArtifact a = as.iterator().next();
						boolean isCorrectType = 
								(chunk != null) ? a.getArtifactType().getTag().equals(TAG_STEP)
								: a.getArtifactType().equals(BatchArtifactType.STEP_LISTENER);
						if(!isCorrectType) {
							addProblem(BatchValidationMessages.STEP_LISTENER_IS_EXPECTED, BatchSeverityPreferences.WRONG_ARTIFACT_TYPE, listener, ATTR_REF, file, -1);
						}
						validateProperties(batchProject, file, cp1, listener, a);
					}
				}
			}
		}
		
		cp1.complete(null);
	}

	private void validatePartitionElement(IBatchProject batchProject, IFile file, ContextProperties cp, Element partition) {
		validateChildRefAndProperties(batchProject, file, cp, partition, BatchArtifactType.PARTITION_MAPPER, 
				BatchValidationMessages.MAPPER_IS_NOT_FOUND, BatchValidationMessages.MAPPER_IS_EXPECTED);
		validateChildRefAndProperties(batchProject, file, cp, partition, BatchArtifactType.PARTITION_ANALYZER, 
				BatchValidationMessages.ANALYZER_IS_NOT_FOUND, BatchValidationMessages.ANALYZER_IS_EXPECTED);
		validateChildRefAndProperties(batchProject, file, cp, partition, BatchArtifactType.PARTITION_COLLECTOR, 
				BatchValidationMessages.COLLECTOR_IS_NOT_FOUND, BatchValidationMessages.COLLECTOR_IS_EXPECTED);
		validateChildRefAndProperties(batchProject, file, cp, partition, BatchArtifactType.PARTITION_REDUCER, 
				BatchValidationMessages.REDUCER_IS_NOT_FOUND, BatchValidationMessages.REDUCER_IS_EXPECTED);
	}

	private void validateDecisionElement(IBatchProject batchProject, IFile file, ContextProperties cp, Element decision) {
		validateRefAndProperties(batchProject, file, cp, decision, BatchArtifactType.DECIDER, 
				BatchValidationMessages.DECIDER_IS_NOT_FOUND, BatchValidationMessages.DECIDER_IS_EXPECTED);
	}

	private void validateBatchletElement(IBatchProject batchProject, IFile file, ContextProperties cp, Element batchlet) {
		validateRefAndProperties(batchProject, file, cp, batchlet, BatchArtifactType.BATCHLET, 
				BatchValidationMessages.BATCHLET_IS_NOT_FOUND, BatchValidationMessages.BATCHLET_IS_EXPECTED);
	}

	private void validateChunkElement(IBatchProject batchProject, IFile file, 
			ContextProperties cp, Element chunk) {
		//Reader
		validateChildRefAndProperties(batchProject, file, cp, chunk, BatchArtifactType.ITEM_READER, 
				BatchValidationMessages.READER_IS_NOT_FOUND, BatchValidationMessages.READER_IS_EXPECTED);
		//Writer
		validateChildRefAndProperties(batchProject, file, cp, chunk, BatchArtifactType.ITEM_WRITER, 
				BatchValidationMessages.WRITER_IS_NOT_FOUND, BatchValidationMessages.WRITER_IS_EXPECTED);
		//Processor
		validateChildRefAndProperties(batchProject, file, cp, chunk, BatchArtifactType.ITEM_PROCESSOR, 
				BatchValidationMessages.PROCESSOR_IS_NOT_FOUND, BatchValidationMessages.PROCESSOR_IS_EXPECTED);
		//Checkpoint algorithm
		validateChildRefAndProperties(batchProject, file, cp, chunk, BatchArtifactType.CHECKPOINT_ALGORITHM, 
				BatchValidationMessages.CHECKPOINT_ALGORITHM_IS_NOT_FOUND, BatchValidationMessages.CHECKPOINT_ALGORITHM_IS_EXPECTED);

		validateExceptions(batchProject, file, chunk, TAG_SKIPPABLE_EXCEPTION_CLASSES);
		validateExceptions(batchProject, file, chunk, TAG_RETRYABLE_EXCEPTION_CLASSES);
		validateExceptions(batchProject, file, chunk, TAG_NO_ROLLBACK_EXCEPTION_CLASSES);

	}

	private void validateChildRefAndProperties(IBatchProject batchProject, IFile file, 
			ContextProperties cp, Element element,
			BatchArtifactType type, String notFoundMessage, String wrongTypeMessage) {
		Element child = XMLUtilities.getUniqueChild(element, type.getTag());
		if(child != null) {
			validateRefAndProperties(batchProject, file, cp, child, type, notFoundMessage, wrongTypeMessage);
		}
	}

	private void validateRefAndProperties(IBatchProject batchProject, IFile file, 
			ContextProperties cp, Element element, 
			BatchArtifactType type, String notFoundMessage, String wrongTypeMessage) {
		String ref = element.getAttribute(ATTR_REF);
		if(ref != null && ref.trim().length() > 0) {
			Collection<IBatchArtifact> as = batchProject.getArtifacts(ref.trim());
			if(as.isEmpty()) {
				addProblem(notFoundMessage, BatchSeverityPreferences.UNKNOWN_ARTIFACT_NAME, element, ATTR_REF, file, -1);
			} else {
				IBatchArtifact a = as.iterator().next();
				if(!a.getArtifactType().equals(type)) {
					addProblem(wrongTypeMessage, BatchSeverityPreferences.WRONG_ARTIFACT_TYPE, element, ATTR_REF, file, -1);
				}
				validateProperties(batchProject, file, cp, element, a);
				if(!isAsYouTypeValidation()) {
					getValidationContext().addLinkedCoreResource(SHORT_ID, a.getType().getResource().getFullPath().toString(), file.getFullPath(), true);
				}
			}
			if(!isAsYouTypeValidation()) {
				getValidationContext().addLinkedCoreResource(SHORT_ID, ref, file.getFullPath(), true);
			}
		}
	}

	static String JOB_PROPERTY_CALL_START = "#{jobProperties['";
	static String JOB_PROPERTY_CALL_END = "'";

	private void validateProperties(IBatchProject batchProject, IFile file, ContextProperties cp, Element parent, IBatchArtifact a) {
		new ContextProperties(cp, parent, file).complete(a);
	}

	@Override
	public boolean isEnabled(IProject project) {
		return BatchSeverityPreferences.isValidationEnabled(project);
	}

	private void validateExceptions(IBatchProject batchProject, IFile file, 
			Element element, String tagName) {
		Element child = XMLUtilities.getUniqueChild(element, tagName);
		if(child != null) {
			for (String tagName1: new String[]{TAG_INCLUDE, TAG_EXCLUDE}) {
				Element[] es = XMLUtilities.getChildren(child, tagName1);
				for (Element e: es) {
					String className = e.getAttribute(ATTR_CLASS).trim();
					IType type = ((BatchProject)batchProject).getType(className);
					if(type == null) {
						addProblem(BatchValidationMessages.EXCEPTION_CLASS_IS_NOT_FOUND, 
							BatchSeverityPreferences.UNKNOWN_EXCEPTION_CLASS, e, ATTR_CLASS, file, -1);
					} else {
						ParametedType pt = ((BatchProject)batchProject).getTypeFactory().newParametedType(type);
						boolean isException = false;
						while(pt != null && !(isException = "java.lang.Exception".equals(pt.getType().getFullyQualifiedName()))) {
							pt = pt.getSuperType();
						}
						if(!isException) {
							addProblem(BatchValidationMessages.EXCEPTION_CLASS_DOES_NOT_EXTEND_JAVA_LANG_EXCEPTION,
								BatchSeverityPreferences.WRONG_EXCEPTION_CLASS, e, ATTR_CLASS, file, -1);
						}
					}
				}
			}
		}
	}

	private Set<IFile> collectFiles(IProject project, Set<IFile> changedFiles, IProjectValidationContext context) {
		Set<IFile> files = new HashSet<IFile>();
		if(context == null) {
			files.addAll(changedFiles);
			return files;
		}
		
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		
		Set<IFile> direct = new HashSet<IFile>();
		Set<IFile> dependent = new HashSet<IFile>();
		for (IFile f: changedFiles) {
			if(f != null && f.exists() && f.getProject() == project) {
				Set<IPath> paths = context.getCoreResourcesByVariableName(SHORT_ID, f.getFullPath().toOSString(), true);
				String name = f.getName();
				
				if(name.toLowerCase().endsWith(".java")) {
					try {
						ICompilationUnit unit = EclipseUtil.getCompilationUnit(f);
						if(unit != null) {
							IType[] ts = unit.getTypes();
							for (IType t: ts) {
								String type = t.getFullyQualifiedName();
								Set<IPath> paths1 = context.getCoreResourcesByVariableName(SHORT_ID, type, true);
								if(paths1 != null) {
									if(paths != null) {
										paths.addAll(paths1);
									} else {
										paths = paths1;
									}
								}
							}
							Collection<IBatchArtifact> as = batchProject.getArtifacts(f);
							if(!as.isEmpty()) {
								for (IBatchArtifact a: as) {
									String n = a.getName();
									Set<IPath> paths1 = context.getCoreResourcesByVariableName(SHORT_ID, n, true);
									if(paths1 != null) {
										if(paths != null) {
											paths.addAll(paths1);
										} else {
											paths = paths1;
										}
									}
								}
							}
						}
					} catch (CoreException e) {
						BatchCorePlugin.pluginLog().logError(e);
					}
				} else if(name.toLowerCase().endsWith(".xml")) { //$NON-NLS-1$
					if(!direct.contains(f) && !dependent.contains(f)) {
						files.add(f);
					}
					direct.add(f);
					dependent.remove(f);
				}

				if(paths != null) {
					for (IPath path: paths) {
						IFile f1 = project.getParent().getFile(path);
						if(f1.exists()) {
							if(direct.contains(f1) || dependent.contains(f1)) continue;
							dependent.add(f1);
							files.add(f1);
						}
					}
				}
			}
		}

		return files;
	}

	@Override
	public void registerPreferenceInfo() {
		PreferenceInfoManager.register(getProblemType(), new BatchPreferenceInfo());
	}

	class BatchPreferenceInfo implements IPreferenceInfo {

		@Override
		public String getPreferencePageId() {
			return PREFERENCE_PAGE_ID;
		}

		@Override
		public String getPropertyPageId() {
			return PROPERTY_PAGE_ID;
		}

		@Override
		public String getPluginId() {
			return BatchCorePlugin.PLUGIN_ID;
		}
		
	}

	class ContextProperties implements BatchConstants {
		ContextProperties parent;
		Map<String, SimpleReference> declared = new HashMap<String, SimpleReference>();
		Set<String> referenced = new HashSet<String>();
		
		public ContextProperties(ContextProperties parent, Element element, IFile file) {
			this.parent = parent;
			Element properties = XMLUtilities.getUniqueChild(element, TAG_PROPERTIES);
			if(properties != null) {
				Element[] es = XMLUtilities.getChildren(properties, TAG_PROPERTY);
				for (Element property: es) {
					String name = property.getAttribute(ATTR_NAME).trim();
					declared.put(name, new SimpleReference(property, ATTR_NAME, file));
				}
				for (Element property: es) {
					lookForPropertyReferences(property, file);
				}
			}		
		}

		void lookForPropertyReferences(Element e, IFile file) {
			NamedNodeMap as = e.getAttributes();
			for (int k = 0; k < as.getLength(); k++) {
				Node n = as.item(k);
				if(n instanceof Attr) {
					String value = ((Attr)n).getValue();
					if(value.trim().length() > 0) {
						SimpleReference v = new SimpleReference(e, n.getNodeName(), file);
						int i = 0;
						while(i < value.length()) {
							int i1 = value.indexOf(JOB_PROPERTY_CALL_START, i);
							if(i1 < 0) break;
							i1 += JOB_PROPERTY_CALL_START.length();
							int i2 = value.indexOf(JOB_PROPERTY_CALL_END, i1);
							if(i2 < 0) break;
							String name = value.substring(i1, i2);
							if(!requestProperty(name)) {
								SimpleReference v1 = new SimpleReference(v.start + i1 + 1, name.length(), file);
								addProblem(BatchValidationMessages.UNKNOWN_PROPERTY, BatchSeverityPreferences.UNKNOWN_PROPERTY, 
										new String[]{name}, v1.getLength(), v1.getStartPosition(), file/*, quickFixId*/);
							}
							i = i2;
						}
					}
					
				}
			}
		}
		boolean requestProperty(String name) {
			if(declared.containsKey(name)) {
				referenced.add(name);
				return true;
			}
			return parent != null && parent.requestProperty(name);
		}
		
		public void complete(IBatchArtifact a) {
			for (String name: declared.keySet()) {
				if(!referenced.contains(name) && (a == null || a.getProperty(name) == null)) {
					SimpleReference ref = declared.get(name);
					if(a == null) {
						addProblem(BatchValidationMessages.PROPERTY_IS_NOT_USED_1, BatchSeverityPreferences.UNUSED_PROPERTY, new String[]{name}, ref.getLength(), ref.getStartPosition(), ref.getResource()/*, quickFixId*/);
					} else {
						addProblem(BatchValidationMessages.PROPERTY_IS_NOT_USED, BatchSeverityPreferences.UNUSED_PROPERTY, new String[]{name, a.getName()}, ref.getLength(), ref.getStartPosition(), ref.getResource()/*, quickFixId*/);
					}
				}
			}
		}
	}

	private static final String BUNDLE_NAME = "org.jboss.tools.batch.internal.core.validation.messages";

	@Override
	protected String getMessageBundleName() {
		return BUNDLE_NAME;
	}

}

class SimpleReference implements ITextSourceReference {
	int start;
	int length;
	IFile resource;
	
	public SimpleReference(Element element, String attr, IFile resource) {
		Attr a = element.getAttributeNode(attr);
		if(a instanceof IDOMAttr) {
			IDOMAttr da = (IDOMAttr)a;
			start = da.getValueRegionStartOffset();
			length = ((IDOMAttr) a).getValueRegionText().length();
		}
		this.resource = resource;
	}

	public SimpleReference(int start, int length, IFile resource) {
		this.start = start;
		this.length = length;
		this.resource = resource;
	}

	@Override
	public int getStartPosition() {
		return start;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public IFile getResource() {
		return resource;
	}
}
