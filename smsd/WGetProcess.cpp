/*
 * WGetProcess.cpp
 *
 *  Created on: Mar 17, 2011
 *      Author: xukh
 */

#include "WGetProcess.h"
#include <time.h>
#include <string>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include "TraceLog.h"
using namespace std;

WGetProcess::WGetProcess() {
	shell = new CmdShell();
	dto = new WGetDTO();
}

WGetProcess::~WGetProcess() {
	delete shell;
	delete dto;
}

char* int2str(const int &i) {
	stringstream stream;
	string str;
	stream << i;
	stream >> str;
	const char* temp = str.c_str();
	size_t lenth = strlen(temp);
	char* ret = (char*) malloc(strlen(temp) + 1);
	memcpy(ret, temp, lenth);
	ret[lenth] = 0;
	return ret;
}

int WGetProcess::Run(const char *szUrl, int timeout) {
	TRACE("WGetProcess::Run!url is:%s!\n", szUrl);
	time_t objCheckTime, objNow;
	time(&objNow);
	time(&objCheckTime);
	//szDev only include url
	this->dto->url = string(szUrl);
	string strExe, strTimeTmp;
	strExe.append("wget ").append(szUrl).append(" --timeout=").append(int2str(
			timeout)).append("\n");
	TRACE("run wget cmd!strExe is:%s!", strExe.c_str());
	NetStatRecorder* pobjNetStatRecorder = NetStatRecorder::getInstance();
	this->m_nLoopCount = pobjNetStatRecorder->m_nLoopCount;
	string strLine;
	shell->m_timeout_sec = timeout;
	shell->Open();
	shell->Write(strExe.c_str());
	unsigned int iPos = 0;
	string strAckType;
	string strTmp, strUrlTmp, strLenTmp;
	double iDeff;
	while (shell->ReadLine(strLine, ' ')) {
		TRACE("WGetProcess::Run cycle!strLineis:%s\n", strLine.c_str());
		if (strLine.find("wget") != string::npos) {
			time(&objNow);
			shell->ReadLine(strLine, '\n');
			if (strLine.find("www.") != string::npos) {
				INFO("Event Wget wget_chat_start\n");
				this->timeToString(strTimeTmp, objNow);
				Event("wget_chat_start", objNow, strTimeTmp.c_str());
			} else {
				TRACE("Can't find wget url param!");
				return 0;
			}
			continue;
		} else if (strLine.find("--2") != string::npos) {
			time(&objNow);
			shell->ReadLine(strLine, '\n');
			continue;
		} else if (strLine.find("Resolving") != string::npos) { //case Resolving
			time(&objNow);
			this->timeToString(strTimeTmp, objNow);
			Event("wget_dns_analysis", objNow, strTimeTmp.c_str());
			INFO("Event Wget wget_dns_analysis\n");

			shell->ReadLine(strLine, '\n');
			TRACE("strLine=%s!", strLine.c_str());
			time(&objNow);
			this->timeToString(strTimeTmp, objNow);
			Event("wget_analysis_ok", objNow, strTimeTmp.c_str());
			INFO("Event Wget wget_analysis_ok\n");

			strUrlTmp = szUrl;
			if (checkUrl(strUrlTmp, strLine) == false) {
				TRACE("Wget!Url changed!return!");
				Event("wget_analysis_failed", objNow, strTimeTmp.c_str());
				INFO("Event Wget wget_analysis_failed\n");
				return -1;
			}
			continue;
		} else if (strLine.find("Connecting") != string::npos) { //case Connecting
			time(&objNow);
			this->timeToString(strTimeTmp, objNow);
			Event("wget_connect_start", objNow, strTimeTmp.c_str());
			INFO("Event Wget wget_connect_start\n");

			shell->ReadLine(strLine, '\n');
			time(&objNow);
			this->timeToString(strTimeTmp, objNow);
			Event("wget_connect_end", objNow, strTimeTmp.c_str());
			INFO("Event Wget wget_connect_end\n");
			TRACE("Connecting following strLine:%s!", strLine.c_str());
			if (strLine.find("refused") != string::npos) {
				Event("wget_connect_refused", objNow, strTimeTmp.c_str());
				INFO("Event Wget wget_connect_refused\n");
				return -1;
			} else if (strLine.find("timed out") != string::npos) {
				Event("wget_connect_timeout", objNow, strTimeTmp.c_str());
				INFO("Event Wget wget_connect_timeout\n");
				return -1;
			}
			continue;
		} else if ((strLine.find("HTTP") != string::npos) || (strLine.find(
				"FTP") != string::npos)) { //case HTTP/FTP request
			shell->ReadLine(strLine, ' ');
			time(&objNow);
			if (strLine.find("request") != string::npos) {
				this->timeToString(strTimeTmp, objNow);
				Event("wget_download_request", objNow, strTimeTmp.c_str());
				INFO("Event Wget wget_download_request\n");
				shell->ReadLine(strLine, '\n');
				iPos = 0;
				strAckType = "";
				if ((iPos = strLine.find("...")) != string::npos) {
					iPos++;
					strAckType = strLine.substr(iPos + 3, 3);
					TRACE("ACK Type str:%s!", strAckType.c_str());
				} else {
					TRACE("Can't find ack Type!");
				}
				time(&objNow);
				this->timeToString(strTimeTmp, objNow);
				Event("wget_download_ack", objNow, strTimeTmp.c_str());
				//				Event("wget_download_ack", objNow, strAckType.c_str());
				INFO("Event Wget wget_download_ack!AckType=%s\n", strAckType.c_str());
			}
			continue;
		} else if (strLine.find("Length:") != string::npos) {
			time(&objNow);
			shell->ReadLine(strLine, '\n');
			strLenTmp = strLine.substr(0);
			if ((iPos = strLenTmp.find(' ')) != string::npos) {
				strLenTmp = strLenTmp.substr(0, iPos);
				INFO("Wget file Length:%s\n", strLenTmp.c_str());
			}
		} else if (strLine.find("Saving") != string::npos) { //case Saving
			time(&objNow);
			this->timeToString(strTimeTmp, objNow);
			Event("wget_download_start", objNow, strTimeTmp.c_str());
			INFO("Event Wget wget_download_start\n");
			shell->ReadLine(strLine, '\n');
			string strNameTmp = strLine.substr(5);
			if ((iPos = strNameTmp.find('\'')) != string::npos) {
				INFO("Wget file name:%s\n", strNameTmp.substr(0, iPos).c_str());
			}
			if (parseDownloadProgress(atoi(strLenTmp.c_str()), timeout) < 0) {
				return -1;
			}
			break;
		} else {
			TRACE("Can't deal with the string:%s!", strLine.c_str());
			time(&objNow);
			iDeff = difftime(objCheckTime, objNow);
			if (iDeff > timeout) {
				this->timeToString(strTimeTmp, objNow);
				//				Event("wget_timeout", objNow, strTimeTmp.c_str());
				Event("wget_timeout", objNow, timeout);
				INFO("Event Wget wget_timeout\n");
				return -1;
			}
			shell->ReadLine(strLine, '\n');
		}
	};
	TRACE("process wget over");
	return 0;
}

