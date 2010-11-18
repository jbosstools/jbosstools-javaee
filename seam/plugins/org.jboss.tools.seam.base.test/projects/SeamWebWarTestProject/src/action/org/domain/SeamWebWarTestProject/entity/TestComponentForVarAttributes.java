package org.domain.SeamWebWarTestProject.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;

@Name("testComponentForVars")
public class TestComponentForVarAttributes {

    @DataModel
	private Set<Value> setItems;

    @DataModel
	private List<Value> listItems;

    @DataModel
	private Map<String, Test> mapItems;

    @DataModel
	private Value[] arrayItems;

    public Test getTest() {
    	return null;
    }

	public List<Value> getList() {
	    return null;
	}

    @Factory("tipos")
    public Value[] initTipos() {
    	return null;
    }

    public Value[] getArray() {
    	return null;
    }

    @Factory("tiposs")
    public List<Value> initTiposNew() {
    	return null;
    }

	public static class Value {
		public String getName() {
			return null;
		}
	}

	public static class Test {
		public List<Value> getItems() {
			return null;
		}
	}
}