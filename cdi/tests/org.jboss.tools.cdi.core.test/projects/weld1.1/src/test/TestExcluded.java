package test;

import javax.inject.Inject;

import exclude.p1.Bean1;
import exclude.p1.Bean2;
import exclude.p2.Bean3;
import exclude.p2.p3.Bean4;
import exclude.p4.Bean5;
import exclude.p4.p5.Bean6;
import exclude.p6.Bean7;

public class TestExcluded {
	@Inject Bean1 bean1;
	@Inject Bean2 bean2; 
	@Inject Bean3 bean3;
	@Inject Bean4 bean4;
	@Inject Bean5 bean5;
	@Inject Bean6 bean6; 
	@Inject Bean7 bean7;
}
