package mipssim;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class iniMem extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton initialize;
	private JTextArea loc;
	private JTextArea data;
	private JTextArea instStart;
	private JButton instStartBtn;

	public iniMem() {
		loc = new JTextArea();
		data = new JTextArea();
		loc.setText("Memory location");
		loc.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (loc.getText().equals("Memory location"))
					loc.setText("");
			}
		});
		data.setText("Data to initialize");
		data.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (data.getText().equals("Data to initialize"))
					data.setText("");
			}
		});
		Container c = getContentPane();
		initialize = new JButton();
		initialize.setText("Initialize");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(400, 600);
		this.setTitle("Initialize Data Memory");
		this.setResizable(false);
		loc.setBackground(Color.white);
		data.setBackground(Color.white);
		this.setLayout(null);
		loc.setPreferredSize(new Dimension(10, 40));
		loc.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		data.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		data.setPreferredSize(new Dimension(20, 80));
		initialize.setPreferredSize(new Dimension(20, 50));
		this.setVisible(false);
		loc.setBounds(40, 170, 120, 40);
		data.setBounds(40, 300, 120, 40);
		initialize.setBounds(40, 400, 120, 40);
		c.add(loc);
		c.add(data);
		c.add(initialize);
		initialize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = Integer.parseInt(loc.getText());
				if (x <= 4000 && x % 4 == 0) {
					Programy.DataMem[(x / 4)] = (Integer.parseInt(data.getText()));
					System.out.println("Added " + data.getText() + " in loc: " + x);
				} else if (x > 4000) {
					System.out.println("ERROR::Location BIGGER THAN 4000");
				} else if (x % 4 != 0) {
					System.out.println("ERROR::Enter a location divisable by Four!");
				}

			}
		});

		instStart = new JTextArea("Instruction Address");
		c.add(instStart);
		instStart.setBounds(240, 170, 120, 40);
		instStart.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		instStart.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				instStart.setText("");
			}
		});

		instStartBtn = new JButton("Save Inst Address");
		instStartBtn.setBounds(240, 300, 120, 40);
		c.add(instStartBtn);

		instStartBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int x = Integer.parseInt(instStart.getText());
				if (x > 4000)
					JOptionPane.showMessageDialog(null, "Address can't be found!", "", JOptionPane.WARNING_MESSAGE);
				else if (x % 4 != 0)
					JOptionPane.showMessageDialog(null, "Address must be divisable by four!", "",
							JOptionPane.WARNING_MESSAGE);
				else {
					Programy.CurrentInstruction = x / 4;
				}

			}
		});

	}

}

