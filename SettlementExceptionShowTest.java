package com.bj58.qa.rs.inteface.modeltest;

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
import com.bj58.qa.rs.inteface.dataprovider.SettlementExceptionShowDataprovider;

/**
 * 异常处理结果展示接口
 * @author peiwei
 * 2017.6.28
 * 1.判断接口返回数据是否异常
 * 2.参数有误时判断接口返回数据与数据库查询结果是否一致
 * 
 */
public class SettlementExceptionShowTest extends BaseTest{

private String url="http://rsm.58.com/settlement/exceptionshow";
	
	private DBUtil db ;
	
	@BeforeClass
	public void beforeClass(){
		db = new DBUtil("M");//初始化数据库
		db.connection();//连接数据库
	}
	@Test(dataProviderClass=SettlementExceptionShowDataprovider.class,dataProvider="settlementExceptionShowrovider")
	public void settlementExceptionShowTest(String msg,String settlementid ) throws SQLException {
		Map<String, String> params =  new HashMap<String, String>();
		params.put("NVrgLpVluBFisSw", "2016051714120056878e24");
		params.put("settlementid", settlementid);
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		
		JSONObject responseObj=HttpRequest.doPostReturnResponseJson(url, params);
		System.out.println(responseObj);
		
		Object state = responseObj.get("state");
		if(state != null){
			Assert.assertEquals(state.toString(),"0", "接口返回信息:获取结算订单信息失败!");
		}else{
			Object originAmount = responseObj.get("originAmount");
			PreparedStatement db_state = db.prepareStatement("select amount from t_rs_settlement where id ="+settlementid);
			ResultSet originAmountRS = db_state.executeQuery();
			originAmountRS.next();
			String db_originAmount = originAmountRS.getString("amount");
			Assert.assertEquals(originAmount,db_originAmount, "接口返回信息与数据库查询结果不一致");
		}
		
	}
	
	@AfterClass
	public void afterClass(){
		if(db!=null){//断开数据库连接
			db.closeConnection();
		}
	}
}
