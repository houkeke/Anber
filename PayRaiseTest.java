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
import com.bj58.lbg.reassureserve.core.components.RsDemandDiaryDebtServiceComp;
import com.bj58.lbg.rs.entity.RsDemandDiaryDebtEntity;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.dataprovider.PayRaiseProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

//import junit.framework.Assert;

/**
 * 发起加价接口
 * @author hkk 
 * 2017.06.30
 * 1.判断返回状态是否正确
 * 2.判断相关操作记录是否成功插入数据库
*/

public class PayRaiseTest extends BaseTest {
	private String url = "http://rsm.58.com/pay/raise";
	private Map<String, String> header;
	private DBUtil db;
	private RsDemandDiaryDebtServiceComp demandDiaryDebtServiceComp;
	private RsDemandDiaryDebtEntity demandDiaryDebtEntity;

	@Override

	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = PayRaiseProvider.class, dataProvider = "payRaiseProvider")
	public void payRaiseTest(String demandId, String amount, String remark) throws Exception {
		Logger.log("加价接口 " + "测试开始=======发送Http请求： " + url, true);
		System.out.println("加价页面");
		System.out.println("amount= " + amount);
		// 判断是否是传入参数为空的反向测试用例
		String judgeMsg = "";
		if ((demandId == "") || (amount == "") || (remark == "")) {
			judgeMsg = "传入的参数存在null";
		}

		// 获取加价之前的该demandId下的加价信息
		PreparedStatement demandPsOrigin = db
				.prepareStatement("select * from t_rs_demand_diary_debt where demand_id = " + demandId);
		ResultSet demandRsOrigin = demandPsOrigin.executeQuery();
		List demandListOrigin = CommonUtil.resultSetToList(demandRsOrigin);

		// 获取加价之前该demandId下的客服操作信息
		PreparedStatement staffOptionLogPsOrigin = db
				.prepareStatement("select * from t_rs_staff_optionlog where demand_id = " + demandId);
		ResultSet staffOptionaLogRsOrigin = staffOptionLogPsOrigin.executeQuery();
		List staffOptionaLogListOrigin = CommonUtil.resultSetToList(staffOptionaLogRsOrigin);

		Map<String, String> param = new HashMap<String, String>();
		param.put("demandId", demandId);
		param.put("amount", amount);
		param.put("remark", remark);
		param.put("NVrgLpVluBFisSw", "201702141355236f87c771");

		// 方法1：传入cookie
		header = new HashMap<String, String>();
		header.put("Cookie",
				"id58=c5/nn1jd9AMRToNfSrSTAg==; gr_user_id=3c0e5e58-5ee5-4d2b-a14f-88e23d139a70; als=0; bj58_id58s=\"dGNDUGdKRWhfMl9sNDE0Ng==\"; cookieuid=38de9712-e965-4a75-a6dc-16655d1ea7a9; cookieuid1=c5/n61jngkqpDQ65A4J9Ag==; NTKF_T2D_CLIENTID=guestTEMP5928-4785-3516-13E6-F89051F8ADC6; Hm_lvt_e15962162366a86a6229038443847be7=1492767040,1494517569,1494598926,1494933913; Hm_lvt_3bb04d7a4ca3846dcc66a99c3e861511=1494516935,1494517562,1494598926,1494933913; _ga=GA1.2.726217911.1491013885; commonTopbar_myfeet_tooltip=end; __utma=253535702.726217911.1491013885.1494522659.1497424519.2; __utmz=253535702.1497424519.2.2.utmcsr=bj.58.com|utmccn=(referral)|utmcmd=referral|utmcct=/chuzu/; myfeet_tooltip=end; bj58_new_uv=9; __autma=253535702.1531904914.1491013884.1491562143.1498011228.6; smartId=de2afa3ddebfe648dd0860717b1cc6fd; bj58bsp_logininfo=userid%3D201702141355236f87c771%26username%3Dhoukeke%26realname%3D%E4%BE%AF%E7%8F%82%E7%8F%82%26orgid%3D201612071352001d9f46f9%26dutyid%3D201101101655507f728cae%26eamil%3Dhoukeke%4058ganji.com%26dutyid%3D201101101655507f728cae%26siteTime%3D042BD570C90A16D504A42F58F55778CB18D01E93A021A71C6%26sitekey%3D0DA5C2BD7C9D50931E3D5EEA23608D56A2D4A781EA8D6AE8120BA49BDCC4A77C3; _bu=201702141355236f87c771");

		// 方法2：param.put("NVrgLpVluBFisSw", "201702141355236f87c771");
		JSONObject responseObj = HttpRequest.doPostReturnResponseJson(url, param);
		System.out.println(responseObj);

		Object message = responseObj.get("message");
		Object status = responseObj.get("status");

		int statusInt = Integer.valueOf(status.toString());
		Assert.assertEquals(message, "SUCCESS", message + " " + judgeMsg);

		// 判断是否增加支付流水信息到支付流水表
		PreparedStatement demandPs = db
				.prepareStatement("select * from t_rs_demand_diary_debt where demand_id = " + demandId);
		ResultSet demandRs = demandPs.executeQuery();
		List demandList = CommonUtil.resultSetToList(demandRs);
		Map<String, Object> demandMap = new HashMap<String, Object>();
		demandMap = (Map) demandList.get(demandList.size() - 1);// 获取最新增的流水信息

		Assert.assertEquals(demandListOrigin.size() + 1, demandList.size(), "保存支付流水失败");

		// 判断对账表是否增加流水记录
		if (status.equals(1)) {
			// 判断是否增加支付流水信息到支付流水表

			// 取最后一条数据的主键id
			long payCheckWechatOrderId = Long.parseLong(demandMap.get("id").toString());
			System.out.println("最后一条数据的主键id " + payCheckWechatOrderId);
			PreparedStatement payCheckWechatPs = db
					.prepareStatement("select * from t_rs_paycheck_wechat where order_id = " + payCheckWechatOrderId);
			ResultSet payCheckWechatRs = payCheckWechatPs.executeQuery();
			List payCheckWechatList = CommonUtil.resultSetToList(payCheckWechatRs);

			Assert.assertEquals(payCheckWechatList.size(), 1, "对账表增加流水记录失败");

		}

		// 判断是否对客服操作表插入记录成功
		if (status.equals(1)) {
			// 获取插入之后的客服操作表数据
			PreparedStatement staffOptionLogPs = db
					.prepareStatement("select * from t_rs_staff_optionlog where demand_id = " + demandId);
			ResultSet staffOptionaLogRs = staffOptionLogPs.executeQuery();
			List staffOptionaLogList = CommonUtil.resultSetToList(staffOptionaLogRs);

			Assert.assertEquals(staffOptionaLogList.size(), staffOptionaLogListOrigin.size() + 1, "插入客服操作表失败");
		}
	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}

	}

}
