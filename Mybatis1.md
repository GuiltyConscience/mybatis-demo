# Mybatis1-课堂笔记

* 要学习三大框架：SSM， SpringMVC+Spring+Mybatis，重点是**练习**
* Mybatis-->Spring-->SpringMVC-->三大框架整合
* Mybatis的3天课程安排
  * 第一天：
    * 概述
    * 快速入门
    * 使用Mybatis进行CURD：代理方式
  * 第二天：
    * 使用Mybatis进行CURD：dao实现类方式
    * 动态SQL拼接
    * 多表查询
  * 第三天：
    * 数据源
    * 懒加载
    * Mybatis的缓存
    * Mybatis的注解开发

## 一、Mybatis概述

### 1. 框架简介

#### 1.1 什么是框架

​	框架：是**整个或部分应用的可重用设计，是可定制化的应用骨架**。它可以帮开发人员简化开发过程，提高开发效率。

​	简而言之，框架是一个应用系统的半成品，开发人员在框架的基础上，根据业务需求开发功能。即：别人搭台，你唱戏。

* 框架：让开发人员的代码更简单，功能更强

#### 1.2 框架解决了什么问题

​	**框架主要是解决了技术整合问题**

​	一个应用系统，必定要选用大量的技术来完成业务需求，实现功能。这就导致应用系统依赖于具体的技术，一旦技术发生了变化或者出现问题，会对应用系统造成直接的冲击，这是应该避免的。

​	框架的出现，解决了这个问题：框架是技术的整合。如果使用了框架，在框架基础上进行开发，那么开发人员就可以直接调用框架的API来实现功能，而不需要关注框架的底层具体使用了哪些技术。这就相当于框架“屏蔽”了具体的技术，实现了应用系统功能和技术的解耦。

​	**框架一般处于低层应用平台（如JavaEE）和高层业务逻辑之间**

#### 1.3 有哪些常见的框架

​	SSH：Spring+Struts+Hibernate

​	SSM：Spring+SpringMVC+Mybatis

​	每个框架都是要解决一些具体的问题的，我们可以从JavaEE的三层架构，来说一下常见的框架有哪些。

1. **Mybatis**：作用在dao层，负责数据库访问的框架。

   ​	它原本是Apache的一个开源项目ibatis，后来迁移到了Google code，并改名为Mybatis；之后又迁移到了github上。

   ​	它是一个优秀的Java轻量级dao层框架，对JDBC进行了封装，使开发人员只需要关注SQL语句，而不需要关注JDBC的API执行细节。

2. **Hibernate**：作用在dao层，负责数据库访问的框架。

   ​	Hibernate是一个完全面向对象的Dao层框架，封装程度非常高，开发人员可以完全以面向对象的方式操作数据库，甚至不需要编写SQL语句。

   ​	但是，正因为Hibernate的封装程度过高，导致它的执行效率受到了影响，是重量级框架。目前在国内使用的越来越少了。

3. **SpringMVC**：作用在web层，负责和客户端交互的框架。

   ​	SpringMVC是Spring Framework的后续产品，受益于Spring框架的流行，并且因为SpringMVC使用简单、和Spring整合简单，导致SpringMVC框架在国内使用的也越来越多。

4. **Struts1/Struts2**：作用在web层，负责和客户端交互的框架。

   ​	Struts1是比较老的框架，目前已经基本不使用了。

   ​	Struts2目前使用的也越来越少，逐步被SpringMVC代替

5. **Spring**：不是作用在某一层，而是实现web层、Service层、Dao层之间解耦的框架，是三层之间的粘合剂

   ​	Spring框架是为了解决应用开发的复杂性而创建的，任何Java应用都可以从Spring中受益。Spring是一个轻量级控制反转（IoC)和面向切面（AOP）的容器框架。

### 2. Mybatis简介

#### 2.1 JDBC的问题

1. 硬编码问题
   - 数据库连接信息的硬编码
   - SQL语句和参数硬编码
   - 结果集封装硬编码
2. 大量的重复代码
3. 性能问题

#### 2.2 Mybatis介绍

​	Mybatis是一个优秀的Java轻量级持久层框架。

- 它内部封装了JDBC，使开发人员**只需要关心SQL语句**，而不需要处理繁琐的JDBC步骤
- 它采用了ORM思想，解决了实体和数据库映射的问题。只要提供好sql语句，**配置**了映射，Mybatis会自动根据参数值动态生成SQL，执行SQL并把结果封装返回给我们。
- 它支持XML和注解两种方式配置映射。

