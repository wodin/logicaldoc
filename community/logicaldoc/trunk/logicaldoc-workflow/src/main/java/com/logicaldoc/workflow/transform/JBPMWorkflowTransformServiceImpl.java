package com.logicaldoc.workflow.transform;

import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import com.logicaldoc.workflow.debug.TRANSMITTER;
import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.WorkflowEditorException;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;
import com.thoughtworks.xstream.XStream;

public class JBPMWorkflowTransformServiceImpl implements
		WorkflowTransformService {

	private XStream xStream;

	private List<TransformModel> componentTransformers;

	public void setXStream(XStream stream) {
		xStream = stream;
	}

	public void setComponentTransformer(
			List<TransformModel> componentTransformers) {
		this.componentTransformers = componentTransformers;
	}
	
	@Override
	public List<BaseWorkflowModel> fromWorkflowDefinitionToObject(
			WorkflowPersistenceTemplate workflowTemplateModel) {
	
		return (List<BaseWorkflowModel>)xStream.fromXML((String)workflowTemplateModel.getXmldata());
	}

	@Override
	public Object fromObjectToWorkflowDefinition(
			WorkflowPersistenceTemplate workflowTemplateModel) {

		List<BaseWorkflowModel> workflowComponents = this
				.retrieveWorkflowModels(workflowTemplateModel.getXmldata());

		JBPMTransformContext transformContext = new JBPMTransformContext(
				workflowComponents);

		Element processDefininiton = transformContext.getDocumentBuildObject().createElement("process-definiton");
		processDefininiton.setAttribute("xmlns", "urn:jbpm.org:jpdl-3.2");
		processDefininiton.setAttribute("name", workflowTemplateModel.getName());

		//the start-state
		Element startState = transformContext.getDocumentBuildObject().createElement("start-state");
		startState.setAttribute("name", "LogicalDOC Workflow StartState");
		Element defTransition = transformContext.getDocumentBuildObject().createElement("transition");
		defTransition.setAttribute("to", workflowTemplateModel.getStartState());
		startState.appendChild(defTransition);
		processDefininiton.appendChild(startState);
		
		while (transformContext.hasNext()) {
			
			BaseWorkflowModel currentModel = transformContext.next();
			
			TransformModel transformer = getFirstMatchingTransformerFor(currentModel);
			
			Element val = (Element)transformer.open(transformContext);
			processDefininiton.appendChild(val);
		}
		
		String stringData = this.retrieveStringFromXMLElement(processDefininiton);
		//DEBUG
		TRANSMITTER.XML_DATA = stringData;

		return stringData;
	}

	private TransformModel getFirstMatchingTransformerFor(
			BaseWorkflowModel model) {
		for (TransformModel transformer : this.componentTransformers) {
			if (transformer.matches(model))
				return transformer;
		}

		throw new IllegalStateException(
				"No valid ComponentTransformer for Model "
						+ model.getClass().getName() + " has been found");
	}

	@SuppressWarnings("unchecked")
	private List<BaseWorkflowModel> retrieveWorkflowModels(Serializable binarayContent) {
		
		List<BaseWorkflowModel> workflowComponents = (List<BaseWorkflowModel>) this.xStream
				.fromXML((String)binarayContent);

		return workflowComponents;
	
	}
	
	private String retrieveStringFromXMLElement(Element elm){
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			 
        	Source source = new DOMSource(elm);
			Transformer transformer = factory.newTransformer();
			StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}" + "indent-amount", "2");
            //Just for TESTPURPOESES XML_DECLARATION IS DISABLED
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
}
