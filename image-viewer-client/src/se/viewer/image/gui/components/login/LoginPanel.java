package se.viewer.image.gui.components.login;

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

import se.viewer.image.communication.ServerCommunicator;
import se.viewer.image.gui.handlers.LoginHandler;
import se.viewer.image.gui.style.Style;

public class LoginPanel extends JPanel implements Observer {
	
	private static final long serialVersionUID = 6572330916585974457L;

	private LoginHandler parent;
	private boolean connected;
	
	private JTextField username;
	private JPasswordField password;
	
	public LoginPanel(LoginHandler parent) {
		ServerCommunicator.instance().addObserver(this);
		this.parent = parent;
		connected = false;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		username = new JTextField();
		username.setFont(Style.FONT_TEXTFIELD);
		username.setForeground(Color.GRAY);
		username.setText("User name");
		username.addMouseListener(new FieldListener(username));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.insets = new Insets(10, 20, 0, 20);
		add(username, c);
		
		password = new JPasswordField();
		password.setFont(Style.FONT_TEXTFIELD);
		password.setForeground(Color.GRAY);
		password.setText("Password");
		password.addMouseListener(new FieldListener(password));
		c.gridy = 1;
		add(password, c);
		
		JCheckBox rememberMe = new JCheckBox();
		rememberMe.setFont(Style.FONT_BUTTON);
		rememberMe.setText("Remember me");
		rememberMe.setSelected(false);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.insets = new Insets(10, 15, 0, 0);
		add(rememberMe, c);
		
		JCheckBox rememberPass = new JCheckBox();
		rememberPass.setFont(Style.FONT_BUTTON);
		rememberPass.setText("Remember password");
		rememberPass.setSelected(false);
		c.gridy = 3;
		c.insets = new Insets(0, 15, 0, 0);
		add(rememberPass, c);
		
		JButton button = new JButton();
		button.setFont(Style.FONT_BUTTON);
		button.setText("LOG IN");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 2;
		c.insets = new Insets(10, 15, 0, 15);
		add(button, c);
		
		JLabel forgotLabel = new JLabel();
		forgotLabel.setFont(Style.FONT_LABEL);
		forgotLabel.setText("Forgot your password?");
		forgotLabel.addMouseListener(new LabelMouse(forgotLabel, "forgot"));
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.insets = new Insets(15, 15, 0, 15);
		add(forgotLabel, c);
		
		JLabel registerLabel = new JLabel();
		registerLabel.setFont(Style.FONT_LABEL);
		registerLabel.setText("Not a user? Register here!");
		registerLabel.addMouseListener(new LabelMouse(registerLabel, "register"));
		c.gridy = 5;
		c.insets = new Insets(5, 15, 15, 15);
		add(registerLabel, c);
		
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		connected = (boolean) arg1;
	}
	
	/**
	 * Called to log in with current credentials
	 */
	private void login() {
		if(connected) {
			String user = username.getText();
			String pass = new String(password.getPassword());
			ServerCommunicator.instance().login(user, pass);
		}
	}
	
	//=======================================
	//	LISTENER CLASSES
	//---------------------------------------

	/**
	 * Mouse listener for the labels at the bottom
	 * @author Harald Brege
	 */
	private class LabelMouse implements MouseListener {

		JLabel label;
		String purpose;
		
		public LabelMouse(JLabel label, String purpose) {
			this.label = label;
			this.purpose = purpose;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(purpose == "register") {
				parent.registerMode();
			}
			else if(purpose == "forgot") {
				label.setForeground(Color.RED);
				label.setText("UR FUKKED KID!");
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
	
	/**
	 * Mouse listener for the user name and password fields
	 * @author Harald Brege
	 */
	private class FieldListener implements MouseListener {

		private JTextField field;
		
		public FieldListener(JTextField parent) {
			field = parent;
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			field.selectAll();
			if(field.getForeground() == Color.GRAY)
				field.setForeground(Color.BLACK);
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent arg0) {}
		
	}
}
