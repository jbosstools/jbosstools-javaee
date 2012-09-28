package demo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.model.DataModel;

@ManagedBean
public class TestBeanForVarAttributes {

    public Test getTest() {
    	return null;
    }

	public List<Value> getList() {
	    return null;
	}

	public Set<Value> getSet() {
	    return null;
	}

	public Map<String, Value> getMap() {
	    return null;
	}

	public Iterable<Value> getIterable() {
	    return null;
	}

    public Value[] getArray() {
    	return null;
    }

	public DataModel<Value> getListDM() {
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