package batch;

import java.util.List;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;

@Named
public class MyWriter extends AbstractItemWriter {

	@Override
	public void writeItems(List<Object> arg0) throws Exception {
	}

}
