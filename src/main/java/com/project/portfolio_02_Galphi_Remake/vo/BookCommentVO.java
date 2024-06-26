package com.project.portfolio_02_Galphi_Remake.vo;

import java.time.LocalDate;
import java.util.Date;

import com.project.portfolio_02_Galphi_Remake.entity.Bookcomment;

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
public class BookCommentVO {

	private int ISBN;
	private String nick;
	private int idx;
	private float score;
	private String memo;
	private LocalDate wdate;
	private boolean flag=false; // 댓글 수정 / 삭제 버튼 플래그

	// 5개 인수로 vo 생성시 => 자동으로 날짜 생성
	public BookCommentVO(int ISBN, String nick, int idx, float score, String memo) {
		super();
		this.ISBN = ISBN;
		this.nick = nick;
		this.idx = idx;
		this.score = score;
		this.memo = memo;
		this.wdate = LocalDate.now();
	}	
	
	// entity 6개 인수 꺼낼시 엔티티의 날짜로 생성
	public static BookCommentVO entityToVO(Bookcomment bookcomment) {
		//System.out.println("Comment 엔티티 객체 => Comment VO 객체");
		return new BookCommentVO(
			bookcomment.getISBN(),
			bookcomment.getNick(),
			bookcomment.getIdx(), 
			bookcomment.getScore(), 
			bookcomment.getMemo(),
			bookcomment.getWdate(),
			false
		);
	}
}
