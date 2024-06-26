package com.galphi.galphi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.galphi.galphi.dao.MybatisDAO;
import com.galphi.galphi.vo.AccountVO;
import com.galphi.galphi.vo.BookCommentList;
import com.galphi.galphi.vo.BookCommentVO;
import com.galphi.galphi.vo.BookList;
import com.galphi.galphi.vo.BookVO;
import com.galphi.galphi.vo.Param;

@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private SqlSession sqlSession;
	
	@RequestMapping("/")
	public String home(Locale locale, Model model) {
		return "redirect:index";
	}

	@RequestMapping("/05ChatWindow")
	public String chatWindow(HttpServletRequest request, Model model) {
		String chatId = request.getParameter("chatId");
		System.out.println("chatid =>>>>" + chatId);
		model.addAttribute("chatId", chatId);
		return "chat";
	}
	
	// 인덱스에서 베스트 / 추천 / 신간
	@RequestMapping("/index")
	public String index(HttpServletRequest request, Model model) {
		// 맵퍼를 얻어온다
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		// xml 리턴타입을 받을 vo 객체 선언
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:/applicationCTX.xml");
		BookVO bookVO = ctx.getBean("bookVO", BookVO.class);
		
		// vo들을 받아서 저장할 booklist 선언
		BookList bookBestList = new BookList();
		BookList bookDailyList = new BookList();
		BookList bookNewList = new BookList();
		
		// xml로 넘겨줄 데이터를 가진 hashmap 객체
		//start 1 // end는 신간도서 전체 수로 고정
		int newCount = mapper.selectNewCount();
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		hmap.put("startNo", 1);
		hmap.put("endNo", newCount);
		bookNewList.setList(mapper.selectNewList(hmap));
		bookBestList.setList(mapper.selectBestList(hmap));
		bookDailyList.setList(mapper.selectDailyList(hmap));
		
		
		// 각 리스트의 ISBN을 기억할 arraylist 객체
		ArrayList<Integer> bestList = new ArrayList<Integer>();
		ArrayList<Integer> dailyList = new ArrayList<Integer>();
		ArrayList<Integer> newList = new ArrayList<Integer>();
		
		// 랜덤객체 선언
		Random random = new Random();
		// 신간의 총 개수 범위에서 랜덤한 인덱스의 ISBN을 기억할 변수
		int newone = bookNewList.getList().get(random.nextInt(newCount)).getISBN();
		
		// 베스트와 오늘의 추천은 10개씩 반복하며 arraylist에 담는다
		for (int i = 0; i < 10; i++) {
			int bestone = bookBestList.getList().get(i).getISBN();
			int dailyone = bookDailyList.getList().get(i).getISBN();
			
			bestList.add(bestone);
			dailyList.add(dailyone);
			/*
			for (int j : newList) {
				if (newList.contains(newone)) {
					newone = bookNewList.getList().get(random.nextInt(newCount)).getISBN();
			    }
			}
			*/
			while (newList.contains(newone)) { // ISBN이 리스트에 존재하면 새로운 랜덤 ISBN을 얻어온다
				newone = bookNewList.getList().get(random.nextInt(newCount)).getISBN();
			}
			newList.add(newone);
		}
			
		//System.out.println(newList);
		
		
		model.addAttribute("newList", newList);
		model.addAttribute("bestList", bestList);
		model.addAttribute("dailyList", dailyList);
		return "index";
	}
	
	@RequestMapping("/account")
	public String account(HttpServletRequest request, Model model) {
		return "account";
	}
	
	@RequestMapping("/login")
	public String login(HttpServletRequest request, Model model) {
		return "login";
	}
	
	@RequestMapping("/contentView")
	public String contentView(HttpServletRequest request, Model model) {
		return "contentView";
	}
	
	@RequestMapping("/list")
	public String list(HttpServletRequest request, Model model, BookVO bookVO) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:/applicationCTX.xml");
		BookList bookList = ctx.getBean("bookList", BookList.class);
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		int pageSize = 10;
		int currentPage = 1;
		String category = request.getParameter("category");
		String item = request.getParameter("item");
		String list = request.getParameter("list");
		String categorylist = request.getParameter("list");
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (NumberFormatException e) { }
		// 검색어 창에 검색어 표시
		if (item != null) { // 검색어가 있거나 공백으로 검색했을 때 검색창에 검색어 표시 / 설정한 검색유형 표시
			item = item.trim().length() == 0 ? "" : item;
			model.addAttribute("item", item);
			// 카테고리를 listSearchView 페이징 버튼에서 get 방식으로 요청시 '+' 기호가 ' '(공백)으로 넘어온다. 다시 '+'로 수정 
			if (category.equals("제목 저자")) {
				category = "제목+저자";
			}
			model.addAttribute("category", category);
		} else { // 검색어가 없을 때 기본값 검색어 => 공백 / 검색 유형 => 제목
			model.addAttribute("item", "");
			model.addAttribute("category", "제목");
		}
		// 검색 결과 표시(검색 결과 리스트)
		if (item == null || item.trim().length() == 0) { // 검색어 없거나 공백일 때 => 전체 책의 리스트를 얻어온다. + 페이징
			int totalCount = mapper.selectCount();
			bookList = new BookList(pageSize, totalCount, currentPage);
			hmap.put("startNo", bookList.getStartNo());
			hmap.put("endNo", bookList.getEndNo());
			bookList.setList(mapper.selectList(hmap));
			model.addAttribute("bookList", bookList);
		} else { // 검색어가 있을 때
			Param param = new Param();
			param.setItem(item);
			param.setCategory(category);
			int totalCount = mapper.selectCountMulti(param);
			bookList = new BookList(pageSize, totalCount, currentPage);
			param.setStartNo(bookList.getStartNo());
			param.setEndNo(bookList.getEndNo());
			bookList.setList(mapper.selectListMulti(param));
			for (int i = 0; i < bookList.getList().size(); i++) {
				String title = bookList.getList().get(i).getTitle();
				String author = bookList.getList().get(i).getAuthor();
				title = title.replace(item, "<span style='background-color: hotpink;'>" + item + "</span>");
				author = author.replace(item, "<span style='background-color: hotpink;'>" + item + "</span>");
				bookList.getList().get(i).setTitle(title);
				bookList.getList().get(i).setAuthor(author);
			}
			model.addAttribute("bookList", bookList); 
		}
		
		
		if (list.equals("Best") || list.equals("Daily")) {
			int totalCount = 1;
			BookList bookDailyList = new BookList(pageSize, totalCount, currentPage);
			BookList bookBestList = new BookList(pageSize, totalCount, currentPage);
			hmap.put("startNo", bookDailyList.getStartNo());
			hmap.put("endNo", bookDailyList.getEndNo());
			hmap.put("startNo", bookBestList.getStartNo());
			hmap.put("endNo", bookBestList.getEndNo());
			bookDailyList.setList(mapper.selectDailyList(hmap));
			bookBestList.setList(mapper.selectBestList(hmap));
			model.addAttribute("bookDailyList", bookDailyList);
			model.addAttribute("bookBestList", bookBestList);
		} else if (list.equals("New")) {
			int totalCount = mapper.selectNewCount();
			BookList bookNewList = new BookList(pageSize, totalCount, currentPage);
			hmap.put("startNo", bookNewList.getStartNo());
			hmap.put("endNo", bookNewList.getEndNo());
			bookNewList.setList(mapper.selectNewList(hmap));
			model.addAttribute("bookNewList", bookNewList);
		} else {
			try {
				categorylist = categorylist.toLowerCase();
			} catch (Exception e) { }
			int totalCount = mapper.selectCategoryCount(categorylist);
			BookList bookCategoryList = new BookList(pageSize, totalCount, currentPage);
			int startNo = bookCategoryList.getStartNo();
			int endNo = bookCategoryList.getEndNo();
			Param param = new Param(startNo, endNo, categorylist);
			bookCategoryList.setList(mapper.selectCategoryList(param));
			model.addAttribute("bookCategoryList", bookCategoryList);
		}
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("enter", "\r\n");
		
		return "list" + list + "View";
	}
	
	@RequestMapping("/selectByISBN")
	public String selectByISBN(HttpServletRequest request, Model model, BookCommentList bookCommentList) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		int ISBN = Integer.parseInt(request.getParameter("ISBN"));
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:/applicationCTX.xml");
		BookVO bookVO = ctx.getBean("bookVO", BookVO.class);
		bookCommentList = new BookCommentList();
		bookCommentList.setList(mapper.selectCommentList(ISBN));
		BookVO vo = mapper.selectByISBN(ISBN);
		model.addAttribute("vo", vo);
		model.addAttribute("bookCommentList", bookCommentList);
		
		// request.setAttribute("enter", "\r\n");
		return "contentView";
	}
	
	@RequestMapping("/insertcommentOK")
	public String insertcommentOK(HttpServletRequest request, Model model) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:/applicationCTX.xml");
		BookCommentVO bookCommentVO = ctx.getBean("bookCommentVO", BookCommentVO.class);
		String check_memo = request.getParameter("memo");
		String nick = request.getParameter("nick");
		int ISBN = Integer.parseInt(request.getParameter("ISBN"));
