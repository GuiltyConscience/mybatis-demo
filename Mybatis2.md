# Mybatis第2天-笔记

## 一、Mybatis实现CURD--接口代理对象的方式（重点）

### 1. 需求说明

针对user表进行CURD操作：

* 查询全部用户，得到`List<User>`（上节课快速入门已写过，略）
* 保存用户（新增用户）
* 修改用户
* 删除用户
* 根据主键查询一个用户，得到`User`
* 模糊查询
* 查询数量

### 2. 准备Mybatis环境

#### 2.1 创建Maven项目，准备JavaBean

##### 1) 创建Maven的Java项目，坐标为：

```xml
    <groupId>com.itheima</groupId>
    <artifactId>day47_mybatis01_curd</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
```

##### 2) 在pom.xml中添加依赖：

```xml
		<dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.6</version>
        </dependency>
```

##### 3) 创建JavaBean

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

#### 2.2 准备Mybatis的映射器和配置文件

##### 1) 创建映射器接口UserDao（暂时不需要增加方法，备用）

```java
public interface UserDao {

}
```

##### 2) 创建映射配置文件UserDao.xml（暂时不需要配置statement，备用）

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.itheima.dao.UserDao">
    
</mapper>
```

##### 3) 创建Mybatis的核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
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
        <mapper resource="com/itheima/dao/UserDao.xml"/>
    </mappers>
</configuration>
```

##### 4) 准备log4j.properties日志配置文件

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

#### 2.3 准备单元测试类

在单元测试类中准备好@Before、@After的方法代码备用。编写完成一个功能，就增加一个@Test方法即可。

代码如下：

```java
public class MybatisCURDTest {
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
        //释放资源
        session.close();
        is.close();
    }
}
```

### 3. 编写代码实现需求

#### 3.1 保存/新增用户

##### 1) 在映射器UserDao中增加方法

```java
void save(User user);
```

##### 2) 在映射配置文件UserDao.xml中添加statement

```xml
    <!--parameterType：是方法的参数类型，写全限定类名-->
    <insert id="save" parameterType="com.itheima.domain.User">
        <!-- 
            selectKey标签：用于向数据库添加数据之后，获取最新的主键值
                resultType属性：得到的最新主键值的类型
                keyProperty属性：得到的最新主键值，保存到JavaBean的哪个属性上
                order属性：值BEFORE|AFTER，在insert语句执行之前还是之后，查询最新主键值。MySql是AFTER
         -->
        <selectKey resultType="int" keyProperty="id" order="AFTER">
            select last_insert_id()
        </selectKey>
        
        insert into user (id, username, birthday, address, sex) 
                  values (#{id}, #{username}, #{birthday},#{address},#{sex})
    </insert>
```

> 注意：
>
> ​	SQL语句中#{}代表占位符，相当于预编译对象的SQL中的?，具体的值由User类中的属性确定

##### 3) 在单元测试类中编写测试代码

```java
    @Test
    public void testSaveUser(){
        User user = new User();
        user.setUsername("tom");
        user.setAddress("广东深圳");
        user.setBirthday(new Date());
        user.setSex("男");

        System.out.println("保存之前：" + user);//保存之前，User的id为空
        dao.save(user);
        session.commit();//注意：必须要手动提交事务
        System.out.println("保存之后：" + user);//保存之后，User的id有值
    }
```

> 注意：<span style="color:red;">执行了DML语句之后，一定要提交事务：session.commit();</span>

#### 3.2 修改用户

##### 1) 在映射器UserDao中增加方法

```java
void edit(User user);
```

##### 2) 在映射配置文件UserDao.xml中添加statement

```xml
<update id="edit" parameterType="com.itheima.domain.User">
    update user set username = #{username}, birthday = #{birthday}, 
    address = #{address}, sex = #{sex} where id = #{id}
</update>
```

##### 3) 在单元测试类中编写测试代码

