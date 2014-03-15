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
   ant
   ```

4. 在tomcat webapps 下建立 WEB-INF 的软连接

   ```
   ln -s `pwd` /var/lib/tomcat7/webapps/
   ```

> 如果需要，可以编辑 `WEB-INF/src/applicationContext.xml` 中的配置（如JDBC）；编辑后，需要重新执行 `ant`