//		if (request.getParameter("score") == null) {
//			request.setAttribute("msg", "별점이 null");
//			request.setAttribute("url", "/galphi/selectByISBN?ISBN=" + ISBN);
//			return "alert";
//		}
		try {
			int checkScore = Integer.parseInt(request.getParameter("score"));
			if (check_memo == "") {
				request.setAttribute("msg", "후기를 입력해주세요.");
		        request.setAttribute("url", "/galphi/selectByISBN?ISBN=" + ISBN);
		        return "alert";
			} else if (checkScore > 10 || checkScore < 0) {
				request.setAttribute("msg", "별점을 0 ~ 10 사이로 입력해 주세요.");
		        request.setAttribute("url", "/galphi/selectByISBN?ISBN=" + ISBN);
		        return "alert";
			} else {
				bookCommentVO.setISBN(ISBN);
				bookCommentVO.setMemo(check_memo);
				bookCommentVO.setNick(nick);
				bookCommentVO.setScore(checkScore);
				mapper.insertComment(bookCommentVO);
				float avg = Float.parseFloat(request.getParameter("avg"));
				int size = Integer.parseInt(request.getParameter("size"));
				float score = bookCommentVO.getScore();
				avg = (avg * size + score) / (size + 1);
				Param param = new Param(bookCommentVO.getISBN(), avg);
				mapper.update(param);
			} 
		} catch (Exception e) {
			request.setAttribute("msg", "별점을 1 ~ 10사이의 정수로 입력하세유.");
	        request.setAttribute("url", "/galphi/selectByISBN?ISBN=" + ISBN);
	        return "alert";
		}
		return selectByISBN(request, model, null);
	}
	
	@RequestMapping("/deletecommentOK")
	public String deletecommentOK(HttpServletRequest request, Model model) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		int idx = Integer.parseInt(request.getParameter("idx"));
		mapper.deleteComment(idx);
		int ISBN = Integer.parseInt(request.getParameter("ISBN"));
		float coscore = Float.parseFloat(request.getParameter("coscore"));
		int size = Integer.parseInt(request.getParameter("size"));
		float avg = Float.parseFloat(request.getParameter("avg"));
		avg = (avg * size - coscore) / (size - 1);
		Param param = new Param(ISBN, avg);
		mapper.update(param);
		return selectByISBN(request, model, null);
	}
	
	@RequestMapping("/updatecommentOK")
	public String updatecommentOK(HttpServletRequest request, Model model) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		int idx = Integer.parseInt(request.getParameter("idx"));
		BookCommentVO co = mapper.selectcommentByIdx(idx);
		System.out.println(request.getParameter("coscore"));
		float coscore2 = Float.parseFloat(request.getParameter("coscore"));
		System.out.println(coscore2);
		int coscore = (int) coscore2;
		System.out.println(coscore);
		int size = Integer.parseInt(request.getParameter("size"));
		float avg = Float.parseFloat(request.getParameter("avg"));
		model.addAttribute("coscore", coscore);
		model.addAttribute("size", size);
		model.addAttribute("avg", avg);
		model.addAttribute("co", co);
		return "updatecomment";
	}
	
	@RequestMapping("/updatecommentOK2")
	public String updatecommentOK2(HttpServletRequest request, Model model) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		int ISBN = Integer.parseInt(request.getParameter("ISBN"));
		int idx = Integer.parseInt(request.getParameter("idx"));
		String memo = request.getParameter("memo");
		int size = Integer.parseInt(request.getParameter("size"));
		float avg = Float.parseFloat(request.getParameter("avg"));
		float coscore = Float.parseFloat(request.getParameter("coscore"));
		try {
			int checkScore = Integer.parseInt(request.getParameter("score"));
			if (memo == "") {
				request.setAttribute("msg", "후기를 입력해주세요.");
				request.setAttribute("url", "/galphi/updatecommentOK?idx=" + idx +"&coscore=" + coscore + "&avg=" + avg + "&size=" + size);
		        return "alert";
			} else if (checkScore > 10 || checkScore < 0) {
				request.setAttribute("msg", "별점을 0 ~ 10 사이로 입력해 주세요.");
				request.setAttribute("url", "/galphi/updatecommentOK?idx=" + idx +"&coscore=" + coscore + "&avg=" + avg + "&size=" + size);
		        return "alert";
			} else {
				float score = Float.parseFloat(request.getParameter("score"));
				Param param = new Param(idx, memo, score);
				mapper.updateComment(param);
				avg = (avg * size - coscore + score) / size;
				Param param2 = new Param(ISBN, avg);
				mapper.update(param2);
				return selectByISBN(request, model, null);
			} 
		} catch (Exception e) {
			request.setAttribute("msg", "별점을 1 ~ 10사이의 정수로 입력하세유.");
			request.setAttribute("url", "/galphi/updatecommentOK?idx=" + idx +"&coscore=" + coscore + "&avg=" + avg + "&size=" + size);
	        return "alert";
		}
	}
	
	@RequestMapping("/account_create")
	public String account_create(HttpServletRequest request, Model model) {
		return "account_create";
	}
	
	@RequestMapping("/loginOk")
	public String loginOk(HttpServletRequest request, Model model, AccountVO accountVO) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:/applicationCTX.xml");
		AccountVO ao = ctx.getBean("accountVO", AccountVO.class);
		String id = request.getParameter("id");
	    String password = request.getParameter("password");
	    HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("id", id);
        hmap.put("password", password);
	    int account = mapper.Login(hmap);
	    int IdExNo = mapper.IdCheck(id);
	    String nickname = mapper.nickname(hmap);
	    if (IdExNo == 0) {
	    	request.setAttribute("msg", "아이디 정보가 존재하지 않습니다.");
	        request.setAttribute("url", "/galphi/login");
	        return "alert";
	    } else if(account == 0) {
	    	request.setAttribute("msg", "비밀번호를 확인하세요");
	        request.setAttribute("url", "/galphi/login");
	        return "alert";
	    } else {
	    	HttpSession session = request.getSession();
	    	session.setAttribute("nickname", nickname);
	    	request.setAttribute("msg", "로그인 성공!");
	        request.setAttribute("url", "/galphi/index");
	        return "alert";
	    }
	}
	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		session.setAttribute("nickname", null);
		return "index";
	}
	
	@RequestMapping("/account_insert")
	public String account_insert(HttpServletRequest request, Model model, AccountVO accountVO) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String nickname = request.getParameter("nickname");
		String password = request.getParameter("password");
		String repassword = request.getParameter("repassword");
		int id_cnt = mapper.IdCheck(id);
		int nick_cnt = mapper.nickCheck(nickname);
		if ((id == "") || (name == "") || (nickname == "") || (password == "") || (repassword == "")) {
			request.setAttribute("msg", "누락된 정보가 있습니다");
	        request.setAttribute("url", "/galphi/account_create");
	        return "alert";	
		} else if (!password.equals(repassword)) {
			request.setAttribute("msg", "비밀번호가 일치하지 않습니다.");
	        request.setAttribute("url", "/galphi/account_create");
	        return "alert";	
		} else if (id_cnt > 0 || nick_cnt > 0) {
			request.setAttribute("msg", "아이디나 닉네임 중복체크 하세요.");
	        request.setAttribute("url", "/galphi/account_create");
	        return "alert";	
		} else {
			mapper.insert(accountVO);
			request.setAttribute("msg", "회원 가입이 완료되었습니다.");
	        request.setAttribute("url", "/galphi/login");
	        return "alert";	
		}
	}
	
	@RequestMapping("/id_Check")
	public String id_Check(HttpServletRequest request, Model model) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		String id = request.getParameter("id");
		System.out.println(id);
		int id_cnt = mapper.IdCheck(id);
		if (id_cnt > 0) {
			request.setAttribute("msg", "이미 사용중인 아이디입니다.");
	        request.setAttribute("url", "/galphi/account_create");
	        return "alert";	
		} else if (id == "")  {
			request.setAttribute("msg", "아이디를 입력하고 중복확인 하세요.");
	        request.setAttribute("url", "/galphi/account_create");
	        return "alert";	
		} else {
			request.setAttribute("msg", "사용 가능한 아이디입니다.");
	        request.setAttribute("url", "/galphi/account_create");
	        return "alert";	
		}
	}
	
	@RequestMapping("/nick_Check")
	public String nick_Check(HttpServletRequest request, Model model) {
		MybatisDAO mapper = sqlSession.getMapper(MybatisDAO.class);
		String nickname = request.getParameter("nickname");
		int nick_cnt = mapper.nickCheck(nickname);
		if (nick_cnt > 0) {
			request.setAttribute("msg", "이미 사용중인 닉네임입니다.");
	        request.setAttribute("url", "/galphi/account_create");
	        return "alert";	
		} else if (nickname == "")  {
			request.setAttribute("msg", "닉네임을 입력하고 중복확인 하세요.");
	        request.setAttribute("url", "/galphi/account_create");
	        return "alert";	
		} else {
			request.setAttribute("msg", "사용 가능한 닉네임입니다.");
	        request.setAttribute("url", "/galphi/account_create");
	        return "alert";	
		}
	}
	
	
}




















