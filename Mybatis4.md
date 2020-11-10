# Mybatis第4天

## 一、Mybatis的延迟加载

　　在**多表关联查询**时，比如查询用户信息，及其关联的帐号信息，在查询用户时就直接把帐号信息也一并查询出来了。但是在实际开发中，并不是每次都需要立即使用帐号信息，这时候，就可以使用延迟加载策略了。

### 1. 什么是延迟加载

#### 1.1 立即加载

　　不管数据是否需要使用，只要调用了方法，就立即发起查询。比如：上节课的多表关联查询，查询帐号，得到关联的用户。

#### 1.2 延迟加载

　　延迟加载，也叫按需加载，或者叫**懒加载**。只有当真正使用到数据的时候，才发起查询。不使用不发起查询。比如：查询用户信息，不使用accounts的时候，不查询帐号的数据；只有当使用了用户的accounts，Mybatis再发起查询帐号的信息

* 好处：先从单表查询，需要使用关联数据时，才进行关联数据的查询。 单表查询的速度要比多表关联查询速度快
* 坏处：当需要使用数据时才会执行SQL。这样大批量的SQL执行的情况下，会造成查询等待时间比较长

#### 1.3 延迟加载的使用场景

* 一对一（多对一），通常不使用延迟加载（建议）。比如：查询帐号，关联加载用户信息
* 一对多（多对多），通常使用延迟加载（建议）。比如：查询用户，关联加载帐号信息

### 2. 延迟加载的演示环境准备

​	我们以用户表（user）和帐户表（account）的关联查询，来分别演示一对一（多对一）懒加载实现、一对多（多对多）懒加载实现。

#### 1) 创建Maven的Java项目，设置坐标，配置好Mybatis的依赖（略）

#### 2) 创建JavaBean：User和Account

​	注意：Account中要有User的引用；User中要有Account的集合

```java
public class User {
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;

    private List<Account> accounts;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", birthday=" + birthday +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
```

```java
public class Account {
    private Integer id;
    private Integer uid;
    private Double money;

    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", uid=" + uid +
                ", money=" + money +
                ", user=" + user +
                '}';
    }
}
```

#### 3) 创建映射器接口UserDao和AccountDao

```java
public interface UserDao {
}
```

```java
public interface AccountDao{
    
}
```

#### 4) 创建映射配置文件UserDao.xml 和AccountDao.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.itheima.dao.UserDao">
</mapper>
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.itheima.dao.AccountDao">
</mapper>
```

#### 5) 创建Mybatis的核心配置文件

​	注意，要配置好别名和映射器

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <typeAliases>
        <package name="com.itheima.domain"/>
    </typeAliases>

    <environments default="mysql_mybatis">
        <environment id="mysql_mybatis">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql:///mybatis"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <package name="com.itheima.dao"/>
    </mappers>
</configuration>
```

#### 6) 准备好log4j日志配置文件

```properties
# Set root category priority to INFO and its only appender to CONSOLE.
#log4j.rootCategory=INFO, CONSOLE            debug   info   warn error fatal
log4j.rootCategory=debug, CONSOLE, LOGFILE

# Set the enterprise logger category to FATAL and its only appender to CONSOLE.
log4j.logger.org.apache.axis.enterprise=FATAL, CONSOLE

# CONSOLE is set to be a ConsoleAppender using a PatternLayout.
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m\n

# LOGFILE is set to be a File appender using a PatternLayout.
log4j.appender.LOGFILE=org.apache.log4j.FileAppender
log4j.appender.LOGFILE.File=d:\axis.log
log4j.appender.LOGFILE.Append=true
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m\n
```

#### 7) 编写单元测试类备用

```java
package com.itheima.test;

import com.itheima.dao.AccountDao;
import com.itheima.dao.UserDao;
import com.itheima.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class MybatisLazyTest {

    private InputStream inputStream;
    private SqlSession session;
    private UserDao userDao;
    private AccountDao accountDao;



    @Before
    public void init() throws IOException {
        inputStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        session = factory.openSession();
        userDao = session.getMapper(UserDao.class);
        accountDao = session.getMapper(AccountDao.class);
    }

    @After
    public void destory() throws IOException {
        session.close();
        inputStream.close();
    }
}

```

### 3. 实现一对一（多对一）的延迟加载

步骤如下：

