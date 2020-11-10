import com.liuwei.dao.AccountDao;
import com.liuwei.dao.UserDao;
import com.liuwei.domain.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestMybatis {

    private UserDao mapper;
    private SqlSession session = null;
    private SqlSessionFactory factory;
    private InputStream resourceAsStream;
    private AccountDao accountDao;
    @Before
    public void Before(){
        try {
            resourceAsStream = Resources.getResourceAsStream("MyBatisConfig.xml");
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            factory = builder.build(resourceAsStream);
            session = factory.openSession();
            mapper = session.getMapper(UserDao.class);
            accountDao = session.getMapper(AccountDao.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void After(){
        try {
            //session.commit();
            resourceAsStream.close();
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void TestMyBatis(){
            List<User> all = mapper.findAll();
            for (User user : all) {
                System.out.println(user);
            }
    }


    @Test
    public void add(){
        User user = new User();
        user.setId(1000);
        user.setAddress("江西");
        user.setSex("男");
        user.setBirthday(new Date());
        user.setUsername("liuwei");
        mapper.add(user);
        session.commit();

    }


    @Test
    public void update(){
        User user = new User();
        user.setId(1000);
        user.setAddress("江西");
        user.setSex("男");
        user.setBirthday(new Date());
        user.setUsername("刘威");
        mapper.update(user);
        session.commit();
    }


    @Test
    public void Delete(){
        mapper.delete(1000);
        session.commit();
    }


    @Test
    public void findById(){
        User user = mapper.findById(1000);
        System.out.println(user);
    }

    @Test
    public void Like(){
        String name ="%王";
        List<User> users = mapper.like(name);
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void count(){
        Integer integer = mapper.Count();
        System.out.println(integer);
    }


    @Test
    public void findListUserName(){
        ListUser listUser =  new ListUser();
        User user = new User();
        user.setUsername("%王");
        listUser.setUser(user);

        List<User> listUserName = mapper.findListUserName(listUser);
        for (User u : listUserName) {
            System.out.println(u);
        }
    }

    @Test
    public void testFindByVO(){
        QueryVO vo = new QueryVO();
        User user = new User();
        user.setUsername("%王%");
        vo.setUser(user);

        List<User> users = mapper.findByVO(vo);
        for (User user1 : users) {
            System.out.println(user1);
        }
    }

    @Test
    public void User2(){
        List<User2> all = mapper.findUser2All();
        for (User2 user2 : all) {
            System.out.println(user2);
        }
    }


    @Test
    public void TestSearch(){

        User user = new User();
        user.setUsername("%王");
        //user.setSex("男");

        List<User> search = mapper.search(user);
        for (User u : search) {
            System.out.println(u);
        }
    }

    @Test
    public void testFindUserByIdsQueryVO(){
        QueryVO vo = new QueryVO();
        vo.setIds(new Integer[]{41, 42});

        List<User> userList = mapper.findArrayList(vo);
        for (User user : userList) {
            System.out.println(user);
        }
    }

    @Test
    public void findAccountAll(){
        List<Account> all = accountDao.findAccountAll();
        for (Account account : all) {
            System.out.println(account);
        }
    }

    @Test
    public void TestQueryAllUsers(){
        List<User> users = mapper.queryAllUsers();
        for (User user : users) {
            System.out.println(user);
        }
    }
}
