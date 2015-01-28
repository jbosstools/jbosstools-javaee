package batch;

import java.util.List;

import javax.batch.api.chunk.listener.SkipWriteListener;
import javax.inject.Named;

@Named
public class MySkipWriteListener implements SkipWriteListener {

	@Override
	public void onSkipWriteItem(List<Object> arg0, Exception arg1)
			throws Exception {
	}

}