```txt
1. 把多表关联查询的SQL，拆分成多条单表查询的SQL
  1.1  查询帐号的SQL：SELECT * FROM account
  1.2  查询帐号关联用户的SQL：SELECT * FROM USER WHERE id = ?

2. 提供查询帐号的功能：
  2.1 映射器AccountDao里: List<Account> queryAllAccount()
  2.2 映射配置文件里：
	使用resultMap封装
	<resultMap id="accountUserMap" type="account">
		<id property="id" column="id"/>
		<result property="uid" column="uid"/>
		<result property="money" column="money"/>
		
		<assocation property="user" javaType="user" 
			select="调用UserDao的findUserById的功能"
			column="调用功能时传参uid列的值"
		    />
	</resultMap>
	
3. 提供查询关联的用户的功能
  3.1 映射器UserDao里： User findUserById(Integer id)
  3.2 映射配置文件里：结果集封装成User对象
  
4. 注意：懒加载的全局开关要打开：核心配置文件里的setting
```



#### 3.1 需求描述

　　需求：查询帐号信息，及其关联的用户信息。使用懒加载的方式实现。

#### 3.2 实现步骤

​	把多表关联查询的一条SQL，拆成单表查询的两条SQL，即：用户和帐号关联查询的SQL，拆成一条查询所有帐号的SQL，一条查询 某帐号所属用户的SQL。

​	首先查询帐号信息，执行查询帐号的SQL；当需要帐号关联的用户时，再执行关联的查询用户的SQL；

​	实现步骤如下：

1. 在映射器UserDao中，提供findUserById的方法，供懒加载时使用

   ```sql
   SELECT * FROM USER WHERE uid = ?
   ```

2. 在映射器AccountDao中查询帐号信息，使用association实现懒加载关联的用户信息

   ```sql
   SELECT * FROM account
   ```

3. 在核心配置文件中，开启懒加载

   ```txt
   lazyLoadingEnabled：设置为true，表示开启懒加载的全局开头
   aggressiveLazyLoading：设置为false，表示禁用积极加载，使用按需加载
   ```

#### 3.3 实现需求

##### 3.3.1 提供UserDao的findUserById方法，供懒加载使用

###### 1) 在映射器UserDao中增加方法

```java
User findUserById(Integer id);
```

###### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="findUserById" parameterType="int" resultType="user">
    select * from user where id = #{id}
</select>
```

##### 3.3.2 查询帐号信息，懒加载关联的用户

###### 1) 在映射器AccountDao中增加方法

```java
List<Account> queryAllAccounts();
```

###### 2) 在映射配置文件AccountDao.xml中增加statement

```xml
<select id="queryAllAccounts" resultMap="accountLazyUser">
    select * from account
</select>
<resultMap id="accountLazyUser" type="account">
    <id property="id" column="id"/>
    <result property="uid" column="uid"/>
    <result property="money" column="money"/>

    <!--
        association标签：用于封装关联的JavaBean对象
            select：调用哪个statement，懒加载 得到关联的JavaBean对象
            column：调用statement时，需要传递的参数值，从哪个字段中取出
        -->
    <association property="user" javaType="user"
                 column="uid" select="com.itheima.dao.UserDao.findUserById"/>
</resultMap>
```

##### 3.3.3 在核心配置文件中开启懒加载

```xml
<!-- 把settings标签放到typeAliases之前 -->
<settings>
    <!-- 启动延迟加载 -->
    <setting name="lazyLoadingEnabled" value="true"/>
    <!-- 禁用积极加载：使用按需加载 -->
    <setting name="aggressiveLazyLoading" value="false"/>
</settings>
```

##### 3.3.4 在单元测试类中编写测试代码

```java
/**
 * 测试一对一实现懒加载：查询帐号，及其关联的一个用户。
 */
@Test
public void testQueryAllAccounts(){
    List<Account> accounts = accountDao.queryAllAccounts();
    for (Account account : accounts) {
        System.out.println(account.getId()+", " + account.getUid() + ", " +account.getMoney());
        //执行下面这行代码，才会发起查询user的SQL语句
        System.out.println(account.getUser());
    }
}
```

### 4. 实现一对多（多对多）的延迟加载

步骤如下：

```txt
1. 拆分SQL语句
  1.1 查询所有用户的SQL：select * from user
  1.2 查询用户关联的帐号的SQL：select * from account where uid = #{uid}
  
2. 提供查询用户的功能:
  2.1 在UserDao映射器里：List<User> queryAllUser();
  2.2 在映射配置文件里：使用resultMap封装
  	<resultMap id="userAccountsMap" type="user">
  		<!-- 封装用户本身的信息 -->
  		
  		<!-- 封装关联的帐号信息 -->
  		<collection property="accounts" ofType="account" 
  			select="调用AccountDao里的findAccountsByUid"
  			column="调用功能时传参id列的值"
  		    />
  	</resultMap>
  	
