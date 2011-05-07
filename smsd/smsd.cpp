#include "CallingProcess.h"
#include "Modem.h"
#include "TraceLog.h"
#include "stdlib.h"
#include "stdio.h"
#include <sstream>
#include <unistd.h>
// Constructors/Destructors
//  

void print_useage(const char* sz) {
	fprintf(
			stderr,
			"Usage: %s [-a] [-l duriation_secs][-t timeout_secs] [-d] dial_No [-p dev_port]\n",
			sz);
}

int main(int argc, char** argv) {
	SETTRACELEVEL(0);
	string strPort;

	strPort = "/dev/ttyUSB2";

	int opt;

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

}



int Run(const char* szDev, int timeout) {
	Modem& modem = *Modem::CreateInstance(szDev);
	int rt = 0;
	string ind;
	if (&modem) 
	{
		modem.Open(modem.m_szDefaultPort);
		modem.Init();
		m_strDevType = modem.m_szType;

		//TODO:Enable SMS receiver
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

		do {
			ind = modem.WaitIndication(now, timeout);
			if (modem.IndicateRing(ind))
			{
				system("killall pppd");
				sleep(5);
				system("pppd call cdma");
			}
			if (modem.IndicateSMessage(ind,mem,id))
			{
				INFO("Get %s",ind.c_str());
				if (modem.ReadSMSPDU(id,stat,len,strpdu))
				{
					INFO("Get %d %d,[%d]%s",stat,len,strpdu.size(),strpdu.c_str());
					PDU pdu(strpdu.c_str());
					if (!pdu.parse())
						ERROR("parse pdu error");
					else
					{
						printf("PDU: %s\n", pdu.getPDU());
						printf("SMSC: %s\n", pdu.getSMSC());
						printf("Sender: %s\n", pdu.getNumber());
						printf("Sender Number Type: %s\n", pdu.getNumberType());
						printf("Date: %s\n", pdu.getDate());
						printf("Time: %s\n", pdu.getTime());
						printf("UDH Type: %s\n", pdu.getUDHType());

						char* tmp = (char*)pdu.getMessage();
						DUMP(tmp,pdu.getMessageLen());
						string msg = tmp;
						if(msg.find("SVR:")!=string::npos)
						{
							//Extrace IP;
							
						}
					}
				}
		} while (true);

		delete &modem;
	} 
	else 
	{
		INFO("failed to create modem instance.");
	}
}
