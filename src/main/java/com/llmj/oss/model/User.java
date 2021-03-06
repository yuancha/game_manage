package com.llmj.oss.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String account;
	private String password;
	private Date loginTime;
	private int role;
}
