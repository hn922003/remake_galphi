package com.project.portfolio_02_Galphi_Remake.vo;

import java.util.ArrayList;

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
public class BookList {

//	1페이지 분량의 메인글 목록과 페이징 작업에 사용할 8개의 변수를 기억하는 클래스
	private ArrayList<BookVO> list = new ArrayList<BookVO>();
	private int pageSize = 10;
	private int totalCount = 0;
	private int currentPage = 1; 
	private int totalPage = 0; 
	private int startNo = 0;
	private int endNo = 0; 
	private int startPage = 0;
	private int endPage = 0;
	
	public BookList(int pageSize, int totalCount, int currentPage) {
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.currentPage = currentPage;
		// pageSize, totalCout, currentPage를 제외한 나머지 변수를 계산해서 초기화시키는 메소드를 호출한다.
		calculator();
	}
	
	private void calculator() {
		totalPage = (totalCount - 1) / pageSize + 1;
		currentPage = currentPage > totalPage ? totalPage : currentPage;
		startNo = (currentPage - 1) * pageSize + 1;
		endNo = startNo + pageSize - 1;
		endNo = endNo > totalCount ? totalCount : endNo;
		startPage = (currentPage - 1) / 10 * 10 + 1;
		endPage = startPage + 9;
		endPage = endPage > totalPage ? totalPage : endPage;
	}
}
