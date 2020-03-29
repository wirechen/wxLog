## 目录
[1. 背景](#背景)  
[2. 日志历史梳理](#日志历史梳理)  
[3. 接入教程](#接入教程)  

## 背景
PM：这期的活动项目当用户参与人数达到250人的时候麻烦通知我一下，我要看下效果决定是否群发推送或者下掉活动。
我：为啥非得是250人？
PM：图个吉利。
我：额，一定要刚好250吗？
PM：是的，拜托了开发爸爸。

既然产品都叫爸爸了，咱们也不好拒绝啊不是？但是咱也不能上线后一直盯着生产数据看啊，em....这是个问题....


好吧，以上都是我YY的，言归正传。我们平时在业务开发中，除了上面的应用场景还有很多，如：对接三方接口服务出错、阈值触发、异常请求等业务相关需要报警的场景有很多。干脆索性就想搞一个能在生活中**任何时候实时主动通知**我的玩意儿，短信？电话？但要成本啊，就想到我们每天都不离手的微信。然后通知的方式既不想对代码有太大的侵入性，又想实现起来要优雅，于是想到了日志框架打warn日志。于是，wechat + log4j = wxLog 诞生了！

## 日志历史梳理
说实话，开发wxLog之前我对SLF4j、log4j、log4j-core、log4j-api、logback、Java Util Logging、commons-logging等等各种日志框架之间的关系是懵逼的，相信也有部分开发同学还是不太清楚，请允许我装个B，在这里简单梳理一下。如果对这日志发展史比较熟悉的同学，可以直接看[接入教程](#接入教程)。

> **以Log4j为发展主线，简单划分Log4j为四个时代：**

### Log4j盛行时代一一Log4j一枝独秀
- 时代背景：
上世纪末至本世纪初，Log4j几乎成为Java社区的日志标准，也让Log4j轻松成为了Apache的一员。Apache曾向Sun公司建议将Log4j引入到Java标准库中，但被Sun拒绝了，结果Sun第二年反手在Java1.4发布中推出了自己的日志库**JUL**（Java Util Logging），更恶心的是JUL几乎就是模仿Log4j的代码来实现的。但此时的Log4j已经在Java日志界站稳了脚跟，JUL推出后并没有受到大众的青睐，哪怕你是Java亲生的。

*在这个时代，如果你想使用Log4j只需要简单引入单独的Log4j的jar包即可：*
![image.png](http://wirechen-image.test.upcdn.net/blog/e32d53f4b1fa10315590576dc00660f1.png)

### Log4j中期时代一一JCL作为日志门面
- 时代背景：
看着Log4j和JUL激烈角逐，两派日志框架水火不容，有你没我。于是Apache推出了Jakarta Commons Logging，也就是我们经常看到的**commons-logging**，简称**JCL**。JCL可不是日志框架，它主要针对Log4j和JUL制定了一套日志接口，支持运行时动态绑定其他的日志框架。也就是说，开发者只需调用commons-logging的接口，底层的实现可以是Log4j，也可以是Java Util Logging。这种模式叫门面设计模式，所以JCL又被称为**日志门面**。
终于Log4j和JUL两派终于不用再撕逼了，他们在JCL的帮助下成为了好兄弟。虽然Sun干了些龌龊事，但Apache是真心为Java社区着想啊。

*在这个时代，由于commons-logging的加入，所以还得引入该日志门面的jar包：*
![image](http://wirechen-image.test.upcdn.net/blog/8aec51e2c81c90bfa725ebc2f7aa2366.png)

### Log4j衰落时代一一SLF4j作为日志门面
- 时代背景
Log4j的衰落得从Ceki Gülcü大佬离开Apache开始说起。。。。。
这位大佬就是Log4j的作者，离开了Apache出来单干，先后创建了**SLF4j**（日志门面，对标Commons-logging）和**Logback**（日志框架，对标Log4j），SFL4j在编译时静态绑定日志框架避免了JCL动态绑定带来的一系列问题，SLF4j+Logback在设计和性能上都优于Commons-logging+Log4j。并且，大佬还写了当时市面上所有主流日志框架的SLF4j桥接包，让主流日志框架都能适配SLF4j作为日志门面，Log4j也不例外。除了Logback，其他的日志实现都不是亲生的，都需要对应的桥接包，只有Logback才是SLF4j的完美实现。大家纷纷投靠大佬，投靠SLF4j+Logback，Log4j被分走了半壁江山。

*在这个时代，大势所趋之下Log4j不得不抛弃它亲生的commons-logging日志门面，去投靠SLF4j，但必须引入桥接包去适配SLF4j：*
![image.png](http://wirechen-image.test.upcdn.net/blog/1ee31b18b68e76d1f466c81605ede42e.png)

### Log4j最后的反击一一log4j-api作为日志门面
- 时代背景
眼看着SLF4j不断壮大，Apache坐不住了，承认JCL+Log4j在设计和性能上确实比不上SLF4j+Logback，但我们绝不认输。于是Apache推出了**Log4j2**。。。
**log4j-api**是Log4j2的日志门面，**log4j-core**是Log4j2的日志实现，同样Log4j2也像SLF4j一样为市面上主流的日志框架提供了适配log4j-api的桥接包，试图对SFL4j发起最后的攻击。但最终Log4j2的反击还是以失败告终，就连javaweb开发界的扛把子SpringBoot也拥抱了SLF4j+Logback。

*Log4j2虽然和Log4j是同门师兄弟，但完全合不来，依赖也需要桥接包。但log4j-api更恶心的是：你想要适配我，必须要通过我的日志实现log4j-core。哪怕是师弟Log4j也不例外，当然桥接包也不能少。*
![image.png](http://wirechen-image.test.upcdn.net/blog/2067702ec82d74a2236c2874c3864eb5.png)

### 总结
上面以Log4j为线索简单讲解了下Log4j的发展历程，为了可以看起来不那么枯燥，写得略微诙谐幽默，增强可读性和记忆性，但比较肤浅也遗漏了许多，比如日志门面之间的桥接等。Java日志框架的发展史非常繁杂，如果感兴趣的同学可以参考下面的两篇博客：[礼炮1空间站](https://www.cnblogs.com/hanszhao/p/9754419.html)、[imango](https://segmentfault.com/a/1190000021121882?utm_source=tag-newest)。

最后，
* 该工具**目前只支持以Log4j作为日志实现**的Java项目。
> 其他同学：卧槽？？？你咋不早说！
* 同学请冷静，实在抱歉，但是wxLog肯定不只是为了一个过时的Log4j而生。
> 其他同学：继续，看着你装X。
* wxLog2.0版现在已在运粮中，考虑到SLF4j的绝对地位，目前暂定下期采用SLF4j+AOP的方式以支持所有以SLF4j作为日志门面的项目。2.0开发分支已建，欢迎提pr，SLF4j的源码分析可以参考这篇博客：[chlsmile](https://www.cnblogs.com/chenhongliang/p/5312517.html)。
欢迎有兴趣的同学一起加入，相互探讨、相互学习！

## 接入教程

### 1. 引入wxLog依赖
目前已上传至maven中央仓库，但阿里云镜像还未同步过去，我也不知道为啥，知道的同学可以告诉我一下。如果不能下载jar包请切换源为中央仓库。
```xml
<dependency>
   <groupId>com.github.wirechen</groupId>
   <artifactId>wxLog</artifactId>
   <version>1.0.RELEASE</version>
</dependency>
```
> ps：如果自己项目中的Log4j和wxLog里面的Log4j版本不同，可以exclusion掉，httpclient同理。

### 2. 获取openid
关注微信公众号【wirechen】，点击【微信报警】在子栏目选择【openid】即可获取。

<img src="http://wirechen-image.test.upcdn.net/blog/44be3ba38b70f13e9cbeac771ede0fd0.jpg" width ="300" height ="300" align=center />

<img src="http://wirechen-image.test.upcdn.net/blog/3bfb9710e98bac63956b966b057151fd.png" width ="300" height ="220" align=center />

<img src="http://wirechen-image.test.upcdn.net/blog/240b89099a9da837f8638ee3973525a8.png" width ="300" height ="180" align=center />


### 3. 添加配置
在日志配置文件添加WxAppender并引入到Root
```xml
<appender name="WxAppender" class="com.github.wirechen.WxAppender">
    <!-- 必填项：openIds添加参考步骤2，多个可以用英文逗号分割 -->
    <param name="openIds" value="xxx"/>
    <!-- 非必填项 -->
    <filter class="com.github.wirechen.WxFilter">
	<!-- 过滤包名：只有该包下的日志才能触发报警 -->
	<param name="packageToMatch" value="com.xxx"/>
	<!-- 过滤日志开头：以特定内容开头的日志才能触发报警 -->
	<param name="stringToMatch" value="【微信报警】"/>
	<!-- 过滤日志等级：有levelMin和levelMax，可自行调节，默认只有WARN级别日志才会触发报警 -->
        <param name="levelMax" value="WARN"/>
    </filter>
</appender>
```
```xml
<root>
    <level value="INFO"/>
    <appender-ref ref="stdout"/>
    <appender-ref ref="FILE"/>
    <!-- 把WxAppender加进来 -->
    <appender-ref ref="WxAppender"/>
</root>
```

### 4. 快速体验

1. 克隆项目到自己本地
`git clone https://github.com/wirechen/wxLog.git`

2. 修改`log4j.xml`配置文件，在`openIds`一栏输入添加自己的openid

<img src="http://wirechen-image.test.upcdn.net/blog/29840a48328f37b63009c606ba830802.png" width ="400" height ="120" align=center />

3. 运行`Test`中的main方法

<img src="http://wirechen-image.test.upcdn.net/blog/ae1178087d66507370f7122f22f85d6c.png" width ="370" height ="200" align=center />

4. 成功接收到微信报警

<img src="http://wirechen-image.test.upcdn.net/blog/8c8d6c4d4579b3c4910342558d9493dc.png" width ="370" height ="280" align=center />

### 5. 项目实战分享

- 其实wxLog这工具我已经运用到我们公司的项目有一年多时间了吧，运行一直也都很稳定。我们公司的日志架构是SLF4j+Log4j，当初就研究Log4j源码，所以wxLog1.0也只支持Log4j。
- 开发wxLog初衷是当初在开发公司的SEM系统对接各大搜索引擎平台时，每天都需要定时与各渠道同步数据，但经常因对方的服务问题或者网络问题导致同步失败（特别是360），可能导致自家系统的数据不准确，影响算法结果，还浪费宝贵的API限额。所以做了个业务报警，触发报警后进行人工干预，暂停或休息该渠道的数据同步。
- 当然还有很多需要报警的业务场景的地方也都用上了。比如：对接公司大数据组每周提供的数据时先做个数据总量的基本校验，判断当新老数据的数据总量相差大于某个阈值时打个log.warn日志，报警就推送到手机微信啦，收到报警后及时跟大数据组反馈一下让对方排查。还有类似[**背景**](#背景)中的玩笑故事等任何业务场景有报警需要都可以用上wxLog。
- 最后说明一点：我的服务器配置也是比较低的，如果后期用的人多了我可能会考虑在服务端做限流。如果大家要运用到生产，建议最好wxLog的服务端用自己的（看源码里面的提供个HTTP接口就可以了，也不一定非要用微信报警的形式）。
