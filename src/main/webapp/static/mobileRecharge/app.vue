<template>
<div class="container">
  <form v-bind:class='formClasses' method="post" action="/mobile/itemInfo">
    <div class="form-group">
      <label for="mobile" class="col-sm-2 control-label">手机号码：</label>
       <div class="col-sm-10">
         <div>
            <input v-mobile-input='mobileNo' v-model='mobileNo' id="mobileNo" name="mobileNo" type="text" placeholder='请输入手机号码' class="form-control" />
            <span class="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-remove form-control-feedback" aria-hidden="true"></span>
         </div>
        <div>
            <h4 class='orange'>{{aliasNumber}}</h4>
        </div>
       </div>
    </div>
    <div class="form-group">
      <label class="col-sm-2 control-label"> 输入面值：</label>
      <div class="col-sm-10">
        <input type="number" v-model="rechargeAmount"  name="rechargeAmount" class="form-control" placeholder="10，20，30"/>
          <!--<div>
              <ul class="list-inline">
                <li v-for='val in charges'>
                  <text-control
                    v-bind:click-handle="selectCharge"
                    v-bind:value="val"
                    v-bind:is-active="val === charge">
                    {{val}}</text-control>
                </li>

              </ul>

          </div>-->
      </div>
    </div>
    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-10">
        <button type="submit" class="btn btn-info">提交</button>
      </div>
    </div>
  </form>
</div>

</template>
<style scoped>
.container{
  margin:20px auto;
}
.form{
    width:600px;
}
.form .glyphicon{
  right:20px;
  display:none;
}
.form.has-error .glyphicon-remove,.form.has-success .glyphicon-ok{
  display:block;
}
.orange{
  color:orange;
}
</style>
<script>
import Vue from 'vue';
import TextControl from '../components/TextControl.vue';
Vue.directive('mobile-input',{
  twoWay:true,
  bind(){
    this.el.setAttribute('maxLength',11);
    this.el.setAttribute('autocomplete',false);
  },
  update(number){
    if(number){
        if(/\D$/.test(number)){
          this.vm.reset(number.replace(/\D$/,''));
          return;
        }
        if(number.length === 11){
          this.vm.send();
        }else{
          this.vm.removeFormStatus();
        }
        number = number.replace(/^(\d{3})(\d{4})?/,'$1 $2 ');
        this.vm.sync(number || '');
    }else{
      this.vm.removeFormStatus();
      this.vm.sync('');
    }

  },
  unbind(){

  }
})
export default{
  data(){
    return {
      rechargeAmount:10,
      //charges:[100,200,500],
      mobileNo:'',
      aliasNumber:'',
      formClasses:['form','form-horizontal']
    }
  },
  computed:{
    validateNo(){
      return /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/.test(this.mobileNo.replace(/\s/g,''));
    },
    validateAmount(){
        return /^[0-9]*$/.test(this.rechargeAmount);
    }

  },
  methods:{
    selectCharge(value){
      this.rechargeAmount = value;
    },
    reset(number){
      this.$nextTick(()=>{
        this.mobileNo = number;
      })

    },
    sync(number){
      this.aliasNumber = number;
    },
    removeFormStatus(){
      if(this.formClasses.indexOf('has-error') > -1 || this.formClasses.indexOf('has-success') > -1){
        this.formClasses.splice(2);
      }
    },
    send(number){

      if(this.validateNo){
       this.formClasses.push("has-success");
        //查询手机支持面值列表
       /*  this.$http.get('/someUrl').then((response)=>{
          // set data on vm
          this.$set('charges', response.data)
        },()=>{
          this.formClasses.push("has-error");
        });*/
      }else{
        this.formClasses.push("has-error");
      }
    }

  },
  components:{
    TextControl
  }
}
</script>