3. 提供查询用户关联的帐号的功能
  3.1 在AccountDao映射器里：List<Account> findAccountsByUid(Integer uid)
  3.2 在映射配置文件里，封装Account信息
  
4. 注意：懒加载的全局开关要打开：核心配置文件里的setting
```



#### 4.1 需求描述

　　需求：查询用户信息，及其关联的帐号信息集合。使用延迟加载实现。

#### 4.2 实现步骤

​	把多表关联查询的一条SQL，拆成单表查询的两条SQL，即：用户和帐号关联查询的SQL，拆成一条查询用户的SQL，一条查询某用户拥有的帐号的SQL；

​	首先查询用户信息，执行查询用户的SQL；当需要用户关联的帐号时，再执行关联的查询帐号的SQL；

​	实现步骤如下：

- 在映射器AccountDao，提供findAccountsByUid的方法，供懒加载时使用

  ```sql
  SELECT * FROM account WHERE uid = ?
  ```

- 要有映射器UserDao，查询用户信息，使用collection实现懒加载帐号关联的帐号信息

  ```sql
  SELECT * FROM USER
  ```

- 在核心配置文件中，开启懒加载

  ```text
  lazyLoadingEnabled：设置为true，表示开启懒加载的全局开头
  aggressiveLazyLoading：设置为false，表示禁用积极加载，使用按需加载
  ```

#### 4.3 实现需求

##### 4.3.1 提供AccountDao的findAccountsByUid方法，供懒加载使用

###### 1) 在映射器AccountDao中增加方法

```java
List<Account> findAccountsByUid(Integer uid);
```

######2) 在映射配置文件AccountDao.xml中增加statement

```xml
<select id="findAccountsByUid" parameterType="int" resultType="account">
    select * from account where uid = #{id}
</select>
```

##### 4.4.2 查询用户信息，懒加载关联的帐号集合

###### 1) 在映射器UserDao中增加方法

```java
List<User> queryAllUsers();
```

######2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="queryAllUsers" resultMap="userLazyAccounts">
    select * from user
</select>
<resultMap id="userLazyAccounts" type="user">
    <id property="id" column="id"/>
    <result property="username" column="username"/>
    <result property="birthday" column="birthday"/>
    <result property="sex" column="sex"/>
    <result property="address" column="address"/>

    <collection property="accounts" ofType="account" 
                column="id" select="com.itheima.dao.AccountDao.findAccountsByUid"/>
</resultMap>
```

##### 4.4.3 在核心配置文件中开启懒加载

```xml
<!-- 把settings标签放到typeAliases之前 -->
<settings>
    <!-- 启动延迟加载 -->
    <setting name="lazyLoadingEnabled" value="true"/>
    <!-- 禁用积极加载：使用按需加载 -->
    <setting name="aggressiveLazyLoading" value="false"/>
</settings>
```

##### 4.4.4 在单元测试类中编写测试代码

```java
/**
 * 测试一对多实现懒加载：查询用户，及其关联的帐号集合
 */
@Test
public void testQueryAllUsers(){
    List<User> users = userDao.queryAllUsers();
    for (User user : users) {
        System.out.println(user.getUsername()+", " + user.getSex());
        //执行页面这行代码，才会发起查询account的SQL语句
        System.out.println(user.getAccounts());
    }
}
```

## 二、Mybatis的缓存

　　Mybatis为了提高数据查询的效率，也提供了缓存的功能：一级缓存和二级缓存。

* 一级缓存：是SqlSession对象提供的缓存，不能关闭
* 二级缓存：是SqlSessionFactory对象的缓存，同一个SqlSessionFactory创建的多个SqlSession共享缓存



　　我们以查询一个用户信息为例，来演示Mybatis缓存的效果。

### 1. 准备Mybatis环境

1. 创建Maven的Java项目，配置好坐标，引入Mybatis的依赖

2. 创建JavaBean：User

   注意：**不要重写toString()方法**，我们需要打印User对象的地址

   ```java
   public class User {
       private Integer id;
       private String username;
       private Date birthday;
       private String sex;
       private String address;
   
       public Integer getId() {
           return id;
       }
   
       public void setId(Integer id) {
           this.id = id;
       }
   
       public String getUsername() {
           return username;
       }
   
       public void setUsername(String username) {
           this.username = username;
       }
   
       public Date getBirthday() {
           return birthday;
       }
   
       public void setBirthday(Date birthday) {
           this.birthday = birthday;
       }
   
       public String getSex() {
           return sex;
       }
   
       public void setSex(String sex) {
           this.sex = sex;
       }
   
       public String getAddress() {
           return address;
       }
   
       public void setAddress(String address) {
           this.address = address;
       }
   }
   ```

