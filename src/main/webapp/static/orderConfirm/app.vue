<style>
.container{
  margin:20px auto;
}
</style>
<template>
<div class="container">
  <table class="table">
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
          订单编号：
        </td>
        <td>
          {{orderNo}}
        </td>
      </tr>
    </tbody>
  </table>
</div>
</template>
<script>
import store from './store.js';
export default{
  data(){
    return {
      mobile:store.getMobile(),
      recharge:store.getRecharge(),
      orderNo:store.getOrderNo()
    }
  }
}

</script>
