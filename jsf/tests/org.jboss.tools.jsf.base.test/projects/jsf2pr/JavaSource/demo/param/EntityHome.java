package demo.param;

public class EntityHome<E> extends Home<EntityManager, E> {

	public boolean isManaged() {
		return true;
	}

	public E find() {
		return getInstance();
	}

	public void create() {
	}
}