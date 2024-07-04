package com.project.portfolio_02_Galphi_Remake.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.project.portfolio_02_Galphi_Remake.service.BookService;
import com.project.portfolio_02_Galphi_Remake.vo.AccountVO;
import com.project.portfolio_02_Galphi_Remake.vo.BookCommentVO;
import com.project.portfolio_02_Galphi_Remake.vo.BookList;
import com.project.portfolio_02_Galphi_Remake.vo.BookVO;

@org.springframework.stereotype.Controller
//@Slf4j 
public class Controller {
	
	@Autowired
	private BookService bookService; // 서비스에서 데이터 전처리
	
	// 최초페이지: index 요청을 넣는다.
	@GetMapping("/")
	public String home(Model model) {
		System.out.println("Controller 클래스의 home() 메소드 실행");
		//log.info("Controller 클래스의 home() 메소드 실행");
		return "redirect:index";
	}
	
	// index 페이지
	@GetMapping("/index")
	public String index(HttpServletRequest request, Model model) {
		System.out.println("Controller 클래스의 index() 메소드 실행");
		//log.info("Controller 클래스의 index() 메소드 실행");
		// 추천 박스 책의 댓글 리스트를 얻어온다.
		ArrayList<BookCommentVO> comments1 = bookService.selectCommentByISBN(72);
		ArrayList<BookCommentVO> comments2 = bookService.selectCommentByISBN(73);
		model.addAttribute("comments1", comments1);
		model.addAttribute("comments2", comments2);
		// 베스트, 신간, 추천, 테마 리스트를 얻어온다.
		//===================베스트====================
		ArrayList<BookVO> bestList = bookService.selectBestList(); // 리스트 객체에 저장
		
		// 베스트1~10까지 각자 다른 변수에 넣어서 넘겨준다.
		for (int i = 0; i < 10; i++) {
			model.addAttribute("best" + (i + 1), bestList.get(i));
		}
				
		//===================신간====================
		ArrayList<BookVO> newList = bookService.selectNewTen(); // 10개
		
		ArrayList<BookVO> newList1 = new ArrayList<BookVO>();
		ArrayList<BookVO> newList2 = new ArrayList<BookVO>();
		// 신간을 2슬라이드로 나눠서 보여줄 것임으로 5개씩 두 개의 객체에 담는다.
		for (int i = 0; i < 5; i++) {
			newList1.add(newList.get(i));
			newList2.add(newList.get(i+5));
		}
		model.addAttribute("newList1", newList1); 
		model.addAttribute("newList2", newList2); 
		//===================오늘 추천====================
		ArrayList<BookVO> dailyList = bookService.selectDailyList();// 10개
		ArrayList<BookVO> dailyList1 = new ArrayList<BookVO>();
		ArrayList<BookVO> dailyList2 = new ArrayList<BookVO>();
		// 오늘 추천을 2슬라이드로 나눠서 보여줄 것임으로 5개씩 두 개의 객체에 담는다.
		for (int i = 0; i < 5; i++) {
			dailyList1.add(dailyList.get(i));
			dailyList2.add(dailyList.get(i+5));
		}
		model.addAttribute("dailyList1", dailyList1); 
		model.addAttribute("dailyList2", dailyList2); 
		//===================테마 탭구성====================
		ArrayList<BookVO> themeList1 = new ArrayList<BookVO>();
		ArrayList<BookVO> themeList2 = new ArrayList<BookVO>();
		ArrayList<BookVO> themeList3 = new ArrayList<BookVO>();
		// 테마리스트별로 3권씩 담는다.
		themeList1.add(bookService.selectByISBN(5));
		themeList1.add(bookService.selectByISBN(6));
		themeList1.add(bookService.selectByISBN(13));
		themeList2.add(bookService.selectByISBN(41));
		themeList2.add(bookService.selectByISBN(45));
		themeList2.add(bookService.selectByISBN(46));
		themeList3.add(bookService.selectByISBN(82));
		themeList3.add(bookService.selectByISBN(87));
		themeList3.add(bookService.selectByISBN(96));
		model.addAttribute("themeList1", themeList1);
		model.addAttribute("themeList2", themeList2);
		model.addAttribute("themeList3", themeList3);
		return "index";
	}
	
