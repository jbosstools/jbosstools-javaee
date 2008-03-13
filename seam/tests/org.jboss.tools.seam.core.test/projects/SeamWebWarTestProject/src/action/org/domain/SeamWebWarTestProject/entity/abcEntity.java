package org.domain.SeamWebWarTestProject.entity;

import javax.ejb.Remove;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import org.hibernate.validator.Length;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity
@Scope(ScopeType.EVENT)
@Name("abcEntity")
public class abcEntity implements Serializable {
	static final long serialVersionUID = 1000;
	//seam-gen attributes (you should probably edit these)
	private Long id;
	private Integer version;
	private String name;
	
    //add additional entity attributes
	
	//seam-gen attribute getters/setters with annotations (you probably should edit)
		
	@Id @GeneratedValue
	public Long getId() {
	     return id;
	}

	public void setId(Long id) {
	     this.id = id;
	}
	
	@Version
	public Integer getVersion() {
	     return version;
	}

	@Remove
	public void removeMethod(){
		
	}

	@Length(max=20)
	public String getName() {
	     return name;
	}

	public void setName(String name) {
	     this.name = name;
	}   	
}
