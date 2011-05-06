/*
 * WAPProcess.cpp
 *
 *  Created on: 2011-3-11
 *      Author: mxx
 */

#include "WAPProcess.h"
#include "WGetProcess.h"
#include "CmdShell.h"
#include "NetStatProcess.h"
#include "Modem.h"
#include "TraceLog.h"
#include <stdlib.h>
#include <stdio.h>
#include "PPPDMessageParser.h"
#include <sstream>
#include <unistd.h>
#include <pty.h>
#include <pthread.h>

void print_useage(const char* sz) {
	fprintf(stderr, "Usage: %s \n", sz);
}

int main(int argc, char** argv) {
	setlocale(LC_MESSAGES, "en");
	WAPProcess wap;
	string strBroadIP;
	UDPSender::GetBroadcastAddr(strBroadIP);
	INFO("Broadcast IP:%s",strBroadIP.c_str());
	gTraceLog.Init(argv[0], strBroadIP.c_str(), 9051);
	SETTRACELEVEL(0);
	string strPort;
	int timeout = 0;

	strPort = "/dev/ttyUSB2";
	INFO("Com Port:%s\n",strPort.c_str());

	int opt;

	while ((opt = getopt(argc, argv, "al:t:p:d:i:x:n:u:")) != -1) {
		TRACE("process %c %s", opt,optarg);
		switch (opt) {
		case 'a':
			break;
		case 'x':
			TRACE("case x!");
			wap.m_strDev = optarg;
			break;
		case 'i':
			TRACE("case i!");
			wap.m_strTaskID = optarg;
			break;
		case 'n':
			TRACE("case n!");
			wap.m_nLoopMax = atoi(optarg);
			break;
		case 't':
			TRACE("case t!");
			timeout = atoi(optarg);
			break;
		case 'u':
			TRACE("case u!");
			wap.m_strUrl = optarg;
			TRACE("getopt u: optarg=%s!m_strUrl=%s!\n", optarg, wap.m_strUrl.c_str());
			break;
		default: /* '?' */
			TRACE("case none!optarg=%d!", optarg);
			print_useage(argv[0]);
			exit(EXIT_FAILURE);
		}
	};
	int iLoopNum = 0;
	SerialPort PortPPP;
	for (iLoopNum = 0; iLoopNum < wap.m_nLoopMax; iLoopNum++) {
		if(PortPPP.Open("/dev/ttyUSB0") >= 0){
			TRACE("Open PPP Port success!");
			PortPPP.Close();
		}else{
			TRACE("Open PPP Port false!");
		}

		wap.Run(strPort.c_str(), timeout);
		if (iLoopNum < (wap.m_nLoopMax - 1)) {
			sleep(6);
		}
		TRACE("iLoopNum=%d!", iLoopNum);
	}
	return 0;
}

WAPProcess::WAPProcess() {
	// TODO Auto-generated constructor stub
	m_nLoopCount = 0;
}

WAPProcess::~WAPProcess() {
	// TODO Auto-generated destructor stub
}

