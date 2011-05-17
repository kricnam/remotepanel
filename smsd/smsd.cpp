
#include "Modem.h"
#include "TraceLog.h"
#include "stdlib.h"
#include "stdio.h"
#include <sstream>
#include <string>
#include <unistd.h>
#include "pdu.h"
#include "Config.h"
// Constructors/Destructors
//  
using namespace std;
using namespace bitcomm;
void print_useage(const char* sz) {
	fprintf(
			stderr,
			"Usage: %s [-a] [-l duriation_secs][-t timeout_secs] [-d] dial_No [-p dev_port]\n",
			sz);
}

string strDev = "/dev/ttyUSB2";
int Run(const char* szDev, int timeout);

int main(int argc, char** argv) {
	SETTRACELEVEL(0);
	string strPort;
	strPort = "/dev/ttyUSB2";

	int opt;
	INFO("deamon for sms started...");
	while ((opt = getopt(argc, argv, "al:t:p:d:i:x:n:")) != -1) {
		TRACE("process %c %s", opt,optarg);
		switch (opt) {
		case 'p':
			strPort = optarg;
			break;
		case 'x':
			strDev = optarg;
			break;
		default: /* '?' */
			print_useage(argv[0]);
			exit(EXIT_FAILURE);
		}
	};
	return Run(strDev.c_str(),30);

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
	do
	{
		ind = modem.WaitIndication(now, timeout);
		if (modem.IndicateRing(ind))
		{
			system("killall pppd");	sleep(5);system("pppd call cdma &");
		}

		int id;
		if (modem.IndicateSMessage(ind,mem,id))
		{
			INFO("Get %s",ind.c_str());
			string strpdu;
			int len,stat;
			if (modem.ReadSMSPDU(id,stat,len,strpdu))
			{
				INFO("Get %6d %d,[%d]%s",stat,len,strpdu.size(),strpdu.c_str());
				INFO("Caller %s",modem.m_strCaller.c_str());
				modem.DeleteSMSAll();
				string::size_type n = strpdu.find("SVR:");
				if (n!=string::npos)
				{
					string svr;
					svr = strpdu.substr(n+4,strpdu.size()-n-4);
					Config conf("./agnet.conf");
					conf.LoadAll();
					conf.strServerName = svr;
					conf.SaveAll();
				}
			}
		}
	}
	while (true);
	delete &modem;
}
