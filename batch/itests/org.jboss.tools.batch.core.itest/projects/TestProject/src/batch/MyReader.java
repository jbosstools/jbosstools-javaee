package batch;

import java.io.Serializable;

import javax.batch.api.Decider;
import javax.batch.api.chunk.ItemReader;
import javax.batch.runtime.StepExecution;
import javax.inject.Named;

@Named
public class MyReader implements ItemReader {

	@Override
	public Serializable checkpointInfo() throws Exception {
		return null;
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public void open(Serializable arg0) throws Exception {
	}

	@Override
	public Object readItem() throws Exception {
		return null;
	}

}