```java
@Test
public void testEditUser(){
    User user = new User();
    user.setId(50);
    user.setUsername("jerry");
    user.setAddress("广东深圳宝安");
    user.setSex("女");
    user.setBirthday(new Date());

    dao.edit(user);
    session.commit();
}
```

> 注意：<span style="color:red;">执行了DML语句之后，一定要提交事务：session.commit();</span>

#### 3.3. 删除用户

##### 1) 在映射器UserDao中增加方法

```java
void delete(Integer id);
```

##### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<delete id="delete" parameterType="int">
    delete from user where id = #{id}
</delete>
```

> 注意：
>
> ​	<span style="color:red;">如果只有一个参数，且参数是简单类型的，那么#{id}中的id可以随意写成其它内容，例如：#{abc}</span>
>
> 简单类型参数：
>
> ​	int, double, short, boolean, ...   或者：java.lang.Integer, java.lang.Double, ...
>
> ​	string 或者 java.lang.String

##### 3) 在单元测试类中编写测试代码

```java
@Test
public void testDeleteUser(){
    dao.delete(50);
    session.commit();
}
```

> 注意：<span style="color:red;">执行了DML语句之后，一定要提交事务：session.commit();</span>

#### 3.4 根据主键查询一个用户

##### 1) 在映射器UserDao中增加方法

```java
User findById(Integer id);
```

##### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="findById" parameterType="int" resultType="com.itheima.domain.User">
    select * from user where id = #{id}
</select>
```

##### 3) 在单元测试类中编写测试代码

```java
@Test
public void testFindUserById(){
    User user = dao.findById(48);
    System.out.println(user);
}
```

#### 3.5 模糊查询

##### 3.5.1 使用#{}方式进行模糊查询

###### 1) 在映射器UserDao中增加方法

```java
/**
 * 使用#{}方式进行模糊查询
 */
List<User> findByUsername1(String username);
```

###### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="findByUsername1" parameterType="string" resultType="com.itheima.domain.User">
    select * from user where username like #{username}
</select>
```

> 注意：只有一个参数，且是简单参数时， #{username}中的username可以写成其它任意名称

######3) 在单元测试类中编写测试代码

```java
/**
 * 使用#{}方式进行模糊查询--单元测试方法
 */
@Test
public void testFindUserByUsername1(){
    List<User> users = dao.findByUsername1("%王%");
    for (User user : users) {
        System.out.println(user);
    }
}
```

> 注意：模糊查询的条件值，前后需要有%

##### 3.5.2 使用${value}方式进行模糊查询

######1) 在映射器UserDao中增加方法

```java
/**
 * 使用${value}方式进行模糊查询
 */
List<User> findByUsername2(String username);
```

######2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="findByUsername2" parameterType="string" resultType="com.itheima.domain.User">
   select * from user where username like '%${value}%'
</select>
```

> 注意：
>
> ​	${value}是固定写法，不能做任何更改
>
> ​	在SQL语句中已经加了%，那么在测试代码中，传递参数值时就不需要再加%了

######3) 在单元测试类中编写测试代码

```java
/**
 * 使用${value}方式进行模糊查询--单元测试方法
 */
@Test
public void testFindUserByUsername2(){
    List<User> users = dao.findByUsername2("王");
    for (User user : users) {
        System.out.println(user);
    }
}
```

> 注意：SQL语句中已经加了%， 参数值前后不需要再加%

##### 3.5.3 #{}和${value}的区别（面试）

* `#{}`：表示一个占位符，相当于预编译对象的SQL中的?

  * 可以有效防止SQL注入；
  * Mybatis会自动进行参数的Java类型和JDBC类型转换；
  * `#{}`中间可以是value或者其它名称

  ```Text
  映射配置文件中的SQL：select * from user where username like #{username}
  单元测试中传递实参：%王%
  
  最终执行的SQL：select * from user where username like ?
  参数值：%王%
  ```

