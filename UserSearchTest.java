package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
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
import com.bj58.qa.rs.inteface.dataprovider.UserSearchProvider;

/**
 * 用户查询接口
 * @author gll
 * 2017.6.24
 * 1.判断接口返回码是否正确
 * 2.判断接口返回的数据数目与数据库中的是否一致
 * 
 */
public class UserSearchTest extends BaseTest{
	
	private String url = "http://rsm.58.com/user/searchresult";
	private Map<String, String> header;

	private DBUtil db;

	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}
	
	@Test(dataProviderClass=UserSearchProvider.class,dataProvider="userSearchProvider")
	public void userSearchTest(String msg,String phone,String name,String pageNum,String pageSize) throws Exception{
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("name", name);
		params.put("pageNum", pageNum);
		params.put("pageSize", pageSize);
		params.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
		JSONObject responseObj=HttpRequest.doPostReturnResponseJson(url, params);
		Object message = responseObj.get("message");
		Assert.assertEquals(message.toString(), "SUCCESS", "用户查询接口返回状态码错误！");
		
		
		//由于整表查询数据量大，因此用例只有带查询条件的，额外加个整表查询数量校验
		Map<String,String> param_s = new HashMap<String,String>();
		param_s.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
		JSONObject responseObj_s=HttpRequest.doPostReturnResponseJson(url, param_s);
		int actual_s = Integer.valueOf(responseObj_s.get("recordsTotal").toString());
		String condition_s = "select count(*) from t_rs_demand";
		PreparedStatement userresult_s_sql = db.prepareStatement(condition_s);
		ResultSet userresult_s = userresult_s_sql.executeQuery();
		userresult_s.next();
		int except_s = userresult_s.getInt("count(*)");
		System.out.println("actual:"+actual_s+",except:"+except_s);
		Assert.assertEquals(String.valueOf(actual_s), String.valueOf(except_s), "用户查询接口需求总数错误！");
		
		//数据库中的数据数目
		String condition = "select count(*) from t_rs_demand";
		switch (Integer.valueOf(msg))
		{
			case 1:
				condition = "select count(*) from t_rs_demand where phone = " + phone + " AND name = '" + name + "'";
				break;
			case 2:
				condition = "select count(*) from t_rs_demand where name = '" + name + "'";
				break;
			case 3:
				condition = "select count(*) from t_rs_demand where phone = " + phone;
				break;
			default:
				System.out.println("没有该条用例");
				break;
		}
		PreparedStatement userresult_sql = db.prepareStatement(condition);
		ResultSet userresult = userresult_sql.executeQuery();
		userresult.next();
		int except_size = userresult.getInt("count(*)");
		
		//接口返回的数据数目
		JSONArray arrayData = responseObj.getJSONArray("data");
		int page_end = except_size % Integer.valueOf(pageSize) == 0 ? 0 : 1;
		int page = except_size / Integer.valueOf(pageSize) + page_end;
		if(page==1){
			Assert.assertEquals(arrayData.size(), except_size, "获取用户需求个数与数据库不一致");
		}else{
			for (int i = 1; i <= page; i++) {
				Map<String,String> param = new HashMap<String,String>();
				param.put("phone", phone);
				param.put("name", name);
				param.put("pageNum", String.valueOf(i));
				param.put("pageSize", pageSize);
				param.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
				JSONObject response=HttpRequest.doPostReturnResponseJson(url, param);
				int actual_size = response.getJSONArray("data").size();
				if(i<page){
					Assert.assertEquals(String.valueOf(actual_size), String.valueOf(pageSize), "第"+i+"页获取用户需求个数与数据库不一致");
				}else{
					int end_size = except_size - Integer.valueOf(pageSize)*(i-1);
					System.out.println(end_size);
					Assert.assertEquals(String.valueOf(actual_size), String.valueOf(end_size), "第"+i+"页获取用户需求个数与数据库不一致");
				}
			}
		}
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
