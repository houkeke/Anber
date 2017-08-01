package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
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
import com.bj58.qa.rs.inteface.dataprovider.OrderBusinessgrabProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

/**
 * 商家抢单接口（商家通）
 * @author hkk 
 * 2017.07.21
 * 1.判断返回状态是否正确
 * 2.判断相关操作记录是否成功插入数据库
 * 3.判断数据库状态是否更新
*/

public class OrderBusinessgrabTest extends BaseTest {
	private String url = "http://rsm.58.com/order/businessgrab";
	private Map<String, String> header;
	private DBUtil db;

	@Override
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = OrderBusinessgrabProvider.class, dataProvider = "orderBusinessgrabProvider")
	public void payRaiseTest(String orderLogId, String businessUserId, String serveModel) throws Exception {
		Logger.log("商家抢单接口（商家通） " + "测试开始=======发送Http请求： " + url, true);
		System.out.println("商家抢单接口（商家通）");

		Map<String, String> param = new HashMap<String, String>();
		param.put("orderLogId", orderLogId);
		param.put("businessUserId", businessUserId);
		param.put("NVrgLpVluBFisSw", "201702141355236f87c771");

		JSONObject responseObj = HttpRequest.doPostReturnResponseJson(url, param);
		System.out.println(responseObj);
		// 派单结果
		Object grabResult = responseObj.get("grabResult");

		// cps、cpa 系统派单
		if ("SUCCESS".equals(grabResult)) {
			// 查询该派单日志是否变为已抢单
			String sqlStrOrderLog = "SELECT * from t_rs_demand_order_log where id = " + orderLogId
					+ "and business_user_id = " + businessUserId;
			PreparedStatement orderLogPs = db.prepareStatement(sqlStrOrderLog);
			ResultSet orderLogRs = orderLogPs.executeQuery();
			List orderLogList = CommonUtil.resultSetToList(orderLogRs);
			Map<String, Object> orderLogMap = new HashMap<String, Object>();
			orderLogMap = (Map) orderLogList.get(0);
			int orderState = (Integer) orderLogMap.get("order_state");
			String demandId = orderLogMap.get("demand_id").toString();
			Assert.assertEquals(orderState, 1, "商家已抢单状态更新失败");

			// 查询其他商家派单记录是否修改为已抢空状态
			String sqlStrOrderLogOther = "SELECT * from t_rs_demand_order_log where demand_id = " + demandId
					+ " business_user_id != " + businessUserId;
			PreparedStatement orderLogOtherPs = db.prepareStatement(sqlStrOrderLog);
			ResultSet orderLogOtherRs = orderLogOtherPs.executeQuery();
			List orderLogOtherList = CommonUtil.resultSetToList(orderLogOtherRs);
			Map<String, Object> orderLogOtherMap = new HashMap<String, Object>();
			int orderStateOther = 0;// 标记其他商家的抢单状态
			boolean flag = true;// 标记其他商家派单记录 是否修改为已抢空状态

			// 遍历其他商家的派单记录是否为已抢完
			for (int i = 0; i < orderLogOtherList.size(); i++) {
				orderLogOtherMap = (Map) orderLogOtherList.get(i);
				orderStateOther = (Integer) orderLogOtherMap.get("order_state");
				if (orderStateOther == 2) {// 2——已抢完
					flag = false;
					break;
				}
			}
			Assert.assertEquals(flag, true, "将其他商家派单记录修改为已抢空状态失败");

			// 查看demand表中，需求状态是否变为待服务（CPS）
			String sqlStrDemand = "SELECT * from t_rs_demand where demand_id = " + demandId;
			PreparedStatement demandPs = db.prepareStatement(sqlStrDemand);
			ResultSet demandRs = demandPs.executeQuery();
			demandRs.next();
			int demandState = Integer.parseInt(demandRs.getString("state"));
			Assert.assertEquals(demandState, 7, "需求表更新需求状态为待服务失败");// 7——待服务

			// 查看是否生成了订单信息
			String sqlStrDemandOrder = " SELECT * from t_rs_demand_order where demand_id = " + demandId
					+ "and business_user_id = " + businessUserId;
			PreparedStatement demandOrderPs = db.prepareStatement(sqlStrDemandOrder);
			ResultSet demandOrderRs = demandOrderPs.executeQuery();
			List demandOrderList = CommonUtil.resultSetToList(demandOrderRs);
			Assert.assertEquals(demandOrderList.size(), 1, "生成订单信息失败");

			// 判断t_rs_demand_amount_book表中是否生成商家结算信息
			String sqlStrAmountBook = "SELECT * from t_rs_demand_amount_book where demand_id = " + demandId;
			PreparedStatement amountBookPs = db.prepareStatement(sqlStrAmountBook);
			ResultSet amountBookRs = amountBookPs.executeQuery();
			amountBookRs.next();
			Long bisSettlement = Long.parseLong(amountBookRs.getString("bis_settlement"));
			boolean bisFlag = false;
			if (bisSettlement != 0) {
				bisFlag = true;
			}
			Assert.assertEquals(bisFlag, true, "对账表中生成商家结算信息失败");

		} else {
			System.out.println("抢单失败");
		}

	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}

	}

}
