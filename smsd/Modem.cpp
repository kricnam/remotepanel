/*
 * Modem.cpp
 *
 *  Created on: 2011-02-15
 *      Author: mxx
 */
#include "SerialPort.h"
#include "Modem.h"
#include "TraceLog.h"
#include <sys/time.h>
#include <stdlib.h>
#include <stdio.h>
/* MODEM_H_ */
//The modem implementation based on EM770W module
//other module define the difference in derived class
#include "EM770WModem.h"
#include "MU203Modem.h"

#ifdef TEST_MODEM
#include "pdu.h"
#include "SMSPDUHead.h"
int main(int argc,char** argv)
{
	SETTRACELEVEL(0);
	setlocale(LC_ALL, "");

	Modem& modem = *Modem::CreateInstance("/dev/ttyUSB2");
	modem.Open(modem.m_szDefaultPort);
	modem.Init();

	if (modem.SetSMSFormatPDU()==false)
	{
		ERROR("Setting PDU mode fail.");
		return 1;
	}
	if (!modem.SetSMSIndicate(true))
	{
		ERROR("Setting SMS Indicate fail");
		return 1;
	}
	string ind,strpdu,mem;
	time_t now;
	int id,stat,len;
	modem.GetCNUM(mem);
	TRACE("%s",mem.c_str());
	do
	{
		ind = modem.WaitIndication(now,30);
		TRACE("%s",ind.c_str());
		if (modem.IndicateSMessage(ind,mem,id))
		{
			INFO("Get %s",ind.c_str());
			if (modem.ReadSMSPDU(id,stat,len,strpdu))
			{
				INFO("Get %d %d,[%d]%s",stat,len,strpdu.size(),strpdu.c_str());
				PDU pdu(strpdu.c_str());
				if (!pdu.parse())
				ERROR("parse pdu error");
				else
				{
					printf("PDU: %s\n", pdu.getPDU());
					printf("SMSC: %s\n", pdu.getSMSC());
					printf("Sender: %s\n", pdu.getNumber());
					printf("Sender Number Type: %s\n", pdu.getNumberType());
					printf("Date: %s\n", pdu.getDate());
					printf("Time: %s\n", pdu.getTime());
					printf("UDH Type: %s\n", pdu.getUDHType());

					char* tmp = (char*)pdu.getMessage();
					DUMP(tmp,pdu.getMessageLen());
					int n=0;
					while(n<pdu.getMessageLen())
					{
						if (tmp[n] && isprint(tmp[n])) printf("%c",tmp[n]);
						else puts(" ");
						n++;
					}

					printf("UDH Data: %s\n", pdu.getUDHData());
					printf("Message: %s\n", pdu.getMessage());
					printf("Message Len:%d\n",pdu.getMessageLen());
					string tmpB = pdu.hex2bin(pdu.getUDHData());
					DUMP(tmpB.c_str(),tmpB.size());
					SMSPDUHead head(tmpB.c_str());
					int org=0,des=0;
					head.GetPortNumber(org,des);
					TRACE("%d,%d",org,des);

				}

			}
		}
	}while(1);
	INFO("OVER");
	return 0;
}
#endif

Modem::Modem() {
}

Modem::~Modem() {
}

Modem* Modem::CreateInstance(const char* szName) {
	SerialPort com;
	if (com.Open(szName) >= 0) {
		TRACE("Modem::CreateInstance!Open Com success!");
		com.Write("ATI\r\n");
		char buf[256];
		string strCache;

		com.SetTimeOut(1000000);
		struct timeval now, start, diff;
		gettimeofday(&start, 0);
		do {
			int n = com.Read(buf, 256);
			if (n > 0) {
				if (n < 255)
					buf[n] = 0;
				TRACE("Expect:%s [R->%s","OK",buf);
				strCache.append(buf, n);
			}
			if (strCache.find("OK") != string::npos) {
				TRACE("Found OK!");
				com.Close();
				if (strCache.find("EM770") != string::npos) {
					return (Modem*) new EM770WModem;
				}else if(strCache.find("MU203") != string::npos){
					return (Modem*) new MU203Modem;
				}
			}
			if (strCache.find("ERROR:") != string::npos) {
				com.Close();
				INFO("can not find device");
				return NULL;
			}

			gettimeofday(&now, 0);
			timersub(&now,&start,&diff);
			if (diff.tv_sec > 10) {
				TRACE("Timeout!");
				break;
			}
		} while (1);
	} else {
		ERROR("Modem::CreateInstance!Can't open Com!");
	}
	return NULL;
}