* `${value}`：表示拼接SQL串，相当于把实际参数值，直接替换掉`${value}`

  * 不能防止SQL注入
  * Mybatis不进行参数的Java类型和JDBC类型转换
  * 如果只有一个参数，并且是简单类型，`${value}`中只能是value，不能是其它名称

  ```text
  映射配置文件中的SQL：select * from user where username like '%${value}%'
  单元测试中传递实参：王
  
  最终执行的SQL：select * from user where username like '%王%'
  ```

#### 3.6 查询数量（聚合函数）

##### 1) 在映射器UserDao中增加方法

```java
Integer findTotalCount();
```

##### 2) 在映射配置文件中UserDao.xml增加statement

```xml
<select id="findTotalCount" resultType="int">
    select count(*) from user
</select>
```

##### 3) 在单元测试类中编写测试代码

```java
@Test
public void testFindTotalCount(){
    Integer totalCount = dao.findTotalCount();
    System.out.println(totalCount);
}
```

## 二、Mybatis的参数和结果集

### 1. OGNL表达式了解

​	OGNL：Object Graphic Navigator Language，是一种表达式语言，用来从Java对象中获取某一属性的值。本质上使用的是JavaBean的getXxx()方法。例如：user.getUsername()---> user.username

​	在#{}里、${}可以使用OGNL表达式，从JavaBean中获取指定属性的值

### 2. parameterType

#### 2.1 简单类型

​	例如：int, double, short 等基本数据类型，或者string

​	或者：java.lang.Integer, java.lang.Double, java.lang.Short,   java.lang.String

#### 2.2 POJO（JavaBean）

​	如果parameterType是POJO类型，在SQL语句中，可以在`#{}`或者`${}`中使用OGNL表达式来获取JavaBean的属性值。

​	例如：parameterType是com.itheima.domain.User， 在SQL语句中可以使用`#{username}`获取username属性值。

#### 2.3 POJO包装类--QueryVO

​	在web应用开发中，通常有综合条件的搜索功能，例如：根据商品名称 和 所属分类 同时进行搜索。这时候通常是把搜索条件封装成JavaBean对象；JavaBean中可能还有JavaBean。

##### 2.3.1  功能需求

​	根据用户名搜索用户信息，查询条件放到QueryVO的user属性中。QueryVO如下：

```java
public class QueryVO {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
```

##### 2.3.1 功能实现

######1) 在映射器UserDao中增加方法

```java
List<User> findByVO(QueryVO vo);
```

######2) 在映射配置文件中增加配置信息

```xml
<select id="findByVO" parameterType="com.itheima.domain.QueryVO"     resultType="com.itheima.domain.User">
   select * from user where username like #{user.username}
</select>
```

######3) 在单元测试类中编写测试代码

```java
@Test
public void testFindByVO(){
    QueryVO vo = new QueryVO();
    User user = new User();
    user.setUsername("%王%");
    vo.setUser(user);

    List<User> users = dao.findByVO(vo);
    for (User user1 : users) {
        System.out.println(user1);
    }
}
```

### 3. resultType

> 注意：resultType是查询select标签上才有的，用来设置查询的结果集要封装成什么类型的

#### 3.1 简单类型

​	例如：int, double, short 等基本数据类型，或者string

​	或者：java.lang.Integer, java.lang.Double, java.lang.Short,   java.lang.String

#### 3.2 POJO（JavaBean）

​	例如：com.itheima.domain.User

>  注意：JavaBean的属性名要和字段名保持一致

#### 3.3 JavaBean中属性名和字段名不一致的情况处理

##### 3.3.1 功能需求

​	有JavaBean类User2，属性名和数据库表的字段名不同。要求查询user表的所有数据，封装成User2的集合。其中User2如下：

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

##### 3.3.2 实现方案一：SQL语句中使用别名，别名和JavaBean属性名保持一致（用的少）

######1) 在映射器UserDao中增加方法

```java
/**
 * JavaBean属性名和字段名不一致的情况处理---方案一
 * @return
 */
List<User2> queryAll_plan1();
```

