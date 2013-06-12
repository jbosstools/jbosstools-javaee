package org.jboss.tools.runtime.seam.detector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.jboss.jdf.stacks.model.Stacks;
import org.jboss.tools.runtime.core.model.DownloadRuntime;
import org.jboss.tools.runtime.core.model.IDownloadRuntimesProvider;
import org.jboss.tools.stacks.core.model.StacksManager;

public class DownloadRuntimesSeamProvider implements IDownloadRuntimesProvider {

	private static final String LABEL_FILE_SIZE = "runtime-size";
	private static final String LABEL_CATEGORY = "runtime-category";
	private static final String LABEL_TYPE = "runtime-type";
	private static final String CATEGORY_PROJECT = "PROJECT";
	private static final String TYPE_SEAM = "SEAM";
	
	public DownloadRuntimesSeamProvider() {
	}

	private Stacks[] getStacks(IProgressMonitor monitor) {
		return new StacksManager().getStacks("Loading Downloadable Runtimes", monitor, StacksManager.StacksType.PRESTACKS_TYPE);
	}
	
	private ArrayList<DownloadRuntime> downloads = null;
	
	@Override
	public DownloadRuntime[] getDownloadableRuntimes(String requestType, IProgressMonitor monitor) {
		if( downloads == null )
			loadDownloadableRuntimes(monitor);
		return (DownloadRuntime[]) downloads.toArray(new DownloadRuntime[downloads.size()]);
	}
	
	private synchronized void loadDownloadableRuntimes(IProgressMonitor monitor) {
		monitor.beginTask("Load Remote Runtimes", 200);
		Stacks[] stacksArr = getStacks(new SubProgressMonitor(monitor, 100));
		ArrayList<DownloadRuntime> all = new ArrayList<DownloadRuntime>();
		monitor.beginTask("Create Download Runtimes", stacksArr.length * 100);		
		for( int i = 0; i < stacksArr.length; i++ ) {
			IProgressMonitor inner = new SubProgressMonitor(monitor, 100);
			if( stacksArr[i] != null ) {
				traverseStacks(stacksArr[i], all, inner);
			}
		}
		monitor.done();
		downloads = all;
	}
	
	private void traverseStacks(Stacks stacks, ArrayList<DownloadRuntime> list, IProgressMonitor monitor) {
		List<org.jboss.jdf.stacks.model.Runtime> runtimes = stacks.getAvailableRuntimes();
		Iterator<org.jboss.jdf.stacks.model.Runtime> i = runtimes.iterator();
		org.jboss.jdf.stacks.model.Runtime workingRT = null;
		monitor.beginTask("Create Download Runtimes", runtimes.size() * 100);
		while(i.hasNext()) {
			
			workingRT = i.next();
			boolean isSeam = isSeam(workingRT);
			String url = workingRT.getDownloadUrl();
			if( isSeam && url != null && !"".equals(url)) {
				// We can make a DL out of this
				String fileSize = workingRT.getLabels().getProperty(LABEL_FILE_SIZE);
				String license = workingRT.getLicense();
				String id = workingRT.getId();
				String legacyId = getLegacyId(id);
				String effectiveId = legacyId == null ? id : legacyId;
				
				String name = workingRT.getName();
				String version = workingRT.getVersion();
				DownloadRuntime dr = new DownloadRuntime(effectiveId, name, version, url);
				dr.setLicenseURL(license);
				dr.setSize(fileSize);
				if( legacyId != null )
					dr.setProperty(DownloadRuntime.PROPERTY_ALTERNATE_ID, id);
				list.add(dr);
			}
			monitor.worked(100);
		}
		monitor.done();
	}
	
	private boolean isSeam(org.jboss.jdf.stacks.model.Runtime rt) {
		if(CATEGORY_PROJECT.equals(rt.getLabels().get(LABEL_CATEGORY)) && TYPE_SEAM.equals(rt.getLabels().get(LABEL_TYPE)))
			return true;
		return false;
	}
	
	
	private HashMap<String, String> LEGACY_HASHMAP = null;
	
	// Given a stacks.yaml runtime id, get the legacy downloadRuntimes id that's required
	private synchronized String getLegacyId(String id) {
		if( LEGACY_HASHMAP == null )
			loadLegacy();
		return LEGACY_HASHMAP.get(id);
	}
	private synchronized void loadLegacy() {
		LEGACY_HASHMAP = new HashMap<String, String>();
		LEGACY_HASHMAP.put("seam202runtime", "org.jboss.tools.runtime.core.seam.202" );
		LEGACY_HASHMAP.put("seam222runtime", "org.jboss.tools.runtime.core.seam.222" );
	}
}
