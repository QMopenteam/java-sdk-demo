<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="author" content="QMopenteam">
  <meta name="keywords" content="sdk,demo,手机快充" >
  <meta name="description" content="">
  <title>千米开放平台－订单确认</title>
  <link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css">
  <link rel="stylesheet" href="/static/css/base.css">
</head>
<body>
<form>
  <div class="container">
    <table class="table">
      <thead>
      <tr>
        <th colspan="2" align="center" class="label-info">
          订单确认
        </th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>
          订单编号：
        </td>
        <td>
          ${billId}
        </td>
      </tr>
      <tr>
        <td>
          手机号码：
        </td>
        <td>
          ${data.mobileNo}
        </td>
      </tr>
      <tr>
        <td>
          充值面额：
        </td>
        <td>
          ${data.rechargeAmount}元
        </td>
      </tr>
      <tr>
        <td>
          商品信息：
        </td>
        <td>
          ${data.itemId}-${data.itemName}
        </td>
      </tr>
      <tr>
        <td>
          运营商：
        </td>
        <td>
          ${data.province}&nbsp;${data.city}&nbsp;${data.operator}
        </td>
      </tr>
      </tbody>
    </table>
    <div class="row">
      <div class="col-md-6">
        <button class="btn btn-primary">去充值</button>
      </div>
    </div>
  </div>
  </form>
</body>
</html>
