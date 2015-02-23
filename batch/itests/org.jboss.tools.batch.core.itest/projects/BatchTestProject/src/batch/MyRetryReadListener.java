package batch;

import javax.batch.api.chunk.listener.RetryReadListener;
import javax.inject.Named;

@Named
public class MyRetryReadListener implements RetryReadListener {

	@Override
	public void onRetryReadException(Exception arg0) throws Exception {
	}

}
