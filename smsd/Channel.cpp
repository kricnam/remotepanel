/*
 * Channel.cpp
 *
 *  Created on: 2009-12-29
 *      Author: mxx
 */

#include "Channel.h"

namespace bitcomm
{

Channel::Channel()
{
	tmLastAction.tv_sec=0;
	tmLastAction.tv_usec=0;
}

Channel::~Channel()
{
}

}
