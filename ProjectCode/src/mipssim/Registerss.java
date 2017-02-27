package mipssim;

import java.util.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

class Register extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int value = 0;

	public String name;

	public EditWindow window = new EditWindow();

	public Register(String name) {
		this.name = name;
		value = 0;
	}

	class EditWindow extends JFrame {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextField jt = new JTextField(30);
		private JButton save;

		public EditWindow() {

			this.setBounds(900, 200, 200, 200);
			this.setTitle("Edit");
			this.setResizable(false);

			Container c = this.getContentPane();
			c.setLayout(null);
			jt.setPreferredSize(new Dimension(20, 20));
			jt.setBounds(60, 30, 60, 35);
			jt.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			save = new JButton("save");
			save.setBounds(50, 80, 80, 50);

			save.addActionListener(new ActionListener() {// when press save
				public void actionPerformed(ActionEvent e) {
					if (jt.getText().isEmpty()) // if nothing is written, put 0
												// because it crashes otherwise
						value = 0;
					else
						value = Integer.parseInt(jt.getText());
					dispose();
				}
			});

			jt.addActionListener(new ActionListener() {// when press enter
				public void actionPerformed(ActionEvent e) {
					if (jt.getText().isEmpty()) // if nothing is written, put 0
												// because it crashes otherwise
						value = 0;
					else
						value = Integer.parseInt(jt.getText());
					dispose();
				}
			});

			c.add(jt);
			c.add(save);

		}

	}

}

public class Registerss {

	public ArrayList<Register> RegArr = new ArrayList<Register>(32);

	public Register $0 = new Register("$0");

	public Register $at = new Register("$at");

	public Register $v0 = new Register("$v0");

	public Register $v1 = new Register("$v1");

	public Register $a0 = new Register("$a0");

	public Register $a1 = new Register("$a1");

	public Register $a2 = new Register("$a2");

	public Register $a3 = new Register("$a3");

	public Register $t0 = new Register("$t0");

	public Register $t1 = new Register("$t1");

	public Register $t2 = new Register("$t2");

	public Register $t3 = new Register("$t3");

	public Register $t4 = new Register("$t4");

	public Register $t5 = new Register("$t5");

	public Register $t6 = new Register("$t6");

	public Register $t7 = new Register("$t7");

	public Register $s0 = new Register("$s0");

	public Register $s1 = new Register("$s1");

	public Register $s2 = new Register("$s2");

	public Register $s3 = new Register("$s3");

	public Register $s4 = new Register("$s4");

	public Register $s5 = new Register("$s5");

	public Register $s6 = new Register("$s6");

	public Register $s7 = new Register("$s7");

	public Register $t8 = new Register("$t8");

	public Register $t9 = new Register("$t9");

	public Register $k0 = new Register("$k0");

	public Register $k1 = new Register("$k1");

	public Register $gp = new Register("$gp");

	public Register $sp = new Register("$sp");

	public Register $fp = new Register("$fp");

	public Register $ra = new Register("$ra");

	public Registerss() {
		RegArr.add($0);
		RegArr.add($at);
		RegArr.add($v0);
		RegArr.add($v1);
		RegArr.add($a0);
		RegArr.add($a1);
		RegArr.add($a2);
		RegArr.add($a3);
		RegArr.add($t0);
		RegArr.add($t1);
		RegArr.add($t2);
		RegArr.add($t3);
		RegArr.add($t4);
		RegArr.add($t5);
		RegArr.add($t6);
		RegArr.add($t7);
		RegArr.add($s0);
		RegArr.add($s1);
		RegArr.add($s2);
		RegArr.add($s3);
		RegArr.add($s4);
		RegArr.add($s5);
		RegArr.add($s6);
		RegArr.add($s7);
		RegArr.add($t8);
		RegArr.add($t9);
		RegArr.add($k0);
		RegArr.add($k1);
		RegArr.add($gp);
		RegArr.add($sp);
		RegArr.add($fp);
		RegArr.add($ra);
	}

	public int getRegNumber(String a) {

		for (int i = 0; i < 32; i++) {
			if (RegArr.get(i).name.equalsIgnoreCase(a))
				return i;
			if (a.equals("$zero"))
				return 0;
		}
		return -1;// if passed an invailed reg name

	}

	public Register returnReg(String a) {// return Reg value
		for (int i = 0; i < 32; i++) {
			if (RegArr.get(i).name.equalsIgnoreCase(a))
				return RegArr.get(i);
			if (a.equals("$zero"))
				return RegArr.get(0);
		}
		System.out.println("ERROR, INVAILD REGISTER, RETURING ZERO");
		// should throw exception
		return $0;///
	}

}
