package batch;

import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.inject.Named;

@Named
public class MyRetryProcessListener implements RetryProcessListener {

	@Override
	public void onRetryProcessException(Object arg0, Exception arg1)
			throws Exception {
	}

}
