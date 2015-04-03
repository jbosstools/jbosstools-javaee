package batch;

import javax.batch.api.BatchProperty;
import javax.batch.api.Batchlet;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class RenamablePropertyBatchlet implements Batchlet {
	
	@Inject @BatchProperty String otherName;

	@Override
	public String process() throws Exception {
		return null;
	}

	@Override
	public void stop() throws Exception {
	}

}