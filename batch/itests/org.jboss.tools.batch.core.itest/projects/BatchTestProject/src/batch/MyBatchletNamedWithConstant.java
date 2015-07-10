package batch;

import javax.batch.api.AbstractBatchlet;
import javax.inject.Named;

@Named(MyBatchletNamedWithConstant.NAME)
public class MyBatchletNamedWithConstant extends AbstractBatchlet {
	public static final String NAME = "batchlet_named_with_constant"; 

	public MyBatchletNamedWithConstant() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String process() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
