/*
 * SerialPort.h
 *
 *  Created on: 2011-02-15
 *      Author: mxx
 */

#ifndef SERIALPORT_H_
#define SERIALPORT_H_

#include <string>

using namespace std;

class SerialPort
{
public:
	SerialPort();
	virtual ~SerialPort();
	int Open(const char* szDev);
	void Close();
	void SetCom(void);
	virtual int Open(void);
	virtual int Read(char* buf,int len);
	virtual int Write(const char* buf,int len);
	virtual int Write(const char* buf);
	virtual void Lock(void);
	virtual void Unlock(void);
	virtual bool IsOpen();
	const char* GetPort(void);
	virtual void SetTimeOut(int usec);
protected:
	int handle;
	int timeout;
	string strDevName;
};


#endif /* SERIALPORT_H_ */
