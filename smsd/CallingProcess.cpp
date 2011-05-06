#include "CallingProcess.h"
#include "Modem.h"
#include "TraceLog.h"
#include "stdlib.h"
#include "stdio.h"
#include <sstream>
#include <unistd.h>
// Constructors/Destructors
//  

void print_useage(const char* sz) {
	fprintf(
			stderr,
			"Usage: %s [-a] [-l duriation_secs][-t timeout_secs] [-d] dial_No [-p dev_port]\n",
			sz);
}

int main(int argc, char** argv) {
	CallingProcess call;
	string strBroadIP;
	UDPSender::GetBroadcastAddr(strBroadIP);
	INFO("Broadcast IP:%s",strBroadIP.c_str());
	gTraceLog.Init(argv[0], strBroadIP.c_str(), 9051);
	SETTRACELEVEL(0);
	string strPort;

	strPort = "/dev/ttyUSB2";

	int opt;

	while ((opt = getopt(argc, argv, "al:t:p:d:i:x:n:")) != -1) {
		TRACE("process %c %s", opt,optarg);
		switch (opt) {
		case 'a':
			call.m_bCallOut = false;
			break;
		case 'd':
			call.m_strCallee = optarg;
			break;
		case 't':
			call.m_nTimeout = atoi(optarg);
			INFO("call.m_nTimeout=%d!optarg=%d!", call.m_nTimeout, optarg);
			break;
		case 'l':
			call.m_nDuriation = atoi(optarg);
			break;
		case 'p':
			strPort = optarg;
			break;
		case 'x':
			call.m_strDev = optarg;
			break;
		case 'i':
			call.m_strTaskID = optarg;
			break;
		case 'n':
			call.m_nLoopMax = atoi(optarg);
			break;
		default: /* '?' */
			print_useage(argv[0]);
			exit(EXIT_FAILURE);
		}
	};

	if (call.m_bCallOut && !call.m_strCallee.size()) {
		ERROR("Callee not set");
		print_useage(argv[0]);
		exit(0);
	}

	return call.Run(strPort.c_str(), call.m_nTimeout);
}

CallingProcess::CallingProcess() {
	id = 0;
	type = 0;
	duriation = 0;
	status = 0;
	cause = 0;
	valid = 0;
	m_bCallOut = true;
	m_nTimeout = 60;
	m_nDuriation = 5;
}

CallingProcess::~CallingProcess() {
}

int CallingProcess::Run(const char* szDev, int timeout) {
	Modem& modem = *Modem::CreateInstance(szDev);
	int rt = 0;
	if (&modem) {
		if (m_bCallOut) {
			m_iBusinessType = 1001;
		} else {
			m_iBusinessType = 1002;
		}
		modem.Open(modem.m_szDefaultPort);
		modem.Init();
		m_strDevType = modem.m_szType;

		do {

			if (modem.GetRSSI(m_nRSSI, m_nDER))
				INFO("Signal Level:%ddbm",m_nRSSI);

			modem.GetREG(m_nStat, m_nLAC, m_nCI);
			time_t now;
			time(&now);
			Event("stat", now, m_nStat);
			Event("rssi", now, m_nRSSI);
			Event("lac", now, m_nLAC);
			Event("ci", now, m_nCI);
			if (m_bCallOut)
				rt = Call(modem, timeout);
			else
				rt = Answer(modem, timeout);
			m_nLoopCount++;
		} while (m_nLoopCount < m_nLoopMax);

		delete &modem;
	} else {
		INFO("failed to create modem instance.");
	}
	string strTimeStrTmp;
	time_t now;
	time(&now);
	this->timeToString(strTimeStrTmp, now);
	Event("taskend", now, strTimeStrTmp.c_str());
	return rt;
}

