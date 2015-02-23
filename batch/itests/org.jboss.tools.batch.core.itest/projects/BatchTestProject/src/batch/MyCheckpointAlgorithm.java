package batch;

import javax.batch.api.chunk.AbstractCheckpointAlgorithm;
import javax.inject.Named;

@Named
public class MyCheckpointAlgorithm extends AbstractCheckpointAlgorithm {

	@Override
	public boolean isReadyToCheckpoint() throws Exception {
		return false;
	}

}
