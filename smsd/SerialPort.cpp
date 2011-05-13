/*
 * SerialPort.cpp
 *
 *  Created on: 2011-02-15
 *      Author: mxx
 */
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/file.h>
#include "SerialPort.h"
#include <unistd.h>
#include "stdio.h"
#include "stdlib.h"
#include "errno.h"
#include "termios.h"
#include "TraceLog.h"


using namespace std;

SerialPort::SerialPort()
{
	handle = -1;
	timeout = 20000;
}

SerialPort::~SerialPort()
{
	if (handle > 0)
		Close();
}

bool SerialPort::IsOpen()
{
	return handle != -1;
}

const char* SerialPort::GetPort(void)
{
	return strDevName.c_str();
}

void SerialPort::SetTimeOut(int usec)
{
	timeout= usec;
}

int SerialPort::Open(void)
{
	return Open(strDevName.c_str());
}

int SerialPort::Open(const char* szDev)
{
	Close();

	if (strDevName != szDev)
		strDevName = szDev;
	TRACE("%s!%s!",szDev, strDevName.c_str());

	handle = open(strDevName.c_str(), O_RDWR | O_NOCTTY | O_NONBLOCK);
	if (handle < 0)
	{
		ERROR("SerialPort::Open!errno=%d!",errno);
		ERRTRACE();
		return -1;
	}
	SetCom();
	return 0;
}

void SerialPort::SetCom(void)
{
	if (handle < 0)
		return;

	struct termios newtio;
	tcflush(handle, TCIFLUSH);

	tcgetattr(handle, &newtio); /* save current port settings */
	/* set new port settings for canonical input processing */
	newtio.c_cflag = 0;
	newtio.c_cflag &= ~(CSIZE | PARENB);
	newtio.c_cflag |= B19200 | CS8 | CLOCAL | CREAD;

	newtio.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG | IEXTEN);

	newtio.c_iflag &= ~(IGNBRK | BRKINT | PARMRK | ISTRIP | INLCR | IGNCR
			| ICRNL | IXON);

	newtio.c_oflag &= ~OPOST;

	/* set input mode (non-canonical, no echo,...) */
	newtio.c_cc[VMIN] = 0;
	newtio.c_cc[VTIME] = 0;

	int n = tcsetattr(handle, TCSANOW, &newtio);
	if (n < 0)
		ERRTRACE();
}

void SerialPort::Lock(void)
{
	if (flock(handle, LOCK_EX) < 0)
	{
		ERRTRACE();
	}
}

void SerialPort::Close()
{
	if (handle > 0)
	{
		tcflush(handle, TCIOFLUSH);
		close(handle);
		handle = -1;
	}
}

void SerialPort::Unlock(void)
{
	if (flock(handle, LOCK_UN) < 0)
		ERRTRACE();
}

int SerialPort::Read(char* buf, int len)
{
	if (len == 0)
		return 0;
	if (handle == -1)
		Open(strDevName.c_str());
	int try_again = 2;
	int n;
	do
	{
		n = read(handle, buf, len);
		if (n > 0)
			return n;
		if (n == -1)
		{
			ERRTRACE();
			Close();
			return 0;
		}
		if (try_again--)
		{
			usleep(timeout);
			continue;
		}
	} while (try_again);
	return n;
}

int SerialPort::Write(const char* buf)
{
	return Write(buf,strlen(buf));
}

int SerialPort::Write(const char* buf, int len)
{
	if (len == 0)
		return 0;
	if (handle == -1)
		Open(strDevName.c_str());

	DUMP(buf,len);

	int n = write(handle, buf, len);
	tcdrain(handle);
	if (n > 0)
		return n;
	if (n == -1)
	{
		ERRTRACE();
		Close();
	}
	return 0;
}
