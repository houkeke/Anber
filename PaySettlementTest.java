package com.bj58.qa.rs.inteface.modeltest;

/**
 * 打款接口
 * @author peiwei
 * 2017.7.17
 * 1.判断接口返回数据是否异常
 * 2.判断插入结算log表是否成功
 * 
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.bj58.qa.rs.inteface.dataprovider.PaySettlementDataProvider;

public class PaySettlementTest extends BaseTest{
  
	private String url = "http://rsm.58.com/settlement/pay";
	private Map<String,String> header;
	private DBUtil db;
	
	@BeforeClass
	public void beforeClass(){
		db = new DBUtil("M");
		db.connection();
	}
	
	@Test(dataProviderClass=PaySettlementDataProvider.class,dataProvider="paySettlementDataProvider")
	public void paySettlementTest(String num,String msg,String settlementid,String name) throws SQLException {
		Map<String, String> params =  new HashMap<String, String>();
		params.put("NVrgLpVluBFisSw", "2016051714120056878e24");
		params.put("settlementid", settlementid);
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		
		JSONObject responseObj=HttpRequest.doPostReturnResponseJson(url, params);
		System.out.println(responseObj);
		responseObj.remove("header");
		responseObj.remove("cookies");
		
		if(num == "1"){
			String message = responseObj.getString("message");
			String state = responseObj.getString("state");
			Assert.assertEquals(message,"结算失败！FAIL  金额不足", "结算金额不足1元");
			Assert.assertEquals(state,"0", "结算金额不足1元");
			PreparedStatement db_state = db.prepareStatement("select * from  t_rs_settlement_log where settlement_id=" + settlementid );
			ResultSet settlement = db_state.executeQuery();
			settlement.next();
			Assert.assertEquals(settlement.getString("settlement_id"),settlementid, "插入结算Log表成功");
		
		}else if(num == "2"){
			String message = responseObj.getString("message");
			String state = responseObj.getString("state");
			Assert.assertEquals(message,"结算失败！FAIL  参数错误:输入的用户openid有误.", "输入的openid有误");
			Assert.assertEquals(state,"0", "结算失败！FAIL  参数错误:输入的用户openid有误.");
			PreparedStatement db_state = db.prepareStatement("select * from  t_rs_settlement_log where settlement_id=" + settlementid);
			ResultSet settlement = db_state.executeQuery();
			settlement.next();
			Assert.assertEquals(settlement.getString("settlement_id"),settlementid, "插入结算Log表成功");
		}else{
			String state = responseObj.getString("state");
			Assert.assertEquals(state,"1", "结算成功");
			PreparedStatement db_state = db.prepareStatement("select * from  t_rs_settlement_log where settlement_id=" + settlementid );
			ResultSet settlement = db_state.executeQuery();
			settlement.next();
			Assert.assertEquals(settlement.getString("settlement_id"),settlementid, "插入结算Log表成功");
		}
		
		
	}
	
	@AfterClass
	public void afterClass(){
		if(db!=null){
			db.closeConnection();
		}
	}
}
