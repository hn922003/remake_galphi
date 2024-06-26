package com.project.portfolio_02_Galphi_Remake.vo;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.project.portfolio_02_Galphi_Remake.entity.Book;

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
public class BookVO {
   private int ISBN;
   private String title;
   private String author;
   private String publisher;
   @DateTimeFormat(pattern = "yyyy년 MM월 dd일")
   private LocalDate pdate;
   private String category;
   private float avg;
   private String chap;
   private String summary;
   private String review;	
   
   public static BookVO entityToVO(Book book) {
	   //System.out.println("Book 엔티티 객체 => Book VO 객체");
	   // 평점을 포맷형식으로 변경
	   DecimalFormat df1 = new DecimalFormat("##.#");
	   float avg = Float.parseFloat(df1.format(book.getAvg()));
	   book.setAvg(avg);
	   return new BookVO(
				   book.getISBN(), 
				   book.getTitle(), 
				   book.getAuthor(), 
				   book.getPublisher(), 
				   book.getPdate(), 
				   book.getCategory(), 
				   book.getAvg(), 
				   book.getChap(), 
				   book.getSummary(), 
				   book.getReview()
			   );
   }
}