#### 2.3 ORM思想

​	ORM：Object Relational Mapping，对象关系映射思想。指把Java对象和数据库的表和字段进行关联映射，从而达到操作Java对象，就相当于操作了数据库。

### 3. Mybatis下载

- Mybatis官网：<http://www.mybatis.org/mybatis-3/zh/index.html>
- Mybatis下载地址：<https://github.com/mybatis/mybatis-3/releases/download/mybatis-3.4.6/mybatis-3.4.6.zip>
- Mybatis源码下载：<https://github.com/mybatis/mybatis-3/archive/mybatis-3.4.6.zip>

## 二、==Mybatis快速入门==

### 1. 需求描述

查询所有用户信息，获取用户集合`List<User>`

### 2. 准备工作

#### 2.1 统一开发环境

1. 初始化数据库：执行数据库脚本《资料/mybatisdb.sql》
2. 准备开发环境：jdk1.8， Maven和本地仓库，idea

#### 2.2 实现步骤

1. 创建Maven项目，准备JavaBean
2. 编写Mybatis的代码，查询所有用户
3. 编写测试代码

### 3. 入门案例

#### 3.1 创建Maven项目，准备JavaBean

##### 1) 创建Maven的java项目

​	因为只涉及Dao层，不涉及客户端，所以只要创建Java项目即可。项目坐标信息如下：

```xml
	groupId:         com.itheima
	artifactId:      mybatis01_quickstart
	version:         1.0-SNAPSHOT
	packing:         jar
```

##### 2) 在pom.xml中添加依赖

```xml
    <dependencies>
        <!--Junit单元测试-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <!--MySql的数据库驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>
        <!--Mybatis的jar包-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.6</version>
        </dependency>
        <!--Mybatis依赖的日志包-->
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
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

#### 3.2 编写Mybatis的代码，查询所有用户

##### 1) 创建dao接口（映射器）

​	在Mybatis里，把dao层的接口称之为**映射器**（取  调用接口的方法即相当于操作数据库 之意）。

​	映射器的类名，可以叫`XXXMapper`，也可以叫`XXXDao`。我们这里按照之前的习惯，取名`UserDao`

​	注意：只要创建接口即可，不需要创建接口的实现类

```java
public interface UserDao {
    List<User> queryAll();  
}
```

##### 2) 准备映射配置文件xml

注意：

1. 映射配置文件名称要和映射器类名一样。例如：映射器叫UserDao，那么配置文件就叫UserDao.xml
2. 映射配置文件位置要和映射器位置一样。例如：映射器在com.itheima.dao里，那么配置文件就应该在resources的com/itheima/dao目录下

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace：给哪个接口配置的映射，写接口的全限定类名-->
<mapper namespace="com.itheima.dao.UserDao">
    <!--select标签：表示要执行查询语句； id：给接口里哪个方法配置的，写方法名；resultType：结果集封装类型-->
    <select id="queryAll" resultType="com.itheima.domain.User">
        select * from user
    </select>
</mapper>
```

##### 3) 准备Mybatis的日志配置文件

​	Mybatis支持使用log4j输出执行日志信息，但是需要我们提供log4j的配置文件：log4j.properties

注意：

1. 如果没有log4j.properties，不影响Mybatis的功能，只是没有详细日志而已
2. 如果需要日志的话，要把log4j.properties文件放到resources目录下。log4j.properties内容如下：

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

##### 4) 准备Mybatis的核心配置文件xml

注意：

1. 核心配置文件的名称随意，我们习惯叫 SqlMapConfig.xml
2. 核心配置文件的位置随意，我们习惯放到resources目录下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--mybatis的核心配置文件，主要配置数据库连接信息-->
<configuration>
    <!--配置默认的数据库环境-->
    <environments default="mysql_mybatis">
        <!--定义一个数据库连接环境-->
        <environment id="mysql_mybatis">
            <!--设置事务管理方式，固定值JDBC-->
            <transactionManager type="JDBC"/>
            <!--设置数据源，POOLED，UNPOOLED，JNDI，我们使用POOLED-->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql:///mybatis"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>

    <!--  !!!配置映射文件的位置!!!!!!!!!!!!!!这一步千万不要忘记!!!!!!!!!!!!!!  -->
    <mappers>
        <mapper resource="com/itheima/dao/UserDao.xml"/>
    </mappers>