bool Modem::Open(const char* szName) {
	if (port.Open(szName) < 0)
		return false;
	else
		return true;
}

int Modem::WaitATResponse(const char *szWait, int timeout, bool bClear) {
	INFO("Modem::WaitATResponse!");
	char buf[256];
	if (bClear)
		strCache.clear();

	port.SetTimeOut(1000);
	struct timeval now, start, diff;
	gettimeofday(&start, 0);
	do {
		int n = port.Read(buf, 256);
		if (n > 0) {
			if (n < 255)
				buf[n] = 0;
			TRACE("Expect:%s [R->%s",szWait,buf);
			strCache.append(buf, n);
		}
		if (strCache.find(szWait) != string::npos)
			return 1;
		if (strCache.find("ERROR:") != string::npos)
			return 0;

		gettimeofday(&now, 0);
		timersub(&now,&start,&diff);
		if (diff.tv_sec > timeout) {
			TRACE("time out");
			return 0;
		}
	} while (1);
}

string Modem::WaitIndication(time_t& startIN, int timeout) {
	char buf[256];
	string tmp;
	time(&startIN);
	while (strCache.size()) {
		tmp = chopLine();
		if (tmp.find("OK") != string::npos || tmp.find("ERR") != string::npos)
			continue;
		else
			break;
	}
	if (tmp.size())
		return tmp;

	port.SetTimeOut(1000);
	struct timeval now, start, diff;
	gettimeofday(&start, 0);
	do {
		int n = port.Read(buf, 256);
		if (n > 0) {
			if (n < 255)
				buf[n] = 0;
			TRACE("[Read]->%s",buf);
			strCache.append(buf, n);
		}

		tmp = chopLine();
		if (tmp.size())
			break;

		gettimeofday(&now, 0);
		timersub(&now,&start,&diff);
		if (diff.tv_sec > timeout)
			break;
	} while (1);
	time(&startIN);
	return tmp;
}

string Modem::chopLine(const char* szLine) {
	string::size_type start;

	if (szLine)
		start = strCache.find(szLine);
	else
		start = 0;
	string::size_type end = strCache.find("\n", start);
	string tmp;
	if (end == string::npos)
		return tmp;
	if (end == 0) {
		strCache.erase(0, 1);
		return tmp;
	}
	if ((start != string::npos) && (end > start)) {
		if (strCache[end - 1] == '\r')
			tmp = strCache.substr(start, end - start - 1);
		else
			tmp = strCache.substr(start, end - start);

		TRACE("chop %s",tmp.c_str());
		strCache.erase(0, end + 1);
	}

	return tmp;
}

bool Modem::Init(void) {
	if (port.IsOpen()) {
		port.Write("ATE0\r\n");
		if (!WaitATResponse("OK", 10))
			return false;

		port.Write("ATV1\r\n");
		if (!WaitATResponse("OK", 10))
			return false;

		return true;
	}
	return false;
}

bool Modem::Dial(const char* szNo, time_t& start, time_t& end) {
	INFO("Calling %s",szNo);
	m_strLastError.clear();
	if (port.IsOpen()) {
		string strAT = "ATD";
		strAT += szNo;
		strAT += ";\r\n";
		if (port.Write(strAT.c_str(), strAT.size()) < 0) {
			INFO("Dialing %s command failed",strAT.c_str());
			return false;
		}
		time(&start);
		if (WaitATResponse("OK", 10) > 0) {
			time(&end);
			return true;
		} else {
			m_strLastError = strCache;
			INFO("Calling time out");
		}
	}
	INFO("device  not opened");
	return false;
}

bool Modem::HungUp(time_t& start) {
	if (port.IsOpen()) {
		string strAT = "AT+CHUP\r\n";
		if (port.Write(strAT.c_str(), strAT.size()) < 0) {
			INFO("%s failed",strAT.c_str());
			return false;
		}
		time(&start);
		if (WaitATResponse("OK", 10) > 0) {
			INFO("HangUp success!");
			m_strLastError.clear();
			return true;
		} else {
			INFO("HangUp failed!No Response!");
			m_strLastError = strCache;
			return false;
		}
	}
	INFO("device  not opened");
	return false;
}

