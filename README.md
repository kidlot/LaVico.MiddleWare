WeLab 的 LaVico CRM 中间件
=================

## 部署

Server OS: ubuntu server 12.04

0. 安装依赖环境：

   ```
   sudo apt-get install git tomcat7 openjdk-7-jdk ant 
   ```

1. 生成server pub key

   ```
   ssh-keygen
   cat ~/.ssh/id_rsa.pub
   ```

   复制打印出来的 pub key ，粘贴到 https://github.com/aleechou/lavico.middleware/settings/keys

2. 从 github clone 代码

   ```
   git clone git@github.com:aleechou/lavico.middleware.git
   ```

3. 编译

   ```
   cd lavico.middleware
   ant --buildfile build-ubuntu.xml
   ```

   > mac 下改为 `ant --buildfile build-osx.xml`

4. 在tomcat webapps 下建立软连接

   ```
   ln -s `pwd` /var/lib/tomcat7/webapps/
   ```

5. 重启 tomcat 

   ```
   sudo service tomcat7 restart
   ```

> 如果需要，可以编辑 `WEB-INF/src/applicationContext.xml` 中的配置（如JDBC）；编辑后，需要重新执行 `ant` 编译，并重启 tomcat


===

# 接口文档说明


* 申请会员卡 MemeberApply

  ### 参数：

  * openid 微信id
  
  * MEM_PSN_BIRTHDAY 生日 yyyy-mm-dd格式

  * MOBILE_TELEPHONE_NO 手机号码

  * MEM_PSN_SEX 性别，0=女, 1=男

  * MEM_PSN_CNAME 用户名字

  ### 返回：

  {"MEMBER_ID":9114883,"issuccessed":true,"error":""}

  * MEMBER_ID: 海澜CRM会员ID

  * issuccessed: true/false 操作是否成功

  * error: 如果失败，返回的错误提示
 


* 会员卡绑定 MemeberBind

  ### 参数：

  * openid 微信id
  
  * MOBILE_TELEPHONE_NO 手机号码

  * MEM_OLDCARD_NO 实体会员卡卡号（俗称老卡卡号）

  * MEM_PSN_CNAME 用户名字

  ### 返回：

  {"MEMBER_ID":9114883,"issuccessed":true,"error":""}

  * MEMBER_ID: 海澜CRM会员ID

  * issuccessed: true/false 操作是否成功

  * error: 如果失败，返回的错误提示
 


* 会员卡解除绑定 MemeberUnbind

  ### 参数：

  * openid 微信id
  
  * MEMBER_ID 海澜CRM 用户id，由 MemberApple/MemberBind 返回

  ### 返回：

  {"issuccessed":true,"error":""}

  * issuccessed: true/false 操作是否成功

  * error: 如果失败，返回的错误提示