/*
 * Process.h
 *
 *  Created on: 2011-2-28
 *      Author: mxx
 */

#ifndef PROCESS_H_
#define PROCESS_H_
#include "UDPSender.h"
#include <sstream>
#include <string>
#include <time.h>
using namespace std;

class Process {
public:
	Process();
	virtual ~Process();

	volatile int m_iBusinessType;
	string m_strDev;
	string m_strTaskID;
	string m_strDevType;
	int m_nLoopMax;
	int m_nInterval;
	string m_strUrl;
	volatile static int m_nSerialNo;

	virtual int Run(const char* szDev, int timeout) = 0;
protected:
	virtual void xmlHead(stringstream& text, int type);
	virtual void xmlTail(stringstream& text);
	virtual void appendItem(stringstream& text, const char* szName,
			time_t value);
	virtual void appendItem(stringstream& text, const char* szName, int value);
	virtual void appendItem(stringstream& text, const char* szName,
			const char* value);
	virtual void appendAttribute(stringstream& text, const char* szName,
			time_t value);
	virtual void appendAttribute(stringstream& text, const char* szName,
			int value);
	virtual void appendAttribute(stringstream& text, const char* szName,
			const char* value);
	virtual void appendParamValue(stringstream& text, const char* szName,
			const char* value);
	virtual void appendParamValue(stringstream& text, const char* szName,
			int value);
	virtual void addBaseAttribute(stringstream& text);
	bool timeToString(string& strTime, time_t time);

	virtual int
	Event(const char* szName, time_t time, const char* value = NULL);
	virtual int Event(const char* szName, time_t time, int value);

	int m_nLAC;
	int m_nCI;
	int m_nRSSI;
	int m_nDER;
	int m_nStat; //Register state
	int m_nLoopCount;

	UDPSender sender;
};

#endif /* PROCESS_H_ */
