package com.logicaldoc.web.applet;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class SelectFolderApplet extends javax.swing.JApplet {
	private static final long serialVersionUID = 1L;

	private JButton browseButton;

	/**
	 * Auto-generated main method to display this JApplet inside a new JFrame.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				SelectFolderApplet inst = new SelectFolderApplet();
				frame.getContentPane().add(inst);
				((JComponent) frame.getContentPane()).setPreferredSize(inst.getSize());
				frame.pack();
				frame.setVisible(true);
			}
		});

	}

	public SelectFolderApplet() {
		super();
		initGUI();
	}

	@Override
	public void init() {
		browseButton.setText(getParameter("label"));
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout(1, 1);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			thisLayout.setColumns(1);
			getContentPane().setLayout(thisLayout);
			this.setSize(80, 19);
			getContentPane().setBackground(new java.awt.Color(255, 255, 255));
			{
				browseButton = new JButton();
				getContentPane().add(browseButton);
				browseButton.setText("Browse");
				browseButton.setSize(60, 19);
				browseButton.setPreferredSize(new java.awt.Dimension(74, 19));
				browseButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						browseButtonActionPerformed(evt);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void browseButtonActionPerformed(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int n = chooser.showOpenDialog(this);
		if (n == JFileChooser.APPROVE_OPTION) {
			System.out.println("Selected dir: " + chooser.getSelectedFile().getPath());
			String path = chooser.getSelectedFile().getPath().replaceAll("\\\\", "/");
			if(path.startsWith("//"))
				path="\\\\"+path.substring(2);
			String javascript = getParameter("javascript");
			javascript = javascript.replaceAll("_FOLDER_", path);
			try {
				getAppletContext().showDocument(new URL("javascript:" + javascript));
			} catch (MalformedURLException me) {
				me.printStackTrace();
			}
		}
	}
}