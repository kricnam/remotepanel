/*
 * Modem.h
 *
 *  Created on: 2011-02-15
 *      Author: mxx
 */

#ifndef MODEM_H_
#define MODEM_H_
#include <string>
#include "SerialPort.h"
using namespace std;
class Modem
{
public:
	Modem();
	virtual ~Modem();

	const char* m_szDefaultPort;
	enum   END_STATUS {
			CM_CALL_END_OFFLINE=0, //单板处于OFFLINE状态
			CM_CALL_END_NO_SRV=21, //单板无服务
			CM_CALL_END_FADE=22, //正常结束
			CM_CALL_END_INTERCEPT=23, //呼叫时被BS中断
			CM_CALL_END_REORDER=24, //呼叫时收到BS的记录
			CM_CALL_END_REL_NORMAL=25, //BS释放呼叫
			CM_CALL_END_REL_SO_REJ=26, //BS拒绝当前SO业务
			CM_CALL_END_INCOM_CALL=27, //收到了BS的来电
			CM_CALL_END_ALERT_STOP=28, //来电时收到了振铃停止的信令
			CM_CALL_END_CLIENT_END=29, //客户端正常结束
			CM_CALL_END_ACTIVATION=30, //OTASP呼叫时激活结束
			CM_CALL_END_MC_ABORT=31, //MC停止发起呼叫或通话
			CM_CALL_END_RUIM_NOT_PRESENT=34, //RUIM 不存在
			CM_CALL_END_NDSS_FAIL=99, //NDSS错误
			CM_CALL_END_LL_CAUSE=100, //释放来自底层，进一步需要查询cc_cause内容
			CM_CALL_END_CONF_FAILED=101, //主叫呼叫后，网络响应失败
			CM_CALL_END_INCOM_REJ=102, //被叫时，本方拒绝
			CM_CALL_END_SETUP_REJ=103, //呼叫建立过程时候拒绝
			CM_CALL_END_NETWORK_END=104, //释放原因来自网络，进一步需要查询
			CM_CALL_END_NO_FUNDS=105, //话费用完
			CM_CALL_END_NO_GW_SRV=106 //不在服务区
	};

