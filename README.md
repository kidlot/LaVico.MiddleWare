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

## REST 路径

```
http://<server ip>:<port>/<appname>/{brand}/<interface name>?<paramters>
```

例如，申请会员卡：

```
http://127.0.0.1:8080/lavico.middleware/L/Member/Apply?openid=1232&MOBILE_TELEPHONE_NO=12334527644&MEM_PSN_CNAME=alee&MEM_PSN_SEX=1&MEM_PSN_BIRTHDAY=1982-10-11
```

> `L` 代表 LaVico ，`Member/Apply` 是接口名称


### 申请会员卡 {brand}/Member/Apply

#### 参数：

  * openid
  
      微信id
  
  * MEM_PSN_BIRTHDAY

      生日 yyyy-mm-dd格式

  * MOBILE_TELEPHONE_NO
 
      手机号码

  * MEM_PSN_SEX

      性别，0=女, 1=男

  * MEM_PSN_CNAME

      用户名字

#### 返回：

  {"MEMBER_ID":9114883,"success":true,"error":""}

  * MEMBER_ID: 海澜CRM会员ID

  * success: true/false 操作是否成功

  * error: 如果失败，返回的错误提示

#### CRM数据库说明：
     
调用海澜CRM数据库中定义的过程 PRO_MEMBER_APPORBIND


### 会员卡绑定 {brand}/Member/Bind

#### 参数：

  * openid
  
      微信id
  
  * MOBILE_TELEPHONE_NO
 
      手机号码

  * MEM_OLDCARD_NO

      实体会员卡卡号（俗称老卡卡号）

  * MEM_PSN_CNAME

      用户名字

#### 返回：

  {"MEMBER_ID":9114883,"success":true,"error":""}

  * MEMBER_ID: 海澜CRM会员ID

  * success: true/false 操作是否成功

  * error: 如果失败，返回的错误提示
 

#### CRM数据库说明：
     
     调用海澜CRM数据库中定义的过程 PRO_MEMBER_APPORBIND


### 会员卡解除绑定 {brand}/Member/Unbind

#### 参数：

  * openid

      微信id
  
  * MEMBER_ID

      海澜CRM 用户id，由 MemberApple/MemberBind 返回

#### 返回：

  {"success":true,"error":""}

  * success: true/false 操作是否成功

  * error: 如果失败，返回的错误提示


#### CRM数据库说明：
     
相关数据表： PUB_MEMBER_ID （清空会员的SYS_MEMBER_MIC_ID字段）



### 优惠券活动列表 {brand}/Coupon/Promotions

#### 参数：

  * perPage
  
  		每页多少行记录，默认 20
  
  * pageNum  

		第几页，默认 1
		
  * code [可选]
  	
  		指定的活动代号

#### 返回：

```javascript
    {
        "list":[
            {
                "PROMOTION_CODE":"1207",
                "PROMOTION_NAME":"每满500减200",
                "PROMOTION_DESC":"每满500减200",
                "coupons": [
                	{"QTY":50,"COUNT":5,"USED":0},
                	{"QTY":100,"COUNT":1,"USED":1},
                	{"QTY":200,"COUNT":1,"USED":1}
                ]
            },
            {
                "PROMOTION_CODE":"L2013112709",
                "PROMOTION_NAME":"无限制现金券",
                "PROMOTION_DESC":"无限制现金券",
                "coupons": [
                	{"QTY":50,"COUNT":5,"USED":0},
                	{"QTY":100,"COUNT":1,"USED":1},
                	{"QTY":200,"COUNT":1,"USED":1}
                ]
            },
            ... ...
        ] ,
        "total": 30,
        "perPage": 20,
        "pageNum": 20
    }
```

* QTY 表示优惠券面额
* Class：101 生日券，102 普通抵用券，103 返店券，104 积分券，105 回馈券，201 普通礼品券
* Type：01 现金抵用券，02 礼品券

#### CRM数据库说明：
     
相关数据表： DRP_PROMOTION_THEME （完整返回该数据表中的记录）


