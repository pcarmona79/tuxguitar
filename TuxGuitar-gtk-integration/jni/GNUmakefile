RM?=rm -f
CFLAGS?=-I../../build-scripts/native-modules/common-include -fPIC `pkg-config --cflags gtk+-3.0`
LDFLAGS?=
LDLIBS?=`pkg-config --libs gtk+-3.0`
LDPATH?=
LIBRARY_NAME?=tuxguitar-gtk-integration-jni
LIBRARY_PREFIX?=lib
LIBRARY_SUFFIX?=.so

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_SUFFIX)
OBJECTS=org_herac_tuxguitar_gtk_TGGTK.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean: 
	$(RM) $(OBJECTS) $(LIBRARY)

#eof "$Id: GNUmakefile,v 1.10 2008/04/17 20:06:34 rzr Exp $"
