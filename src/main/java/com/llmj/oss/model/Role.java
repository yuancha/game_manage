package com.llmj.oss.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int role;
	private String power;
}
