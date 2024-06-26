package com.project.portfolio_02_Galphi_Remake.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.portfolio_02_Galphi_Remake.entity.Bookcomment;

public interface BookCommentRepository  extends JpaRepository<Bookcomment, Integer>{
	// ISBN에 해당하는 책의 모든 댓글을 얻어오는 메소드
	ArrayList<Bookcomment> findByISBN(int ISBN);
	
	// ISBN에 해당하는 책의 댓글 수를 얻어오는 메소드
	int countByISBN(int ISBN);
	
	// Idx에 해당하는 댓글 1건의 별점을 얻어오는 메소드
	float findScoreByIdx(int idx);


}
