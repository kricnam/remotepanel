/*
 * EM669Modem.cpp
 *
 *  Created on: 2011-2-21
 *      Author: mxx
 */

#include "EM660Modem.h"
#include "TraceLog.h"

EM660Modem::EM660Modem() {
	m_szType = "HUAWEI EM660";
	m_szDefaultPort = "/dev/ttyUSB2";
}

EM660Modem::~EM660Modem() {
	// TODO Auto-generated destructor stub
}



