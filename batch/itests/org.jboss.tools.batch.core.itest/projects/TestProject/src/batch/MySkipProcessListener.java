package batch;

import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.inject.Named;

@Named
public class MySkipProcessListener implements SkipProcessListener {

	@Override
	public void onSkipProcessItem(Object arg0, Exception arg1) throws Exception {
	}

}