3. 创建映射器接口UserDao

   ```java
   public interface UserDao {
       User findUserById(Integer uid);
   }
   ```

4. 创建映射配置文件UserDao.xml

   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.itheima.dao.UserDao">
       <select id="findUserById" parameterType="int" resultType="user">
           select * from user where id = #{uid}
       </select>
   </mapper>
   ```

5. 准备Mybatis的核心配置文件，配置好别名和映射器

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE configuration
           PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-config.dtd">
   <configuration>
       <typeAliases>
           <package name="com.itheima.domain"/>
       </typeAliases>
   
       <environments default="mysql_mybatis">
           <environment id="mysql_mybatis">
               <transactionManager type="JDBC"/>
               <dataSource type="POOLED">
                   <property name="driver" value="com.mysql.jdbc.Driver"/>
                   <property name="url" value="jdbc:mysql:///mybatis"/>
                   <property name="username" value="root"/>
                   <property name="password" value="root"/>
               </dataSource>
           </environment>
       </environments>
   
       <mappers>
           <package name="com.itheima.dao"/>
       </mappers>
   </configuration>
   ```

6. 创建log4j日志配置文件

   ```properties
   # Set root category priority to INFO and its only appender to CONSOLE.
   #log4j.rootCategory=INFO, CONSOLE            debug   info   warn error fatal
   log4j.rootCategory=debug, CONSOLE, LOGFILE
   
   # Set the enterprise logger category to FATAL and its only appender to CONSOLE.
   log4j.logger.org.apache.axis.enterprise=FATAL, CONSOLE
   
   # CONSOLE is set to be a ConsoleAppender using a PatternLayout.
   log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
   log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
   log4j.appender.CONSOLE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m\n
   
   # LOGFILE is set to be a File appender using a PatternLayout.
   log4j.appender.LOGFILE=org.apache.log4j.FileAppender
   log4j.appender.LOGFILE.File=d:\axis.log
   log4j.appender.LOGFILE.Append=true
   log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
   log4j.appender.LOGFILE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m\n
   ```

7. 准备好单元测试类备用

   ```java
   /**
    * Mybatis一级缓存效果演示
    */
   public class MybatisLevel1CacheTest {
       private InputStream is;
       private SqlSession session;
       private UserDao dao;
   
       @Before
       public void init() throws IOException {
           is = Resources.getResourceAsStream("SqlMapConfig.xml");
           SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
           SqlSessionFactory factory = builder.build(is);
           session = factory.openSession();
           dao = session.getMapper(UserDao.class);
       }
   
       @After
       public void destory() throws IOException {
           session.close();
           is.close();
       }
   }
   ```

### 2. 一级缓存

#### 2.1 什么是一级缓存

##### 2.1.1 一级缓存介绍

　　一级缓存，是SqlSession对象提供的缓存（无论用不用，始终都有的）。

　　当我们执行一次查询之后，查询的结果（JavaBean对象）会同时缓存到SqlSession提供的一块存储区域中（是一个Map）。当我们再次查询同样的数据，Mybatis会优先从缓存中查找；如果找到了，就不再查询数据库。

##### 2.1.2 一级缓存的清除

　　当调用了SqlSession对象的修改、添加、删除、commit()、close()、clearCache()等方法时，一级缓存会被清空。

#### 2.2 一级缓存效果演示

```java
    /**
     * 测试 Mybatis的一级缓存：
     * SQL语句执行了一次、输出user1和user2的地址是相同的
     * 说明Mybatis使用了缓存
     */
    @Test
    public void testLevel1Cache(){
        User user1 = dao.findUserById(41);
        System.out.println(user1);

        User user2 = dao.findUserById(41);
        System.out.println(user2);
    }

    /**
     * 测试  清除Mybatis的一级缓存
     * 两次打印的User地址不同，执行了两次SQL语句
     * SqlSession的修改、添加、删除、commit()、clearCache()、close()都会清除一级缓存
     */
    @Test
    public void testClearLevel1Cache(){
        User user1 = dao.findUserById(41);
        System.out.println(user1);

        session.clearCache();

        User user2 = dao.findUserById(41);
        System.out.println(user2);
    }
```

### 3. 二级缓存

#### 3.1 什么是二级缓存

