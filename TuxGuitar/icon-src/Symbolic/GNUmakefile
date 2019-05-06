SVGS = $(wildcard *.svg)
PNGS_IN = $(wildcard *.png)
OUTDIR = ../../share/skins/Symbolic/
OUTDIR_DARK = ../../share/skins/Symbolic-Dark/
PNGS_COPY = $(patsubst %.png,$(OUTDIR)%.png,$(PNGS_IN))
PNGS_OUT = $(patsubst %.svg,$(OUTDIR)%.png,$(SVGS))
PNGS_OUT_2X = $(patsubst %.svg,$(OUTDIR)%@2x.png,$(SVGS))
PNGS_COPY_DARK = $(patsubst %.png,$(OUTDIR_DARK)%.png,$(PNGS_IN))
PNGS_OUT_DARK = $(patsubst %.svg,$(OUTDIR_DARK)%.png,$(SVGS))
PNGS_OUT_2X_DARK = $(patsubst %.svg,$(OUTDIR_DARK)%@2x.png,$(SVGS))
COLOR_CONVERSION = 's/202020/e0e0e0/g'

.PHONY: all clean
all: $(PNGS_OUT) $(PNGS_OUT_2X) $(PNGS_COPY) $(PNGS_OUT_DARK) $(PNGS_OUT_2X_DARK) $(PNGS_COPY_DARK)

$(PNGS_OUT_2X): $(OUTDIR)%@2x.png: %.svg
	rsvg-convert -z 2 $< > $@

$(PNGS_OUT): $(OUTDIR)%.png: %.svg
	rsvg-convert $< > $@

$(PNGS_COPY): $(OUTDIR)%.png: %.png
	install $< $@

$(PNGS_OUT_2X_DARK): $(OUTDIR_DARK)%@2x.png: %.svg
	sed -e $(COLOR_CONVERSION) $< | rsvg-convert -z 2 > $@

$(PNGS_OUT_DARK): $(OUTDIR_DARK)%.png: %.svg
	sed -e $(COLOR_CONVERSION) $< | rsvg-convert > $@

$(PNGS_COPY_DARK): $(OUTDIR_DARK)%.png: %.png
	install $< $@

clean:
	rm -f $(PNGS_OUT) $(PNGS_OUT_2X) $(PNGS_COPY) $(PNGS_OUT_DARK) $(PNGS_OUT_2X_DARK) $(PNGS_COPY_DARK)
