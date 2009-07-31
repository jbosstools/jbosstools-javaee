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
package org.jboss.tools.jsf.ui.editor.form;

import java.util.*;
import org.jboss.tools.common.model.ui.forms.*;
/**
 * @author Igels
 */
public class JSFFormLayoutData implements IFormLayoutData {
	private final static String STBFE_CLASS_NAME = "org.jboss.tools.common.model.ui.attribute.editor.JavaHyperlinkLineFieldEditor"; //$NON-NLS-1$
	private final static String SBFEE_CLASS_NAME = "org.jboss.tools.common.model.ui.attribute.editor.StringButtonFieldEditorEx"; //$NON-NLS-1$
	
	/**
	 * 
	 * @param actionPath (non-translatable)
	 * @return
	 */
	private static IFormActionData[] createDefaultFormActionData(String actionPath) {
		return FormLayoutDataUtil.createDefaultFormActionData(actionPath);
	}

	private final static IFormData[] FACES_CONFIG_DEFINITIONS =
		new IFormData[] {
			new FormData(
				"Factories",
				"", //$NON-NLS-1$
				"factory", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("application-factory", null, STBFE_CLASS_NAME), new FormAttributeData("faces-context-factory", null, STBFE_CLASS_NAME), new FormAttributeData("lifecycle-factory", null, STBFE_CLASS_NAME), new FormAttributeData("render-kit-factory", null, STBFE_CLASS_NAME)} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			),
			// Faces Config Lifecycle Form
			new FormData(
				"Lifecycle",
				"", //$NON-NLS-1$
				"lifecycle", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("phase-listener", 100)}, //$NON-NLS-1$
				new String[]{"JSFPhaseListener"}, //$NON-NLS-1$
				FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreatePhaseListener", true) //$NON-NLS-1$
			)
		};
	private final static IFormData ATTRIBUTES_FORM_DEFINITIONS =
		new FormData(
			"Attributes",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("attribute-name", 30, "name"), new FormAttributeData("attribute-class", 70, "class")}, //$NON-NLS-1$ //$NON-NLS-3$
			new String[]{"JSFAttribute"}, //$NON-NLS-1$
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateAttribute") //$NON-NLS-1$
		);
	private static IFormData PROPERTIES_FORM_DEFINITIONS =
		new FormData(
			"Properties",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("property-name", 30, "name"), new FormAttributeData("property-class", 70, "class")}, //$NON-NLS-1$ //$NON-NLS-3$
			new String[]{"JSFProperty"}, //$NON-NLS-1$
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateProperty") //$NON-NLS-1$
		);
	private final static IFormData FACETS_FORM_DEFINITIONS =
		new FormData(
			"Facets",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("facet-name", 100, "name")}, //$NON-NLS-1$
			new String[]{"JSFFacet11"}, //$NON-NLS-1$
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateFacet") //$NON-NLS-1$
	);
	
	/**
	 * 
	 * @param name (translatable)
	 * @param entity (non-translatable)
	 * @param addAction (non-translatable)
	 * @return
	 */
	static IFormData createResolver(String name, String entity, String addAction) {
		return new FormData(
			name,
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("class name", 100, "class name")}, //$NON-NLS-1$
				new String[]{entity},
				FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateResolvers." + addAction) //$NON-NLS-1$
			);
	}
	
	private final static IFormData[] APPLICATION_DEFINITIONS = new IFormData[] {
		new FormData(
			"Application",
			"", //$NON-NLS-1$
			FormLayoutDataUtil.createGeneralFormAttributeData("JSFApplication") //$NON-NLS-1$
		),
		createResolver("Property Resolvers", "JSFPropertyResolver", "AddPropertyResolver"), //$NON-NLS-2$ //$NON-NLS-3$ 
		createResolver("Variable Resolvers", "JSFVariableResolver", "AddVariableResolver"), //$NON-NLS-2$ //$NON-NLS-3$ 
		new FormData(
			"Message Bundles",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("message-bundle", 100)}, //$NON-NLS-1$
			new String[]{"JSFMessageBundle"}, //$NON-NLS-1$
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddMessageBundle", true) //$NON-NLS-1$
		),
		new FormData("org.jboss.tools.jsf.ui.editor.form.LocaleConfigForm"), //$NON-NLS-1$
		new FormData(
			"Advanced",
			"", //$NON-NLS-1$
			FormLayoutDataUtil.createAdvancedFormAttributeData("JSFApplication") //$NON-NLS-1$
		)
	};

	private final static IFormData[] APPLICATION_12_DEFINITIONS = new IFormData[] {
		new FormData(
			"Application",
			"", //$NON-NLS-1$
			FormLayoutDataUtil.createGeneralFormAttributeData("JSFApplication12") //$NON-NLS-1$
		),
		createResolver("EL Resolvers", "JSFELResolver", "AddELResolver"), //$NON-NLS-2$ //$NON-NLS-3$
		createResolver("Property Resolvers", "JSFPropertyResolver", "AddPropertyResolver"), //$NON-NLS-2$ //$NON-NLS-3$
		createResolver("Variable Resolvers", "JSFVariableResolver", "AddVariableResolver"), //$NON-NLS-2$ //$NON-NLS-3$
		new FormData(
			"Message Bundles",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("message-bundle", 100)}, //$NON-NLS-1$
			new String[]{"JSFMessageBundle"}, //$NON-NLS-1$
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddMessageBundle", true) //$NON-NLS-1$
		),
		new FormData(
			"Resource Bundles",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("base-name", 70), new FormAttributeData("var", 30)}, //$NON-NLS-1$ //$NON-NLS-2$
			new String[]{"JSFResourceBundle"}, //$NON-NLS-1$
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddResourceBundle") //$NON-NLS-1$
		),
		new FormData("org.jboss.tools.jsf.ui.editor.form.LocaleConfigForm"), //$NON-NLS-1$
		new FormData(
			"Extensions",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("element type", 100, "element")}, //$NON-NLS-1$
			new String[]{"JSFApplicationExtension"}, //$NON-NLS-1$
			createDefaultFormActionData("CreateActions.CreateExtension") //$NON-NLS-1$
		),
		new FormData(
			"Advanced",
			"", //$NON-NLS-1$
			FormLayoutDataUtil.createAdvancedFormAttributeData("JSFApplication") //$NON-NLS-1$
		)
	};

	private final static IFormData APPLICATION_DEFINITION =
		new FormData("JSFApplication", new String[]{null}, APPLICATION_DEFINITIONS); //$NON-NLS-1$
	private final static IFormData APPLICATION_12_DEFINITION =
		new FormData("JSFApplication12", new String[]{null}, APPLICATION_12_DEFINITIONS); //$NON-NLS-1$

	/**
	 * 
	 * @param entity (non-translatable)
	 * @param name (translatable)
	 * @return
	 */
	private final static IFormData createExtensionFormDefinition(String entity, String name) {
		return new FormData(
			entity,
			new String[]{null},
			new IFormData[] {
				new FormData(
					name,
					"", //$NON-NLS-1$
					new FormAttributeData[]{new FormAttributeData("tag", 100)}, //$NON-NLS-1$
					new String[]{"AnyElement"}, //$NON-NLS-1$
					FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateTag") //$NON-NLS-1$
				),
			}
		);
	}
	
	/**
	 * 
	 * @param parentEntity (non-translatable)
	 * @param childEntity (non-translatable)
	 * @return
	 */
	private final static IFormData createComponentsFormDefinitions(String parentEntity, String childEntity) {
		return new FormData(
			"Components",
			"", //$NON-NLS-1$
			parentEntity,
			new FormAttributeData[]{new FormAttributeData("component-type", 30, "type"), new FormAttributeData("component-class", 70, "class")}, //$NON-NLS-1$ //$NON-NLS-3$
			new String[]{childEntity},
			createDefaultFormActionData("CreateActions.AddComponent") //$NON-NLS-1$
		);
	}
	
	/**
	 * 
	 * @param entity (non-translatable)
	 * @param facets
	 * @return
	 */
	private final static IFormData createComponentFormDefinitions(String entity, boolean facets) {
		IFormData component = new FormData(
			"Component",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("component-type"), new FormAttributeData("component-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		);
		IFormData[] definitions = (facets) ? new IFormData[] {component, FACETS_FORM_DEFINITIONS, ATTRIBUTES_FORM_DEFINITIONS, PROPERTIES_FORM_DEFINITIONS}
		                                   : new IFormData[] {component, ATTRIBUTES_FORM_DEFINITIONS, PROPERTIES_FORM_DEFINITIONS};
		return new FormData(entity,	new String[]{null},	definitions);
	}
	
	/**
	 * 
	 * @param parentEntity (non-translatable)
	 * @param childEntity (non-translatable)
	 * @return
	 */
	private final static IFormData createRenderKitsFormDefinitions(String parentEntity, String childEntity) {
		return new FormData(
			"Render Kits",
			"", //$NON-NLS-1$
			parentEntity,
			new FormAttributeData[]{new FormAttributeData("render-kit-id", 30, "id"), new FormAttributeData("render-kit-class", 70, "class")}, //$NON-NLS-1$ //$NON-NLS-3$
			new String[]{childEntity},
			createDefaultFormActionData("CreateActions.AddRenderKit") //$NON-NLS-1$
		);
	}
	
	/**
	 * 
	 * @param parentEntity (non-translatable)
	 * @param childEntity (non-translatable)
	 * @return
	 */
	private final static IFormData createRenderKitFormDefinitions(String parentEntity, String childEntity) {
		int size = 3;
		boolean is12 = parentEntity.endsWith("12"); //$NON-NLS-1$
		if(is12) size++;
		IFormData[] result = new IFormData[size];
		result[0] = new FormData(
			"Render Kit",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("render-kit-id"), new FormAttributeData("render-kit-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		);
		result[1] = new FormData(
			"Renderers",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("renderer-class", 70, "class"), new FormAttributeData("renderer-type", 30, "type")}, //$NON-NLS-1$ //$NON-NLS-3$
			new String[]{childEntity},
			createDefaultFormActionData("CreateActions.CreateRenderer") //$NON-NLS-1$
		);
		if(is12) {
			result[2] = new FormData(
				"Extensions",
				"", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("element type", 100, "element"), }, //$NON-NLS-1$
				new String[]{"JSFRenderKitExtension"}, //$NON-NLS-1$
				createDefaultFormActionData("CreateActions.CreateExtension") //$NON-NLS-1$
			);
		}
		result[size - 1] = new FormData(
			"Advanced",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		);
		
		return new FormData(parentEntity, new String[]{null}, result);
	}

	/**
	 * 
	 * @param entity (non-translatable)
	 * @param facets
	 * @return
	 */
	private final static IFormData createRendererFormDefinitions(String entity, boolean facets) {
		IFormData renderer = new FormData(
			"Renderer",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("renderer-class", null, STBFE_CLASS_NAME), new FormAttributeData("renderer-type", null, SBFEE_CLASS_NAME), new FormAttributeData("component-family"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		);
		IFormData types = new FormData(
			"Supported Component Types",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("attribute-names", 30), new FormAttributeData("component-type", 70)}, //$NON-NLS-1$ //$NON-NLS-2$
			new String[]{"JSFSupportedComponentType"}, //$NON-NLS-1$
			createDefaultFormActionData("CreateActions.CreateSupportedComponentType") //$NON-NLS-1$
		);
		IFormData classes = new FormData(
			"Supported Component Classes",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("attribute-names", 30), new FormAttributeData("component-class", 70)}, //$NON-NLS-1$ //$NON-NLS-2$
			new String[]{"JSFSupportedComponentClass"}, //$NON-NLS-1$
			createDefaultFormActionData("CreateActions.CreateSupportedComponentClass") //$NON-NLS-1$
		);
		IFormData advanced = new FormData(
			"Advanced",
			"", //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		);
		IFormData[] definitions = (facets) ? new IFormData[] {renderer, FACETS_FORM_DEFINITIONS, ATTRIBUTES_FORM_DEFINITIONS, advanced}
										   : new IFormData[] {renderer, ATTRIBUTES_FORM_DEFINITIONS, types, classes, advanced};
		return new FormData(entity, new String[]{null}, definitions);
	}

	private final static IFormData[] FORM_LAYOUT_DEFINITIONS =
		new IFormData[] {
			new FormData("FacesConfig", new String[]{null}, FACES_CONFIG_DEFINITIONS), //$NON-NLS-1$
			new FormData("FacesConfig11", new String[]{null}, FACES_CONFIG_DEFINITIONS), //$NON-NLS-1$
			new FormData("FacesConfig12", new String[]{null}, FACES_CONFIG_DEFINITIONS), //$NON-NLS-1$
			APPLICATION_DEFINITION,
			APPLICATION_12_DEFINITION,
			createComponentsFormDefinitions("JSFComponents", "JSFComponent"), //$NON-NLS-1$ //$NON-NLS-2$
			createComponentsFormDefinitions("JSFComponents11", "JSFComponent11"), //$NON-NLS-1$ //$NON-NLS-2$
			createComponentFormDefinitions("JSFComponent", false), //$NON-NLS-1$
			createComponentFormDefinitions("JSFComponent11", true), //$NON-NLS-1$
			new FormData(
				"JSFFacet11", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					// Attribute Form
					new FormData(
						"Facet",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("facet-name"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$
					),
					// Advanced Attribute Form
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					)
				}
			),
			createExtensionFormDefinition("JSFConfigExtension", "Faces Config Extension"), //$NON-NLS-1$
			createExtensionFormDefinition("JSFFacetExtension", "Facet Extension"), //$NON-NLS-1$
			createExtensionFormDefinition("JSFApplicationExtension", "Application Extension"), //$NON-NLS-1$
			createExtensionFormDefinition("JSFAttributeExtension", "Attribute Extension"), //$NON-NLS-1$
			createExtensionFormDefinition("JSFComponentExtension", "Component Extension"), //$NON-NLS-1$
			createExtensionFormDefinition("JSFPropertyExtension", "Property Extension"), //$NON-NLS-1$
			createExtensionFormDefinition("JSFRendererExtension", "Renderer Extension"), //$NON-NLS-1$
			createExtensionFormDefinition("JSFRenderKitExtension", "Render Kit Extension"), //$NON-NLS-1$
			createExtensionFormDefinition("JSFValidatorExtension", "Validator Extension"), //$NON-NLS-1$
			new FormData(
				"JSFAttribute", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					// Attribute Form
					new FormData(
						"Attribute",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("attribute-name"), new FormAttributeData("attribute-class", null, STBFE_CLASS_NAME), new FormAttributeData("default-value"), new FormAttributeData("suggested-value", InfoLayoutDataFactory.getInstance()), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					),
					// Advanced Attribute Form
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					)
				}
			),
			new FormData(
				"JSFProperty", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					// Property Form
					new FormData(
						"Property",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("property-name"), new FormAttributeData("property-class", null, STBFE_CLASS_NAME), new FormAttributeData("default-value"), new FormAttributeData("suggested-value", InfoLayoutDataFactory.getInstance()), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					),
					// Advanced Attribute Form
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
					)
				}
			),
			// Converters Form
			new FormData(
				"Converters",
				"", //$NON-NLS-1$
				"JSFConverters", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("converter-id", 30, "id"), new FormAttributeData("converter-class", 70, "class")}, //$NON-NLS-1$ //$NON-NLS-3$
				new String[]{"JSFConverter"}, //$NON-NLS-1$
				createDefaultFormActionData("CreateActions.AddConverter.WithId") //$NON-NLS-1$
			),
			new FormData(
				"JSFConverter", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					// Converter Form
					new FormData(
						"Converter",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("converter-id"), new FormAttributeData("converter-for-class", null, STBFE_CLASS_NAME), new FormAttributeData("converter-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
					),
					ATTRIBUTES_FORM_DEFINITIONS,
					PROPERTIES_FORM_DEFINITIONS
				}
			),
			// Managed Beans Form
			new FormData(
				"Managed Beans",
				"", //$NON-NLS-1$
				"JSFManagedBeans", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("managed-bean-name", 50, "name"), new FormAttributeData("managed-bean-class", 35, "class"), new FormAttributeData("managed-bean-scope", 15, "scope")}, //$NON-NLS-1$ //$NON-NLS-3$ //$NON-NLS-5$
				new String[]{"JSFManagedBean"}, //$NON-NLS-1$
				createDefaultFormActionData("CreateActions.AddManagedBean") //$NON-NLS-1$
			),
			new FormData(
				"JSFManagedBean", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					// Managed Bean Form
					new FormData(
						"Managed Bean",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("managed-bean-name"), new FormAttributeData("managed-bean-class", null, STBFE_CLASS_NAME), new FormAttributeData("managed-bean-scope"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					),
					// Properties Form
					new FormData(
						"Properties",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("property-name", 50, "name"), new FormAttributeData("property-class", 30, "class"), new FormAttributeData("value", 20)}, //$NON-NLS-1$ //$NON-NLS-3$ //$NON-NLS-5$
						new String[]{"JSFManagedProperty"}, //$NON-NLS-1$
						createDefaultFormActionData("CreateActions.CreatePropertySafe") //$NON-NLS-1$
					),
					// Advanced Managed Bean Form
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
					)
				}
			),
			new FormData(
				"JSFManagedProperty", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					// Managed Bean Property Form
					new FormData("org.jboss.tools.jsf.ui.editor.form.ManagedBeanPropertyForm"), //$NON-NLS-1$
					// Advanced Attribute Form
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance()), new FormAttributeData("comment", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
					)
				}
			),
			new FormData(
				"Map Entry",
				"", //$NON-NLS-1$
				"JSFMapEntry", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("key"), new FormAttributeData("null-value"), new FormAttributeData("value", GreedyLayoutDataFactory.getInstance(), null, 0)} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			),
			new FormData(
				"List Entry",
				"", //$NON-NLS-1$
				"JSFListEntry", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("null-value"), new FormAttributeData("value", GreedyLayoutDataFactory.getInstance(), null, 0)} //$NON-NLS-1$ //$NON-NLS-2$ 
			),
			// Navigation Rules Form
			new FormData(
				"Navigation Rules",
				"", //$NON-NLS-1$
				"JSFNavigationRules", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("presentation", 100, "from-view-id")}, //$NON-NLS-1$
				new String[]{"JSFNavigationRule"}, //$NON-NLS-1$
				createDefaultFormActionData("CreateActions.AddRule") //$NON-NLS-1$
			),
			new FormData(
				"JSFNavigationRule", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					// Navigation Rule Form
					new FormData(
						"Navigation Rule",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("from-view-id", null, SBFEE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$
					),
					// Navigation Cases Form
					new FormData(
						"Navigation Cases",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("from-outcome", 30), new FormAttributeData("from-action", 30), new FormAttributeData("to-view-id", 30), new FormAttributeData("redirect", 10)}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new String[]{"JSFNavigationCase"}, //$NON-NLS-1$
						createDefaultFormActionData("CreateActions.CreateCase") //$NON-NLS-1$
					),
					// Advanced Navigation Rule Form
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
					)
				}
			),
			new FormData(
				"JSFNavigationCase", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					// Navigation Case Form
					new FormData(
						"Navigation Case",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("from-outcome"), new FormAttributeData("from-action"), new FormAttributeData("to-view-id", null, SBFEE_CLASS_NAME), new FormAttributeData("redirect"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 
					),
					// Advanced Navigation Case Form
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
					)
				}
			),
			// Referenced Beans Form
			new FormData(
				"Referenced Beans",
				"", //$NON-NLS-1$
				"JSFReferencedBeans", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("referenced-bean-name", 35, "name"), new FormAttributeData("referenced-bean-class", 65, "class")}, //$NON-NLS-1$ //$NON-NLS-3$
				new String[]{"JSFReferencedBean"}, //$NON-NLS-1$
				createDefaultFormActionData("CreateActions.AddReferencedBean") //$NON-NLS-1$
			),
			new FormData(
				"JSFReferencedBean", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					// Referenced Bean Form
					new FormData(
						"Referenced Bean",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("referenced-bean-name"), new FormAttributeData("referenced-bean-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
					),
					// Advanced Referenced Bean Form
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
					)
				}
			),
			// Render Kits Form
			createRenderKitsFormDefinitions("JSFRenderKits", "JSFRenderKit"), //$NON-NLS-1$ //$NON-NLS-2$ 
			createRenderKitsFormDefinitions("JSFRenderKits11", "JSFRenderKit11"), //$NON-NLS-1$ //$NON-NLS-2$ 
			createRenderKitsFormDefinitions("JSFRenderKits12", "JSFRenderKit12"), //$NON-NLS-1$ //$NON-NLS-2$ 
			createRenderKitFormDefinitions("JSFRenderKit", "JSFRenderer"), //$NON-NLS-1$ //$NON-NLS-2$ 
			createRenderKitFormDefinitions("JSFRenderKit11", "JSFRenderer11"), //$NON-NLS-1$ //$NON-NLS-2$ 
			createRenderKitFormDefinitions("JSFRenderKit12", "JSFRenderer11"), //$NON-NLS-1$ //$NON-NLS-2$ 
			createRendererFormDefinitions("JSFRenderer", false), //$NON-NLS-1$ 
			createRendererFormDefinitions("JSFRenderer11", true), //$NON-NLS-1$ 
			// Supported Component Type Form
			new FormData(
				"Supported Component Type",
				"", //$NON-NLS-1$
				"JSFSupportedComponentType", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("attribute-names"), new FormAttributeData("component-type", null, SBFEE_CLASS_NAME), new FormAttributeData("id")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			),
			// Supported Component Class Form
			new FormData(
				"Supported Component Class",
				"", //$NON-NLS-1$
				"JSFSupportedComponentClass", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("attribute-names"), new FormAttributeData("component-class", null, STBFE_CLASS_NAME), new FormAttributeData("id")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			),
			// Validators Form
			new FormData(
				"Validators",
				"", //$NON-NLS-1$
				"JSFValidators", //$NON-NLS-1$ 
				new FormAttributeData[]{new FormAttributeData("validator-id", 30, "id"), new FormAttributeData("validator-class", 70, "class")}, //$NON-NLS-1$ //$NON-NLS-3$
				new String[]{"JSFValidator"}, //$NON-NLS-1$ 
				createDefaultFormActionData("CreateActions.AddValidator") //$NON-NLS-1$ 
			),
			new FormData(
				"Validators",
				"", //$NON-NLS-1$
				"JSFValidators12", //$NON-NLS-1$ 
				new FormAttributeData[]{new FormAttributeData("validator-id", 30, "id"), new FormAttributeData("validator-class", 70, "class")}, //$NON-NLS-1$ //$NON-NLS-3$
				new String[]{"JSFValidator12"}, //$NON-NLS-1$ 
				createDefaultFormActionData("CreateActions.AddValidator") //$NON-NLS-1$ 
			),
			new FormData(
				"Extensions",
				"", //$NON-NLS-1$
				"JSFConfigExtensions", //$NON-NLS-1$ 
				new FormAttributeData[]{new FormAttributeData("element type", 100, "element")}, //$NON-NLS-1$
				new String[]{"JSFConfigExtension"}, //$NON-NLS-1$ 
				createDefaultFormActionData("CreateActions.CreateExtension") //$NON-NLS-1$
				),
			new FormData(
				"JSFValidator",
				new String[]{null},
				new IFormData[] {
					new FormData(
						"Validator",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("validator-id"), new FormAttributeData("validator-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
					),
					ATTRIBUTES_FORM_DEFINITIONS,
					PROPERTIES_FORM_DEFINITIONS,
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
					)
				}
			),
			new FormData(
				"JSFValidator12", //$NON-NLS-1$
				new String[]{null},
				new IFormData[] {
					new FormData(
						"Validator",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("validator-id"), new FormAttributeData("validator-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					),
					ATTRIBUTES_FORM_DEFINITIONS,
					PROPERTIES_FORM_DEFINITIONS,
					new FormData(
						"Extensions",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("element type", 100, "element")}, //$NON-NLS-1$
						new String[]{"JSFValidatorExtension"}, //$NON-NLS-1$
						createDefaultFormActionData("CreateActions.CreateExtension") //$NON-NLS-1$
					),
					new FormData(
						"Advanced",
						"", //$NON-NLS-1$
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					)
				}
			),
			new FormData(
				"EL Resolver",
				"", //$NON-NLS-1$
				"JSFELResolver", //$NON-NLS-1$
				FormLayoutDataUtil.createGeneralFormAttributeData("JSFELResolver") //$NON-NLS-1$
			),
			new FormData(
				"Property Resolver",
				"", //$NON-NLS-1$
				"JSFPropertyResolver", //$NON-NLS-1$
				FormLayoutDataUtil.createGeneralFormAttributeData("JSFPropertyResolver") //$NON-NLS-1$
			),
			new FormData(
				"Variable Resolver",
				"", //$NON-NLS-1$
				"JSFVariableResolver", //$NON-NLS-1$
				FormLayoutDataUtil.createGeneralFormAttributeData("JSFVariableResolver") //$NON-NLS-1$
			),
		};

	private static Map FORM_LAYOUT_DEFINITION_MAP = Collections.unmodifiableMap(new ArrayToMap(FORM_LAYOUT_DEFINITIONS));

	private static final JSFFormLayoutData INSTANCE = new JSFFormLayoutData();

	public static JSFFormLayoutData getInstance() {
		return INSTANCE;
	}

	private JSFFormLayoutData() {
	} 

	public IFormData getFormData(String entityName) {
		return (IFormData)FORM_LAYOUT_DEFINITION_MAP.get(entityName);
	}

}
