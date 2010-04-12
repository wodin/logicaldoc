package com.logicaldoc.workflow.transform;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;

public class JBPMTransformContext extends TransformContext {

	private Document documentElement;

	public JBPMTransformContext(List<BaseWorkflowModel> list) {
		super(list);
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = null;

		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		this.documentElement = documentBuilder.newDocument();

	}
	
	public Document getDocumentBuildObject(){
		return this.documentElement;
	}
}
