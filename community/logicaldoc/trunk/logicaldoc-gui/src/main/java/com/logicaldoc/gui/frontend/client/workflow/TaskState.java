package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.util.Util;

/**
 * A box displaying a single task.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TaskState extends WorkflowState {

	public TaskState(boolean dropped) {
		super(dropped);
		title.setIcon(Util.imageUrl("task.png"));
	}
}