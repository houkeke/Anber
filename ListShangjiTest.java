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
import com.bj58.qa.rs.inteface.dataprovider.CommonDataCatesProvider;
import com.bj58.qa.rs.inteface.dataprovider.CommonDataCitysProvider;
import com.bj58.qa.rs.inteface.dataprovider.ListShangjiProvider;
import com.bj58.qa.rs.inteface.dataprovider.MissTelCallProvider;
/**
 * 获取商机列表接口  
 * @author gll
 * 2017.7.10
 * 1.判断返回状态码正确
 * 2.判断接口返回的数据数量与数据库中的是否一致
 */
public class ListShangjiTest extends BaseTest{
	
	private String url = "http://rsm.58.com/demand/shangji/listData";
	private Map<String,String> header;
	private DBUtil db;
	
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}
	
	@Test(dataProviderClass=ListShangjiProvider.class,dataProvider="listShangjiProvider")
	public void listShangjiTest(String msg,String pageSize) throws Exception{
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		Map<String,String> params = new HashMap<String,String>();
		params.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
		params.put("pageSize", pageSize);
		JSONObject object = HttpRequest.doPostReturnResponseJson(url, params);
		Object message = object.get("message");
		Assert.assertEquals(message.toString(), "SUCCESS", "获取商机列表接口返回状态码错误！");
		
		//数据库返回的数据数目
		String condition = "SELECT count(*) from t_rs_demand where serve_model = 2 AND staff_id = '871980545922351104'";
		PreparedStatement sql = db.prepareStatement(condition);
		ResultSet result = sql.executeQuery();
		result.next();
		int except = result.getInt("count(*)");
		System.out.println(except);
		
		//接口返回的数据
		int pagesize = Integer.valueOf(pageSize);
		int page_end = except % pagesize == 0 ? 0 : 1;
		int page = except / pagesize + page_end ;
		System.out.println(page);
		
		if(page==1){
			Assert.assertEquals(object.getJSONArray("data").size(), except, "获取商机个数与数据库中不一致");
		}else{
			for(int i=1;i<=page;i++){
				Map<String,String> param = new HashMap<String,String>();
				param.put("pageNum", String.valueOf(i));
				param.put("pageSize", pageSize);
				param.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
				JSONObject responses = HttpRequest.doPostReturnResponseJson(url, param);
				int actual = responses.getJSONArray("data").size();
				if(i<page){
					Assert.assertEquals(String.valueOf(actual), pageSize, "第"+i+"页获取商机数目与数据库不一致");
				}else{
					int end_num = except - pagesize *(i-1);
					System.out.println(end_num);
					Assert.assertEquals(String.valueOf(actual), String.valueOf(end_num), "第"+i+"页获取商机数目与数据库不一致");
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
