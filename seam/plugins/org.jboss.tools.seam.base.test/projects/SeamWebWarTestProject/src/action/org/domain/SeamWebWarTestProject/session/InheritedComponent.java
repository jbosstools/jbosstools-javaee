package org.domain.SeamWebWarTestProject.session;

import javax.ejb.Stateful;
import javax.persistence.Entity;

import org.jboss.seam.annotations.Name;

@Name("inheritedComponent")
@Entity
@Stateful
public class InheritedComponent extends BaseComponent {

}
