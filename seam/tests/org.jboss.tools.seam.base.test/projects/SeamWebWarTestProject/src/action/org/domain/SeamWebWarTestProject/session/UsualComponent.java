package org.domain.SeamWebWarTestProject.session;

import org.jboss.seam.annotations.Name;
import javax.ejb.Remove;

@Name("usualComponent")
public class UsualComponent {

    @Remove
    public void removeMethod1() {}

    @Remove
    public void removeMethod2() {}
}