​	指Mybatis中SqlSessionFactory对象的缓存。由同一个SqlSessionFactory对象生产的SqlSession对象，同样的映射器Mapper共享其缓存。**二级缓存需要手动开启**

#### 3.2 开启二级缓存，及效果演示

##### 1) 修改Mybatis核心配置文件，开启全局的二级缓存开关

```xml
<!-- 把设置项添加到核心配置文件内，typeAliases之前 -->
<settings>
    <!-- 增加此配置项，启动二级缓存（默认值就是true，所以这一步可以省略不配置） -->
    <setting name="cacheEnabled" value="true"/>
</settings>
```

##### 2) 修改映射配置文件UserDao.xml，让映射器支持二级缓存

```xml
<mapper namespace="com.itheima.dao.UserDao">
    <!-- 把cache标签加到映射配置文件 mapper标签里 -->
    <cache/>
    ......
</mapper>
```

##### 3) 修改映射配置文件UserDao中的findById，让此方法（statement）支持二级缓存

```xml
<!-- 如果statement的标签上，设置有的useCache="true"，表示此方法要使用二级缓存 -->
<select id="findById" parameterType="int" resultType="user" useCache="true">
    select * from user where id = #{id}
</select>
```

##### 4) 修改JavaBean：User

注意：如果要使用二级缓存，那么**JavaBean需要实现Serializable接口**

```java
public class User implements Serializable {
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
```

##### 5) 编写测试代码

```java
/**
 * Mybatis二级缓存效果演示
 */
public class MybatisLevel2CacheTest {
    private InputStream is;
    private SqlSessionFactory factory;

    /**
     * 测试二级缓存。
     * 测试结果：
     *      虽然输出的user1和user2地址不同，但是SQL语句只执行了一次。
     *      Mybatis的二级缓存，保存的不是JavaBean对象，而是散列的数据。
     *      当要获取缓存时，把这些数据重新组装成一个JavaBean对象，所以地址不同
     */
    @Test
    public void testLevel2Cache(){
        SqlSession session1 = factory.openSession();
        UserDao dao1 = session1.getMapper(UserDao.class);
        User user1 = dao1.findUserById(41);
        System.out.println(user1);
        session1.close();

        SqlSession session2 = factory.openSession();
        UserDao dao2 = session2.getMapper(UserDao.class);
        User user2 = dao2.findUserById(41);
        System.out.println(user2);
        session2.close();
    }

    @Before
    public void init() throws IOException {
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        factory = builder.build(is);
    }

    @After
    public void destory() throws IOException {
        is.close();
    }
}
```

## 三、Mybatis的注解开发（<span style="color:red;">掌握</span>）

　　Mybatis也支持注解开发。但是需要明确的是，Mybatis仅仅是把映射配置文件 使用注解代替了；而Mybatis的核心配置文件，仍然是xml配置。

### 1. 常用注解介绍

* @Select：相当于映射配置文件里的select标签
* @Insert：相当于映射配置文件里的insert标签
* @SelectKey：相当于映射配置文件里的selectKey标签，用于添加数据后获取最新的主键值
* @Update：相当于映射配置文件里的update标签
* @Delete：相当于映射配置文件里的delete标签
* @Results：相当于映射配置文件里的resultMap标签
* @Result：相当于映射配置文件里的result标签，和@Results配合使用，封装结果集的
* @One：相当于映射配置文件里的association，用于封装关联的一个JavaBean对象
* @Many：相当于映射配置文件里的collection标签，用于封装关联的一个JavaBean对象集合

### 2. 使用注解实现简单CURD操作

#### 2.1 准备Mybatis环境

1. 创建Maven的Java项目，配置好坐标，并引入Mybatis的依赖

2. 创建JavaBean：User

   ```java
   public class User {
       private Integer id;
       private String username;
       private Date birthday;
       private String sex;
       private String address;
   
       public Integer getId() {
           return id;
       }
   
       public void setId(Integer id) {
           this.id = id;
       }
   
       public String getUsername() {
           return username;
       }
   
       public void setUsername(String username) {
           this.username = username;
       }
   
       public Date getBirthday() {
           return birthday;
       }
   
       public void setBirthday(Date birthday) {
           this.birthday = birthday;
       }
   
       public String getSex() {
           return sex;
       }
   
       public void setSex(String sex) {
           this.sex = sex;
       }
   
       public String getAddress() {
           return address;
       }
   
       public void setAddress(String address) {
           this.address = address;
       }
   
       @Override
       public String toString() {
           return "User{" +
                   "id=" + id +
                   ", username='" + username + '\'' +
                   ", birthday=" + birthday +
                   ", sex='" + sex + '\'' +
                   ", address='" + address + '\'' +
                   '}';
       }
   }
   ```

