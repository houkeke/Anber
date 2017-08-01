package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.junit.AfterClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.dataprovider.SettlementAccountProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

import junit.framework.Assert;

/**
 * 结算审核接口
 * @author hkk 
 * 2017.07.17
 * 1.判断返回状态否正确
 * 2.判断相关数据库操作是否更新
 * 
*/

public class SettlementAccountTest extends BaseTest {
	private String url = "http://rsm.58.com/settlement/account";
	private Map<String, String> header;
	private DBUtil db;

	@Override
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = SettlementAccountProvider.class, dataProvider = "settlementAccountProvider")
	public void settlementAccountTest(String settlementid, String actionid, String remark) throws Exception {
		Logger.log("结算审核接口 " + "测试开始=======发送Http请求： " + url, true);
		System.out.println("结算审核接口");

		String sqlStrSettlement = "SELECT * from t_rs_settlement where id = ";
		String sqlStrSettlementLog = "SELECT * from t_rs_settlement_log where settlement_id = ";

		// 获取该settlementid下的初始更新时间
		PreparedStatement settlementPsOrigin = db.prepareStatement(sqlStrSettlement + settlementid);
		ResultSet settlementRsOrigin = settlementPsOrigin.executeQuery();
		settlementRsOrigin.next();
		String upTime = settlementRsOrigin.getString("update_time");
		String updateTimeOrigin = "0000-00-00 00:00:00";

		// 判断更新时间是否为空，以防空指针报错
		if (upTime == null) {
			updateTimeOrigin = "0000-00-00 00:00:00";
		} else {
			updateTimeOrigin = upTime.substring(0, 19);
		}

		// 获取客服日志表中的初始数据
		PreparedStatement settlementLogPsOrigin = db.prepareStatement(sqlStrSettlementLog + settlementid);
		ResultSet settlementLogRsOrigin = settlementLogPsOrigin.executeQuery();
		List settlementLogListOrigin = CommonUtil.resultSetToList(settlementLogRsOrigin);

		Map<String, String> param = new HashMap<String, String>();
		param.put("settlementid", settlementid);
		param.put("actionid", actionid);
		param.put("remark", remark);
		param.put("NVrgLpVluBFisSw", "201702141355236f87c771");

		JSONObject responseObj = HttpRequest.doPostReturnResponseJson(url, param);
		System.out.println(responseObj);

		Object state = responseObj.get("state");
		Object message = responseObj.get("message");

		// 审核成功（审核通过、审核拒绝）
		if (Integer.parseInt(state.toString()) == 1) {
			PreparedStatement settlementPs = db.prepareStatement(sqlStrSettlement + settlementid);
			ResultSet settlementRs = settlementPs.executeQuery();
			settlementRs.next();

			// 获取该settlementid下的更新时间
			String updateTime = settlementRs.getString("update_time").toString().substring(0, 19);
			boolean flag = false;
			if (updateTime != updateTimeOrigin) {
				flag = true;
			}
			Assert.assertEquals("审核结算订单信息失败！", true, flag);

			// 获取客服日志表中的数据
			PreparedStatement settlementLogPs = db.prepareStatement(sqlStrSettlementLog + settlementid);
			ResultSet settlementLogRs = settlementLogPs.executeQuery();
			List settlementLogList = CommonUtil.resultSetToList(settlementLogRs);
			Assert.assertEquals("审核结算订单日志信息录入失败!", settlementLogListOrigin.size() + 1, settlementLogList.size());

		} else {// 审核失败 state==0;
			System.out.println(message);
		}

	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}

	}
}