	enum CC_CAUSE {
		UNASSIGNED_CAUSE=1,
		NO_ROUTE_TO_DEST=3,
		CHANNEL_UNACCEPTABLE=6,
		OPERATOR_DETERMINED_BARRING=8,
		NORMAL_CALL_CLEARING=16,
		USER_BUSY=17,
		NO_USER_RESPONDING=18,
		USER_ALERTING_NO_ANSWER=19,
		CALL_REJECTED=21,
		NUMBER_CHANGED=22,
		NON_SELECTED_USER_CLEARING=26,
		DESTINATION_OUT_OF_ORDER=27,
		INVALID_NUMBER_FORMAT=28,
		FACILITY_REJECTED=29,
		RESPONSE_TO_STATUS_ENQUIRY=30,
		NORMAL_UNSPECIFIED=31,
		NO_CIRCUIT_CHANNEL_AVAILABLE=34,
		NETWORK_OUT_OF_ORDER=38,
		TEMPORARY_FAILURE=41,
		SWITCHING_EQUIPMENT_CONGESTION=42,
		ACCESS_INFORMATION_DISCARDED=43,
		REQUESTED_CIRCUIT_CHANNEL_NOT_AVAILABLE=44,
		RESOURCES_UNAVAILABLE_UNSPECIFIED=47,
		QUALITY_OF_SERVICE_UNAVAILABLE=49,
		REQUESTED_FACILITY_NOT_SUBSCRIBED=50,
		INCOMING_CALL_BARRED_WITHIN_CUG=55,
		BEARER_CAPABILITY_NOT_AUTHORISED=57,
		BEARER_CAPABILITY_NOT_PRESENTLY_AVAILABLE=58,
		SERVICE_OR_OPTION_NOT_AVAILABLE=63,
		BEARER_SERVICE_NOT_IMPLEMENTED=65,
		ACM_GEQ_ACMMAX=68,
		REQUESTED_FACILITY_NOT_IMPLEMENTED=69,
		ONLY_RESTRICTED_DIGITAL_INFO_BC_AVAILABLE=70,
		SERVICE_OR_OPTION_NOT_IMPLEMENTED=79,
		INVALID_TRANSACTION_ID_VALUE=81,
		USER_NOT_MEMBER_OF_CUG=87,
		INCOMPATIBLE_DESTINATION=88,
		INVALID_TRANSIT_NETWORK_SELECTION=91,
		SEMANTICALLY_INCORRECT_MESSAGE=95,
		INVALID_MANDATORY_INFORMATION=96,
		MESSAGE_TYPE_NON_EXISTENT=97,
		MESSAGE_TYPE_NOT_COMPATIBLE_WITH_PROT_STATE=98,
		IE_NON_EXISTENT_OR_NOT_IMPLEMENTED=99,
		CONDITIONAL_IE_ERROR=100,
		MESSAGE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE=101,
		RECOVERY_ON_TIMER_EXPIRY=102,
		PROTOCOL_ERROR_UNSPECIFIED=111,
		INTERWORKING_UNSPECIFIED=127,
		REJ_UNSPECIFIE=160,
		AS_REJ_RR_REL_IND=161,
		AS_REJ_RR_RANDOM_ACCESS_FAILURE=162,
		AS_REJ_RRC_REL_IND=163,
		AS_REJ_RRC_CLOSE_SESSION_IND=164,
		AS_REJ_RRC_OPEN_SESSION_FAILURE=165,
		AS_REJ_LOW_LEVEL_FAIL=166,
		AS_REJ_LOW_LEVEL_FAIL_REDIAL_NOT_ALLOWED=167,
		MM_REJ_INVALID_SIM=168,
		MM_REJ_NO_SERVICE=169,
		MM_REJ_TIMER_T3230_EXP=170,
		MM_REJ_NO_CELL_AVAILABLE=171,
		MM_REJ_WRONG_STATE=172,
		MM_REJ_ACCESS_CLASS_BLOCKED=173,
		ABORT_MSG_RECEIVED=174,
		OTHER_CAUSE=175,
		CNM_REJ_TIMER_T303_EXP=176,
		CNM_REJ_NO_RESOURCES=177,
		CNM_MM_REL_PENDING=178,
		CNM_INVALID_USER_DATA=179
	};

	static Modem* CreateInstance(const char* szPort);

	virtual bool Open(const char* szName);
	virtual int WaitATResponse(const char *szWait, int timeout,bool bClear=true);
	virtual bool Init(void);
	virtual bool Dial(const char* szNo,time_t& start,time_t& end);
	virtual bool HungUp(time_t& start);
	virtual bool Answer(time_t& start);
	virtual bool EnableCLIP(bool enable);
	virtual bool GetRSSI(int& rssi,int& ber);
	virtual bool GetREG(int& stat, int& lac, int& ci);
	virtual bool GetCNUM(string& strNo);
	virtual bool SetSMSIndicate(bool enable);
	virtual bool SetSMSFormatPDU(void);
	virtual bool ActivePDP(int cid,int& nDelay);
	virtual bool ReadSMSPDU(int id,int& stat, int& len,string& pdu);
	virtual bool DeleteSMS(int id);
	virtual bool DeleteSMSAll(void);
	virtual bool IndicateDialing(string& strInd,int& id,int& type);
	virtual bool IndicateNetConnected(string& strInd,int& id);
	virtual bool IndicateOffHook(string& strInd,int& id,int& type);
	virtual bool IndicateHungUp(string& strInd,int& id,int& duriation,int& status,int& cause);
	virtual bool IndicateCLIP(string& strInd,string& number,int& type,int& valid);
	virtual bool IndicateRing(string& strInd);
	virtual bool IndicateRSSI(string& strInd, int& rssi,int& der);
	virtual bool IndicateCREG(string& strInd, int& stat,int& lac, int& ci);
	virtual bool IndicateSMessage(string& strInd,string& mem,int& id);
	virtual string WaitIndication(time_t& start,int timeout);
	virtual void CSQ2DBm(int& rssi);
	const char* m_szType;
	string m_strCaller;
protected:
	virtual string chopLine(const char* szLine=NULL);

	string strCache;
	string m_strLastError;
	SerialPort port;
};


#endif /* MODEM_H_ */
