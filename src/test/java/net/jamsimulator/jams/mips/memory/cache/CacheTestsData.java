/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.memory.cache;

public class CacheTestsData {

    public static final String PROGRAM = """
             .data
            data: .space 1024 #256 words
             .text
             .globl main
            main:
             la $s0, data
             li $t0, 0
             li $t1, 256

            add_loop:
             sw $t0, 0($s0)
             addiu $t0, $t0, 1
             addiu $s0, $s0, 4
             bne $t0, $t1, add_loop

             la $s0, data
             li $t0, 0
             li $t2, 1024

            sum_loop_1:

             add $t1, $zero, $t0
             li $t3, 0

             sum_loop_2:

              add $s1, $s0, $t1
              lw $t4, 0($s1)
              add $t3, $t3, $t4

              addiu $t1, $t1, 4

              bne $t1, $t2, sum_loop_2
              
             add $s1, $s0, $t0
             sw $t3, 0($s1)
             
             addiu $t0, $t0, 4
             bne $t0, $t2, sum_loop_1
            """;

}
