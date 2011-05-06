/*
 * UDPSender.h
 *
 *  Created on: 2011-2-25
 *      Author: mxx
 */

#ifndef UDPSENDER_H_
#define UDPSENDER_H_
#include <sys/socket.h>
#include <sys/types.h>
#include <ifaddrs.h>
#include <string>
using namespace std;
class UDPSender {
public:
	UDPSender();
	UDPSender(int port);
	virtual ~UDPSender();
	void Send(const char* szMsg);
	static void GetBroadcastAddr(string& str);
protected:
	void init();
	static void getLocalAddr(void);
	static struct sockaddr hostaddr;
	static struct sockaddr broadaddr;
	int socketID;
	int nPort;
};

#endif /* UDPSENDER_H_ */