int CallingProcess::Call(Modem& modem, int timeout) {
	time_t start, now;
	int idTmp;
	bool bHangUpFlag = false;
	string strTimeStrTmp;

	answerTime = 0;
	dialingTime = 0;
	connectTime = 0;
	endTime = 0;

	strCaller.clear();
	modem.GetCNUM(strCaller);
	time(&start);

	if (!modem.Dial(m_strCallee.c_str(), startTime, startTime))
		return -1;
	this->timeToString(strTimeStrTmp, startTime);
	Event("dial", startTime, strTimeStrTmp.c_str());
	INFO("timeout=%d!", timeout);
	string ind;
	do {
		time(&now);

		if (!answerTime && ((now - start) > timeout) && bHangUpFlag == false) {
			INFO("Waiting time out!now - start=%d!", now - start);
			Event("answer_time_out", now, m_nTimeout);
			if (true == modem.HungUp(endTime)) {
				bHangUpFlag = true;
			}
			this->timeToString(strTimeStrTmp, endTime);
			Event("hungup", endTime, strTimeStrTmp.c_str());
		}

		if (answerTime && (now - answerTime) > m_nDuriation && bHangUpFlag
				== false) {
			INFO("to hung up");
			DEBUG("delay %d",now - answerTime);
			if (true == modem.HungUp(endTime)) {
				bHangUpFlag = true;
			}
			//			modem.HungUp(endTime);
			this->timeToString(strTimeStrTmp, endTime);
			Event("hungup", endTime, strTimeStrTmp.c_str());
			start = now;
		}

		if (!answerTime && now > start && timeout > (now - start)) {
			//			timeout = timeout - (now - start);
			INFO("timeout decrease!now=%d!", now);
			INFO("timeout decrease!start=%d!", start);
			INFO("Waiting time out!now - start=%d!", now - start);
			DEBUG("timeout decrease %d",timeout);
		}
		//		if (answerTime)
		//			timeout = 1;
		ind = modem.WaitIndication(now, timeout);
		INFO("ind=%s!", ind.c_str());
		if (modem.IndicateDialing(ind, id, type)) {
			INFO("dialing now");
			dialingTime = now;
			this->timeToString(strTimeStrTmp, now);
			Event("dialing", now, strTimeStrTmp.c_str());
			continue;
		}
		if (modem.IndicateRSSI(ind, m_nRSSI, m_nDER)) {
			INFO("IndicateRSSI!");
			Event("rssi", now, m_nRSSI);
			continue;
		}
		if (modem.IndicateNetConnected(ind, idTmp)) {
			INFO("connected");
			if (id == idTmp) {
				INFO("id check!connected");
				connectTime = now;
				this->timeToString(strTimeStrTmp, now);
				Event("connected", now, strTimeStrTmp.c_str());
			}
			continue;
		}
		if (modem.IndicateOffHook(ind, idTmp, type)) {
			INFO("off_hook!");
			if (id == idTmp) {
				answerTime = now;
				INFO("id check!off_hook!");
				this->timeToString(strTimeStrTmp, now);
				Event("off_hook", now, strTimeStrTmp.c_str());
			}
			continue;
		}
		if (modem.IndicateHungUp(ind, idTmp, duriation, status, cause)) {
			INFO("IndicateHungUp!id=%d!idTmp=%d!", id, idTmp);
			if (id == idTmp) {
				endTime = now;
				INFO("id check!IndicateHungUp!");
				this->timeToString(strTimeStrTmp, now);
				Event("disconnected", now, strTimeStrTmp.c_str());
				Event("duriation", now, duriation);
				Event("status", now, status);
				Event("cause", now, cause);
				Event("hungup", now, strTimeStrTmp.c_str());
			}
			break;
		}
		if (modem.IndicateCREG(ind, m_nStat, m_nLAC, m_nCI)) {
			Event("stat", now, m_nStat);
			Event("lac", now, m_nLAC);
			Event("ci", now, m_nCI);
		}
	} while (1);
	INFO("Quit main cycle!");
	return 1;
}

int CallingProcess::Answer(Modem& modem, int timeout) {
	time_t start, now;
	int idTmp;
	string strTimeStrTmp;

	answerTime = 0;
	dialingTime = 0;
	connectTime = 0;
	endTime = 0;
	ringTime = 0;

	modem.GetCNUM(m_strCallee);
	modem.EnableCLIP(true);
	time(&start);
	string ind;
	do {
		time(&now);
		this->timeToString(strTimeStrTmp, now);

		if (connectTime && (now - connectTime > m_nDuriation)) {
			modem.HungUp(endTime);
			Event("hungup", endTime, strTimeStrTmp.c_str());
		}

		if (!answerTime && ((now - start) > timeout)) {
			INFO("Waiting time out");
			Event("wait_timeout", now, m_nTimeout);
			return 0;
		}

		if (!answerTime && now > start && timeout > (now - start))
			timeout = timeout - (now - start);
		if (connectTime && answerTime)
			timeout = 1;
		ind = modem.WaitIndication(now, timeout);
		TRACE("Get %s",ind.c_str());
		if (modem.IndicateRing(ind)) {
			ringTime = now;
			Event("ring", now, strTimeStrTmp.c_str());
			INFO("Ringing...");
			continue;
		}
		if (modem.IndicateRSSI(ind, m_nRSSI, m_nDER)) {
			Event("rssi", now, m_nRSSI);
			continue;
		}
		if (modem.IndicateCLIP(ind, strCaller, type, valid)) {
			//			INFO("Call from %s",strCaller.c_str());
			//			Event("caller", now, strCaller.c_str());
			if (modem.Answer(connectTime)) {
				INFO("Answer the call");
				Event("answer", connectTime, 1);
				continue;
			}
			ERROR("Can not answer the call");
			time(&now);
			Event("answer", now, 0);
			return -1;
		}
		if (modem.IndicateOffHook(ind, id, type)) {
			answerTime = now;
			Event("connected", now, strTimeStrTmp.c_str());
			INFO("Connected.");
			Event("off_hook", now, strTimeStrTmp.c_str());
			INFO("off_hook.");
			continue;
		}

		if (modem.IndicateHungUp(ind, idTmp, duriation, status, cause)) {
			if (id == idTmp) {
				endTime = now;
				Event("disconnected", now, strTimeStrTmp.c_str());
				Event("duriation", now, duriation);
				Event("status", now, status);
				Event("cause", now, cause);
			}
			INFO("Hungup.");
			break;
		}
		if (modem.IndicateCREG(ind, m_nStat, m_nLAC, m_nCI)) {
			Event("stat", now, m_nStat);
			Event("lac", now, m_nLAC);
			Event("ci", now, m_nCI);
		}

	} while (1);
	return 1;
}

int CallingProcess::Report(void) {
	return 0;
}
