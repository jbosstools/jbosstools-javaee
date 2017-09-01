package org.jboss.jsr299.tck.tests.jbt.resolution.parameters;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class Bean {

	@Inject
	@Q("set1")
	Set<String> s1;

	@Inject
	@Q("set2")
	Set<? extends Set<String>> s2;

	//resolved to Factory.getMap1();
	@Inject
	@Q("map")
	Map<String, Set<String>> m1;
	
	//resolved to Factory.getMap2();
	@Inject
	@Q("map2")
	Map<Integer, Set<Integer>> m2;
	
	//resolved to Factory.getMap3();
	@Inject
	@Q("map3")
	Map<Set<Integer>, Map<Long,Integer>> m3;
	
	//not resolved to Factory.getMap3();
	@Inject
	@Q("map3")
	Map<Set<Integer>, Map<Long,Short>> m3a;

	//resolved to Factory.getMap3();
	@Inject
	@Q("map3")
	Map<? extends Set<Integer>, Map<Long,Integer>> m3b;

	//resolved to Factory.getMap4();
	@Inject
	@Q("map4")
	Map<Set<A>, Map<String,A>> m4;

	//resolved to Factory.getMap4();
	@Inject
	@Q("map4")
	Map<Set<B>, Map<Set<A>,B>> m4a;

	//not resolved to Factory.getMap4();
	@Inject
	@Q("map4")
	Map<Set<B>, Map<String,A>> m4b;
}