######2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="queryAll_plan1" resultType="com.itheima.domain.User2">
    select id as userId, username as username, birthday as userBirthday, address as userAddress, sex as userSex from user
</select>
```

######3) 在单元测试类中编写测试代码

```java
/**
 * JavaBean属性名和字段名不一致的情况处理---方案一 单元测试代码
 */
@Test
public void testQueryAllUser2_plan1(){
    List<User2> user2List = dao.queryAll_plan1();
    for (User2 user2 : user2List) {
        System.out.println(user2);
    }
}
```

##### 3.3.3 实现方案二：使用resultMap配置字段名和属性名的对应关系（推荐）

######1) 在映射器UserDao中增加方法

```java
/**
 * JavaBean属性名和字段名不一致的情况处理--方案二
 * @return
 */
List<User2> queryAll_plan2();
```

######2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="queryAll_plan2" resultMap="user2Map">
    select * from user
</select>

<!-- 
 resultMap标签：设置结果集中字段名和JavaBean属性的对应关系
     id属性：唯一标识
   type属性：要把查询结果的数据封装成什么对象，写全限定类名 
 -->
<resultMap id="user2Map" type="com.itheima.domain.User2">
    <!--id标签：主键字段配置。  property：JavaBean的属性名；  column：字段名-->
    <id property="userId" column="id"/>
    <!--result标签：非主键字段配置。 property：JavaBean的属性名；  column：字段名-->
    <result property="username" column="username"/>
    <result property="userBirthday" column="birthday"/>
    <result property="userAddress" column="address"/>
    <result property="userSex" column="sex"/>
</resultMap>
```

######3) 在单元测试类中编写测试代码

```java
/**
 * JavaBean属性名和字段名不情况处理--方案二  单元测试代码
 */
@Test
public void testQueryAllUser2_plan2(){
    List<User2> user2List = dao.queryAll_plan2();
    for (User2 user2 : user2List) {
        System.out.println(user2);
    }
}
```

## 三、Mybatis实现传统开发CURD（了解）

​	Mybatis中提供了两种dao层的开发方式：一是使用映射器接口代理对象的方式；二是使用映射器接口实现类的方式。其中代理对象的方式是主流，也是我们主要学习的内容。

### 1. 相关类介绍

#### 1.1 SqlSession

##### 1.1.1 SqlSession简介

​	SqlSession是一个面向用户的接口，定义了操作数据库的方法，例如：selectList, selectOne等等。

​	每个线程都应该有自己的SqlSession对象，它不能共享使用，也是**线程不安全**的。因此最佳的使用范围是在请求范围内、或者方法范围内，绝不能将SqlSession放到静态属性中。

​	SqlSession使用原则：要做到SqlSession：随用随取，用完就关，一定要关

##### 1.1.2 SqlSession的常用API	

​	SqlSession操作数据库的常用方法有：

| 方法                                       | 作用                                                         |
| ------------------------------------------ | ------------------------------------------------------------ |
| selectList(String statement, Object param) | 查询多条数据，封装JavaBean集合                               |
| selectOne(String statement, Object param)  | 查询一条数据，封装JavaBean对象<br />查询一个数据，比如查询数量 |
| insert(String statement, Object param)     | 添加数据，返回影响行数                                       |
| update(String statement, Object param)     | 修改数据，返回影响行数                                       |
| delete(String statement, Object param)     | 删除数据，返回影响行数                                       |

> 以上方法中的参数statment，是映射配置文件中的namespace 和  id的值方法名组成的。
>
> 例如：
>
> ​	映射配置文件的namespace值为com.itheima.dao.UserDao，执行的方法名是queryAll
>
> ​	那么statement的值就是：com.itheima.dao.UserDao.queryAll

#### 1.2 SqlSessionFactory

​	是一个接口，定义了不同的openSession()方法的重载。SqlSessionFactory一旦创建后，可以重复使用，通常是以单例模式管理。

​	SqlSessionFactory使用原则：单例模式管理，一个应用中，只要有一个SqlSessionFactory对象即可。

