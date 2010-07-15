package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;

/**
 * A box displaying a single task.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TransitionBox extends Label {

	public TransitionBox() {
		super("Task Name");

		setHeight(40);
		setWidth(100);
		setBorder("1px solid #4040ff");
		setAlign(Alignment.CENTER);
	}
}