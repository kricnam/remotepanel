#include "CmdShell.h"
#include <pty.h>
#include <unistd.h>
#include <utmp.h>
#include "TraceLog.h"
#include "NetStatProcess.h"
#include <syslog.h>
#include <stdlib.h>

NetStatRecorder* NetStatRecorder::m_pobjNetStatRecorder = NULL;

NetStatProcess::NetStatProcess() {

}

NetStatProcess::~NetStatProcess() {

}

int NetStatProcess::Run(const char*, int iLoop) {
	syslog(LOG_INFO, "NetStatProcess::GetNetStat!");
	TRACE("NetStatProcess::GetNetStat!\n");
	this->m_nLoopCount = iLoop;
	NetStatRecorder* pobjNetStatRec = NetStatRecorder::getInstance();

	CmdShell shell;
	shell.Open();
	shell.Write("netstat -ns\n");
	string strLine;
	while (shell.ReadLine(strLine, '\n')) {
		//		syslog(LOG_INFO, "OutputLine is:%s\n", strLine.c_str());
		if (true == parseNetStats(strLine)) {
			//			syslog(LOG_INFO, "Got!strLine=%s!", strLine.c_str());
			TRACE("Got!strLine=%s!", strLine.c_str());
		} else {
			//			TRACE("Discard!strLine=%s!", strLine.c_str());
		}
	};
	pobjNetStatRec->m_bNetStatFlag = true;
	shell.Write("\0x03");
	return 1;
}

