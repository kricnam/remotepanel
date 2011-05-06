
#ifndef CALLINGPROCESS_H
#define CALLINGPROCESS_H

#include <string>
#include "Process.h"
class Modem;
/**
  * class CallingProcess
  * 
  */

class CallingProcess:public virtual Process
{
public:

  // Constructors/Destructors
  CallingProcess ( );
  virtual ~CallingProcess ( );

  virtual int Run(const char* szDev,int timeout);
  virtual int Report();
  int Call(Modem& modem,int timeout);
  int Answer(Modem& modem,int timeout);
  int  m_nTimeout;
  int  m_nDuriation;
  bool m_bCallOut;
  string m_strCallee;
protected:
  int id;
  int type;
  int duriation;
  int status;
  int cause;
  int valid;

  time_t startTime;
  time_t dialingTime;
  time_t connectTime;
  time_t answerTime;
  time_t endTime;
  time_t ringTime;
  string strCaller;
};

#endif // CALLINGPROCESS_H
