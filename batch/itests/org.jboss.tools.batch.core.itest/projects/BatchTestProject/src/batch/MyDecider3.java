package batch;

import javax.batch.api.Decider;
import javax.batch.runtime.StepExecution;
import javax.inject.Named;

@Named("myNamedDecider3")
public class MyDecider3 implements Decider {

	public MyDecider3() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String decide(StepExecution[] arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
