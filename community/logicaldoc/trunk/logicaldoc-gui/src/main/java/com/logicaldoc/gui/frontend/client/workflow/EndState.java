package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.util.Util;

/**
 * A box displaying a single end state.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class EndState extends WorkflowState {

	public EndState(boolean dropped) {
		super(dropped);
		title.setIcon(Util.imageUrl("endState.png"));
	}
}