	// 더보기 네비게이션 버튼 리스트 페이지
	@GetMapping("/list/{kind}")
	public String list(@PathVariable String kind, HttpServletRequest request, Model model) {
		System.out.println("Controller 클래스의 list() 메소드 실행");
		BookList bookList = new BookList();
		int currentPage = 1;
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (NumberFormatException e) { }
		model.addAttribute("currentPage", currentPage);
		// 베스트 리스트는 10권 고정이므로 분리실행
		if (kind.equals("best")) {
			bookList = new BookList();
			bookList.setList(bookService.selectBestList());
			bookList.setStartPage(1);
		}
		
		// 나머지는 10권 이상이므로 페이징을 할 수 있는 BookList로 받는다
		else {
			bookList = bookService.selectKindList(kind, currentPage);
		}
		
		//==================페이징======================
		// 페이징 버튼 처리를 해준다.================================
		// 변수를 받는다. currentPage / startPage / endPage / totalPage
		int startPage = bookList.getStartPage();
		int endPage = bookList.getEndPage();
		int totalPage = bookList.getTotalPage();
		System.out.println("startPage: " + startPage);
		System.out.println("endPage: " + endPage);
		System.out.println("totalPage: " + totalPage);
		//1. 처음으로 버튼 currentPage <= 1이면 비활성 // 아니면 활성
		model.addAttribute("p1", currentPage <= 1 ? null: "active");
		//2. 10페이지 이전 버튼 startPage <= 1 이면 비활성 // 아니면 활성
		model.addAttribute("p2", startPage <= 1 ? null: startPage - 1);
		//3. 페이징 버튼 수 startPage ~ endPage 개수 => currentPage와 같으면 비활성// 아니면 활성
		ArrayList<Integer> prevnum = new ArrayList<Integer>();
		ArrayList<Integer> nextnum = new ArrayList<Integer>();
		for (int i = startPage; i < endPage + 1; i++) {
			if (i < currentPage) {
				prevnum.add(i);
			}
			if (i > currentPage) {
				nextnum.add(i);
			}
		}
		model.addAttribute("prevnum", prevnum);
		model.addAttribute("nextnum", nextnum);
		//4. 10페이지 뒤로 버튼 endPage >= totalPage 이면 비활성 // 아니면 활성
		model.addAttribute("p4", endPage >= totalPage ? null: endPage + 1);
		//5. 마지막으로 버튼 currentPage >= totalPage 이면 비활성 // 아니면 활성
		model.addAttribute("p5", currentPage >= totalPage ? null: totalPage);
		//=============================================================
		
		// 페이징 버튼 설정용 변수
		model.addAttribute("page", bookList); // 페이징 저장
		model.addAttribute("kind", "../list/" + kind + "?"); // 페이징용 href 저장
		
		//뷰페이지에서 사용하기 위해 리스트를 꺼내서 저장한다.
		ArrayList<BookVO> bookListFinal = bookList.getList(); 
		model.addAttribute("bookList", bookListFinal); // 책저장
		return "list";
	}
	