3. 创建映射器接口UserDao，备用

   ```java
   public interface UserDao {
   
   }
   ```

4. 准备Mybatis的核心配置文件，配置好别名和映射器

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE configuration
           PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-config.dtd">
   <configuration>
       <typeAliases>
           <package name="com.itheima.domain"/>
       </typeAliases>
   
       <environments default="mysql_mybatis">
           <environment id="mysql_mybatis">
               <transactionManager type="JDBC"/>
               <dataSource type="POOLED">
                   <property name="driver" value="com.mysql.jdbc.Driver"/>
                   <property name="url" value="jdbc:mysql:///mybatis"/>
                   <property name="username" value="root"/>
                   <property name="password" value="root"/>
               </dataSource>
           </environment>
       </environments>
   
       <mappers>
           <package name="com.itheima.dao"/>
       </mappers>
   </configuration>
   ```

5. 准备好单元测试类备用

   ```java
   /**
    * Mybatis的注解开发功能测试--简单的CURD操作
    */
   public class MybatisAnnotationTest {
       private InputStream is;
       private SqlSession session;
       private UserDao dao;
   
   
       @Before
       public void init() throws IOException {
           is = Resources.getResourceAsStream("SqlMapConfig.xml");
           SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
           session = factory.openSession();
           dao = session.getMapper(UserDao.class);
       }
       @After
       public void destory() throws IOException {
           session.close();
           is.close();
       }
   }
   ```

#### 2.2 查询全部用户

1. 在映射器接口UserDao中增加方法

   ```java
   @Select("select * from user")
   List<User> queryAll();
   ```

2. 在测试类MybatisAnnotationTest中编写测试代码

   ```java
   @Test
   public void testQueryAll(){
       List<User> users = dao.queryAll();
       for (User user : users) {
           System.out.println(user);
       }
   }
   ```

#### 2.3 根据主键查询一个用户

1. 在映射器接口UserDao中增加方法

   ```java
   @Select("select * from user where id = #{id}")
   User findById(Integer id);
   ```

2. 在测试类MybatisAnnotationTest中编写测试代码

   ```java
   @Test
   public void testFindById(){
       User user = dao.findById(41);
       System.out.println(user);
   }
   ```

#### 2.4 添加用户

1. 在映射器接口UserDao中增加方法

   ```java
   @Insert("insert into user (id,username,birthday,sex,address) values (#{id},#{username},#{birthday},#{sex},#{address})")
   @SelectKey(
       statement = "select last_insert_id()", //查询最新主键值的SQL语句
       resultType = Integer.class,  //得到最新主键值的类型
       keyProperty = "id",  //得到最新主键值，保存到哪个属性里
       before = false  //是否在insert操作之前查询最新主键值
   )
   void save(User user);
   ```

2. 在测试类MybatisAnnotationTest中编写测试代码

   ```java
   @Test
   public void testSave(){
       User user = new User();
       user.setUsername("小红");
       user.setSex("女");
       user.setAddress("中粮商务公园");
       user.setBirthday(new Date());
   
       System.out.println("保存之前：" + user);
       dao.save(user);
       session.commit();
       System.out.println("保存之后：" + user);
   }
   ```

#### 2.5 修改用户

1. 在映射器接口UserDao中增加方法

   ```java
   @Update("update user set username=#{username},birthday=#{birthday},sex=#{sex},address=#{address} where id=#{id}")
   void edit(User user);
   ```

2. 在测试类MybatisAnnotationTest中编写测试代码

   ```java
   @Test
   public void testEdit(){
       User user = dao.findById(57);
       user.setAddress("广州");
   
       dao.edit(user);
       session.commit();
   }
   ```

#### 2.6 删除用户

1. 在映射器接口UserDao中增加方法

   ```java
   @Delete("delete from user where id = #{id}")
   void delete(Integer id);
   ```

2. 在测试类MybatisAnnotationTest中编写测试代码

   ```java
   @Test
   public void testDelete(){
       dao.delete(57);
       session.commit();
   }
   ```

#### 2.7 JavaBean属性名和字段名不一致的情况处理

1. 创建JavaBean：  User2

   ```java
   public class User2 {
       private Integer userId;
       private String username;
       private Date userBirthday;
       private String userSex;
       private String userAddress;
   
       public Integer getUserId() {
           return userId;
       }
   
       public void setUserId(Integer userId) {
           this.userId = userId;
       }
   
       public String getUsername() {
           return username;
       }
   
       public void setUsername(String username) {
           this.username = username;
       }
   
       public Date getUserBirthday() {
           return userBirthday;
       }
   
       public void setUserBirthday(Date userBirthday) {
           this.userBirthday = userBirthday;
       }
   
       public String getUserSex() {
           return userSex;
       }
   
       public void setUserSex(String userSex) {
           this.userSex = userSex;
       }
   
       public String getUserAddress() {
           return userAddress;
       }
   
       public void setUserAddress(String userAddress) {
           this.userAddress = userAddress;
       }
   
       @Override
       public String toString() {
           return "User2{" +
                   "userId=" + userId +
                   ", username='" + username + '\'' +
                   ", userBirthday=" + userBirthday +
                   ", userSex='" + userSex + '\'' +
                   ", userAddress='" + userAddress + '\'' +
                   '}';
       }
   }
   ```

2. 在映射器接口UserDao中增加方法

   ```java
   @Select("select * from user")
   @Results({
       @Result(property = "userId", column = "id", id = true),
       @Result(property = "username", column = "username"),
       @Result(property = "userBirthday", column = "birthday"),
       @Result(property = "userSex", column = "sex"),
       @Result(property = "userAddress", column = "address")
   })
   List<User2> queryAllUser2();
   ```

3. 在测试类MybatisAnnotationTest中编写测试代码

   ```java
   @Test
   public void testQueryAllUser2(){
       List<User2> user2List = dao.queryAllUser2();
       for (User2 user2 : user2List) {
           System.out.println(user2);
       }
   }
   ```

### 3. 使用注解实现复杂关系操作-多表关联查询

#### 3.1 准备Mybatis环境

1. 创建Maven的Java项目，设置好坐标，引入Mybatis的依赖

2. 创建JavaBean

   注意：Account中要有User的引用，User中要有Account的集合

   ```java
   public class User {
       private Integer id;
       private String username;
       private Date birthday;
       private String sex;
       private String address;
   
       private List<Account> accounts;
   
       public Integer getId() {
           return id;
       }
   
       public void setId(Integer id) {
           this.id = id;
       }
   
       public String getUsername() {
           return username;
       }
   
       public void setUsername(String username) {
           this.username = username;
       }
   
       public Date getBirthday() {
           return birthday;
       }
   
       public void setBirthday(Date birthday) {
           this.birthday = birthday;
       }
   
       public String getSex() {
           return sex;
       }
   
       public void setSex(String sex) {
           this.sex = sex;
       }
   
       public String getAddress() {
           return address;
       }
   
       public void setAddress(String address) {
           this.address = address;
       }
   
       public List<Account> getAccounts() {
           return accounts;
       }
   
       public void setAccounts(List<Account> accounts) {
           this.accounts = accounts;
       }
   
       @Override
       public String toString() {
           return "User{" +
                   "id=" + id +
                   ", username='" + username + '\'' +
                   ", birthday=" + birthday +
                   ", sex='" + sex + '\'' +
                   ", address='" + address + '\'' +
                   ", accounts=" + accounts +
                   '}';
       }
   }
   ```

   ```java
   public class Account {
       private Integer id;
       private Integer uid;
       private Double money;
   
       private User user;
   
       public Integer getId() {
           return id;
       }
   
       public void setId(Integer id) {
           this.id = id;
       }
   
       public Integer getUid() {
           return uid;
       }
   
       public void setUid(Integer uid) {
           this.uid = uid;
       }
   
       public Double getMoney() {
           return money;
       }
   
       public void setMoney(Double money) {
           this.money = money;
       }
   
       public User getUser() {
           return user;
       }
   
       public void setUser(User user) {
           this.user = user;
       }
   
       @Override
       public String toString() {
           return "Account{" +
                   "id=" + id +
                   ", uid=" + uid +
                   ", money=" + money +
                   ", user=" + user +
                   '}';
       }
   }
   ```

3. 创建映射器接口UserDao和AccountDao，备用

   ```java
   public interface AccountDao {
   }
   ```

   ```java
   public interface UserDao {
   }
   ```

4. 准备好核心配置文件，配置好别名和映射器

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE configuration
           PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-config.dtd">
   <configuration>
       <typeAliases>
           <package name="com.itheima.domain"/>
       </typeAliases>
   
       <environments default="mysql_mybatis">
           <environment id="mysql_mybatis">
               <transactionManager type="JDBC"/>
               <dataSource type="POOLED">
                   <property name="driver" value="com.mysql.jdbc.Driver"/>
                   <property name="url" value="jdbc:mysql:///mybatis"/>
                   <property name="username" value="root"/>
                   <property name="password" value="root"/>
               </dataSource>
           </environment>
       </environments>
   
       <mappers>
           <package name="com.itheima.dao"/>
       </mappers>
   </configuration>
   ```

