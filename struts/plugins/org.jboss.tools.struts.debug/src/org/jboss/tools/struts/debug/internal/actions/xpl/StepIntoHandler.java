/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Exadel, Inc.
 *     Red Hat, Inc. 
 *******************************************************************************/
package org.jboss.tools.struts.debug.internal.actions.xpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventFilter;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.jboss.tools.struts.debug.internal.StrutsDebugPlugin;


/**
 * @author Jeremy
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StepIntoHandler implements IDebugEventFilter {
    /**
     * The thread in which to step
     */
    private IJavaThread fThread;
    
    /**
     * The initial stack frame
     */
    private String fOriginalName;
    private String fOriginalSignature;
    private String fOriginalTypeName;
    private int fOriginalStackDepth;
        
    /**
     * Whether this is the first step into.
     */
    private boolean fFirstStep = true;
        
    /**
     * The state of step filters before the step.
     */
    private boolean fStepFilterEnabledState;
        
    /**
     * Expected event kind
     */
    private int fExpectedKind = -1;
    
    /**
     * Expected event detail
     */
    private int fExpectedDetail = -1;

    /**
     * Empty event set.
     */
    private static final DebugEvent[] fgEmptyEvents = new DebugEvent[0];
    
    /**
     * Constructs a step handler to step into the given method in the given thread
     * starting from the given stack frame.
     */
    public StepIntoHandler(JDIStackFrame frame) {
        fThread = (IJavaThread)frame.getThread();

        try {
            fOriginalName = frame.getName();
            fOriginalSignature = frame.getSignature();
            fOriginalTypeName = frame.getDeclaringTypeName();
        } catch (CoreException e) {
            DebugPlugin.log(e);
        }
    }
        
    /**
     * Returns the target thread for the step.
     * 
     * @return the target thread for the step
     */
    protected IJavaThread getThread() {
        return fThread;
    }
        
    protected IJavaDebugTarget getDebugTarget() {
        return (IJavaDebugTarget)getThread().getDebugTarget();
    }
        
    /**
     * @see org.eclipse.debug.core.IDebugEventFilter#filterDebugEvents(org.eclipse.debug.core.DebugEvent)
     */
    public DebugEvent[] filterDebugEvents(DebugEvent[] events) {
        // we only expect one event from our thread - find the event
        DebugEvent event = null;
        int index = -1;
        int threadEvents = 0;

        for (int i = 0; i < events.length; i++) {
            DebugEvent e = events[i];
            
            if (e.getKind() == DebugEvent.TERMINATE || 
                (e.getKind() == DebugEvent.SUSPEND && e.getDetail() == DebugEvent.BREAKPOINT)) {
                    cleanup();
                    return events;
            }
                
            if (isExpectedEvent(e)) {
                event = e;
                index = i;
                threadEvents++;
            } else if (e.getSource() == getThread()) {
                threadEvents++;
            } 
        }
        
        if (event == null) {
            // nothing to process in this event set
//            System.err.println("nothing to process in this event set");
            return events;
        }
                
        // create filtered event set
        DebugEvent[] filtered = new DebugEvent[events.length - 1];
        if (filtered.length > 0) {
            int j = 0;
            for (int i = 0; i < events.length; i++) {
                if (i != index) {
                    filtered[j] = events[i];
                    j++;
                }
            }
        }
        
        // if more than one event in our thread, abort (filtering our event)
        if (threadEvents > 1) {
            cleanup();
//            System.err.println("more than one event in our thread, abort (filtering our event)");
            return filtered;
        }
        
        // we have the one expected event - process it
        switch (event.getKind()) {
            case DebugEvent.RESUME:
                // next, we expect a step end
                setExpectedEvent(DebugEvent.SUSPEND, DebugEvent.STEP_END);
                if (fFirstStep) {
                    fFirstStep = false;
                    return events; // include the first resume event
                } else {
                    // secondary step - filter the event
                    return filtered;
                }           
            case DebugEvent.SUSPEND:
                // compare location to desired location
                try {
                    final IJavaStackFrame frame = (IJavaStackFrame)getThread().getTopStackFrame();
                    int stackDepth = frame.getThread().getStackFrames().length;
                    String typeName = frame.getDeclaringTypeName();
                    String methodName = frame.getName();
                    String methodSignature = frame.getSignature();
                        
                    String name = null;
                    if (frame.isConstructor()) {
                        name = frame.getDeclaringTypeName();
                        index = name.lastIndexOf('.');
                        if (index >= 0) {
                            name = name.substring(index + 1);
                        }
                    } else {
                        name = frame.getName();
                    }
                    
                    List stackFramesList = new ArrayList();
                    IStackFrame[] stackFrames = fThread.getStackFrames();
                        
                    updateInfoStack (frame);

                    Runnable r = null;
                    
                    // Update check-point if needed
                    if (stackDepth < fOriginalStackDepth) fOriginalStackDepth = stackDepth;
                     
                    if (typeName.startsWith("java.") || typeName.startsWith("javax.")) {
                        // Do step return (java SDK classes)
                        r = new Runnable() {
                            public void run() {
                                try {
                                    setExpectedEvent(DebugEvent.RESUME, DebugEvent.STEP_RETURN);
                                    frame.stepReturn();
                                } catch (DebugException e) {
                                    DebugPlugin.log(e);
                                    cleanup();
                                    DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[]{new DebugEvent(getDebugTarget(), DebugEvent.CHANGE)});
                                }
                            }
                        };
                    } else {
                        // Do step into (non-java SDK classes)
                        fOriginalStackDepth = stackDepth; // Set new check-point
 
                        r = new Runnable() {
                            public void run() {
                                try {
                                    setExpectedEvent(DebugEvent.RESUME, DebugEvent.STEP_INTO);
                                    frame.stepInto();   
                                } catch (DebugException e) {
                                    DebugPlugin.log(e);
                                    cleanup();
                                    DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[]{new DebugEvent(getDebugTarget(), DebugEvent.CHANGE)});
                                }
                            }
                        };                                                              
                              
                    } 
                    DebugPlugin.getDefault().asyncExec(r);
                    return fgEmptyEvents;
                } catch (CoreException e) {
                	DebugPlugin.log(e);
                    cleanup();
                    return events;
                }           
        }
        return events;
    }

    private class StackFrameInfo {
        private String fSavedMethodName = null;
        private String fSavedMethodSignature = null;
        private String fSavedTypeName = null;
        private int fSavedStackDepth = 0;
        private boolean fInitialized = false;
        
        StackFrameInfo (IJavaStackFrame topFrame) {
            try {
                int stackDepth = topFrame.getThread().getStackFrames().length;
                String typeName = topFrame.getDeclaringTypeName();
                String methodName = topFrame.getName();
                String methodSignature = topFrame.getSignature();
                int index = -1;                
                String name = null;
                if (topFrame.isConstructor()) {
                    name = topFrame.getDeclaringTypeName();
                    index = name.lastIndexOf('.');
                    if (index >= 0) {
                        name = name.substring(index + 1);
                    }
                } else {
                    name = topFrame.getName();
                }
            
                fSavedMethodName = methodName;
                fSavedMethodSignature = methodSignature;
                fSavedTypeName = typeName;
                fSavedStackDepth = stackDepth;
                
                fInitialized = true;
            } catch (DebugException ex) {
            	DebugPlugin.log(ex);
            }
        }
        
        boolean isInitialized() {
            return fInitialized;
        }
        
        private boolean canContinueValidation(IJavaStackFrame topFrame) {
            if (fSavedMethodName == null && fSavedMethodSignature == null &&
                fSavedTypeName == null && fSavedStackDepth == 0) return true;
            
            
            try {
                int stackDepth = topFrame.getThread().getStackFrames().length;
                String typeName = topFrame.getDeclaringTypeName();
                String methodName = topFrame.getName();
                String methodSignature = topFrame.getSignature();
                int index = -1;                
                String name = null;
                if (topFrame.isConstructor()) {
                    name = topFrame.getDeclaringTypeName();
                    index = name.lastIndexOf('.');
                    if (index >= 0) name = name.substring(index + 1);
                } else {
                    name = topFrame.getName();
                }
            
                if ( fSavedStackDepth < stackDepth || (fSavedMethodName != null && fSavedMethodName.equals(methodName) &&
                        fSavedMethodSignature != null && fSavedMethodSignature.equals(methodSignature) &&
                        fSavedTypeName != null && fSavedTypeName.equals(typeName) &&
                        fSavedStackDepth == stackDepth)) 
                    return false;
            } catch (DebugException ex) {
            	DebugPlugin.log(ex);
                return true;
            }
            return true;
        }
        
        public boolean equals(Object obj) {
            if (obj == null) return false; 
            if (obj instanceof StackFrameInfo) {
                StackFrameInfo sfi = (StackFrameInfo)obj;
                if (fSavedMethodName != null && !fSavedMethodName.equals(sfi.fSavedMethodName)) 
                    return false;
                
                if (fSavedMethodName == null && sfi.fSavedMethodName != null) 
                    return false;
                        
                if (fSavedMethodSignature != null && !fSavedMethodSignature.equals(sfi.fSavedMethodSignature)) 
                    return false;
                    
                if (fSavedMethodSignature == null && sfi.fSavedMethodSignature == null) 
                    return false;

                if (fSavedTypeName != null && !fSavedTypeName.equals(sfi.fSavedTypeName)) 
                    return false;
                    
                if (fSavedTypeName == null && sfi.fSavedTypeName == null) 
                    return false;
                    
                if (fSavedStackDepth != sfi.fSavedStackDepth) 
                    return false;

                return true;    
            }
            return false;
        }
    }   
   
    private static Stack infoStack;
   
    private void addStackFrameInfo(IJavaStackFrame topFrame) {
//        System.err.println(">>> addStackFrameInfo()");
        if (infoStack == null) infoStack = new Stack();
        StackFrameInfo info = new StackFrameInfo(topFrame);
        if (info.isInitialized()){
            infoStack.push(info); 
            DebugPlugin.logDebugMessage("StepIntoHandler.addStackFrameInfo() : Stack Frame Info added");
        } else {
        	DebugPlugin.logDebugMessage("StepIntoHandler.addStackFrameInfo() : Stack Frame Info NOT INITIALIZED, so it's NOT added");
        }
    }
    
    private void removeStackFrameInfo() {
//        System.err.println(">>> removeStackFrameInfo()");
        if (infoStack != null && !infoStack.isEmpty()) {
            infoStack.pop(); 
            DebugPlugin.logDebugMessage("StepIntoHandler.removeStackFrameInfo() : Stack Frame Info removed");
        } else {
        	DebugPlugin.logDebugMessage("StepIntoHandler.removeStackFrameInfo() : Info Stack is EMPTY");
        }
    }
    
    private void updateInfoStack(IJavaStackFrame topFrame) {
        if (infoStack == null || infoStack.isEmpty()) {
            return;
        } 
        DebugPlugin.logDebugMessage("infoStack NOT EMPTY: size = " + infoStack.size());
        StackFrameInfo top = (StackFrameInfo)infoStack.peek();
        try {
            int stackDepth = topFrame.getThread().getStackFrames().length;
            if (top.fSavedStackDepth > stackDepth) {
                removeStackFrameInfo();
                updateInfoStack(topFrame);
            } 
        } catch (DebugException ex) {
        	DebugPlugin.log(ex);
        }
    }
    
    private boolean isRepeatedStop(IJavaStackFrame topFrame) {
        if (topFrame == null) return false; 
        if (infoStack == null || infoStack.isEmpty()) return false; 
        StackFrameInfo info = new StackFrameInfo(topFrame);
        boolean res = info.equals(infoStack.peek());
        if ( res ) {
        	DebugPlugin.logDebugMessage("isRepeated() == TRUE!!!");
        }
        return res;
    }
    
    /** 
     * Called when stepping returned from the original frame without entering the desired method.
     */
    protected void missed() {
        cleanup();
        Runnable r = new Runnable() {
            public void run() {
            }
        };
    }
    
    /**
     * Performs the step.
     */
    public void step() {
        DebugPlugin.getDefault().addDebugEventFilter(this);
        fStepFilterEnabledState = getDebugTarget().isStepFiltersEnabled();
        getDebugTarget().setStepFiltersEnabled(false);
        try {
            fOriginalStackDepth = getThread().getStackFrames().length;
            setExpectedEvent(DebugEvent.RESUME, DebugEvent.STEP_INTO);
            getThread().stepInto();
        } catch (DebugException e) {
        	DebugPlugin.log(e);
            cleanup();
            DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[]{new DebugEvent(getDebugTarget(), DebugEvent.CHANGE)});          
        }
    }
        
    /**
     * Cleans up when the step is complete/aborted.
     */
    protected void cleanup() {
        DebugPlugin.getDefault().removeDebugEventFilter(this);
        // restore step filter state
        getDebugTarget().setStepFiltersEnabled(fStepFilterEnabledState);
//        System.err.println("StepIntoHandler.cleanup() : DONE");
    }
    
    /**
     * Sets the expected debug event kind and detail we are waiting for next.
     * 
     * @param kind event kind
     * @param detail event detail
     */
    private void setExpectedEvent(int kind, int detail) {
        fExpectedKind = kind;
        fExpectedDetail = detail;
    }
    
    /**
     * Returns whether the given event is what we expected.
     * 
     * @param event fire event
     * @return whether the event is what we expected
     */
    protected boolean isExpectedEvent(DebugEvent event) {
        return event.getSource().equals(getThread()) &&
            event.getKind() == fExpectedKind &&
            event.getDetail() == fExpectedDetail;
    }
}
