package com.ehsure;

import java.util.List;

public class RedisInputDto implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9200590258223910396L;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public List<RedisInputDto> getDtos() {
		return dtos;
	}
	public void setDtos(List<RedisInputDto> dtos) {
		this.dtos = dtos;
	}
	public void addDtos(RedisInputDto dto) {
		this.dtos.add(dto);
	}
	private String name ;
	private String desc ;
	private List<RedisInputDto> dtos;
	
	public RedisInputDto() {
	}
	
	public RedisInputDto(String name , String desc) {
		this.name = name ;
		this.desc = desc ;
	}
	
}