5. 准备好单元测试类备用

   ```java
   /**
    *  Mybatis的注解开发--复杂的注解开发，多表关联查询测试类
    */
   public class MybatisComplexAnnotationTest {
       private InputStream is;
       private SqlSession session;
       private UserDao userDao;
       private AccountDao accountDao;
   
       @Before
       public void init() throws IOException {
           is = Resources.getResourceAsStream("SqlMapConfig.xml");
           SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
           session = factory.openSession();
           userDao = session.getMapper(UserDao.class);
           accountDao = session.getMapper(AccountDao.class);
       }
       @After
       public void destory() throws IOException {
           session.close();
           is.close();
       }
   }
   ```

#### 3.2  一对一（多对一）关联查询，实现懒加载

需求：查询帐号信息，及其关联的用户信息

1. 修改映射器接口UserDao，增加方法findById（供关联查询时使用）

   ```java
   @Select("select * from user where id = #{id}")
   User findById(Integer id);
   ```

2. 创建JavaBean：Account

   注意：Account中要有User的引用，前边已经准备好

3. 修改映射器接口AccountDao，增加方法

   ```java
   @Select("select * from account where id = #{id}")
   @Results({
       @Result(property = "id", column = "id", id = true),
       @Result(property = "uid", column = "uid"),
       @Result(property = "money", column = "money"),
       @Result(
           property = "user",
           javaType = User.class,
           column = "uid",
           one = @One(
               //一对一关联查询，调用select配置的statement，得到关联的User对象
               select = "com.itheima.dao.UserDao.findById",
               //FetchType.LAZY 表示要使用延迟加载
               fetchType = FetchType.LAZY
           )
       )
   })
   Account findById(Integer id);
   ```

