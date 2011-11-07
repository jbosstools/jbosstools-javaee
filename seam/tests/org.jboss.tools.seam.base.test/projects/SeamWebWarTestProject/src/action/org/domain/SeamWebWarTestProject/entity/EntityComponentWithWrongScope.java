package org.domain.SeamWebWarTestProject.entity;

import javax.persistence.Entity;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;

@Entity
@Scope(ScopeType.STATELESS)
@Name("AbcEntity")
public class EntityComponentWithWrongScope {

}
