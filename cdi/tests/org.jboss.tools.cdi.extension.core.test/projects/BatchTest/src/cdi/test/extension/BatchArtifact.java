package cdi.test.extension;

import javax.batch.api.BatchProperty;
import javax.batch.api.Batchlet;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ApplicationScoped
public class BatchArtifact implements Batchlet {
	@Inject
	JobContext context;

	@Inject
	StepContext stepContext;

	@Inject
	String notABatchProperty;

	@Inject
	@BatchProperty
	String batchProperty;

	@Override
	public String process() throws Exception {
		return null;
	}

	@Override
	public void stop() throws Exception {
	}
	
}