#### 1.3 SqlSessionFactoryBuilder

​	用于构建SqlSessionFactory工厂对象的。一旦工厂对象构建完成，就不再需要SqlSessionFactoryBuilder了，通常是作为工具类使用。

​	SqlSessionFactoryBuilder：只要生产了工厂，builder对象就可以垃圾回收了

### 2. 需求说明

针对user表进行CURD操作，要求使用映射器接口实现类的方式实现：

- 查询全部用户，得到`List<User>`（上节课快速入门已写过，略）
- 保存用户（新增用户）
- 修改用户
- 删除用户
- 根据主键查询一个用户，得到`User`
- 模糊查询
- 查询数量

### 3. 准备Mybatis环境

#### 3.1 创建Maven的Java项目，准备JavaBean

##### 1) 创建Maven的Java项目，坐标为：

```xml
	<groupId>com.itheima</groupId>
    <artifactId>day47_mybatis02_dao</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
```

##### 2) 在pom.xml中添加依赖：

```xml
<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.46</version>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.17</version>
    </dependency>
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.4.6</version>
    </dependency>
</dependencies>
```

##### 3) 创建JavaBean

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

#### 3.2 准备Mybatis的映射器和配置文件

##### 1) 创建映射器接口UserDao（暂时不需要增加方法，备用）

```java
public interface UserDao{
    
}
```

##### 2) 创建映射器接口的实现类UserDaoImpl

```java
public class UserDaoImpl implements UserDao{
    private SqlSessionFactory factory;

    /**
     * 构造方法。因为工厂对象，是整个应用只要一个就足够了，所以这里不要创建SqlSessionFactory对象
     * 而是接收获取到工厂对象来使用。
     */
    public UserDaoImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }
}
```

##### 3) 创建映射配置文件UserDao.xml（暂时不需要配置statement，备用）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.itheima.dao.UserDao">
    
</mapper>
```

##### 4) 创建Mybatis的核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <typeAliases>
        <package name="com.itheima.domain"/>
    </typeAliases>
    <environments default="mysql">
        <environment id="mysql">
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
        <mapper resource="com/itheima/dao/UserDao.xml"/>
    </mappers>
</configuration>
```

##### 4) 准备log4j.properties日志配置文件

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

#### 3.3 准备单元测试类

```java
public class MybatisDaoCURDTest {
    private InputStream is;
    private SqlSessionFactory factory;
    private UserDao dao;

    @Before
    public void init() throws IOException {
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        factory = builder.build(is);
        //创建Dao实现类对象时，把factory作为构造参数传递进去---一个应用只要有一个factory就够了
        dao = new UserDaoImpl(factory);
    }

    @After
    public void destory() throws IOException {
        is.close();
    }
}
```

### 4. 编写代码实现需求

#### 4.1 查询全部用户

##### 1) 在映射器UserDao中增加方法

```java
List<User> queryAll();
```

##### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="queryAll" resultType="User">
    select * from user
</select>
```

##### 3) 在映射器实现类UserDaoImpl中实现方法

```java
@Override
public List<User> queryAll() {
    SqlSession session = factory.openSession();
    List<User> users = session.selectList("com.itheima.dao.UserDao.queryAll");
    session.close();
    return users;
}
```

##### 4) 在单元测试类中编写测试代码

```java
@Test
public void testQueryAll(){
    List<User> users = dao.queryAll();
    for (User user : users) {
        System.out.println(user);
    }
}
```

#### 4.2 保存/新增用户

##### 1) 在映射器UserDao中增加方法

```java
void save(User user);
```

##### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<insert id="save" parameterType="User">
    <selectKey resultType="int" keyProperty="id" order="AFTER">
        select last_insert_id()
    </selectKey>
    insert into user (id, username, birthday, address, sex) 
    values (#{id}, #{username}, #{birthday},#{address},#{sex})
</insert>
```

##### 3) 在映射器实现类UserDaoImpl中实现方法

