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
import com.bj58.qa.rs.inteface.dataprovider.CpsOrderDetailSetBadProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

/**
 * 搬家订单详情 ---修改无效状态接口
 * @author hkk 
 * 2017.07.25
 * 1.判断返回状态是否正确
 * 2.判断相关操作记录是否成功插入数据库
 * 3.判断数据库中相应表中的状态是否修改
*/

public class CpsOrderDetailSetBadTest extends BaseTest {
	private String url = "http://rsm.58.com/cps/order/detail/setBad";
	private Map<String, String> header;
	private DBUtil db;

	@Override
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = CpsOrderDetailSetBadProvider.class, dataProvider = "cpsOrderDetailSetBadProvider")
	public void cpsOrderDetailSetBadTest(String demandId, String remark, String bad_remark, String actionid)
			throws Exception {
		Logger.log("搬家订单详情 ---修改无效状态 " + "测试开始=======发送Http请求： " + url, true);
		System.out.println("搬家订单详情 ---修改无效状态页面");

		Map<String, String> param = new HashMap<String, String>();
		param.put("NVrgLpVluBFisSw", "201508121014263a58b880");
		param.put("demandId", demandId);
		param.put("remark", remark);
		param.put("bad_remark", bad_remark);
		param.put("actionid", actionid);

		JSONObject responseObject = HttpRequest.doPostReturnResponseJson(url, param);
		System.out.println(responseObject);

		Object status = responseObject.get("status");
		Assert.assertEquals(Integer.parseInt(status.toString()), 1, "该" + demandId + "置为无效状态失败");

		// 将demand表中该demandId下的状态置为无效
		String sqlStrDemand = "select * from t_rs_demand where id = " + demandId;
		PreparedStatement demandPs = db.prepareStatement(sqlStrDemand);
		ResultSet demandRs = demandPs.executeQuery();
		demandRs.next();
		int state = demandRs.getInt("state");// 查看是否无效 10——无效
		Assert.assertEquals(state, 10, "需求表中将该订单置为无效失败");

		// 是否增加了客服操作
		String sqlStrStaffOptionLog = "select * from t_rs_staff_optionlog where demand_id " + demandId
				+ "order by create_time desc";
		PreparedStatement staffOptionLogPs = db.prepareStatement(sqlStrStaffOptionLog);
		ResultSet staffOptionLogRs = staffOptionLogPs.executeQuery();
		staffOptionLogRs.next();
		String option = staffOptionLogRs.getString("option");
		Assert.assertEquals(option, "无效", "添加客服操作置为无效记录失败");

		// 将流水表中该demandI的下的加价和下单信息的状态置为无效
		String sqlStrDemandDiaryDebt = "select * from t_rs_demand_diary_debt where demand_id = " + demandId
				+ "AND (debt_type = 1 or debt_type = 2)";
		PreparedStatement demandDirayDebtPs = db.prepareStatement(sqlStrDemandDiaryDebt);
		ResultSet demandDirayDebtRs = demandDirayDebtPs.executeQuery();
		List demandDirayDebtList = CommonUtil.resultSetToList(demandDirayDebtRs);
		Map<String, Object> demandDirayDebtMap = new HashMap<String, Object>();
		int dState = 0;
		boolean flag = true;// 标志该demandid下的下单和加价信息中，是否存在没有置为无效状态的情况

		for (int i = 0; i < demandDirayDebtList.size(); i++) {
			demandDirayDebtMap = (Map) demandDirayDebtList.get(i);
			dState = Integer.parseInt(demandDirayDebtMap.get("state").toString());
			if (dState != 10) {
				flag = false;
				break;
			}
		}
		Assert.assertEquals(flag, true, "该流水表中所有未支付流水置为无效失败");

	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}
	}
}
