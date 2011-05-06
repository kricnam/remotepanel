/*
 * MU203Modem.cpp
 *
 *  Created on: Apr 26, 2011
 *      Author: zhouziyuan
 */

#include "MU203Modem.h"
#include "TraceLog.h"

MU203Modem::MU203Modem() {
	m_szType = "HUAWEI MU203";
	m_szDefaultPort = "/dev/ttyUSB2";
}

MU203Modem::~MU203Modem() {
	// TODO Auto-generated destructor stub
}
