package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.ui.event.UIMouseDragListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;

public class TGTableDividerListener implements UIMouseDragListener {

	public static final String LEFT_CONTROL = "leftControl";
	public static final String RIGHT_CONTROL = "rightControl";

	private TGTable table;
	private TGTableHeader leftColumn;
	private TGTableHeader rightColumn;
	private final boolean atEnd;

	public TGTableDividerListener(TGTable table, TGTableHeader leftColumn, TGTableHeader rightColumn, boolean atEnd) {
		this.table = table;
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
		this.atEnd = atEnd;
	}

	public void onMouseDrag(UIMouseEvent event) {
		float move = event.getPosition().getX();

		MoveWidths widths = computeWidths(move);

		// try to snap to the minimum size
		if (widths.left == null && move < 0f) {
			float minWidth = this.computeMinWidth(this.leftColumn.getControl(), false);
			move = minWidth - this.leftColumn.getControl().getBounds().getWidth();
			widths = computeWidths(move);
		} else if (widths.right == null && move > 0f) {
			float minWidth = this.computeMinWidth(this.rightColumn.getControl(), atEnd);
			move = this.rightColumn.getControl().getBounds().getWidth() - minWidth;
			widths = computeWidths(move);
		}

		if( widths.left != null && widths.right != null) {
			UITableLayout uiLayout = (UITableLayout) this.table.getColumnControl().getLayout();
			uiLayout.set(this.leftColumn.getControl(), UITableLayout.MINIMUM_PACKED_WIDTH, widths.left);
			if (!atEnd) {
				uiLayout.set(this.rightColumn.getControl(), UITableLayout.MINIMUM_PACKED_WIDTH, widths.right);
			}

			this.table.update();
			TGWindow.getInstance(this.table.getContext()).updateMinimumSize();
		}
	}

	MoveWidths computeWidths(float move) {
		MoveWidths widths = new MoveWidths();
		widths.left = this.computeWidth(this.leftColumn.getControl(), move, false);
		widths.right = this.computeWidth(this.rightColumn.getControl(), -move, atEnd);
		return widths;
	}

	private Float computeWidth(UIControl control, float move, boolean isLast) {
		float minWidth = computeMinWidth(control, isLast);

		float newWidth = control.getBounds().getWidth() + move;
		if (newWidth >= minWidth) {
			return newWidth;
		}
		return null;
	}

	private Float computeMinWidth(UIControl control, boolean isLast) {
		if (isLast) {
			UITableLayout uiLayout = (UITableLayout) this.table.getColumnControl().getLayout();
			float minWidth = uiLayout.get(control, UITableLayout.MINIMUM_PACKED_WIDTH);
			// need to compensate for scroll bar overlay which last column will always overflow into
			minWidth += this.table.getViewer().getTrimSize().getWidth();
			return minWidth;
		}
		UISize currentPackedSize = control.getPackedSize();
		control.computePackedSize(null, null);
		UISize computedPackedSize = control.getPackedSize();
		control.computePackedSize(currentPackedSize.getWidth(), currentPackedSize.getHeight());
		return computedPackedSize.getWidth();
	}

	private static class MoveWidths {
		public Float left;
		public Float right;
	}
}
