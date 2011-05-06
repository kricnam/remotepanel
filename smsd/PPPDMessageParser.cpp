/*
 * PPPDMessageParser.cpp
 *
 *  Created on: 2011-3-7
 *      Author: mxx
 */

#include "PPPDMessageParser.h"

PPPDMessageParser::PPPDMessageParser() {
	// TODO Auto-generated constructor stub

}

PPPDMessageParser::~PPPDMessageParser() {
	// TODO Auto-generated destructor stub
}

enum PPPDMessageParser::PPP_MSG PPPDMessageParser::Parse(string& strMsg,
		string& strLine) {
	strLine = chopLine(strMsg);
	if (strLine.find("send (AT+CGDCONT") == 0)
		return CHAT_START;
	if (strLine.find("send (ATD") == 0)
		return CHAT_DIAL;
	if (strLine.find("Serial connection established") == 0)
		return PPP_CONNECTED;
	if (strLine.find("authentication succeeded") != string::npos)
		return AUTH_SUCCESS;
	if (strLine.find("local  IP address") != string::npos)
		return NEGOTIAT_SUCCESS;
	if (strLine.find("Serial link disconnected") == 0)
		return PPP_DISCONNECT;
	if (strLine.find("DELAYED") == 0)
			return PPP_DELAYED;
	if (strLine.find("BUSY") == 0)
			return PPP_BUSY;
	if (strLine.find("NO CARRIER") == 0)
		return PPP_NO_CARRIER;
	if (strLine.find("ERROR") == 0)
		return PPP_ERROR;
	if (strLine.find("Failed") == 0)
		return PPP_FAIL;

	return PPP_MSG_UNKNOWN;
}

string PPPDMessageParser::chopLine(string& strMsg) {
	string line;
	string::size_type pos = strMsg.find("\n");
	if (pos == string::npos)
		return line;

	line = strMsg.substr(0, pos);
	strMsg.erase(0, pos + 1);
	return line;
}
