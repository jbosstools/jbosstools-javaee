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
package org.jboss.tools.struts.debug.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.eval.IAstEvaluationEngine;
import org.eclipse.jdt.debug.eval.ICompiledExpression;
import org.eclipse.jdt.debug.eval.IEvaluationListener;
import org.eclipse.jdt.debug.eval.IEvaluationResult;
import org.eclipse.jdt.internal.debug.core.JDIDebugPlugin;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

import com.sun.jdi.Method;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.LocatableEvent;

/**
 * @author igels
 */
public abstract class ActionConditionBreakpoint extends ActionBreakpoint {

	private static final String ACTION_CONDITION_BREAKPOINT_MANAGER = "org.jboss.tools.struts.debug.actionConditionBreakpointManager";
	private static boolean suspend = false;

	protected Map fSuspendEvents = new HashMap();
	private IJavaProject fProject;

	public ActionConditionBreakpoint() {
		getActionConditionBreakpointManager().addToBreakpointList(this);
	}

	public ActionConditionBreakpoint(IResource resource, String typePattern, final String methodName, String methodSignature, boolean entry, boolean exit, Map attributes) throws CoreException {
		super(resource, typePattern, methodName, methodSignature, entry, exit, attributes);
		enableActionCondition();
		attributes.put(ACTION_CONDITION_BREAKPOINT_MANAGER, getActionConditionBreakpointManager());
		getActionConditionBreakpointManager().addToBreakpointList(this);
	}

	abstract protected String getActionCondition() throws CoreException;

	abstract protected IActionConditionBreakpointManager getActionConditionBreakpointManager();

	private void enableActionCondition() throws CoreException {
		this.setConditionEnabled(true);
		this.setCondition(getActionCondition());
		this.setConditionSuspendOnTrue(true);
	}

	public void delete() throws CoreException {
		super.delete();
		getActionConditionBreakpointManager().removeBreakpoint(this);
	}

	private boolean threadResumeQuiet(JDIThread thread) {
		try {
			if(!getActionConditionBreakpointManager().computeNotUsedBreakpoints()) {
				thread.resumeQuiet();
				return true;
			}
			suspend = false;
			return false;
		} catch (DebugException e) {
			//ignore
			return true;
		}
	}

	protected boolean handleMethodEvent(LocatableEvent event, Method method, JDIDebugTarget target, JDIThread thread) {
		try {
			if (isNativeOnly()) {
				if (!method.isNative()) {
					return true;
				}
			}

			if (getMethodName() != null) {
				if (!method.name().equals(getMethodName())) {
					return true;
				}
			}

			if (getMethodSignature() != null) {
				if (!method.signature().equals(getMethodSignature())) {
					return true;
				}
			}

			return handleConditionalBreakpointEvent(event, thread, target);

		} catch (CoreException e) {
			JDIDebugPlugin.log(e);
		}
		return true;
	}

	protected boolean handleConditionalBreakpointEvent(Event event, JDIThread thread, JDIDebugTarget target) throws CoreException {
		getActionConditionBreakpointManager().addToUsedBreakpointList(this);
		if(getActionConditionBreakpointManager().isHandlingBreakpointStoped()) {
			if(getActionConditionBreakpointManager().computeNotUsedBreakpoints()) {
				return false;
			}
			getActionConditionBreakpointManager().setStopHandleBreakpoints(false);
			return true;
		}
		if (thread.isPerformingEvaluation()) {
			StrutsDebugPlugin.log("Thread alraedy is performing evaluation.");
			// If an evaluation is already being computed for this thread,
			// we can't perform another
			return !suspendForEvent(event, thread);
		}

		EvaluationListener listener = new EvaluationListener();

		int suspendPolicy = SUSPEND_THREAD;
		try {
			suspendPolicy = getSuspendPolicy();
		} catch (CoreException e) {
			//ignore
		}

		JDIThread jdiThread = (JDIThread)thread;
		if (suspendPolicy == SUSPEND_VM) {
			((JDIDebugTarget)jdiThread.getDebugTarget()).prepareToSuspendByBreakpoint(this);
		} else {
			waitPreviousBreakpoint(jdiThread);
			jdiThread.handleSuspendForBreakpointQuiet(this);
			suspend = true;
		}
		try {
			JDIStackFrame frame = computeNewStackFrame(jdiThread);
			if(frame == null) {
				return threadResumeQuiet(jdiThread);
			}
			IJavaProject project = getJavaProject();
			IAstEvaluationEngine engine = getEvaluationEngine(target, project);
			if (project == null) {
				StrutsDebugPlugin.log("Struts debbuger: Java project is NULL!!!");
				return threadResumeQuiet(jdiThread);
			}
			if (engine == null) {
				StrutsDebugPlugin.log("Evaluation Engine is NULL!!!");
				return threadResumeQuiet(jdiThread);
			}
			ICompiledExpression expression = engine.getCompiledExpression(getCondition(), frame);
			fSuspendEvents.put(thread, event);
			engine.evaluateExpression(expression, frame, listener, DebugEvent.EVALUATION_IMPLICIT, false);
		} catch(Exception e) {
            StrutsDebugPlugin.log(new Status(IStatus.ERROR,
            		StrutsDebugPlugin.PLUGIN_ID,
            		0,
            		e.getMessage(),
            		e));
			return threadResumeQuiet(jdiThread);
		}
		waitPreviousBreakpoint(jdiThread);
		getActionConditionBreakpointManager().computeNotUsedBreakpoints();
		return false;
	}

	private void waitPreviousBreakpoint(JDIThread jdiThread) {
		while(suspend && (jdiThread.isSuspended() || jdiThread.isSuspendedQuiet())) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//ignore
			}
		}
	}

	protected JDIStackFrame computeNewStackFrame(JDIThread jdiThread) throws DebugException {
		List frames = jdiThread.computeNewStackFrames();
		JDIStackFrame frame = (JDIStackFrame)frames.get(0);
		return frame;
	}

	private IJavaProject getJavaProject() {
		if(fProject==null) {
			IProject project = this.getMarker().getResource().getProject();
			fProject = EclipseResourceUtil.getJavaProject(project);
		}
		return fProject;
	}

	class EvaluationListener implements IEvaluationListener {
		public void evaluationComplete(IEvaluationResult result) {
			JDIThread thread = (JDIThread)result.getThread();
			Event event = (Event)fSuspendEvents.get(thread);
			if (result.hasErrors()) {
				threadResumeQuiet(thread);
				return;
			}
			try {
				IValue value = result.getValue();
				if (value instanceof IJavaPrimitiveValue) {
					// Suspend when the condition evaluates true
					IJavaPrimitiveValue javaValue = (IJavaPrimitiveValue)value;
					if (isConditionSuspendOnTrue()) {
						if (javaValue.getJavaType().getName().equals("boolean") && javaValue.getBooleanValue()) {
							suspendForCondition(event, thread);
							getActionConditionBreakpointManager().setStopHandleBreakpoints(true);
							return;
						}
					}
				}
				int suspendPolicy = SUSPEND_THREAD;
				try {
					suspendPolicy = getSuspendPolicy();
				} catch (CoreException e) {
					//ignore
				}
				if (suspendPolicy == SUSPEND_VM) {
					((JDIDebugTarget)thread.getDebugTarget()).resumeQuiet();
				} else {
					threadResumeQuiet(thread);
				}
				return;
			} catch (DebugException e) {
				JDIDebugPlugin.log(e);
			}
			suspendForEvent(event, thread);
		}
	}

}