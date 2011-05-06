/*
 * CmdShell.h
 *
 *  Created on: 2011-3-14
 *      Author: mxx
 */

#ifndef CMDSHELL_H_
#define CMDSHELL_H_
#include <string>
using namespace std;
class CmdShell {
public:
	CmdShell();
	virtual ~CmdShell();
	virtual bool Open(const char* szCmd=NULL);
	virtual int Read(string& strLine,int nSize=0);
	virtual int ReadLine(string& strLine,char cDelimter);
	virtual int Write(const char* szCmd);
	char* const*  m_argv;
	int m_timeout_sec;
protected:
	int chopLine(string& strLine,char cDelimter);
	int masterHandle;
	string strPtyName;
	int nChildPID;
	string strReadCache;
};

#endif /* CMDSHELL_H_ */
