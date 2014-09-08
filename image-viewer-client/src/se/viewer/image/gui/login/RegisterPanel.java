package se.viewer.image.gui.login;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import se.viewer.image.gui.Style;
import se.viewer.image.launcher.ServerCommunicator;

/**
 * Class that handles the register user panel for the login frame
 * @author Harald Brege
 */
public class RegisterPanel extends JPanel implements Observer {
	
	private static final long serialVersionUID = 2331036124156385298L;
	
	private LoginFrame2 parent;
	private boolean connected;

	private JTextField username;
	private JPasswordField password1;
	private JPasswordField password2;
	
	public RegisterPanel(LoginFrame2 parent) {
		ServerCommunicator.instance().addObserver(this);
		this.parent = parent;
		connected = false;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel label = new JLabel();
		label.setFont(Style.FONT_LABEL);
		label.setText("User name");
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.insets = new Insets(5, 20, 0, 20);
		add(label, c);
		
		username = new JTextField();
		username.setFont(Style.FONT_TEXTFIELD);
		username.setForeground(Color.GRAY);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(2, 20, 0, 20);
		add(username, c);
		
		label = new JLabel();
		label.setFont(Style.FONT_LABEL);
		label.setText("Password");
		c.fill = GridBagConstraints.NONE;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 20, 0, 20);
		add(label, c);
		
		password1 = new JPasswordField();
		password1.setFont(Style.FONT_TEXTFIELD);
		password1.setForeground(Color.GRAY);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(2, 20, 0, 20);
		add(password1, c);
		
		label = new JLabel();
		label.setFont(Style.FONT_LABEL);
		label.setText("Re-type password");
		c.fill = GridBagConstraints.NONE;
		c.gridy = 4;
		c.gridwidth = 1;
		add(label, c);
		
		password2 = new JPasswordField();
		password2.setFont(Style.FONT_TEXTFIELD);
		password2.setForeground(Color.GRAY);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 5;
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(password2, c);
		
		JCheckBox acceptBox = new JCheckBox();
		acceptBox.setFont(Style.FONT_BUTTON);
		acceptBox.setText("I accept arbitrary BS");
		acceptBox.setSelected(false);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.gridy = 6;
		c.gridwidth = 1;
		c.insets = new Insets(10, 15, 0, 0);
		add(acceptBox, c);
		
		JButton button = new JButton();
		button.setFont(Style.FONT_BUTTON);
		button.setText("REGISTER");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				register();
			}
		});
		c.gridx = 1;
		c.insets = new Insets(10, 15, 0, 15);
		add(button, c);
		
		JLabel loginLabel = new JLabel();
		loginLabel.setFont(Style.FONT_LABEL);
		loginLabel.setText("Already a user? Log in here!");
		loginLabel.addMouseListener(new LabelMouse(loginLabel, "login"));
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 15, 15, 15);
		add(loginLabel, c);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		connected = (boolean) arg1;
	}
	
	/**
	 * Called to send the current user registration info to the server
	 */
	private void register() {
		if(connected) {
			String user = username.getText();
			String pass = new String(password1.getPassword());
			String passConfirm = new String(password2.getPassword());
			
			if(pass.equals(passConfirm))
				ServerCommunicator.instance().registerUser(user, pass);
		}
	}
	
	//=======================================
	//	LISTENER CLASSES
	//---------------------------------------

	private class LabelMouse implements MouseListener {

		JLabel label;
		String purpose;
		
		LabelMouse(JLabel label, String purpose) {
			this.label = label;
			this.purpose = purpose;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(purpose == "login") {
				parent.loginMode();
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
			fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			label.setFont(label.getFont().deriveFont(fontAttributes));
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			label.setFont(Style.FONT_LABEL);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}
}
