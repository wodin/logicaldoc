package com.logicaldoc.web.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

/**
 * This utility bean is used in combination with CompositeControlHandler in
 * order to mask a ValueExpression with a MethodExpression. Basically this is a
 * wrapper of a MethodExpression.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version $Id:$
 * @since 3.5
 */
public class MethodValueExpression extends ValueExpression implements Externalizable {
	private ValueExpression orig;

	private MethodExpression methodExpression;

	public MethodValueExpression() {
	}

	MethodValueExpression(ValueExpression orig, MethodExpression methodExpression) {
		this.orig = orig;
		this.methodExpression = methodExpression;
	}

	@Override
	public Class getExpectedType() {
		return orig.getExpectedType();
	}

	@Override
	public Class getType(ELContext ctx) {
		return MethodExpression.class;
	}

	@Override
	public Object getValue(ELContext ctx) {
		return methodExpression;
	}

	@Override
	public boolean isReadOnly(ELContext ctx) {
		return orig.isReadOnly(ctx);
	}

	@Override
	public void setValue(ELContext ctx, Object val) {
	}

	@Override
	public boolean equals(Object val) {
		return orig.equals(val);
	}

	@Override
	public String getExpressionString() {
		return orig.getExpressionString();
	}

	@Override
	public int hashCode() {
		return orig.hashCode();
	}

	@Override
	public boolean isLiteralText() {
		return orig.isLiteralText();
	}

	/**
	 * *
	 * 
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		orig = (ValueExpression) in.readObject();
		methodExpression = (MethodExpression) in.readObject();
	}

	/**
	 * *
	 * 
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(orig);
		out.writeObject(methodExpression);
	}
}