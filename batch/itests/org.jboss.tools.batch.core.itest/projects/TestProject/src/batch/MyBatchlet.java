package batch;

import javax.batch.api.BatchProperty;
import javax.batch.api.Batchlet;
import javax.inject.Inject;
import javax.inject.Named;

@Named("batchlet1")
public class MyBatchlet implements Batchlet {
	
	@Inject @BatchProperty(name="worktime") String time;

	@Override
	public String process() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
