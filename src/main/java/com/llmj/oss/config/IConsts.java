package com.llmj.oss.config;

public interface IConsts {
	
	enum UpFileType {

		Android(0,"android"),
		Ios(1,"ios"),
		;
		private int type;
		private String desc;
		
		private UpFileType(int type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public int getType() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
		
	}
	
	enum UpFileState {
		
		original(0,"刚刚上传"),
		up2oss(1,"已上传oss"),
		online(2,"线上版本"),
		delete(3,"已删除"),
		;
		private int state;
		private String desc;
		
		private UpFileState(int state, String desc) {
			this.state = state;
			this.desc = desc;
		}

		public int getState() {
			return state;
		}

		public String getDesc() {
			return desc;
		}
		
	}
	
	enum UpFileTable {

		test(0, "upload_file_test"), 
		online(1, "upload_file_online"),
		;
		private int state;
		private String tableName;

		private UpFileTable(int state, String tableName) {
			this.state = state;
			this.tableName = tableName;
		}

		public int getState() {
			return state;
		}

		public String getTableName() {
			return tableName;
		}

	}
}
