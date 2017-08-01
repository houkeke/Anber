package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.dataprovider.CommonDataCitysProvider;
import com.bj58.qa.rs.inteface.dataprovider.MissTelCallProvider;
/**
 * 获取城市列表接口  
 * @author gll
 * 2017.6.30
 * 1.判断接口返回码
 * 2.判断当前权限的城市列表是否正确
 */
public class CommonDataCitysTest extends BaseTest{
	
	private String url = "http://rsm.58.com/data/citys";
	private Map<String,String> header;
	private DBUtil db;
	
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}
	
	@Test(dataProviderClass=CommonDataCitysProvider.class,dataProvider="commonDataCitysProvider")
	public void commonDataCitysTest(String msg,String citys) throws Exception{
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		Map<String,String> params = new HashMap<String,String>();
		params.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
		JSONObject object = HttpRequest.doPostReturnResponseJson(url, params);
		Object message = object.get("message");
		Assert.assertEquals(message.toString(), "SUCCESS", "获取城市列表接口返回状态码错误！");
		
		JSONArray array = object.getJSONArray("data");
		String actual = "";
		for(int i = 0 ; i < array.size(); i++){
			actual = actual + "," + array.getJSONObject(i).getString("listname");
		}
		String except = citys;
		Assert.assertEquals(actual, except, "获取城市列表接口城市错误！");
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
