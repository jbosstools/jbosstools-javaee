package org.jboss.beans.validation.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MyBean3 {
	Set<String> set1 = new HashSet<String>();
	Set<Integer> set2 = new HashSet<Integer>();
	
	Map<Integer,Long> map = new HashMap<Integer, Long>();
	
	MyBean1 bean1;
	MyBean1 bean2;

}
