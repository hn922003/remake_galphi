package com.project.portfolio_02_Galphi_Remake.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.portfolio_02_Galphi_Remake.entity.Book;
import com.project.portfolio_02_Galphi_Remake.entity.Bookcomment;
import com.project.portfolio_02_Galphi_Remake.entity.Usertable;
import com.project.portfolio_02_Galphi_Remake.repository.AccountRepository;
import com.project.portfolio_02_Galphi_Remake.repository.BookCommentRepository;
import com.project.portfolio_02_Galphi_Remake.repository.BookRepository;
import com.project.portfolio_02_Galphi_Remake.vo.AccountVO;
import com.project.portfolio_02_Galphi_Remake.vo.BookCommentVO;
import com.project.portfolio_02_Galphi_Remake.vo.BookList;
import com.project.portfolio_02_Galphi_Remake.vo.BookVO;

@Service
public class BookService {
	@Autowired // 자동 mapper 초기화
	private BookRepository bookRepository;
	@Autowired // 자동 mapper 초기화
	private BookCommentRepository bookCommentRepository;
	@Autowired
	private AccountRepository accountRepository;
	
	// 베스트 도서 전체(10권) 리스트 불러오기
	public ArrayList<BookVO> selectBestList() {
		System.out.println("서비스 클래스의 selectBestList() 실행");
		return  (ArrayList<BookVO>)bookRepository.selectBestList()
				.stream()
				.map(book -> BookVO.entityToVO(book))
				.collect(Collectors.toList());
	}

	// 신간 도서 총 개수 불러오기
	public int selectNewCount() {
		System.out.println("서비스 클래스의 selectNewCount() 실행");
		return bookRepository.selectNewCount();
	}
	
	// 신간 도서 10권 랜덤 뽑기(전체 불러와서 그 중에 랜덤 10권)
	public ArrayList<BookVO> selectNewTen() {
		System.out.println("서비스 클래스의 selectNewTen() 실행");
		int newCount = selectNewCount(); //신간 전체를 얻어온다.(신간 갯수만큼)
		ArrayList<BookVO> bookNewListAll = selectNewList(1, newCount);
		ArrayList<BookVO> newList = new ArrayList<BookVO>(); // 랜덤 10권 저장할 빈 객체
		int len = bookNewListAll.size(); // 얻어온 리스트 개수
		//newList.get(0).getPdate().toString().substring(0, 9);
		Random random = new Random();
		ArrayList<Integer> flags = new ArrayList<Integer>(); //flag리스트
		int flag = random.nextInt(len); // 랜덤 숫자 기억
		while (flags.size() != 10) {
			if (!flags.contains(flag)) {
				flags.add(flag);
				newList.add(bookNewListAll.get(flag));
			}
			flag = random.nextInt(len);
		}
		return newList;
	}
	
	// 1페이지 분량의 신간도서 불러오기
	public ArrayList<BookVO> selectNewList(int startNo, int endNo) {
		System.out.println("서비스 클래스의 selectNewList() 실행");
		return  (ArrayList<BookVO>)bookRepository.selectNewList(startNo, endNo)
				.stream()
				.map(book -> BookVO.entityToVO(book))
				.collect(Collectors.toList());
	}
	
	// 오늘의 추천도서 불러오기(DB 자체에서 랜덤 10권)
	public ArrayList<BookVO> selectDailyList() {
		System.out.println("서비스 클래스의 selectDailyList() 실행");
		return  (ArrayList<BookVO>)bookRepository.selectDailyList()
				.stream()
				.map(book -> BookVO.entityToVO(book))
				.collect(Collectors.toList());
	}

	// 리스트별 전체 도서 목록 불러오는 메소드(베스트리스트 제외)
	public BookList selectKindList(String kind, int currentPage) { 
		System.out.println("서비스 클래스의 selectKindList() 실행");
		// 전체 10권으로 고정된 베스트는 selectBestList() 별도 메소드 이용
		
		//고정된 페이징크기
		int pageSize = 10;
		int startNo = 1;
		int endNo = 10;
		// 나머지는 해당 리스트 전체수를 얻어와 페이징 후,
		// BookList의 List 필드에 저장한다.
		BookList bookList = null;
		
		// kind와 일치하는 주제의 리스트를 1페이지 분량으로 불러온다
		if (kind.equals("new")) { // 신간도서 1페이지 분량
			int totalCount = selectNewCount(); // 전체 신간수 
			bookList = new BookList(pageSize, totalCount, currentPage); // 페이징
			startNo = bookList.getStartNo();
			endNo = bookList.getEndNo();
			bookList.setList(selectNewList(startNo, endNo));
		}
		
		if (kind.equals("daily")) { // 추천도서 1페이지 분량을 얻어온다.
			int totalCount = selectDailyCount(); // 전체 추천도서 수 
			bookList = new BookList(pageSize, totalCount, currentPage); // 페이징
			startNo = bookList.getStartNo();
			endNo = bookList.getEndNo();
			bookList.setList(selectRecommentList(startNo, endNo));
		}
		
		// category(소설, 계발, IT, 역사, 아동)에 해당하는 도서 1페이지 분량을 얻어온다
		List<String> category = Arrays.asList("novel", "develop", "it", "history", "child");
		if (category.contains(kind)) { // ㅇ
			int totalCount = selectCategoryCount(kind); // 해당 카테고리 전체 도서 수 
			bookList = new BookList(pageSize, totalCount, currentPage); // 페이징
			startNo = bookList.getStartNo();
			endNo = bookList.getEndNo();
			bookList.setList(selectCategoryList(startNo, endNo, kind));
		}
		
		return bookList;
	}

