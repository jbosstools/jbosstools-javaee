package batch;

import javax.batch.api.Decider;
import javax.batch.runtime.StepExecution;
import javax.inject.Named;

@Named
public class MyDecider implements Decider {

	@Override
	public String decide(StepExecution[] arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
