package com.project.portfolio_02_Galphi_Remake.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.project.portfolio_02_Galphi_Remake.vo.BookCommentVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@SequenceGenerator(
	name = "gen",
	sequenceName = "BOOKCOMMENT_IDX_SEQ",
	schema = "TJOEUNIT",
	allocationSize = 1
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Bookcomment {
	@Column
	private int ISBN;
	@Column
	private String nick;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")  
	private int idx;
	@Column
	private float score;
	@Column
	private String memo;
	@Column
	private LocalDate wdate;
	
	public static Bookcomment voToEntity(BookCommentVO bookCommentVO) { // vo => 엔티티
		System.out.println("CommentVO 객체 => Comment 엔티티객체");
		return new Bookcomment(
			bookCommentVO.getISBN(),
			bookCommentVO.getNick(),
			bookCommentVO.getIdx(),
			bookCommentVO.getScore(),
			bookCommentVO.getMemo(),
			// 입력시간이 null이면 현재 시간으로 넣어준다.
			bookCommentVO.getWdate() == null ? LocalDate.now():bookCommentVO.getWdate() 
		);
	}

}