	// 검색 페이지(제목 / 저자 / 출판사*)
	@GetMapping("/search")
	public String search(HttpServletRequest request, Model model) {
		System.out.println("Controller 클래스의 search() 메소드 실행");
		// 검색유형과 검색어를 받는다.(word, type)
		String type = request.getParameter("type");
		String word = request.getParameter("word");
		// 검색 상황별 검색어와 검색유형과 검색어를 설정한다.
		// 1. 검색어가 있을 때(공백 1칸 이상) => 검색어/유형를 검색창에 넣어준다.
		if (word != null) {
			word = word.trim().length() == 0 ? "" : word;
			model.addAttribute("word", word);
			if (type.equals("제목")) {
				model.addAttribute("type", "제목");
			} else {
				model.addAttribute(type.equals("저자") ? "type2":"type3", type);
			}
		} 
		// 2. 검색어가 없을 때 => 검색어: 공백0칸 / 유형: 제목 으로 설정
		else { 
			model.addAttribute("word", "");
			model.addAttribute("type", "제목");
		}
		
		// 검색 결과 페이징
		int currentPage = 1;
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (NumberFormatException e) { }
		model.addAttribute("currentPage", currentPage);
		
		// 검색 리스트를 얻어온다.
		BookList searchList = bookService.searchList(word, type, currentPage);
		//System.out.println(searchList);
		
		// 페이징 버튼 처리를 해준다.================================
		// 변수를 받는다. currentPage / startPage / endPage / totalPage
		int startPage = searchList.getStartPage();
		int endPage = searchList.getEndPage();
		int totalPage = searchList.getTotalPage();
		System.out.println("startPage: " + startPage);
		System.out.println("endPage: " + endPage);
		System.out.println("totalPage: " + totalPage);
		//1. 처음으로 버튼 currentPage <= 1이면 비활성({{^cpflag}}) // 아니면 활성
		model.addAttribute("p1", currentPage <= 1 ? null: "active");
		//2. 10페이지 이전 버튼 startPage <= 1 이면 비활성 // 아니면 활성
		model.addAttribute("p2", startPage <= 1 ? null: startPage - 1);
		//3. 페이징 버튼 수 startPage ~ endPage 개수 => currentPage와 같으면 비활성// 아니면 활성
		ArrayList<Integer> prevnum = new ArrayList<Integer>();
		ArrayList<Integer> nextnum = new ArrayList<Integer>();
		for (int i = startPage; i < endPage + 1; i++) {
			if (i < currentPage) {
				prevnum.add(i);
			}
			if (i > currentPage) {
				nextnum.add(i);
			}
		}
		model.addAttribute("prevnum", prevnum);
		model.addAttribute("nextnum", nextnum);
		//4. 10페이지 뒤로 버튼 endPage >= totalPage 이면 비활성 // 아니면 활성
		model.addAttribute("p4", endPage >= totalPage ? null: endPage + 1);
		//5. 마지막으로 버튼 currentPage >= totalPage 이면 비활성 // 아니면 활성
		model.addAttribute("p5", currentPage >= totalPage ? null: totalPage);
		//=============================================================
		
		model.addAttribute("page", searchList); // 페이징 저장
		model.addAttribute("kind", "../search?type=" + type + "&word=" + word + "&"); // 페이징용 href 저장
		
		//뷰페이지에서 사용하기 위해 리스트를 꺼내서 저장한다.
		ArrayList<BookVO> bookListFinal = searchList.getList(); 
		model.addAttribute("bookList", bookListFinal); // 책저장
		return "list"; 
	}
	
	// 책 한권의 상세 정보를 보여주는 페이지
	@GetMapping("/book/{ISBN}")
	public String selectByISBN(@PathVariable int ISBN, HttpServletRequest request, Model model) {
		System.out.println("Controller 클래스의 selectByISBN() 메소드 실행");
		
		// 책 1권을 얻어온다
		BookVO vo = bookService.selectByISBN(ISBN);
		
		// 평점으로 별점 계산
		float avg = vo.getAvg();
		float fillstars = avg / 2 - avg % 2; // 내림처리
		float halfstar = avg % 2; //나머지
		
		String stars = "";
		int count = 0;
		for (int i = 0; i < fillstars; i++) {
			stars += "<i class='bi bi-star-fill'></i>";
			count += 1;
		}
		if (halfstar >= 1) {
			stars += "<i class='bi bi-star-half'></i>";
			count += 1;
			for (int i = 0; i < 5 - count; i++) {
				stars += "<i class='bi bi-star'></i>";
			}
		} else {
			for (int i = 0; i < 5 - count; i++) {
				stars += "<i class='bi bi-star'></i>";
			}
		}
		model.addAttribute("stars", stars); // 뷰페이지의 평점 별모양 저장
		
		// 얻어온 책의 댓글들을 얻어와 리스트에 저장
		ArrayList<BookCommentVO> commentList = bookService.selectCommentByISBN(ISBN);
		
		// 책 한권의 평점 도장모양 저장
		String stamp = "<b style=\"color: deeppink; border: 1px solid;\">&nbsp;<i class=\"bi bi-emoji-laughing\"></i> 완벽해요&nbsp;</b>";
		if (avg >= 6 && avg < 8) {
			stamp = "<b style=\"color: blue; border: 1px solid;\">&nbsp;<i class=\"bi bi-emoji-smile\"></i> 추천해요&nbsp;</b>";
		} else if (avg >= 4 && avg < 6) {
			stamp ="<b style=\"color: green; border: 1px solid;\">&nbsp;<i class=\"bi bi-emoji-neutral\"></i> 읽어봐요&nbsp;</b>";
		} else if (avg >= 2 && avg < 4) {
			stamp = "<b style=\"color: orangered; border: 1px solid;\">&nbsp;<i class=\"bi bi-emoji-dizzy\"></i> 별로예요&nbsp;</b>";
		} else if (avg < 2) {
			stamp ="<b style=\"color: red; border: 1px solid;\">&nbsp;<i class=\"bi bi-emoji-angry\"></i> 비추예요&nbsp;</b>";
		}
		
		try {
			HttpSession session = request.getSession();
			String nickname = (String) session.getAttribute("nickname");
			// 닉네임과 코멘트 닉네임이 일치하면 수정 삭제 버튼이 보이는 flag를 1로 만든다.
			for (BookCommentVO comment : commentList) {
				if (nickname.equals(comment.getNick())) {
					comment.setFlag(true);
				}
			}
		} catch (Exception e) {
			System.out.println("비로그인 상태 혹은 닉네임 정보 없음");
		}
		// model 객체에 저장한다.
		model.addAttribute("vo", vo); 
		model.addAttribute("stamp", stamp); 
		model.addAttribute("commentList", commentList); 
		//System.out.println(vo);
		//System.out.println(commentList);
		
		
		return "book";
	}
	
