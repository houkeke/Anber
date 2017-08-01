package com.bj58.qa.rs.inteface.modeltest;

/**
 * CPA派单接口
 * @author peiwei
 * 2017.7.14
 * 1.判断接口返回状态
 * 
 */

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.dataprovider.OrderAssignDataProvider;

public class OrderAssignTest extends BaseTest{
private String url="http://rsm.58.com/order/assign";
	
	private DBUtil db ;
	
	@BeforeClass
	public void beforeClass(){
		db = new DBUtil("M");//初始化数据库
		db.connection();//连接数据库s
	}
  @Test(dataProviderClass=OrderAssignDataProvider.class,dataProvider="orderAssignDataProvider")
  public void orderAssignTest(String num,String msg,String demandId ,String businessUserId) {
	  Map<String, String> params =  new HashMap<String, String>();
		params.put("NVrgLpVluBFisSw", "2016051714120056878e24");
		
		params.put("businessUserId", businessUserId);
		params.put("demandId", demandId);
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		
		JSONObject responseObj=HttpRequest.doPostReturnResponseJson(url, params);
		System.out.println(responseObj);
		
		//cps、cpa人工派单
		if(num =="1"){
//		String result = responseObj.getString("result");
//		Assert.assertEquals(result,"抢单成功", "订单已抢完");
//		
//		String code1 = responseObj.getString("code");
//		Assert.assertEquals(code1,"2", "订单已抢完");
		
		String code2 = responseObj.getString("code");
		Assert.assertEquals(code2,"101", "订单已抢完");}else{
			//cpa人工派单，账户没有余额
			String result = responseObj.getString("result");
			Assert.assertEquals(result,"支付失败 余额不足，请充值!", "账户余额不足");
		}
  }
  
  @AfterClass
	public void afterClass(){
		if(db!=null){//断开数据库连接
			db.closeConnection();
		}
	}
}
