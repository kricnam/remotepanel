/*
 * WGetProcess.h
 *
 *  Created on: Mar 17, 2011
 *      Author: xukh
 */

#ifndef WGETPROCESS_H_
#define WGETPROCESS_H_

#include "Process.h"
#include "NetStatProcess.h"
#include "CmdShell.h"
#include "CallingProcess.h"
#include <time.h>

/*
 *
 */
class WGetDTO {
public:
    string url;
    time_t startConnTime;

    time_t connectedTime;
    int    connected;

    time_t startDownTime;
    long   fileSize;
    time_t doneTime;
    int    percent;
};
class WGetProcess: public virtual Process
{
public:
    WGetProcess();
    virtual ~WGetProcess();
    virtual int Run(const char* szDev,int timeout);
    WGetDTO* dto;
private:
    CmdShell* shell;
    int parseDownloadProgress(const unsigned int iDataLength, int iTimeOut);
    bool checkUrl(string& strUrlChecking, string& strUrlLine);
};

#endif /* WGETPROCESS_H_ */
