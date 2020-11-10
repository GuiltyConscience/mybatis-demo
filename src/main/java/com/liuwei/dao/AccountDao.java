package com.liuwei.dao;

import com.liuwei.domain.Account;

import java.util.List;

public interface AccountDao {
    List<Account> findAccountAll();
}