```java
@Override
public void save(User user) {
    SqlSession session = factory.openSession();
    session.insert("com.itheima.dao.UserDao.save", user);
    session.commit();
    session.close();
}
```

##### 4) 在单元测试类中编写测试代码

```java
@Test
public void testSaveUser(){
    User user = new User();
    user.setUsername("tom");
    user.setAddress("广东深圳");
    user.setBirthday(new Date());
    user.setSex("男");

    System.out.println("保存之前：" + user);
    dao.save(user);
    System.out.println("保存之后：" + user);
}
```

#### 4.2 修改用户

##### 1) 在映射器UserDao中增加方法

```java
void edit(User user);
```

##### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<update id="edit" parameterType="User">
    update user set username = #{username}, birthday = #{birthday}, 
    address = #{address}, sex = #{sex} where id = #{id}
</update>
```

##### 3) 在映射器实现类UserDaoImpl中实现方法

```java
@Override
public void edit(User user) {
    SqlSession session = factory.openSession();
    session.update("com.itheima.dao.UserDao.edit", user);
    session.commit();
    session.close();
}
```

##### 4) 在单元测试类中编写测试代码

```java
@Test
public void testEditUser(){
    User user = new User();
    user.setId(71);
    user.setUsername("jerry");
    user.setAddress("广东深圳宝安");
    user.setSex("女");
    user.setBirthday(new Date());

    dao.edit(user);
}
```

#### 4.3 删除用户

##### 1) 在映射器UserDao中增加方法

```java
void delete(Integer id);
```

##### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<delete id="delete" parameterType="int">
    delete from user where id = #{uid}
</delete>
```

##### 3) 在映射器实现类UserDaoImpl中实现方法

```java
@Override
public void delete(Integer id) {
    SqlSession session = factory.openSession();
    session.delete("com.itheima.dao.UserDao.delete", id);
    session.commit();
    session.close();
}
```

##### 4) 在单元测试类中编写测试代码

```java
@Test
public void testDeleteUser(){
    dao.delete(71);
}
```

#### 4.4 根据主键查询一个用户

##### 1) 在映射器UserDao中增加方法

```java
User findById(Integer id);
```

##### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="findById" parameterType="int" resultType="User">
    select * from user where id = #{id}
</select>
```

##### 3) 在映射器实现类UserDaoImpl中实现方法

```java
@Override
public User findById(Integer id) {
    SqlSession session = factory.openSession();
    User user = session.selectOne("com.itheima.dao.UserDao.findById", id);
    session.close();
    return user;
}
```

##### 4) 在单元测试类中编写测试代码

```java
@Test
public void testFindUserById(){
    User user = dao.findById(48);
    System.out.println(user);
}
```

#### 4.5 模糊查询

##### 4.5.1 使用`#{}`方式进行模糊查询

######1) 在映射器UserDao中增加方法

```java
/**使用#{}方式进行模糊查询*/
List<User> findByUsername1(String username);
```

######2) 在映射配置文件UserDao.xml中增加statement

```xml
<!-- 使用#{}方式进行模糊查询 -->
<select id="findByUsername1" parameterType="string" resultType="User">
    select * from user where username like #{username}
</select>
```

######3) 在映射器实现类UserDaoImpl中实现方法

```java
@Override
public List<User> findByUsername1(String username) {
    SqlSession session = factory.openSession();
    List<User> users = session.selectList("com.itheima.dao.UserDao.findByUsername1", username);
    session.close();
    return users;
}
```

######4) 在单元测试类中编写测试代码

```java
@Test
public void testFindUserByUsername1(){
    List<User> users = dao.findByUsername1("%王%");
    for (User user : users) {
        System.out.println(user);
    }
}
```

##### 4.5.2 使用`${value}`方式进行模糊查询

###### 1) 在映射器UserDao中增加方法

```java
/**使用${value}方式进行模糊查询*/
List<User> findByUsername2(String username);
```

###### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<!-- 使用${value}方式进行模糊查询 -->
<select id="findByUsername2" parameterType="string" resultType="User">
    select * from user where username like '%${value}%'
