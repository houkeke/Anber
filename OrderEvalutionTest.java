package com.bj58.qa.rs.inteface.modeltest;

/**
 * 服务评价打分接口
 * @author peiwei
 * 2017.7.14
 * 1.判断接口返回条数与数据库是否一致
 * 2.查看数据是否插入成功
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
import com.bj58.qa.rs.inteface.dataprovider.OrderEvalutionDataProvider;

public class OrderEvalutionTest extends BaseTest{
  
private String url="http://rsm.58.com/order/evalution";
	
	private DBUtil db ;
	
	@BeforeClass
	public void beforeClass(){
		db = new DBUtil("M");//初始化数据库
		db.connection();//连接数据库s
	}
  @Test(dataProviderClass=OrderEvalutionDataProvider.class,dataProvider="settlementPayDataProvider")
  public void orderEvalutionTest(String num,String msg,String orderId, String score,String remark) {
	  Map<String, String> params =  new HashMap<String, String>();
		params.put("NVrgLpVluBFisSw", "2016051714120056878e24");
		params.put("orderId", orderId);
		params.put("score", score);
		params.put("remark", remark);
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		
		JSONObject responseObj=HttpRequest.doPostReturnResponseJson(url, params);
		System.out.println(responseObj);
		String status = responseObj.getString("status");
		Assert.assertEquals(status,"1", "接口返回状态为0，评论失败");
  }
  
  @AfterClass
	public void afterClass(){
		if(db!=null){//断开数据库连接
			db.closeConnection();
		}
	}
}
