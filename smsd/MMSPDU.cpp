/*
 * MMSPDU.cpp
 *
 *  Created on: 2011-3-31
 *      Author: mxx
 */

#include "MMSPDU.h"
#include <string.h>

MMSPDU::MMSPDU() {
	// TODO Auto-generated constructor stub

}

MMSPDU::~MMSPDU() {
	// TODO Auto-generated destructor stub
}

unsigned int MMSPDU::readUintVar(const char* buf , int& len)
{
	unsigned val=0;
	for(int i=0;i<5;i++)
	{
		val = (val<<7) | (buf[i]&0x7F);
		if (!(buf[i]& 0x80))
		{
			len = i+1;
			break;
		}
	}
	return val;
}

void MMSPDU::parse(const char* buf , int len)
{
	head.TID = buf[0];
	head.TYPE = (PDU_TYPE)buf[1];
	switch(head.TYPE)
	{
	case Push:
		parsePUSH(buf+2,len-2);
		break;
	default:
		break;
	}
}

void MMSPDU::parsePUSH(const char* buf , int len)
{
	int size=0;
	int offset=0;
	int heads_len = readUintVar(buf,size);
	offset = size;
	headers["ContentType"]= buf+size;
	size = strlen(buf+size)+1;
	offset += size;
	headers["Headers"] = string(buf+size,heads_len-size);
	offset += heads_len-size;
	if (len > offset )
		headers["data"]= string(buf+offset,len -offset);
}

/*
#undef WSP_HEADER_DEF
#define WSP_HEADER_DEF(id,value,name)  case value: return name;
	switch(val)
	{
		WSP_HEADERS
		default:
			return "";
	};
#undef WSP_HEADER_DEF
*/