</select>
```

###### 3) 在映射器实现类UserDaoImpl中实现方法

```java
@Override
public List<User> findByUsername2(String username) {
    SqlSession session = factory.openSession();
    List<User> users = session.selectList("com.itheima.dao.UserDao.findByUsername2", username);
    session.close();
    return users;
}
```

###### 4) 在单元测试类中编写测试代码

```java
@Test
public void testFindUserByUsername2(){
    List<User> users = dao.findByUsername2("王");
    for (User user : users) {
        System.out.println(user);
    }
}
```

#### 4.6 查询数量

##### 1) 在映射器UserDao中增加方法

```java
Integer findTotalCount();
```

##### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="findTotalCount" resultType="int">
    select count(*) from user
</select>
```

##### 3) 在映射器实现类UserDaoImpl中实现方法

```java
@Override
public Integer findTotalCount() {
    SqlSession session = factory.openSession();
    Integer count = session.selectOne("com.itheima.dao.UserDao.findTotalCount");
    session.close();
    return count;
}
```

##### 4) 在单元测试类中编写测试代码

```java
@Test
public void testFindTotalCount(){
    Integer totalCount = dao.findTotalCount();
    System.out.println(totalCount);
}
```

### 5. 映射器接口代理对象方式和实现类方式运行原理

#### 5.1 映射器接口代理对象的方式  运行过程

​	debug跟踪--->实现类传统方式的sqlsession的方法--->底层是JDBC的代码

#### 5.2 映射器接口实现类的方式  运行过程

​	debug跟踪--->底层是JDBC的代码

## 四、SqlMapConfig.xml核心配置文件

SqlMapConfig.xml中配置的内容和顺序如下：

```text
properties（属性） ★
settings（全局配置参数） 
typeAliases（类型别名） ★ 
typeHandlers（类型处理器） 
objectFactory（对象工厂） 
plugins（插件） 
environments（环境集合属性对象） 
environment（环境子属性对象） 
transactionManager（事务管理） 
dataSource（数据源） 
mappers（映射器） ★
```

### 1. properties属性（了解）

可以在SqlMapConfig.xml中引入properties文件的配置信息，实现配置的**热插拔**效果。例如：

#### 1.1 准备jdbc.properties放在resources下

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql:///mybatis49
jdbc.username=root
jdbc.password=root
```

#### 1.2 在SqlMapConfig.xml中引入jdbc.properties

```xml
<configuration>
    <!-- 
 	properties标签：
		resource属性：properties资源文件的路径，从类加载路径下开始查找
		url属性：url地址
	-->
    <properties resource="jdbc.properties"/>

    <environments default="mysql_mybatis">
        <environment id="mysql_mybatis">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <!-- 使用${OGNL}表达式，获取从properties中得到的配置信息 -->
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.username}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <mapper resource="com/itheima/dao/UserDao.xml"/>
    </mappers>
</configuration>
```

#### 1.3 注意加载顺序

假如：

有jdbc.properties配置文件在resource下

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql:///mybatis49
jdbc.username=root
jdbc.password=root
```

SqlMapConfig.xml配置如下：

```xml
<properties resurce="jdbc.properties">
	<property name="jdbc.username" value="root"/>
    <property name="jdbc.password" value="123456"/>
</properties>
```

那么：

​	Mybatis会首先加载xml配置文件里的的property标签，得到了jdbc.username和jdbc.password

​	然后再加载外部jdbc.properties配置文件，覆盖掉刚刚得到的jdbc.username和jdbc.password的值

总结：

​	外部properties配置文件的优先级，要高于xml里property标签的配置

### 2. typeAlias类型别名

​	在映射配置文件中，我们要写大量的parameterType和resultType，如果全部都写全限定类名的话，代码就太过冗余，开发不方便。可以使用类型别名来解决这个问题。



​	类型别名：是Mybatis为Java类型设置的一个短名称，目的仅仅是为了减少冗余。

