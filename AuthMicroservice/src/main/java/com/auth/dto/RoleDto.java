package com.auth.dto;

public class RoleDto {
	
		Long id;


	 	String name;
	    
	    


		public RoleDto() {
			super();
			// TODO Auto-generated constructor stub
		}


		public RoleDto(Long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}


		public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}
	    
	    
}
