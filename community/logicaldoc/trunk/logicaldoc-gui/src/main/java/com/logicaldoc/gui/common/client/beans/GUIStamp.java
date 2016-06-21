package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * Implementation of a Stamp
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.3
 */
public class GUIStamp implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id = 0L;

	public static int TYPE_TEXT = 0;

	public static int TYPE_IMAGE = 1;

	public static int PAGE_OPT_ALL = 0;

	public static int PAGE_OPT_LAST = 1;

	public static int PAGE_OPT_SEL = 2;

	private int type = TYPE_TEXT;

	private int pageOption = PAGE_OPT_ALL;

	private String pageSelection = "1";

	private int enabled = 1;

	private String name;

	private String description;

	private String text;

	private int opacity = 100;

	private int rotation = 0;

	private int size = 24;

	private String exprX;

	private String exprY;

	private String color = "black";

	private GUIUser[] users;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getOpacity() {
		return opacity;
	}

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public String getExprX() {
		return exprX;
	}

	public void setExprX(String exprX) {
		this.exprX = exprX;
	}

	public String getExprY() {
		return exprY;
	}

	public void setExprY(String exprY) {
		this.exprY = exprY;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public GUIUser[] getUsers() {
		return users;
	}

	public void setUsers(GUIUser[] users) {
		this.users = users;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPageOption() {
		return pageOption;
	}

	public void setPageOption(int pageOption) {
		this.pageOption = pageOption;
	}

	public String getPageSelection() {
		return pageSelection;
	}

	public void setPageSelection(String pageSelection) {
		this.pageSelection = pageSelection;
	}
}