package batch;

import java.util.List;

import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.inject.Named;

@Named
public class MyRetryWriteListener implements RetryWriteListener {

	@Override
	public void onRetryWriteException(List<Object> arg0, Exception arg1)
			throws Exception {
	}

}
