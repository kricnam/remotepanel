/*
 * Config.h
 *
 *  Created on: 2010-1-9
 *      Author: mxx
 */

#ifndef CONFIG_H_
#define CONFIG_H_
#include <string>

using namespace std;

namespace bitcomm
{
#define CONF_FILENAME "./agent.conf"

class Config
{
public:
	Config(const char* szFile);
	virtual ~Config();
	string GetServerName();
	string GetMPdev();
	string GetIP();
	int GetMachine();
	int GetPowerOnDelay();
	int GetTraceLevel();
	int GetDataPort();
	void LoadAll(void);
	void SaveAll(void);

	string strFileName;
	string strServerName;
	string strMPdev;
	string strIP;
	int    nPowerOnDelay;
	int    nMachine;
	int    nTraceLevel;
	int	   nDataPort;
};

}

#endif /* CONFIG_H_ */
