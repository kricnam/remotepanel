/*
 * SMSPDUHead.h
 *
 *  Created on: 2011-3-31
 *      Author: mxx
 */

#ifndef SMSPDUHEAD_H_
#define SMSPDUHEAD_H_
#include <map>
#include <string>
using namespace std;
class SMSPDUHead {
public:
	SMSPDUHead();
	SMSPDUHead(const char* pHead);
	bool GetPortNumber(int& orgPort,int& destPort);
	virtual ~SMSPDUHead();
protected:
	void parse(const char* pHead);

	char* head;
	int size;
	map<string,string> values;

	static map<char,string> keyvalue;
	static void initMap(void);

};

#endif /* SMSPDUHEAD_H_ */
