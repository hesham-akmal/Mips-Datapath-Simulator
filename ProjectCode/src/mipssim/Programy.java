package mipssim;

import java.util.ArrayList;

class Line {

	public static int n = 0;
	public int Number;
	String Code;
	String Label;

	public Line(String s) {
		Code = s;
		Number = n++;
		System.out.println("Line number: " + Number + " has code: " + Code);
		Label = "nolabelyet";
	}
}

public class Programy {

	public static int[] DataMem = new int[1000];
	public static int instructionsExcecuted = 0;
	public static int CurrentInstruction = 0;
	public static ArrayList<Line> Lines = new ArrayList<>();

	public static boolean Binary = true;
	public static boolean Dec = false;
	public static boolean ConvToBinary = false;
	public static boolean ConvToDec = false;

	public static int CycleTime = 1000;

	private static String PC, Adder0outp, Adder1outp, Inst31_0, Inst31_26, Inst25_21, Inst20_16, Inst15_11, Inst15_0,
			Inst15_0Signex, Inst15_0SignexShifted, Inst5_0, WriteReg, mux1, mux3, WriteData, ReadData1, ReadData2,
			ReadData, ALUres, ALUzero, ANDoutp, RegDst, Jump, Branch, MemRead, MemtoReg, MemWrite, jr, ALUSrc, RegWrite,
			ALUOP, jal, ALUcontrol, jump1, jalRegMux, jalDataMux
			// the ra address is only selected when jal signal =1 otherwise mux0
			// output is selected
	, jrMux // selects the rs.value over the jump address when jr signal
			// is 1 // for JUMP INST
	, mux4, Inst25_0, Inst25_0shifted, PCplsFour31_28, Jaddress31_0; // Jaddress31_0;

	public static int getWordOffset(String Code) {
		String off = Code.substring(Code.indexOf(",") + 1, Code.indexOf("("));
		System.out.println(" getWordOffset: " + off);
		return Integer.parseInt(off);
	}

	public static String WordSecondReg(String Code) {
		System.out.print("WordSecondReg: " + Code.substring(Code.indexOf("(") + 1, Code.indexOf(")")));
		return Code.substring(Code.indexOf("(") + 1, Code.indexOf(")"));
	}

	public static boolean hasLabel(String Code) {
		return Code.contains(":");
	}

	public static String getJrReg(String Code) {
		return Code.substring(Code.lastIndexOf(" ") + 1, Code.length());
	}

	public static String getFirstReg(String Code) {
		// gets destination register, works with sub,add , what else?

		String reg = Code.substring(Code.lastIndexOf(" ") + 1, Code.indexOf(","));
		System.out.print(reg + "  ");
		return reg;
	}

	public static String getSecondReg(String Code) {
		// works for add,sub what else?

		String reg = Code.substring(Code.indexOf(",") + 1, Code.lastIndexOf(","));
		System.out.print(reg + "  ");
		return reg;
	}

	public static String getThirdReg(String Code) {
		// works for add,sub what else?

		String reg = Code.substring(Code.lastIndexOf(",") + 1, Code.length());
		System.out.print(reg + "  ");
		return reg;
	}

	public static void add(String Code) {
		// adds two register values and insert to the destination(first)
		// register
		if (check$0AtFirst(Code))
			return;

		Register rd = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rs = Mainy.Registers.returnReg(getSecondReg(Code));
		Register rt = Mainy.Registers.returnReg(getThirdReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));
		int thirdRegNumber = Mainy.Registers.getRegNumber(getThirdReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		String thirdRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(thirdRegNumber)).replace(" ", "0");