</configuration>
```

#### 3.3 编写测试代码

```java
	@Test
    public void testQuickStart() throws IOException {
        //1. 读取核心配置文件SqlMapConfig.xml
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2. 创建SqlSessionFactoryBuilder构造者对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //3. 使用构造者builder，根据配置文件的信息is，构造一个SqlSessionFactory工厂对象
        SqlSessionFactory factory = builder.build(is);
        //4. 使用工厂对象factory，生产一个SqlSession对象
        SqlSession session = factory.openSession();
        //5. 使用SqlSession对象，获取映射器UserDao接口的代理对象
        UserDao dao = session.getMapper(UserDao.class);
        //6. 调用UserDao代理对象的方法，查询所有用户
        List<User> users = dao.queryAll();
        for (User user : users) {
            System.out.println(user);
        }
        //7. 释放资源
        session.close();
        is.close();
    }
```

### 4. Mybatis的运行过程分析

​	Mybatis的本质是JDBC，但是不需要关注JDBC，**只需要关注SQL语句**

## 三、==Mybatis实现CURD--接口代理对象的方式==

### 1. 需求说明

针对user表进行CURD操作：

- 查询全部用户，得到`List<User>`（上节课快速入门已写过，略）
- 保存用户（新增用户）
- 修改用户
- 删除用户
- 根据主键查询一个用户，得到`User`
- 模糊查询
- 查询数量

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

###### 3) 在单元测试类中编写测试代码

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

###### 1) 在映射器UserDao中增加方法

```java
/**
 * 使用${value}方式进行模糊查询
 */
List<User> findByUsername2(String username);
```

###### 2) 在映射配置文件UserDao.xml中增加statement

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

###### 3) 在单元测试类中编写测试代码

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

##### 3.5.3 ==#{}和${value}的区别==

- `#{}`：表示一个占位符，相当于预编译对象的SQL中的?

  - 可以有效防止SQL注入；
  - Mybatis会自动进行参数的Java类型和JDBC类型转换；
  - `#{}`中间可以是value或者其它名称

  ```Text
  映射配置文件中的SQL：select * from user where username like #{username}
  单元测试中传递实参：%王%
  
  最终执行的SQL：select * from user where username like ?
  参数值：%王%
  ```

- `${value}`：表示拼接SQL串，相当于把实际参数值，直接替换掉`${value}`

  - 不能防止SQL注入
  - Mybatis不进行参数的Java类型和JDBC类型转换
  - 如果只有一个参数，并且是简单类型，`${value}`中只能是value，不能是其它名称

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

### 4. 小结

* Mybatis实现CURD所有功能都只有两步：
  * 在映射器里增加方法
  * 在映射配置文件里增加statement
    * 选择合适的标签：
      * select：执行select语句的
      * insert：执行insert语句的
      * update：执行update语句的
      * delete：执行delete语句的
    * 如果有参数，标签上要增加属性：parameterType
    * 如果是查询，有结果集，就需要设置结果集封装类型：resultType
    * 标签里的SQL语句：
      * 参数值要使用 `#{}`。
      * `#{}`里边：
        * 如果是从简单类型参数里，假如只有一个参数，`#{任意名称}`
        * 如果是众JavaBean参数里，`#{JavaBean的属性名称}`
  * 在测试代码里，调用方法操作数据库：
    * 如果执行的是DML操作，操作完成一定要提交事务`session.commit()`

## 四、Mybatis的参数和结果集

### 1. OGNL表达式了解

​	OGNL：Object Graphic Navigator Language，是一种表达式语言，用来从Java对象中获取某一**属性**的值。本质上使用的是JavaBean的getXxx()方法。例如：user.getUsername()---> user.username

​	在`#{}`里、`${}`可以使用OGNL表达式，从JavaBean中获取指定属性的值

### 2. parameterType

#### 2.1 简单类型

* 参数写法：

​	例如：int, double, short 等基本数据类型，或者string

​	或者：java.lang.Integer, java.lang.Double, java.lang.Short,   java.lang.String

* SQL语句里获取参数：

  如果是一个简单类型参数，写法是：`#{随意}`

#### 2.2 POJO（JavaBean）

* 参数写法：

​	如果parameterType是POJO类型，在SQL语句中，可以在`#{}`或者`${}`中使用OGNL表达式来获取JavaBean的属性值。

