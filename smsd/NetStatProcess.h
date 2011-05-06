
#ifndef NETSTATPROCESS_H_
#define NETSTATPROCESS_H_

#include "Process.h"

class NetStatRecorder {
public:
	static inline NetStatRecorder *getInstance() {
		if (NULL == m_pobjNetStatRecorder) {
			m_pobjNetStatRecorder = new NetStatRecorder();
		}
		return m_pobjNetStatRecorder;
	}
	NetStatRecorder();
	~NetStatRecorder();
public:
	bool m_bNetStatFlag;
	int m_iPacketsRecvIp;
	int m_iSegmentsRecvTcp;
	int m_iSegmentsSentTcp;
	int m_iSegmentsRetransmitedTcp;
	int m_iBadSegmentsRecvTcp;
	int m_iPacketsRecvUdp;
	int m_iPacketsSentUdp;
	int m_iInOctets;
	int m_iOutOctets;
public:
	int m_nLoopCount;
private:
	static NetStatRecorder *m_pobjNetStatRecorder;
};

class NetStatProcess: public virtual Process {
public:
	NetStatProcess();
	virtual ~NetStatProcess();
	virtual int Run(const char* , int iLoop);
	static void* NetStatCycle(void* pobjNetStat);
private:
	bool parseNetStats(string strOutputLine);
};

#endif /* WAPPROCESS_H_ */