bool Modem::Answer(time_t& start) {
	if (port.IsOpen()) {
		string strAT = "ATA\r\n";
		if (port.Write(strAT.c_str(), strAT.size()) < 0) {
			INFO("%s failed",strAT.c_str());
			return false;
		}
		time(&start);
		if (WaitATResponse("OK", 10) > 0) {
			m_strLastError.clear();
			return true;
		} else {
			m_strLastError = strCache;
			return false;
		}
	}
	INFO("device  not opened");
	return false;
}

bool Modem::EnableCLIP(bool enable) {
	if (port.IsOpen()) {
		char szAT[20] = { 0 };
		sprintf(szAT, "AT+CLIP=%d\r\n", enable ? 1 : 0);
		if (port.Write(szAT) < 0) {
			INFO("%s failed",szAT);
			return false;
		}

		if (WaitATResponse("OK", 10) > 0) {
			m_strLastError.clear();
			return true;
		} else {
			m_strLastError = strCache;
			return false;
		}
	}
	TRACE("device  not opened");
	return false;
}

bool Modem::GetRSSI(int& rssi, int& ber) {
	if (port.IsOpen()) {
		char szAT[] = "AT+CSQ\r\n";
		if (port.Write(szAT) < 0) {
			INFO("%s failed",szAT);
			return false;
		}

		if (WaitATResponse("OK", 10) > 0) {
			string tmp;
			tmp = chopLine("+CSQ:");
			sscanf(tmp.c_str(), "+CSQ:%d,%d", &rssi, &ber);
			TRACE("CSQ:%d",rssi);
			CSQ2DBm(rssi);
			m_strLastError.clear();
			TRACE("RSSI:%d",rssi);
			return true;
		}
	}
	return false;
}

bool Modem::GetREG(int& stat, int& lac, int& ci) {
	if (port.IsOpen()) {
		if (port.Write("AT+CREG=2\r\n") < 0) {
			INFO("set creg failed");
			return false;
		}

		if (WaitATResponse("OK", 10) > 0) {
			if (port.Write("AT+CREG?\r\n") < 0) {
				INFO("Read creg failed");
				return false;
			}

			if (WaitATResponse("OK", 10) > 0) {
				string tmp;
				tmp = chopLine("+CREG:");
				int n;
				sscanf(tmp.c_str(), "+CREG:%d,%d,%X,%X", &n, &stat, &lac, &ci);
				TRACE("lac=%d,ci=%d",lac,ci);
				return true;
			}
		}
	}
	return false;
}

bool Modem::GetCNUM(string& strNo) {
	if (port.IsOpen()) {
		char szAT[] = "AT+CNUM\r\n";
		if (port.Write(szAT) < 0) {
			INFO("%s failed",szAT);
			return false;
		}

		if (WaitATResponse("OK", 10) > 0) {
			char szBuf[128] = { 0 };
			char *pchar;

			string tmp;
			tmp = chopLine("+CNUM:");

			sscanf(tmp.c_str(), "+CNUM:%s", szBuf);
			TRACE("szBuf=%s",szBuf);
			strtok(szBuf, ",");
			pchar = strtok(NULL, ",");
			if (pchar)
				strNo = pchar;
			else {
				WARNING("can not get cnum");
				return false;
			}
			string::size_type t = strNo.find("\"");
			if (t != string::npos)
				strNo.erase(t, 1);
			t = strNo.find("\"");
			if (t != string::npos)
				strNo.erase(t, 1);

			m_strLastError.clear();
			TRACE("CNUM:%s",strNo.c_str());
			return true;
		}
	}
	return false;
}

bool Modem::ActivePDP(int cid, int& nDelay) {
	if (port.IsOpen()) {
		char szAT[48] = { 0 };
		time_t start, now;
		sprintf(szAT, "AT+CGACT=1,%d\r\n", cid);
		time(&start);
		if (port.Write(szAT) < 0) {
			INFO("%s failed",szAT);
			return false;
		}

		if (WaitATResponse("OK", 10) > 0) {
			time(&now);
			nDelay = start - now;
			return true;
		}
	}
	return false;
}

bool Modem::IndicateDialing(string& strInd, int& id, int& type) {
	string::size_type pos = strInd.find("ORIG");
	if (pos != string::npos) {
		sscanf(strInd.c_str() + pos + 5, "%d,%d", &id, &type);
		return true;
	}
	return false;
}

