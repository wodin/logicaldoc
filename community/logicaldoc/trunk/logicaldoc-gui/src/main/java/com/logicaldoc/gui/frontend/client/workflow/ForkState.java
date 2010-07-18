package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.util.Util;

/**
 * A box displaying a single fork.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ForkState extends WorkflowState {

	public ForkState(boolean dropped) {
		super(dropped);
		title.setIcon(Util.imageUrl("fork.png"));
	}
}