int WAPProcess::Run(const char* szDev, int timeout) {
	TRACE("WAPProcess::Run!this->m_nLoopCount=%d!", this->m_nLoopCount);
	char const* ppp_argv[4] = { "/usr/sbin/pppd", "call", "test", NULL };
	bool bNetStatThreadFlag = false;
	bool bWGetFlag = false;
	bool bPPPConnectFlag = false;
	bool bPPPDisconnectFlag = false;
	bool bPPPLinkFlag = false;
	CmdShell shell;
	PPPDMessageParser parser;
	string strLine, strPPPStatus, strCache, strTimeStrTmp;
	time_t now;
	WGetProcess objWget;
	NetStatProcess netstat;
	if (this->m_strUrl.length() > 4) {
		m_iBusinessType = 1004;
	} else {
		m_iBusinessType = 1005;
	}
	netstat.m_iBusinessType = m_iBusinessType;
	objWget.m_iBusinessType = m_iBusinessType;
	objWget.m_strTaskID = m_strTaskID;
	objWget.m_strDev = m_strDev;
	pthread_t objNetStatPid;
	NetStatRecorder* pobjNetStatRecorder = NetStatRecorder::getInstance();
	pobjNetStatRecorder->m_nLoopCount = this->m_nLoopCount;
	netstat.m_strDev = m_strDev;
	netstat.m_strTaskID = m_strTaskID;

	Modem& modem = *Modem::CreateInstance(szDev);
	if (&modem) {
		modem.Open(szDev);
		modem.Init();
		m_strDevType = modem.m_szType;

		if (modem.GetRSSI(m_nRSSI, m_nDER)) {
			INFO("Signal Level:%ddbm\n",m_nRSSI);
		}

		modem.GetREG(m_nStat, m_nLAC, m_nCI);
		time(&now);
		INFO("Event Start rssi=%d\n", m_nRSSI);
		Event("rssi", now, m_nRSSI);
		INFO("Event Start lac=%d\n", m_nLAC);
		Event("lac", now, m_nLAC);
		INFO("Event Start ci=%d\n", m_nCI);
		Event("ci", now, m_nCI);

		delete &modem;
	} else {
		INFO("failed to create modem instance.");
	}
	sleep(3);
	shell.m_argv = (char* const *) ppp_argv;

	if (!shell.Open(shell.m_argv[0]))
		return 0;

	enum PPPDMessageParser::PPP_MSG msg, lastmsg;
	while (shell.ReadLine(strLine, '\n') >= 0) {
		time(&now);
		this->timeToString(strTimeStrTmp, now);
		msg = parser.Parse(strLine, strCache);

		if (msg == lastmsg)
			continue; //bypass duplicate message
		lastmsg = msg;

		switch (msg) {
		case PPPDMessageParser::PPP_CONNECTED:
			INFO("Event PPP connect\n");
			Event("chat_connect", now, strTimeStrTmp.c_str());
			break;
		case PPPDMessageParser::CHAT_DIAL:
			INFO("Event PPP dialing\n");
			Event("chat_dial", now, strTimeStrTmp.c_str());
			break;
		case PPPDMessageParser::CHAT_START:
			INFO("Event PPP Starting\n");
			Event("chat_start", now, strTimeStrTmp.c_str());
			break;
		case PPPDMessageParser::AUTH_SUCCESS:
			INFO("Event PPP auth_success\n");
			Event("auth_success", now, strTimeStrTmp.c_str());
			break;
		case PPPDMessageParser::NEGOTIAT_SUCCESS:
			INFO("Event PPP negotiat_success\n");
			bPPPConnectFlag = true;
			Event("negotiat_success", now, strTimeStrTmp.c_str());
			break;
		case PPPDMessageParser::PPP_DISCONNECT:
			INFO("Event PPP ppp_disconnect\n");
			bPPPDisconnectFlag = true;
			Event("ppp_disconnect", now, strTimeStrTmp.c_str());
			break;
		case PPPDMessageParser::PPP_DELAYED:
			INFO("Event PPP PPP_DELAYED\n");
			strPPPStatus = "DELAYED";
			break;
		case PPPDMessageParser::PPP_BUSY:
			INFO("Event PPP PPP_BUSY\n");
			strPPPStatus = "BUSY";
			break;
		case PPPDMessageParser::PPP_NO_CARRIER:
			INFO("Event PPP PPP_NO_CARRIER\n");
			strPPPStatus = "NO CARRIER";
			break;
		case PPPDMessageParser::PPP_ERROR:
			INFO("Event PPP PPP_ERROR\n");
			strPPPStatus = "ERROR";
			break;
		case PPPDMessageParser::PPP_FAIL:
			INFO("Event PPP ppp_fail!Status:%s\n", strPPPStatus.c_str());
			Event("ppp_fail", now, strPPPStatus.c_str());
			if ((this->m_nLoopCount + 1) == this->m_nLoopMax) {
				time(&now);
				Event("taskend", now, strTimeStrTmp.c_str());
			} else {
				this->m_nLoopCount++;
			}
			return -1;
		default:
			break;
		}

		if (this->m_strUrl.length() > 4) {
			//if pppd ok
			if ((bNetStatThreadFlag == false) && (bPPPConnectFlag == true)) {
				TRACE("Init NetStat Recorder!\n");
				netstat.Run(szDev, this->m_nLoopCount);
				TRACE("Create NetStat Cycle!\n");
				if (0 != pthread_create(&objNetStatPid, NULL,
						NetStatProcess::NetStatCycle, &netstat)) {
					TRACE("Create NetStat pthread Error!\n");
					return -1;
				} else {
					TRACE("Create NetStat pthread Success!\n");
					bNetStatThreadFlag = true;
				}
			}
			// wget
			if ((bNetStatThreadFlag == true) && (bPPPConnectFlag == true)
					&& (bWGetFlag == false)) {
				TRACE("Start Wget!\n");
				if (objWget.Run(this->m_strUrl.c_str(), timeout) < 0) {
					TRACE("Run Wget failed!");
					pobjNetStatRecorder->m_bNetStatFlag = false;
					pthread_cancel(objNetStatPid);
					if (this->m_nLoopCount == this->m_nLoopMax) {
						time(&now);
						Event("taskend", now, strTimeStrTmp.c_str());
					} else {
						this->m_nLoopCount++;
					}
					return -1;
				} else {
					TRACE("Run Wget Success!kill pppd!");
					//				shell.Write("\0x03");
					system("killall pppd");
					//				break;
				}
				bWGetFlag = true;
			}
			//try disconnect event
			if ((bNetStatThreadFlag == true) && (bPPPDisconnectFlag == true)) {
				pobjNetStatRecorder->m_bNetStatFlag = false;
				pthread_cancel(objNetStatPid);
				break;
			}
		} else {
			if ((bPPPConnectFlag == true) && (bPPPLinkFlag == false)) {
				TRACE("Download site is NULL!kill pppd!");
				system("killall pppd");
				bPPPLinkFlag = true;
			}
			if (bPPPDisconnectFlag == true) {
				break;
			}
		}
	};
	//	sleep(3);
	if ((this->m_nLoopCount + 1) == this->m_nLoopMax) {
		time(&now);
		Event("taskend", now, strTimeStrTmp.c_str());
		INFO("Event taskend");
	} else {
		TRACE("Not end!m_nLoopCount=%d!m_nLoopMax=%d!", m_nLoopCount, m_nLoopMax);
		this->m_nLoopCount++;
	}
	return 0;
}
