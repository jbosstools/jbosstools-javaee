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
	private final static String STBFE_CLASS_NAME = "org.jboss.tools.common.model.ui.attribute.editor.JavaHyperlinkLineFieldEditor";
	private final static String SBFEE_CLASS_NAME = "org.jboss.tools.common.model.ui.attribute.editor.StringButtonFieldEditorEx";
	
	private static IFormActionData[] createDefaultFormActionData(String actionPath) {
		return FormLayoutDataUtil.createDefaultFormActionData(actionPath);
	}

	private final static IFormData[] FACES_CONFIG_DEFINITIONS =
		new IFormData[] {
			new FormData(
				"Factories",
				"",
				"factory",
				new FormAttributeData[]{new FormAttributeData("application-factory", null, STBFE_CLASS_NAME), new FormAttributeData("faces-context-factory", null, STBFE_CLASS_NAME), new FormAttributeData("lifecycle-factory", null, STBFE_CLASS_NAME), new FormAttributeData("render-kit-factory", null, STBFE_CLASS_NAME)}
			),
			// Faces Config Lifecycle Form
			new FormData(
				"Lifecycle",
				"",
				"lifecycle",
				new FormAttributeData[]{new FormAttributeData("phase-listener", 100)},
				new String[]{"JSFPhaseListener"},
				FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreatePhaseListener", true)
			)
		};
	private final static IFormData ATTRIBUTES_FORM_DEFINITIONS =
		new FormData(
			"Attributes",
			"",
			new FormAttributeData[]{new FormAttributeData("attribute-name", 30, "name"), new FormAttributeData("attribute-class", 70, "class")},
			new String[]{"JSFAttribute"},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateAttribute")
		);
	private static IFormData PROPERTIES_FORM_DEFINITIONS =
		new FormData(
			"Properties",
			"",
			new FormAttributeData[]{new FormAttributeData("property-name", 30, "name"), new FormAttributeData("property-class", 70, "class")},
			new String[]{"JSFProperty"},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateProperty")
		);
	private final static IFormData FACETS_FORM_DEFINITIONS =
		new FormData(
			"Facets",
			"",
			new FormAttributeData[]{new FormAttributeData("facet-name", 100, "name")},
			new String[]{"JSFFacet11"},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateFacet")
	);
	
	static IFormData createResolver(String name, String entity, String addAction) {
		return new FormData(
			name,
			"",
			new FormAttributeData[]{new FormAttributeData("class name", 100, "class name")},
				new String[]{entity},
				FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateResolvers." + addAction)
			);
	}
	
	private final static IFormData[] APPLICATION_DEFINITIONS = new IFormData[] {
		new FormData(
			"Application",
			"",
			FormLayoutDataUtil.createGeneralFormAttributeData("JSFApplication")
		),
		createResolver("Property Resolvers", "JSFPropertyResolver", "AddPropertyResolver"),
		createResolver("Variable Resolvers", "JSFVariableResolver", "AddVariableResolver"),
		new FormData(
			"Message Bundles",
			"",
			new FormAttributeData[]{new FormAttributeData("message-bundle", 100)},
			new String[]{"JSFMessageBundle"},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddMessageBundle", true)
		),
		new FormData("org.jboss.tools.jsf.ui.editor.form.LocaleConfigForm"),
		new FormData(
			"Advanced",
			"",
			FormLayoutDataUtil.createAdvancedFormAttributeData("JSFApplication")
		)
	};

	private final static IFormData[] APPLICATION_12_DEFINITIONS = new IFormData[] {
		new FormData(
			"Application",
			"",
			FormLayoutDataUtil.createGeneralFormAttributeData("JSFApplication12")
		),
		createResolver("EL Resolvers", "JSFELResolver", "AddELResolver"),
		createResolver("Property Resolvers", "JSFPropertyResolver", "AddPropertyResolver"),
		createResolver("Variable Resolvers", "JSFVariableResolver", "AddVariableResolver"),
		new FormData(
			"Message Bundles",
			"",
			new FormAttributeData[]{new FormAttributeData("message-bundle", 100)},
			new String[]{"JSFMessageBundle"},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddMessageBundle", true)
		),
		new FormData(
			"Resource Bundles",
			"",
			new FormAttributeData[]{new FormAttributeData("base-name", 70), new FormAttributeData("var", 30)},
			new String[]{"JSFResourceBundle"},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddResourceBundle")
		),
		new FormData("org.jboss.tools.jsf.ui.editor.form.LocaleConfigForm"),
		new FormData(
			"Extensions",
			"",
			new FormAttributeData[]{new FormAttributeData("element type", 100, "element")},
			new String[]{"JSFApplicationExtension"},
			createDefaultFormActionData("CreateActions.CreateExtension")
		),
		new FormData(
			"Advanced",
			"",
			FormLayoutDataUtil.createAdvancedFormAttributeData("JSFApplication")
		)
	};

	private final static IFormData APPLICATION_DEFINITION =
		new FormData("JSFApplication", new String[]{null}, APPLICATION_DEFINITIONS);
	private final static IFormData APPLICATION_12_DEFINITION =
		new FormData("JSFApplication12", new String[]{null}, APPLICATION_12_DEFINITIONS);

	
	private final static IFormData createExtensionFormDefinition(String entity, String name) {
		return new FormData(
			entity,
			new String[]{null},
			new IFormData[] {
				new FormData(
					name,
					"",
					new FormAttributeData[]{new FormAttributeData("tag", 100)},
					new String[]{"AnyElement"},
					FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateTag")
				),
			}
		);
	}
	
	private final static IFormData createComponentsFormDefinitions(String parentEntity, String childEntity) {
		return new FormData(
			"Components",
			"",
			parentEntity,
			new FormAttributeData[]{new FormAttributeData("component-type", 30, "type"), new FormAttributeData("component-class", 70, "class")},
			new String[]{childEntity},
			createDefaultFormActionData("CreateActions.AddComponent")
		);
	}
	private final static IFormData createComponentFormDefinitions(String entity, boolean facets) {
		IFormData component = new FormData(
			"Component",
			"",
			new FormAttributeData[]{new FormAttributeData("component-type"), new FormAttributeData("component-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
		);
		IFormData[] definitions = (facets) ? new IFormData[] {component, FACETS_FORM_DEFINITIONS, ATTRIBUTES_FORM_DEFINITIONS, PROPERTIES_FORM_DEFINITIONS}
		                                   : new IFormData[] {component, ATTRIBUTES_FORM_DEFINITIONS, PROPERTIES_FORM_DEFINITIONS};
		return new FormData(entity,	new String[]{null},	definitions);
	}
	private final static IFormData createRenderKitsFormDefinitions(String parentEntity, String childEntity) {
		return new FormData(
			"Render Kits",
			"",
			parentEntity,
			new FormAttributeData[]{new FormAttributeData("render-kit-id", 30, "id"), new FormAttributeData("render-kit-class", 70, "class")},
			new String[]{childEntity},
			createDefaultFormActionData("CreateActions.AddRenderKit")
		);
	}
	private final static IFormData createRenderKitFormDefinitions(String parentEntity, String childEntity) {
		int size = 3;
		boolean is12 = parentEntity.endsWith("12");
		if(is12) size++;
		IFormData[] result = new IFormData[size];
		result[0] = new FormData(
			"Render Kit",
			"",
			new FormAttributeData[]{new FormAttributeData("render-kit-id"), new FormAttributeData("render-kit-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
		);
		result[1] = new FormData(
			"Renderers",
			"",
			new FormAttributeData[]{new FormAttributeData("renderer-class", 70, "class"), new FormAttributeData("renderer-type", 30, "type")},
			new String[]{childEntity},
			createDefaultFormActionData("CreateActions.CreateRenderer")
		);
		if(is12) {
			result[2] = new FormData(
				"Extensions",
				"",
				new FormAttributeData[]{new FormAttributeData("element type", 100, "element"), },
				new String[]{"JSFRenderKitExtension"},
				createDefaultFormActionData("CreateActions.CreateExtension")
			);
		}
		result[size - 1] = new FormData(
			"Advanced",
			"",
			new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
		);
		
		return new FormData(parentEntity, new String[]{null}, result);
	}

	private final static IFormData createRendererFormDefinitions(String entity, boolean facets) {
		IFormData renderer = new FormData(
			"Renderer",
			"",
			new FormAttributeData[]{new FormAttributeData("renderer-class", null, STBFE_CLASS_NAME), new FormAttributeData("renderer-type", null, SBFEE_CLASS_NAME), new FormAttributeData("component-family"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
		);
		IFormData types = new FormData(
			"Supported Component Types",
			"",
			new FormAttributeData[]{new FormAttributeData("attribute-names", 30), new FormAttributeData("component-type", 70)},
			new String[]{"JSFSupportedComponentType"},
			createDefaultFormActionData("CreateActions.CreateSupportedComponentType")
		);
		IFormData classes = new FormData(
			"Supported Component Classes",
			"",
			new FormAttributeData[]{new FormAttributeData("attribute-names", 30), new FormAttributeData("component-class", 70)},
			new String[]{"JSFSupportedComponentClass"},
			createDefaultFormActionData("CreateActions.CreateSupportedComponentClass")
		);
		IFormData advanced = new FormData(
			"Advanced",
			"",
			new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
		);
		IFormData[] definitions = (facets) ? new IFormData[] {renderer, FACETS_FORM_DEFINITIONS, ATTRIBUTES_FORM_DEFINITIONS, advanced}
										   : new IFormData[] {renderer, ATTRIBUTES_FORM_DEFINITIONS, types, classes, advanced};
		return new FormData(entity, new String[]{null}, definitions);
	}

	private final static IFormData[] FORM_LAYOUT_DEFINITIONS =
		new IFormData[] {
			new FormData("FacesConfig", new String[]{null}, FACES_CONFIG_DEFINITIONS),
			new FormData("FacesConfig11", new String[]{null}, FACES_CONFIG_DEFINITIONS),
			new FormData("FacesConfig12", new String[]{null}, FACES_CONFIG_DEFINITIONS),
			APPLICATION_DEFINITION,
			APPLICATION_12_DEFINITION,
			createComponentsFormDefinitions("JSFComponents", "JSFComponent"),
			createComponentsFormDefinitions("JSFComponents11", "JSFComponent11"),
			createComponentFormDefinitions("JSFComponent", false),
			createComponentFormDefinitions("JSFComponent11", true),
			new FormData(
				"JSFFacet11",
				new String[]{null},
				new IFormData[] {
					// Attribute Form
					new FormData(
						"Facet",
						"",
						new FormAttributeData[]{new FormAttributeData("facet-name"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					// Advanced Attribute Form
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
					)
				}
			),
			createExtensionFormDefinition("JSFConfigExtension", "Faces Config Extension"),
			createExtensionFormDefinition("JSFFacetExtension", "Facet Extension"),
			createExtensionFormDefinition("JSFApplicationExtension", "Application Extension"),
			createExtensionFormDefinition("JSFAttributeExtension", "Attribute Extension"),
			createExtensionFormDefinition("JSFComponentExtension", "Component Extension"),
			createExtensionFormDefinition("JSFPropertyExtension", "Property Extension"),
			createExtensionFormDefinition("JSFRendererExtension", "Renderer Extension"),
			createExtensionFormDefinition("JSFRenderKitExtension", "Render Kit Extension"),
			createExtensionFormDefinition("JSFValidatorExtension", "Validator Extension"),
			new FormData(
				"JSFAttribute",
				new String[]{null},
				new IFormData[] {
					// Attribute Form
					new FormData(
						"Attribute",
						"",
						new FormAttributeData[]{new FormAttributeData("attribute-name"), new FormAttributeData("attribute-class", null, STBFE_CLASS_NAME), new FormAttributeData("default-value"), new FormAttributeData("suggested-value", InfoLayoutDataFactory.getInstance()), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					// Advanced Attribute Form
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
					)
				}
			),
			new FormData(
				"JSFProperty",
				new String[]{null},
				new IFormData[] {
					// Property Form
					new FormData(
						"Property",
						"",
						new FormAttributeData[]{new FormAttributeData("property-name"), new FormAttributeData("property-class", null, STBFE_CLASS_NAME), new FormAttributeData("default-value"), new FormAttributeData("suggested-value", InfoLayoutDataFactory.getInstance()), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					// Advanced Attribute Form
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
					)
				}
			),
			// Converters Form
			new FormData(
				"Converters",
				"",
				"JSFConverters",
				new FormAttributeData[]{new FormAttributeData("converter-id", 30, "id"), new FormAttributeData("converter-class", 70, "class")},
				new String[]{"JSFConverter"},
				createDefaultFormActionData("CreateActions.AddConverter.WithId")
			),
			new FormData(
				"JSFConverter",
				new String[]{null},
				new IFormData[] {
					// Converter Form
					new FormData(
						"Converter",
						"",
						new FormAttributeData[]{new FormAttributeData("converter-id"), new FormAttributeData("converter-for-class", null, STBFE_CLASS_NAME), new FormAttributeData("converter-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					ATTRIBUTES_FORM_DEFINITIONS,
					PROPERTIES_FORM_DEFINITIONS
				}
			),
			// Managed Beans Form
			new FormData(
				"Managed Beans",
				"",
				"JSFManagedBeans",
				new FormAttributeData[]{new FormAttributeData("managed-bean-name", 50, "name"), new FormAttributeData("managed-bean-class", 35, "class"), new FormAttributeData("managed-bean-scope", 15, "scope")},
				new String[]{"JSFManagedBean"},
				createDefaultFormActionData("CreateActions.AddManagedBean")
			),
			new FormData(
				"JSFManagedBean",
				new String[]{null},
				new IFormData[] {
					// Managed Bean Form
					new FormData(
						"Managed Bean",
						"",
						new FormAttributeData[]{new FormAttributeData("managed-bean-name"), new FormAttributeData("managed-bean-class", null, STBFE_CLASS_NAME), new FormAttributeData("managed-bean-scope"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					// Properties Form
					new FormData(
						"Properties",
						"",
						new FormAttributeData[]{new FormAttributeData("property-name", 50, "name"), new FormAttributeData("property-class", 30, "class"), new FormAttributeData("value", 20)},
						new String[]{"JSFManagedProperty"},
						createDefaultFormActionData("CreateActions.CreatePropertySafe")
					),
					// Advanced Managed Bean Form
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
					)
				}
			),
			new FormData(
				"JSFManagedProperty",
				new String[]{null},
				new IFormData[] {
					// Managed Bean Property Form
					new FormData("org.jboss.tools.jsf.ui.editor.form.ManagedBeanPropertyForm"),
					// Advanced Attribute Form
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance()), new FormAttributeData("comment", InfoLayoutDataFactory.getInstance())}
					)
				}
			),
			new FormData(
				"Map Entry",
				"",
				"JSFMapEntry",
				new FormAttributeData[]{new FormAttributeData("key"), new FormAttributeData("null-value"), new FormAttributeData("value", GreedyLayoutDataFactory.getInstance(), null, 0)}
			),
			new FormData(
				"List Entry",
				"",
				"JSFListEntry",
				new FormAttributeData[]{new FormAttributeData("null-value"), new FormAttributeData("value", GreedyLayoutDataFactory.getInstance(), null, 0)}
			),
			// Navigation Rules Form
			new FormData(
				"Navigation Rules",
				"",
				"JSFNavigationRules",
				new FormAttributeData[]{new FormAttributeData("presentation", 100, "from-view-id")},
				new String[]{"JSFNavigationRule"},
				createDefaultFormActionData("CreateActions.AddRule")
			),
			new FormData(
				"JSFNavigationRule",
				new String[]{null},
				new IFormData[] {
					// Navigation Rule Form
					new FormData(
						"Navigation Rule",
						"",
						new FormAttributeData[]{new FormAttributeData("from-view-id", null, SBFEE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					// Navigation Cases Form
					new FormData(
						"Navigation Cases",
						"",
						new FormAttributeData[]{new FormAttributeData("from-outcome", 30), new FormAttributeData("from-action", 30), new FormAttributeData("to-view-id", 30), new FormAttributeData("redirect", 10)},
						new String[]{"JSFNavigationCase"},
						createDefaultFormActionData("CreateActions.CreateCase")
					),
					// Advanced Navigation Rule Form
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
					)
				}
			),
			new FormData(
				"JSFNavigationCase",
				new String[]{null},
				new IFormData[] {
					// Navigation Case Form
					new FormData(
						"Navigation Case",
						"",
						new FormAttributeData[]{new FormAttributeData("from-outcome"), new FormAttributeData("from-action"), new FormAttributeData("to-view-id", null, SBFEE_CLASS_NAME), new FormAttributeData("redirect"), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					// Advanced Navigation Case Form
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
					)
				}
			),
			// Referenced Beans Form
			new FormData(
				"Referenced Beans",
				"",
				"JSFReferencedBeans",
				new FormAttributeData[]{new FormAttributeData("referenced-bean-name", 35, "name"), new FormAttributeData("referenced-bean-class", 65, "class")},
				new String[]{"JSFReferencedBean"},
				createDefaultFormActionData("CreateActions.AddReferencedBean")
			),
			new FormData(
				"JSFReferencedBean",
				new String[]{null},
				new IFormData[] {
					// Referenced Bean Form
					new FormData(
						"Referenced Bean",
						"",
						new FormAttributeData[]{new FormAttributeData("referenced-bean-name"), new FormAttributeData("referenced-bean-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					// Advanced Referenced Bean Form
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
					)
				}
			),
			// Render Kits Form
			createRenderKitsFormDefinitions("JSFRenderKits", "JSFRenderKit"),
			createRenderKitsFormDefinitions("JSFRenderKits11", "JSFRenderKit11"),
			createRenderKitsFormDefinitions("JSFRenderKits12", "JSFRenderKit12"),
			createRenderKitFormDefinitions("JSFRenderKit", "JSFRenderer"),
			createRenderKitFormDefinitions("JSFRenderKit11", "JSFRenderer11"),
			createRenderKitFormDefinitions("JSFRenderKit12", "JSFRenderer11"),
			createRendererFormDefinitions("JSFRenderer", false),
			createRendererFormDefinitions("JSFRenderer11", true),
			// Supported Component Type Form
			new FormData(
				"Supported Component Type",
				"",
				"JSFSupportedComponentType",
				new FormAttributeData[]{new FormAttributeData("attribute-names"), new FormAttributeData("component-type", null, SBFEE_CLASS_NAME), new FormAttributeData("id")}
			),
			// Supported Component Class Form
			new FormData(
				"Supported Component Class",
				"",
				"JSFSupportedComponentClass",
				new FormAttributeData[]{new FormAttributeData("attribute-names"), new FormAttributeData("component-class", null, STBFE_CLASS_NAME), new FormAttributeData("id")}
			),
			// Validators Form
			new FormData(
				"Validators",
				"",
				"JSFValidators",
				new FormAttributeData[]{new FormAttributeData("validator-id", 30, "id"), new FormAttributeData("validator-class", 70, "class")},
				new String[]{"JSFValidator"},
				createDefaultFormActionData("CreateActions.AddValidator")
			),
			new FormData(
				"Validators",
				"",
				"JSFValidators12",
				new FormAttributeData[]{new FormAttributeData("validator-id", 30, "id"), new FormAttributeData("validator-class", 70, "class")},
				new String[]{"JSFValidator12"},
				createDefaultFormActionData("CreateActions.AddValidator")
			),
			new FormData(
				"Extensions",
				"",
				"JSFConfigExtensions",
				new FormAttributeData[]{new FormAttributeData("element type", 100, "element")},
				new String[]{"JSFConfigExtension"},
				createDefaultFormActionData("CreateActions.CreateExtension")
				),
			new FormData(
				"JSFValidator",
				new String[]{null},
				new IFormData[] {
					new FormData(
						"Validator",
						"",
						new FormAttributeData[]{new FormAttributeData("validator-id"), new FormAttributeData("validator-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					ATTRIBUTES_FORM_DEFINITIONS,
					PROPERTIES_FORM_DEFINITIONS,
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
					)
				}
			),
			new FormData(
				"JSFValidator12",
				new String[]{null},
				new IFormData[] {
					new FormData(
						"Validator",
						"",
						new FormAttributeData[]{new FormAttributeData("validator-id"), new FormAttributeData("validator-class", null, STBFE_CLASS_NAME), new FormAttributeData("description", InfoLayoutDataFactory.getInstance())}
					),
					ATTRIBUTES_FORM_DEFINITIONS,
					PROPERTIES_FORM_DEFINITIONS,
					new FormData(
						"Extensions",
						"",
						new FormAttributeData[]{new FormAttributeData("element type", 100, "element")},
						new String[]{"JSFValidatorExtension"},
						createDefaultFormActionData("CreateActions.CreateExtension")
					),
					new FormData(
						"Advanced",
						"",
						new FormAttributeData[]{new FormAttributeData("id"), new FormAttributeData("display-name"), new FormAttributeData("small-icon"), new FormAttributeData("large-icon")}
					)
				}
			),
			new FormData(
				"EL Resolver",
				"",
				"JSFELResolver",
				FormLayoutDataUtil.createGeneralFormAttributeData("JSFELResolver")
			),
			new FormData(
				"Property Resolver",
				"",
				"JSFPropertyResolver",
				FormLayoutDataUtil.createGeneralFormAttributeData("JSFPropertyResolver")
			),
			new FormData(
				"Variable Resolver",
				"",
				"JSFVariableResolver",
				FormLayoutDataUtil.createGeneralFormAttributeData("JSFVariableResolver")
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
