<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>消息服务</title>
    <link rel="stylesheet" type="text/css" href="//cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/static/css/base.css"/>
</head>
<body>
    <div class="alert alert-danger margin-20" role="alert">
        <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
        消息服务开启失败，${errMsg}
    </div>
    <form class="margin-20" action="/qmcs/start">
        <button type="submit" class="btn btn-primary">重试</button>
    </form>
</body>
</html>