int WGetProcess::parseDownloadProgress(const unsigned int iDataLength,
		int iTimeOut) {
	string strProcessLine;
	time_t ObjCheckTime, objNow;
	unsigned int iPos = 0, iDatalenRecv = 0;
	int iPercent = 0;
	bool bDownloadFlag = false;
	double iTimeDiff;
	string strLineTmp, strTimeTmp;
	time(&ObjCheckTime);
	time(&objNow);
	do {
		shell->ReadLine(strProcessLine, '\r');
		TRACE("DownloadLine:%s!", strProcessLine.c_str());
		if ((iPos = strProcessLine.find("%")) != string::npos) {
			iPercent = atoi(strProcessLine.substr(0, iPos).c_str());
			TRACE("Percent str is:%s!",strProcessLine.substr(0, iPos).c_str() );
			time(&objNow);
			Event("wget_download_percent", objNow, iPercent);
			INFO("Event Wget wget_download_percent!Percent=%d\n", iPercent);
		} else {
			TRACE("Can't find percent string!");
			//					break;
		}
		if ((iPos = strProcessLine.find("] ")) != string::npos) {
			string strBytes = "";
			strLineTmp = strProcessLine.substr(iPos + 2);
			if ((iPos = strLineTmp.find(" ")) != string::npos) {
				strBytes = strLineTmp.substr(0, iPos);
				for (string::iterator it = strBytes.begin(); it
						< strBytes.end(); it++) {
					if ((*it) == ',') {
						strBytes.erase(it);
						it--;
					}
				}
				time(&objNow);
				Event("wget_download_bytes", objNow, strBytes.c_str());
				INFO("Event Wget wget_download_bytes!Bytes:%s\n", strBytes.c_str());
				iDatalenRecv = atoi(strBytes.c_str());
			}
		} else {
			TRACE("Can't find download bytes string!");
			//					break;
		}
		if (((iPos = strProcessLine.find("K/s")) != string::npos)) {
			strLineTmp = strProcessLine.substr(iPos - 4, 4);
			for (string::iterator it = strLineTmp.begin(); it
					< strLineTmp.end(); it++) {
				if ((*it) == ' ') {
					strLineTmp.erase(it);
					it--;
				}
			}
			time(&objNow);
			Event("wget_download_speed", objNow, strLineTmp.c_str());
			INFO("Event Wget wget_download_speed!Speed:%sK/s\n", strLineTmp.c_str());
		} else {
			TRACE("Can't find download speed string!");
			//					break;
		}
		if ((strProcessLine.find('\n') != string::npos)) {
			TRACE("Found \\n!");
			if (strProcessLine.find("saved") != string::npos) {
				INFO("Event Wget wget_download_success\n");
				time(&objNow);
				this->timeToString(strTimeTmp, objNow);
				Event("wget_download_success", objNow, strTimeTmp.c_str());
				bDownloadFlag = true;
			} else {
				TRACE("Not wget_download_success string!strLine:%s!", strProcessLine.c_str());
			}
		} else if ((iPercent == 100) || (iDatalenRecv == iDataLength)) {
			INFO("Event Wget wget_download_success\n");
			time(&objNow);
			this->timeToString(strTimeTmp, objNow);
			Event("wget_download_success", objNow, strTimeTmp.c_str());
			bDownloadFlag = true;
		}
		time(&ObjCheckTime);
		iTimeDiff = difftime(ObjCheckTime, objNow);
		if (iTimeDiff > iTimeOut) {
			this->timeToString(strTimeTmp, objNow);
			//			Event("wget_timeout", objNow, strTimeTmp.c_str());
			Event("wget_timeout", objNow, iTimeOut);
			INFO("Event Wget wget_timeout\n");
			return -1;
		}
		TRACE("iDataLength=%d!iDatalenRecv=%d!", iDataLength, iDatalenRecv);
	} while ((iPercent < 100) && (bDownloadFlag == false) && (iDatalenRecv
			< iDataLength));
	//	 while ((iPercent != 100) || (iDatalenRecv != iDataLength));
	return 0;
}