bool Modem::IndicateNetConnected(string& strInd, int& id) {
	string::size_type pos = strInd.find("CONF");
	if (pos != string::npos) {
		sscanf(strInd.c_str() + pos + 5, "%d", &id);
		return true;
	}
	return false;
}

bool Modem::IndicateOffHook(string& strInd, int& id, int& type) {
	string::size_type pos = strInd.find("CONN");
	if (pos != string::npos) {
		sscanf(strInd.c_str() + pos + 5, "%d,%d", &id, &type);
		return true;
	}
	return false;
}

bool Modem::IndicateHungUp(string& strInd, int& id, int& duriation,
		int& status, int& cause) {
	string::size_type pos = strInd.find("CEND");
	if (pos != string::npos) {
		sscanf(strInd.c_str() + pos + 5, "%d,%d,%d,%d", &id, &duriation,
				&status, &cause);
		return true;
	}
	return false;
}

bool Modem::IndicateCLIP(string& strInd, string& number, int& type, int& valid) {
	char szTmp[256] = { 0 };
	string::size_type pos = strInd.find("CLIP");
	if (pos != string::npos) {
		sscanf(strInd.c_str() + pos + 5, "%s,%d,,,,%d", szTmp, &type, &valid);
		number = szTmp;
		return true;
	}

	return false;
}

bool Modem::IndicateRing(string& strInd) {
	string::size_type pos = strInd.find("RING");
	if (pos != string::npos) {
		return true;
	}
	return false;
}

bool Modem::IndicateRSSI(string& strInd, int& rssi, int& der) {
	string::size_type pos = strInd.find("RSSI");
	if (pos != string::npos) {
		sscanf(strInd.c_str() + pos + 5, "%d,%d", &rssi, &der);
		CSQ2DBm(rssi);
		return true;
	}
	return false;
}

bool Modem::IndicateCREG(string& strInd, int& stat, int& lac, int& ci) {
	string::size_type pos = strInd.find("CREG");
	if (pos != string::npos) {
		sscanf(strInd.c_str() + pos + 5, "%d,%d,%d", &stat, &lac, &ci);
		return true;
	}
	return false;
}

void Modem::CSQ2DBm(int& rssi) {
	if (rssi == 0)
		rssi = -113;
	else if (rssi == 1)
		rssi = -111;
	else if (rssi >= 2 && rssi <= 30) {
		rssi = ((rssi * (-53 + 109)) / (30 - 2)) - 109;
	} else if (rssi == 31)
		rssi = -51;
}

bool Modem::SetSMSIndicate(bool enable) {
	if (port.IsOpen()) {
		char szAT[48] = { 0 };
		if (enable)
			sprintf(szAT, "AT+CNMI=2,1,0,2,1\r\n");
		else
			sprintf(szAT, "AT+CNMI=1,1,0,1,1\r\n");

		TRACE("%s",szAT);
		if (port.Write(szAT) < 0) {
			INFO("%s failed",szAT);
			return false;
		}

		if (WaitATResponse("OK", 10) > 0) {
			return true;
		}
	}
	return false;
}

bool Modem::SetSMSFormatPDU(void) {
	if (port.IsOpen()) {
		if (port.Write("AT+CMGF=0\r\n") < 0) {
			INFO("failed");
			return false;
		}

		if (WaitATResponse("OK", 10) > 0) {
			return true;
		}
	}
	return false;

}

bool Modem::IndicateSMessage(string& strInd, string& mem, int& id) {
	string::size_type pos = strInd.find("CMTI:");
	if (pos != string::npos) {
		char tmp[32] = { 0 };
		sscanf(strInd.c_str() + pos + 5, "%s", tmp);
		char* pchar = strtok(tmp, ",");
		if (pchar)
			mem = pchar;
		pchar = strtok(NULL, ",");
		if (pchar)
			id = atoi(pchar);

		return true;
	}
	return false;
}

bool Modem::ReadSMSPDU(int id, int& stat, int& len, string& pdu) {
	if (port.IsOpen()) {
		char szAT[48] = { 0 };
		sprintf(szAT, "AT+CMGR=%d\r\n", id);
		if (port.Write(szAT) < 0) {
			INFO("%s failed",szAT);
			return false;
		}

		if (WaitATResponse("OK", 10) > 0) {
			string tmp;
			tmp = chopLine("CMGR");
			sscanf(tmp.c_str(), "CMGR:%d,,%d", &stat, &len);
			pdu = chopLine();
			strCache.clear();
			return true;
		}
	}
	return false;

}

