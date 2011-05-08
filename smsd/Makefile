CXXFLAGS =	-O2 -g -Wall -fmessage-length=0 -lpthread

SMSD_OBJS =	Modem.o SerialPort.o TraceLog.o pdu.o \
		EM660Modem.o  smsd.o
LIBS = -lutil

SMSD_TARGET =	smsd

$(SMSD_TARGET):	$(SMSD_OBJS)
	$(CXX) $(CXXFLAGS) -o $(SMSD_TARGET) $(SMSD_OBJS) $(LIBS)

all:	$(SMSD_TARGET)

clean:
	rm -f *.o $(SMSD_TARGET)
