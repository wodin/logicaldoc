package com.logicaldoc.bm;

public class TestDefinition {

	public String name;
	public String type;	
	public int testCount = 1;
	public long iterations = 0L;
	public long depth = 1L;
	
	public void setDepth(long depth) {
		this.depth = depth;
	}
	public void setIterations(long iterations) {
		this.iterations = iterations;
	}
	public void setTestCount(int testCount) {
		this.testCount = testCount;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setName(String name) {
		this.name = name;
	}

}
