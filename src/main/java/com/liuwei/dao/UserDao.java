package com.liuwei.dao;

import com.liuwei.domain.*;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    public void  add(User user);

    public void update(User user);

    public void delete(Integer id);

    public User findById(Integer Id);

    public List<User> like(String Key);

    public Integer Count();

    public List<User> findListUserName(ListUser user);

    List<User> findByVO(QueryVO vo);

    List<User2> findUser2All();

    public List<User> search(User user);


    public List<User> findArrayList(QueryVO ids);

    public List<Classes> findClassesWithTeacher(Integer integer);


    List<User> queryAllUsers();

}
