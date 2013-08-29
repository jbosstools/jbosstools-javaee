package test.a;

import javax.inject.Inject;
import javax.enterprise.context.ConversationScoped;

import test.b.animal.Cat;
import test.b.animal.Dog;
import test.b.fish.Eel;
import test.b.fish.Salmon;
import test.b.plant.Flower;
import test.b.plant.Tree;
import test.c.bird.Crow;
import test.c.bird.Heron;
import test.c.insect.Bee;
import test.c.insect.Fly;
import test.d.planets.Mercury;
import test.d.planets.Venus;
import test.d.stars.Sirius;

@ConversationScoped
public class Test {
	//Archive cdianimals.jar has beans.xml with bean-discovery-mode="annotated"
	@Inject Cat cat; //not annotated - not a bean
	@Inject Dog dog; //annotated - bean

	//Archive cdibirds.jar does not include beans.xml
	@Inject Crow crow; //not annotated - not a bean
	@Inject Heron heron; //annotated - bean

	//Archive folder 'plants' has beans.xml with bean-discovery-mode="annotated"
	@Inject Tree tree; //not annotated - not a bean
	@Inject Flower flower; //annotated - bean

	//Archive folder 'cdiinsects' does not include beans.xml
	@Inject Bee bee; //not annotated - not a bean
	@Inject Fly fly; //annotated - bean

	//Archive cdifish.jar has beans.xml with bean-discovery-mode="all"
	@Inject Salmon salmon; //not annotated - bean
	@Inject Eel eel; //annotated - bean

	//Archive cdiastro.jar has vetoed package test.d.stars and class test.d.planets.Venus
	@Inject Mercury mercury; //bean
	@Inject Venus venus; //vetoed - not a bean
	@Inject Sirius sirius; //package is vetoed - not a bean
}
