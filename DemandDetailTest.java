package com.bj58.qa.rs.inteface.modeltest;

import static org.testng.Assert.fail;

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
import com.bj58.qa.rs.inteface.dataprovider.DemandDetailProvider;
import com.bj58.qa.rs.inteface.dataprovider.ListOrderProvider;
/**
 * 需求详情接口  
 * @author gll
 * 2017.7.11
 * 1.判断返回状态码正确
 * 2.客服name、demandentity、demanddetailentity、demandamountentity
 */
public class DemandDetailTest extends BaseTest{
	
	private String urll = "http://rsm.58.com/demand/detail/";
	private Map<String,String> header;
	private DBUtil db;
	
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}
	
	@Test(dataProviderClass=DemandDetailProvider.class,dataProvider="demandDetailProvider")
	public void demandDetailTest(String msg,String demandId) throws Exception{
		Logger.log(msg + "测试开始=========发送Http请求： " + urll, true);
		Map<String,String> params = new HashMap<String,String>();
		params.put("NVrgLpVluBFisSw", "201407221101513ef447b0");
		String url = urll + demandId;
		JSONObject object = HttpRequest.doPostReturnResponseJson(url, params);
		Object message = object.get("message");
		//判断当前id是否为待审核或待跟进
		String condition = "select * from t_rs_demand where id = " + demandId;
		PreparedStatement sql = db.prepareStatement(condition);
		ResultSet result = sql.executeQuery();
		if(!result.next()){
			Logger.log("该用例id为非需求id");
		}else{
			int state = result.getInt("state");
			if(1==state||2==state){
				Assert.assertEquals(message.toString(), "SUCCESS", "获取需求详情接口返回状态码错误！");
				//校验详情页内容
				verifentity(demandId,object);
			}else{
				Assert.assertEquals(message.toString(), "需求状态不是待审核", "获取需求详情接口返回状态码错误！");
			}
		}
	}
	
	public void verifentity(String demandId,JSONObject object) throws Exception{
		//1.demandEntity:staff_id,id,phone
		String condition1 = "select * from t_rs_demand where id="+demandId;
		PreparedStatement sql1 = db.prepareStatement(condition1);
		ResultSet result1 = sql1.executeQuery();
		result1.next();
		int staff_id = result1.getInt("staff_id");
		if(object.get("staffName")==null){
			Assert.assertEquals(String.valueOf(staff_id), "-1", "接口返回json中的客服信息与数据库中不符！");
		}else{
			if(staff_id==-1){
				fail("接口返回json中的客服信息与数据库中不符！");
			}
		}
		String demandId_actual = object.getJSONObject("data").getJSONObject("demandEntity").getString("id");
		Assert.assertEquals(demandId_actual, demandId, "接口返回json中的需求id与实际不符！");
		String phone_actual = object.getJSONObject("data").getJSONObject("demandEntity").getString("phone");
		String phone_except = result1.getString("phone");
		Assert.assertEquals(phone_actual, phone_except, "接口返回json中的用户电话phone与数据库中不符！");
		//3.amountBookEntity：original_amount,discount
		
		//4.detailEntity
		
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
