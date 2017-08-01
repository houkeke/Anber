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
import com.bj58.qa.rs.inteface.dataprovider.DemandPendingListProvider;
/**
 * 待审核列表接口
 * @author gll
 * 1.判断返回状态码正确
 * 2.判断接口返回的数据数量与数据库中的是否一致
 */
public class DemandPendingListTest extends BaseTest{

	private String url = "http://rsm.58.com/demand/pending/listData";
	private Map<String,String> header;
	private DBUtil db;
	
	@BeforeClass
	public void beforeClass(){
		db = new DBUtil("M");
		db.connection();
	}
	
	@Test(dataProviderClass=DemandPendingListProvider.class,dataProvider="demandPendingListProvider")
	public void demandPendingListTest(String msg,String pageSize) throws Exception{
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		Map<String,String> params = new HashMap<String,String>();
		params.put("pageSize", pageSize);
		params.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
		JSONObject response = HttpRequest.doPostReturnResponseJson(url, params);
		Object message = response.get("message");
		Assert.assertEquals(message.toString(), "SUCCESS", "待审核列表接口返回状态码错误");
		
		//数据库返回的数据数目
		//待审核列表中展示的需求为未绑定客服的需求和绑定的客服为自己的需求
		String condition = "select count(*) from t_rs_demand where state=1 AND staff_id = '871980545922351104' OR state=1 AND ISNULL(staff_id)";
		PreparedStatement sql = db.prepareStatement(condition);
		ResultSet result = sql.executeQuery();
		result.next();
		int except_size = result.getInt("count(*)");
		System.out.println(except_size);
		
		//接口返回的数据数目
		int pagesize = Integer.valueOf(pageSize);
		int page_end = except_size % pagesize == 0 ? 0 : 1;
		int page = except_size / pagesize + page_end ;
		System.out.println(page);
		if(page==1){
			Assert.assertEquals(response.getJSONArray("data").size(), except_size, "获取待审核需求个数与数据库中不一致");
		}else{
			for (int i = 1; i <= page; i++){
				Map<String,String> param = new HashMap<String,String>();
				param.put("pageNum", String.valueOf(i));
				param.put("pageSize", pageSize);
				param.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
				JSONObject responses = HttpRequest.doPostReturnResponseJson(url, param);
				int actual_size = responses.getJSONArray("data").size();
				if(i<page){
					Assert.assertEquals(String.valueOf(actual_size), String.valueOf(pagesize), "第"+i+"页获取待审核需求数目与数据库不一致");
				}else{
					int end_size = except_size - pagesize *(i-1);
					System.out.println(end_size);
					Assert.assertEquals(String.valueOf(actual_size), String.valueOf(end_size), "第"+i+"页获取待审核需求数目与数据库不一致");
				}
			}
			
		}
	}
	
	@AfterClass
	public void afterClass(){
		if(db!=null){
			db.closeConnection();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
