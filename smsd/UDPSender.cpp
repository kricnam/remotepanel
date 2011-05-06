/*
 * UDPSender.cpp
 *
 *  Created on: 2011-2-25
 *      Author: mxx
 */

#include "UDPSender.h"
#include "TraceLog.h"

struct sockaddr UDPSender::hostaddr = { 0 };
struct sockaddr UDPSender::broadaddr = { 0 };

UDPSender::UDPSender() {
	nPort = 9050;
	init();
}

UDPSender::UDPSender(int port) {
	nPort = port;
	init();
}

UDPSender::~UDPSender() {
	if (socketID != -1) {
		close(socketID);
		socketID = -1;
	}
}

void UDPSender::GetBroadcastAddr(string& str) {
	UDPSender::getLocalAddr();
	struct sockaddr_in *addr = (struct sockaddr_in *) &broadaddr;
	str = inet_ntoa((addr->sin_addr));
	return;
}

void UDPSender::init(void) {
	socketID = socket(PF_INET, SOCK_DGRAM, 0);

	if (socketID == -1) {
		ERRTRACE();
		return;
	}
	int so_broadcast = 1;
	if (setsockopt(socketID, SOL_SOCKET, SO_BROADCAST, &so_broadcast,
			sizeof(so_broadcast)) == -1) {
		ERROR("failed to enable broadcast");
		return;
	}

	getLocalAddr();
	hostaddr.sa_family = 0;
	struct sockaddr_in *addr = (struct sockaddr_in *) &hostaddr;
	addr->sin_port = htons(nPort);
	addr = (struct sockaddr_in *) &broadaddr;
	addr->sin_port = htons(nPort);
	if (bind(socketID, &hostaddr, sizeof(hostaddr)) == -1)
		ERROR("failed to bind");
}

void UDPSender::getLocalAddr(void) {
	struct ifaddrs *ifaddr, *ifa;
	int family;

	struct sockaddr_in *addr = (struct sockaddr_in *) &hostaddr;
	if ((addr->sin_addr.s_addr))
		return;

	if (getifaddrs(&ifaddr) == -1) {
		ERRTRACE();
		return;
	}

	/* Walk through linked list, maintaining head pointer so we
	 can free list later */

	for (ifa = ifaddr; ifa != NULL; ifa = ifa->ifa_next) {
		if (!ifa->ifa_addr)
			continue;
		family = ifa->ifa_addr->sa_family;
		if (family != AF_INET)
			continue;
		if (strcmp(ifa->ifa_name, "lo") == 0) {
			TRACE("find lo interface");
			hostaddr.sa_family = ifa->ifa_addr->sa_family;
			memcpy(hostaddr.sa_data, ifa->ifa_addr->sa_data,
					sizeof(hostaddr.sa_data));
			broadaddr.sa_family = ifa->ifa_ifu.ifu_broadaddr->sa_family;
			memcpy(broadaddr.sa_data, ifa->ifa_ifu.ifu_broadaddr->sa_data,
					sizeof(broadaddr.sa_data));
			continue;
		}
		if (strcmp(ifa->ifa_name, "eth0") != 0)
			continue;
		TRACE("find eth0 interface");
		hostaddr.sa_family = ifa->ifa_addr->sa_family;
		memcpy(hostaddr.sa_data, ifa->ifa_addr->sa_data,
				sizeof(hostaddr.sa_data));
		broadaddr.sa_family = ifa->ifa_ifu.ifu_broadaddr->sa_family;
		memcpy(broadaddr.sa_data, ifa->ifa_ifu.ifu_broadaddr->sa_data,
				sizeof(broadaddr.sa_data));
		freeifaddrs(ifaddr);
		break;
	}
	return;
}

void UDPSender::Send(const char* szMsg) {
	if (socketID != -1) {
		sendto(socketID, szMsg, strlen(szMsg), 0, &broadaddr, sizeof(broadaddr));
	}else{
		TRACE("UDPSender::Send Err!szMsg=%s!\n", szMsg);
	}
}
