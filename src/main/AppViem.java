package main;

import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import control.DropTextField;

import model.Excel2Mongo;

/**
 * JAVA 导表工具
 * 
 * @author Huang
 * @date 2013-6-20 下午7:48:18
 */
public class AppViem extends JFrame implements ActionListener,
		DropTargetListener {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;
	private DropTextField importFilePath;
	private JButton buttonSelect; // 选择按钮
	private JButton buttonImport; // 导入按钮
	public static JTextField StatusText;// 状态显示

	public AppViem() {

		this.setTitle("MongoDB工具");
		Container con = this.getContentPane();
		buttonSelect = new JButton("选择文件");
		buttonSelect.setBounds(100, 10, 200, 26);
		buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				openChooser();
			}
		});

		importFilePath = new DropTextField();
		importFilePath.setBounds(10, 50, 360, 60);
		importFilePath.setText("可将xls配置表拖放到这里");
		buttonImport = new JButton("导入");
		buttonImport.setBounds(100, 120, 200, 30);
		buttonImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppViem.StatusText.setText("0");
				try {
					AppViem.StatusText.setText(Excel2Mongo
							.importDB(importFilePath.getText()));
				} catch (Exception ee) {
					ee.printStackTrace();
					AppViem.StatusText.setText(ee.getMessage());
				}
			}
		});

		StatusText = new JTextField();
		StatusText.setBounds(10, 160, 360, 80);
		StatusText.setText("等待操作...");

		con.add(buttonSelect);
		con.add(importFilePath);
		con.add(buttonImport);
		con.add(StatusText);

		// 窗体组件初始化
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 设置布局方式为绝对定位
		this.setLayout(null);
		this.setBounds(0, 0, 400, 300);
		// 窗体大小不能改变
		this.setResizable(true);
		// 居中显示
		this.setLocationRelativeTo(null);
		// 窗体可见
		this.setVisible(true);

	}

	public static void main(String args[]) {
		new AppViem();
	}

	/**
	 * 打开文件选择框
	 * @author Huang
	 * @date 2013-6-25 下午5:00:27
	 * @return void
	 */
	public void openChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("xls文件",
				"xls");
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("选择xls文件");
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file1 = chooser.getCurrentDirectory();
			File file = chooser.getSelectedFile();
			String filename = file.getName();
			String filepath = file1.getAbsolutePath();
			importFilePath.setText(filepath + "\\" + filename);// 将文件路径设到JTextField
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		// TODO Auto-generated method stub
		try {
			// Transferable tr = dtde.getTransferable();
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				List<?> list = (List<?>) (dtde.getTransferable()
						.getTransferData(DataFlavor.javaFileListFlavor));
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					File f = (File) iterator.next();
					importFilePath.setText(f.getAbsolutePath() + "\n");
					break;
				}
				dtde.dropComplete(true);
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