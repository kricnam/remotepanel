/*
 * PPPDMessageParser.h
 *
 *  Created on: 2011-3-7
 *      Author: mxx
 */

#ifndef PPPDMESSAGEPARSER_H_
#define PPPDMESSAGEPARSER_H_
#include <string>
using namespace std;
class PPPDMessageParser {
public:
	PPPDMessageParser();
	virtual ~PPPDMessageParser();
	enum PPP_MSG {
		CHAT_START,
		CHAT_DIAL,
	    PPP_CONNECTED,
		AUTH_SUCCESS,
		NEGOTIAT_SUCCESS,
		PPP_DISCONNECT,
		PPP_BUSY,
		PPP_DELAYED,
		PPP_NO_CARRIER,
		PPP_ERROR,
		PPP_FAIL,
		PPP_MSG_UNKNOWN
	};
	enum PPP_MSG Parse(string& strMsg,string& strLine);
protected:
	string chopLine(string& strMsg);
};

#endif /* PPPDMESSAGEPARSER_H_ */
