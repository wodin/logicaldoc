package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.widgets.layout.VStack;

/**
 * Where the workflow diagram is drawn
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DrawingPanel extends VStack {
	public DrawingPanel() {
		setHeight(50);
		setMembersMargin(5);
		setCanAcceptDrop(true);
		setAnimateMembers(true);
		setShowDragPlaceHolder(true);
		setDropTypes("component");

		// HStack hStack = new HStack(10);
		// hStack.setLayoutMargin(10);
		// hStack.setLeft(120);
		// //hStack.setShowEdges(true);
		// // hStack.setEdgeImage("edges/edge.png");
		// hStack.setCanAcceptDrop(true);
		// hStack.setAnimateMembers(true);
		// hStack.setShowDragPlaceHolder(true);
		// hStack.setBorder("1px dotted #4040ff");
		// hStack.addMember(new DragPiece("cube_blue.png"));
		// hStack.addMember(new DragPiece("cube_green.png"));
		// hStack.addMember(new DragPiece("cube_yellow.png"));
		// VStack vStack = new VStack(10);
		// vStack.setLayoutMargin(10);
		// //vStack.setShowEdges(true);
		// // vStack.setEdgeImage("edges/green/6.png");
		// vStack.setCanAcceptDrop(true);
		// vStack.setAnimateMembers(true);
		// vStack.setDropLineThickness(4);
		// Canvas dropLineProp = new Canvas();
		// dropLineProp.setBackgroundColor("#40c040");
		// vStack.setDropLineProperties(dropLineProp);
		// vStack.addMember(new DragPiece("cube_blue.png"));
		// vStack.addMember(new DragPiece("cube_green.png"));
		// vStack.addMember(new DragPiece("cube_yellow.png"));

		addMember(new WorkflowComponent());
		addMember(new WorkflowComponent());
	}

	// private class DragPiece extends VLayout {
	// public DragPiece(String imgname) {
	// setCanDragReposition(true);
	// setCanDrop(true);
	// setDragAppearance(DragAppearance.TARGET);
	//
	// HTML title = new HTML(imgname);
	// addMember(title);
	// title.setHeight("15px");
	//
	// Img img = ItemFactory.newImg(imgname);
	// img.setWidth(48);
	// img.setHeight(48);
	// img.setLayoutAlign(Alignment.CENTER);
	// img.setAppImgDir(Util.imagePrefix());
	// addMember(img);
	//
	// setWidth(48);
	// setHeight(48);
	// }
	// }
}