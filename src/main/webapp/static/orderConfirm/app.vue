<style>
.container{
  margin:20px auto;
}
.label-info{
  color:#fff;
}
</style>
<template>
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
          手机号码：
        </td>
        <td>
          {{mobile}}
        </td>
      </tr>
      <tr>
        <td>
          充值面额：
        </td>
        <td>
          {{recharge}}
        </td>
      </tr>
      <tr>
        <td>
          商品信息：
        </td>
        <td>
          {{itemInfo}}
        </td>
      </tr>
      <tr>
        <td>
          运营商：
        </td>
        <td>
          {{operator}}
        </td>
      </tr>
    </tbody>
  </table>
  <div class="row">
    <div class="col-md-6">
      <button class="btn btn-primary" @click="orderConfirm">确认订单</button>
    </div>
  </div>
</div>
</template>
<script>
import store from './store.js';
export default{
  data(){
    return {
      itemInfo:store.getItemInfo(),
      operator:store.getOperator(),
      mobile:store.getMobile(),
      recharge:store.getRecharge()
    }
  },
  methods:{
    orderConfirm(){
      alert("下单成功！");
    }
  }
}

</script>