4. ```java
   @Select("select * from account where id = #{id}")
   @Results({
       @Result(property = "id", column = "id", id = true),
       @Result(property = "uid", column = "uid"),
       @Result(property = "money", column = "money"),
       @Result(
           property = "user",
           javaType = User.class,
           column = "uid",
           one = @One(
               select = "com.itheima.dao.UserDao.findById",
               fetchType = FetchType.LAZY
           )
       )
   })
   Account findById(Integer id);
   ```

5. 编写测试代码

   ```java
   @Test
   public void testOne2One(){
       Account account = accountDao.findById(1);
       System.out.println(account.getId() + ", "+ account.getMoney());
       
       //如果不执行下面这行代码，Mybatis不会发起查询用户的SQL
       System.out.println(account.getUser());
   }
   ```

#### 3.3 一对多（多对多）关联查询，实现懒加载

需求：查询用户信息，及其关联的帐号集合信息

1. 修改映射器AccountDao，增加方法findAccountsByUid（供关联查询时使用）

   ```java
   @Select("select * from account where uid = #{uid}")
   List<Account> findAccountsByUid(Integer uid);
   ```

2. 修改JavaBean：User

   注意：User中需要有Account的集合，前边已经准备好

3. 修改映射器接口UserDao，增加方法

   ```java
   /**
        * 查询用户信息，及其关联的帐号信息集合
        * @param id
        * @return
        */
   @Select("select * from user where id = #{id}")
   @Results({
       @Result(property = "id",column = "id",id = true),
       @Result(property = "username",column = "username"),
       @Result(property = "birthday",column = "birthday"),
       @Result(property = "sex",column = "sex"),
       @Result(property = "address",column = "address"),
       @Result(
           property = "accounts",
           javaType = List.class, //注意，这里是List.class，而不是Account.class
           column = "id",
           many = @Many(
               //一对多关联查询，调用select对应的statement，得到帐号集合
               select = "com.itheima.dao.AccountDao.findAccountsByUid",
               //FetchType.LAZY 表示要使用延迟加载
               fetchType = FetchType.LAZY
           )
       )
   })
   User findUserAccountsById(Integer id);
   ```

4. 编写测试代码

   ```java
   @Test
   public void testOne2Many(){
       User user = userDao.findUserAccountsById(41);
       System.out.println(user.getUsername()+", " + user.getAddress());
       
       //如果不执行下面这行代码，Mybatis不会发起查询帐号的SQL语句
       System.out.println(user.getAccounts());
   }
   ```
