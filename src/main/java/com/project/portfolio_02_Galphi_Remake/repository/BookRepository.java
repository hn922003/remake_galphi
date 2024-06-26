package com.project.portfolio_02_Galphi_Remake.repository;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.portfolio_02_Galphi_Remake.entity.Book;
import com.project.portfolio_02_Galphi_Remake.vo.BookList;
import com.project.portfolio_02_Galphi_Remake.vo.BookVO;

public interface BookRepository  extends JpaRepository<Book, Integer>{
	
	// 베스트 도서 리스트 얻어온다 => 평점 높은 순 10권
	@Query(value = "select * from (select rownum rnum, TT.* from (select * from book order by avg desc) TT where rownum <= 10) where rnum >= 1", nativeQuery = true)
	ArrayList<Book> selectBestList(); // 엔티티 객체로 반환
	
	// 신간 도서의 전체 개수를 얻어온다. => 180일안에 출간된 도서
	@Query(value = "select count(*) from book where TO_DATE(pDate) > sysdate - 180", nativeQuery = true)
	int selectNewCount();
	
	// 1페이지 분량의 신간 도서 리스트를 얻어온다. => 180일안에 출간된 도서
	@Query(value = "select * from (select rownum rnum, TT.* from (select * from book where TO_DATE(pDate) > sysdate - 180 order by pdate desc) TT where rownum <= :endNo) where rnum >= :startNo", nativeQuery = true)
	ArrayList<Book> selectNewList(@Param("startNo") int startNo,@Param("endNo") int endNo);
	
	// 오늘의 추천도서 10권을 랜덤으로 얻어온다 => 평점 9점이상 랜덤 10권
	@Query(value = "select * from (select rownum rnum, TT.* from (select * from book where avg > 9 order by DBMS_RANDOM.RANDOM) TT where rownum <= 10) where rnum >= 1", nativeQuery = true)
	ArrayList<Book> selectDailyList();
	
	// 추천 도서 전체 개수를 얻어온다. => 평점 9점이상 전체
	@Query(value = "select count(*) from book where avg > 9", nativeQuery = true)
	int selectDailyCount();
	
	// 1페이지 분량의 추천 도서 리스트를 얻어온다.
	//@Query(value = "select * from book where avg > 9", nativeQuery = true)
	@Query(value = "select * from (select rownum rnum, TT.* from (select * from book where avg > 9) TT where rownum <= :endNo) where rnum >= :startNo", nativeQuery = true)
	ArrayList<Book> selectRecommentList(@Param("startNo") int startNo,@Param("endNo") int endNo);
	
	// 해당 카테고리 전체 개수를 얻어온다
	@Query(value = "select count(*) from book where category = :kind", nativeQuery = true)
	int selectCategoryCount(@Param("kind") String kind);
	
	// 카테고리에 해당하는 1페이지 분량의 도서 리스트를 얻어온다.
	@Query(value = "select * from (\r\n"
			+ "			select rownum rnum, TT.* from (\r\n"
			+ "				select * from book where category = :kind\r\n"
			+ "			) TT where rownum <= :endNo\r\n"
			+ "		) where rnum >= :startNo", nativeQuery = true)
	ArrayList<Book> selectCategoryList(@Param("startNo") int startNo, @Param("endNo") int endNo, @Param("kind") String kind);
	
	// DB에 존재하는 도서 전체 개수를 얻어온다.
	@Query(value = "select count(*) from book", nativeQuery = true)
	int selectCount();
	
	// DB에 존재하는 도서 중에 1페이지 분량을 얻어온다.
	@Query(value = "select * from (\r\n"
			+ "			select rownum rnum, TT.* from (\r\n"
			+ "				select * from book\r\n"
			+ "			) TT where rownum <= :endNo \r\n"
			+ "		) where rnum >= :startNo", nativeQuery = true)
	ArrayList<Book> selectList(@Param("startNo") int startNo, @Param("endNo") int endNo);
	
	// 유형별 검색결과의 도서 개수를 얻어온다.
	int countByTitleLike(@Param ("title") String word); // 제목
	int countByAuthorLike(@Param ("author") String word); // 저자
	int countByPublisherLike(@Param ("publisher") String word); // 출판사

	// 검색 결과 전체를 얻어와서 서비스에서 페이징한다.
	ArrayList<Book> findByTitleContaining(@Param ("title") String word); // 제목
	ArrayList<Book> findByAuthorContaining(@Param ("author") String word); // 저자
	ArrayList<Book> findByPublisherContaining(@Param ("publisher") String word); // 출판사
	
//=======================








}
