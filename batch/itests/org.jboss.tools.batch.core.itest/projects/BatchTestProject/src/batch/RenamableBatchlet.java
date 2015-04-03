package batch;

import javax.batch.api.BatchProperty;
import javax.batch.api.Batchlet;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class RenamableBatchlet implements Batchlet {
	
	@Inject @BatchProperty(name="secondName") String otherName;

	@Override
	public String process() throws Exception {
		return null;
	}

	@Override
	public void stop() throws Exception {
	}

}