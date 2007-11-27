/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.model.helpers.page.link;

import java.util.*;
import org.jboss.tools.common.model.*;

public class Links {
	Set<String> tags;
	String[] tagsArray;
	Map<String,Link[]> links = new HashMap<String,Link[]>();
	ArrayList<LinksListener> listeners = new ArrayList<LinksListener>();
	long timeStamp = 0;
	
	public Set getTags() {
		return tags;
	}
	public String[] getTagsArray() {
		return tagsArray;
	}
	
	public Link[] getLinks(String tag) {
		Link[] ls = (Link[])links.get(tag);
		return (ls != null) ? ls : new Link[0]; 
	}
	
	public void update(XModelObject object) {
		if(timeStamp == object.getTimeStamp()) return;
		timeStamp = object.getTimeStamp();
		Set<String> _tags = new HashSet<String>();
		Map<String,Set<Link>> _links = new HashMap<String,Set<Link>>();
		Map<String,Link[]> links2 = new HashMap<String, Link[]>();
		XModelObject[] cs = object.getChildren();
		for (int i = 0; i < cs.length; i++) {
			String tag = cs[i].getAttributeValue("tag");
			String attr = cs[i].getAttributeValue("attribute");
			String referTo = cs[i].getAttributeValue("refer to");
			_tags.add(tag);
			Set<Link> s = (Set<Link>)_links.get(tag);
			if(s == null) {
				s = new HashSet<Link>();
				_links.put(tag, s);				
			}
			Link link = new Link();
			link.setTag(tag);
			link.setAttribute(attr);
			link.setReferTo(referTo);			
			s.add(link);			
		}
		Iterator it = _tags.iterator();
		while(it.hasNext()) {
			String tag = it.next().toString();
			Set<Link> s = (Set<Link>)_links.get(tag);
			if(s == null) continue;
			Link[] ls = s.toArray(new Link[0]);
			links2.put(tag, ls);			
		}
		links = links2;
		tags = _tags;
		tagsArray = (String[])tags.toArray(new String[0]);
		fireLinksChanged();		 
	}
	
	private void fireLinksChanged() {
		LinksListener[] ls = (LinksListener[])listeners.toArray(new LinksListener[0]);
		for (int i = 0; i < ls.length; i++) {
			ls[i].linksChanged();
		}		
	}
	
	public void addLinksListener(LinksListener listener) {
		listeners.add(listener);
	}

	public void removeLinksListener(LinksListener listener) {
		listeners.remove(listener);
	}

}
