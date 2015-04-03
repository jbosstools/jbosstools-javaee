package batch;

import java.util.List;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;

@Named("rewriter")
public class SearchableWriter extends AbstractItemWriter {

	@Override
	public void writeItems(List<Object> arg0) throws Exception {
	}

}
