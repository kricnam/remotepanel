#include <stdlib.h>
#include <stdio.h>
#include "SerialPort.h"
#include "TCPPort.h"
#include "TraceLog.h"
#include "Config.h"
using namespace bitcomm;
#undef max
#define max(x,y) ((x) > (y) ? (x) : (y))
#define BUF_SIZE 6000
void dump(const char* buf,int size)
{
	for(int i=0;i<size;i++)
		printf("%u ",(unsigned)buf[i]);
	if (size) printf("\n");
}
int main(int argc,char** argv)
{
	SETTRACELEVEL(0);
	Config conf("/app/bin/agent.conf");
	TCPPort tcp;
	SerialPort com;
    fd_set read_set,write_set;
    char com_in_buf[BUF_SIZE];
    char com_out_buf[BUF_SIZE];
    int buf_in_avail, buf_in_written;
    int buf_out_avail, buf_out_written;
    string strServer;
    string strMPPort;
    time_t tLastRead;


    if (argc>2) strServer = argv[2];
    else strServer = conf.GetServerName();

    if (argc>1) strMPPort = argv[1];
    else strMPPort = conf.GetMPdev();

    if (strServer.empty()) strServer="192.168.1.2";

    int max_handle=0;

	buf_in_written = buf_in_avail = 0;
	buf_out_written = buf_out_avail = 0;
	struct timeval tv;
	string strTmp;
	time(&tLastRead);
	while(1)
	{
		usleep(1000);
		FD_ZERO(&read_set);
		FD_ZERO(&write_set);
		tv.tv_sec =30;
		tv.tv_usec = 0;

		strTmp = conf.GetServerName();
		if (!strTmp.empty() && strTmp!=strServer)
		{
			INFO("Server changed %s -> %s",strServer.c_str(),strTmp.c_str());
			tcp.Close();
			strServer = strTmp;
		}

		if (!tcp.IsOpen())
		{
			if (tcp.Open(strServer.c_str(),9998)==0)
			{
				buf_out_written = buf_out_avail = 0;
			}
			else
			{
				INFO("Open fail. restart pppd service");
				system("killall pppd");
				sleep(30);
				system("pppd call cdma &");
				sleep(60);
				continue;
			}
		}

		if(!com.IsOpen())
		{
			if (com.Open(strMPPort.c_str())==0)
				buf_in_written = buf_in_avail = 0;
			else
			{
				sleep(3);
				continue;
			}
		}

		if (tcp.GetHandle()!=-1)
		{
			max_handle = max(max_handle,tcp.GetHandle());
			FD_SET(tcp.GetHandle(),&read_set);
			FD_SET(tcp.GetHandle(),&write_set);
		}

		if (com.GetHandle()!=-1)
		{
			max_handle = max(max_handle,com.GetHandle());
			FD_SET(com.GetHandle(),&read_set);
			FD_SET(com.GetHandle(),&write_set);
		}

		int ret = select(max_handle + 1, &read_set, &write_set, NULL, &tv);

		if (ret == -1 && errno == EINTR)
			continue;
		if (ret == 0)
		{
			continue;
		}

        if (ret < 0)
        {
            ERRTRACE();
            exit (1);
        }

        if (tcp.GetHandle()>0 && FD_ISSET(tcp.GetHandle(),&read_set))
        {
        	try
        	{
				ret = tcp.Read(com_out_buf + buf_out_avail,
						BUF_SIZE - buf_out_avail);
				buf_out_avail += ret;
			}
        	catch(ChannelException& e)
        	{
        		tcp.Close();
        	}
        }

        if (com.GetHandle()>0 && FD_ISSET(com.GetHandle(),&read_set))
        {
        	time(&tLastRead);
        	ret = com.Read(com_in_buf+buf_in_avail,BUF_SIZE-buf_in_avail);
        	buf_in_avail+=ret;
        }

        if(tcp.GetHandle()>0 &&
        		(FD_ISSET(tcp.GetHandle(),&write_set)  || (buf_in_avail-buf_in_written) > 0))
        {
        	time_t now;
			time(&now);
			if ((now - tLastRead) > 1)
			{
				try {
					dump(com_in_buf + buf_in_written,buf_in_avail- buf_in_written);
					ret = tcp.Write(com_in_buf + buf_in_written, buf_in_avail
							- buf_in_written);
					buf_in_written += ret;
				} catch (ChannelException& e) {
					tcp.Close();
				}
			}
        }

        if (com.GetHandle()>0 && FD_ISSET(com.GetHandle(),&write_set))
        {
        	ret = com.Write(com_out_buf+buf_out_written,buf_out_avail-buf_out_written);
        	buf_out_written+=ret;
        }

        if (buf_out_written == buf_out_avail) buf_out_written = buf_out_avail = 0;
        if (buf_in_written==buf_in_avail) buf_in_written = buf_in_avail = 0;
	}
	return 0;
}


