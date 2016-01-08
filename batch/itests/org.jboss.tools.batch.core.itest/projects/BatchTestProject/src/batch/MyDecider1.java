package batch;

import javax.batch.api.Decider;
import javax.batch.runtime.StepExecution;
import javax.inject.Named;

@Named
public class MyDecider1 implements Decider {

	public MyDecider1() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String decide(StepExecution[] arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
