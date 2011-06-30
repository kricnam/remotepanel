/*
 * TCPPort.h
 *
 *  Created on: 2009-12-30
 *      Author: mxx
 */

#ifndef TCPPORT_H_
#define TCPPORT_H_

#include "Channel.h"
#include <string>
#include <unistd.h>
#include <sys/socket.h>
#include <netdb.h>

using namespace std;
namespace bitcomm
{
class TCPPort: public virtual Channel
{
public:
	TCPPort();
	virtual ~TCPPort();
	int Open(const char* szServer,int nPort);
	virtual int Open() {return Open(strServerName.c_str(),nPort);};
	virtual int Read(char* buf,int len) throw(ChannelException);
	virtual int Write(const char* buf,int len) throw(ChannelException);
	virtual void Lock(void);
	virtual void Unlock(void);
	virtual bool IsOpen() {return bConnected;};
	void SetTimeOut(int n);
	void Bind(int socket){socketID = socket;bConnected = (socket!=-1);};
	void SetRemoteHost(const char* szHost)
	{
		strServerName = szHost;
	};
	void SetRemotePort(int nPort)
	{
		this->nPort = nPort;
	};
	int Connect();
	void Close();
	int GetHandle(void) { return socketID;};

protected:

	int waitConnect();
	int nPort;
	string strServerName;
	int socketID;
	bool bConnected;
	int timeout;

};
}
#endif /* TCPPORT_H_ */
