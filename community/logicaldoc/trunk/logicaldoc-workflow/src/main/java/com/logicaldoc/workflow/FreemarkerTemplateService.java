package com.logicaldoc.workflow;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.logicaldoc.core.security.User;
import com.logicaldoc.workflow.model.script.UserScriptObject;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerTemplateService implements TemplateService{

	public String transformToString(String text,
			Map<String, Object> modelProperties) {

		Template template = null;
		
		try {
			template = new Template("tmp", new StringReader(text),
					new Configuration());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		StringWriter sw = new StringWriter();
		try {
			template.process(modelProperties, sw);
			return sw.getBuffer().toString();
		} catch (Exception e){
			throw new RuntimeException(e);
		}

	}
	
	
	
	public static void main(String[] args) {
		TemplateService templateService = new FreemarkerTemplateService();
		HashMap test = new HashMap();
		
		User user = new User();
		user.setName("christoph");
		UserScriptObject uso = new UserScriptObject(user);
		
		test.put("user", uso);
		List<String> tesa =new LinkedList<String>();
		tesa.add("sdsada");
		tesa.add("gold");
		test.put("test", tesa);
		System.out.println(templateService.transformToString(
				"hello <#list test as x>" +
				"${x}<#if x_has_next>,</#if></#list>", 
				test));
	}
}