### 会员积分 {brand}/Point/{memberId}

#### 参数：

  * memberId

      海澜CRM 用户id，由 MemberApple/MemberBind 返回    

#### 返回：

```javascript
    { "point": 1234 }
```

如果失败：


```javascript
    { "error": "error message text" }
```

#### CRM数据库说明：

相关数据表： PUB_MEMBER_ID、PUB_MEMBER_CARD


### 会员积分明细 {brand}/Point/Log/{memberId}

#### 参数：

  * memberId

      海澜CRM 用户id，由 MemberApple/MemberBind 返回    

  * perPage
  
  		每页多少行记录，默认 20
  
  * pageNum

		第几页，默认 1

	
#### 返回：

```javascript
    {
        "log":[
            {
                "value":"1207",
                "time":"2014-03-06 00:00:00.0",
                "memo":"每满500减200"
	    	},
	    	{
                "value":"1207",
                "time":"2014-03-06 00:00:00.0",
                "memo":"每满500减200"
            },
            ... ...
        ],
        "total": 30,
        "perPage": 20,
        "pageNum": 20
    }
```

#### CRM数据库说明：

相关数据表： PUB_MEMBER_POINT





### 会员所获优惠券的列表 {brand}/Coupon/GetCoupons

#### 参数：

  * status

		优惠券状态 01: 未生效  02: 已生效  03: 已使用  04: 已到期失效
		
		默认 02
		
  * memberId [可选]

      海澜CRM 用户id，由 MemberApple/MemberBind 返回    

  * promotionCode [可选]

      活动代码
      
  * perPage
  
  		每页多少行记录，默认 20
  
  * pageNum

		第几页，默认 1


#### 返回：

```javascript
    {
        "coupons":[
            {
                "BEGIN_DATE":"2014-03-06 00:00:00",
                "END_DATE":"2014-03-06 00:00:00",
                "COUPON_STATUS":"01",				//  01: 未生效  02: 已生效  03: 已使用  04: 已到期失效
				"PROMOTION_CODE":"CQA201401030002",	//  活动代号，和 Promotions 接口的 PROMOTION_CODE 一致
				"COUPON_NO": "AV1403060004"	,	//  优惠券券号
				"row_number": 1					//  序号
			},
            ... ...
        ],
        "total": 30,
        "perPage": 20,
        "pageNum": 20
    }
```

#### CRM数据库说明：

相关数据表： PUB_MEMBER_COUPON、DRP_PROMOTION_COUPON、DRP_PROMOTION_THEME
     
```sql
SELECT 
    DRP_PROMOTION_COUPON.BEGIN_DATE
    , DRP_PROMOTION_COUPON.END_DATE
    , COUPON_STATUS
    , DRP_PROMOTION_THEME.PROMOTION_CODE
    , DRP_PROMOTION_COUPON.COUPON_NO
FROM PUB_MEMBER_COUPON
       left join DRP_PROMOTION_COUPON on (PUB_MEMBER_COUPON.SYS_PCOUPON_ID=DRP_PROMOTION_COUPON.SYS_PCOUPON_ID)
       left join DRP_PROMOTION_THEME on (DRP_PROMOTION_COUPON.SYS_PTHEME_ID=DRP_PROMOTION_THEME.SYS_PTHEME_ID)
WHERE PUB_MEMBER_COUPON.SYS_MEMBER_ID=9088949
```


### 领取优惠券 {brand}/Coupon/FetchCoupon



#### 参数：

  * openid

  * otherPromId

    微信活动识别ID，由 WebLab 这边提供

  * PROMOTION_CODE

    海澜CRM 活动代码，由 Promotions 接口返回

  * qty

    优惠券金额；定额优惠券固定为0，不定额（侃价）传入实际金额

  * point

    积分增减：>0 增加积分; <0 扣减积分; =0 无积分变化

  * memo

    应用 - 活动名称，例如：积分兑换 - 电饭煲；答题抢积分 - 你是哪类型男？


