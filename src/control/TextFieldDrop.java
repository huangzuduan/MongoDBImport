package control;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextField;

/**
 * 可拖放的文本框
 * 
 * @author Huang
 * @date 2013-6-22 下午2:52:41
 */
public class TextFieldDrop extends JTextField implements DropTargetListener {
	/**
	 * @Fields serialVersionUID : TODO
	 */ 
	private static final long serialVersionUID = 1L;

	public TextFieldDrop() {
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void drop(DropTargetDropEvent dtde) {
		try {
			// Transferable tr = dtde.getTransferable();
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				List<?> list = (List<?>) (dtde.getTransferable()
						.getTransferData(DataFlavor.javaFileListFlavor));
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					File f = (File) iterator.next();
					this.setText(f.getAbsolutePath());
					break;
				}
				dtde.dropComplete(true);
				this.updateUI();
			} else {
				dtde.rejectDrop();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		}
	}
}
