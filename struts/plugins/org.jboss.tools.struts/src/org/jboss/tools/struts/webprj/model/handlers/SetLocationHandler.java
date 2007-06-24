/*
 * SetLocationHandler.java
 *
 * Created on February 13, 2003, 5:14 PM
 */

package org.jboss.tools.struts.webprj.model.handlers;

import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.*;
import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.model.undo.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  valera
 */
public class SetLocationHandler extends DefaultEditHandler {
    
    private static final UnmountFileSystemHandler ufs = new UnmountFileSystemHandler();
//    private static final MountFileSystemHandler mfs = new MountFileSystemHandler();
    
    /** Creates a new instance of SetLocationHandler */
    public SetLocationHandler() {
    }
    
    public void executeHandler(XModelObject object, Properties prop) throws Exception {
        XUndoManager undo = object.getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("Edit " + DefaultCreateHandler.title(object, false), XTransactionUndo.EDIT);
        undo.addUndoable(u);
        try {
            Properties p = extractProperties(data[0]);
            String location = p.getProperty("location");
            if (!location.equals(object.getAttributeValue("location"))) {
                String fsName = null;
                String owner = object.getPath();
                owner = "WebProject"+owner.substring(owner.indexOf('/'));
                XModelObject fs = object.getModel().getByPath("FileSystems");
                XModelObject[] fss = fs.getChildren();
                for (int i = 0; i < fss.length; i++) {
                    if (owner.equals(fss[i].getAttributeValue("owner"))) {
                        fsName = fss[i].getAttributeValue("name");
                        fss[i].setAttributeValue("owner", "");
                        ufs.setAction(this.action);
                        ufs.executeHandler(fss[i], prop);
                        if (fss[i].isActive()) {//cancel
                            fss[i].setAttributeValue("owner", owner);
                            undo.rollbackTransactionInProgress();
                            return;
                        }
                        break;
                    }
                }
                super.executeHandler(object, prop);
                if (location.length() == 0) return;
                if (fsName == null) {
                    fsName = object.getAttributeValue("name");
                    if (fs.getChildByPath(fsName) != null) {
                        int ind = 2;
                        while (fs.getChildByPath(fsName+"-"+ind) != null) ind++;
                        fsName += "-"+ind;
                    }
                }
                
                Properties p2 = new Properties();
                p2.setProperty("name", fsName);
                p2.setProperty("location", location);
                p2.setProperty("owner", owner);
                String entity = new File(location).isDirectory() ? "FileSystemFolder" : "FileSystemJar";
                if(entity.equals("FileSystemJar")) {
                    p2.setProperty("info", "hidden=yes");
                }
                XModelObject c = XModelObjectLoaderUtil.createValidObject(object.getModel(), entity, p2);
                addCreatedObject(fs, c, false, -1);
                c.setModified(false);
                object.getModel().getUndoManager().addUndoable(new MountFileSystemUndo(c));
                XModelClassLoader cl = (XModelClassLoader)object.getModel().getModelClassLoader();
                cl.validate();
            }
        } catch (Exception e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }
    }
}