#### 返回：

  {success:true/false,error:"error message",coupon_no:"xxxxx"}

  * coupon_no: 优惠券券号

  * success: true/false 操作是否成功

  * error: 如果失败，返回的错误提示


#### CRM数据库说明：
     
调用海澜CRM数据库中定义的过程 PRO_MEMBER_GET_COUPON



### 门店列表 {brand}/Shops

#### 参数：


  * perPage
  
  		每页多少行记录，默认 20
  
  * pageNum

		第几页，默认 1


#### 返回：

```javascript
  {
  	list:[
  		CODE: ""
  		NAME: "",
  		ADDR: "",
  		TEL: "",
  		CITY: "",
  		PROVINCE: ""
  	],
  	total: 123,
  	perPage: 20,
  	pageNum: 1
  }
```


#### CRM数据库说明：
     
相关数据表 PUB_CUSTOMER_TRANSIT



### 会员消费记录 {brand}/Member/Spending/{memberId}

#### 参数：


  * perPage
  
  		每页多少行记录，默认 20
  
  * pageNum

		第几页，默认 1


#### 返回：

```javascript
  {
  	list:[
		AMT:			<int>					消费金额
		DATE:			<int:UNIX TIME STAMPE>	日期
		POINT:			<int>					获得积分
		SHOP_NAME:		<string>				门店名称
		PRODUCT_NAME:	<string>				商品名称
  	],
  	total: 123,
  	perPage: 20,
  	pageNum: 1
  }
```


#### CRM数据库说明：
     
相关数据表 DRP_RETAIL_HEADER, DRP_RETAIL_DETAIL, PUB_CUSTOMER_TRANSIT, PUB_BARCODE



### 会员卡等级 {brand}/Member/Level/{memberId}

#### 参数：

无

#### 返回：

```javascript
  {
  	level: "01"
  	error: "ooxx"
  }
```

01: 白卡，02:普通VIP卡，03：白金VIP卡


#### CRM数据库说明：
     
相关数据表 PUB_MEMBER_CARD



### 保存会员资料到CRM {brand}/Member/SaveInfo/{memberId}

#### 参数：

* email

	
* industry
		
	行业
	
* province

	省份
	
* city

	城市
	
* addr

	地址
	
* hoppy

	喜欢的款式
	
* color

	喜欢的颜色

#### 返回：

```javascript
  {
  	success: true/false
  	error: "ooxx"
  }
```

> 行业、款式的最终定义及代号


### 从CRM读取会员资料 {brand}/Member/Info/{memberId}

#### 参数：

无

#### 返回：

```javascript
  {
  	email: ""
  	, industry: ""
  	, province: ""
  	, city: ""
  	, addr: ""
  	, hoppy: ""
  	, color: ""
  }
```


### 会员积分增减 {brand}/Point/Change

* memberId

	会员 MEMBER_ID
	
* qty

	积分值（正数表示增加，负数表示扣减）
	
* memo

	应用 - 活动名称，例如：积分兑换 - 电饭煲；答题抢积分 - 你是哪类型男？
	

#### 返回：

```javascript
  {
  	success: true/false
  	error: "ooxx"
  }
```


### 会员增加标签 {brand}/Tag/Add

* memberId

	会员 MEMBER_ID
	
* tag

	潮男


#### 返回：

```javascript
  {
  	success: true/false
  	error: "ooxx"
  }
```


### 会员移除标签 {brand}/Tag/Remove

* memberId

	会员 MEMBER_ID
	
* tag

	潮男


#### 返回：

```javascript
  {
  	success: true/false
  	error: "ooxx"
  }
```


### 会员增加收藏 {brand}/Collection/Add

* memberId

	会员 MEMBER_ID
	
* goodsNo

	商品品号


#### 返回：

```javascript
  {
  	success: true/false
  	error: "ooxx"
  }
```


### 会员移除收藏 {brand}/Collection/Remove

* memberId

	会员 MEMBER_ID
	
* goodsNo

	商品品号


#### 返回：

```javascript
  {
  	success: true/false
  	error: "ooxx"
  }
```
