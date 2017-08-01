package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.bj58.qa.rs.inteface.dataprovider.CpaOrderDetailProvider;

/**
 * 搬家商机订单详情页接口
 * @author hkk 
 * 2017.07.11
 * 1.判断返回的数据是否正确 
 * 2.判断接口返回的数据与数据库中的是否一致
 */

public class CpaOrderDetailTest extends BaseTest {
	private String url = "http://rsm.58.com/cpa/order/detail/";
	private Map<String, String> header;
	private DBUtil db;

	@Override
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = CpaOrderDetailProvider.class, dataProvider = "cpaOrderDetailProvider")
	public void cpaOrderDetailTest(String demandId, String msg) throws Exception {
		Logger.log("搬家商机订单详情页测试开始=========发送Http请求： " + url, true);
		String sqlStrOrder = "select * from t_rs_demand_order where demand_id = ";
		String sqlStrBusiness = "select * from t_rs_business where userid = ";

		Map<String, String> param = new HashMap<String, String>();
		param.put("NVrgLpVluBFisSw", "201508121014263a58b880");

		JSONObject responseObject = HttpRequest.doPostReturnResponseJson(url + demandId, param);
		System.out.println(responseObject);

		// 从页面返回的数据中的值
		JSONObject jsonObject = responseObject.getJSONObject("0");
		Object jsonOrderId = jsonObject.get("orderId");
		Object jsonDemandId = jsonObject.get("demanId");
		Object jsonBusinessName = jsonObject.get("businessName");
		Object jsonBusinessPhone = jsonObject.get("businessPhone");
		Object jsonServeType = jsonObject.get("serve_type");
		Object jsonState = jsonObject.get("state");
		Object jsonCreateTime = jsonObject.get("create_time");

		// 根据demandId,获取订单id和商家id
		PreparedStatement orderPs = db.prepareStatement(sqlStrOrder + demandId);
		ResultSet orderRs = orderPs.executeQuery();
		orderRs.next();
		String orderId = orderRs.getString("id");
		String serveType = orderRs.getString("serve_type");
		String state = orderRs.getString("state");
		String businessUserId = orderRs.getString("business_user_id");
		String createTime = orderRs.getString("create_time").substring(0, 19);

		// 将数据库中的serveType的状态1,2转换成对应的抢单模式
		if (Integer.parseInt(serveType) == 1) {
			serveType = "抢单";
		} else {
			serveType = "派单";
		}
		// 根据商家id获取商家信息
		PreparedStatement businessPs = db.prepareStatement(sqlStrBusiness + businessUserId);
		ResultSet businessRs = businessPs.executeQuery();
		businessRs.next();
		String businessName = businessRs.getString("name");
		String businessPhone = businessRs.getString("telphone");

		// 通过判断jsonObject返回的数据与在数据库中查询的数据是否一致，判断获取cpa商机订单详情页是否成功
		Assert.assertEquals(jsonOrderId.toString(), orderId, "数据库中的orderId与返回数据不一致");
		Assert.assertEquals(jsonBusinessName.toString(), businessName, "数据库中的businessName与返回数据不一致");
		Assert.assertEquals(jsonBusinessPhone.toString(), businessPhone, "数据库中的businessPhone与返回数据不一致");
		Assert.assertEquals(jsonServeType.toString(), serveType, "数据库中的serveType与返回数据不一致");
		Assert.assertEquals(jsonState.toString(), state, "数据库中的state与返回数据不一致");
		Assert.assertEquals(jsonCreateTime.toString(), createTime, "数据库中的createTime与返回数据不一致");
	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}
	}
}
