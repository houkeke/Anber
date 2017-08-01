package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.bj58.qa.rs.inteface.dataprovider.SettlementLogDataProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

/**
 * 异常处理操作记录查看接口
 * @author peiwei
 * 2017.7.4
 * 1.判断接口返回条数与数据库是否一致
 * 2.参数正确时判断接口返回数据与数据库查询结果是否一致
 * 3.参数有误时判断接口返回数据与数据库查询结果是否一致
 * 
 */

public class SettlementLogTest extends BaseTest{

	private String url="http://rsm.58.com/settlement/showlog";
	
	private DBUtil db ;
	
	@BeforeClass
	public void beforeClass(){
		db = new DBUtil("M");//初始化数据库
		db.connection();//连接数据库
	}
	
	@Test(dataProviderClass=SettlementLogDataProvider.class,dataProvider="settlementLogDataProvider")
	public void settlementLog(String num,String msg,String settlementid) throws SQLException{
		Map<String, String> params =  new HashMap<String, String>();
		params.put("NVrgLpVluBFisSw", "2016051714120056878e24");
		params.put("settlementid", settlementid);
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		JSONObject responseObj=HttpRequest.doPostReturnResponseJson(url, params);
		System.out.println(responseObj);
		responseObj.remove("header");
        responseObj.remove("cookies");
		int size = responseObj.size();
		String size_obj = String.valueOf(size-1);

		if(num =="1"){
		//取返回数据的最后一条
		JSONObject testdata = (JSONObject) responseObj.get(size_obj);
		String remark = (String) testdata.get("remark");
		String staffId = (String) testdata.get("staffId");
		
		PreparedStatement db_test = db.prepareStatement("select * from t_rs_settlement_log where settlement_id = "+settlementid);
		ResultSet settleLogRS = db_test.executeQuery();
		List settleLogList = CommonUtil.resultSetToList(settleLogRS);
		
		Assert.assertEquals(String.valueOf(size),String.valueOf(settleLogList.size()), "接口返回的数据条数与数据库查询不一致");
		
		Map<String,Object> db_remark = (Map)settleLogList.get(0);
    	Assert.assertEquals(remark, db_remark.get("remark"), "获取操作记录remark不一致");
		
    	Map<String,Object> db_staffId = (Map)settleLogList.get(0);
		Assert.assertEquals(staffId, db_staffId.get("staff_id"), "获取操作记录staffId不一致");
		}else{
			return;
		}
			
		}
	
	@AfterClass
	public void afterClass(){
		if(db!=null){//断开数据库连接
			db.closeConnection();
		}
	}
	
}
