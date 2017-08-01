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
import com.bj58.qa.rs.inteface.dataprovider.MissTelCallProvider;
/**
 * 未接来电外呼接口  
 * @author gll
 * 2017.6.30
 * 1.t_rs_miss_tel表state由0置为1
 */
public class MissTelCallTest extends BaseTest{
	
	private String url = "http://rsm.58.com/misstel/docall";
	private Map<String,String> header;
	private DBUtil db;
	
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}
	
	@Test(dataProviderClass=MissTelCallProvider.class,dataProvider="missTelCallProvider")
	public void missTelCallTest(String msg,String mobile) throws Exception{
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		Map<String,String> params = new HashMap<String,String>();
		params.put("mobile", mobile);
		params.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
		JSONObject object = HttpRequest.doPostReturnResponseJson(url, params);
		Object state = object.get("state");
		Assert.assertEquals(state.toString(), "success", "未接来电外拨接口返回状态码错误！");
		
		String condition = "select * from t_rs_miss_tel where tel = " + mobile;
		PreparedStatement mobile_state = db.prepareStatement(condition);
		ResultSet mobile_states = mobile_state.executeQuery();
		mobile_states.next();
		Assert.assertEquals(mobile_states.getInt("state"), "1", "未接来电外拨接口数据库状态未修改！");
	}
	
	@AfterClass
	public void afterClass(){
		if(db!=null){//断开数据库连接
			db.closeConnection();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