public class Frame extends JFrame // implements MouseWheelListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ArrayList<String> LinesArr = new ArrayList<String>();
	public static JTextArea Program;
	public static JTextArea DataPathOP;
	public static JTextArea memContents;
	private JButton[] RegBtns = new JButton[32];
	private JLabel[] RegValues = new JLabel[32];
	private JPanel TopPnl;
	private JPanel Regs1;
	private JPanel Regs2;
	private JPanel Regs3;
	private JPanel Regs4;
	private JPanel Regs1V;
	private JPanel Regs2V;
	private JPanel Regs3V;
	private JPanel Regs4V;
	private JPanel OtherPnl;
	private JPanel BtnPnl;
	private JPanel Stops;
	private JPanel Speedy;
	private JPanel Center;

	private JButton Reset;
	// private JPanel RegBtnsPanel;
	// private JPanel RegValuesPanel;
	private JButton RunAll;
	private JButton RunOneLine;
	private JPanel RUNPANEL;
	public static boolean Compile = true;

	// private JButton Stop;

	private JButton Advanced;

	private JButton memory;
	private iniMem initialize;

	public Frame() {
		initialize = new iniMem();
		memory = new JButton();
		memory.setText("Memory");
		memory.setPreferredSize(new Dimension(20, 50));
		memory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initialize.setVisible(true);
			}

		});
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1280, 720);
		this.setTitle("Mips simulator");
		this.setResizable(true);

		Container c = this.getContentPane();

		TopPnl = new JPanel();
		Regs1 = new JPanel();
		Regs2 = new JPanel();
		Regs3 = new JPanel();
		Regs4 = new JPanel();
		Regs1V = new JPanel();
		Regs2V = new JPanel();
		Regs3V = new JPanel();
		Regs4V = new JPanel();

		RUNPANEL = new JPanel();
		OtherPnl = new JPanel();

		BtnPnl = new JPanel();
		Stops = new JPanel();
		Speedy = new JPanel();
		Program = new JTextArea("Write program here");
		RunAll = new JButton("Run All");
		RunOneLine = new JButton("Run 1Line");

		Center = new JPanel();
		Advanced = new JButton("Advanced");
		Reset = new JButton("Reset");

		Program.setLineWrap(true);
		Program.setBounds(10, 40, 200, 600);
		Program.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		Program.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (Program.getText().equals("Write program here"))
					Program.setText("");
			}
		});

		memContents = new JTextArea("Memory Contents");
		memContents.setLineWrap(true);
		memContents.setEditable(false);

		DataPathOP = new JTextArea("Datapath output");
		DataPathOP.setLineWrap(true);
		DataPathOP.setEditable(false);

		for (int i = 0; i < 8; i++) {
			RegBtns[i] = new JButton((Mainy.Registers.RegArr.get(i)).name);
			Regs1.add(RegBtns[i]);
		}
		for (int i = 8; i < 16; i++) {
			RegBtns[i] = new JButton((Mainy.Registers.RegArr.get(i)).name);
			Regs2.add(RegBtns[i]);
		}
		for (int i = 16; i < 24; i++) {
			RegBtns[i] = new JButton((Mainy.Registers.RegArr.get(i)).name);
			Regs3.add(RegBtns[i]);
		}
		for (int i = 24; i < 32; i++) {
			RegBtns[i] = new JButton((Mainy.Registers.RegArr.get(i)).name);
			Regs4.add(RegBtns[i]);
		}

		/// ACTIONLISTENER TO ALL BUTTONS // CANT BE DONE BY LOOP IDK WHY
		RegBtns[1].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(1)).window.setVisible(true);
			}
		});
		RegBtns[2].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(2)).window.setVisible(true);
			}
		});
		RegBtns[3].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(3)).window.setVisible(true);
			}
		});
		RegBtns[4].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(4)).window.setVisible(true);
			}
		});
		RegBtns[5].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(5)).window.setVisible(true);
			}
		});
		RegBtns[6].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(6)).window.setVisible(true);
			}
		});
		RegBtns[7].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(7)).window.setVisible(true);
			}
		});
		RegBtns[8].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(8)).window.setVisible(true);
			}
		});
		RegBtns[9].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(9)).window.setVisible(true);
			}
		});
		RegBtns[10].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(10)).window.setVisible(true);
			}
		});
		RegBtns[11].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(11)).window.setVisible(true);
			}
		});
		RegBtns[12].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(12)).window.setVisible(true);
			}
		});
		RegBtns[13].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(13)).window.setVisible(true);
			}
		});
		RegBtns[14].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(14)).window.setVisible(true);
			}
		});
		RegBtns[15].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(15)).window.setVisible(true);
			}
		});
		RegBtns[16].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(16)).window.setVisible(true);
			}
		});
		RegBtns[17].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(17)).window.setVisible(true);
			}
		});
		RegBtns[18].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(18)).window.setVisible(true);
			}
		});
		RegBtns[19].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(19)).window.setVisible(true);
			}
		});
		RegBtns[20].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(20)).window.setVisible(true);
			}
		});
		RegBtns[21].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(21)).window.setVisible(true);
			}
		});
		RegBtns[22].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(22)).window.setVisible(true);
			}
		});
		RegBtns[23].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(23)).window.setVisible(true);
			}
		});
		RegBtns[24].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(24)).window.setVisible(true);
			}
		});
		RegBtns[25].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(25)).window.setVisible(true);
			}
		});
		RegBtns[26].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(26)).window.setVisible(true);
			}
		});
		RegBtns[27].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(27)).window.setVisible(true);
			}
		});
		RegBtns[28].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(28)).window.setVisible(true);
			}
		});
		RegBtns[29].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(29)).window.setVisible(true);
			}
		});
		RegBtns[30].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(30)).window.setVisible(true);
			}
		});
		RegBtns[31].addActionListener(new ActionListener() {// when pressed
			public void actionPerformed(ActionEvent e) {
				(Mainy.Registers.RegArr.get(31)).window.setVisible(true);
			}
		});

		/////////// panels stuff

		for (int i = 0; i < 8; i++) {
			RegValues[i] = new JLabel("      " + (Mainy.Registers.RegArr.get(i)).value);
			Regs1V.add(RegValues[i]);
			RegValues[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}
		for (int i = 8; i < 16; i++) {
			RegValues[i] = new JLabel("      " + (Mainy.Registers.RegArr.get(i)).value);
			Regs2V.add(RegValues[i]);
			RegValues[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}
		for (int i = 16; i < 24; i++) {
			RegValues[i] = new JLabel("      " + (Mainy.Registers.RegArr.get(i)).value);
			Regs3V.add(RegValues[i]);
			RegValues[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}
		for (int i = 24; i < 32; i++) {
			RegValues[i] = new JLabel("      " + (Mainy.Registers.RegArr.get(i)).value);
			Regs4V.add(RegValues[i]);
			RegValues[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}

		c.add(TopPnl, BorderLayout.EAST);
		c.add(Center, BorderLayout.CENTER);
		Center.setLayout(new BorderLayout());

		Center.add(DataPathOP, BorderLayout.WEST);
		DataPathOP.setPreferredSize(new Dimension(420, 5));
		// DataPathOP.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		Center.add(memContents, BorderLayout.EAST);
		memContents.setPreferredSize(new Dimension(200, 5));
		// memContents.setBorder(BorderFactory.createLineBorder(Color.BLACK,
		// 1));

		TopPnl.setLayout(new GridLayout(8, 1));

		Regs1.setLayout(new GridLayout(1, 1));
		Regs1V.setLayout(new GridLayout(1, 1));
		Regs2.setLayout(new GridLayout(1, 1));
		Regs2V.setLayout(new GridLayout(1, 1));
		Regs3.setLayout(new GridLayout(1, 1));
		Regs3V.setLayout(new GridLayout(1, 1));
		Regs4.setLayout(new GridLayout(1, 1));
		Regs4V.setLayout(new GridLayout(1, 1));

		TopPnl.add(Regs1, BorderLayout.NORTH);
		TopPnl.add(Regs1V, BorderLayout.AFTER_LAST_LINE);

		TopPnl.add(Regs2, BorderLayout.AFTER_LAST_LINE);
		TopPnl.add(Regs2V, BorderLayout.AFTER_LAST_LINE);

		TopPnl.add(Regs3, BorderLayout.AFTER_LAST_LINE);
		TopPnl.add(Regs3V, BorderLayout.AFTER_LAST_LINE);

		TopPnl.add(Regs4, BorderLayout.AFTER_LAST_LINE);
		TopPnl.add(Regs4V, BorderLayout.AFTER_LAST_LINE);

		c.add(OtherPnl, BorderLayout.WEST);
		OtherPnl.setLayout(new BorderLayout());
		OtherPnl.add(BtnPnl, BorderLayout.SOUTH);
		OtherPnl.add(memory, BorderLayout.NORTH);
		OtherPnl.add(Program, BorderLayout.CENTER);

		RUNPANEL.setPreferredSize(new Dimension(50, 35));

		// Stop.setPreferredSize(new Dimension(50, 35));

		BtnPnl.setLayout(new GridLayout(1, 2));
		BtnPnl.setPreferredSize(new Dimension(0, 75));
		BtnPnl.add(RUNPANEL, BorderLayout.WEST);
		RUNPANEL.setLayout(new GridLayout(2, 1));
		RUNPANEL.add(RunAll, BorderLayout.NORTH);
		RUNPANEL.add(RunOneLine, BorderLayout.SOUTH);

		BtnPnl.add(Stops, BorderLayout.EAST);

		Stops.setLayout(new GridLayout(2, 1));
		Stops.add(Advanced, BorderLayout.NORTH);
		Stops.add(Reset, BorderLayout.CENTER);
		Speedy.setLayout(new GridLayout(1, 2));

		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////

		RunAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RUNALL();
			}
		});

		RunOneLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (Compile) {
					split();
					Compile = false;
					Programy.RunMain(LinesArr, false);
					// empty arr
					for (int i = LinesArr.size() - 1; i > -1; i--)
						LinesArr.remove(i);
				}
				Mainy.RunONEline = true;

			}
		});

		Advanced.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OtherBtns a = new OtherBtns();
				a.setVisible(true);
			}
		});
		Reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("\n\nStop and Reset\n\n\n");

				// clears registers
				for (int i = 0; i < Mainy.Registers.RegArr.size(); i++)
					Mainy.Registers.RegArr.get(i).value = 0;

				// set sp to 1000 again
				Mainy.Registers.returnReg("$sp").value = 4000; // 4096?

				Compile = true; // RunOneline & RunAll buttons complies again

				// clears memory
				Programy.clrMem();

				DataPathOP.setText("");

				Programy.RESET();
			}
		});
	}

	public static void RUNALL() {

		if (Compile)
			split();

		Mainy.start = true;

		if (Compile) {
			Programy.RunMain(LinesArr, true);
			for (int i = LinesArr.size() - 1; i > -1; i--)
				LinesArr.remove(i);

			Compile = false;
		}
	}

	public void updateRegPnls() {
		// update the values for each register
		for (int i = 0; i < RegValues.length; i++)
			RegValues[i].setText("    " + (Mainy.Registers.RegArr.get(i)).value);

	}

	public static void split() {
		// splits all the program code into separate lines
		for (String lineCode : Program.getText().split("\\n"))
			LinesArr.add(lineCode);
	}

}
