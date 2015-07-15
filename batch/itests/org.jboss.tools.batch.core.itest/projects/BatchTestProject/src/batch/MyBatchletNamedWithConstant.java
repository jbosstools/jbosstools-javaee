package batch;

import javax.batch.api.AbstractBatchlet;
import javax.batch.api.BatchProperty;
import javax.inject.Inject;
import javax.inject.Named;

@Named(MyBatchletNamedWithConstant.NAME)
public class MyBatchletNamedWithConstant extends AbstractBatchlet {
	public static final String NAME = "batchlet_named_with_constant";
	public static final String PROP_NAME = "property_named_with_constant";

	@Inject @BatchProperty(name=MyBatchletNamedWithConstant.PROP_NAME) String time;

	public MyBatchletNamedWithConstant() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String process() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
