package com.project.portfolio_02_Galphi_Remake.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.project.portfolio_02_Galphi_Remake.vo.AccountVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@SequenceGenerator(
	name = "gen",
	sequenceName = "USERTABLE_IDX_SEQ",
	schema = "TJOEUNIT",
	allocationSize = 1
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Usertable { // DB 테이블 이름
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen") 
	private int usernum; //기본키 - 유저 고유번호
	@Column
	private String username;
	@Column
	private String nick;
	@Column
	private String id;
	@Column
	private String pw;
	
	// VO 객체로 넘어온 데이터를 엔티티 객체로 수정
	public static Usertable voToEntity(AccountVO accountVO) {
		System.out.println("accountVO 객체 => Usertable 엔티티 객체");
		return new Usertable(
			accountVO.getUsernum(),
			accountVO.getUsername(), 
			accountVO.getNick(), 
			accountVO.getId(), 
			accountVO.getPw()
		);
	}




}