	// 전체 추천 도서의 개수를 얻어오는 메소드
	private int selectDailyCount() {
		System.out.println("서비스 클래스의 selectDailyCount() 실행");
		return bookRepository.selectDailyCount();
	}
	
	// 1페이지 분량의 추천 도서를 얻어오는 메소드
	private ArrayList<BookVO> selectRecommentList(int startNo, int endNo) {
		System.out.println("서비스 클래스의 selectRecommentList() 실행");
		return  (ArrayList<BookVO>)bookRepository.selectRecommentList(startNo, endNo)
				.stream()
				.map(book -> BookVO.entityToVO(book))
				.collect(Collectors.toList());
	}

	// 카테고리에 해당하는 전체 도서의 개수를 얻어오는 메소드
	private int selectCategoryCount(String kind) {
		System.out.println("서비스 클래스의 selectCategoryCount() 실행");
		return bookRepository.selectCategoryCount(kind);
	}
	
	// 카테고리에 해당하는 1페이지 분량의 도서를 얻어오는 메소드
	private ArrayList<BookVO> selectCategoryList(int startNo, int endNo, String kind) {
		System.out.println("서비스 클래스의 selectCategoryList() 실행");
		return  (ArrayList<BookVO>)bookRepository.selectCategoryList(startNo, endNo, kind)
				.stream()
				.map(book -> BookVO.entityToVO(book))
				.collect(Collectors.toList());
	}
	
	//검색 결과 리스트를 얻어오는 메소드
	public BookList searchList(String word, String type, int currentPage) {
		System.out.println("서비스 클래스의 searchList() 실행");
		
		// 페이징 변수 설정
		int pageSize = 10;
		int startNo = 1;
		int endNo = 10;
		BookList searchList = new BookList();
		
		// 검색 상황별로 메소드를 실행한다.
		// 1. 검색어가 없거나 공백일 때 => DB의 모든 책을 리스트로 얻어온다.
		if (word == null || word.trim().length() == 0) {
			int totalCount = selectCount(); // 전체 도서 수 
			searchList = new BookList(pageSize, totalCount, currentPage); // 페이징
			startNo = searchList.getStartNo();
			endNo = searchList.getEndNo();
			searchList.setList(selectList(startNo, endNo));
		}
		// 2. 검색어가 있을 때 => 검색유형과 검색어에 해당되는 리스트를 얻어온다.
		else { 
			int totalCount = selectSearchCount(type, word); // 검색 결과 도서 수를 얻어온다.
			System.out.println("totalcount================>" + totalCount);
			searchList = new BookList(pageSize, totalCount, currentPage); // 페이징
			startNo = searchList.getStartNo();
			endNo = searchList.getEndNo();
			searchList.setList(selectSearchList(type, word, startNo, endNo));
			
			// => 검색어를 하이라이트 처리해준다.(페이지에 나온 단어 전체)
			for (int i=0; i <searchList.getList().size(); i++) {
				String title = searchList.getList().get(i).getTitle();
				String author = searchList.getList().get(i).getAuthor();
				String publisher = searchList.getList().get(i).getPublisher();
				String review = searchList.getList().get(i).getReview();
				String summary = searchList.getList().get(i).getSummary();
				title = title.replace(word, "<span style='background-color: hotpink;'>" + word + "</span>");
				author = author.replace(word, "<span style='background-color: hotpink;'>" + word + "</span>");
				publisher = publisher.replace(word, "<span style='background-color: hotpink;'>" + word + "</span>");
				try { // review나 summary 없는 글...처리
					review = review.replace(word, "<span style='background-color: hotpink;'>" + word + "</span>");
					summary = summary.replace(word, "<span style='background-color: hotpink;'>" + word + "</span>");
				} catch (Exception e) {
				}
				searchList.getList().get(i).setTitle(title);
				searchList.getList().get(i).setAuthor(author);
				searchList.getList().get(i).setPublisher(publisher);
				searchList.getList().get(i).setReview(review);
				searchList.getList().get(i).setSummary(summary);
			}
		}
		return searchList;
	}
	
