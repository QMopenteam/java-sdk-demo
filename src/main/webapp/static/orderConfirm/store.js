
var orderData = {};
let count = 0;
function getDeepData(data){
  for(let i in data){
    for(let j in data[i]){
      return data[i][j];

    }
  }
};
if(response.data && !response.data['error_response']){
  orderData = getDeepData(response.data);
}else{
  orderData.error = response.data['error_response']['sub_msg'];
}

export default {
  getOperator(){
    return orderData.province+"-"+orderData.city+"-"+orderData.operator;
  },
  getMobile(){
    return orderData.mobileNo;
  },
  getRecharge(){
    return orderData.rechargeAmount;
  },
  getItemInfo(){
    return orderData.itemId+"-"+orderData.itemName;
  },
  getCity(){
    return orderData.province+"-"+orderData.city;
  },
  getReverseFlag(){
    return orderData.reverseFlag;
  }
}
