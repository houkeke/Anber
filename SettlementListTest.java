package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.dataprovider.SettlementListProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

import junit.framework.Assert;

/**
 * 结算列表查询接口
 * @author hkk 
 * 2017.07.14
 * 判断接口返回的数据数量与数据库中的是否一致
 * 
*/

public class SettlementListTest extends BaseTest {
	private String url = "http://rsm.58.com/settlement/list";
	private Map<String, String> header;
	private DBUtil db;

	@Override
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = SettlementListProvider.class, dataProvider = "settlementListProvider")
	public void settlementListTest(String businessuserid, String orderid, String state, String cate, String begintime,
			String endtime) throws Exception {
		Logger.log("结算查询接口 " + "测试开始=======发送Http请求： " + url, true);
		System.out.println("结算查询接口");

		Map<String, String> param = new HashMap<String, String>();
		StringBuffer sqlStr = new StringBuffer("");

		if (state.length() != 0) {
			param.put("state", state);
			sqlStr.append(
					" SELECT * FROM t_rs_settlement LEFT JOIN t_act_object ON t_act_object.id = t_rs_settlement.id where state_id = "
							+ state + " AND type = 'SETTLEMENT'");
		} else {
			sqlStr = new StringBuffer("SELECT * FROM t_rs_settlement where 1 = 1");
			// sqlStr = "SELECT * FROM t_rs_settlement where 1 = 1";//
			// 条件永远为真，就查出所有数据
			if (businessuserid.length() != 0) {
				// 此处businessuserid查的是已结算的，businessuserId返回的是结算列表中的第一页
				param.put("businessuserid", businessuserid);
				sqlStr.append(" and t_rs_settlement.business_user_id = " + businessuserid);
			}
			if (orderid.length() != 0) {
				param.put("orderid", orderid);
				sqlStr.append(" and t_rs_settlement.order_id = " + orderid);
			}
			if (cate.length() != 0) {
				param.put("cate", cate);
				sqlStr.append(" and _rs_settlement.cate = " + cate);
			}
			if (begintime.length() != 0) {
				param.put("begintime", begintime);
				sqlStr.append(" and t_rs_settlement.create_time >= '" + begintime);
				sqlStr.append("'");
			}
			if (endtime.length() != 0) {
				param.put("endtime", endtime);
				sqlStr.append(" and t_rs_settlement.create_time <= '" + endtime);
				sqlStr.append("'");
			}
		}
		System.out.println(sqlStr);
		param.put("NVrgLpVluBFisSw", "201702141355236f87c771");
		JSONObject responseObj = HttpRequest.doPostReturnResponseJson(url, param);
		System.out.println(responseObj);

		Object resrecords = responseObj.get("records");// 将此条信息作为查询比对的依据；

		PreparedStatement settlementPs = db.prepareStatement(sqlStr.toString());
		ResultSet settlementRs = settlementPs.executeQuery();
		List settlementList = CommonUtil.resultSetToList(settlementRs);

		Assert.assertEquals("查询条数不一致", Integer.parseInt(resrecords.toString()), settlementList.size());
	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}

	}

}
