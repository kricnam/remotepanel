/*
 * Process.cpp
 *
 *  Created on: 2011-2-28
 *      Author: mxx
 */

#include "Process.h"
#include "stdio.h"

volatile int Process::m_nSerialNo = 0;

Process::Process() {
	m_nLoopCount = 0;
	m_nLoopMax = 1;
	m_nInterval = 10;
}

Process::~Process() {
	// TODO Auto-generated destructor stub
}

void Process::xmlHead(stringstream& text, int type) {
	text << "<cewa>" << endl;
	text << "<head>" << endl;
	text << "<type>" << type << "</type>" << endl;
	text << "</head>" << endl;
	text << "<body>" << endl;
}

void Process::xmlTail(stringstream& text) {
	text << "</body>\r\n</cewa>" << endl;
}

void Process::appendItem(stringstream& text, const char* szName,
		const char* value) {
	text << "<" << szName;
	if (value && value[0])
		text << ">" << value << "</" << szName << ">" << endl;
	else
		text << "/>" << endl;
}

void Process::appendItem(stringstream& text, const char* szName, int value) {
	text << "<" << szName << ">" << value << "</" << szName << ">" << endl;
}

void Process::appendItem(stringstream& text, const char* szName, time_t value) {
	struct tm time = { 0 };
	char szTime[32] = { 0 };
	if (value) {
		localtime_r(&value, &time);
		sprintf(szTime, "%04d-%02d-%02d %02d:%02d:%02d", time.tm_year + 1900,
				time.tm_mon + 1, time.tm_mday, time.tm_hour, time.tm_min,
				time.tm_sec);
	}

	text << "<" << szName;
	if (!value) {
		text << "/>" << endl;
		return;
	}

	text << ">" << szTime << "</" << szName << ">" << endl;
}

void Process::appendParamValue(stringstream& text, const char* szName,
		int value) {
	text << "<value name=\"" << szName;
	text << "\"/>" << value;
	text << "</value>" << endl;
}

void Process::appendParamValue(stringstream& text, const char* szName,
		const char* value) {
	text << "<value name=\"" << szName;
	if (value && value[0])
		text << "\">" << value;
	else {
		text << "\" />" << endl;
		return;
	}
	text << "</value>" << endl;
}

void Process::appendAttribute(stringstream& text, const char* szName, int value) {

	//	text << " " << szName << "=" << value;
	text << " " << szName << "=\"" << value << "\"";
}

void Process::appendAttribute(stringstream& text, const char* szName,
		time_t value) {
	struct tm time = { 0 };
	char szTime[32] = { 0 };
	if (value) {
		localtime_r(&value, &time);
		sprintf(szTime, "%04d-%02d-%02d %02d:%02d:%02d", time.tm_year + 1900,
				time.tm_mon + 1, time.tm_mday, time.tm_hour, time.tm_min,
				time.tm_sec);
	}

	text << " " << szName;
	if (!value) {
		text << "=\"\"";
		return;
	}

	text << "=\"" << szTime << "\"";
}

void Process::appendAttribute(stringstream& text, const char* szName,
		const char* value) {
	text << " " << szName << "=\"" << value << "\"";
}

void Process::addBaseAttribute(stringstream& text) {
	appendAttribute(text, "devid", m_strDev.c_str());
	appendAttribute(text, "taskid", m_strTaskID.c_str());
	appendAttribute(text, "devtype", m_strDevType.c_str());
	appendAttribute(text, "loop", m_nLoopCount);
	appendAttribute(text, "serial", Process::m_nSerialNo++);
}

int Process::Event(const char* szName, time_t time, int value) {
	char szBuf[256] = { 0 };
	sprintf(szBuf, "%d", value);
	return Event(szName, time, szBuf);
}

int Process::Event(const char* szName, time_t time, const char* value) {
	stringstream text;

	xmlHead(text, m_iBusinessType);
	text << "<event";
	addBaseAttribute(text);
	appendAttribute(text, "time_tag", time);
	text << ">" << endl;
	appendParamValue(text, szName, value);
	text << "</event>" << endl;
	xmlTail(text);

	sender.Send(text.str().c_str());
	//	TRACE("%s",text.str().c_str());
	return 0;
}

bool Process::timeToString(string& strTime, time_t time) {
	struct tm tmTime = { 0 };
	char szTime[32] = { 0 };
	if (time) {
		localtime_r(&time, &tmTime);
		sprintf(szTime, "%04d-%02d-%02d %02d:%02d:%02d", tmTime.tm_year + 1900,
				tmTime.tm_mon + 1, tmTime.tm_mday, tmTime.tm_hour,
				tmTime.tm_min, tmTime.tm_sec);
		strTime = szTime;
		return true;
	} else {
		return false;
	}
}