	// 검색 결과에 해당하는 1페이지 분량의 책을 얻어오는 메소드
	
	private ArrayList<BookVO> selectSearchList(String type, String word, int startNo, int endNo) {
		System.out.println("서비스 클래스의 selectSearchList() 실행");
		ArrayList<BookVO> AllList = new ArrayList<BookVO>();
		ArrayList<BookVO> finalList = new ArrayList<BookVO>();
		
		// SQL문에 쓸 검색유형을 설정한다.
		if (type.equals("제목")) {
			type = "author";
			AllList = (ArrayList<BookVO>)bookRepository.findByTitleContaining(word)
					.stream()
					.map(book -> BookVO.entityToVO(book))
					.collect(Collectors.toList());
		}
		if (type.equals("저자")) {
			type = "author";
			AllList = (ArrayList<BookVO>)bookRepository.findByAuthorContaining(word)
					.stream()
					.map(book -> BookVO.entityToVO(book))
					.collect(Collectors.toList());
		}
		if (type.equals("출판사")) {
			type = "publisher";
			AllList = (ArrayList<BookVO>)bookRepository.findByPublisherContaining(word)
					.stream()
					.map(book -> BookVO.entityToVO(book))
					.collect(Collectors.toList());
		}
		// 페이징 처리
		try {
			for (int i = startNo; i<= endNo; i++) {
				finalList.add(AllList.get(i-1));
			}
		} catch (Exception e) {
			for (int i = startNo; i<= AllList.size(); i++) {
				finalList.add(AllList.get(i-1));
			}
		}
		return finalList;
	}
	
	// 검색 결과에 해당하는 책의 개수를 얻어오는 메소드
	private int selectSearchCount(String type, String word) {
		System.out.println("서비스 클래스의 selectSearchCount() 실행");
		// SQL 와일드 카드 => "%검색어%" 모양을 만들어준다.
		word = "%" + word + "%"; // Like 메소드는 %단어% / Containing은 와일드카드 없이 검색어만..
		
		// SQL문에 쓸 검색유형을 설정한다.
		if (type.equals("저자")) {
			return bookRepository.countByAuthorLike(word);
		}
		if (type.equals("출판사")) {
			return bookRepository.countByPublisherLike(word);
		}
		// 기본적으로 제목 검색결과 리턴
		return bookRepository.countByTitleLike(word);
	}

	// 1페이지 분량의 DB 도서를 얻어오는 메소드
	private ArrayList<BookVO> selectList(int startNo, int endNo) {
		System.out.println("서비스 클래스의 selectList() 실행");
		return  (ArrayList<BookVO>)bookRepository.selectList(startNo, endNo)
				.stream()
				.map(book -> BookVO.entityToVO(book))
				.collect(Collectors.toList());
	}

	// DB에 저장된 모든 책의 개수를 얻어오는 메소드
	private int selectCount() {
		System.out.println("서비스 클래스의 selectCount() 실행");
		return bookRepository.selectCount();
	}
	
	// 책 1권을 얻어오는 메소드
	public BookVO selectByISBN(int ISBN) {
		System.out.println("서비스 클래스의 selectByISBN() 실행");
		//==============================***********************검색 결과 벗어날 때 터지는거 처리해주기
 		return BookVO.entityToVO(bookRepository.findById(ISBN).orElse(null));
	}
	
	
	//===============================================================
	// Comment 메소드
	//===============================================================
	
	// 책 한권에 해당하는 모든 댓글을 얻어오는 메소드
	public ArrayList<BookCommentVO> selectCommentByISBN(int ISBN) {
		System.out.println("서비스 클래스의 selectCommentByISBN() 실행");
		return (ArrayList<BookCommentVO>) bookCommentRepository.findByISBN(ISBN)
				.stream()
				.map(comment -> BookCommentVO.entityToVO(comment))
				.collect(Collectors.toList());
	}
	