	// 댓글을 저장하는 요청 => form에 넘어온 데이터를 vo객체로 받는다
	@PostMapping("/book/{ISBN}/new")
	public String insertComment(@PathVariable int ISBN, HttpServletRequest request, Model model, BookCommentVO bookCommentVO) {
		System.out.println("Controller 클래스의 insertComment() 메소드 실행");
		
		System.out.println(ISBN + "<===책번호========댓글점수===>" + bookCommentVO.getScore());
		// 데이터 유효성을 확인한다.
		String mess = "";
		// 댓글이 공백만 있는지 확인한다.(누락값 등은 뷰페이지에서 검사완료)
		if (bookCommentVO.getMemo().trim().equals("")) { // 공백만 있다면
			System.out.println("메모가 공백만 있습니다. <" + bookCommentVO.getMemo().trim() + "> 사이가 비었습니다.");
			mess = "메모에 공백만 존재합니다. 내용을 입력하세요.";
			model.addAttribute("mess", mess);
			return selectByISBN(ISBN, request, model);
		}
		
		// 데이터가 유효하면 저장할 정보를 서비스 클래스로 넘겨준다.
		System.out.println("==============" + bookCommentVO);
		bookService.insertComment(bookCommentVO);
		return "redirect:/book/" + ISBN;
	}
	
	// 댓글을 수정하는 요청 => form에 넘어온 데이터를 vo객체로 받는다
	@PostMapping("/comment/edit")
	public String updateComment(HttpServletRequest request, Model model, BookCommentVO bookCommentVO) {
		System.out.println("Controller 클래스의 updateComment() 메소드 실행");
		System.out.println(bookCommentVO.getISBN() + "<===책번호========댓글점수===>" + bookCommentVO.getScore());
		// 데이터 유효성을 확인한다.
		String mess = "";
		// 댓글이 공백만 있는지 확인한다.(누락값 등은 뷰페이지에서 검사완료)
		if (bookCommentVO.getMemo().trim().equals("")) { // 공백만 있다면
			System.out.println("메모가 공백만 있습니다. <" + bookCommentVO.getMemo().trim() + "> 사이가 비었습니다.");
			mess = "메모에 공백만 존재합니다. 내용을 입력하세요.";
			model.addAttribute("mess", mess);
			return selectByISBN(bookCommentVO.getISBN(), request, model);
		}
		
		// 수정할 정보를 서비스 클래스로 넘겨준다.
		bookService.updateComment(bookCommentVO);
		return "redirect:/book/" + bookCommentVO.getISBN();
	}
	
	// 댓글을 삭제하는 요청 => idx, ISBN을 vo객체로 받는다
	@PostMapping("/delete")
	public String deleteComment(HttpServletRequest request, Model model, BookCommentVO bookCommentVO) {
		System.out.println("Controller 클래스의 deleteComment() 메소드 실행");
		// 삭제할 댓글정보를 서비스 클래스로 넘겨준다.
		bookService.deleteComment(bookCommentVO);
		return "redirect:/book/" + bookCommentVO.getISBN();
	}
	
	// 로그인/회원가입 페이지로 이동 요청(한 페이지에서 같이 처리)
	@GetMapping("/loginpage")
	public String loginpage(HttpServletRequest request, Model model, AccountVO accountVO) {
		System.out.println("Controller 클래스의 loginpage() 메소드 실행");
		
		String returnpage = "index";
		//로그인 요청을 한 현재 뷰페이지 주소값을 얻어온다. 
		try {
			returnpage = request.getHeader("referer").split("9090/")[1];
		} catch (Exception e) {
		}
		//세션 값으로 저장한다.(로그인 실패해도 기억할 수 있게..)
		HttpSession session = request.getSession();
		session.setAttribute("returnpage", returnpage);
		// 로그인/회원가입 페이지로 이동한다.
		return "loginpage";
	}
	
