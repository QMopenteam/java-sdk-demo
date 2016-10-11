## Java SDK 话费充值示例

### 简介
本项目主要为了演示如何使用Java SDK调用[千米开放平台][qmopen]接口。Php和.Net SDK的接口调用方式与Java类似，可作参考。项目代码仅做演示用，希望能够帮助开发者快速、顺利地对接千米开放平台。

项目主要有三个功能：
* 授权
* 创建话费充值订单
* 消息服务

其中，授权和消息服务由网站的管理员来使用。

项目采用maven结构，使用Spring MVC框架开发。简单起见，所有业务逻辑代码均放在src/main/java/com/qianmi/open/sdk/web/controller里面。项目所需配置文件放在src/main/resources里面。页面代码放在src/main/java/webapp/WEB-INF/pages里面。

### 事前准备

1. 请先仔细阅读[业务接入][service-access]和[E生活接入指南][elife-service-access]
2. 上级账号登录[控制台][console]创建[商家后台应用][app-houtai]，在`我的应用-应用设置-证书管理`中得到appKey和appSecret。
3. 上级账号直接创建一个直销商账号，或者在上级账号的直销商站点注册一个账号。这个账号用来对应用进行授权。

### 安装项目

1. 安装maven和Tomcat，请自行百度。

2. 下载项目代码到本地，找到src/main/resources/META-INF/cconfig/config.properties文件，并将appKey和appSecret改成自己的。accessToken可以不改。

    appKey=你的appKey<br/>
    appSecret=你的appSecret<br/>
    accessToken=你的accessToken<br/>

3. 在项目根目录下执行`mvn clean install`命令，会在项目根目录的target文件夹下生成`sdk-demo.war`文件。

4. 将Tomcat的`webapps`目录下的ROOT重命名为其他任意名字，然后将步骤3中的`sdk-demo.war`拷贝到webapps下面并`重命名为ROOT.war`，执行Tomcat的bin目录下的`startup.sh`或`startup.bat`启动项目。

5. 访问http://localhost:8080，出现如下页面，项目安装成功。