bool NetStatProcess::parseNetStats(string strOutputLine) {
	syslog(LOG_INFO, "NetStatProcess::parseNetStats!");
	//	TRACE("Output is:%s!", strOutputLine.c_str());
	NetStatRecorder* pobjNetStatRec = NetStatRecorder::getInstance();
	string strTmp;
	int iNewValue = 0;
	time_t objNow;
	string::size_type iPos = 0;
	if ((iPos = strOutputLine.find("total packets received")) != string::npos) {
		strTmp = strOutputLine.substr(0, iPos - 1);
		//		TRACE("strTmp=%s!\n", strTmp.c_str());
		iNewValue = atoi(strTmp.c_str());
		if (pobjNetStatRec->m_bNetStatFlag) {
			time(&objNow);
			Event("recv_packets_ip", objNow, iNewValue
					- pobjNetStatRec->m_iPacketsRecvIp);
			//			TRACE("iNewValue=%d!\n");
			INFO("Event NetStat recv_packets_ip=%d\n", iNewValue
					- pobjNetStatRec->m_iPacketsRecvIp);
		}
		pobjNetStatRec->m_iPacketsRecvIp = iNewValue;
		return true;
	} else if (((iPos = strOutputLine.find("segments received"))
			!= string::npos) && ((iPos = strOutputLine.find("bad"))
			== string::npos)) {
		strTmp = strOutputLine.substr(0, iPos - 1);
		//		TRACE("strTmp=%s!\n", strTmp.c_str());
		iNewValue = atoi(strTmp.c_str());
		if (pobjNetStatRec->m_bNetStatFlag) {
			time(&objNow);
			Event("recv_segments_tcp", objNow, iNewValue
					- pobjNetStatRec->m_iSegmentsRecvTcp);
			//			TRACE("iNewValue=%d!\n");
			INFO("Event NetStat recv_segments_tcp=%d\n", iNewValue
					- pobjNetStatRec->m_iSegmentsRecvTcp);
		}
		pobjNetStatRec->m_iSegmentsRecvTcp = iNewValue;
		return true;
	} else if ((iPos = strOutputLine.find("segments send out")) != string::npos) {
		strTmp = strOutputLine.substr(0, iPos - 1);
		//		TRACE("strTmp=%s!\n", strTmp.c_str());
		iNewValue = atoi(strTmp.c_str());
		if (pobjNetStatRec->m_bNetStatFlag) {
			time(&objNow);
			Event("sent_segments_tcp", objNow, iNewValue
					- pobjNetStatRec->m_iSegmentsSentTcp);
			//			TRACE("iNewValue=%d!\n");
			INFO("Event NetStat sent_segments_tcp=%d\n", iNewValue
					- pobjNetStatRec->m_iSegmentsSentTcp);
		}
		pobjNetStatRec->m_iSegmentsSentTcp = iNewValue;
		return true;
	} else if ((iPos = strOutputLine.find("segments retransmited"))
			!= string::npos) {
		strTmp = strOutputLine.substr(0, iPos - 1);
		//		TRACE("strTmp=%s!\n", strTmp.c_str());
		iNewValue = atoi(strTmp.c_str());
		if (pobjNetStatRec->m_bNetStatFlag) {
			time(&objNow);
			Event("retransmited_segments_tcp", objNow, iNewValue
					- pobjNetStatRec->m_iSegmentsRetransmitedTcp);
			//			TRACE("iNewValue=%d!\n");
			INFO("Event NetStat retransmited_segments_tcp=%d\n", iNewValue
					- pobjNetStatRec->m_iSegmentsRetransmitedTcp);
		}
		pobjNetStatRec->m_iSegmentsRetransmitedTcp = iNewValue;
		return true;
	} else if ((iPos = strOutputLine.find("bad segments received"))
			!= string::npos) {
		strTmp = strOutputLine.substr(0, iPos - 1);
		//		TRACE("strTmp=%s!\n", strTmp.c_str());
		iNewValue = atoi(strTmp.c_str());
		if (pobjNetStatRec->m_bNetStatFlag) {
			time(&objNow);
			Event("bad_segments_tcp", objNow, iNewValue
					- pobjNetStatRec->m_iBadSegmentsRecvTcp);
			//			TRACE("iNewValue=%d!\n");
			INFO("Event NetStat bad_segments_tcp=%d\n", iNewValue
					- pobjNetStatRec->m_iBadSegmentsRecvTcp);
		}
		pobjNetStatRec->m_iBadSegmentsRecvTcp = iNewValue;
		return true;
	} else if (((iPos = strOutputLine.find("packets received")) != string::npos)
			&& (strOutputLine.find("total") == string::npos)) {
		strTmp = strOutputLine.substr(0, iPos - 1);
		//		TRACE("strTmp=%s!\n", strTmp.c_str());
		iNewValue = atoi(strTmp.c_str());
		if (pobjNetStatRec->m_bNetStatFlag) {
			time(&objNow);
			Event("recv_packets_udp", objNow, iNewValue
					- pobjNetStatRec->m_iPacketsRecvUdp);
			//			TRACE("iNewValue=%d!\n");
			INFO("Event NetStat recv_packets_udp=%d\n", iNewValue
					- pobjNetStatRec->m_iPacketsRecvUdp);
		}
		pobjNetStatRec->m_iPacketsRecvUdp = iNewValue;
		return true;
	} else if ((iPos = strOutputLine.find("packets sent")) != string::npos) {
		strTmp = strOutputLine.substr(0, iPos - 1);
		//		TRACE("strTmp=%s!\n", strTmp.c_str());
		iNewValue = atoi(strTmp.c_str());
		if (pobjNetStatRec->m_bNetStatFlag == true) {
			time(&objNow);
			Event("sent_packets_udp", objNow, iNewValue
					- pobjNetStatRec->m_iPacketsSentUdp);
			//			TRACE("iNewValue=%d!\n");
			INFO("Event NetStat sent_packets_udp=%d\n", iNewValue
					- pobjNetStatRec->m_iPacketsSentUdp);
		}
		pobjNetStatRec->m_iPacketsSentUdp = iNewValue;
		return true;
	} else if ((iPos = strOutputLine.find("InOctets:")) != string::npos) {
		strTmp = strOutputLine.substr(iPos + strlen("InOctets:"));
		//		TRACE("strTmp=%s!\n", strTmp.c_str());
		iNewValue = atoi(strTmp.c_str());
		if (pobjNetStatRec->m_bNetStatFlag == true) {
			time(&objNow);
			Event("in_octets_total", objNow, iNewValue
					- pobjNetStatRec->m_iInOctets);
			//			TRACE("iNewValue=%d!\n");
			INFO("Event NetStat in_octets_total=%d\n", iNewValue
					- pobjNetStatRec->m_iInOctets);
		}
		pobjNetStatRec->m_iInOctets = iNewValue;
		return true;
	} else if ((iPos = strOutputLine.find("OutOctets:")) != string::npos) {
		strTmp = strOutputLine.substr(iPos + strlen("OutOctets:"));
		//		TRACE("strTmp=%s!\n", strTmp.c_str());
		iNewValue = atoi(strTmp.c_str());
		if (pobjNetStatRec->m_bNetStatFlag == true) {
			time(&objNow);
			Event("out_octets_total", objNow, iNewValue
					- pobjNetStatRec->m_iOutOctets);
			//			TRACE("iNewValue=%d!\n");
			INFO("Event NetStat out_octets_total=%d\n", iNewValue
					- pobjNetStatRec->m_iOutOctets);
		}
		pobjNetStatRec->m_iOutOctets = iNewValue;
		return true;
	} else {
		//		syslog(LOG_INFO, "Can't find this line!");
		return false;
	}
}

void* NetStatProcess::NetStatCycle(void* pobjNetStat) {
	syslog(LOG_INFO, "NetStatProcess::NetStatCycle!");
	TRACE("NetStatProcess::NetStatCycle!\n");
	NetStatProcess* pobjNetStatus = (NetStatProcess*) pobjNetStat;
	while (true) {
		sleep(3);
		syslog(LOG_INFO, "Get NetStat data!");
		TRACE("Get NetStat data!\n");
		pobjNetStatus->Run(pobjNetStatus->m_strDev.c_str(),
				pobjNetStatus->m_nLoopCount);
	}
	return NULL;
}

NetStatRecorder::NetStatRecorder() {
	m_iPacketsRecvIp = 0;
	m_iSegmentsRecvTcp = 0;
	m_iSegmentsSentTcp = 0;
	m_iBadSegmentsRecvTcp = 0;
	m_iSegmentsRetransmitedTcp = 0;
	m_iPacketsRecvUdp = 0;
	m_iPacketsSentUdp = 0;
	m_iInOctets = 0;
	m_iOutOctets = 0;
	m_bNetStatFlag = false;

}

NetStatRecorder::~NetStatRecorder() {

}