	// ****로그인 시 한글아이디 방지 js구현 나중에***
	// 로그인 요청(로그인 정보는 POST 형식)
	@PostMapping("/login")
	public String login(HttpServletRequest request, Model model, AccountVO accountVO) {
		System.out.println("Controller 클래스의 login() 메소드 실행");
		//System.out.println("id 입력정보 체크 =>" + accountVO.getId());
		//System.out.println("pw 입력정보 체크 =>" + accountVO.getPw());
		
		// 누락정보 체크 => 누락정보가 있으면 로그인 페이지로 이동
		HttpSession session = request.getSession();
		String result = ""; // redirect 반환이므로 세션에 저장해준다.
		
		if (accountVO.getId() == null || accountVO.getId().trim().length() == 0) {
			result = "아이디를 입력하세요";
			model.addAttribute("result", result);
			return "loginpage";
		}
		else if (accountVO.getPw() == null || accountVO.getPw().trim().length() == 0) {
			result = "비밀번호를 입력하세요";
			model.addAttribute("result", result);
			return "loginpage";
		}
		
		// 누락 정보가 없으면 로그인 정보를 서비스 클래스로 넘겨서 확인한다.
		AccountVO account = bookService.login(accountVO);
		if (account == null) {
			result = "없는 아이디이거나 비밀번호가 일치하지 않습니다.";
			model.addAttribute("result", result);
			return "loginpage";
		} else { // 로그인 정보가 모두 있고 일치하므로 로그인 세션을 불러온다.
			//redirect 사용시 세션값으로 저장해주기
			session.setAttribute("nickname", account.getNick());
		}
		
		// 로그인 최초 요청한 페이지로 돌려보낸다
		String returnpage = null;
		try {
			returnpage = (String) session.getAttribute("returnpage");
			System.out.println("====================" + returnpage);
			//System.out.println(account);
			//System.out.println(result);
			if (returnpage.equals("logout") || returnpage.equals("loginpage")) {
				returnpage = "index";
			}
		} catch (Exception e) {
			returnpage = "index";
		}
		return "redirect:" + returnpage; // 리다이렉트 안하면 index 정보 누락 발생
	}
	
	//로그아웃 요청
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, Model model) {
		System.out.println("Controller 클래스의 logout() 메소드 실행");
		
		// 세션값 얻어와 모든 세션값 삭제
		HttpSession session = request.getSession();
		session.invalidate(); 
		//로그아웃 요청을 한 페이지 주소값을 얻어온다. 
		String page = request.getHeader("referer"); // => http://localhost:9090/book/73
		page = page.split("9090/")[1]; // => book/73
		//System.out.println("========================> " + page);
		return "redirect:" + page;
	}
	
	// 회원가입 요청(POST 방식으로)
	@PostMapping("/account")
	public String account(HttpServletRequest request, Model model, AccountVO accountVO) {
		System.out.println("Controller 클래스의 account() 메소드 실행");
		
		// 누락된 회원가입 정보 있는지 확인 => 뷰만져주기
		String check = "";
		
		if (accountVO.getUsername() == null || accountVO.getUsername().trim().length() == 0) {
			check = "이름을 입력하세요";
			model.addAttribute("check", check);
			return "loginpage";
		}
		else if (accountVO.getNick() == null || accountVO.getNick().trim().length() == 0) {
			check = "닉네임을 입력하세요";
			model.addAttribute("check", check);
			return "loginpage";
		}
		else if (accountVO.getId() == null || accountVO.getId().trim().length() == 0) {
			check = "아이디를 입력하세요";
			model.addAttribute("check", check);
			return "loginpage";
		}
		else if (accountVO.getPw() == null || accountVO.getPw().trim().length() == 0) {
			check = "비밀번호를 입력하세요";
			model.addAttribute("check", check);
			return "loginpage";
		}
		
		// 누락된 정보가 없으면 회원가입 메소드를 실행한다.
		bookService.account(accountVO);
		model.addAttribute("success", "회원가입 완료!");
		// 회원가입 완료 후에 로그인 화면으로 이동한다.
		return "loginpage";
	}
	
	// 중복체크 메소드>>js
	
	
	
}
