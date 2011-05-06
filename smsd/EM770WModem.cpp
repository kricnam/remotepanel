/*
 * EM770WModem.cpp
 *
 *  Created on: 2011-2-21
 *      Author: mxx
 */

#include "EM770WModem.h"
#include "TraceLog.h"

EM770WModem::EM770WModem() {
	m_szType = "HUAWEI EM770W";
	m_szDefaultPort = "/dev/ttyUSB2";
}

EM770WModem::~EM770WModem() {
	// TODO Auto-generated destructor stub
}



