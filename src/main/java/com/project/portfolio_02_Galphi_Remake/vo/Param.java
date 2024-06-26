package com.project.portfolio_02_Galphi_Remake.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Param {
	private int idx;
	private String memo;
	private Float score;
	private int startNo;
	private int endNo;
	private String list;
	private String category;
	private String item;
	private int ISBN;
	private float avg;
	
	public Param(int idx, String memo, Float score) {
		super();
		this.idx = idx;
		this.memo = memo;
		this.score = score;
	}
	
	public Param(int ISBN, float avg) {
		super();
		this.ISBN = ISBN;
		this.avg = avg;
	}

	public Param(int startNo, int endNo, String list) {
		super();
		this.startNo = startNo;
		this.endNo = endNo;
		this.list = list;
	}
}
