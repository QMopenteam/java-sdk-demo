<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="author" content="QMopenteam">
  <meta name="keywords" content="sdk,demo,手机快充" >
  <meta name="description" content="">
  <title>千米开放平台－手机话费充值</title>
  <link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css">
  <link rel="stylesheet" href="/static/css/base.css">
  <script type="text/javascript" src="/static/js/jquery-1.9.1.min.js"></script>
</head>
<body>
<div class="container">
  <form method="post" id="form" class="form form-horizontal" action="/mobile/createBill" onsubmit="return check()">
    <div class="form-group">
      <label for="mobileNo" class="col-sm-2 control-label">手机号码：</label>
      <div class="col-sm-10">
        <div>
          <input id="mobileNo" name="mobileNo" type="text" placeholder='请输入手机号码' maxlength="11" class="form-control" oninput="highlightMobileNo($(this).val())"/>
          <span class="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
        </div>
        <div>
          <h4 class='orange' id="alias"></h4>
        </div>
      </div>
    </div>
    <div class="form-group">
      <label class="col-sm-2 control-label"> 输入面值：</label>
      <div class="col-sm-10" id="rechargeAmountDiv">
        <span class="label label-unselect" onclick="chooseRechargeAmount(this)">30</span>
        <span class="label label-select" onclick="chooseRechargeAmount(this)">50</span>
        <span class="label label-unselect margin-right-20" onclick="chooseRechargeAmount(this)">100</span>
        其他面值：<input type="number" id="customAmount" name="customAmount" placeholder="200" onclick="customRechargeAmount()"/>
        <input type="hidden" name="rechargeAmount" id="rechargeAmount" value="50"/>
      </div>
    </div>
    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-10">
        <button type="submit" class="btn btn-info">提交订单</button>
      </div>
    </div>
  </form>
</div>
<script type="text/javascript">
  function highlightMobileNo(mobileNo) {
    mobileNo = mobileNo.replace(/^(\d{3})(\d{4})?/,'$1 $2 ');
    $('#alias').html(mobileNo);
    if (validateNo(mobileNo)) {
      $('#form').removeClass('has-error');
      $('#form').addClass('has-success');
    } else {
      $('#form').removeClass('has-success');
      $('#form').addClass('has-error');
    }
  }

  function validateNo(mobileNo){
    return /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/.test(mobileNo.replace(/\s/g,''));
  }

  function validateAmount(rechargeAmount){
    return /^[1-9][0-9]*$/.test(rechargeAmount);
  }

  function chooseRechargeAmount(e) {
    $('#rechargeAmountDiv').find('.label-select').removeClass('label-select').addClass('label-unselect');
    $(e).removeClass('label-unselect');
    $(e).addClass('label-select');
    $('customAmount').val('');
    $('#rechargeAmount').val($(e).html());
  }

  function customRechargeAmount() {
    $('#rechargeAmountDiv').find('.label').removeClass('label-select').addClass('label-unselect');
  }

  function check() {
    if (!$('#form').hasClass('has-success') && !validateNo($('#mobileNo').val().replace(/\s/g,''))) {
      $('#mobileNo').focus();
      return false;
    }
    if ($('#rechargeAmountDiv').find('.label-select').length != 0) {
      return true;
    }
    var customAmount = $('#customAmount').val().replace(/\s/g,'');
    if (validateAmount(customAmount)) {
      $('#rechargeAmount').val(customAmount);
      return true;
    }
    $('#customAmount').val('');
    $('#customAmount').focus();
    return false;
  }
</script>
</body>
</html>
