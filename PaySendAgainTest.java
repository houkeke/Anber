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
import com.bj58.qa.rs.inteface.dataprovider.PaySendAgainProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

import junit.framework.Assert;

/**
 * 再次发起加价接口
 * @author hkk 
 * 2017.07.04
 * 1.判断返回状态是否正确
 * 2.判断相关操作记录是否成功插入数据库
 * 
*/

public class PaySendAgainTest extends BaseTest {

	private String url = "http://rsm.58.com/pay/paySendAgain";
	private Map<String, String> Header = new HashMap<String, String>();
	private DBUtil db;

	@Override

	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@SuppressWarnings("deprecation")
	@Test(dataProviderClass = PaySendAgainProvider.class, dataProvider = "paySendAgainProvider")
	public void payAgainTest(String demandDiaryDebtId, String state) throws Exception {
		Logger.log("再次发起加价接口 " + "测试开始=======发送Http请求： " + url, true);
		System.out.println("再次发起加价");

		// 从数据库中查询该流水demandDiaryDebtId的demandId
		PreparedStatement diaryDebtPsOrigin = db
				.prepareStatement("select * from t_rs_demand_diary_debt where id = " + demandDiaryDebtId);
		ResultSet diaryDebtRsOrigin = diaryDebtPsOrigin.executeQuery();
		// diaryDebtRsOrigin.last();
		// diaryDebtRsOrigin.getRow();
		diaryDebtRsOrigin.next();
		long diaryDebtDemandId = diaryDebtRsOrigin.getLong("demand_id");

		// 获取再次加价之前的该demandId下的加价信息
		PreparedStatement diaryDebtDemandPsOrigin = db
				.prepareStatement("select * from t_rs_demand_diary_debt where demand_id = " + diaryDebtDemandId);
		ResultSet diaryDebtDemandRsOrigin = diaryDebtDemandPsOrigin.executeQuery();
		List diaryDebtDemandListOrigin = CommonUtil.resultSetToList(diaryDebtDemandRsOrigin);

		// 从员工操数据库中查询该demandId下的操作记录条数
		PreparedStatement staffOptionlogPsOrigin = db
				.prepareStatement("select * from t_rs_staff_optionlog where demand_id = " + diaryDebtDemandId);
		ResultSet staffOptionlogRsOrigin = staffOptionlogPsOrigin.executeQuery();
		List staffOptionListOrigin = CommonUtil.resultSetToList(staffOptionlogRsOrigin);

		Map<String, String> param = new HashMap<String, String>();
		param.put("demandDiaryDebtId", demandDiaryDebtId);
		param.put("NVrgLpVluBFisSw", "201702141355236f87c771");

		JSONObject responseObject = HttpRequest.doPostReturnResponseJson(url, param);
		System.out.println(responseObject);

		Object status = responseObject.get("status");
		Object message = responseObject.get("message");
		int statusInt = Integer.valueOf(status.toString());

		// 流水已支付
		if ("2".equals(state)) {
			// Assert.assertEquals("再次加价发送失败 " + message, 1, statusInt);
			Assert.assertEquals("流水已支付下，对加价信息又做了处理 ", "该笔流水已支付!", message);
		}

		// 未过期或还未生成流水,发送原流水id的请求
		if ("1".equals(state)) {
			// 是否发送新的请求成功
			Assert.assertEquals("再次加价发送失败", "支付请求已发送!", message);

			// 查询员工操作记录表是否插入成功
			PreparedStatement staffOptionlogPs = db
					.prepareStatement("select * from t_rs_staff_optionlog where demand_id = " + diaryDebtDemandId);
			ResultSet staffOptionlogRs = staffOptionlogPs.executeQuery();
			List staffOptionList = CommonUtil.resultSetToList(staffOptionlogRs);
			Assert.assertEquals("再次发起加价，客服操作记录插入失败", staffOptionListOrigin.size() + 1, staffOptionList.size());
		}

		// 已失效的流水,原流水id已关闭，需再次发送新的流水
		if ("3".equals(state)) {
			// 是否发送新的请求成功
			Assert.assertEquals("再次加价发送失败", "支付请求已发送!", message.toString());

			// 查询t_rs_demand_diary_debt表中是否将原流水id置为无效
			PreparedStatement diaryDebtPs = db
					.prepareStatement("select * from t_rs_demand_diary_debt where id = " + demandDiaryDebtId);
			ResultSet diaryDebtRs = diaryDebtPs.executeQuery();
			List diaryDebtList = CommonUtil.resultSetToList(diaryDebtRs);

			Map<String, Object> mapOrigin = new HashMap<String, Object>();
			Map<String, Object> mapAfter = new HashMap<String, Object>();
			mapOrigin = (Map) diaryDebtList.get(0);// 获取该流水Id下的信息
			mapAfter = (Map) diaryDebtList.get(diaryDebtList.size() - 1);// 获取新创建的流水信息
			/*diaryDebtRs.next();
			int diaryDebtState = diaryDebtRs.getInt("state");*/
			int diaryDebtState = Integer.parseInt(mapOrigin.get("state").toString());
			Assert.assertEquals("将已失效的流水id置为失效失败", 3, diaryDebtState);

			// 判断是否增加支付流水信息到支付流水表
			Assert.assertEquals("保存支付流水失败", diaryDebtDemandListOrigin.size() + 1, diaryDebtList.size());

			// 判断对账表是否增加流水记录
			if ("1".equals(statusInt)) {

				// 取最后一条数据的主键id
				long dPayCheckWechatOrderId = Long.parseLong(mapAfter.get("id").toString());
				PreparedStatement dPayCheckWechatPs = db
						.prepareStatement("select * from t_rs_paycheck_wechat where order_id = payCheckWechatOrderId");
				ResultSet dPpayCheckWechatRs = dPayCheckWechatPs.executeQuery();
				List dPayCheckWechatList = CommonUtil.resultSetToList(dPpayCheckWechatRs);
				Assert.assertEquals("对账表增加流水记录失败", 1, dPayCheckWechatList.size());

			}

			// 判断是否对客服操作表插入记录成功
			if ("1".equals(statusInt)) {
				// 获取插入之后的客服操作表数据
				PreparedStatement staffOptionLogPs = db
						.prepareStatement("select * from t_rs_staff_optionlog where demand_id = " + diaryDebtDemandId);
				ResultSet staffOptionaLogRs = staffOptionLogPs.executeQuery();
				List staffOptionaLogList = CommonUtil.resultSetToList(staffOptionaLogRs);

				Assert.assertEquals("插入客服操作表失败", staffOptionListOrigin.size() + 1, staffOptionaLogList.size());
			}

		}

	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}

	}
}