​	例如：parameterType是com.itheima.domain.User， 在SQL语句中可以使用`#{username}`获取username属性值。

* SQL语句里获取参数：`#{xxx}`

#### 2.3 POJO包装类（复杂JavaBean）--QueryVO

* 参数写法：

​	在web应用开发中，通常有综合条件的搜索功能，例如：根据商品名称 和 所属分类 同时进行搜索。这时候通常是把搜索条件封装成JavaBean对象；JavaBean中可能还有JavaBean。

* SQL里取参数：`#{xxx.xx.xx}`

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

###### 1) 在映射器UserDao中增加方法

```java
List<User> findByVO(QueryVO vo);
```

###### 2) 在映射配置文件中增加配置信息

```xml
<select id="findByVO" parameterType="com.itheima.domain.QueryVO"     resultType="com.itheima.domain.User">
   select * from user where username like #{user.username}
</select>
```

###### 3) 在单元测试类中编写测试代码

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

> 注意：JavaBean的属性名要和字段名保持一致

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

###### 1) 在映射器UserDao中增加方法

```java
/**
 * JavaBean属性名和字段名不一致的情况处理---方案一
 * @return
 */
List<User2> queryAll_plan1();
```

###### 2) 在映射配置文件UserDao.xml中增加statement

```xml
<select id="queryAll_plan1" resultType="com.itheima.domain.User2">
    select id as userId, username as username, birthday as userBirthday, address as userAddress, sex as userSex from user
</select>
```

###### 3) 在单元测试类中编写测试代码

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

###### 1) 在映射器UserDao中增加方法

```java
/**
 * JavaBean属性名和字段名不一致的情况处理--方案二
 * @return
 */
List<User2> queryAll_plan2();
```

###### 2) 在映射配置文件UserDao.xml中增加statement

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

###### 3) 在单元测试类中编写测试代码

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



# 内容回顾

1. 能够搭建Mybatis的环境

   1. 创建Maven的java项目，引入依赖：mysql驱动包、mybatis、junit、log4j
   2. 创建JavaBean：建议属性名和表的字段名一致
   3. **在dao层，创建映射器**：只要接口即可，不需要创建实现类
   4. **在resouces里创建映射配置文件**：映射器类名.xml，放在 映射器相同的目录里
      * 每个映射器，都要有一个映射配置文件
      * 映射器里每个方法，都要在映射配置里创建Statement
   5. 创建Mybatis核心配置文件
      * environments：必须要配置数据库的环境
      * mappers：必须要把配置文件的路径配置进来
   6. 创建单元测试类

2. 实现CURD功能

   1. 插入数据，配置statement

      ```xml
      <insert id="方法名" paramerterType="参数类型">
          <selectKey resultType="主键值类型" keyProperty="主键值放在哪个属性里" order="AFTER">
          	select last_insert_id()
          </selectKey>
          insert into .....
      </insert>
      ```

      > 插入数据之后，一定要提交事务 session.commit()

   2. 修改数据，配置statement

      ```xml
      <update id="方法名" parameterType="参数类型">
          update ......
      </update>
      ```

      > 修改数据之后，一定要提交事务 session.commit()

   3. 删除数据，配置statement

      ```xml
      <delete id="方法名" parameterType="参数类型">
          delete ....
      </delete>
      ```

      > 修改数据之后，一定要提交事务 session.commit()

   4. 模糊查询：`#{}`

      ```xml
      <select id="方法名" parameterType="参数类型" resultType="结果集封装类型">
          select * from ....   where 条件 like  #{OGNL表达式}
      </select>
      ```

      > 注意，调用方法传参的时候，参数值前边要加上%

   5. 模糊查询：`${}`

      ```xml
      <select id="方法名" parameterType="参数类型" resultType="结果集封装类型">
          select * from ....   where 条件 like '%${OGNL表达式}%'
      </select>
      ```

      > 注意：
      >
      > * SQL语句里要写成`${}`，如果只有一个参数，并且是简单类型，一定要写成：`${value}`
      > * 调用方法传参的时候，参数值不需要再加%

   6. `#{}`和`${}`的区别：

   7. 查询数量：

3. 参数深入：

   1. parameterType：
      1. 简单类型：`#{随意}`
      2. POJO：`#{JavaBean的属性名}`
      3. 复杂POJO：`#{JavaBean的属性名.xxx.xxxx}`
   2. resultType