package batch;

import javax.batch.api.chunk.listener.SkipReadListener;
import javax.inject.Named;

@Named
public class MySkipReadListener implements SkipReadListener {

	@Override
	public void onSkipReadItem(Exception arg0) throws Exception {
	}

}
