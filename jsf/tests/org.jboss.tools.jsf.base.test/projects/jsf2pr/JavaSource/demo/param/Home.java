package demo.param;

public abstract class Home<T, E> {

	protected E instance;

	public E getInstance() {
		return instance;
	}

	public void setInstance(E instance) {
		this.instance = instance;
	}

	public String getId() {
		return null;
	}

	public String getVersion() {
		return "";
	}
}