/**
 * check connect url
 */
bool WGetProcess::checkUrl(string & strUrlChecking, string & strUrlLine) {
	TRACE("WGetProcess::checkUrl!");
	unsigned int iPos = 0;
	if ((iPos = strUrlLine.find("failed:")) != string::npos) //remove http header
	{
		TRACE("CheckUrl Failed!%s!\n", strUrlLine.substr(iPos).c_str());
		return false;
	}
	TRACE("strUrlChecking=%s!\nstrUrlLine=%s!", strUrlChecking.c_str(), strUrlLine.c_str());
	if (strUrlChecking.find("http") != string::npos) //remove http header
	{
		strUrlChecking = strUrlChecking.substr(7, strUrlChecking.length() - 7);
	}
	if (strUrlChecking.find("ftp") != string::npos) //remove http header
	{
		strUrlChecking = strUrlChecking.substr(6, strUrlChecking.length() - 6);
	}
	string::size_type loc = 0;
	if ((loc = strUrlChecking.find("\\")) != string::npos)//remove URI
	{
		strUrlChecking = strUrlChecking.substr(0, loc);
	}
	if (strUrlLine.find(strUrlChecking.c_str()) != string::npos) {
		TRACE("strUrlChecking=%s!\nstrUrlLine=%s!", strUrlChecking.c_str(), strUrlLine.c_str());
		return true;
	} else {
		TRACE("strUrlChecking=%s!\nstrUrlLine=%s!", strUrlChecking.c_str(), strUrlLine.c_str());
		return false;
	}
}

