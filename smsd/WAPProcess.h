/*
 * WAPProcess.h
 *
 *  Created on: 2011-3-11
 *      Author: mxx
 */

#ifndef WAPPROCESS_H_
#define WAPPROCESS_H_

#include "Process.h"

class WAPProcess: public virtual Process {
public:
	WAPProcess();
	virtual ~WAPProcess();
	virtual int Run(const char* szDev,int timeout);
protected:
	int master;
	int slave;
	char ptyname[64];
};

#endif /* WAPPROCESS_H_ */
