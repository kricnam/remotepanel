/*
 * SMSPDUHead.cpp
 *
 *  Created on: 2011-3-31
 *      Author: mxx
 */

#include "SMSPDUHead.h"
#include "TraceLog.h"
#include <string.h>
map<char,string> SMSPDUHead::keyvalue;
#define DEFINE_IE(x,y) 	keyvalue[x] = y;TRACE("%02hhX->%s",x,y);
#define INIT_IE_KEY_VALUE   \
	DEFINE_IE(0x05,"port number")

SMSPDUHead::SMSPDUHead()
{
	head = NULL;
	size = 0;
	initMap();
}

SMSPDUHead::SMSPDUHead(const char* pHead)
{
	initMap();
	parse(pHead);
	head = NULL;
	size = 0;
}


void SMSPDUHead::initMap(void)
{
	if (keyvalue.size()) return;
	INIT_IE_KEY_VALUE
}

SMSPDUHead::~SMSPDUHead()
{
	if (head) delete head;
}

void SMSPDUHead::parse(const char* pHead)
{
	if (!pHead) return;

	int len = pHead[0];
	TRACE("len=%d",len);
	int n = 1;
	char key;
	while(n<(len-1))
	{
		key = pHead[n];
		TRACE("key [%02hhX]:%s",key,keyvalue[key].c_str());
		values[keyvalue[key]] = string(&pHead[n+2],pHead[n+1]);
		n += pHead[n+1];
	};
}

bool SMSPDUHead::GetPortNumber(int& orgPort,int& destPort)
{
	string port = values["port number"];
	DUMP(port.c_str(),port.size());
	if (port.size()==4)
	{
		destPort = ((port[0] << 8) & 0x00FF00) | (port[1] & 0x00FF);
		orgPort = ((port[2] << 8) & 0x00FF00) | (port[3] & 0x00FF);
		return true;
	}
	return false;
}
