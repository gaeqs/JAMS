package net.jamsimulator.jams.mips.memory.cache;

public class CacheTestsData {

	public static final String PROGRAM = "\t.data\n" +
			"data:\t.space 1024 #256 words\n" +
			"\t.text\n" +
			"\t.globl main\n" +
			"main:\t\n" +
			"\tla $s0, data\n" +
			"\tli $t0, 0\n" +
			"\tli $t1, 256\n" +
			"\n" +
			"add_loop:\t\n" +
			"\tsw $t0, 0($s0)\n" +
			"\taddiu $t0, $t0, 1\n" +
			"\taddiu $s0, $s0, 4\n" +
			"\tbne $t0, $t1, add_loop\n" +
			"\n" +
			"\tla $s0, data\n" +
			"\tli $t0, 0\n" +
			"\tli $t2, 1024\n" +
			"\n" +
			"sum_loop_1:\t\n" +
			"\n" +
			"\tadd $t1, $zero, $t0\n" +
			"\tli $t3, 0\n" +
			"\n" +
			"\tsum_loop_2:\t\n" +
			"\n" +
			"\t\tadd $s1, $s0, $t1\n" +
			"\t\tlw $t4, 0($s1)\n" +
			"\t\tadd $t3, $t3, $t4\n" +
			"\n" +
			"\t\taddiu $t1, $t1, 4\n" +
			"\n" +
			"\t\tbne $t1, $t2, sum_loop_2\n" +
			"\t\t\n" +
			"\tadd $s1, $s0, $t0\n" +
			"\tsw $t3, 0($s1)\n" +
			"\t\n" +
			"\taddiu $t0, $t0, 4\n" +
			"\tbne $t0, $t2, sum_loop_1\n" +
			"\t\n" +
			"\t\n" +
			"\t\n";

}
