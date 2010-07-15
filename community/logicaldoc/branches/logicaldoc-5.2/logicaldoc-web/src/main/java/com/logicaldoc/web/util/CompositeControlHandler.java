package com.logicaldoc.web.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.ui.ComponentRef;
import com.sun.facelets.tag.ui.ComponentRefHandler;
import com.sun.facelets.util.ReflectionUtil;

/**
 * This handler is used for a custom tags that helps in creating new facelets
 * when you need to pass method expressions. The problem with facelets is that
 * the user tag handler of facelets always creates ValueExpression objects for
 * each attribute in the source tag.
 * <p>
 * You can configure this tag through the attribute methodBinfings for example:<br>
 * &lt;ldoc:compositeControl methodBindings="action=java.lang.String;
 * actionListener=void javax.faces.event.ActionEvent;"><br>
 * ....<br/> &lt;/ldoc:compositeControl>
 * <p>
 * In this case attributes 'action' and 'actionListener' will be considered
 * method expressions instead of value expressions
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5
 */
public class CompositeControlHandler extends TagHandler {
	private final static Pattern METHOD_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(.+?)\\s*;\\s*");

	private final TagAttribute rendererType;

	private final TagAttribute componentType;

	private final TagAttribute methodBindings;

	private ComponentHandler componentHandler;

	/**
	 * @param config
	 */
	public CompositeControlHandler(TagConfig config) {
		super(config);
		rendererType = getAttribute("rendererType");
		componentType = getAttribute("componentType");
		methodBindings = getAttribute("methodBindings");
		componentHandler = new ComponentRefHandler(new ComponentConfig() {
			/**
			 * *
			 * 
			 * @see com.sun.facelets.tag.TagConfig#getNextHandler()
			 */
			public FaceletHandler getNextHandler() {
				return CompositeControlHandler.this.nextHandler;
			}

			public Tag getTag() {
				return CompositeControlHandler.this.tag;
			}

			public String getTagId() {
				return CompositeControlHandler.this.tagId;
			}

			/**
			 * @see com.sun.facelets.tag.jsf.ComponentConfig#getComponentType()
			 */
			public String getComponentType() {
				return (componentType == null) ? ComponentRef.COMPONENT_TYPE : componentType.getValue();
			}

			/**
			 * @see com.sun.facelets.tag.jsf.ComponentConfig#getRendererType()
			 */
			public String getRendererType() {
				return (rendererType == null) ? null : rendererType.getValue();
			}
		});
	}

	/**
	 * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
	 *      javax.faces.component.UIComponent)
	 */
	public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException,
			ELException {
		VariableMapper origVarMap = ctx.getVariableMapper();
		try {
			VariableMapperWrapper variableMap = new VariableMapperWrapper(origVarMap);
			ctx.setVariableMapper(variableMap);
			if (methodBindings != null) {
				String value = (String) methodBindings.getValue(ctx);
				Matcher match = METHOD_PATTERN.matcher(value);
				while (match.find()) {
					String var = match.group(1);
					ValueExpression currentExpression = origVarMap.resolveVariable(var);
					if (currentExpression != null) {
						try {
							FunctionMethodData methodData = new FunctionMethodData(var, match.group(2).split("\\s+"));
							MethodExpression mexpr = buildMethodExpression(ctx,
									currentExpression.getExpressionString(), methodData);
							variableMap.setVariable(var, new MethodValueExpression(currentExpression, mexpr));
						} catch (Exception ex) {
							throw new FacesException(ex);
						}
					}
				}
			}
			componentHandler.apply(ctx, parent);
		} finally {
			ctx.setVariableMapper(origVarMap);
		}
	}

	private MethodExpression buildMethodExpression(FaceletContext ctx, String expression, FunctionMethodData methodData)
			throws NoSuchMethodException, ClassNotFoundException {
		return ctx.getExpressionFactory().createMethodExpression(ctx, expression, methodData.getReturnType(),
				methodData.getArguments());
	}

	private class FunctionMethodData {
		private String variable;

		private Class returnType;

		private Class[] arguments;

		FunctionMethodData(String variable, String[] types) throws ClassNotFoundException {
			this.variable = variable;
			if ("null".equals(types[0]) || "void".equals(types[0]))
				returnType = null;
			else
				returnType = ReflectionUtil.forName(types[0]);
			arguments = new Class[types.length - 1];
			for (int i = 0; i < arguments.length; i++)
				arguments[i] = ReflectionUtil.forName(types[i + 1]);
		}

		public Class[] getArguments() {
			return this.arguments;
		}

		public void setArguments(Class[] arguments) {
			this.arguments = arguments;
		}

		public Class getReturnType() {
			return this.returnType;
		}

		public void setReturnType(Class returnType) {
			this.returnType = returnType;
		}

		public String getVariable() {
			return this.variable;
		}

		public void setVariable(String variable) {
			this.variable = variable;
		}
	}
}