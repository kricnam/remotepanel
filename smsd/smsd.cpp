
#include "Modem.h"
#include "TraceLog.h"
#include "stdlib.h"
#include "stdio.h"
#include <sstream>
#include <string>
#include <unistd.h>
#include <sys/wait.h>
#include "pdu.h"
#include "Config.h"
// Constructors/Destructors
//  
using namespace std;
using namespace bitcomm;

const char szDev[] = "/dev/ttyUSB2";
int Run(const char* szDev, int timeout);
int main_work(int argc, char** argv);

void InitPort(void)
{
	//echo 72 > /sys/class/gpio/export;"
	//"echo in > /sys/class/gpio/gpio72/direction;"
	int n  = system("echo 79 > /sys/class/gpio/export;"\
			"echo 81 > /sys/class/gpio/export;"\
			"echo 85 > /sys/class/gpio/export;"\
			"echo low >/sys/class/gpio/gpio79/direction;"\
			"echo low >/sys/class/gpio/gpio81/direction;"\
			"echo in > /sys/class/gpio/gpio85/direction");

	DEBUG("system return %d",n);
	system("echo high >/sys/class/gpio/gpio81/direction;"); //enable power off
}

int main(int argc, char** argv)
{
	InitPort();
    while (1)
	{
		pid_t child = fork();

		if (child == 0)
		{
			return main_work(argc,argv);
		}

		if (child == -1)
			ERROR("fail to create working child" );
		else
		{
			waitpid(child, NULL, 0);
			INFO("App Working process exit,Restart after 10 second");
			SerialPort port;
			if (port.Open("/dev/ttyUSB2")==0)
			{
				INFO("Open CDMA Modem");
				port.Write("AT^RESET\r\n");
				INFO("Send reset command");
				sleep(10);
			}
			system("reboot");
			return 0;
		}
	}
	return -1;
}


int main_work(int argc, char** argv)
{
	INFO("deamon for sms started...");
	Config conf("/app/bin/agent.conf");
	conf.LoadAll();
	SETTRACELEVEL(conf.nTraceLevel);
	return Run(szDev,30);
}



int Run(const char* szDev, int timeout)
{
	Modem& modem = *Modem::CreateInstance(szDev);

	string ind;
	if (!(&modem))
	{
		ERROR("failed to create modem instance.");
	}

	modem.Open(modem.m_szDefaultPort);
	modem.Init();

	DEBUG("Set SMS Format");
	if (modem.SetSMSFormatPDU()==false)
	{
		ERROR("Setting PDU mode fail.");
		return 1;
	}
	if (!modem.SetSMSIndicate(true))
	{
		ERROR("Setting SMS Indicate fail");
		return 1;
	}
	//time_t t,e;
	//modem.Dial("13641158242",t,e);
	time_t now;
    string mem;
    modem.DeleteSMSAll();
//    system("cd /app/bin ; /app/bin/comd &");
	do
	{
		ind = modem.WaitIndication(now, timeout);
		if (modem.IndicateRing(ind))
		{
			time_t now;
			modem.Answer(now);
			sleep(1);
			modem.HungUp(now);
//			system("killall pppd");
//			sleep(5);
//			system("pppd call cdma &");
		}

		int id;
		if (modem.IndicateSMessage(ind,mem,id))
		{
			INFO("Get %s",ind.c_str());
			string strpdu;
			int len,stat;
			if (modem.ReadSMSPDU(id,stat,len,strpdu))
			{
				INFO("Get %6d%d,[%d]%s",stat,len,strpdu.size(),strpdu.c_str());
				INFO("Caller %s",modem.m_strCaller.c_str());
				modem.DeleteSMSAll();
				string::size_type n = strpdu.find("SVR:");
				if (n!=string::npos)
				{
					string svr;
					size_t pos;
					svr = strpdu.substr(n+4,strpdu.size()-n-4);
					while((pos = svr.find(' '))!=string::npos) svr.erase(pos);
					while((pos=svr.find('\t'))!=string::npos) svr.erase(pos);
					while((pos=svr.rfind('\t'))!=string::npos) svr.erase(pos);
					while((pos=svr.rfind(' '))!=string::npos) svr.erase(pos);
					Config conf("/app/bin/agent.conf");
					conf.LoadAll();
					conf.strServerName = svr;
					conf.SaveAll();
//					if (system("pgrep comd")==1)
//						system("cd /app/bin ; /app/bincomd &");
					continue;
				}
				n = strpdu.find("START");
				if (n!=string::npos)
				{
					system("cd /app/bin ; /app/bin/comd &");
					continue;
				}
				n = strpdu.find("STOP");
				{
					system("killall comd");
					system("killall pppd");
					continue;
				}
				n = strpdu.find("REBOOT");
				{
					//system("reboot");
					break;
				}
			}
		}
	}
	while (true);
	delete &modem;
	return 0;
}