	@Transactional // 트랙잭션 적용
	// 댓글 1건을 저장하고 그 글의 평점을 조정하는 메소드
	public void insertComment(BookCommentVO bookCommentVO) {
		System.out.println("서비스 클래스의 insertComment() 실행");
		// 데이터 유효성은 컨트롤러와 뷰에서 이미 체크 완료..
		//System.out.println(bookCommentVO.getScore() + "===========");
		// vo객체를 엔티티로 변환해서 저장한다.
		bookCommentRepository.save(Bookcomment.voToEntity(bookCommentVO));
			
		// 댓글이 저장된 책의 평점을 수정한다.
		BookVO vo = selectByISBN(bookCommentVO.getISBN());
		int size = bookCommentRepository.countByISBN(bookCommentVO.getISBN());
		float score = bookCommentVO.getScore();
		// 책의 평점 => (책의평점 * 댓글수 + 입력점수) / (댓글수+1)
		vo.setAvg((vo.getAvg() * size + score) / (size + 1));
		// 엔티티로 변환해서 저장한다.
		bookRepository.save(Book.voToEntity(vo));
	}
	
	@Transactional
	// 댓글 1건의 내용과 평점을 수정하는 메소드
	public void updateComment(BookCommentVO bookCommentVO) {
		System.out.println("서비스 클래스의 updateComment() 실행");
		try { //별점을 정수로 입력했는지 확인한다.
			bookCommentVO.setScore((int) bookCommentVO.getScore()); 
			// 입력하지 않은 정보가 있는지 확인한다.
			if (bookCommentVO.getMemo() != null) {
				throw new IllegalArgumentException("댓글 내용을 입력하세요.");
			}
			if (bookCommentVO.getScore() > 10 || bookCommentVO.getScore() < 0) {
				throw new IllegalArgumentException("별점을 0 ~ 10 사이로 입력해 주세요.");
			}
			
			// vo객체를 엔티티로 변환해서 수정된 정보를 저장한다.
			bookCommentRepository.save(Bookcomment.voToEntity(bookCommentVO));
			
			// 댓글이 저장된 책의 평점을 수정한다.
			BookVO vo = selectByISBN(bookCommentVO.getISBN());
			// 오리지널 별점을 가져온다.
			float original = bookCommentRepository.findScoreByIdx(bookCommentVO.getIdx());
			int size = bookCommentRepository.countByISBN(bookCommentVO.getISBN());
			float score = bookCommentVO.getScore();
			// 책의 평점 => (책의평점 * 댓글수 + 입력점수 - 오리지날점수) / (댓글수)
			vo.setAvg((vo.getAvg() * size + score - original) / size);
			// 엔티티로 변환해서 저장한다.
			bookRepository.save(Book.voToEntity(vo));
			
		} catch (Exception e) {
			throw new IllegalArgumentException("별점을 0 ~ 10 사이의 정수로 입력하세요.");
		}
		
	}
	
	@Transactional
	// 댓글 1건을 삭제하는 메소드
	public void deleteComment(BookCommentVO bookCommentVO) {
		System.out.println("서비스 클래스의 deleteComment() 실행");
		// 댓글을 삭제한다.
		System.out.println("============>" + bookCommentVO.getIdx());
		bookCommentRepository.deleteById(bookCommentVO.getIdx());
		
		// 댓글이 삭제된 책의 평점을 수정한다.
		BookVO vo = selectByISBN(bookCommentVO.getISBN());
		int size = bookCommentRepository.countByISBN(bookCommentVO.getISBN());
		float score = bookCommentVO.getScore();
		// 책의 평점 => (책의평점 * 삭제전 댓글수 - 삭제댓글점수) / (삭제후 댓글수)
		vo.setAvg((vo.getAvg() * (size + 1) - score) / size);
		//System.out.println("size: " + size + " score: " + score + "vo.setavg: " + vo.getAvg());
		
		// 엔티티로 변환해서 저장한다.
		bookRepository.save(Book.voToEntity(vo));
	}

	
	//===============================================================
	// 로그인 / 회원가입
	//===============================================================
	
	// 로그인하는 메소드
	public AccountVO login(AccountVO accountVO) {
		System.out.println("서비스 클래스의 login() 실행");
		// 로그인 정보를 토대로 DB 계정정보를 불러온다.
		Usertable usertable = accountRepository.findById(accountVO.getId());
		
		// 정보 일치여부를 확인한다.
		try {
			if (usertable.getId().equals(accountVO.getId()) && 
					usertable.getPw().equals(accountVO.getPw())
					) {
				//아이디 비밀번호 둘다 일치시 유저정보 리턴
				return AccountVO.entityToVo(usertable); 
			}
		} catch (Exception e) {
		}
		return null;
	}

	// 회원가입하는 메소드
	@Transactional 
	public void account(AccountVO accountVO) {
		System.out.println("서비스 클래스의 account() 실행");
		//DB에 받은 정보를 저장한다.
		accountRepository.save(Usertable.voToEntity(accountVO));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
