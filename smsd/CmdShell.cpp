/*
 * CmdShell.cpp
 *
 *  Created on: 2011-3-14
 *      Author: mxx
 */

#include "CmdShell.h"
#include <pty.h>
#include <unistd.h>
#include <utmp.h>
#include "TraceLog.h"

#ifdef SHELL_TEST
#include <stdlib.h>
#include <stdio.h>
int main(int argc,char** argv)
{
	SETTRACELEVEL(0);
	CmdShell shell;
	//	char const*  myargv[4]={"/usr/sbin/pppd","call","test",NULL};
	//	shell.m_argv = (char* const*)myargv;
	//	shell.Open(argv[0]);
	shell.Open();
	shell.Write("ls\n");
	string strLine;
	while (shell.ReadLine(strLine,'\n')>0)
	{
		printf("%s\n",strLine.c_str());
	};
	TRACE("Read Over");
	shell.Write("\0x03");
	exit(1);
}
#endif

CmdShell::CmdShell() {
	nChildPID = -1;
	m_timeout_sec = 2;
}

CmdShell::~CmdShell() {
	if (masterHandle > 0)
		close(masterHandle);
}

bool CmdShell::Open(const char* szCmd) {
	char name[200] = { 0 };
	if (nChildPID != -1)
		return false;

	nChildPID = forkpty(&masterHandle, name, 0, 0);
	strPtyName = name;
	if (nChildPID == -1) {
		ERRTRACE();
		return false;
	} else if (nChildPID == 0) {
		if (szCmd == NULL)
			execl("/bin/bash", "/bin/bash", NULL);
		else
			execv(szCmd, m_argv);
		ERRTRACE();
	}

	return true;
}

int CmdShell::Read(string& strBuf, int nSize) {
	char buf[1024];
	int rt;
	int sum = 0;
	struct timeval tmWait = { 0 };
	fd_set reads;

	while (!nSize || sum < nSize) {
		tmWait.tv_sec = m_timeout_sec;
		tmWait.tv_usec = 0;
		FD_ZERO( &reads );
		FD_SET( masterHandle, &reads );

		rt = select(masterHandle + 1, &reads, NULL, NULL, &tmWait);
		if (rt < 0) {
			ERRTRACE();
			return -1;
		}
		if (rt == 0)
			break;

		rt = read(masterHandle, buf, 1024);
		if (rt < 0) {
			ERRTRACE();
			return sum;
		} else {
			sum += rt;
			strBuf.append(buf, rt);
		}
	};

	return sum;
}

int CmdShell::Write(const char* szCmd) {
	if (!szCmd)
		return -1;
	int rt = write(masterHandle, szCmd, strlen(szCmd));
	if (rt < 0)
		ERRTRACE();
	return rt;
}

int CmdShell::ReadLine(string& strLine, char cDelimter) {
	char buf[64] = { 0 };
	int rt;
	struct timeval tmWait = { 0 };
	fd_set reads;

	while (!chopLine(strLine, cDelimter)) {
		tmWait.tv_sec = m_timeout_sec;
		tmWait.tv_usec = 0;
		FD_ZERO( &reads );
		FD_SET( masterHandle, &reads );
		rt = select(masterHandle + 1, &reads, NULL, NULL, &tmWait);
		if (rt < 0) {
			ERRTRACE();
			return -1;
		}
		if (rt == 0)
			break;

		rt = read(masterHandle, buf, 64);
		//    	TRACE("return %d bytes!buf=%s!",rt, buf);
		if (rt < 0) {
			ERRTRACE();
			return -1;
		}
		strReadCache.append(buf, rt);
	};
	return strLine.size();
}

int CmdShell::chopLine(string& strLine, char cDelimter) {
	string::size_type pos;
	if (strLine.size())
		strLine.clear();
	pos = strReadCache.find(cDelimter);
	if (pos != string::npos) {
		strLine = strReadCache.substr(0, pos + 1);
		strReadCache.erase(0, pos + 1);
		return strLine.size();
	}
	return 0;
}