​	注意：**类型别名不区分大小写**

Mybatis提供的别名有：

| **别名**   | **映射的类型** |
| ---------- | -------------- |
| _byte      | byte           |
| _long      | long           |
| _short     | short          |
| _int       | int            |
| _integer   | int            |
| _double    | double         |
| _float     | float          |
| _boolean   | boolean        |
| string     | String         |
| byte       | Byte           |
| long       | Long           |
| short      | Short          |
| int        | Integer        |
| integer    | Integer        |
| double     | Double         |
| float      | Float          |
| boolean    | Boolean        |
| date       | Date           |
| decimal    | BigDecimal     |
| bigdecimal | BigDecimal     |
| object     | Object         |
| map        | Map            |
| hashmap    | HashMap        |
| list       | List           |
| arraylist  | ArrayList      |
| collection | Collection     |
| iterator   | Iterator       |

### 3. 自定义类型别名

​	例如：自己定义的JavaBean，全限定类名太长，可以自定义类型别名。

#### 3.1 给一个类指定别名

##### 3.1.1 在SqlMapConfig.xml中配置一个类的别名

```xml
    <typeAliases>
        <!-- type：要指定别名的全限定类名    alias：别名 -->
        <typeAlias type="com.itheima.domain.QueryVO" alias="vo"/>
        <typeAlias type="com.itheima.domain.User" alias="user"/>
        <typeAlias type="com.itheima.domain.User2" alias="user2"/>
    </typeAliases>
```

##### 3.1.2 在映射配置文件中使用类型别名

```xml
<!-- parameterType使用别名：vo， resultType使用别名：user -->    
<select id="findByVO" parameterType="vo" resultType="user">
    select * from user where username like #{user.username}
</select>
```

#### 3.2 指定一个包名

##### 3.2.1 在SqlMapConfig.xml中为一个package下所有类注册别名：<span style="color:red;">类名即别名</span>

```xml
    <typeAliases>
        <!-- 把com.itheima.domain包下所有JavaBean都注册别名，类名即别名，不区分大小写 -->
        <package name="com.itheima.domain"/>
    </typeAliases>
```

##### 3.2.2 在映射配置文件中使用类型别名

```xml
<!-- parameterType使用别名：queryvo， resultType使用别名：user -->    
<select id="findByVO" parameterType="queryvo" resultType="user">
    select * from user where username like #{user.username}
</select>
```

### 4. mappers映射器

​	用来配置映射器接口的配置文件位置，或者映射器接口的全限定类名

#### 4.1 `<mapper resource=""/>`

用于指定映射配置文件xml的路径，支持xml开发方式，例如：

`<mapper resource="com/itheima/dao/UserDao.xml"/>`

注意：

​	映射配置文件的名称，和映射器接口类名   可以不同

​	映射配置文件的位置，和映射器接口位置   可以不同

> 配置了xml的路径，Mybatis就可以加载statement信息，并且根据namespace属性找到映射器

#### 4.2 `<mapper class=""/>`

用于指定映射器接口的全限定类名，支持XML开发和注解开发，例如：

`<mapper class="com.itheima.dao.UserDao"/>`

如果是使用xml方式开发，那么要注意：

​	映射配置文件的名称 要和 映射器接口的类名相同

​	映射配置文件的位置 要和 映射器接口的位置相同

> Mybatis只知道映射器的名称和位置，不知道配置文件的名称和位置。只能查找同名同路径的配置文件

#### 4.3 `<package name=""/>`

用于自动注册指定包下所有的映射器接口，支持XML开发和注解开发，例如：

`<package class="com.itheima.dao"/>`

如果是使用XML方式开发，那么要注意：

​	映射配置文件的名称 要和 映射器接口的类名相同

​	映射配置文件的位置 要和 映射器接口的位置相同

> Mybatis只能根据包名找到所有的映射器的类名和位置， 不知道配置文件的名称和位置。只能查找同名同路径的配置文件
