/*
 * Config.cpp
 *
 *  Created on: 2010-1-9
 *      Author: mxx
 */

#include "Config.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "TraceLog.h"
namespace bitcomm
{

void print_config(FILE* fp,const char*szKey,const char* szVal,const char* szDesc)
{
	fprintf(fp,"#%s\n%s=%s\n",szDesc,szKey,szVal);
}

void print_config(FILE* fp,const char*szKey,int nVal,const char* szDesc)
{
	fprintf(fp,"#%s\n%s=%d\n",szDesc,szKey,nVal);
}

int GetValue(const char* szFile,const char *szKey, char *pBuf, int nLen)
{
	FILE *confFile;
	int nRet,nLine,nCmp;
	int nszKey = strlen(szKey);
	char line[1024]={0};
	char szKeyCmp[1024]={0};
	char* moveEof=NULL;

	int nSpace=0;
	int nEnd=0;
	int i;

	pBuf[0]=0;

	if( (confFile  = fopen((const char *)szFile, "r" )) == NULL )
		nRet = -1;
	else
	{
		nRet = -2;
		while (!feof (confFile))
		{
			memset(line,0,1024);
			memset(szKeyCmp,0,1024);
			fgets(line, 1024,confFile);

			if (line[0]=='#')
				continue;

			nLine = strlen(line);
			if(line[nLine-1]=='\n')
				nEnd = 1;
			else
				nEnd=0;
			moveEof=strtok(line,"\n");
			if(moveEof!=NULL)
			{
				while(*moveEof == ' ')
				{
					nSpace++;
					moveEof--;
				}
			}
			if(nSpace!=0)
				nLine = nLine-nSpace-1;
			strcpy(szKeyCmp,szKey);
			strcat(szKeyCmp,"=");
			nCmp=strncmp(szKeyCmp,line,nszKey+1);
			if(!nCmp)
			{
				int temp = nLine-nszKey;
				if(nLen<temp)
					nRet = temp;
				else
				{
					if(nEnd>0)
					{
						for(i=0;i<temp-1;i++)
							pBuf[i]=line[i+nszKey+1];
					}
					else
					{
						for(i=0;i<temp;i++)
							pBuf[i]=line[i+nszKey+1];
					}
					nRet = 0;
				}
				break;
			}
		}
		fclose (confFile);
	}
	return nRet;
}

Config::Config(const char* szFileName)
{
	strFileName = szFileName;
}

Config::~Config()
{

}

string Config::GetServerName()
{
	char tmp[1024]={0};
	if (GetValue(strFileName.c_str(),"SERVER",tmp,1024)==0)
	{
		strServerName = tmp;
	}
	else
		strServerName.clear();
	return strServerName;

}

string Config::GetMPdev()
{
	char tmp[1024]={0};
	if (GetValue(strFileName.c_str(),"MP_PORT",tmp,1024)==0)
	{
		TRACE("Read %s",tmp);
		strMPdev = tmp;
	}
	else
		strMPdev = "/dev/ttyS2";
	if (strMPdev.empty()) strMPdev = "/dev/ttyS2";
	return strMPdev;
}

int Config::GetMachine()
{
	char tmp[1024]={0};
	if (GetValue(strFileName.c_str(),"MACHINE",tmp,1024)==0)
	{
		nMachine = atoi(tmp);
	}
	else
		nMachine = 1;
	return nMachine;
}

int Config::GetPowerOnDelay()
{
	char tmp[1024]={0};
	if (GetValue(strFileName.c_str(),"POWERON_DELAY",tmp,1024)==0)
	{
		nPowerOnDelay =  atoi(tmp);
	}
	else
		nPowerOnDelay = 90;
	return nPowerOnDelay;
}

int Config::GetTraceLevel(void)
{
	char tmp[1024]={0};
	if (GetValue(strFileName.c_str(),"LOG_LEVEL",tmp,1024)==0)
	{
		nTraceLevel = atoi(tmp);
	}
	else
		nTraceLevel = LP_INFO;
	return nTraceLevel;
}

int Config::GetDataPort(void)
{
	char tmp[1024]={0};
	if (GetValue(strFileName.c_str(),"DATA_PORT",tmp,1024)==0)
	{
		nDataPort = atoi(tmp);
	}
	else
		nDataPort = 9998;
	return nDataPort;
}


string Config::GetIP(void)
{
	char tmp[1024]={0};
	if (GetValue(strFileName.c_str(),"LOCAL_IP",tmp,1024)==0)
	{
		strIP = tmp;
	}

	if (strIP.empty())	strIP = "192.168.1.77";
	return strIP;
}

void Config::LoadAll(void)
{
	GetServerName();
	GetMPdev();
	GetIP();
	GetMachine();
	GetPowerOnDelay();
	GetTraceLevel();
	GetDataPort();
}

void Config::SaveAll(void)
{
	FILE* fp;
	string strVal;
	fp = fopen(strFileName.c_str(),"w");
	if (fp)
	{
		print_config(fp,"SERVER",strServerName.c_str(), "Server address");
		print_config(fp,"POWERON_DELAY", nPowerOnDelay,"waiting second for MP self test");
		print_config(fp,"LOG_LEVEL",nTraceLevel, "log message detail level, 0-9, 0 for more, 9 for less detail");
		print_config(fp,"MP_PORT",strMPdev.c_str(),"device file name for MP");
		print_config(fp,"MACHINE",nMachine, "Part No. of  MP");
		print_config(fp,"LOCAL_IP",strIP.c_str(),"public IP for satellite modem");
		print_config(fp,"DATA_PORT",nDataPort,"TCP Port number for server");
		fclose(fp);
	}
	else
		ERRTRACE();

}

}
