package org.herac.tuxguitar.app.view.dialog.info;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeInfoAction;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public class TGSongInfoDialog {
	
	private static final float GROUP_WIDTH = 450;
	
	public void show(final TGViewContext context) {		
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		
		UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, true);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("composition.properties"));
		
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = uiFactory.createPanel(dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, GROUP_WIDTH, null, null);

		//-------NAME------------------------------------
		UILabel nameLabel = uiFactory.createLabel(group);
		nameLabel.setText(TuxGuitar.getProperty("composition.name"));
		groupLayout.set(nameLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField nameText = uiFactory.createTextField(group);
		nameText.setText(song.getName());
		groupLayout.set(nameText, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		//-------ARTIST------------------------------------
		UILabel artistLabel = uiFactory.createLabel(group);
		artistLabel.setText(TuxGuitar.getProperty("composition.artist"));
		groupLayout.set(artistLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField artistText = uiFactory.createTextField(group);
		artistText.setText(song.getArtist());
		groupLayout.set(artistText, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		//-------ALBUM------------------------------------
		UILabel albumLabel = uiFactory.createLabel(group);
		albumLabel.setText(TuxGuitar.getProperty("composition.album"));
		groupLayout.set(albumLabel, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField albumText = uiFactory.createTextField(group);
		albumText.setText(song.getAlbum());
		groupLayout.set(albumText, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		//-------AUTHOR------------------------------------
		UILabel authorLabel = uiFactory.createLabel(group);
		authorLabel.setText(TuxGuitar.getProperty("composition.author"));
		groupLayout.set(authorLabel, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField authorText = uiFactory.createTextField(group);
		authorText.setText(song.getAuthor());
		groupLayout.set(authorText, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		//-------DATE------------------------------------
		UILabel dateLabel = uiFactory.createLabel(group);
		dateLabel.setText(TuxGuitar.getProperty("composition.date"));
		groupLayout.set(dateLabel, 5, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField dateText = uiFactory.createTextField(group);
		dateText.setText(song.getDate());
		groupLayout.set(dateText, 5, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		//-------COPYRIGHT------------------------------------
		UILabel copyrightLabel = uiFactory.createLabel(group);
		copyrightLabel.setText(TuxGuitar.getProperty("composition.copyright"));
		groupLayout.set(copyrightLabel, 6, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField copyrightText = uiFactory.createTextField(group);
		copyrightText.setText(song.getCopyright());
		groupLayout.set(copyrightText, 6, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		//-------WRITER-------------------------------------
		UILabel writerLabel = uiFactory.createLabel(group);
		writerLabel.setText(TuxGuitar.getProperty("composition.writer"));
		groupLayout.set(writerLabel, 7, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField writerText = uiFactory.createTextField(group);
		writerText.setText(song.getWriter());
		groupLayout.set(writerText, 7, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		//-------TRANSCRIBER------------------------------------
		UILabel transcriberLabel = uiFactory.createLabel(group);
		transcriberLabel.setText(TuxGuitar.getProperty("composition.transcriber"));
		groupLayout.set(transcriberLabel, 8, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField transcriberText = uiFactory.createTextField(group);
		transcriberText.setText(song.getTranscriber());
		groupLayout.set(transcriberText, 8, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		//-------COMMENTS------------------------------------
		UILabel commentsLabel = uiFactory.createLabel(group);
		commentsLabel.setText(TuxGuitar.getProperty("composition.comments"));
		groupLayout.set(commentsLabel, 9, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextArea commentsText = uiFactory.createTextArea(group, true, false);
		commentsText.setText(song.getComments());
		groupLayout.set(commentsText, 9, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, 100f, null);
		
		//------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					String name = nameText.getText();
					String artist = artistText.getText();
					String album = albumText.getText();
					String author = authorText.getText();
					String date = dateText.getText();
					String copyright = copyrightText.getText();
					String writer = writerText.getText();
					String transcriber = transcriberText.getText();
					String comments = commentsText.getText();

					updateSongInfo(context.getContext(), song, name, artist, album, author, date, copyright, writer, transcriber, comments);
					dialog.dispose();
				}),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void updateSongInfo(TGContext context, TGSong song, String name, String artist, String album, String author, String date, String copyright, String writer, String transcriber, String comments) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeInfoAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_NAME, name);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ARTIST, artist);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ALBUM, album);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_AUTHOR, author);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_DATE, date);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COPYRIGHT, copyright);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_WRITER, writer);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_TRANSCRIBER, transcriber);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COMMENTS, comments);
		tgActionProcessor.processOnNewThread();
	}
}
