package org.jboss.jsr299.tck.tests.jbt.resolution.parameters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Produces;

public class Factory {

	@Produces
	@Q("set1")
	<X> HashSet<X> getSet() {
		return null;
	}

	@Produces 
	@Q("set2")
	<X> Set<HashSet<X>> getSetOfSet() {
		return null;
	}

	@Produces
	@Q("map")
	<X,Y> HashMap<X,Y> getMap() {
		return null;
	}
	
	@Produces
	@Q("map2")
	<X> HashMap<X,Set<X>> getMap2() {
		return null;
	}
	
	@Produces
	@Q("map3")
	<X,Y> HashMap<Set<X>,Map<Y,X>> getMap3() {
		return null;
	}
	
	@Produces
	@Q("map4")
	<X extends A,Y> HashMap<Set<X>,Map<Y,X>> getMap4() {
		return null;
	}
	
}
