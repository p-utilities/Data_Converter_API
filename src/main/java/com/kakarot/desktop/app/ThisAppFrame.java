package com.kakarot.desktop.app;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.kakarot.data.converter.Converter;
import com.kakarot.data.converter.exceptions.CategoryDuplicateProductException;
import com.kakarot.data.converter.exceptions.ProductIdException;
import com.kakarot.data.converter.exceptions.ProductIdNotUniqueException;

public class ThisAppFrame {

	private JFrame frame;
	private JLabel inputLabel;
	private JLabel outputLabel;
	private JComboBox<String> comboBox;
	private JTextField nameField;
	private JLabel typeLabel;
	private JButton button;
	private final String FROM;
	private final String TO;
	private final Converter CONVERTER = new Converter();
	private JLabel resultMessageLabel = new JLabel();
	private boolean filesExist = false;

	public ThisAppFrame(String from, String to) {
		this.FROM = from;
		this.TO = to;

		frame = new JFrame();
		frame.setSize(1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		inputLabel = new JLabel("Input directory path: " + from);
		inputLabel.setBounds(50, 50, 800, 20);

		outputLabel = new JLabel("Output directory path: " + to);
		outputLabel.setBounds(50, 80, 800, 20);

		comboBox = new JComboBox<String>(findAllFiles(from));
		comboBox.addActionListener(action -> setComboBoxValue());
		comboBox.setBounds(50, 110, 200, 20);

		nameField = new JTextField("file name");
		nameField.setBounds(50, 140, 400, 20);

		typeLabel = new JLabel();
		typeLabel.setBounds(50, 170, 200, 20);

		button = new JButton();
		button.addActionListener(action -> submitBtnListener());

		button.setBounds(150, 200, 100, 20);
		button.setText("Submit");

		setComboBoxValue();

		frame.add(inputLabel);
		frame.add(comboBox);
		frame.add(outputLabel);
		frame.add(nameField);
		frame.add(typeLabel);
		frame.add(button);

		resultMessageLabel.setBounds(50, 230, 200, 200);
		resultMessageLabel.setSize(resultMessageLabel.getPreferredSize());
		frame.add(resultMessageLabel);

		frame.setLayout(null);
		frame.setVisible(true);
	}

	private void setComboBoxValue() {
		if (comboBox.getSelectedIndex() != -1) {
			String choosenFile = comboBox.getSelectedItem().toString();
			String type = choosenFile.substring(choosenFile.lastIndexOf(".") + 1);
			if (type.equals("xml"))
				type = "csv";
			else
				type = "xml";
			typeLabel.setText(type);
		}
	}

	private String[] findAllFiles(String path) {
		if (Paths.get(path).toFile().exists()) {
			var files = Paths.get(path).toFile().listFiles();
			List<String> fileNames = new ArrayList<String>();

			for (File file : files) {
				if (file.getName().endsWith(".xml") || file.getName().endsWith(".csv"))
					fileNames.add(file.getName());
			}

			if (fileNames.size() > 0)
				filesExist = true;

			return (String[]) fileNames.toArray(new String[fileNames.size()]);
		} else {
			return new String[] {};
		}
	}

	private void submitBtnListener() {
		if (filesExist) {
			String fileToRead = comboBox.getSelectedItem().toString();
			String absolutePathFrom = FROM + fileToRead;

			String type = typeLabel.getText();
			String absolutePathTo = TO + nameField.getText() + "." + type;

			String resultValue = "";

			try {

				CONVERTER.convertData(absolutePathFrom, absolutePathTo);

				// IF THERE ARE NO ECEPTION, FILE IS CONVERTED SUCCESSFULY
				System.exit(0);

			} catch (CategoryDuplicateProductException catExc) {
				resultValue = catExc.getMessage();
			} catch (ProductIdException prodExc) {
				resultValue = prodExc.getMessage();
			} catch (ProductIdNotUniqueException prodIdExc) {
				resultValue = prodIdExc.getMessage();
			} catch (Exception e) {

				resultValue = "Somthing went wrong in api.";

				e.printStackTrace();

			}

			resultMessageLabel.setText("<html>" + resultValue + "<br>Please try again <html>");
			
		} else {
			resultMessageLabel.setText("There is no file to convert.");
		}
		
		resultMessageLabel.setSize(resultMessageLabel.getPreferredSize());
	}
}
