SVGS = $(wildcard *.svg)
OUTDIR = ../../share/skins/Symbolic-Dark/
PNGS = $(patsubst %.svg,$(OUTDIR)%.png,$(SVGS))
PNGS2X = $(patsubst %.svg,$(OUTDIR)%@2x.png,$(SVGS))

.PHONY: all
all: $(PNGS) $(PNGS2X)

$(PNGS2X): $(OUTDIR)%@2x.png: %.svg
	rsvg-convert -z 2 $< > $@

$(PNGS): $(OUTDIR)%.png: %.svg
	rsvg-convert $< > $@
