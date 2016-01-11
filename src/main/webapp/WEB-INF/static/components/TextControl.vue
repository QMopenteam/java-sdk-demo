<template>
<span v-bind:class="classes" @click="_clickHandle">
  <slot></slot>
</span>
</template>
<style scoped>
.label{
  cursor: pointer;
  padding:6px 12px;
}
.label-bordered{
  background-color: #fff;
  color:#777;
  border:1px solid #ccc;
}
.label-bordered.active{
  border-color:#F60;
}
</style>
<script>
export default{
  props:{
    isActive:{
      type:Boolean,
      default:false
    },
    value:{
      type:Number,
      required:true
    },
    clickHandle:{
      type:Function
    }
  },
  data(){
    return {
      classes:['label','label-default','label-bordered'],
      active:false
    }
  },
  watch:{
    isActive(isActive){
      if(isActive){
        this.classes.push("active");
      }else{
        this.classes.splice(3);
      }
    }
  },
  methods:{
    _clickHandle(){
      this.clickHandle(this.value);
    }
  }
}
</script>
