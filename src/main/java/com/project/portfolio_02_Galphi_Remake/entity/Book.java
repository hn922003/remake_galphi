package com.project.portfolio_02_Galphi_Remake.entity;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

import com.project.portfolio_02_Galphi_Remake.vo.BookVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int ISBN;
	@Column
    private String title;
	@Column
    private String author;
	@Column
    private String publisher;
	@Column
    private LocalDate pdate; // 카멜표기법(pDate)으로 적으면 스프링부트가 스네이크표기법(p_date)으로 인식한다. 주의!!
	@Column
    private String category;
	@Column
    private float avg;
	@Column
    private String chap;
	@Column
    private String summary;
	@Column
    private String review;
	
	public static Book voToEntity(BookVO bookVO) { // vo를 엔티티로 변환
		//System.out.println("BookVO 객체 => Book 엔티티객체");
		return new Book(
					bookVO.getISBN(), 
					bookVO.getTitle(), 
					bookVO.getAuthor(), 
					bookVO.getPublisher(), 
					bookVO.getPdate(), 
					bookVO.getCategory(), 
					bookVO.getAvg(), 
					bookVO.getChap(), 
					bookVO.getSummary(), 
					bookVO.getReview()
				);
	}
}
