package com.project.portfolio_02_Galphi_Remake.vo;

import com.project.portfolio_02_Galphi_Remake.entity.Usertable;

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
public class AccountVO {
	// 필드 선언
	private int usernum;
    private String username; // 회원 실명
    private String nick; // 회원 닉네임
    private String id; // 회원 아이디
    private String pw; // 회원 비밀번호
    
    // 엔티티 객체를 vo 객체로 수정
    public static AccountVO entityToVo(Usertable usertable) {
    	
    	return new AccountVO(
			usertable.getUsernum(), 
			usertable.getUsername(), 
			usertable.getNick(), 
			usertable.getId(), 
			usertable.getPw() 
		);
    }
}