		// control unit
		RegDst = "1";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "1";
		ALUOP = "10";

		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		// Whole Instruction = opcode+rs+rt+rd+shamt+func
		Inst31_0 = "000000" + secRegNumberBin + thirdRegNumberBin + firstRegNumberBin + "00000" + "100000";

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);
		Inst25_0 = Inst31_0.substring(6, 32);
		Inst25_0shifted = Integer.toBinaryString(Integer.parseInt(Inst25_0, 2) << 2);
		Jaddress31_0 = Adder0outp + Inst25_0shifted;

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		try {
			Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s",
					Integer.toBinaryString(Integer.parseInt(Inst15_0Signex, 2) * 4)).replace(" ", "0");
		} catch (NumberFormatException e) {
			Inst15_0SignexShifted = String
					.format("%" + Integer.toString(32) + "s",
							Integer.toBinaryString(
									Integer.parseInt(Inst15_0Signex.substring(1, Inst15_0Signex.length()), 2) * 4))
					.replace(" ", "0");

		}
		if (RegDst.equals("1"))
			WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		if (ALUSrc.equals("0"))
			mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
								// IN ADD "CASE"
		else
			mux1 = Inst15_0Signex;

		try {
			Adder1outp = Integer
					.toBinaryString(Integer.parseInt(Adder0outp, 2) + Integer.parseInt(Inst15_0SignexShifted, 2));

		} catch (NumberFormatException e) {
			Adder1outp = Integer.toBinaryString(Integer.parseInt(Adder0outp, 2)
					+ Integer.parseInt(Inst15_0Signex.substring(1, Inst15_0Signex.length()), 2));
		}

		ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) + Integer.parseInt(ReadData1, 2));

		ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
		// BEQ,BNE,SUB,ADDI FUNCTIONS

		///////////////////////////////////////

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;
		// else WriteData = Data memory Read data output "needs more code"

		/////////// hehsam/////
		jalDataMux = "0";
		jalRegMux = "0";
		jal = "0";
		/////////////////////

		// Final step, write data, value of rd = values of rs+rt
		rd.value = rs.value + rt.value;
	}

	public static void addi(String Code) {
		// adds a register value and a constant, then insert to a third register

		if (check$0AtFirst(Code))
			return;

		Register rs = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rt = Mainy.Registers.returnReg(getSecondReg(Code));
		int x;
		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(rs.name);
		int secRegNumber = Mainy.Registers.getRegNumber(rt.name);

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		if (Code.contains("–")) {
			x = -Integer.parseInt(Code.substring(Code.lastIndexOf("–") + 1, Code.length()));
			Inst15_0 = Integer.toBinaryString(0xFFFF & x);
			// ^ return two's complement binary string in 16 bit
		}

		else if (Code.contains("-")) {
			x = -Integer.parseInt(Code.substring(Code.lastIndexOf("-") + 1, Code.length()));
			Inst15_0 = Integer.toBinaryString(0xFFFF & x);
		}

		else { // +ve
			x = Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1, Code.length()));
			Inst15_0 = String.format("%" + Integer.toString(16) + "s", Integer.toBinaryString(x)).replace(" ", "0");
		}

		// control unit
		RegDst = "0";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "1";
		RegWrite = "1";
		ALUOP = "00";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		Inst31_0 = "001000" + secRegNumberBin + firstRegNumberBin + Inst15_0;

		System.out.println(x + " Inst15_0: " + Inst15_0 + " secRegNumberBin: " + secRegNumberBin
				+ " firstRegNumberBin: " + firstRegNumberBin + " Inst31_0: " + Inst31_0);

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);
		Inst25_0 = Inst31_0.substring(6, 32);
		Inst25_0shifted = Integer.toBinaryString(Integer.parseInt(Inst25_0, 2) << 2);
		Jaddress31_0 = Adder0outp + Inst25_0shifted;
		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		// Inst15_0 already done

		Inst5_0 = Inst31_0.substring(26, 32);

		String Inst15_0shfited = Integer.toBinaryString(Integer.parseInt(Inst15_0, 2) << 2);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		if (Inst15_0Signex.substring(0, 1).equals("1"))
			Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s", Inst15_0shfited).replace(" ", "1");
		else
			Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s", Inst15_0shfited).replace(" ", "0");

		if (RegDst.equals("1"))
			WriteReg = Inst15_11;
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		if (Inst15_0SignexShifted.substring(0, 1).equals("1")) {
			int Inst15_0SS31 = Integer.parseInt(Inst15_0SignexShifted.substring(1, 32), 2);
			Adder1outp = Integer.toBinaryString(Integer.parseInt(Adder0outp, 2) + Inst15_0SS31);

			if (Code.contains("–") || Code.contains("-"))
				ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) - rs.value);
			else
				Integer.toBinaryString(Integer.parseInt(mux1, 2) + rs.value);

		} else {

			Adder1outp = Integer
					.toBinaryString(Integer.parseInt(Adder0outp, 2) + Integer.parseInt(Inst15_0SignexShifted, 2));

			if (Code.contains("–") || Code.contains("-"))
				ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) - rs.value);
			else
				ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) + rs.value);

		}
		//// *//*// //// *//*// //// *//*// //// *//*// //// *//*// //// *//*//
		//// //// *//*//

		if (ALUSrc.equals("0"))
			mux1 = ReadData2;
		// else mux1 = Inst15_0Signex;

		ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
						// BEQ,BNE,SUB,ADDI FUNCTIONS

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;

		rs.value = rt.value + x;

	}

	public static void sub(String Code) {
		if (check$0AtFirst(Code))
			return;

		Register rd = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rs = Mainy.Registers.returnReg(getSecondReg(Code));
		Register rt = Mainy.Registers.returnReg(getThirdReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));
		int thirdRegNumber = Mainy.Registers.getRegNumber(getThirdReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		String thirdRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(thirdRegNumber)).replace(" ", "0");

		// control unit
		RegDst = "1";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "1";
		ALUOP = "10";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		// Whole Instruction = opcode+rs+rt+rd+shamt+func
		Inst31_0 = "000000" + secRegNumberBin + thirdRegNumberBin + firstRegNumberBin + "00000" + "100010";

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);
		Inst25_0 = Inst31_0.substring(6, 32);
		Inst25_0shifted = Integer.toBinaryString(Integer.parseInt(Inst25_0, 2) << 2);
		Jaddress31_0 = Adder0outp + Inst25_0shifted;
		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s",
				Integer.toBinaryString(Integer.parseInt(Inst15_0Signex, 2) * 4)).replace(" ", "0");

		if (RegDst.equals("1"))
			WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		if (ALUSrc.equals("0"))
			mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
								// IN ADD "CASE"
		else
			mux1 = Inst15_0Signex;

		Adder1outp = Integer.toBinaryString(Integer.parseInt(Adder0outp, 2) + Integer.parseInt(Inst15_0SignexShifted, 2)

		);

		///////////////////////////// VARIABLES

		ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) - rs.value);

		ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
		// BEQ,BNE,SUB,ADDI FUNCTIONS

		///////////////////////////////////////

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;
		// else WriteData = Data memory Read data output "needs more code"

		/////////// hehsam/////
		jalDataMux = "0";
		jalRegMux = "0";
		jal = "0";
		/////////////////////

		//
		rd.value = rs.value - rt.value;
	}

	public static void move(String Code) {
		// subtracts two register values and insert to the destination(first)
		// register
		if (check$0AtFirst(Code))
			return;
		Mainy.Registers.returnReg(getFirstReg(Code)).value = Mainy.Registers
				.returnReg(Code.substring(Code.indexOf(",") + 1, Code.length())).value;
	}

	public static void slt(String Code) {
		if (check$0AtFirst(Code))
			return;

		Register rd = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rs = Mainy.Registers.returnReg(getSecondReg(Code));
		Register rt = Mainy.Registers.returnReg(getThirdReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));
		int thirdRegNumber = Mainy.Registers.getRegNumber(getThirdReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		String thirdRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(thirdRegNumber)).replace(" ", "0");

		// control unit
		RegDst = "1";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "1";
		ALUOP = "111";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		// Whole Instruction = opcode+rs+rt+rd+shamt+func
		Inst31_0 = "000000" + secRegNumberBin + thirdRegNumberBin + firstRegNumberBin + "00000" + "101010";

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);
		Inst25_0 = Inst31_0.substring(6, 32);
		Inst25_0shifted = Integer.toBinaryString(Integer.parseInt(Inst25_0, 2) << 2);
		Jaddress31_0 = Adder0outp + Inst25_0shifted;
		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s",
				Integer.toBinaryString(Integer.parseInt(Inst15_0Signex, 2) * 4)).replace(" ", "0");

		if (RegDst.equals("1"))
			WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		if (ALUSrc.equals("0"))
			mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
								// IN ADD "CASE"
		else
			mux1 = Inst15_0Signex;

		Adder1outp = Integer.toBinaryString(Integer.parseInt(Adder0outp, 2) + Integer.parseInt(Inst15_0SignexShifted, 2)

		);

		///////////////////////////// VARIABLES

		if (rs.value < rt.value) {
			ALUres = "1";
		} else
			ALUres = "0";

		ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
		// BEQ,BNE,SUB,ADDI FUNCTIONS

		///////////////////////////////////////

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;
		// else WriteData = Data memory Read data output "needs more code"

		/////////// hehsam/////
		jalDataMux = "0";
		jalRegMux = "0";
		jal = "0";
		/////////////////////

		// Final step, write data, value of rd = values of rs+rt
		if (rs.value < rt.value)
			rd.value = 1;
		else
			rd.value = 0;
	}

	public static void slti(String Code) {
		/*
		 * if (check$0AtFirst(Code)) return; if
		 * (Mainy.Registers.returnReg(getSecondReg(Code)).value < Integer
		 * .parseInt(Code.substring(Code.lastIndexOf(",") + 1, Code.length())))
		 * Mainy.Registers.returnReg(getFirstReg(Code)).value = 1; else
		 * Mainy.Registers.returnReg(getFirstReg(Code)).value = 0;
		 */

		if (check$0AtFirst(Code))
			return;

		Register rs = Mainy.Registers.returnReg(getSecondReg(Code));
		Register rd = Mainy.Registers.returnReg(getFirstReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(rd.name);
		int secRegNumber = Mainy.Registers.getRegNumber(rs.name);
		int x = Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1, Code.length()));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		if (Code.contains("–")) {
			x = -Integer.parseInt(Code.substring(Code.lastIndexOf("–") + 1, Code.length()));
			Inst15_0 = Integer.toBinaryString(0xFFFF & x);
			// ^ return two's complement binary string in 16 bit
		}

		else if (Code.contains("-")) {
			x = -Integer.parseInt(Code.substring(Code.lastIndexOf("-") + 1, Code.length()));
			Inst15_0 = Integer.toBinaryString(0xFFFF & x);
		}

		else { // +ve
			x = Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1, Code.length()));
			Inst15_0 = String.format("%" + Integer.toString(16) + "s", Integer.toBinaryString(x)).replace(" ", "0");
		}

		// control unit
		RegDst = "0";
		Jump = "0";
		Branch = "0";
		MemRead = "x";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "1";
		RegWrite = "1";
		ALUOP = "10";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		Inst31_0 = "001010" + secRegNumberBin + firstRegNumberBin + Inst15_0;

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s", Integer.toBinaryString(x * 4))
				.replace(" ", "0");

		if (RegDst.equals("1"))
			WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rd.value);
		ReadData2 = Integer.toBinaryString(rs.value);

		if (ALUSrc.equals("0"))
			mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
								// IN ADD "CASE"
		else
			mux1 = Inst15_0Signex;

		Adder1outp = Integer.toBinaryString(Integer.parseInt(Adder0outp, 2) + (x * 4));

		///////////////////////////// VARIABLES

		if (rs.value < x) {
			ALUres = "1";
		} else
			ALUres = "0";

		ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
		// BEQ,BNE,SUB,ADDI FUNCTIONS

		///////////////////////////////////////

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;
		// else WriteData = Data memory Read data output "needs more code"

		/////////// hehsam/////
		jalDataMux = "0";
		jalRegMux = "0";
		jal = "0";
		/////////////////////

		// Final step, write data, value of rd = values of rs+rt
		if (rs.value < x)
			rd.value = 1;
		else
			rd.value = 0;
	}

	public static void sll(String Code) {

		if (check$0AtFirst(Code))
			return;

		Register rd = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rt = Mainy.Registers.returnReg(getSecondReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));
		int x = Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1, Code.length()));
		// int thirdRegNumber = Mainy.Registers.getRegNumber(getThirdReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String thirdRegBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		// constant to 16 bits
		String constantNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(x))
				.replace(" ", "0");

		// control unit
		RegDst = "0";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "1";
		RegWrite = "1";
		ALUOP = "00";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		// Whole Instruction = opcode+rs+rt+rd+shamt+func
		Inst31_0 = "000000" + "000000" + thirdRegBin + firstRegNumberBin + constantNumberBin + "000000";

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);
		Inst25_0 = Inst31_0.substring(6, 32);
		Inst25_0shifted = Integer.toBinaryString(Integer.parseInt(Inst25_0, 2) << 2);
		Jaddress31_0 = Adder0outp + Inst25_0shifted;
		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		Inst15_0SignexShifted = String
				.format("%" + Integer.toString(32) + "s", Integer.toBinaryString(Integer.parseInt(Inst15_0, 2) * x * 2))
				.replace(" ", "0");

		if (RegDst.equals("1"))
			WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rd.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		if (ALUSrc.equals("0"))
			mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
								// IN ADD "CASE"
		else
			mux1 = Inst15_0Signex;

		try {
			Adder1outp = Integer
					.toBinaryString(Integer.parseInt(Adder0outp, 2) + Integer.parseInt(Inst15_0SignexShifted, 2));
		} catch (NumberFormatException e) {
			Adder1outp = Integer
					.toBinaryString(Integer.parseInt(Adder0outp, 2) + (Integer.parseInt(Inst15_0, 2) * x * 2));
		}

		try {
			ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) * rt.value * 2);
		} catch (NumberFormatException e) {
			ALUres = Integer.toBinaryString(Integer.parseInt(mux1.substring(1, mux1.length()), 2) * rt.value * 2);
		}
		ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
		// BEQ,BNE,SUB,ADDI FUNCTIONS

		///////////////////////////////////////

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;
		// else WriteData = Data memory Read data output "needs more code"

		/////////// hehsam/////
		jalDataMux = "0";
		jalRegMux = "0";
		jal = "0";
		/////////////////////

		//
		rd.value = rt.value * x * 2;
	}

	public static void srl(String Code) {
		if (check$0AtFirst(Code))
			return;

		Register rs = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rt = Mainy.Registers.returnReg(getSecondReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));
		int x = Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1, Code.length()));
		// int thirdRegNumber = Mainy.Registers.getRegNumber(getThirdReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");
		// constant to 16 bits
		String constantNumberBin = String.format("%" + Integer.toString(16) + "s", Integer.toBinaryString(x))
				.replace(" ", "0");

		// String thirdRegNumberBin = String
		// .format("%" + Integer.toString(5) + "s",
		// Integer.toBinaryString(thirdRegNumber)).replace(" ", "0");

		// control unit
		RegDst = "0";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "1";
		RegWrite = "1";
		ALUOP = "00";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		// Whole Instruction = opcode+rs+rt+rd+shamt+func
		Inst31_0 = "000010" + secRegNumberBin + firstRegNumberBin + constantNumberBin;

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);/// kan maktoob empty??

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		try {
			Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s",
					Integer.toBinaryString(Integer.parseInt(Inst15_0Signex, 2) / 4)).replace(" ", "0");

			if (RegDst.equals("1"))
				WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
			else
				WriteReg = Inst20_16;

			ReadData1 = Integer.toBinaryString(rs.value);
			ReadData2 = Integer.toBinaryString(rt.value);

			if (ALUSrc.equals("0"))
				mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
									// IN ADD "CASE"
			else
				mux1 = Inst15_0Signex;

			Adder1outp = Integer
					.toBinaryString(Integer.parseInt(Adder0outp, 2) + Integer.parseInt(Inst15_0SignexShifted, 2)

			);

			ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) * Integer.parseInt(ReadData1, 2) * 2);

			ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
			// BEQ,BNE,SUB,ADDI FUNCTIONS

			///////////////////////////////////////

			if (Branch.equals("1") && ALUzero.equals("1"))
				ANDoutp = "1";
			else
				ANDoutp = "0";

			if (ANDoutp.equals("1"))
				mux3 = Adder1outp;
			else
				mux3 = Adder0outp;

			if (MemtoReg.equals("0"))
				WriteData = ALUres;
			// else WriteData = Data memory Read data output "needs more code"

			/////////// hehsam/////
			jalDataMux = "0";
			jalRegMux = "0";
			jal = "0";
			/////////////////////

		} catch (NumberFormatException e) {
		}

		Mainy.Registers.returnReg(getFirstReg(Code)).value = (Mainy.Registers.returnReg(getSecondReg(Code)).value)
				/ (Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1), Code.length()) * 2);

	}

	public static void sltu(String Code) {

		if (check$0AtFirst(Code))
			return;

		Register rd = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rs = Mainy.Registers.returnReg(getSecondReg(Code));
		Register rt = Mainy.Registers.returnReg(getThirdReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));
		int thirdRegNumber = Mainy.Registers.getRegNumber(getThirdReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		String thirdRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(thirdRegNumber)).replace(" ", "0");

		// control unit
		RegDst = "1";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "1";
		ALUOP = "10";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		// Whole Instruction = opcode+rs+rt+rd+shamt+func
		Inst31_0 = "101001" + secRegNumberBin + thirdRegNumberBin + firstRegNumberBin + "00000" + "101011";

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);
		Inst25_0 = Inst31_0.substring(6, 32);
		Inst25_0shifted = Integer.toBinaryString(Integer.parseInt(Inst25_0, 2) << 2);
		Jaddress31_0 = Adder0outp + Inst25_0shifted;
		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		int xa = Integer.parseInt(Inst15_0, 2);

		Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s", Integer.toBinaryString(xa * 4))
				.replace(" ", "0");

		if (RegDst.equals("1"))
			WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		if (ALUSrc.equals("0"))
			mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
								// IN ADD "CASE"
		else
			mux1 = Inst15_0Signex;

		Adder1outp = Integer.toBinaryString(Integer.parseInt(Adder0outp, 2) + xa * 4);

		///////////////////////////// FUNC
		long x = (rs.value & 0x00000000ffffffffL);
		long y = (rt.value & 0x00000000ffffffffL);
		if (x < y) {
			ALUres = "1";
		} else
			ALUres = "0";

		ALUzero = "0";

		///////////////////////////////////////

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;

		if (x < y) {
			rd.value = 1;
		} else
			rd.value = 0;
	}

	public static void sltui(String Code) {
		/*
		 * if (check$0AtFirst(Code)) return;
		 * 
		 * int a = Mainy.Registers.returnReg(getSecondReg(Code)).value; int b =
		 * Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1),
		 * Code.length()); long x = (a & 0x00000000ffffffffL); long y = (b &
		 * 0x00000000ffffffffL);
		 * 
		 * if (x < y) Mainy.Registers.returnReg(getFirstReg(Code)).value = 1;
		 * else Mainy.Registers.returnReg(getFirstReg(Code)).value = 0;
		 */
		if (check$0AtFirst(Code))
			return;

		Register rs = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rt = Mainy.Registers.returnReg(getSecondReg(Code));
		int x;
		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));

		if (Code.contains("–"))
			x = -Integer.parseInt(Code.substring(Code.lastIndexOf("–") + 1, Code.length()));
		else
			x = Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1, Code.length()));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");
		// constant to 16 bits
		String constantNumberBin;
		if (x >= 0)
			constantNumberBin = String.format("%" + Integer.toString(16) + "s", Integer.toBinaryString(x)).replace(" ",
					"0");
		else
			constantNumberBin = Integer.toBinaryString(x).substring(16, 32);

		// control unit
		RegDst = "0";
		Jump = "0";
		Branch = "0";
		MemRead = "x";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "1";
		RegWrite = "1";
		ALUOP = "00";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		//
		Inst31_0 = "001011" + secRegNumberBin + firstRegNumberBin + constantNumberBin;

		System.out.println(firstRegNumberBin);
		System.out.println(secRegNumberBin);
		System.out.println(Inst31_0);

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);
		Inst25_0 = Inst31_0.substring(6, 32);
		Inst25_0shifted = Integer.toBinaryString(Integer.parseInt(Inst25_0, 2) << 2);
		Jaddress31_0 = Adder0outp + Inst25_0shifted;
		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		long x1 = (rt.value & 0x00000000ffffffffL);
		long x2 = (x & 0x00000000ffffffffL);

		if (x1 < x2)
			ALUres = "1";
		else
			ALUres = "0";

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;
		// else WriteData = Data memory Read data output "needs more code"

		if (x1 < x2)
			rs.value = 1;
		else
			rs.value = 0;

	}

	public static void lw(String Code) {

		if (check$0AtFirst(Code))
			return;

		int address = Mainy.Registers.returnReg(WordSecondReg(Code)).value + (getWordOffset(Code));
		// (getWordOffset(Code)*4 is a mistake, because we're already playing on
		// fake address here which is 4000
		int y;
		if (address < 4000) {
			if (address % 4 != 0)
				System.out.println("ERROR!!!!!!!!!!!, LOADING FROM: SP which is NOT DIV BY 4 ");

			y = DataMem[(address / 4)];
			Mainy.Registers.returnReg(getFirstReg(Code)).value = y;
			System.out.println("loading: " + y + " from address: " + address);
			String firstRegNumberBin = String.format("%" + Integer.toString(5) + "s",
					Integer.toBinaryString(Mainy.Registers.getRegNumber(getFirstReg(Code)))).replace(" ", "0");
			String secondRegNumberBin = String
					.format("%" + Integer.toString(5) + "s",
							Integer.toBinaryString(Mainy.Registers.getRegNumber(WordSecondReg(Code))))
					.replace(" ", "0");
			int x = Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1, Code.indexOf("(")));
			String constantNumberBin = String.format("%" + Integer.toString(16) + "s", Integer.toBinaryString(x))
					.replace(" ", "0");
			PC = "" + CurrentInstruction * 4;
			Adder0outp = "" + (CurrentInstruction * 4 + 4);
			Adder1outp = "" + (CurrentInstruction * 4 + getWordOffset(Code) * 4);
			Inst31_0 = "100011" + secondRegNumberBin + firstRegNumberBin + constantNumberBin;
			Inst31_26 = Inst31_0.substring(0, 5);
			Inst25_21 = Inst31_0.substring(6, 10);
			Inst20_16 = Inst31_0.substring(11, 15);
			Inst15_11 = Inst31_0.substring(16, 20);
			Inst15_0 = Inst31_0.substring(16, 31);
			Inst15_0Signex = "" + getWordOffset(Code);
			Inst15_0SignexShifted = "" + (getWordOffset(Code) << 2);
			Inst5_0 = Inst31_0.substring(26, 31);
			WriteReg = "" + y;
			WriteData = "x";
			ReadData1 = "" + Mainy.Registers.returnReg(WordSecondReg(Code)).value;
			ReadData2 = "" + Mainy.Registers.returnReg(getFirstReg(Code)).value;
			ALUzero = "0";
			ALUres = "" + address;
			ANDoutp = "0";
			mux1 = "" + getWordOffset(Code);
			mux3 = "" + Adder0outp;
			RegDst = "0";
			Branch = "0";
			ReadData = "" + y;
			MemRead = "1";
			MemtoReg = "1";
			MemWrite = "0";
			ALUSrc = "1";
			RegWrite = "1";
			ALUcontrol = "10";
			ALUOP = "0";
			Jump = "0";
		} else
			System.out.println("ERROR!!!!!!!!!!!, LOADING FROM: SP which is > 4000 ");
	}

	public static void sw(String Code) {

		int address = Mainy.Registers.returnReg(WordSecondReg(Code)).value + (getWordOffset(Code));
		if (address < 4000) {
			if (address % 4 != 0)
				System.out.println("ERROR!!!!!!!!!!!, SAVING FROM: SP which is NOT DIV BY 4 ");
			// -1 because array has size of 1000, last index is 999
			else {
				int Data = Mainy.Registers.returnReg(getFirstReg(Code)).value;
				DataMem[(address / 4)] = Data;
				System.out.println("Saving: " + Data + " in address: " + address);
				/*
				 * String PC, Adder0outp, Adder1outp, Inst31_0, Inst31_26,
				 * Inst25_21, Inst20_16, Inst15_11, Inst15_0, Inst15_0Signex,
				 * Inst15_0SignexShifted, Inst5_0, WriteReg, mux1, mux3,
				 * WriteData, ReadData1, ReadData2, ALUres, ALUzero, ANDoutp,
				 * RegDst, Jump, Branch, MemRead, MemtoReg, MemWrite, ALUSrc,
				 * RegWrite, ALUOP,
				 */
				String firstRegNumberBin = String
						.format("%" + Integer.toString(5) + "s",
								Integer.toBinaryString(Mainy.Registers.getRegNumber(getFirstReg(Code))))
						.replace(" ", "0");
				String secondRegNumberBin = String
						.format("%" + Integer.toString(5) + "s",
								Integer.toBinaryString(Mainy.Registers.getRegNumber(WordSecondReg(Code))))
						.replace(" ", "0");
				int x = Integer.parseInt(Code.substring(Code.lastIndexOf(",") + 1, Code.indexOf("(")));
				String constantNumberBin = String.format("%" + Integer.toString(16) + "s", Integer.toBinaryString(x))
						.replace(" ", "0");
				PC = "" + CurrentInstruction * 4;
				Adder0outp = "" + (CurrentInstruction * 4 + 4);
				Adder1outp = "" + (CurrentInstruction * 4 + getWordOffset(Code) * 4);
				Inst31_0 = "101011" + secondRegNumberBin + firstRegNumberBin + constantNumberBin;
				Inst31_26 = Inst31_0.substring(0, 5);
				Inst25_21 = Inst31_0.substring(6, 10);
				Inst20_16 = Inst31_0.substring(11, 15);
				Inst15_11 = Inst31_0.substring(16, 20);
				Inst15_0 = Inst31_0.substring(16, 31);
				Inst15_0Signex = "" + getWordOffset(Code);
				Inst15_0SignexShifted = "" + (getWordOffset(Code) << 2);
				Inst5_0 = Inst31_0.substring(26, 31);
				WriteReg = "x";
				WriteData = "x";
				ReadData1 = "" + Mainy.Registers.returnReg(WordSecondReg(Code)).value;
				ReadData2 = "" + Mainy.Registers.returnReg(getFirstReg(Code)).value;
				ALUzero = "0";
				ALUres = "" + address;
				ANDoutp = "0";
				mux1 = "" + getWordOffset(Code);
				mux3 = "" + Adder0outp;
				RegDst = "x";
				Branch = "0";
				ReadData = "" + Data;
				MemRead = "0";
				MemtoReg = "x";
				MemWrite = "1";
				ALUSrc = "1";
				RegWrite = "0";
				ALUcontrol = "2";
				ALUOP = "0";
				Jump = "0";
			}
		} else
			System.out.println("ERROR!!!!!!!!!!!, SAVING FROM: SP which is > 4000 ");
	}

	public static void OR(String Code) {

		if (check$0AtFirst(Code))
			return;

		Register rd = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rs = Mainy.Registers.returnReg(getSecondReg(Code));
		Register rt = Mainy.Registers.returnReg(getThirdReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));
		int thirdRegNumber = Mainy.Registers.getRegNumber(getThirdReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		String thirdRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(thirdRegNumber)).replace(" ", "0");

		// control unit
		RegDst = "1";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "1";
		ALUOP = "10";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		// Whole Instruction = opcode+rs+rt+rd+shamt+func
		Inst31_0 = "000000" + secRegNumberBin + thirdRegNumberBin + firstRegNumberBin + "00000" + "100101";

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s",
				Integer.toBinaryString(Integer.parseInt(Inst15_0Signex, 2) * 4)).replace(" ", "0");

		if (RegDst.equals("1"))
			WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		if (ALUSrc.equals("0"))
			mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
								// IN ADD "CASE"
		else
			mux1 = Inst15_0Signex;

		Adder1outp = Integer.toBinaryString(Integer.parseInt(Adder0outp, 2) + Integer.parseInt(Inst15_0SignexShifted, 2)

		);

		///////////////////////////// VARIABLES

		ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) | Integer.parseInt(ReadData1, 2));

		ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
		// BEQ,BNE,SUB,ADDI FUNCTIONS

		///////////////////////////////////////

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;
		// else WriteData = Data memory Read data output "needs more code"

		/////////// hehsam/////
		jalDataMux = "0";
		jalRegMux = "0";
		jal = "0";
		/////////////////////

		// Final step, write data, value of rd = values of rs&rt
		rd.value = rs.value | rt.value;

	}

	public static void AND(String Code) {

		if (check$0AtFirst(Code))
			return;

		Register rd = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rs = Mainy.Registers.returnReg(getSecondReg(Code));
		Register rt = Mainy.Registers.returnReg(getThirdReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));
		int thirdRegNumber = Mainy.Registers.getRegNumber(getThirdReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		String thirdRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(thirdRegNumber)).replace(" ", "0");

		// control unit
		RegDst = "1";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "1";
		ALUOP = "10";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		// Whole Instruction = opcode+rs+rt+rd+shamt+func
		Inst31_0 = "000000" + secRegNumberBin + thirdRegNumberBin + firstRegNumberBin + "00000" + "100100";

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s",
				Integer.toBinaryString(Integer.parseInt(Inst15_0Signex, 2) * 4)).replace(" ", "0");

		if (RegDst.equals("1"))
			WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		if (ALUSrc.equals("0"))
			mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
								// IN ADD "CASE"
		else
			mux1 = Inst15_0Signex;

		Adder1outp = Integer.toBinaryString(Integer.parseInt(Adder0outp, 2) + Integer.parseInt(Inst15_0SignexShifted, 2)

		);

		///////////////////////////// VARIABLES

		ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) & rs.value);

		ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
		// BEQ,BNE,SUB,ADDI FUNCTIONS

		///////////////////////////////////////

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;
		// else WriteData = Data memory Read data output "needs more code"

		/////////// hehsam/////
		jalDataMux = "0";
		jalRegMux = "0";
		jal = "0";
		/////////////////////

		// Final step, write data, value of rd = values of rs&rt
		rd.value = rs.value & rt.value;
	}

	public static void NOR(String Code) {
		if (check$0AtFirst(Code))
			return;

		Register rd = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rs = Mainy.Registers.returnReg(getSecondReg(Code));
		Register rt = Mainy.Registers.returnReg(getThirdReg(Code));

		// initializing REGISTERS NUMBERS in DECIMAL (NOT THE VALUES INSIDE
		// THEM)
		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));
		int thirdRegNumber = Mainy.Registers.getRegNumber(getThirdReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		String thirdRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(thirdRegNumber)).replace(" ", "0");

		// control unit
		RegDst = "1";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "1";
		ALUOP = "10";
		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		// Whole Instruction = opcode+rs+rt+rd+shamt+func
		Inst31_0 = "000000" + secRegNumberBin + thirdRegNumberBin + firstRegNumberBin + "00000" + "100111";

		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		// rd
		Inst15_11 = Inst31_0.substring(16, 21);

		Inst15_0 = Inst31_0.substring(16, 32);

		Inst5_0 = Inst31_0.substring(26, 32);

		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "1");
		else
			// extend with 0
			Inst15_0Signex = String.format("%" + Integer.toString(32) + "s", Inst15_0).replace(" ", "0");

		Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s",
				Integer.toBinaryString(Integer.parseInt(Inst15_0Signex, 2) * 4)).replace(" ", "0");

		if (RegDst.equals("1"))
			WriteReg = Inst15_11; // THIS WILL ALWAYS HAPPEN IN ADD "CASE"
		else
			WriteReg = Inst20_16;

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		if (ALUSrc.equals("0"))
			mux1 = ReadData2; // THIS WILL ALWAYS HAPPEN
								// IN ADD "CASE"
		else
			mux1 = Inst15_0Signex;

		Adder1outp = Integer.toBinaryString(Integer.parseInt(Adder0outp, 2) + Integer.parseInt(Inst15_0SignexShifted, 2)

		);

		///////////////////////////// VARIABLES

		ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) | rs.value);

		ALUzero = "0"; // CHANGE THIS LINE TO COMLPEX CODE ONLY IN
		// BEQ,BNE,SUB,ADDI FUNCTIONS

		///////////////////////////////////////

		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;
		// else WriteData = Data memory Read data output "needs more code"

		/////////// hehsam/////
		jalDataMux = "0";
		jalRegMux = "0";
		jal = "0";
		/////////////////////

		// Final step, write data, value of rd = values of rs&rt
		rd.value = ~rs.value | rt.value;
	}

	public static void jumpToLabel(String Label) {
		System.out.println("jump to label: " + Label);
		for (int i = 0; i < Lines.size(); i++) {
			if (Lines.get(i).Label.equals(Label)) {
				CurrentInstruction = Lines.get(i).Number;
				System.out.println("CurrentInstruction = " + Lines.get(i).Number);
				return;
			}
		}
		System.out.println("ERROR, CANT FIND LABEL: " + Label + "\nSkipping to next line.");
		CurrentInstruction++;
		return;
	}

	public static void beq(String Code) {

		String first = getFirstReg(Code);
		String second = getSecondReg(Code);
		String Label = Code.substring(Code.lastIndexOf(",") + 1, Code.length());

		Register rs = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rt = Mainy.Registers.returnReg(getSecondReg(Code));

		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		// control unit
		RegDst = "x";
		Jump = "0";
		Branch = "1";
		MemRead = "x";
		MemtoReg = "x";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "0";
		ALUOP = "01";

		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		int oldInst = CurrentInstruction;

		if (Mainy.Registers.returnReg(first).value == Mainy.Registers.returnReg(second).value) {

			System.out.println("first: " + first + " second: " + second + " Label: " + Label);
			jumpToLabel(Label);
			ALUzero = "1";

		} else { // not equal, next instruction is next line
			CurrentInstruction++;
			ALUzero = "0";
		}

		// offset FOR POSITIVE
		int offset = CurrentInstruction - oldInst - 1;
		if (offset >= 0)
			Inst15_0 = String.format("%" + Integer.toString(16) + "s", Integer.toBinaryString(offset)).replace(" ",
					"0");
		else
			// return two's complement binary string
			// offset FOR NEGATIVE
			Inst15_0 = Integer.toBinaryString(0xFFFF & offset);

		// Whole Instruction = opcode+rs+rt+address
		Inst31_0 = "000100" + secRegNumberBin + firstRegNumberBin + Inst15_0;

		System.out.println(
				"oldInst: " + oldInst + " offset: " + offset + " Inst15_0: " + Inst15_0 + " Inst31_0: " + Inst31_0);
		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		Inst15_11 = Inst31_0.substring(16, 20);

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) + rs.value);

		///////////////////////////////////////
		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;

		Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s",
				Integer.toBinaryString(Integer.parseInt(Inst15_0Signex, 2) << 2)).replace(" ", "0");

	}

	public static void bne(String Code) {
		/*
		 * String first = getFirstReg(Code); String second = getSecondReg(Code);
		 * String Label = Code.substring(Code.lastIndexOf(",") + 1,
		 * Code.length());
		 * 
		 * if (Mainy.Registers.returnReg(first).value !=
		 * Mainy.Registers.returnReg(second).value) {
		 * 
		 * System.out.println("first: " + first + " second: " + second +
		 * " Label: " + Label);
		 * 
		 * jumpToLabel(Label);
		 * 
		 * } else // equal, go to next line CurrentInstruction++;
		 * 
		 * 
		 */

		String first = getFirstReg(Code);
		String second = getSecondReg(Code);
		String Label = Code.substring(Code.lastIndexOf(",") + 1, Code.length());

		Register rs = Mainy.Registers.returnReg(getFirstReg(Code));
		Register rt = Mainy.Registers.returnReg(getSecondReg(Code));

		int firstRegNumber = Mainy.Registers.getRegNumber(getFirstReg(Code));
		int secRegNumber = Mainy.Registers.getRegNumber(getSecondReg(Code));

		// REGISTERS NUMBERS in BINARY //puts binary in exactly 5 digits
		String firstRegNumberBin = String
				.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(firstRegNumber)).replace(" ", "0");

		String secRegNumberBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(secRegNumber))
				.replace(" ", "0");

		// control unit
		RegDst = "x";
		Jump = "0";
		Branch = "1";
		MemRead = "x";
		MemtoReg = "x";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "0";
		ALUOP = "01";

		////////////// always/////////////////////////////////////////////////////////////////
		PC = Integer.toBinaryString(CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = Integer.toBinaryString((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		int oldInst = CurrentInstruction;

		if (Mainy.Registers.returnReg(first).value != Mainy.Registers.returnReg(second).value) {

			System.out.println("first: " + first + " second: " + second + " Label: " + Label);
			jumpToLabel(Label);
			ALUzero = "1";

		} else {

			// equal, next instruction is next line
			CurrentInstruction++;
			ALUzero = "0";

		}

		// offset FOR POSITIVE
		int offset = CurrentInstruction - oldInst - 1;
		if (offset >= 0)
			Inst15_0 = String.format("%" + Integer.toString(16) + "s", Integer.toBinaryString(offset)).replace(" ",
					"0");
		else
			// return two's complement binary string
			// offset FOR NEGATIVE
			Inst15_0 = Integer.toBinaryString(0xFFFF & offset);

		// Whole Instruction = opcode+rs+rt+address
		Inst31_0 = "000101" + secRegNumberBin + firstRegNumberBin + Inst15_0;

		System.out.println(
				"oldInst: " + oldInst + " offset: " + offset + " Inst15_0: " + Inst15_0 + " Inst31_0: " + Inst31_0);
		// opcode
		Inst31_26 = Inst31_0.substring(0, 6);

		// rs
		Inst25_21 = Inst31_0.substring(6, 11);

		// rt
		Inst20_16 = Inst31_0.substring(11, 16);

		Inst15_11 = Inst31_0.substring(16, 20);

		ReadData1 = Integer.toBinaryString(rs.value);
		ReadData2 = Integer.toBinaryString(rt.value);

		ALUres = Integer.toBinaryString(Integer.parseInt(mux1, 2) + rs.value);

		///////////////////////////////////////
		if (Branch.equals("1") && ALUzero.equals("1"))
			ANDoutp = "1";
		else
			ANDoutp = "0";

		if (ANDoutp.equals("1"))
			mux3 = Adder1outp;
		else
			mux3 = Adder0outp;

		if (MemtoReg.equals("0"))
			WriteData = ALUres;

		Inst15_0SignexShifted = String.format("%" + Integer.toString(32) + "s",
				Integer.toBinaryString(Integer.parseInt(Inst15_0Signex, 2) << 2)).replace(" ", "0");

	}

	public static void jump(String Code) {

		// control unit
		RegDst = "x";
		Jump = "1";
		Branch = "x";
		MemRead = "x";
		MemtoReg = "x";
		MemWrite = "0";
		ALUSrc = "x";
		RegWrite = "0";

		////////////// always/////////////////////////////////////////////////////////////////
		PC = "" + (CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = "" + ((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////

		String Label = Code.substring(Code.lastIndexOf(" ") + 1, Code.length());
		jumpToLabel(Label);

		String s = String.format("%" + Integer.toString(32) + "s", Integer.toBinaryString(CurrentInstruction))
				.replace(" ", "0");

		s = s.substring(6, 31);
		Inst31_0 = "000010" + s;
		Inst25_0shifted = (Integer.valueOf(s, 2) << 2) + "00";

		Inst25_0 = s;
		Jaddress31_0 = Adder0outp + Inst25_0shifted;
		mux4 = Jaddress31_0;
		Adder1outp = "x";
		Inst31_26 = Inst31_0.substring(0, 5);
		Inst25_21 = Inst31_0.substring(6, 10);
		Inst20_16 = Inst31_0.substring(11, 15);
		Inst15_11 = Inst31_0.substring(16, 20);
		Inst15_0 = Inst31_0.substring(16, 31);
		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = "1111111111111111" + Inst15_0;
		else
			// extend with 0
			Inst15_0Signex = "0000000000000000" + Inst15_0;
		int x = Integer.valueOf(Inst15_0Signex, 2) << 2;
		Inst15_0SignexShifted = Integer.toBinaryString(x);
		ReadData1 = "x";
		ReadData2 = "x";
		mux1 = "x";
		mux3 = "x";
		WriteData = "x";
		ALUres = "x";
		ALUzero = "x";
		ANDoutp = "x";

		/////////// hehsam/////
		jalDataMux = "0";
		jalRegMux = "0";
		jal = "0";
		/////////////////////

	}

	public static void jal(String Code) {
		/*
		 * public static void jal(String Code) {
		 * 
		 * Mainy.Registers.returnReg("$ra").value = (CurrentInstruction + 1);
		 * String Label = Code.substring(Code.lastIndexOf(" ") + 1,
		 * Code.length());
		 * 
		 * System.out.println("saved in $ra: " + (CurrentInstruction + 1));
		 * jumpToLabel(Label);
		 * 
		 * }
		 */

		Mainy.Registers.returnReg("$ra").value = (CurrentInstruction + 1);

		// control unit
		RegDst = "X";
		Jump = "1";
		Branch = "X";
		MemRead = "X";
		MemtoReg = "X";
		MemWrite = "0";
		ALUSrc = "X";
		RegWrite = "0";
		jal = "1";
		WriteData = "" + (CurrentInstruction + 4);
		////////////// always/////////////////////////////////////////////////////////////////
		PC = "" + (CurrentInstruction * 4);
		PCplsFour31_28 = "" + ((CurrentInstruction * 4) + 4);
		Adder0outp = "" + ((CurrentInstruction * 4) + 4);
		/////////////////////////////////////////////////////////////////////////////////////
		String s = String.format("%" + Integer.toString(32) + "s", Integer.toBinaryString(CurrentInstruction))
				.replace(" ", "0");
		s = s.substring(6, 31);

		Inst31_0 = "000011" + s;
		Inst25_0shifted = (Integer.valueOf(s, 2) << 2) + "00";
		Inst25_0 = s;
		Jaddress31_0 = Adder0outp + Inst25_0shifted;
		mux4 = Jaddress31_0;
		Adder1outp = "x";
		Inst31_26 = Inst31_0.substring(0, 5);
		Inst25_21 = Inst31_0.substring(6, 10);
		Inst20_16 = Inst31_0.substring(11, 15);
		Inst15_11 = Inst31_0.substring(16, 20);
		Inst15_0 = Inst31_0.substring(16, 31);
		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = "1111111111111111" + Inst15_0;
		else
			// extend with 0
			Inst15_0Signex = "0000000000000000" + Inst15_0;
		int x = Integer.valueOf(Inst15_0Signex, 2) << 2;
		Inst15_0SignexShifted = Integer.toBinaryString(x);
		ReadData1 = "x";
		ReadData2 = "x";
		mux1 = "x";
		mux3 = "x";

		ALUres = "x";
		ALUzero = "x";
		ANDoutp = "x";

		jalRegMux = "" + Mainy.Registers.getRegNumber("$ra");
		jalDataMux = PCplsFour31_28;

		String Label = Code.substring(Code.lastIndexOf(" ") + 1, Code.length());
		System.out.println("saved in $ra: " + (CurrentInstruction + 1));
		jumpToLabel(Label);

	}

	public static void jr(String Code) {

		/*
		 * CurrentInstruction = Mainy.Registers.returnReg("$ra").value;
		 * 
		 * System.out.println("Next instruction = : " +
		 * Mainy.Registers.returnReg("$ra").value);
		 */
		int reg = Mainy.Registers.getRegNumber(getJrReg(Code));
		Register rs = Mainy.Registers.returnReg(getJrReg(Code));
		PC = "" + CurrentInstruction * 4;
		PCplsFour31_28 = "" + (CurrentInstruction * 4 + 4);
		CurrentInstruction = rs.value;
		String regBin = String.format("%" + Integer.toString(5) + "s", Integer.toBinaryString(reg)).replace(" ", "0");
		System.out.println("Next instruction = : " + rs.value);
		// wires
		Inst31_0 = "000000" + regBin + "000000000000000001000";
		Inst31_26 = Inst31_0.substring(0, 5);
		Inst25_21 = Inst31_0.substring(6, 10);
		Inst20_16 = Inst31_0.substring(11, 15);
		Inst15_11 = Inst31_0.substring(16, 20);
		Inst15_0 = Inst31_0.substring(16, 31);
		if (Inst15_0.substring(0, 1).equals("1"))
			// extend with 1
			Inst15_0Signex = "1111111111111111" + Inst15_0;
		else
			// extend with 0
			Inst15_0Signex = "0000000000000000" + Inst15_0;
		Inst15_0SignexShifted = "x";
		jrMux = "" + CurrentInstruction;
		mux4 = "0";
		mux3 = "x";
		mux1 = "x";
		ALUzero = "x";
		ALUres = "x";
		Adder1outp = "x";
		Adder0outp = "x";
		// Control Signals
		jr = "1";
		Jump = "1";
		RegDst = "0";
		Branch = "x";
		MemRead = "0";
		MemWrite = "0";
		MemtoReg = "x";
		RegWrite = "0";
		ALUOP = "xx";
		ALUSrc = "x";

	}

	public static void mul(String Code) { // for factorial program
		if (check$0AtFirst(Code))
			return;

		Mainy.Registers.returnReg(getFirstReg(Code)).value = Mainy.Registers.returnReg(getSecondReg(Code)).value
				* Mainy.Registers.returnReg(getThirdReg(Code)).value;
	}

	public static String getOperation(String Code) {
		// Determines operation of Code,
		String operation;

		if (hasLabel(Code))
			operation = Code.substring(Code.indexOf(" ") + 1, Code.lastIndexOf(" "));
		else
			operation = Code.substring(0, Code.indexOf(" "));

		System.out.print(operation + "  ");
		return operation;
	}

	public static boolean check$0AtFirst(String Code) {
		if (Code.substring(Code.lastIndexOf(" ") + 1, Code.indexOf(",")).equals("$0")
				|| Code.substring(Code.lastIndexOf(" ") + 1, Code.indexOf(",")).equals("$zero")) {
			System.out.println("ERROR, $zero CANNOT BE CHANGED");
			return true;
		}
		return false;
	}

	public static void clrMem() {
		for (int i = 0; i < 1000; i++)
			Programy.DataMem[i] = 0;
	}

	public static void RESET() {
		// DOES NOT RESET REG VALUES, OR MEMORY VALUES
		Mainy.start = false; // stop running

		Line.n = 0;
		// clrMem();
		Frame.Compile = true;
		CurrentInstruction = 0;
		instructionsExcecuted = 0;
		// empty lines list
		ResetLines();
	}

	public static void ResetLines() {
		// empty lines list
		for (int i = Lines.size() - 1; i > -1; i--)
			Lines.remove(i);
	}

	public static void printMemoryNoZeros() {
		for (int i = 0; i < 1000; i++)
			if (Programy.DataMem[i] != 0)
				System.out.println("mem index:" + i * 4 + "=" + Programy.DataMem[i]);
	}

	public static void prtMemWo0sTXT() {
		Frame.memContents.setText("");
		for (int i = 999; i >= 0; i--)
			if (Programy.DataMem[i] != 0)
				Frame.memContents.setText(
						Frame.memContents.getText() + "\n Memory Address: " + i * 4 + " = " + Programy.DataMem[i]);
	}

	/////////////////////////////////////////////////////////////////////
	public static void convertToBin() {

		PC = StrDecToStrBin(PC);
		Adder0outp = StrDecToStrBin(Adder0outp);
		Adder1outp = StrDecToStrBin(Adder1outp);
		Inst31_0 = StrDecToStrBin(Inst31_0);
		Inst31_26 = StrDecToStrBin(Inst31_26);
		Inst25_21 = StrDecToStrBin(Inst25_21);
		Inst20_16 = StrDecToStrBin(Inst20_16);
		Inst15_11 = StrDecToStrBin(Inst15_11);
		Inst15_0 = StrDecToStrBin(Inst15_0);
		Inst15_0Signex = StrDecToStrBin(Inst15_0Signex);
		Inst15_0SignexShifted = StrDecToStrBin(Inst15_0SignexShifted);
		Inst5_0 = StrDecToStrBin(Inst5_0);
		WriteReg = StrDecToStrBin(WriteReg);
		mux1 = StrDecToStrBin(mux1);
		mux3 = StrDecToStrBin(mux3);
		WriteData = StrDecToStrBin(WriteData);
		ReadData1 = StrDecToStrBin(ReadData1);
		ReadData2 = StrDecToStrBin(ReadData2);
		ALUres = StrDecToStrBin(ALUres);
		ALUzero = StrDecToStrBin(ALUzero);
		ANDoutp = StrDecToStrBin(ANDoutp);
		RegDst = StrDecToStrBin(RegDst);
		Jump = StrDecToStrBin(Jump);
		Branch = StrDecToStrBin(Branch);
		MemRead = StrDecToStrBin(MemRead);
		MemtoReg = StrDecToStrBin(MemtoReg);
		MemWrite = StrDecToStrBin(MemWrite);
		ALUSrc = StrDecToStrBin(ALUSrc);
		RegWrite = StrDecToStrBin(RegWrite);
		ALUOP = StrDecToStrBin(ALUOP);
		ALUcontrol = StrDecToStrBin(ALUcontrol);
		ReadData = StrDecToStrBin(ReadData);
		mux4 = StrDecToStrBin(mux4);
		Inst25_0 = StrDecToStrBin(Inst25_0);
		Inst25_0shifted = StrDecToStrBin(Inst25_0shifted);
		PCplsFour31_28 = StrDecToStrBin(PCplsFour31_28);
		Jaddress31_0 = StrDecToStrBin(Jaddress31_0);

		jump1 = StrDecToStrBin(jump1);
		jalRegMux = StrDecToStrBin(jalRegMux);
		jalDataMux = StrDecToStrBin(jalDataMux);
		jal = StrDecToStrBin(jal);
		jrMux = StrDecToStrBin(jrMux);

	}

	public static String StrDecToStrBin(String x) {
		if (x == null)
			return "-1";

		System.out.println(x);
		try {
			return Integer.toBinaryString(Integer.parseInt(x));
		} catch (NumberFormatException e) {
			return x;

		}
	}

	public static void convertToDec() {

		PC = StrBinToStrDec(PC);
		Adder0outp = StrBinToStrDec(Adder0outp);
		Adder1outp = StrBinToStrDec(Adder1outp);
		Inst31_0 = StrBinToStrDec(Inst31_0);
		Inst31_26 = StrBinToStrDec(Inst31_26);
		Inst25_21 = StrBinToStrDec(Inst25_21);
		Inst20_16 = StrBinToStrDec(Inst20_16);
		Inst15_11 = StrBinToStrDec(Inst15_11);
		Inst15_0 = StrBinToStrDec(Inst15_0);
		Inst15_0Signex = StrBinToStrDec(Inst15_0Signex);
		Inst15_0SignexShifted = StrBinToStrDec(Inst15_0SignexShifted);
		Inst5_0 = StrBinToStrDec(Inst5_0);
		WriteReg = StrBinToStrDec(WriteReg);
		mux1 = StrBinToStrDec(mux1);
		mux3 = StrBinToStrDec(mux3);
		WriteData = StrBinToStrDec(WriteData);
		ReadData1 = StrBinToStrDec(ReadData1);
		ReadData2 = StrBinToStrDec(ReadData2);
		ALUres = StrBinToStrDec(ALUres);
		ALUzero = StrBinToStrDec(ALUzero);
		ANDoutp = StrBinToStrDec(ANDoutp);
		RegDst = StrBinToStrDec(RegDst);
		Jump = StrBinToStrDec(Jump);
		Branch = StrBinToStrDec(Branch);
		MemRead = StrBinToStrDec(MemRead);
		MemtoReg = StrBinToStrDec(MemtoReg);
		MemWrite = StrBinToStrDec(MemWrite);
		ALUSrc = StrBinToStrDec(ALUSrc);
		RegWrite = StrBinToStrDec(RegWrite);
		ALUOP = StrBinToStrDec(ALUOP);
		ALUcontrol = StrBinToStrDec(ALUcontrol);
		ReadData = StrBinToStrDec(ReadData);
		mux4 = StrBinToStrDec(mux4);
		Inst25_0 = StrBinToStrDec(Inst25_0);
		Inst25_0shifted = StrBinToStrDec(Inst25_0shifted);
		PCplsFour31_28 = StrBinToStrDec(PCplsFour31_28);
		Jaddress31_0 = StrBinToStrDec(Jaddress31_0);

		jump1 = StrBinToStrDec(jump1);
		jalRegMux = StrBinToStrDec(jalRegMux);
		jalDataMux = StrBinToStrDec(jalDataMux);
		jal = StrBinToStrDec(jal);
		jrMux = StrBinToStrDec(jrMux);

	}

	public static String StrBinToStrDec(String x) {
		if (x == null)
			return "-1";
		try {
			return Integer.toString(Integer.parseInt(x, 2));
		} catch (NumberFormatException e) {
			return x;

		}
	}

	public static void resetWires() {
		PC = "0";
		Adder0outp = "0";
		Adder1outp = "0";
		Inst31_0 = "0";
		Inst31_26 = "0";
		Inst25_21 = "0";
		Inst20_16 = "0";
		Inst15_11 = "0";
		Inst15_0 = "0";
		Inst15_0Signex = "0";
		Inst15_0SignexShifted = "0";
		Inst5_0 = "0";
		WriteReg = "0";
		mux1 = "0";
		mux3 = "0";
		WriteData = "0";
		ReadData1 = "0";
		ReadData2 = "0";
		ALUres = "0";
		ALUzero = "0";
		ANDoutp = "0";
		RegDst = "0";
		Jump = "0";
		Branch = "0";
		MemRead = "0";
		MemtoReg = "0";
		MemWrite = "0";
		ALUSrc = "0";
		RegWrite = "0";
		ALUOP = "0";
		ALUcontrol = "0";
		ReadData = "0";
		// for JUMP INST
		mux4 = "0";
		Inst25_0 = "0";
		Inst25_0shifted = "0";
		PCplsFour31_28 = "0";
		Jaddress31_0 = "0";
		// for jal
		jalRegMux = "0";
		jal = "0";
		jalDataMux = "0";
		jump1 = "0";
		jr = "0";
		jrMux = "0";

	}

	public static boolean checkIneg(String Code) {
		char c = Code.charAt(Code.lastIndexOf(",") + 1);
		if (c == '-' || c == '-')
			return true;
		else if (c == ' ') {
			c = Code.charAt(Code.lastIndexOf(",") + 2);
			if (c == '-' || c == '-')
				return true;
			else
				return false;
		} else
			return false;
	}

	/////////////////////////////////////////////////////////////////////
	public static void RunALine() {
		// Run a single line of code, by determining operation first
		if (CurrentInstruction >= Lines.size()) {
			// PROGRAM ENDED
			System.out.println("\nInstructions excecuted: " + instructionsExcecuted);
			System.out.println("\nEnd of text");
			printMemoryNoZeros();
			RESET();
			System.out.println("adsa");
			printMemoryNoZeros();
			return;
		}

		Line line = Lines.get(CurrentInstruction);

		System.out.println("\nRunning Line number: " + line.Number);

		resetWires();// all wires = 0

		switch (getOperation(line.Code)) {
		case "add":
			add(line.Code);
			CurrentInstruction++;
			break;
		case "sub":
			sub(line.Code);
			CurrentInstruction++;
			break;
		case "addi":
			addi(line.Code);
			CurrentInstruction++;
			break;
		case "mul":
			mul(line.Code);
			CurrentInstruction++;
			break;
		case "j":
			jump(line.Code);
			break;
		case "slt":
			slt(line.Code);
			CurrentInstruction++;
			break;
		case "slti":
			slti(line.Code);
			CurrentInstruction++;
			break;
		case "beq":
			beq(line.Code);
			break;
		case "bne":
			bne(line.Code);
			break;
		case "sll":
			sll(line.Code);
			CurrentInstruction++;
			break;
		case "srl":
			srl(line.Code);
			CurrentInstruction++;
			break;
		case "sltu":
			sltu(line.Code);
			CurrentInstruction++;
			break;
		case "sltui":
			sltui(line.Code);
			CurrentInstruction++;
			break;
		case "sw":
			sw(line.Code);
			CurrentInstruction++;
			break;
		case "lw":
			lw(line.Code);
			CurrentInstruction++;
			break;
		case "and":
			AND(line.Code);
			CurrentInstruction++;
			break;
		case "or":
			OR(line.Code);
			CurrentInstruction++;
			break;
		case "nor":
			NOR(line.Code);
			CurrentInstruction++;
			break;
		case "jal":
			jal(line.Code);
			break;
		case "jr":
			jr(line.Code);
			break;
		case "move":
			move(line.Code);
			CurrentInstruction++;
			break;
		}

		if (ConvToBinary) {
			if (Dec && !Binary) {
				convertToBin();
				Dec = false;
				Binary = true;
				ConvToBinary = false;
				ConvToDec = false;
			}
		}

		if (ConvToDec) {
			if (Binary && !Dec) {

				convertToDec();
				Dec = true;
				Binary = false;
				ConvToBinary = false;
				ConvToDec = false;
			}
		}

		Frame.DataPathOP.setText("\n     PC: " + PC + "\n     Adder 0 output: " + Adder0outp + "\n     Adder 1 output: "
				+ Adder1outp + " \n     Inst31_0: " + Inst31_0 + " \n     Inst31_26: " + Inst31_26
				+ "\n     Inst25_21: " + Inst25_21 + "\n     Inst25_0: " + Inst25_0 + "\n     Inst25_0 shifted: "
				+ Inst25_0shifted + "\n     PC+Four31_28: " + PCplsFour31_28 + "\n     Jaddress31_0: " + Jaddress31_0
				+ " \n     Inst20_16: " + Inst20_16 + "\n     Inst15_11: " + Inst15_11 + " \n     Inst15_0: " + Inst15_0
				+ " \n     Inst15_0 Signex: " + Inst15_0Signex + " \n     Inst15_0 Signex Shifted: "
				+ Inst15_0SignexShifted + "\n     Inst5_0: " + Inst5_0 + "\n     RegDst: " + RegDst + "\n     Jump: "
				+ Jump + "\n     Branch: " + Branch + "\n     Mem Read: " + MemRead + "\n     Mem to Reg: " + MemtoReg
				+ "\n     Mem Write: " + MemWrite + "\n     ALU Src: " + ALUSrc + "\n     Reg Write: " + RegWrite
				+ "\n     Read Data1:  " + ReadData1 + "\n     Read Data2:  " + ReadData2 + "\n     Write Reg:  "
				+ WriteReg + "\n     mux1:  " + mux1 + "\n     ALUres:  " + ALUres + "\n     ALUzero:  " + ALUzero
				+ "\n     AND outp:  " + ANDoutp + "\n     mux3:  " + mux3 + "\n     mux4:  " + mux4
				+ "\n     Write Data:  " + WriteData + "\n     Read Data: " + ReadData + "\n     ALUOP: " + ALUOP
				// for jal
				+ "\n     jal Reg Mux: " + jalRegMux + "      jal Data Mux: " + jalDataMux + "\n     jal: " + jal
				+ "      jr Mux: " + jrMux + "      jump1: " + jump1 + "      jr: " + jr + "\n    Clock cycles spanned: "
				+ instructionsExcecuted);
		// + ALUcontrol fakes?

		instructionsExcecuted++;

	}

	public static void AssignLabel(Line line) {
		if (!hasLabel(line.Code))
			return;
		// else if it has a label
		line.Label = line.Code.substring(0, line.Code.indexOf(":"));
		System.out.println("::Line number: " + line.Number + " now has label: " + line.Label);

	}

	public static void RunMain(ArrayList<String> LinesArr, boolean all) {
		// Run each line by order from the arraylist

		// Create array list of Lines, each line has a string of code, a
		// number and a label (if it has one)
		for (int i = 0; i < LinesArr.size(); i++)
			Lines.add(new Line(LinesArr.get(i)));

		for (int i = 0; i < Lines.size(); i++)
			AssignLabel(Lines.get(i));

		if (all)
			Mainy.start = true;

	}
}