![话费充值](https://github.com/QMopenteam/java-sdk-demo/raw/master/image/mobile-recharge.png)

### 授权
授权是调用接口前必不可少的一步。几乎所有的接口都需要accessToken参数。授权就是为了获得accessToken参数。开放平台使用OAuth2.0标准协议对用户进行授权，详细说明可参考文档[用户授权介绍][auth-intro]。[登录授权方法说明][auth-detail-intro]详细介绍了如何登录授权获取accessToken以及如何刷新accessToken，使用[accessToken辅助获取工具][auth-tool]可以快速获取accessToken。

下面看一下如何使用SDK获取授权，主要使用SDK提供的`OAuthUtils.getToken(String appKey, String appSecret, String authCode)`获取aaccessToken，使用`OAuthUtils.refreshToken(String appKey, String appSecret, String refreshToken)`刷新accessToken。授权相关代码在`AuthController`中。

首先要获取授权码code。访问`http://localhost:8080/auth`进入如下页面：

![授权](https://github.com/QMopenteam/java-sdk-demo/raw/master/image/auth.png)

点击`授权`按钮，跳转至千米开放平台授权服务器

![授权登录](https://github.com/QMopenteam/java-sdk-demo/raw/master/image/auth-login.png)

E生活用户输入直销商或经销商账号进行登录。账号可以由上级直接提供，或者到上级所提供的直销商站点去注册。
登录成功之后，跳转到授权页面，点击`授权`按钮，页面就会跳转到`redirect_uri`所指定的url，这个url必须是一个外网可以访问的地址。由于这只是一个示例项目，为了页面能够正常跳转，这里redirect_uri指定的是`http://www.qianmi.com`

![授权登录](https://github.com/QMopenteam/java-sdk-demo/raw/master/image/auth-code.png)

在地址栏可以看到code已经跟在了`redirect_uri`后面。将`http://www.qianmi.com`改为`http://localhost:8080`并访问，如果授权成功，就会跳转到如下页面：

![授权成功](https://github.com/QMopenteam/java-sdk-demo/raw/master/image/auth-success.png)

代码如下：
``` java
@RequestMapping("/callback")
public String getToken(String code, Model model) {
    try {
        QianmiContext context = OAuthUtils.getToken(APP_KEY, APP_SECRET, code);
        TokenResponse response = context.getTokenResponse();
        if (response.isSuccess()) {
            updateToken(response);
            startRefreshToken();
        } else {
            model.addAttribute("errMsg", response.getErrorCode() + ": " + response.getMsg());
            return "auth-fail";
        }
    } catch (ApiException e) {
        e.printStackTrace();
    }
    return "auth-success";
}
```
首先这个方法会接收之前url里面的code参数，然后将code传给SDK提供的`OAuthUtils.getToken(APP_KEY, APP_SECRET, code)`方法获取Token。方法参数中的`APP_KEY`和`APP_SECRET`由父类BaseController提供，下面会有说明。通过`response.isSuccess()`判断接口调用是否成功，如果成功，就会跳转到上面的授权成功页面。

在页面跳转之前，还会再做两件事：<br/>
<p>1.更新accessToken</p>

``` java
private void updateToken(TokenResponse response) {
    // 设置refreshToken
    Constants.setProperty("refreshToken", response.getRefreshToken());
    // 更新Token
    accessToken = response.getAccessToken();
}
```
* 保存新获取的refreshToken，用于刷新accessToken。因为accessToken本身是有时效的，有效期在[用户授权介绍][auth-intro]里面有详细说明。
* 将新获取的accessToken设置到BaseController中，后面调用接口时就会使用。这里面的accessToken也是继承自BaseController。BaseController保存了调用接口所需的url、appKey、appSecret和accessToken，还会创建一个OpenClient对象用来调接口。
``` java
public class BaseController {

    private static final String API_URL = "http://localhost:8080/api";

    protected static final String APP_KEY = Constants.getProperty("appKey");
    protected static final String APP_SECRET = Constants.getProperty("appSecret");

    protected static String accessToken = Constants.getProperty("accessToken");
    // 创建一个客户端，默认连接超时时间为3秒，请求超时时间为15秒。
    protected static final OpenClient client = new DefaultOpenClient(API_URL, APP_KEY, APP_SECRET);

}
```
这些参数的初始值均是从src/main/resources/META-INF/config/config.properties中读取。

<p>2.启动定时任务刷新accessToken</p>

``` java
private void startRefreshToken() {
    // 刷新Token任务
    TimerTask refreshTokenTask = new TimerTask() {
        @Override
        public void run() {
            try {
                QianmiContext context = OAuthUtils.refreshToken(APP_KEY, APP_SECRET, Constants.getProperty("refreshToken"));
                updateToken(context.getTokenResponse());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    };
    // 每天7点刷新Token，每隔12个小时刷新一次
    new Timer("refresh-token-timer", true).schedule(refreshTokenTask, getFirstTime(), 12 * 60 * 60 * 1000);
}
```
任务在早上7点和晚上7点定时执行，使用SDK提供的`OAuthUtils.refreshToken(String appKey, String appSecret, String refreshToken))`和之前获取到的`refreshToken`来刷新accessToken，并替换掉旧的accessToken。

### 开启消息服务
消息服务主要是为了实时的获取到数据的变更信息，最常见的订单的充值状态变更。消息服务的详细说明可参考文档[消息服务说明][qmcs-service]和[消息字段说明][qmcs-fiels]。开启消息服务需要调用相关接口。如何调用接口请参考[API调用详解][api-call]。消息接口可参考文档[消息服务API][qmcs-api]。

在开启消息服务之前，必须先由上级账号登录[控制台][console]为应用订阅消息，具体步骤参考[消息服务说明][qmcs-service]的`订阅千米消息服务`。做完这步，就可以为用户开启消息服务了。

在上面的授权成功页面，点击`开启消息服务`或者直接访问`http://localhost:8080/qmcs/start`。开启消息服务的代码在`QmcsController`中。
``` java
@RequestMapping("/qmcs/start")
public String startQmcs(Model model) throws ApiException {
    QmcsUserPermitRequest request = new QmcsUserPermitRequest();
    QmcsUserPermitResponse response = client.execute(request, accessToken);
    if (response.isSuccess()) {
        // 启动定时任务，定时消费消息
        startConsume();
        return "qmcs-success";
    } else {
        model.addAttribute("errMsg", response.getErrorCode() + ": " + response.getMsg());
        return "qmcs-fail";
    }
}
```
方法的前两行，通过使用SDK调用`qianmi.qmcs.user.permit`接口，为用户开启消息服务。开启成功之后，就可以开始消费消息了。<br/>
消费消息的代码在`startConsume()`方法中：
``` java
private void startConsume() {
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                // 调用消费消息接口，指定消费数量为100
                QmcsMessagesConsumeRequest consumeRequest = new QmcsMessagesConsumeRequest();
                consumeRequest.setQuantity(100);
                QmcsMessagesConsumeResponse consumeResponse = client.execute(consumeRequest, accessToken);
                if (CollectionUtils.isEmpty(consumeResponse.getQmcsMessages())) {
                    return;
                }
                final List<QmcsMessage> qmcsMessages = consumeResponse.getQmcsMessages();
                // 启动线程处理消息
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        handleMessages(qmcsMessages);
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    };
    // 启动定时任务，每10秒钟消费一次
    new Timer("qmcs-timer", true).schedule(task, 10 * 1000, 10 * 1000);
}
```
定时任务每隔10秒钟调用一次`	qianmi.qmcs.messages.consume`接口获取用户的消息，然后将获取到的消息交给一个线程去处理。
消息处理的代码如下：
``` java
/**
 * 处理消息
 * @param qmcsMessages
 */
private void handleMessages(List<QmcsMessage> qmcsMessages) {
    // 处理消息，并将处理完成的消息id加入到handledMessageIds
    StringBuilder handledMessageIds = new StringBuilder();
    boolean handleComplete;
    for (QmcsMessage message : qmcsMessages) {
        handleComplete = false;
        // 处理订单充值消息
        if ("qianmi_elife_rechargeStateChange".equals(message.getTopic())) {
            // 处理消息内容
            if (handleOrder(message.getContent())) {
                handleComplete = true;
            }
        }
        // 处理票务消息
        else if ("qianmi_elife_ticketStateChange".equals(message.getTopic())) {
            // TODO
        }
        // 只加入已处理完成的消息id
        if (handleComplete) {
            handledMessageIds.append(message.getId()).append(",");
        }
    }
    // 对已处理完的消息进行消费确认
    try {
        QmcsMessagesConfirmRequest confirmRequest = new QmcsMessagesConfirmRequest();
        confirmRequest.setsMessageIds(handledMessageIds.deleteCharAt(handledMessageIds.length() - 1).toString());
        client.execute(confirmRequest, accessToken);
    } catch (ApiException e) {
        e.printStackTrace();
    }
}

private boolean handleOrder(String content) {
    // TODO 处理消息内容，更新本地订单充值状态，根据充值状态做相应处理
    return true;
}
```
处理流程如下：
* 对获取到的消息进行遍历
* 根据消息的topic分别交给不同的业务方法去处理
* 将处理成功的消息id收集起来
* 调用`qianmi.qmcs.messages.confirm`对处理成功的消息进行确认

**注1：** 确认过的消息通过`qianmi.qmcs.messages.consume`不会再获取到。未确认的消息10分钟之后可再次获取。

**注2：** 一个用户只需调`qianmi.qmcs.user.permit`接口一      次。只有当用户调用了`qianmi.qmcs.user.cancel`接口之后，需要重新开启消息服务的时候，才需要再次调用`qianmi.qmcs.user.permit`。重复调用`qianmi.qmcs.user.permit`不会有副作用。


### 创建话费充值订单
准备工作已经做完，接下来就可以调用[手机话费API][mobil-api]接口创建充值订单了。相关代码在recharge/MobileController中。

访问`http://localhost:8080`进入充值页面：

![话费充值](https://github.com/QMopenteam/java-sdk-demo/raw/master/image/mobile-recharge.png)

输入手机号码和充值金额，点击提交订单，就会调用相关接口创建订单：
``` java
@Controller
@RequestMapping("/mobile")
public class MobileController extends BaseController {

    /**
     * 下单分为两步：
     * 1. 查询可用商品，调用qianmi.elife.recharge.mobile.getItemInfo接口
     * 2. 下单，调用qianmi.elife.recharge.mobile.createBill接口
     * @throws ApiException
     */
    @RequestMapping(value = "/createBill")
    public Object mobileCreateBill(String mobileNo, String rechargeAmount, Model model) throws ApiException {
        String itemId = "";
        // 获取商品编号
        RechargeMobileGetItemInfoRequest req = new RechargeMobileGetItemInfoRequest();
        req.setMobileNo(mobileNo);
        req.setRechargeAmount(rechargeAmount);
        RechargeMobileGetItemInfoResponse response = client.execute(req, accessToken);
        if (!response.isSuccess()) {
            handleError(response);
            return "create-bill-fail";
        }
        // 下单
        RechargeMobileCreateBillRequest createBillRequest = new RechargeMobileCreateBillRequest();
        createBillRequest.setItemId(response.getMobileItemInfo().getItemId());
        createBillRequest.setMobileNo(mobileNo);
        createBillRequest.setRechargeAmount(rechargeAmount);
        RechargeMobileCreateBillResponse createBillResponse = client.execute(createBillRequest, accessToken);
        if (!createBillResponse.isSuccess()) {
            // 处理错误信息
            handleError(response);
            return "create-bill-fail";
        }
        // 将订单信息保存到本地
        saveOrderToDb(createBillResponse.getOrderDetailInfo());

        model.addAttribute("data", response.getMobileItemInfo());
        model.addAttribute("billId", createBillResponse.getOrderDetailInfo().getBillId());
        return "order-confirm";
    }

    private void handleError(QianmiResponse response) {
        System.out.println(response.getSubCode() + ":" + response.getSubMsg());
        // handle the error
    }

    private void saveOrderToDb(OrderDetailInfo orderDetailInfo) {
        // save bill to database
    }

}
```
首先调用`qianmi.elife.recharge.mobile.getItemInfo	`查询可用商品，接下来调用`	qianmi.elife.recharge.mobile.createBill`来创建订单。
订单创建成功后，会返回订单信息，可将订单信息保存到本地数据库中。

订单创建成功后，会跳转到如下页面。

![充值](https://github.com/QMopenteam/java-sdk-demo/raw/master/image/order-confirm.png)

充值功能待开发。

### 异常处理
在调用接口过程中，不可避免地会遇到调用出错的情况。SDK会将错误信息解析并放入response中，用户只需要根据response中的相关字段，再结合API的异常码，就可以快速定位问题。
* 调用response.isSuccess()方法判断接口调用是否成功
* 如果调用失败，调用response.getErrorCode()获取异常码。[开发异常说明][exception-intro]列出了所有的系统级错误。
* 如果异常码为9，说明是业务逻辑异常。这时还需要调用`response.getSubCode()`和`response.getSubMsg()`的获取具体的业务逻辑错误。每个API里面都有异常说明及解决方案，也可通过[错误码自查工具][exp-tool]查看解决方案。







[qmopen]:http://open.qianmi.com
[auth-intro]:http://open.qianmi.com/doc/detail?redirect=%2Fdoc%2Fdocument%2Fdev%2Fauth
[auth-detail-intro]:http://open.qianmi.com/doc/detail?redirect=%2Fdoc%2Fdocument%2Fdev%2Fauth-detail
[auth-tool]:http://open.qianmi.com/qmapi/authTools
[qmcs-service]:http://open.qianmi.com/doc/detail?redirect=%2Fdoc%2Fdocument%2Fdev%2Fqmcs
[qmcs-fiels]:http://open.qianmi.com/doc/detail?redirect=%2Fdoc%2Fdocument%2Fdev%2Fqmcs-topic-field
[console]:http://open.qianmi.com/login?redirect_uri=/console
[api-call]:http://open.qianmi.com/doc/detail?redirect=%2Fdoc%2Fdocument%2Fdev%2Fapi
[qmcs-api]:http://open.qianmi.com/qmapi/apiList?gid=G1100001865&sysId=1
[mobil-api]:http://open.qianmi.com/qmapi/apiList?gid=G1100000910&sysId=1
[exception-intro]:http://open.qianmi.com/doc/detail?redirect=%2Fdoc%2Fdocument%2Fdev%2Ferr-desc
[exp-tool]: http://open.qianmi.com/qmapi/expTools
[app-houtai]:http://open.qianmi.com/doc/detail?redirect=%2Fdoc%2Fbusiness%2Fapp-houtai
[service-access]:http://open.qianmi.com/doc/detail?redirect=%2Fdoc%2Fbusiness%2Fjoin-companion
[elife-service-access]:http://open.qianmi.com/doc/detail?redirect=%2Fdoc%2Fbusiness%2Felife-join
