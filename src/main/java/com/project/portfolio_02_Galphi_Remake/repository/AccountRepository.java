package com.project.portfolio_02_Galphi_Remake.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.portfolio_02_Galphi_Remake.entity.Usertable;
import com.project.portfolio_02_Galphi_Remake.vo.AccountVO;

public interface AccountRepository  extends JpaRepository<Usertable, Integer>{

	Usertable findById(String id);



}
