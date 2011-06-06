/*
 * TCPPort.cpp
 *
 *  Created on: 2009-12-30
 *      Author: mxx
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <unistd.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <sys/file.h>
#include "TraceLog.h"
#include "TCPPort.h"
using namespace std;
namespace bitcomm
{
TCPPort::TCPPort()
{
	nPort = 0;
	socketID = -1;
	bConnected = false;
	timeout = 60000000;
}

TCPPort::~TCPPort()
{

}

int TCPPort::Open(const char* szServer,int nPort)
{
	TRACE("%s:%d",szServer,nPort);
	if (strServerName!=szServer)
		strServerName = szServer;

	this->nPort = nPort;

	if (socketID > 0)	Close();


		socketID = socket(AF_INET,SOCK_STREAM,0);
		if (socketID<0)
		{
			perror("TCPPort::Open");
			return -1;
		}
		SetTimeOut(timeout);

	return Connect();
}

int TCPPort::waitConnect()
{
    fd_set rfds;
    struct timeval tv;
    int retval;

    /* Watch stdin (fd 0) to see when it has input. */
    FD_ZERO(&rfds);
    FD_SET(socketID, &rfds);

    /* Wait up to 200 seconds. */
    tv.tv_sec = 20;
    tv.tv_usec = 0;

    retval = select(socketID+1, NULL,&rfds,  NULL, &tv);
    /* Don't rely on the value of tv now! */

    if (retval == -1)
    {
        ERRTRACE()
        return -1;
    }
    int error = 0;
    socklen_t len=sizeof(error);
    if (getsockopt(socketID,SOL_SOCKET,SO_ERROR,&error,&len)<0)
    {
    	ERRTRACE();
    	return -1;
    }

    if (error)
    {
    	INFO("connection failed:%d",error);
    	return -1;
    }

    return 0;
}

int TCPPort::Connect()
{
    struct addrinfo hints;
    struct addrinfo *result, *rp;
    int s;
    gettimeofday(&tmLastAction, 0);
    /* Obtain address(es) matching host/port */
    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_UNSPEC;    /* Allow IPv4 or IPv6 */
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = 0;
    hints.ai_protocol = 0;          /* Any protocol */
    char szPort[32];
    sprintf(szPort,"%d",nPort);
    s = getaddrinfo(strServerName.c_str(), szPort,
    		&hints, &result);
    if (s != 0)
    {
        ERROR("%s\n", gai_strerror(s));
        return -1;
    }

    /* getaddrinfo() returns a list of address structures.
       Try each address until we successfully connect(2).
       If socket(2) (or connect(2)) fails, we (close the socket
       and) try the next address. */
    for (rp = result; rp != NULL; rp = rp->ai_next)
    {
    	if (socketID==-1)
    	{
    		socketID = socket(rp->ai_family, rp->ai_socktype,
                     rp->ai_protocol);
    		SetTimeOut(timeout);
    	}

        if (socketID == -1)
            continue;
        struct sockaddr_in* pin = (struct sockaddr_in*)rp->ai_addr;
        TRACE("Connect to %s:%d",inet_ntoa(pin->sin_addr),ntohs(pin->sin_port));
        if (::connect(socketID, rp->ai_addr, rp->ai_addrlen) != -1)
        {
        	bConnected = true;
            break;                  /* Success */
        }

        //if (errno == EINPROGRESS)
        //{
        //    if (waitConnect()!=-1)
        //    {
        //    	bConnected = true;
        //        break;                  /* Success */
        //    }
        //}
        ERRTRACE();
        close(socketID);
        socketID = -1;
    }

    if (rp == NULL)
    {               /* No address succeeded */
        ERROR("No address succeeded");
        return -1;
    }

    gettimeofday(&tmLastAction, 0);
    freeaddrinfo(result);           /* No longer needed */
    if (!bConnected) return -1;
    return 0;
}

void TCPPort::Close()
{
	gettimeofday(&tmLastAction, 0);

	if (socketID>0)
	{
		shutdown(socketID,SHUT_RDWR);
		close(socketID);
		socketID = -1;
		bConnected = false;
		DEBUG("%s:%d closed",strServerName.c_str(),nPort);
	}
}

int TCPPort::Read(char* buf,int len) throw(ChannelException)
{
	TRACE("Reading from %s:%d ...",strServerName.c_str(),nPort);
	if (!bConnected) throw ChannelException();

	int n = recv(socketID,buf,len,0);
	TRACE("Read from %s:%d  [%d] bytes",strServerName.c_str(),nPort,n);
	if (n<0)
	{
		int err = errno;
		if (err == EAGAIN || err == EWOULDBLOCK)
		{
			INFO("No more date");
			return 0;
		}
		else
		{
			ERRTRACE();
			ChannelException excp(err);
			throw excp;
		}
	}
	if (n==0)
	{
		ERRTRACE();
		Close();
	}
	gettimeofday(&tmLastAction, 0);
	return n;
}

int TCPPort::Write(const char* buf,int len) throw(ChannelException)
{
	if (!bConnected) throw ChannelException();
	gettimeofday(&tmLastAction, 0);
	if (len==0) return 0;
	TRACE("send to %s:%d  [%d] bytes",strServerName.c_str(),nPort,len);
	int n = send(socketID,buf,len,0);
	if (n<0)
	{
		int err = errno;
		ERRTRACE();
		perror("TCPPort::Write");
		ChannelException excp(err);
		throw excp;
	}
	gettimeofday(&tmLastAction, 0);
	return n;
}

void TCPPort::SetTimeOut(int uSecond)
{
	timeout=uSecond;

	if (socketID>0)
	{
		struct timeval tm;
		tm.tv_sec = uSecond/1000000;
		tm.tv_usec = uSecond%1000000;

		setsockopt(socketID,SOL_SOCKET,SO_RCVTIMEO,
			(char *)&tm,sizeof(struct timeval));
		setsockopt(socketID,SOL_SOCKET,SO_SNDTIMEO,
			(char *)&tm,sizeof(struct timeval));
	}
}

void  TCPPort::Lock(void)
{

}
void  TCPPort::Unlock(void)
{

}

}
